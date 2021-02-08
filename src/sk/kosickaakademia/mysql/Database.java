package sk.kosickaakademia.mysql;

import sk.kosickaakademia.mysql.entity.City;
import sk.kosickaakademia.mysql.entity.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private String url="jdbc:mysql://itsovy.sk:3306/world_x";
    private String username = "mysqluser";
    private String password = "Kosice2021!";

    public List<City> getCities(String country){
        String query = "SELECT city.Name, JSON_EXTRACT(Info,'$.Population') AS Population " +
                " FROM city " +
                " INNER JOIN country ON country.code = city.CountryCode " +
                " WHERE country.name LIKE ? ORDER BY Population DESC";
        List<City> list = new ArrayList<>();

        try {
            Connection conn = getConnection();
            if(conn!=null){

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1,country);

                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    String name = rs.getString("Name");
                    int pop = rs.getInt("Population");
                    City city = new City(name,pop);
                    list.add(city);
                }
                conn.close();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url,username,password);
        return conn;
    }

    public Country getCountryInfo(String country){
        String query = "SELECT country.name, country.code, city.name, " +
                " JSON_UNQUOTE(JSON_EXTRACT(doc, '$.geography.Continent')) AS Continent, " +
                " JSON_EXTRACT(doc, '$.geography.SurfaceArea') AS Area" +
                " FROM country " +
                " INNER JOIN city ON country.Capital = city.ID " +
                " INNER JOIN countryinfo ON country.code = countryinfo._id " +
                " WHERE country.name like ?";

        Country countryInfo = null;

        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,country);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String code3=rs.getString("country.code");
                String capitalCity=rs.getString("city.name");
                String continent=rs.getString("continent");
                int area=rs.getInt("Area");
                //System.out.println(code3 +" "+capitalCity+" "+continent+" "+area);
                countryInfo = new Country(country, code3, capitalCity,area, continent);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return countryInfo;
    }

    public String getCountryCode(String name)  {
        if(name==null || name.equalsIgnoreCase(""))
            return null;
        try {
            Connection con = getConnection();
            String query = "SELECT Code FROM country WHERE Name LIKE ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String code = rs.getString("Code");
                con.close();
                return code;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void insertCity(City newCity) {

        String country = newCity.getCountry();
        String code3 = getCountryCode(country);
        if(code3==null){
            System.out.println("Warning! Country "+country+" does not exist! ");
        }
        else{
            newCity.setCode3(code3);
            String query = "INSERT INTO city (Name, CountryCode, District, Info) " +
                    "VALUES( ?,?,?,?) ";
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1,newCity.getName());
                ps.setString(2,newCity.getCode3());
                ps.setString(3,newCity.getDistrict());
                String json="{ \"Population\":"+newCity.getPopulation()+"}";
                ps.setString(4,json);
                boolean result= ps.execute();
                System.out.println("Result: "+result);
                con.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public int existCity(String code3, String cityName){
        if(code3==null || cityName == null || code3.equals("") || cityName.equals(""))
            return -1;

        String query = "SELECT id FROM city WHERE CountryCode LIKE ? AND name LIKE ? ";
        Connection con=null;
        try {
             con = getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, code3);
            ps.setString(2, cityName);
            ResultSet rs = ps.executeQuery();
            System.out.println(ps);

            if(rs.next()){
                int id = rs.getInt("id");
                con.close();
                return id;
            }else{
                con.close();
                return -1;
            }

        }catch(Exception e){

        }finally {

        }
        return -1; // mesto neexistuje
    }

    public boolean insertNewMonument( String code3, String city, String name ){
        if(name==null || name.equals(""))
            return false;

        int cityId=existCity(code3, city);
        System.out.println(cityId);
        if(cityId==-1)
            return false;
        // vlozenie pamiatky
        String query = "INSERT INTO monument(name, city) VALUES (?, ?)";
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,name);
            ps.setInt(2,cityId);
            ps.executeUpdate();
            con.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
