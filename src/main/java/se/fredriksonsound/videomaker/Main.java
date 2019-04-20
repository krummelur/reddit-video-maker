package se.fredriksonsound.videomaker;

public class Main {

    static VideoMaker videomaker = new VideoMaker();

    private static void setExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            if (videomaker != null) {
                videomaker.destroy();
                //Give webdriver a chance to exit properly
                new Thread(() -> {
                    try {Thread.sleep(5000);}
                    catch (InterruptedException e1) {e1.printStackTrace();}
                    System.exit(-1);
                }).start();
            }
            else
                System.exit(-1);
        });
    }

    public static void main(String[] args) {
        setExceptionHandler();

        if (args.length == 0)
            printHelpAndExit();
        else if (args[0].equals(""))
            printHelpAndExit();

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

    static void printHelpAndExit() {
        System.out.println("This app creates a video from a given subreddit, it comes with a sweet royalty free bach soundtrack, but you can\nuse custom ones too!\nFirst argument must be a subreddit i.e. prorevenge\n" +
                "Optional second argument is desired length of video in seconds i.e. 300, default is 600\nOptional third argument is filename of background music\nOptional fourth argument is the post offset, if there are a lot of pinned posts, these can be skipped.\n\n" +
                "example usage:\njava -jar redditVideoMaker.jar prorevenge 300 nickelback-photograph.mp3 3");
        System.exit(-1);
    }
}
