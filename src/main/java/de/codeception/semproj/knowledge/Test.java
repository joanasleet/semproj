package de.codeception.semproj.knowledge;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {

    final static String WIKI_VOYAGE_EN = "https://en.wikivoyage.org/w/api.php";

    public static void main(String[] args) {

        JsonNode node = getAllInfo("Berlin");

        if (node == null) {
            System.out.println("error");
            return;
        }

        JSONObject obj = node.getObject();

        if (obj == null) {
            System.out.println("error");
            return;
        }

        JSONObject pages = obj.getJSONObject("query").getJSONObject("pages");
        String wiki = pages.getJSONObject(pages.names().getString(0))
                .getJSONArray("revisions").getJSONObject(0).getString("*");

        wiki = wiki.replaceAll("\\\\n", "\n");

        wiki = wiki.replaceAll("\\[{2}\\w+:[^\\]]+\\]{2}", ""); // strip file link
        wiki = wiki.replaceAll("\\{{2}[^\\}]+\\}{2}", ""); // strip ref link
        wiki = wiki.replaceAll("\\{[^\\}]+\\}", ""); // strip templates

        wiki = wiki.replaceAll("\\[{2}", ""); // strip link
        wiki = wiki.replaceAll("\\]{2}", ""); // strip link

        wiki = wiki.replaceAll("\\[[^ ]+ ", ""); // strip file link
        wiki = wiki.replaceAll("\\]", ""); // strip file link

        wiki = wiki.replaceAll("''''", "\""); // text formatig
        wiki = wiki.replaceAll("'''", "\""); // text formatig
        wiki = wiki.replaceAll("''", "\""); // text formatig

        wiki = wiki.replaceAll("[*][ ]?", "");
        wiki = wiki.replaceAll("\n{3,}", "\n");
        System.out.println(wiki);
    }

    public static JsonNode getAllInfo(String city) {

        try {
            HttpResponse<JsonNode> request = Unirest.get(WIKI_VOYAGE_EN)
                    .queryString("titles", city)
                    //.queryString("rvlimit", "1")
                    .queryString("format", "json")
                    .queryString("action", "query")
                    .queryString("rvprop", "content")
                    .queryString("prop", "revisions")
                    .header("User-Agent", "Emma/1.0 (zimmeral@hu-berlin.de)")
                    .asJson();
            return request.getBody();

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }
}
