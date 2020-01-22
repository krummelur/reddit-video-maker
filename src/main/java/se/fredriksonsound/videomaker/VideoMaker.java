package se.fredriksonsound.videomaker;

import org.json.JSONException;
import org.json.JSONObject;
import se.fredriksonsound.videomaker.media.FFMPEGMediaEditor;
import se.fredriksonsound.videomaker.redditIntegration.RedditAPI;
import se.fredriksonsound.videomaker.redditIntegration.RedditImageGrabber;
import se.fredriksonsound.videomaker.redditIntegration.RedditPost;
import se.fredriksonsound.videomaker.ttsImplementation.TTSImplementation;
import se.fredriksonsound.videomaker.ttsImplementation.oddCastTTSImplementation;



public class VideoMaker {
    FFMPEGMediaEditor editor = new FFMPEGMediaEditor();
    private String subreddit;
    RedditImageGrabber imageGrabber = null;
    private int DESIRED_LENGTH = 400;
    private String musicName = "bach.mp3";
    private int postOffset = 0;
    private final int maxPostLength = 250;

    void makeVideo(String subreddit, int desiredLength) {
        this.DESIRED_LENGTH = desiredLength;
        makeVideo(subreddit);
    }

    void makeVideo(String subreddit, int desiredLength, String musicName) {
        this.musicName = musicName;
        makeVideo(subreddit, desiredLength);
    }

    void makeVideo(String subreddit, int desiredLength, String musicName, int postOffset) {
        this.musicName = musicName;
        this.postOffset = postOffset;
        makeVideo(subreddit, desiredLength);
    }

    void makeVideo(String subreddit) {
        this.subreddit = subreddit;
        printStartMessage(subreddit);
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        imageGrabber = new RedditImageGrabber();
        TTSImplementation TTSImpl = new oddCastTTSImplementation();
        RedditAPI rAPI = new RedditAPI();
        JSONObject JResponse = rAPI.getHotPosts(subreddit);
        RedditPost post = null;


        int i = 0;
        double totalLength = 0;
        while(totalLength < DESIRED_LENGTH) {
            try {
                //First post is almost always meta, so let's skip that.
                //Let's also skip huge 12+ minute posts.


                do {
                    post = new RedditPost(((JSONObject) JResponse.getJSONObject("data").getJSONArray("children").get(i+postOffset)).getJSONObject("data"));

                    //If post is a cross post, find original post and create from that.
                    if(post.getChunks().length == 0) {

                        //Post is crosspost
                        if(post.isCrossPost()) {
                            JSONObject jObj = rAPI.getPost(post.getParent());
                            post = new RedditPost (((JSONObject) jObj.getJSONObject("data").getJSONArray("children").get(0)).getJSONObject("data"));
                        } else {
                            //post is just empty
                            postOffset++;
                        }

                    }
                    else {
                        postOffset += (post.isStickied()|| post.estimateSpokenLengthSeconds() > maxPostLength) ? 1 : 0;
                    }
                } while(post.isStickied()|| post.estimateSpokenLengthSeconds() > maxPostLength);


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            totalLength += post.estimateSpokenLengthSeconds();

            System.out.println("*********\nvideo length estimate after next post: "+totalLength+" sec\n*********");
            String[] postChunks = post.getChunks();
            for (int j = 0; j < postChunks.length; j++)
                System.out.println("\n" + postChunks[j]);

            String voiceFiles[] = new String[postChunks.length];
            for (int j = 0; j < postChunks.length; j++) {

                String curFilename = "chunk_" + j + ".mpga";
                TTSImpl.saveVoiceFile(postChunks[j], curFilename);
                voiceFiles[j] = curFilename;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int[] imageDimensions = imageGrabber.savePostScreenshot(post, "post.png");
            editor.concatenateMedia(voiceFiles, "concataudio.mp3");
            editor.mux("post.png", imageDimensions[1], "concataudio.mp3", "mux_"+i+".mp4");
            i++;
        }
        String postVideos[] = new String[i];
        for (int j = 0; j < i; j++) {
            postVideos[j] = "mux_"+j+".mp4";
        }
        editor.concatenateMedia(postVideos, "output_nomusic.mp4");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String outputFile = "output-"+ subreddit + ".mp4";
        editor.addMusicTrack("output_nomusic.mp4", "bach.mp3", outputFile);
        System.out.println("Video is done! output file: " + outputFile);
        imageGrabber.destroy();
        //System.exit(0);
    }

    public void destroy() {
        if(imageGrabber != null) {
            imageGrabber.destroy();
        }
    }

    private void printStartMessage(String subreddit) {
        System.out.println("******************************\nCreating video from subreddit: " + subreddit + "\nDesired length: "+DESIRED_LENGTH+" seconds\n*****************************\n" +
                "In case of errors, make sure that the chromedriver matches your chrome installation version (donload: http://chromedriver.chromium.org/downloads)\n" +
                "if you are on mac you must download the mac version and place in /chromedriver.\n");
    }
}
