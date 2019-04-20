package se.fredriksonsound.videomaker.media;

import se.fredriksonsound.videomaker.utilities.Command;

import java.io.*;

public class FFMPEGMediaEditor {


    public void encodeVideoFromImage(String imageFile, double length, String outputFilename) {
        new File(outputFilename).delete();
        String ffmpegCommand = "ffmpeg -r 1/" + length + " -i " + imageFile + " -c:v libx264 -vf fps=25 -pix_fmt yuv420p " + outputFilename + " -loglevel error";
        Command.executeCommand(ffmpegCommand, false);
    }

    public double findDuration(String mediaFilename) {
        String ffmpegCommand = "ffmpeg -i " + mediaFilename;
        String commandResult = Command.executeCommand(ffmpegCommand, true);
        int durationIndex = commandResult.indexOf("Duration:");
        durationIndex = commandResult.indexOf("00:", durationIndex);
        String unparsedDuration = commandResult.substring(durationIndex, durationIndex + 11);
        return durationToSeconds(unparsedDuration);
    }

    public void concatenateMedia(String[] files, String outputFilename) {
        new File(outputFilename).delete();
        boolean isUnsupported = false;
        for (int i = 0; i < files.length; i++) {
            isUnsupported |= files[i].endsWith(".mp4");
        }

        String ffmpegCommand = "";
        String concatFileArgs = "";
        String concatFilename = "concat.txt";
        //For some F***n reason, ffmpeg demux protocol ONLY works when submitting files argument as a path to a file containing the names to concatenate...
        if (isUnsupported) {
            for (int i = 0; i < files.length; i++) {
                concatFileArgs += "file '" + files[i] + "'\n";
            }
            File textFile = new File(concatFilename);
            try {
                textFile.delete();
                BufferedWriter writer = new BufferedWriter(new FileWriter(concatFilename));
                writer.write(concatFileArgs);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ffmpegCommand = "ffmpeg -f concat -i " + textFile.getName() + " -c copy " + outputFilename + " -loglevel error";
        } else {
            for (int i = 0; i < files.length; i++) {
                if (i != 0)
                    concatFileArgs += "|";
                concatFileArgs += files[i];
                ffmpegCommand = "ffmpeg -i concat:\"" + concatFileArgs + "\" -c copy " + outputFilename + " -loglevel error";
            }
        }
        Command.executeCommand(ffmpegCommand, false);
    }
        public void addMusicTrack(String inputVideo, String musicFile, String outputFile){
            new File(outputFile).delete();
            String ffmpegCommand = "ffmpeg -i " +
                    inputVideo + " -filter_complex \"amovie=" +
                    musicFile +
                    ":loop=999[s];[0][s]amix=duration=shortest,volume=2\" -movflags +faststart " +
                    outputFile +
                    " -loglevel error";
            Command.executeCommand(ffmpegCommand, false);
        }

        public void mux (String inputImageFilename,int imageHeight, String inputAudioFilename, String outputFilename){
            new File(outputFilename).delete();
            double duration = findDuration(inputAudioFilename);
            double scrollspeed = imageHeight < 1000 ? 0.0 : (1 / duration);
            String yExpression = "'t*(h-1000)*"+scrollspeed+"'";
            if(scrollspeed == 0) {
                yExpression = "'-"+(500-imageHeight/2)+"'";
            }
            String ffmpegCommand = "ffmpeg -f lavfi -i color=c=white:s=1920x1080:d=" +
                    duration +
                    " -loop 1 -i " +
                    inputImageFilename +
                    " -i " +
                    inputAudioFilename +
                    " -filter_complex \"[1:v]scale=897:" +
                    imageHeight + "[fg]; [0:v][fg]overlay=y=-" +
                    yExpression + ":x=500:shortest=1[v]\" -map \"[v]\" -map 2:a " +
                    outputFilename +
                    " -loglevel error";

            Command.executeCommand(ffmpegCommand, false);

    /*
        String ffmpegCommand = "ffmpeg -i " + inputVideoFilename + " -i " + inputAudioFilename + " -codec copy -shortest " + outputFilename;
        Command.executeCommand(ffmpegCommand, false);
    */
        }

        private double durationToSeconds (String strDuration){
            int hours = Integer.parseInt(strDuration.substring(0, 2));
            int minutes = Integer.parseInt(strDuration.substring(3, 5));
            double seconds = Double.parseDouble(strDuration.substring(6, 11));
            return hours * 60 * 60 + minutes * 60 + seconds;
        }
    }
