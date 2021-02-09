package sk.kosickaakademia.mysql.json;

import org.json.simple.*;
import sk.kosickaakademia.mysql.Database;
import sk.kosickaakademia.mysql.entity.Monument;

import java.util.List;

public class Server {
    public String getMonuments(){
        String resultJSON ="";
        List<Monument> list = new Database().getMonuments();
        if(list.isEmpty())
            return "{}";
        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        for(Monument m : list){
            JSONObject newItem = new JSONObject();
            newItem.put("id",m.getId());
            newItem.put("country",m.getCountry());
            newItem.put("city",m.getCity());
            newItem.put("monument",m.getName());
            jArray.add(newItem);
        }
        jObject.put("count", list.size());
        jObject.put("monuments",jArray);

        resultJSON = jObject.toJSONString();
        return resultJSON;
    }
}
