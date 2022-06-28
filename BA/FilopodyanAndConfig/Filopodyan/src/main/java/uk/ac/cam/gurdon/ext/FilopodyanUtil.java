package uk.ac.cam.gurdon.ext;

import uk.ac.cam.gurdon.BatchFilopodyan;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weiying
 * @date 2022/1/15 11:40
 */
public class FilopodyanUtil {


    private String OS = System.getProperty("os.name").toLowerCase();

    private Map<String, BatchFilopodyan> batchFilopodyans = new HashMap<>();

    public FilopodyanUtil() {
        init(" /Users/wangweiying/Desktop/filopodyan-config.csv");
    }

    public FilopodyanUtil(String configPath) {
        init(configPath);
    }

    private void init(String configPath) {
        try {
            // read the csv file (读入csv文件信息)
            CsvReader csv = new CsvReader(new File(configPath));
            csv.readHeader();
            while (csv.read()) {
                String file = csv.get("File");
                BatchFilopodyan bb = readCSVToFilopodyan(csv);
                batchFilopodyans.put(file, bb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read csv and go to BatchFilopodyan
     * 读取csv单行信息进入BatchFilopodyan
     *
     * @param csv
     */
    private BatchFilopodyan readCSVToFilopodyan(CsvReader csv) throws IOException {
        BatchFilopodyan bb = new BatchFilopodyan();
        configDefault(bb);
        bb.mapC = getInt(csv, "Map C");
        bb.measureC = getInt(csv, "Measure C");
        bb.threshold = csv.get("Threshold");
        bb.adaptive = getBoolean(csv, "Adaptive Thresholding");
        bb.fit = getBoolean(csv, "Fit tip to measure C");
        bb.join = getBoolean(csv, "Join fragments");
        bb.eds = getInt(csv, "ED Iterations");
        bb.backFrames = getInt(csv, "Base Back Frames");
        bb.sigma = getDouble(csv, "Log sigma");
        bb.filoTable = getBoolean(csv, "Filopodia Table");
        bb.coordTable = getBoolean(csv, "Coordinate Table");
        bb.bodyTable = getBoolean(csv, "Body Table");
        bb.processProfile = getBoolean(csv, "Process Profile Graphs");
        bb.boundaryAnalysis = getBoolean(csv, "Boundary Visualisation");
        bb.kymographs = getBoolean(csv, "Kymographs");
        bb.ccf = getBoolean(csv, "CCF");
        bb.time = getBoolean(csv, "Show Time");
        bb.verbose = getBoolean(csv, "Verbose Mode");
        bb.showBackground = getBoolean(csv, "Show Background Areas");
        bb.start = getInt(csv, "Min start frame");
        bb.frames = getInt(csv, "Min frames");
        bb.length = getDouble(csv, "Min max length(um)");
        bb.dl = getDouble(csv, "Min length change(um)");
        bb.dctm = getDouble(csv, "Min max DCTM(um)");
        bb.dcbm = getDouble(csv, "Min max DCBM(um)");
        bb.waviness = getDouble(csv, "Max mean waviness");

        return bb;
    }

    /**
     * inject the information in the configuration file by filename
     * 将配置文件中的信息按文件名注入
     *
     * @param fileName name of file (文件名)
     * @param bb       (需要注入的BatchFilopodyan)
     * @return
     */
    public BatchFilopodyan configBatchFilopodyan(String fileName, BatchFilopodyan bb) {
        BatchFilopodyan defaultBb = batchFilopodyans.get(fileName);
        bb.mapC = defaultBb.mapC;
        bb.measureC = defaultBb.measureC;
        bb.threshold = defaultBb.threshold;
        bb.adaptive = defaultBb.adaptive;
        bb.fit = defaultBb.fit;
        bb.join = defaultBb.join;
        bb.eds = defaultBb.eds;
        bb.backFrames = defaultBb.backFrames;
        bb.sigma = defaultBb.sigma;
        bb.filoTable = defaultBb.filoTable;
        bb.coordTable = defaultBb.coordTable;
        bb.bodyTable = defaultBb.bodyTable;
        bb.processProfile = defaultBb.processProfile;
        bb.boundaryAnalysis = defaultBb.boundaryAnalysis;
        bb.kymographs = defaultBb.kymographs;
        bb.ccf = defaultBb.ccf;
        bb.time = defaultBb.time;
        bb.verbose = defaultBb.verbose;
        bb.showBackground = defaultBb.showBackground;
        bb.start = defaultBb.start;
        bb.frames = defaultBb.frames;
        bb.length = defaultBb.length;
        bb.dl = defaultBb.dl;
        bb.dctm = defaultBb.dctm;
        bb.dcbm = defaultBb.dcbm;
        bb.waviness = defaultBb.waviness;
        return bb;
    }

    /**
     * 将配置文件中的信息按文件名注入
     *
     * @param fileName 文件名
     * @return
     */
    public BatchFilopodyan configBatchFilopodyan(String fileName) {
        BatchFilopodyan defaultBb = batchFilopodyans.get(fileName);
        return defaultBb;
    }


    public boolean isLinux() {
        return OS.contains("linux");
    }

    public boolean isMac() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && !OS.contains("x");
    }

    public boolean isWindows() {
        return OS.contains("windows");
    }

    /**
     * 默认初始化
     *
     * @param bb BatchFilopodyan
     */
    public void configDefault(BatchFilopodyan bb) {
//        frames = 1, length = 0.1, dl = 0.0, dctm = 0.0, dcbm = 0.0, waviness = 1.0, start = 1, fileRegex = '.+\.tiff?', path = 'C:\Users\Jangbao\Desktop\test\test_png_1', count = 0, parent = null, oked = false, preview = false, boundaryAnalysis = false, tipPlot = false, filoTable = true, coordTable = true, bodyTable = true, kymographs = false, ccf = false, basePlot = false, time = false, adaptive = false, fit = false, verbose = false, join = true, showBackground = false, processProfile = false, advMode = true, eds = 6, backFrames = 0, mapC = 1, measureC = 1, sigma = 2.6, weight = 0.8, threshold = 'RenyiEntropy', C = 2, rootPaneCheckingEnabled = true
    }

    public int getInt(CsvReader csvReader, String title) throws IOException {
        return Integer.parseInt(csvReader.get(title));
    }

    public boolean getBoolean(CsvReader csvReader, String title) throws IOException {
        return Boolean.parseBoolean(csvReader.get(title));
    }

    public double getDouble(CsvReader csvReader, String title) throws IOException {
        return Double.parseDouble(csvReader.get(title));
    }


}
