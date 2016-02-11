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
import java.util.HashMap;
import java.util.Scanner;
import org.json.JSONArray;

public class KnowledgeBase {

    private static final String SEASONS = "(spring|summer|autumn|winter)";
    private static final String MONTHS = "(january|february|march|april|may|june|july|august|september|october|november|december)";
    private static final String DBP_ISCITY = "ask { <http://dbpedia.org/resource/%s> a <http://dbpedia.org/ontology/PopulatedPlace> }";

    private static String isCityQuery(String city) {
        return String.format(DBP_ISCITY, city);
    }

    public static String confirmCity(String input) {

        input = Util.sanitize(input);

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
        return Util.match(input, "(small|medium|big)");
    }

    /*
     * return (wikisection -> sectioncontent) map
     */
    public static HashMap<String, String> getWikiOn(String city) {

        JsonNode jsnode = getWikiExtract(city);

        if (jsnode == null) {
            return null;
        }

        JSONObject jsobj = jsnode.getObject();

        if (jsobj == null) {
            return null;
        }

        JSONObject pages = jsobj.getJSONObject("query").getJSONObject("pages");
        String extract = pages.getJSONObject(pages.names().getString(0)).getString("extract");

        /* parse out section and content */
        HashMap<String, String> wiki = new HashMap<>(5);

        extract = extract.replaceAll("\\\\n", "\n").trim();
        try (Scanner scan = new Scanner(extract)) {
            scan.useDelimiter("");

            /* general info */
            if (scan.hasNext("[^=]")) {
                String general = scan.nextLine();
                while (!scan.hasNext("[=]")) {
                    general += " " + scan.nextLine();
                }
                wiki.put("general", general.trim());
            }

            /* return non empty map */
            if (!scan.hasNext()) {
                return wiki.isEmpty() ? null : wiki;
            }

            /* section */
            while (scan.hasNext("[=]")) {

                /* header */
                String head = scan.nextLine().replaceAll("=", "")
                        .replaceAll("Edit", "") // in case wiki has a spazfit
                        .toLowerCase().trim();

                /* content */
                String content = "";
                while (!scan.hasNext("[=]") && scan.hasNextLine()) {
                    content += " " + scan.nextLine();
                }
                content = content.trim();
                if (!content.isEmpty()) {
                    wiki.put(head, content);
                }
            }

        }

        wiki.remove("external links");
        wiki.remove("references");
        wiki.remove("see also");

        return wiki.isEmpty() ? null : wiki;
    }

    private static final int MAX_WIKI_LENGTH = 500;

    public static String getSeason(String input) {
        return Util.match(input, SEASONS);
    }

    public static String getMonth(String input) {
        return Util.match(input, MONTHS);
    }

    public static String getTemperature(String input) {
        return Util.match(input, "(cold|mild|warm)");
    }

