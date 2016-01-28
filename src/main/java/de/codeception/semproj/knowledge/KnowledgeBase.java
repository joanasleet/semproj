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
        JSONObject jsobj = node.getObject(); // will crash on bad input "I want to travel to London"

        if (jsobj.has("boolean")) {
            return (jsobj.getBoolean("boolean") ? input : null);
        }
        return null;
    }

    public static String getCitySize(String input) {
        return Util.match(input, "(small|middle|big)");
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

    public static JsonNode askDBpedia(String query) {

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
