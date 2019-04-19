package se.fredriksonsound.videomaker;

import se.fredriksonsound.videomaker.media.FFMPEGMediaEditor;
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

    FFMPEGMediaEditor editor = new FFMPEGMediaEditor();
    double clipLength = editor.findLength("content2.mpga");
    editor.encodeVideoFromImage("image.png", clipLength,"output.mp4");
    editor.mux("output.mp4", "content2.mpga", "muxed.mp4");
    }

    private static void writeUsingOutputStream(String data) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File("content.mp3"));
            os.write(data.getBytes(), 0, data.length());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
