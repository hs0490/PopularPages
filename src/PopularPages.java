import com.itextpdf.text.DocumentException;
import org.json.JSONException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by hs0490 on 9/11/14.
 */
public class PopularPages {

    public static void main(String[] args) throws IOException, JSONException, SQLException, DocumentException {

        String accessToken = "CAACEdEose0cBAHWFkwdlQvTvtnC9IZCAjL7uxhK5LD1LitqWZCv0wjajZBwqZBahZA5ZB0xWJgZAGJZBcPC3wbbzanfNfNemVIGl6MaeZCS6381ifKqeLDt5mwVrYsboMHCzwxpLz02wUgai13GV1zLF3YE81qpIjm5KnFYyH4U5uuHzOVfrXketpAowgZArcQvFd9pTpKZB5ZA5Bc4Mr4MfLvtX";
        String appName = "subway+surfers";
        String url = "https://graph.facebook.com/search?q="+appName+"&fields=name&type=page&access_token=" + accessToken;
        String reportName = "bar.pdf";
        String fileName = "a.txt";

        MakeReport aReport = new MakeReport(url,fileName);
        aReport.createPdf(reportName);



    }
}

