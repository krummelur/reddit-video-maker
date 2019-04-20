package se.fredriksonsound.videomaker;

import org.json.JSONException;
import org.json.JSONObject;
import se.fredriksonsound.videomaker.media.FFMPEGMediaEditor;
import se.fredriksonsound.videomaker.redditIntegration.RedditAPI;
import se.fredriksonsound.videomaker.redditIntegration.RedditImageGrabber;
import se.fredriksonsound.videomaker.redditIntegration.RedditPost;
import se.fredriksonsound.videomaker.ttsImplementation.TTSImplementation;
import se.fredriksonsound.videomaker.ttsImplementation.oddCastTTSImplementation;
import se.fredriksonsound.videomaker.utilities.Command;

import java.io.File;
import java.util.Arrays;

public class Main {
    static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            if (videomaker != null) {
                e.printStackTrace();
                videomaker.destroy();
                //Give webdriver a chance to exit properly
                new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(-1);
                });
            }
        }
    }

    static VideoMaker videomaker = new VideoMaker();

    public static void main(String[] args) {
        if (args.length == 0 || args == null) {
            printHelp();
        } else if (args[0].equals("")) {
            printHelp();
        }
        ExceptionHandler globalExceptionHandler = new ExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);
        if (args.length == 1) {
            videomaker.makeVideo(args[0]);
        } else if (args.length == 2) {
            videomaker.makeVideo(args[0], Integer.parseInt(args[1]));
        } else if (args.length == 3) {
            videomaker.makeVideo(args[0], Integer.parseInt(args[1]), args[2]);
        } else if (args.length == 4) {
            videomaker.makeVideo(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
        }
    }

    static void printHelp() {
        System.out.println("This app creates a video from a given subreddit, it comes with a sweet royalty free bach soundtrack, but you can\nuse custom ones too!\nFirst argument must be a subreddit i.e. prorevenge\n" +
                "Optional second argument is desired length of video in seconds i.e. 300, default is 600\nOptional third argument is filename of background music\nOptional fourth argument is the post offset, if there are a lot of pinned posts, these can be skipped.\n\n" +
                "example usage:\njava -jar redditVideoMaker.jar prorevenge 300 nickelback-photograph.mp3 3");
    }
}
