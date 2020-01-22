package se.fredriksonsound.videomaker.redditIntegration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RedditPost {
    private final int CHUNKSIZE = 580;
    private final double averageLengthOfSpokenChar = 0.0616;
    private JSONObject postJson;
    private ArrayList<String> postBodyChunks = new ArrayList<>();

    public RedditPost(JSONObject post) {
        this.postJson = post;
        cleanTextBody();
    }

    public String[] getChunks() {
        chunkPost();
        String[] chunksArr = new String[postBodyChunks.size()];
        for(int i = 0; i < chunksArr.length; i++)
            chunksArr[i] = postBodyChunks.get(i);
        return chunksArr;
    }

    public String getName() {
        try {
            return postJson.getString("name");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getID() {
        try {
            return postJson.getString("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void cleanTextBody() {
        String result = null;
        try {
            result = postJson.getString("selftext");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        result = result.replaceAll("\n", " ");
        result = result.replaceAll("&amp;", " ");
        result = result.replaceAll( "#x.{1,4};", " ");
        result = result.replaceAll( "  ", " ");
        //postJson.remove("selftext");
        try {
            postJson.put("selftext", result);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void chunkPost() {
        this.postBodyChunks = new ArrayList<>();
        String text = null;
        try {
            text = postJson.getString("selftext");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        while(text.length() > 0) {
            int nextChunkSz = text.length() >= CHUNKSIZE ? CHUNKSIZE : text.length();
            while(text.contains(" ") && text.charAt(nextChunkSz-1) != ' ' && text.charAt(nextChunkSz-1) != '.' && nextChunkSz != 1)
                nextChunkSz--;

            if(!text.substring(0, nextChunkSz).trim().equals(""))
                postBodyChunks.add(text.substring(0, nextChunkSz).trim());

            text = text.substring(nextChunkSz).trim();
        }
    }

    public double estimateSpokenLengthSeconds() {
        try {
            return averageLengthOfSpokenChar * this.postJson.getString("selftext").length();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCrossPost() {
        try {
            postJson.getString("crosspost_parent");
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public Object getParent() {
        try {
            return postJson.getString("crosspost_parent").replaceAll(".._","");
        } catch (JSONException e) {
         throw new RuntimeException(e);
        }
    }

    public boolean isSelf() {
        try {
            return postJson.getBoolean("is_self");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isStickied() {
        try {
            return postJson.getBoolean("stickied");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
