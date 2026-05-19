package hr.algebra.dao;

import hr.algebra.model.*;
import hr.algebra.repository.Repository;
import hr.algebra.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeerRepository implements Repository<Beer> {

    private final BreweryRepository breweryRepository = new BreweryRepository();
    private final BeerStyleRepository beerStyleRepository = new BeerStyleRepository();
    private final CountryRepository countryRepository = new CountryRepository();
    private final BrewmasterRepository brewmasterRepository = new BrewmasterRepository();

    private List<Award> findAwardsByBeerId(int beerId) throws SQLException {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT a.* FROM award a " +
                "JOIN beer_award ba ON a.id = ba.award_id " +
                "WHERE ba.beer_id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, beerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                awards.add(new Award(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("year"),
                        rs.getString("organization")
                ));
            }
        }
        return awards;
    }

    private Beer mapResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");

        Brewery brewery = breweryRepository.findById(rs.getInt("brewery_id")).orElse(null);
        BeerStyle beerStyle = beerStyleRepository.findById(rs.getInt("beer_style_id")).orElse(null);
        Country country = countryRepository.findById(rs.getInt("country_id")).orElse(null);
        Brewmaster brewmaster = brewmasterRepository.findById(rs.getInt("brewmaster_id")).orElse(null);
        List<Award> awards = findAwardsByBeerId(id);

        return new Beer(
                id,
                rs.getString("name"),
                rs.getDouble("abv"),
                rs.getInt("ibu"),
                rs.getInt("srm"),
                rs.getString("flavor_profile"),
                rs.getString("image_path"),
                brewery,
                beerStyle,
                country,
                brewmaster,
                awards
        );
    }

    @Override
    public List<Beer> findAll() throws SQLException {
        List<Beer> beers = new ArrayList<>();
        String sql = "SELECT * FROM beer ORDER BY name";

        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                beers.add(mapResultSet(rs));
            }
        }
        return beers;
    }

    @Override
    public Optional<Beer> findById(int id) throws SQLException {
        String sql = "SELECT * FROM beer WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
        }
        return Optional.empty();
    }

    public List<Beer> findByStyle(int styleId) throws SQLException {
        List<Beer> beers = new ArrayList<>();
        String sql = "SELECT * FROM beer WHERE beer_style_id = ? ORDER BY name";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, styleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                beers.add(mapResultSet(rs));
            }
        }
        return beers;
    }

    public List<Beer> findByAbvRange(double minAbv, double maxAbv) throws SQLException {
        List<Beer> beers = new ArrayList<>();
        String sql = "SELECT * FROM beer WHERE abv BETWEEN ? AND ? ORDER BY abv";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, minAbv);
            stmt.setDouble(2, maxAbv);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                beers.add(mapResultSet(rs));
            }
        }
        return beers;
    }

    @Override
    public void save(Beer beer) throws SQLException {
        String sql = "INSERT INTO beer (name, abv, ibu, srm, flavor_profile, image_path, " +
                "brewery_id, beer_style_id, country_id, brewmaster_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, beer.getName());
            stmt.setDouble(2, beer.getAbv());
            stmt.setInt(3, beer.getIbu());
            stmt.setInt(4, beer.getSrm());
            stmt.setString(5, beer.getFlavorProfile());
            stmt.setString(6, beer.getImagePath());
            stmt.setInt(7, beer.getBrewery().getId());
            stmt.setInt(8, beer.getBeerStyle().getId());
            stmt.setInt(9, beer.getCountry().getId());
            stmt.setInt(10, beer.getBrewmaster().getId());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int beerId = generatedKeys.getInt(1);
                saveAwards(beerId, beer.getAwards());
            }
        }
    }

    @Override
    public void update(Beer beer) throws SQLException {
        String sql = "UPDATE beer SET name = ?, abv = ?, ibu = ?, srm = ?, flavor_profile = ?, " +
                "image_path = ?, brewery_id = ?, beer_style_id = ?, country_id = ?, " +
                "brewmaster_id = ? WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, beer.getName());
            stmt.setDouble(2, beer.getAbv());
            stmt.setInt(3, beer.getIbu());
            stmt.setInt(4, beer.getSrm());
            stmt.setString(5, beer.getFlavorProfile());
            stmt.setString(6, beer.getImagePath());
            stmt.setInt(7, beer.getBrewery().getId());
            stmt.setInt(8, beer.getBeerStyle().getId());
            stmt.setInt(9, beer.getCountry().getId());
            stmt.setInt(10, beer.getBrewmaster().getId());
            stmt.setInt(11, beer.getId());
            stmt.executeUpdate();

            deleteAwards(beer.getId());
            saveAwards(beer.getId(), beer.getAwards());
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        deleteAwards(id);

        String sql = "DELETE FROM beer WHERE id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private void saveAwards(int beerId, List<Award> awards) throws SQLException {
        if (awards == null || awards.isEmpty()) return;

        String sql = "INSERT INTO beer_award (beer_id, award_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Award award : awards) {
                stmt.setInt(1, beerId);
                stmt.setInt(2, award.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void deleteAwards(int beerId) throws SQLException {
        String sql = "DELETE FROM beer_award WHERE beer_id = ?";
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, beerId);
            stmt.executeUpdate();
        }
    }
}