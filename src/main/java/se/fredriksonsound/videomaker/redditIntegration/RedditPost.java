package se.fredriksonsound.videomaker.redditIntegration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RedditPost {
    private final int CHUNKSIZE = 580;
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
        postJson.remove("seltext");
        try {
            postJson.put("selftext", result);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void chunkPost() {
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
}
