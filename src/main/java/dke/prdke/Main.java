package dke.prdke;

public class Main {

    public static void main(String[] args) {
        CovidCSV.updateCovidData();
        Scraper.updateWeatherData("linz");
        Scraper.updateWeatherData("wels");
        Scraper.updateWeatherData("steyr");
    }
}
