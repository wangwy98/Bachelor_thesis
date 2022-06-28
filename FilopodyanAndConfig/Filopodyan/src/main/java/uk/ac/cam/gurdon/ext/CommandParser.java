package uk.ac.cam.gurdon.ext;

/**
 * @author weiying
 * @date 2022/2/23 13:36
 */
public abstract class CommandParser {

    public static boolean SHOW = false;

    String configPath;

    String filePath;

    String[] args;

    abstract boolean handle(String[] args);

    abstract void run();

    public void handleArgs() {
        for (String arg : args) {
            if (arg.contains("-f")) {
                filePath = arg.replaceAll("-f:", "");
            }
            if (arg.contains("-c")) {
                configPath = arg.replaceAll("-c:", "");
            }
        }
    }
}
