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

        static FFMPEGMediaEditor editor = new FFMPEGMediaEditor();
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

        String concatFiles[] = new String[postChunks.length];
        for(int i = 0; i < postChunks.length; i++) {

            String curFilename = "chunk_"+i+".mpga";
            //TTSImpl.saveVoiceFile(postChunks[i], curFilename);
            concatFiles[i] = curFilename;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int[] imageDimensions = imageGrabber.savePostScreenshot(post, "testPost.png");
        editor.concatenateMedia(concatFiles, "concatFin.mp3");
        editor.mux("testPost.png",imageDimensions[1],"concatFin.mp3", "programux.mp4");
        System.exit(0);
    }

    static void videoEditTest() {

    }
}
