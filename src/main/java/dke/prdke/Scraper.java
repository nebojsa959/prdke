package dke.prdke;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;

public class Scraper {

    public static void updateWeatherData(String city){
        try {

            Document doc = Jsoup.connect("https://www.wetter.at/wetter/oesterreich/oberoesterreich/"+city).get();

            String temp = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.left > div > span:nth-child(2)").text();
            String rain = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.left > div > span.d-block.mt-2.pt-4").text();
            String wind = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.left > div > span.d-block.notetext").text();
            String weather = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.bottom > div.flex-grow-1 > span.d-block.mt-2").text();

            String minTemp=temp.substring(0,temp.indexOf("° /"));
            String maxTemp=temp.substring(temp.indexOf("° /")+4,temp.length()-1);
            rain=rain.substring(15,rain.length()-5);
            String strengthWind=wind.substring(6,wind.indexOf("km/h")-1);
            String directionWind =wind.substring(wind.indexOf("km/h")+5,wind.length());

            LocalDate date = LocalDate.now(); // Gets the current date

            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            String location = city.substring(0, 1).toUpperCase() + city.substring(1);
            int tempMin = Integer.parseInt(minTemp);
            int tempMax = Integer.parseInt(maxTemp);
            double rainfall = Double.parseDouble(rain);
            double windstr = Double.parseDouble(strengthWind);
            String weatherScale = null;

            double scale1 = -1;
            double scale2 = -1;
            double scale3 = -1;
            long scale_res = -1;
            String season = null;
            String parameter = null;

            Connection con = null;
            try {
                con = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-158-179.eu-west-1.compute.amazonaws.com:5432/djp47beps30l4", "angubgjkxdieah", "86e4ba06fe962430207888c4e4352c189139baddb1f19507104f03b47539aa2b");
                con.setAutoCommit(false);
                System.out.println("Connected to the PostgreSQL server successfully.");
            } catch (Exception e) {
                System.out.println("Failed to connect!");
                System.out.println(e.getMessage());
            }

            PreparedStatement stmt = null;
            String datum =  sqlDate.toString();
            String query = "SELECT * FROM seasons WHERE '" + datum + "' BETWEEN date_from AND date_to ";
            try {
                stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    season = rs.getString("parameter_");
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Step 2
            stmt = null;
            parameter = "Temperatur";
            query = "SELECT * FROM scale_definition WHERE parameter_ = '" + parameter + "' AND description like '%" + season + "%' AND "+ tempMax + " BETWEEN value_from AND value_to";
            try {
                stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    scale1 = Double.parseDouble(rs.getString("scale_"));
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Step 3
            stmt = null;
            parameter = "Rainfall";
            query = "SELECT * FROM scale_definition WHERE parameter_ = '" + parameter + "' AND description like '%" + season + "%' AND " + rainfall + " BETWEEN value_from AND value_to";
            try {
                stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    scale2 = Double.parseDouble(rs.getString("scale_"));
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Step 4
            stmt = null;
            parameter = "Windspeed";
            query = "SELECT * FROM scale_definition WHERE parameter_ = '" + parameter + "' AND description like '%" + season + "%' AND " + windstr + " BETWEEN value_from AND value_to";
            try {
                stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    scale3 = Double.parseDouble(rs.getString("scale_"));
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            scale_res = Math.round((scale1 + scale2 + scale3)/3);

            //Step 5
            stmt = null;
            query = "SELECT * FROM scales WHERE id="+scale_res;
            try {
                stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    weatherScale = rs.getString("description");
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Final check
            stmt = null;
            java.sql.Date dbLastUpdDayDate = null;
            query = "SELECT max(date_) FROM weather_data WHERE location= '" + location + "'";
            try {
                stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while(rs.next())
                {
                    dbLastUpdDayDate = java.sql.Date.valueOf(rs.getString(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sqlDate.after(dbLastUpdDayDate)) {

                String inst = "INSERT INTO weather_data (date_, location, tmin, tmax, rainfall, wspd, wdir, weather) " + " VALUES (?, ?, ?, ?, ?, ?, null, ?)";

                try (PreparedStatement insert = con.prepareStatement(inst)) {
                    insert.setDate(1, sqlDate);
                    insert.setString(2, location);
                    insert.setInt(3, tempMin);
                    insert.setInt(4, tempMax);
                    insert.setDouble(5, rainfall);
                    insert.setDouble(6, windstr);
                    insert.setString(7, weatherScale);
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
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}

