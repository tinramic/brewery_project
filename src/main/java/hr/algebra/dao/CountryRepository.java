package hr.algebra.dao;

import hr.algebra.model.Country;
import hr.algebra.repository.Repository;
import hr.algebra.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountryRepository implements Repository<Country> {

    private Country mapResultSet(ResultSet rs) throws SQLException {
        return new Country(
                rs.getInt("id"),
                rs.getString("name")
        );
    }

    @Override
    public List<Country> findAll() throws SQLException {
        List<Country> countries = new ArrayList<>();
        String sql = "SELECT * FROM country ORDER BY name";

        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                countries.add(mapResultSet(rs));
            }
        }
        return countries;
    }

    @Override
    public Optional<Country> findById(int id) throws SQLException {
        String sql = "SELECT * FROM country WHERE id = ?";

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
    public void save(Country country) throws SQLException {
        String sql = "INSERT INTO country (name) VALUES (?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, country.getName());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Country country) throws SQLException {
        String sql = "UPDATE country SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, country.getName());
            stmt.setInt(2, country.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM country WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}