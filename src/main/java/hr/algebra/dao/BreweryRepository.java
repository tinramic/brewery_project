package hr.algebra.dao;

import hr.algebra.model.Brewery;
import hr.algebra.model.Country;
import hr.algebra.repository.Repository;
import hr.algebra.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BreweryRepository implements Repository<Brewery> {

    private final CountryRepository countryRepository = new CountryRepository();

    private Brewery mapResultSet(ResultSet rs) throws SQLException {
        int countryId = rs.getInt("country_id");
        Country country = null;
        try {
            country = countryRepository.findById(countryId).orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load country for brewery", e);
        }

        return new Brewery(
                rs.getInt("id"),
                rs.getString("name"),
                country
        );
    }

    @Override
    public List<Brewery> findAll() throws SQLException {
        List<Brewery> breweries = new ArrayList<>();
        String sql = "SELECT * FROM brewery ORDER BY name";

        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                breweries.add(mapResultSet(rs));
            }
        }
        return breweries;
    }

    @Override
    public Optional<Brewery> findById(int id) throws SQLException {
        String sql = "SELECT * FROM brewery WHERE id = ?";

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

    @Override
    public void save(Brewery brewery) throws SQLException {
        String sql = "INSERT INTO brewery (name, country_id) VALUES (?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, brewery.getName());
            stmt.setInt(2, brewery.getCountry().getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Brewery brewery) throws SQLException {
        String sql = "UPDATE brewery SET name = ?, country_id = ? WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, brewery.getName());
            stmt.setInt(2, brewery.getCountry().getId());
            stmt.setInt(3, brewery.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM brewery WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}