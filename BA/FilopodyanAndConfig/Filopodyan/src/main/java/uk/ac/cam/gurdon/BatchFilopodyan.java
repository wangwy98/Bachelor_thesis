package uk.ac.cam.gurdon;


import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import uk.ac.cam.gurdon.ext.FilopodyanUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@inheritDoc}
 * Extends <code>FilopodyanGui</code> to provide a GUI for running Filopodyan on a folder of images with pre-defined parameters.
 *
 * @author Richard Butler
 */
public class BatchFilopodyan extends FilopodyanGui implements ActionListener {
    private static final long serialVersionUID = 123412341234l;
    private JTextField startField, framesField, lengthField, dlField, dctmField, dcbmField, wavinessField;
    public int frames = (int) Math.round(Prefs.get("Filopodyan.frames", 5));
    public double length = Prefs.get("Filopodyan.length", 0.5d);
    public double dl = Prefs.get("Filopodyan.dl", 2d);
    public double dctm = Prefs.get("Filopodyan.dctm", 0d);
    public double dcbm = Prefs.get("Filopodyan.dcbm", 0d);
    public double waviness = Prefs.get("Filopodyan.waviness", 1d);
    public int start = (int) Math.round(Prefs.get("Filopodyan.start", 1));
    private String fileRegex = ".+\\.tiff?";
    public String path = Prefs.get("Filopodyan.path", System.getProperty("user.home"));
    public AtomicInteger count;
    private Timer timer;

