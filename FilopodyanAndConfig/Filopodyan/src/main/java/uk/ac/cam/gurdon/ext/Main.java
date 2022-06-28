package uk.ac.cam.gurdon.ext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weiying
 * @date 2022/2/20 18:48
 */
public class Main {

    public static void main(String[] args) {
        List<CommandParser> commandParsers = new ArrayList<>();
        commandParsers.add(new DCommandParser());
        commandParsers.add(new SCommandParser());
        for (CommandParser commandParser : commandParsers) {
            if (commandParser.handle(args)) {
                commandParser.run();
                return;
            }
        }
    }

}
