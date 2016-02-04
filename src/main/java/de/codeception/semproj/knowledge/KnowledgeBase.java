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
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.json.JSONArray;

public class KnowledgeBase {

    private static final String SEASONS = "(spring|summer|autumn|winter)";
    private static final String CONTINENTS = "(europe|africa|asia|australia|america|north america|south america)";
    private static final String DBP_ISCITY = "ask { <http://dbpedia.org/resource/%s> a <http://dbpedia.org/ontology/PopulatedPlace> }";

    // TODO: underscore multi word inputs
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

    public static String getWikiOn(String city) {

        JsonNode jsnode = getWiki(city);

        if (jsnode == null) {
            return null;
        }

        JSONObject jsobj = jsnode.getObject();

        if (jsobj == null) {
            return null;
        }

        JSONObject pages = jsobj.getJSONObject("query").getJSONObject("pages");
        String wiki = pages.getJSONObject(pages.names().getString(0))
                .getJSONArray("revisions").getJSONObject(0).getString("*");

        return wiki.replaceAll("\\\\n", "\n")
                .replaceAll("\\[{2}\\w+:[^\\]]+\\]{2}", "") // strip file link
                .replaceAll("\\{{2}[^\\}]+\\}{2}", "") // strip ref link
                .replaceAll("\\{[^\\}]+\\}", "") // strip templates

                .replaceAll("\\[{2}", "") // strip link
                .replaceAll("\\]{2}", "") // strip link

                .replaceAll("\\[[^ ]+ ", "") // strip file link
                .replaceAll("\\]", "") // strip file link

                .replaceAll("''''", "\"") // text formatig
                .replaceAll("'''", "\"") // text formatig
                .replaceAll("''", "\"") // text formatig

                .replaceAll("[*][ ]?", "")
                .replaceAll("\n{3,}", "\n");
    }

    private static final int MAX_WIKI_LENGTH = 500;
    private static final String WIKI_SECTION_HEAD = "(={1,})([ ]*)(%s)([ ]*)(={1,})";

    public static String getWikiSection(String wiki, String section) {

        try (Scanner scan = new Scanner(wiki)) {

            scan.useDelimiter("");

            /* general info */
            if (section == null) {
                if (scan.hasNext("[^=]+")) {
                    return wiki.substring(0, MAX_WIKI_LENGTH).trim();
                }
                return null;
            }

            /* section info */
            String head = String.format(WIKI_SECTION_HEAD, section);

            /* find section */
            while (scan.hasNextLine()) {
                if (scan.findInLine(head) != null) {
                    break;
                } else {
                    scan.nextLine();
                }
            }

            if (!scan.hasNextLine()) {
                return null;
            }

            String info = "";
            while (scan.hasNextLine() && !scan.hasNext("[=]")) {
                info += scan.nextLine() + " ";
            }

            info = info.trim();
            info = info.substring(0, Math.min(info.length() - 1, MAX_WIKI_LENGTH)).trim();
            return (info.isEmpty() ? null : info);

        } catch (NoSuchElementException ex) {
            System.out.println("WARN: No " + section + " section in wiki");
        }
        return null;
    }

    public static String getSeason(String input) {
        return Util.match(input, SEASONS);
    }

    public static String getContinent(String input) {
        return Util.match(input, CONTINENTS);
    }

    public static String getTemperature(String input) {
        return Util.match(input, "(cold|mild|warm)");
    }

    public static String getCity(String cont, String size, String seas, String tempr, boolean airport, boolean rainy, boolean sunny) {

        String mon;
        switch (seas) {
            case "summer":
                mon = "jul";
                break;
            case "winter":
                mon = "jan";
                break;
            case "spring":
                mon = "apr";
                break;
            case "autumn":
                mon = "sep";
                break;
            default:
                mon = "jan";
        }

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

        String t = "";
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

        String airp = (airport ? "?uri dbp:cityServed ?air .\n" : "");

        String query
                = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX dbp: <http://dbpedia.org/property/>\n"
                + "PREFIX res: <http://dbpedia.org/resource/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "SELECT DISTINCT ?city\n"
                + "WHERE {\n"
                + "?uri rdf:type dbo:PopulatedPlace .\n"
                + "?uri dbp:populationTotal ?inhabitants .\n"
                + "?uri dbp:%sMeanC ?temp.\n"
                + "?uri dbp:%sPrecipitationMm ?rain.\n"
                + "?uri dbp:%sSun ?sun.\n"
                + "%s"
                + "?uri rdfs:label ?city .\n"
                + "FILTER (?inhabitants %s ) .\n"
                + "FILTER (?temp %s ) .\n"
                + "FILTER (?rain %s ) .\n"
                + "FILTER (?sun %s ) .\n"
                + "FILTER (lang(?city) = 'en') .\n"
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

        JSONObject city0 = cities.getJSONObject(0).getJSONObject("city");
        String city = city0.getString("value");

        return city;
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
