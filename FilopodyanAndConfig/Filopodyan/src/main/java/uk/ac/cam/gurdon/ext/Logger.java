package uk.ac.cam.gurdon.ext;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author
 * @date 2022/1/15 11:18
 */
public class Logger {

    private static File file = new File("");

    public static synchronized void log(String text) {
        try {
            FileUtils.writeStringToFile(file, text + "\n", "utf8", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
