package se.fredriksonsound.videomaker.redditIntegration;

import org.json.JSONException;
import org.json.JSONObject;
import se.fredriksonsound.videomaker.utilities.Http;

import java.io.IOException;

public class RedditAPI {
    private final String redditBaseURL = "https://api.reddit.com/";
    private Http httpUtility = new Http();

    public JSONObject getHotPosts(String subreddit) {
        String responseStr = null;
        try {
            responseStr = httpUtility.sendGET(redditBaseURL+"/r/"+subreddit+"/hot");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            return new JSONObject(responseStr);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