    public static JSONArray getCity(String size, String month, String tempr, boolean airport, boolean rainy, boolean sunny) {

        String mon = month.substring(0, 3);

        String sz;
        switch (size) {
            case "small":
                sz = "< 20000";
                break;
            case "medium":
                sz = "> 20000 && ?inhabitants < 100000";
                break;
            case "big":
                sz = "> 100000";
                break;
            default:
                sz = "< 20000";
        }

        String t;
        switch (tempr) {
            case "cold":
                t = "< 10";
                break;
            case "mild":
                t = "> 10 && ?temp < 20";
                break;
            case "warm":
                t = "> 20";
                break;
            default:
                t = "> 10 && ?temp < 20";
        }

        String rain = (rainy ? "> 50" : "<= 50");
        String sun = (sunny ? "> 250" : "<= 250");

        String airp = (airport ? "?air dbp:cityServed ?uri .\n" : "");

        String query
                = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX dbp: <http://dbpedia.org/property/>\n"
                + "PREFIX res: <http://dbpedia.org/resource/>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "SELECT DISTINCT ?city ?country ?wiki \n"
                + "WHERE {\n"
                /* city candidates */
                + "?uri rdf:type dbo:PopulatedPlace .\n"
                /* user criteria */
                + "?uri dbp:populationTotal ?inhabitants .\n"
                + "?uri dbp:%sMeanC ?temp.\n"
                + "?uri dbp:%sPrecipitationMm ?rain.\n"
                + "?uri dbp:%sSun ?sun.\n"
                + "%s"
                /* interns */
                + "?uri rdfs:label ?city .\n"
                + "?uri foaf:isPrimaryTopicOf ?wiki .\n"
                + "?uri dbo:country ?couri .\n"
                + "?couri rdfs:label ?country .\n"
                /* filters */
                + "FILTER (?inhabitants %s ) .\n"
                + "FILTER (?temp %s ) .\n"
                + "FILTER (?rain %s ) .\n"
                + "FILTER (?sun %s ) .\n"
                + "FILTER (lang(?city) = 'en') .\n"
                + "FILTER (lang(?country) = 'en') .\n"
                + "}\n"
                + "ORDER BY DESC(?inhabitants)\n"
                + "LIMIT 10\n";

        query = String.format(query, mon, mon, mon, airp, sz, t, rain, sun);

        JsonNode node = askDBpedia(query);

        if (node == null) {
            return null;
        }

        JSONObject jsobj = node.getObject();

        if (jsobj == null) {
            return null;
        }

        JSONObject results = jsobj.getJSONObject("results");
        JSONArray cities = results.getJSONArray("bindings");

        if (cities.length() == 0) {
            return null;
        }

        return cities;
    }

    public static String getWikiPage(String input) {

        String query
                = "PREFIX res: <http://dbpedia.org/resource/>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "SELECT DISTINCT ?wiki \n"
                + "WHERE {\n"
                + "res:%s foaf:isPrimaryTopicOf ?wiki .\n"
                + "}\n";

        query = String.format(query, input);

        JsonNode node = askDBpedia(query);

        if (node == null) {
            return null;
        }

        JSONObject jsobj = node.getObject();

        if (jsobj == null) {
            return null;
        }

        JSONObject results = jsobj.getJSONObject("results");
        JSONArray bindings = results.getJSONArray("bindings");

        if (bindings.length() == 0) {
            return null;
        }

        String wikipage = bindings.getJSONObject(0).getJSONObject("wiki").getString("value");
        String[] split = wikipage.split("/");
        return split[split.length - 1];
    }

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

    private final static String WIKIPEDIA_EN = "https://en.wikipedia.org/w/api.php";

    public static JsonNode getWikiExtract(String title) {

        try {
            HttpResponse<JsonNode> request = Unirest.get(WIKIPEDIA_EN)
                    .queryString("action", "query")
                    .queryString("prop", "extracts")
                    .queryString("format", "json")
                    .queryString("explaintext", "")
                    .queryString("exsectionformat", "wiki")
                    .queryString("titles", title)
                    .header("User-Agent", "Emma/1.0 (zimmeral@hu-berlin.de)")
                    .asJson();
            return request.getBody();
        } catch (UnirestException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public static double[] getLoc(String wikipage) {

        String query = "PREFIX dbr: <http://dbpedia.org/resource/>\n"
                + "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n"
                + "\n"
                + "SELECT DISTINCT ?lat ?long \n"
                + "WHERE {\n"
                + " dbr:%s geo:lat ?lat .\n"
                + " dbr:%s geo:long ?long .\n"
                + "}";
        query = String.format(query, wikipage, wikipage);

        JsonNode node = KnowledgeBase.askDBpedia(query);

        if (node == null) {
            return null;
        }

        JSONObject jsobj = node.getObject();

        if (jsobj == null) {
            return null;
        }

        JSONObject results = jsobj.getJSONObject("results");
        JSONArray bindings = results.getJSONArray("bindings");

        if (bindings.length() == 0) {
            return null;
        }

        double lat = bindings.getJSONObject(0).getJSONObject("lat").getDouble("value");
        double lon = bindings.getJSONObject(0).getJSONObject("long").getDouble("value");

        return new double[]{lat, lon};
    }
}

// Google Maps API key:   AIzaSyBbB8aOymNe97b6oFSG0-X21NDHVWLPgvg
