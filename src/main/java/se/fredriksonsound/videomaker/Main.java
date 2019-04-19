package se.fredriksonsound.videomaker;

import org.json.JSONException;
import org.json.JSONObject;
import se.fredriksonsound.videomaker.media.FFMPEGMediaEditor;
import se.fredriksonsound.videomaker.redditIntegration.RedditAPI;
import se.fredriksonsound.videomaker.redditIntegration.RedditImageGrabber;
import se.fredriksonsound.videomaker.redditIntegration.RedditPost;
import se.fredriksonsound.videomaker.ttsImplementation.TTSImplementation;
import se.fredriksonsound.videomaker.ttsImplementation.oddCastTTSImplementation;

public class Main {

    public static void main(String[] args) {
        RedditImageGrabber imageGrabber = new RedditImageGrabber();
        TTSImplementation TTSImpl = new oddCastTTSImplementation();
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

        //TTSImpl.saveVoiceFile(postChunks[0], "chunk_1.mpga");
        imageGrabber.savePostScreenshot(post, "testPost.png");
        System.exit(0);
    }

    static void videoEditTest() {
        FFMPEGMediaEditor editor = new FFMPEGMediaEditor();
        double clipLength = editor.findLength("content2.mpga");
        editor.encodeVideoFromImage("image.png", clipLength,"output.mp4");
        editor.mux("output.mp4", "content2.mpga", "muxed.mp4");
    }
}
