import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created by hs0490 on 9/11/14.
 */
public class GetPageLikes implements Runnable {
    int start;
    int end;
    JSONArray dataArray;
    static Hashtable<Integer,String> likeTable;


    GetPageLikes(int start, int end, JSONArray dataArray){
        this.start = start;
        this.end = end;
        this.dataArray = dataArray;
        likeTable = new Hashtable<Integer, String>(100);

    }


    @Override
    public void run() {
        try {
            for (int i = start; i < end ; i++) {
                JSONObject aJsonObject = dataArray.getJSONObject(i);
                String name = aJsonObject.getString("name");
                String pageID = aJsonObject.getString("id");
                int likes = getInfo(pageID);
                likeTable.put(likes,pageID);

                System.out.println(Thread.currentThread().getName()+" "+name + "--->" + aJsonObject.getString("id") + "---->" + likes);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getInfo(String id) throws JSONException, IOException {
        JSONParser aParser = new JSONParser();
        String url = "https://graph.facebook.com/"+id;
        JSONObject aJSONObject = aParser.readJsonFromUrl(url);
        return aJSONObject.getInt("likes");
    }

}