    /**
     * Constructor, loads previous parameters or sets defaults.
     */
    public BatchFilopodyan() {
        try {
            this.boundaryAnalysis = Prefs.get("Filopodyan.boundaryAnalysis", false);
            this.tipPlot = Prefs.get("Filopodyan.tipPlot", false);
            this.filoTable = Prefs.get("Filopodyan.filoTable", false);
            this.coordTable = Prefs.get("Filopodyan.coordTable", false);
            this.bodyTable = Prefs.get("Filopodyan.bodyTable", false);
            this.kymographs = Prefs.get("Filopodyan.kymographs", false);
            this.ccf = Prefs.get("Filopodyan.ccf", false);
            this.basePlot = Prefs.get("Filopodyan.basePlot", false);
            this.time = Prefs.get("Filopodyan.time", false);
            this.adaptive = Prefs.get("Filopodyan.adaptive", false);
            this.fit = Prefs.get("Filopodyan.fit", false);
            this.join = Prefs.get("Filopodyan.join", false);
            this.eds = (int) Prefs.get("Filopodyan.eds", 8);
            this.backFrames = (int) Prefs.get("Filopodyan.backFrames", 3);
            this.mapC = (int) Math.round(Prefs.get("Filopodyan.mapC", 1));
            this.measureC = (int) Math.round(Prefs.get("Filopodyan.measureC", 2));
            this.sigma = Prefs.get("Filopodyan.sigma", 4d);
            this.threshold = Prefs.get("Filopodyan.threshold", "Triangle");
            this.C = 2;

        } catch (Exception e) {
            IJ.log(e.toString() + "\n~~~~~\n" + Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    /**
     * Creates the batch mode dialog, calls <code>FilopodyanGui.makeFrame()</code> and adds additional fields to take filter parameters.
     *
     * @see FilopodyanGui
     */
    public void createDialog() {
        try {
            makeFrame();

            JPanel filterPanel = new JPanel(new GridLayout(0, 2, 2, 2));
            filterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Batch Filter"));
            filterPanel.add(new JLabel("Min start frame", JLabel.RIGHT));
            startField = new JTextField("" + start, 3);
            filterPanel.add(startField);
            filterPanel.add(new JLabel("Min frames", JLabel.RIGHT));
            framesField = new JTextField("" + frames, 3);
            filterPanel.add(framesField);
            filterPanel.add(new JLabel("Min max length (\u00b5m)", JLabel.RIGHT));
            lengthField = new JTextField("" + length, 3);
            filterPanel.add(lengthField);
            filterPanel.add(new JLabel("Min length change (\u00b5m)", JLabel.RIGHT));
            dlField = new JTextField("" + dl, 3);
            filterPanel.add(dlField);
            filterPanel.add(new JLabel("Min max DCTM (\u00b5m)", JLabel.RIGHT));
            dctmField = new JTextField("" + dctm, 3);
            filterPanel.add(dctmField);
            filterPanel.add(new JLabel("Min max DCBM (\u00b5m)", JLabel.RIGHT));
            dcbmField = new JTextField("" + dcbm, 3);
            filterPanel.add(dcbmField);
            filterPanel.add(new JLabel("Max mean waviness", JLabel.RIGHT));
            wavinessField = new JTextField("" + waviness, 3);
            filterPanel.add(wavinessField);
            add(filterPanel);

            JPanel buttonPan = new JPanel();
            JButton batchButton = new JButton("Run");
            batchButton.addActionListener(this);
            buttonPan.add(batchButton);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            buttonPan.add(cancelButton);
            add(buttonPan);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);

            if (this.log == null) {
                this.log = FilopodyanLog.get();
            }

        } catch (Exception e) {
            IJ.log(e.toString() + "\n~~~~~\n" + Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    @Override
    public void logSettings() {
        try {
            super.logSettings();
            String filterStr = "Filopodyan Batch Mode: " + path + "\n" +
                    "Filter settings-\n" +
                    "Min start frame: " + start + "\n" +
                    "Min frames: " + frames + "\n" +
                    "Min max length: " + length + "\n" +
                    "Min length change: " + dl + "\n" +
                    "Min max DCTM: " + dctm + "\n" +
                    "Min max DCBM: " + dcbm + "\n" +
                    "Max mean waviness: " + waviness + "\n";
            log.print(filterStr);
        } catch (Exception e) {
            IJ.log(e.toString() + "\n~~~~~\n" + Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            String event = ae.getActionCommand();
            if (event.equals("Run")) {
                if (verbose) {
                    log.print("Filopodyan batch mode running " + path);
                }
                try {
                    start = Integer.parseInt(startField.getText());
                } catch (NumberFormatException nfe) {
                    IJ.error(startField.getText() + " is not an integer.");
                    return;
                }
                try {
                    frames = Integer.parseInt(framesField.getText());
                } catch (NumberFormatException nfe) {
                    IJ.error(framesField.getText() + " is not an integer.");
                    return;
                }
                try {
                    length = Double.valueOf(lengthField.getText());
                } catch (NumberFormatException nfe) {
                    IJ.error(lengthField.getText() + " is not a number.");
                    return;
                }
                try {
                    dl = Double.valueOf(dlField.getText());
                } catch (NumberFormatException nfe) {
                    IJ.error(dlField.getText() + " is not a number.");
                    return;
                }
                try {
                    dctm = Double.valueOf(dctmField.getText());
                } catch (NumberFormatException nfe) {
                    IJ.error(dctmField.getText() + " is not a number.");
                    return;
                }
                try {
                    dcbm = Double.valueOf(dcbmField.getText());
                } catch (NumberFormatException nfe) {
                    IJ.error(dcbmField.getText() + " is not a number.");
                    return;
                }
                try {
                    waviness = Double.valueOf(wavinessField.getText());
                } catch (NumberFormatException nfe) {
                    IJ.error(wavinessField.getText() + " is not a number.");
                    return;
                }
                if (super.setFields() == false) {
                    return;
                }

                JFileChooser fc = new JFileChooser(path);
                fc.setDialogTitle("Directory...");
                fc.setApproveButtonText("Select");
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setSelectedFile(new File(path));
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    path = fc.getSelectedFile().getAbsolutePath();
                    Prefs.set("Bounder.path", path);
                } else {
                    return;
                }
                logSettings();
                setVisible(false);

                File inputFile = new File(path);
                boolean got = false;
                count = new AtomicInteger(0);
                if (inputFile.isDirectory()) {
                    File[] files = inputFile.listFiles();

                    //?????????????????????
//                    int nThreads = Runtime.getRuntime().availableProcessors();
//                    ExecutorService exec = Executors.newFixedThreadPool(nThreads);
                    for (int f = 0; f < files.length; f++) {
                        if (files[f].getName().matches(fileRegex)) {
                            final File expDir = files[f];

                            //????????????????????????????????????
                            BatchFilopodyan filopodyan = new FilopodyanUtil().configBatchFilopodyan(expDir.getName());
                            filopodyan.count = count;
                            filopodyan.path = path;
                            filopodyan.advMode = true;
                            filopodyan.log = FilopodyanLog.get();
                            log.print(expDir.getName() + ":\t" + filopodyan.allParam());
                            uk.ac.cam.gurdon.ext.Batch job = new uk.ac.cam.gurdon.ext.Batch(expDir, filopodyan);
                            job.run();
//                            exec.submit(job);
                            got = true;
                            count.getAndIncrement();
                            showWorkFrame();
                        }
                    }
//                    exec.shutdown();
                    timer = new Timer();
                    TimerTask task = new TimerTask() {    //this is a stupid way to synchronise threads - use ij.util.ThreadUtil?
                        public void run() {
                            if (count.get() == 0) {
                                log.print("Filopodyan batch mode finished " + path);
                                timer.cancel();
                                timer.purge();
                                setPrefs();
                                Prefs.set("Filopodyan.start", start);
                                Prefs.set("Filopodyan.frames", frames);
                                Prefs.set("Filopodyan.length", length);
                                Prefs.set("Filopodyan.dl", dl);
                                Prefs.set("Filopodyan.dctm", dctm);
                                Prefs.set("Filopodyan.dcbm", dcbm);
                                Prefs.set("Filopodyan.waviness", waviness);
                                workFrame.dispose();
                            }
                        }
                    };
                    timer.scheduleAtFixedRate(task, 1000, 1000);

                }
                if (!got) {
                    IJ.error("No images found", path + " does not contain any files matching " + fileRegex);
                }
            } else if (event.equals("Cancel")) {
                if (verbose) {
                    IJ.log("Filopodyan cancelled");
                }
                dispose();
            } else if (event == "Log Settings") {
                logSettings();
            } else if (event.equals("advanced")) {
                advMode = advTick.isSelected();
                Prefs.set("Filopodyan.advMode", advMode);
                setAdvancedMode();
            } else if (event.equals("help")) {
                showHelp();
            }
        } catch (Exception e) {
            IJ.log(e.toString() + "\n~~~~~\n" + Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    /**
     * A <code>Runnable</code> to run Filopodyan on an image file. Submitted to an <code>ExecutorService</code> to analyse multiple images in parallel.
     */
    public class Batch implements Runnable {
        private File file;
        private BatchFilopodyan bb;

        public Batch(File file, BatchFilopodyan bb) {
            try {
                this.file = file;
                this.bb = bb;
            } catch (Exception e) {
                IJ.log(e.toString() + "\n~~~~~\n" + Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }
        }

        public void run() {
            try {
                ImagePlus image = new ImagePlus(file.getAbsolutePath());
                Filopodyan_ bounder = new Filopodyan_();
                bounder.setImp(image);
                bounder.batch = true;
                bounder.bgui = bb;
                FilopodyanProcessor fp = null;
                if (adaptive) {
                    fp = new ALTProcessor();
                } else {
                    fp = new LoGProcessor();
                }
                bounder.filopodia(false, fp);
                image.close();
            } catch (Exception e) {
                IJ.log(e.toString() + "\n~~~~~\n" + Arrays.toString(e.getStackTrace()).replace(",", "\n"));
            }
        }
    }

    @Override
    public String toString() {
        return "BatchFilopodyan{" +
                "mapC=" + mapC +
                ", measureC=" + measureC +
                ", threshold='" + threshold + '\'' +
                ", adaptive=" + adaptive +
                ", fit=" + fit +
                ", join=" + join +
                ", eds=" + eds +
                ", backFrames=" + backFrames +
                ", coordTable=" + coordTable +
                ", sigma=" + sigma +
                ", filoTable=" + filoTable +
                ", bodyTable=" + bodyTable +
                ", processProfile=" + processProfile +
                ", boundaryAnalysis=" + boundaryAnalysis +
                ", kymographs=" + kymographs +
                ", ccf=" + ccf +
                ", time=" + time +
                ", verbose=" + verbose +
                ", showBackground=" + showBackground +
                ", start=" + start +
                ", frames=" + frames +
                ", length=" + length +
                ", dl=" + dl +
                ", dctm=" + dctm +
                ", dcbm=" + dcbm +
                ", waviness=" + waviness +
                '}';
    }

    public String allParam() {
        return "BatchFilopodyan{frames=" + frames +
                ", length=" + length +
                ", dl=" + dl +
                ", dctm=" + dctm +
                ", dcbm=" + dcbm +
                ", waviness=" + waviness +
                ", start=" + start +
                ", fileRegex='" + fileRegex + '\'' +
                ", path='" + path + '\'' +
                ", count=" + count +
                ", parent=" + parent +
                ", oked=" + oked +
                ", preview=" + preview +
                ", boundaryAnalysis=" + boundaryAnalysis +
                ", tipPlot=" + tipPlot +
                ", filoTable=" + filoTable +
                ", coordTable=" + coordTable +
                ", bodyTable=" + bodyTable +
                ", kymographs=" + kymographs +
                ", ccf=" + ccf +
                ", basePlot=" + basePlot +
                ", time=" + time +
                ", adaptive=" + adaptive +
                ", fit=" + fit +
                ", verbose=" + verbose +
                ", join=" + join +
                ", showBackground=" + showBackground +
                ", processProfile=" + processProfile +
                ", advMode=" + advMode +
                ", eds=" + eds +
                ", backFrames=" + backFrames +
                ", mapC=" + mapC +
                ", measureC=" + measureC +
                ", sigma=" + sigma +
                ", weight=" + weight +
                ", threshold='" + threshold + '\'' +
                ", C=" + C +
                ", rootPaneCheckingEnabled=" + rootPaneCheckingEnabled +
                '}';
    }
}