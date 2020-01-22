package se.fredriksonsound.videomaker.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Command {

    public static String executeCommand(String command, boolean silent) {
        if(!silent)
            System.out.println("Executing command: " + command);
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader reader;

            flushInputStreamReader(p);
            int exitCode = p.waitFor();
            if(exitCode == 0) {
                reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            }
            else
            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line;
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
            p.destroy();
            return totalOutput;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void flushInputStreamReader (Process process) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder s = new StringBuilder();
        while((line=input.readLine()) != null) {
            s.append(line);
        }
    }
}
