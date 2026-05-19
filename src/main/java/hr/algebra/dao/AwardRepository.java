package hr.algebra.dao;

import hr.algebra.model.Award;
import hr.algebra.repository.Repository;
import hr.algebra.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AwardRepository implements Repository<Award> {

    private Award mapResultSet(ResultSet rs) throws SQLException {
        return new Award(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("year"),
                rs.getString("organization")
        );
    }

    @Override
    public List<Award> findAll() throws SQLException {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT * FROM award ORDER BY year DESC";

        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                awards.add(mapResultSet(rs));
            }
        }
        return awards;
    }

    @Override
    public Optional<Award> findById(int id) throws SQLException {
        String sql = "SELECT * FROM award WHERE id = ?";

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
    public void save(Award award) throws SQLException {
        String sql = "INSERT INTO award (name, year, organization) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, award.getName());
            stmt.setInt(2, award.getYear());
            stmt.setString(3, award.getOrganization());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Award award) throws SQLException {
        String sql = "UPDATE award SET name = ?, year = ?, organization = ? WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, award.getName());
            stmt.setInt(2, award.getYear());
            stmt.setString(3, award.getOrganization());
            stmt.setInt(4, award.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM award WHERE id = ?";

        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}