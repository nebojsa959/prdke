package dke.prdke;

import java.sql.*;

public class Test {

    public static void main(String[] args) {

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "dkepr");
            con.setAutoCommit(false);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (Exception e) {
            System.out.println("Failed to connect!");
            System.out.println(e.getMessage());
        }

        String inst = "INSERT INTO test (row_id, bezeichnung) " + " VALUES (?, ?)";

        try (PreparedStatement insert = con.prepareStatement(inst)) {
            insert.setInt(1, 11111);
            insert.setString(2, "test2");
            insert.executeUpdate();

            con.commit();
            System.out.println("Insertion Successful");
        } catch (SQLException ex) {
            System.out.println("Insertion Failed");
            ex.printStackTrace();
            try {
                System.out.println("Rolling back ...");
                con.rollback();
            } catch (SQLException ignore) {
                System.out.println("Rollback failed - report and ignore");
                ex.printStackTrace();
            }
        }

        PreparedStatement stmt = null;
        String query = "SELECT * FROM test";

        try {
            stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\nData:");

            while (rs.next()) {
                String row_id = rs.getString("row_id");
                String bezeichnung = rs.getString("bezeichnung");
                System.out.println(row_id + "  " + bezeichnung);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                con.close();
            } catch (Exception e) {
            }
        }
    }
}
