package dke.prdke;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Scraper {

    public static void downloadFile(URL url, String fileName) throws Exception {
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(fileName));
        }
    }

    public static void main(String[] args) {
        try {
            // Here we create a document object and use JSoup to fetch the website
            Document doc = Jsoup.connect("https://www.data.gv.at/katalog/dataset/covid-19-zeitliche-darstellung-von-daten-zu-covid19-fallen-je-bezirk/resource/9eb08d45-ff99-40f1-90cd-7b3659b0bc8d").get();

            // With the document fetched, we use JSoup's title() method to fetch the title
            System.out.printf("Title: %s\n", doc.title());
            //System.out.printf("Title: %s\n", doc.getElementsByClass("descritpion").text());

            // Get the list of repositories
            Elements repositories = doc.getElementsByClass("resource-url-analytics");

            for (Element repository : repositories) {
                // Extract the title
                String fileUrl = repository.attr("title");
                System.out.println(fileUrl);

                //URL url = URL.;

                //downloadFile(url,"CovidFaelle_Timeline_GKZ.csv");

                BufferedReader csvReader = new BufferedReader(new FileReader(String.valueOf(Paths.get("CovidFaelle_Timeline_GKZ.csv"))));

                String row = null;
                String[] data = null;
                while ((row = csvReader.readLine()) != null) {
                    data = row.split(",");
                    // do something with the data
                }
                // Extract the number of issues on the repository
                //String repositoryIssues = repository.getElementsByClass("repo-item-issues").text();

                // Extract the description of the repository
               // String repositoryDescription = repository.getElementsByClass("repo-item-description").text();

                // Get the full name of the repository
                //String repositoryGithubName = repository.getElementsByClass("repo-item-full-name").text();

                // The reposiory full name contains brackets that we remove first before generating the valid Github link.
                //String repositoryGithubLink = "https://github.com/" + repositoryGithubName.replaceAll("[()]", "");

                // Format and print the information to the console
                System.out.println(data[0]);
                System.out.println("\n");

            }

            // In case of any IO errors, we want the messages written to the console
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

