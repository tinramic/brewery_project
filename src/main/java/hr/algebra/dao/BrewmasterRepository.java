package hr.algebra.dao;

import hr.algebra.model.Brewmaster;
import hr.algebra.repository.Repository;
import hr.algebra.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrewmasterRepository implements Repository<Brewmaster> {

    private Brewmaster mapResultSet(ResultSet rs) throws SQLException {
        return new Brewmaster(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("bio"),
                rs.getString("brewing_philosophy")
        );
    }

    @Override
    public List<Brewmaster> findAll() throws SQLException {
        List<Brewmaster> brewmasters = new ArrayList<>();
        String sql = "SELECT * FROM brewmaster ORDER BY name";

        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                brewmasters.add(mapResultSet(rs));
            }
        }
        return brewmasters;
    }

    @Override
    public Optional<Brewmaster> findById(int id) throws SQLException {
        String sql = "SELECT * FROM brewmaster WHERE id = ?";

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
    public void save(Brewmaster brewmaster) throws SQLException {
        String sql = "INSERT INTO brewmaster (name, bio, brewing_philosophy) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, brewmaster.getName());
            stmt.setString(2, brewmaster.getBio());
            stmt.setString(3, brewmaster.getBrewingPhilosophy());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Brewmaster brewmaster) throws SQLException {
        String sql = "UPDATE brewmaster SET name = ?, bio = ?, brewing_philosophy = ? WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, brewmaster.getName());
            stmt.setString(2, brewmaster.getBio());
            stmt.setString(3, brewmaster.getBrewingPhilosophy());
            stmt.setInt(4, brewmaster.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM brewmaster WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}