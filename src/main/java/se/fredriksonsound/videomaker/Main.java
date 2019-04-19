package se.fredriksonsound.videomaker;

import org.json.JSONException;
import org.json.JSONObject;
import se.fredriksonsound.videomaker.media.FFMPEGMediaEditor;
import se.fredriksonsound.videomaker.redditIntegration.RedditAPI;
import se.fredriksonsound.videomaker.redditIntegration.RedditPost;
import se.fredriksonsound.videomaker.ttsImplementation.TTSImplementation;
import se.fredriksonsound.videomaker.ttsImplementation.oddCastTTSImplementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Main {

    public static void main(String[] args) {
    //TTSImplementation TTSImpl = new oddCastTTSImplementation();
    //String mp3Content = TTSImpl.getVoiceDataAsString("max är gay");
    //System.out.println(mp3Content);
        RedditAPI rAPI = new RedditAPI();
        JSONObject JResponse = rAPI.getHotPosts("prorevenge");
        RedditPost post = null;
        try {
            post = new RedditPost(((JSONObject)JResponse.getJSONObject("data").getJSONArray("children").get(1))
                    .getJSONObject("data"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] postChunks = post.getChunks();
        for(int i = 0; i < postChunks.length; i++)
            System.out.println("\n"+postChunks[i]);
        System.exit(0);
    }

    static void videoEditTest() {
        FFMPEGMediaEditor editor = new FFMPEGMediaEditor();
        double clipLength = editor.findLength("content2.mpga");
        editor.encodeVideoFromImage("image.png", clipLength,"output.mp4");
        editor.mux("output.mp4", "content2.mpga", "muxed.mp4");
    }
}
