package dke.prdke;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.*;



public class Scraper {



    public static void main(String[] args) {
        try {
            // Here we create a document object and use JSoup to fetch the website
            Document doc = Jsoup.connect("https://www.wetter.at/wetter/oesterreich/oberoesterreich/linz").get();
            // With the document fetched, we use JSoup's title() method to fetch the title
            System.out.printf("Title: %s\n", doc.select("body > div.frames.blur > div.body > main > section:nth-child(4) > section > section > div > div.geoLocationMain > div.left > div > span:nth-child(2)").text());
            // Get the list of repositories


            // In case of any IO errors, we want the messages written to the console
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}

