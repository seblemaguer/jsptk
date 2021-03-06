package jsptk;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.Resources;
import com.google.common.io.ByteStreams;


// Audio
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


/**
 *  Provider utilities
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKProvider
{
    /******************************************************************
     ** Some parameters used for the extraction using SPTK
     ******************************************************************/
    public static final int SAMPLING_RATE = 48000;
    public static final int FFT_LEN = 2048;
    public static final int FRAME_SHIFT = 240;
    public static final int FRAME_LENGTH = 1200;
    public static final Window WIN_TYPE = Window.swigToEnum(1);
    public static final int MGC_ORDER = 34;
    public static final int NB_FRAMES = 432;
    public static final double ALPHA = 0.55;
    public static final double GAMMA = 0;
    public static final int MGC2SP_OTYPE = 2;
    public static final double PER_ERR = 1.0E-08;
    public static final double LOWER_F0 = 50;
    public static final double UPPER_F0 = 250;

    /******************************************************************
     ** Providers
     ******************************************************************/
    public static double[] providerB() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.b");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
        double[] b = new double[lines.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = Double.parseDouble(lines.get(i));
        }

        return b;
    }

    public static double[] providerMGC() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mgc");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
        double[] mgc = new double[lines.size()];
        for (int i = 0; i < mgc.length; i++) {
            mgc[i] = Double.parseDouble(lines.get(i));
        }

        return mgc;
    }


    // FIXME: hardcoded nb of frames
    public static double[][] providerMGCFull() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mgc_full");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
        int dim = lines.size()/341;

        double[][] mgc = new double[341][dim];
        for (int i = 0; i<mgc.length; i++) {
            for (int j=0; j<dim; j++)
                mgc[i][j] = Double.parseDouble(lines.get(i*dim+j));
        }

        return mgc;
    }

    public static double[] providerFREQT() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.freqt");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] freqt = new double[lines.size()];
        for (int i = 0; i < freqt.length; i++) {
            freqt[i] = Double.parseDouble(lines.get(i));
        }

        return freqt;
    }

    public static double[] providerSP() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.sp");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] sp = new double[lines.size()];
        for (int i = 0; i < sp.length; i++) {
            sp[i] = Double.parseDouble(lines.get(i));
        }

        return sp;
    }

    public static double[] providerMCEP() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mcep");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] mcep = new double[lines.size()];
        for (int i = 0; i < mcep.length; i++) {
            mcep[i] = Double.parseDouble(lines.get(i));
        }

        return mcep;
    }

    public static double[] providerPostFilteredMGC() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.postfilt");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] sp = new double[lines.size()];
        for (int i = 0; i < sp.length; i++) {
            sp[i] = Double.parseDouble(lines.get(i));
        }

        return sp;
    }

    public static double[] providerMC2SP() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mc2sp");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] sp = new double[lines.size()];
        for (int i = 0; i < sp.length; i++) {
            sp[i] = Double.parseDouble(lines.get(i));
        }

        return sp;
    }

    public static double[] providerRAWSignal() throws Exception {

        byte[] b_arr = ByteStreams.toByteArray(JSPTKWrapperTest.class.getResourceAsStream("/cmu_us_arctic_slt_b0535.raw"));
        ByteBuffer buf = ByteBuffer.wrap(b_arr);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        double[] raw = new double[103680]; // NOTE: hardcoded got from x2x +sa cmu_us_arctic_slt_b0535.raw | wc -l
        for (int i = 0; i < raw.length; i++) {
            raw[i] = (double) buf.getShort();
        }

        return raw;
    }



    public static double[][] providerFramedSignal() throws Exception {

        byte[] b_arr = ByteStreams.toByteArray(JSPTKWrapperTest.class.getResourceAsStream("/cmu_us_arctic_slt_b0535.framed"));
        ByteBuffer buf = ByteBuffer.wrap(b_arr);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        double[][] framed = new double[NB_FRAMES][1200]; // NOTE: 1200 = frame length used, frameshift = 240 => NB_FRAMES frames
        for (int t = 0; t<framed.length; t++) {
            for (int d=0; d<framed[0].length; d++)
                framed[t][d] = (double) buf.getFloat();
        }

        return framed;
    }


    public static double[][] providerWindowedSignal() throws Exception {

        byte[] b_arr = ByteStreams.toByteArray(JSPTKWrapperTest.class.getResourceAsStream("/cmu_us_arctic_slt_b0535.windowed"));
        ByteBuffer buf = ByteBuffer.wrap(b_arr);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        double[][] windowed = new double[NB_FRAMES][JSPTKProvider.FFT_LEN]; // FIXME: 2018 = FFT length (in this case frame length)
        for (int t = 0; t<windowed.length; t++) {
            for (int d=0; d<windowed[0].length; d++)
                windowed[t][d] = (double) buf.getFloat();
        }

        return windowed;
    }



    public static double[][] providerMGC2SP() throws Exception {

        byte[] b_arr = ByteStreams.toByteArray(JSPTKWrapperTest.class.getResourceAsStream("/cmu_us_arctic_slt_b0535.mgc2sp"));
        ByteBuffer buf = ByteBuffer.wrap(b_arr);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        double[][] sp = new double[NB_FRAMES][FFT_LEN/2+1];
        for (int t = 0; t<sp.length; t++) {
            for (int d=0; d<sp[0].length; d++)
                sp[t][d] = (double) buf.getFloat();
        }

        return sp;
    }


    public static double[][] providerMGCFromWindowedSignal() throws Exception {

        byte[] b_arr = ByteStreams.toByteArray(JSPTKWrapperTest.class.getResourceAsStream("/cmu_us_arctic_slt_b0535.mgc"));
        ByteBuffer buf = ByteBuffer.wrap(b_arr);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        double[][] mgc = new double[NB_FRAMES][MGC_ORDER+1];
        for (int t = 0; t<mgc.length; t++) {
            for (int d=0; d<mgc[0].length; d++)
                mgc[t][d] = (double) buf.getFloat();
        }

        return mgc;
    }

    public static double[] providerLF0RAPT() throws Exception {

        byte[] b_arr = ByteStreams.toByteArray(JSPTKWrapperTest.class.getResourceAsStream("/cmu_us_arctic_slt_b0535.lf0_rapt"));
        ByteBuffer buf = ByteBuffer.wrap(b_arr);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        double[] lf0 = new double[NB_FRAMES];
        for (int t = 0; t<lf0.length; t++) {
                lf0[t] = (double) buf.getFloat();
        }

        return lf0;
    }

    public static AudioInputStream providerAIS() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/cmu_us_arctic_slt_b0535.wav");
        return AudioSystem.getAudioInputStream(url);
    }
}
