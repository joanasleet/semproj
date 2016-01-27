package de.codeception.semproj.knowledge;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class KnowledgeBase {

    public static final String SEASONS = "(spring|summer|autumn|winter)";
    public static final String CONTINENTS = "(europe|africa|asia|australia|america|north america|south america)";

    private static String isCityTemplate(String city) {
        final String template;
        template = "ask { <http://dbpedia.org/resource/%s> a <http://dbpedia.org/ontology/PopulatedPlace> }";
        return String.format(template, city);
    }

    public static String getCity(String input) {

        /* capitalize */
        StringBuilder capit = new StringBuilder(input.toLowerCase());
        capit.setCharAt(0, input.toUpperCase().charAt(0));
        input = capit.toString();

        // TODO: sanitize
        JsonNode node = askDBpedia(isCityTemplate(input));
        JSONObject jsobj = node.getObject();

        if (jsobj.has("boolean")) {
            return (jsobj.getBoolean("boolean") ? input : null);
        }
        return null;
    }

    public static String getCitySize(String input) {
        return null;
    }

    public static String getSeason(String input) {
        return Util.match(input, SEASONS);
    }

    public static String getContinent(String input) {
        return Util.match(input, CONTINENTS);
    }

    public static String getTemperature(String input) {
        return null;
    }

    public static JsonNode askDBpedia(String query) {

        JsonNode node;

        try {
            HttpResponse<JsonNode> resp = Unirest.get("http://dbpedia.org/sparql")
                    .queryString("default-graph-uri", "http://dbpedia.org")
                    .queryString("query", query)
                    .queryString("output", "json")
                    .asJson();
            return resp.getBody();
        } catch (UnirestException ex) {
            System.out.println(ex);
        }

        return null;
    }

}
