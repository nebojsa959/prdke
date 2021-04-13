package dke.prdke;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.*;



public class Scraper {



    public static void main(String[] args) {
        try {

            Document doc = Jsoup.connect("https://www.wetter.at/wetter/oesterreich/oberoesterreich/linz").get();

            String temp = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.left > div > span:nth-child(2)").text();
            String rain = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.left > div > span.d-block.mt-2.pt-4").text();
            String wind = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.left > div > span.d-block.notetext").text();
            String clouds = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.bottom > div.flex-grow-1 > span.d-block.notetext").text();
            String weather = doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.bottom > div.flex-grow-1 > span.d-block.mt-2").text();

            String minTemp=temp.substring(0,temp.indexOf("° /"));
            String maxTemp=temp.substring(temp.indexOf("° /")+4,temp.length()-1);
            rain=rain.substring(15,rain.length()-5);
            String strengthWind=wind.substring(6,wind.indexOf("km/h"));
            String directionWind =wind.substring(wind.indexOf("km/h")+5,wind.length());
            clouds=clouds.substring(0,4);


            System.out.printf("Minimale Temperatur: %s\n", minTemp);
            System.out.printf("Maximale Temperatur: %s\n", maxTemp);
            System.out.printf("Niederschlag in mm/h: %s\n", rain);
            System.out.printf("Wind in km/h: %s\n", strengthWind);
            System.out.printf("Windrichtung: %s\n", directionWind);
            System.out.printf("Bewölkung in Prozent: %s\n", clouds);
            System.out.printf("Wetter: %s\n", weather);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}

