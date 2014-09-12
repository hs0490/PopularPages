import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hs0490 on 9/12/14.
 */
public class PageInfo implements Runnable  {


    public static Hashtable<String,String> pagesTable;
    public static SortedMap<Integer, String> likeCountMap;
    public static SortedMap<Integer, String> talkingAboutCountMap;

    private int likesCount;
    private int talkingAboutCount;
    private boolean isCommunityPage;
    private static JSONArray dataArray;
    private int start;
    private int end;

    // Default Constructor
    PageInfo(){
        pagesTable = new Hashtable<String, String>();
        talkingAboutCountMap = Collections.synchronizedSortedMap(new TreeMap<Integer, String>());
        likeCountMap = Collections.synchronizedSortedMap(new TreeMap<Integer, String>());
    }

    PageInfo(int start, int end){
        this.start = start;
        this.end = end;
    }


    public void getPageInfo(String url) throws IOException, JSONException {

        JSONParser aParser = new JSONParser();
        JSONObject json = aParser.readJsonFromUrl(url);
        dataArray = json.getJSONArray("data");
        int mid = ((dataArray.length()%2)==0)?dataArray.length()/2: (dataArray.length()+1)/2;

        // Two threads reading the data
        Thread threadOne = new Thread(new PageInfo(0,mid));
        Thread threadTwo = new Thread(new PageInfo(mid,dataArray.length()));
        threadOne.setName("First");
        threadOne.start();
        threadTwo.setName("Second");
        threadTwo.start();
    }

    @Override
    public void run(){

        try {
            for (int i = start; i < end ; i++) {
                JSONObject aJSONObject = dataArray.getJSONObject(i);
                String pageName = aJSONObject.getString("name");
                String pageID = aJSONObject.getString("id");
                getCount(pageID);
                String uniqueName = pageName+" ("+pageID+")";
                if(! isCommunityPage){
                    pagesTable.put(pageID,uniqueName);
                    likeCountMap.put(likesCount, pageID);
                    talkingAboutCountMap.put(talkingAboutCount,pageID);
                }
                System.out.println(Thread.currentThread().getName()+ " Thread Reading---> PageID=" +pageID+ "----> LikesCount=" +likesCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getCount(String id) throws JSONException, IOException {
        JSONParser aParser = new JSONParser();
        String url = "https://graph.facebook.com/"+id;
        JSONObject aJSONObject = aParser.readJsonFromUrl(url);
        likesCount = aJSONObject.getInt("likes");
        talkingAboutCount = aJSONObject.getInt("talking_about_count");
        isCommunityPage = aJSONObject.getBoolean("is_community_page");
    }


}
