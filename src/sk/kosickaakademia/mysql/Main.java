package sk.kosickaakademia.mysql;

import sk.kosickaakademia.mysql.entity.City;
import sk.kosickaakademia.mysql.entity.Country;
import sk.kosickaakademia.mysql.output.Output;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Database database = new Database();
        Output out = new Output();
        String name = "Slovakia";
        Country country = database.getCountryInfo(name);
        out.printCountryInfo(country);
        List<City> list=database.getCities(name);
        out.printCities(list);
    }
}
