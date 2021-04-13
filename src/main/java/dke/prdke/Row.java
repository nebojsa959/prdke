package dke.prdke;

import java.sql.Date;

public class Row {

    public java.sql.Date sqlDate;
    public String city;
    public int population;
    public int covidCases;
    public int covidCasesSum;
    public int covidCasesWeek;
    public double covidCasesWeekInc;

    public Row() {
        this.sqlDate = null;
        this.city = null;
        this.population = 0;
        this.covidCases = 0;
        this.covidCasesSum = 0;
        this.covidCasesWeek = 0;
        this.covidCasesWeekInc = 0;
    }

    public Row(Date sqlDate, String city, int population, int covidCases, int covidCasesSum, int covidCasesWeek, double covidCasesWeekInc) {
        this.sqlDate = sqlDate;
        this.city = city;
        this.population = population;
        this.covidCases = covidCases;
        this.covidCasesSum = covidCasesSum;
        this.covidCasesWeek = covidCasesWeek;
        this.covidCasesWeekInc = covidCasesWeekInc;
    }

    public Date getSqlDate() {
        return sqlDate;
    }

    public void setSqlDate(Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getCovidCases() {
        return covidCases;
    }

    public void setCovidCases(int covidCases) {
        this.covidCases = covidCases;
    }

    public int getCovidCasesSum() {
        return covidCasesSum;
    }

    public void setCovidCasesSum(int covidCasesSum) {
        this.covidCasesSum = covidCasesSum;
    }

    public int getCovidCasesWeek() {
        return covidCasesWeek;
    }

    public void setCovidCasesWeek(int covidCasesWeek) {
        this.covidCasesWeek = covidCasesWeek;
    }

    public double getCovidCasesWeekInc() {
        return covidCasesWeekInc;
    }

    public void setCovidCasesWeekInc(double covidCasesWeekInc) {
        this.covidCasesWeekInc = covidCasesWeekInc;
    }
}
