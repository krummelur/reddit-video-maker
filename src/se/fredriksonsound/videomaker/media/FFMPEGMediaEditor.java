package se.fredriksonsound.videomaker.media;

import se.fredriksonsound.videomaker.utilities.Command;

public class FFMPEGMediaEditor {


    public void encodeVideoFromImage(String imageFile, double length, String outputFilename) {
        String ffmpegCommand = "ffmpeg -r 1/" + length +" -i " + imageFile + " -c:v libx264 -vf fps=25 -pix_fmt yuv420p " + outputFilename;
        Command.executeCommand(ffmpegCommand, false);
    }

    public double findLength(String mediaFilename) {
        String ffmpegCommand = "ffmpeg -i " + mediaFilename;
        String commandResult = Command.executeCommand(ffmpegCommand, false);
        int durationIndex = commandResult.indexOf("Duration:");
        durationIndex = commandResult.indexOf("0:", durationIndex);
        String unparsedDuration = commandResult.substring(durationIndex, durationIndex+11);
        return durationToSeconds(unparsedDuration);
    }

    public void mux(String inputVideoFilename, String inputAudioFilename, String outputFilename) {
        String ffmpegCommand = "ffmpeg -i " + inputVideoFilename + " -i " + inputAudioFilename + " -codec copy -shortest " + outputFilename;
    }

    private double durationToSeconds (String strDuration) {
        int hours = Integer.parseInt(strDuration.substring(0,1));
        int minutes = Integer.parseInt(strDuration.substring(3,4));
        double  seconds = Double.parseDouble(strDuration.substring(6,10));
        return hours*60*60+minutes*60+seconds;
    }
}
