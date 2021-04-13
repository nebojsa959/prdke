package dke.prdke;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.opencsv.CSVReader;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import java.sql.*;


public class CovidCSV {

    private static void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

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

        PreparedStatement stmt = null;
        LocalDate date = null;
        try {
            Document doc = Jsoup.connect("https://www.data.gv.at/katalog/dataset/covid-19-zeitliche-darstellung-von-daten-zu-covid19-fallen-je-bezirk/resource/9eb08d45-ff99-40f1-90cd-7b3659b0bc8d").get();

            String url = doc.select("#content > main > div.card.rounded-0.border-0.p-0.mb-5 > div.card-body.rounded-0.border-0.bg-secondary.p-0 > div.row.p-0.m-0.rounded-0.wrapper > section > div.module-content.col-12 > div.clearfix > p > a").text();

            //downloadUsingStream(url,"CovidFaelle.csv");

            String fileName = "CovidFaelle.csv";
            CSVReader reader = new CSVReader(new FileReader(fileName));

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

                    String city = columnValues[idxCity];
                    int population = Integer.parseInt(columnValues[idxPop]);
                    int covidCases = Integer.parseInt(columnValues[idxCases]);
                    int covidCasesSum = Integer.parseInt(columnValues[idxCasesSum]);
                    int covidCasesWeek = Integer.parseInt(columnValues[idxCasesWeek]);
                    double covidCasesWeekInc = Double.parseDouble(columnValues[idxWeeklyInc]);

                    if (Arrays.asList(new String[]{"Linz(Stadt)", "Wels(Stadt)", "Steyr(Stadt)"}).contains(city) && LocalDate.now(ZoneId.of("Europe/Vienna")).minusDays(2).isEqual(date)) {
                        System.out.println(date.toString() + " " + city + " " + covidCases + ";");

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
