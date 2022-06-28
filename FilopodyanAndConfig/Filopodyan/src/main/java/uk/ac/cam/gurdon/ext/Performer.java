package uk.ac.cam.gurdon.ext;

import uk.ac.cam.gurdon.BatchFilopodyan;
import uk.ac.cam.gurdon.FilopodyanLog;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weiying
 * @date 2022/2/20 18:49
 */
public class Performer {

    private String configPath;

    private String filePath;

    private AtomicInteger count;

    private String fileRegex = ".+\\.tiff?";

    public Performer(String configPath, String filePath) {
        this.configPath = configPath;
        this.filePath = filePath;
    }

    public void action() {
        File inputFile = new File(filePath);
        count = new AtomicInteger(0);
        if (inputFile.isDirectory()) {
            File[] files = inputFile.listFiles();
            //不用多线程运行
            for (int f = 0; f < files.length; f++) {
                if (files[f].getName().matches(fileRegex)) {
                    final File expDir = files[f];
                    //使用配置文件中的信息运行
                    BatchFilopodyan filopodyan = new FilopodyanUtil(configPath).configBatchFilopodyan(expDir.getName());
                    filopodyan.count = count;
                    filopodyan.path = filePath;
                    filopodyan.advMode = true;
                    filopodyan.log = FilopodyanLog.get();
                    filopodyan.log.print(expDir.getName() + ":\t" + filopodyan.allParam());
                    Batch job = new Batch(expDir, filopodyan);
                    job.run();
                    count.getAndIncrement();
                }
            }
        }
    }
}
