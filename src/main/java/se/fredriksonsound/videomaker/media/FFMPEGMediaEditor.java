package se.fredriksonsound.videomaker.media;

import se.fredriksonsound.videomaker.utilities.Command;

import java.io.File;

public class FFMPEGMediaEditor {


    public void encodeVideoFromImage(String imageFile, double length, String outputFilename) {
        new File(outputFilename).delete();
        String ffmpegCommand = "ffmpeg -r 1/" + length +" -i " + imageFile + " -c:v libx264 -vf fps=25 -pix_fmt yuv420p " + outputFilename + " -loglevel error";
        Command.executeCommand(ffmpegCommand, false);
    }

    public double findDuration(String mediaFilename) {
        String ffmpegCommand = "ffmpeg -i " + mediaFilename;
        String commandResult = Command.executeCommand(ffmpegCommand, false);
        int durationIndex = commandResult.indexOf("Duration:");
        durationIndex = commandResult.indexOf("00:", durationIndex);
        String unparsedDuration = commandResult.substring(durationIndex, durationIndex+11);
        return durationToSeconds(unparsedDuration);
    }

    public void concatenateMedia(String[] files, String outputFilename) {
        new File(outputFilename).delete();
        String concatFileArgs = "";
        for(int i = 0; i < files.length; i++) {
            if(i != 0)
                concatFileArgs += "|";
            concatFileArgs += files[i];
        }
        String ffmpegCommand = "ffmpeg -i \"concat:"+ concatFileArgs +"\" -c copy " + outputFilename + " -loglevel error";
        Command.executeCommand(ffmpegCommand, false);
    }


    public void mux(String inputImageFilename, int imageHeight, String inputAudioFilename, String outputFilename) {
        new File(outputFilename).delete();
        double duration = findDuration(inputAudioFilename);
        String ffmpegCommand = "ffmpeg -f lavfi -i color=c=white:s=1920x1080:d=" +
                duration +
                " -loop 1 -i " +
                inputImageFilename +
                " -i " +
                inputAudioFilename +
                " -filter_complex \"[1:v]scale=897:"+
                imageHeight +"[fg]; [0:v][fg]overlay=y=-'t*(h-1000)*"+
                1/duration + "':x=500:shortest=1[v]\" -map \"[v]\" -map 2:a " +
                outputFilename +
                " -loglevel error";

        Command.executeCommand(ffmpegCommand, false);

    /*
        String ffmpegCommand = "ffmpeg -i " + inputVideoFilename + " -i " + inputAudioFilename + " -codec copy -shortest " + outputFilename;
        Command.executeCommand(ffmpegCommand, false);
    */
    }

    private double durationToSeconds (String strDuration) {
        int hours = Integer.parseInt(strDuration.substring(0,2));
        int minutes = Integer.parseInt(strDuration.substring(3,5));
        double  seconds = Double.parseDouble(strDuration.substring(6,11));
        return hours*60*60+minutes*60+seconds;
    }
}
