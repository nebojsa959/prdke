package dke.prdke;

import java.sql.*;

public class Test {

    public static void main(String[] args) {

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-158-179.eu-west-1.compute.amazonaws.com:5432/djp47beps30l4", "angubgjkxdieah", "86e4ba06fe962430207888c4e4352c189139baddb1f19507104f03b47539aa2b");
            con.setAutoCommit(false);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (Exception e) {
            System.out.println("Failed to connect!");
            System.out.println(e.getMessage());
        }

        /**String inst = "INSERT INTO test (row_id, bez) " + " VALUES (?, ?)";

        try (PreparedStatement insert = con.prepareStatement(inst)) {
            insert.setInt(1, 11111);
            insert.setString(2, "aaaa");
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
        }*/

        PreparedStatement stmt = null;
        String query = "SELECT * FROM test";

        try {
            stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\nData:");

            while (rs.next()) {
                String row_id = rs.getString("row_id");
                String bezeichnung = rs.getString("bez");
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
