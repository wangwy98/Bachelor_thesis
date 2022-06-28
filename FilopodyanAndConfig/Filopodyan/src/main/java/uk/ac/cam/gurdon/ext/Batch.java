package uk.ac.cam.gurdon.ext;

import ij.IJ;
import ij.ImagePlus;
import uk.ac.cam.gurdon.ALTProcessor;
import uk.ac.cam.gurdon.BatchFilopodyan;
import uk.ac.cam.gurdon.FilopodyanProcessor;
import uk.ac.cam.gurdon.Filopodyan_;
import uk.ac.cam.gurdon.LoGProcessor;

import java.io.File;
import java.util.Arrays;

/**
 * A <code>Runnable</code> to run Filopodyan on an image file. Submitted to an <code>ExecutorService</code> to analyse multiple images in parallel.
 */
public class Batch implements Runnable {
    private File file;
    private BatchFilopodyan bb;
    private boolean adaptive = false;

    public Batch(File file, BatchFilopodyan bb) {
        try {
            this.file = file;
            this.bb = bb;
        } catch (Exception e) {
            IJ.log(e.toString() + "\n~~~~~\n" + Arrays.toString(e.getStackTrace()).replace(",", "\n"));
        }
    }

    @Override
    public void run() {
        try {
            ImagePlus image = new ImagePlus(file.getAbsolutePath());
            Filopodyan_ bounder = new Filopodyan_();
            bounder.batch = true;
            bounder.setImp(image);
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