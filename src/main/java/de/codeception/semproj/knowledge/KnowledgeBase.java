/*
 * Copyright 2015 Julia <julia@julia-laptop>, <alex@codeception.de>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package de.codeception.semproj.knowledge;

import org.json.JSONObject;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

public class KnowledgeBase {

    private static final String SEASONS = "(spring|summer|autumn|winter)";
    private static final String CONTINENTS = "(europe|africa|asia|australia|america|north america|south america)";
    private static final String DBP_ISCITY = "ask { <http://dbpedia.org/resource/%s> a <http://dbpedia.org/ontology/PopulatedPlace> }";

    // TODO: underscore multi word inputs
    private static String isCityQuery(String city) {

        return String.format(DBP_ISCITY, city);
    }

    public static String confirmCity(String input) {

        input = Util.capitalize(input);

        // TODO: sanitize bad input: "I want to travel to London"
        JsonNode node = askDBpedia(isCityQuery(input));
        if (node == null) {
            return null;
        }

        JSONObject jsobj = node.getObject();
        if (jsobj == null) {
            return null;
        }

        if (jsobj.has("boolean")) {
            return (jsobj.getBoolean("boolean") ? input : null);
        }
        return null;
    }

    public static String getCitySize(String input) {
        return Util.match(input, "(small|middle|big)");
    }

    public static String getWikiOn(String city) {

        JsonNode jsnode = getWiki(city);

        return null;
    }

    public static String getSeason(String input) {
        return Util.match(input, SEASONS);
    }

    public static String getContinent(String input) {
        return Util.match(input, CONTINENTS);
    }

    public static String getTemperature(String input) {
        return Util.match(input, "([0-9]+)|(warm|cold|mild)");
    }

    /*
     * external resource wrapper 
     */
    public static JsonNode askDBpedia(String query) {

        System.out.println("DBpedia query: \n" + query);

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

    private final static String WIKI_VOYAGE_EN = "https://en.wikivoyage.org/w/api.php";

    public static JsonNode getWiki(String title) {

        try {
            HttpResponse<JsonNode> request = Unirest.get(WIKI_VOYAGE_EN)
                    .queryString("rvlimit", "1")
                    .queryString("titles", title)
                    .queryString("format", "json")
                    .queryString("action", "query")
                    .queryString("rvprop", "content")
                    .queryString("prop", "revisions")
                    .header("User-Agent", "Emma/1.0 (zimmeral@hu-berlin.de)")
                    .asJson();
            return request.getBody();
        } catch (UnirestException ex) {
            System.out.println(ex);
        }

        return null;
    }

}
