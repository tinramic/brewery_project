package hr.algebra.dao;

import hr.algebra.model.BeerStyle;
import hr.algebra.repository.Repository;
import hr.algebra.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeerStyleRepository implements Repository<BeerStyle> {

    private BeerStyle mapResultSet(ResultSet rs) throws SQLException {
        return new BeerStyle(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }

    @Override
    public List<BeerStyle> findAll() throws SQLException {
        List<BeerStyle> styles = new ArrayList<>();
        String sql = "SELECT * FROM beer_style ORDER BY name";

        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                styles.add(mapResultSet(rs));
            }
        }
        return styles;
    }

    @Override
    public Optional<BeerStyle> findById(int id) throws SQLException {
        String sql = "SELECT * FROM beer_style WHERE id = ?";

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
    public void save(BeerStyle beerStyle) throws SQLException {
        String sql = "INSERT INTO beer_style (name, description) VALUES (?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, beerStyle.getName());
            stmt.setString(2, beerStyle.getDescription());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(BeerStyle beerStyle) throws SQLException {
        String sql = "UPDATE beer_style SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, beerStyle.getName());
            stmt.setString(2, beerStyle.getDescription());
            stmt.setInt(3, beerStyle.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM beer_style WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}