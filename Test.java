import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Test {

    final static String WIKI_VOYAGE_EN = "https://de.wikivoyage.org/w/api.php";

    public static void main(String[] args) {

        System.out.println( getAllInfo("Berlin") );
    }

    public static String getAllInfo( String city ) {

        try {

            HttpResponse<String> request = Unirest.get(WIKI_VOYAGE_EN)
                .queryString("rvlimit", "1")
                .queryString("titles", city)
                .queryString("format", "json")
                .queryString("action", "query")
                .queryString("rvprop", "content")
                .queryString("prop", "revisions")
                .header("User-Agent", "Emma/1.0 (zimmeral@hu-berlin.de)" )
                .asString();
            return request.getBody();

        } catch( Exception ex ) {
            System.out.println(ex);
        }

        return "null";

    }
}
