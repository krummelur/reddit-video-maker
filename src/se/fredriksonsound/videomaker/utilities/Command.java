package se.fredriksonsound.videomaker.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Command {

    public static String executeCommand(String command, boolean silent) {
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader reader = null;

            int exitCode = p.waitFor();
            if(exitCode == 0)
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            else
            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = "";
            String totalOutput = "";
            while ((line = reader.readLine()) != null) {
                totalOutput += line;
            }
            if(!silent) {
                if(exitCode == 0)
                System.out.println(totalOutput);
                else
                System.err.println(totalOutput);

            }
            return totalOutput;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
