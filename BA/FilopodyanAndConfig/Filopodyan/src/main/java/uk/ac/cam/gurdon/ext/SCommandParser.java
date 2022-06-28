package uk.ac.cam.gurdon.ext;

import java.util.Scanner;

/**
 * @author weiying
 * @date 2022/2/23 13:37
 */
public class SCommandParser extends CommandParser {

    @Override
    public boolean handle(String[] args) {
        for (String arg : args) {
            if (arg.contains("-s")) {
                this.args = args;
                SHOW = true;
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Print 1 to quit 输入1退出程序");
        while (true) {
            //输入结束
            int exit = scanner.nextInt();
            if (exit == 1) {
                scanner.close();
                System.exit(0);
            }
        }
    }


}
