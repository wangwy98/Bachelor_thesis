package uk.ac.cam.gurdon.ext;

import java.util.concurrent.TimeUnit;

/**
 * @author weiying
 * @date 2022/2/23 13:36
 */
public class DCommandParser extends CommandParser {

    @Override
    public boolean handle(String[] args) {
        for (String arg : args) {
            if (arg.contains("-d")) {
                this.args = args;
                SHOW = false;
                return true;
            }

        }
        return false;
    }

    @Override
    public void run() {
        handleArgs();
        Performer performer = new Performer(configPath, filePath);
        performer.action();
        System.exit(0);
    }
}
