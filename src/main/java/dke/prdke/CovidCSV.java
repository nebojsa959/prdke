package dke.prdke;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.opencsv.CSVReader;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.sql.*;

public class CovidCSV {

    public static void updateCovidData(){
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:postgresql://ec2-54-247-158-179.eu-west-1.compute.amazonaws.com:5432/djp47beps30l4", "angubgjkxdieah", "86e4ba06fe962430207888c4e4352c189139baddb1f19507104f03b47539aa2b");
            con.setAutoCommit(false);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (Exception e) {
            System.out.println("Failed to connect!");
            System.out.println(e.getMessage());
        }

        Statement selectStmt = null;
        java.sql.Date dabLastUpdDayDate = null;
        try {
            selectStmt = con.createStatement();
            ResultSet rs = selectStmt.executeQuery("select max(date_) from covid_numerics");
            while(rs.next())
            {
                dabLastUpdDayDate = java.sql.Date.valueOf(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PreparedStatement stmt = null;
        LocalDate date = null;
        try {
            Document doc = Jsoup.connect("https://www.data.gv.at/katalog/dataset/covid-19-zeitliche-darstellung-von-daten-zu-covid19-fallen-je-bezirk/resource/9eb08d45-ff99-40f1-90cd-7b3659b0bc8d").get();

            String url = doc.select("#content > main > div.card.rounded-0.border-0.p-0.mb-5 > div.card-body.rounded-0.border-0.bg-secondary.p-0 > div.row.p-0.m-0.rounded-0.wrapper > section > div.module-content.col-12 > div.clearfix > p > a").text();

            URL stockURL = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
            CSVReader reader = new CSVReader(in);

            String[] headers = reader.readNext();
            for(String header : headers){
                headers = header.split(";");
            }

            int idxTime =Arrays.asList(headers).indexOf("Time");
            int idxCity =Arrays.asList(headers).indexOf("Bezirk");
            int idxPop =Arrays.asList(headers).indexOf("AnzEinwohner");
            int idxCases =Arrays.asList(headers).indexOf("AnzahlFaelle");
            int idxCasesSum =Arrays.asList(headers).indexOf("AnzahlFaelleSum");
            int idxCasesWeek =Arrays.asList(headers).indexOf("AnzahlFaelle7Tage");
            int idxWeeklyInc =Arrays.asList(headers).indexOf("SiebenTageInzidenzFaelle");

            String[] columnValues = reader.readNext();

            while(!(columnValues[idxTime].equals("01.04.2021 00:00:00"))){
                columnValues = reader.readNext();
                columnValues = columnValues[0].split(";");
            }

            while((columnValues = reader.readNext())!=null) {

                columnValues = columnValues[0].split(";");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss");
                date = LocalDate.parse(columnValues[idxTime], formatter);

                java.sql.Date sqlDate = java.sql.Date.valueOf(date);

                String city = columnValues[idxCity].replace("(Stadt)","");
                int population = Integer.parseInt(columnValues[idxPop]);
                int covidCases = Integer.parseInt(columnValues[idxCases]);
                int covidCasesSum = Integer.parseInt(columnValues[idxCasesSum]);
                int covidCasesWeek = Integer.parseInt(columnValues[idxCasesWeek]);
                double covidCasesWeekInc = Double.parseDouble(columnValues[idxWeeklyInc]);

                if (Arrays.asList(new String[]{"Linz", "Wels", "Steyr"}).contains(city) && sqlDate.after(dabLastUpdDayDate)) {

                    String inst = "INSERT INTO covid_numerics (date_, location, number_of_residents, number_of_new_cases,number_of_cases_sum,number_of_cases_7days,incidence) " + " VALUES (?,?,?,?,?,?,?)";

                    try (PreparedStatement insert = con.prepareStatement(inst)) {

                        insert.setDate(1,sqlDate);
                        insert.setString(2, city);
                        insert.setInt(3, population);
                        insert.setInt(4, covidCases);
                        insert.setInt(5, covidCasesSum);
                        insert.setInt(6, covidCasesWeek);
                        insert.setDouble(7, covidCasesWeekInc);

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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                stmt.close();
                con.close();
            } catch (Exception e) {
            }
        }


    }
}
