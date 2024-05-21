package coloti.tcs.trajectory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jastronomy.jsofa.JSOFA.JulianDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import astri.astron.Observer;
import astri.astron.Target;
import astri.astron.TimeUtil;
import astri.astron.Weather;

public class TrajectoryFitter {
    private static final Logger logger = LoggerFactory.getLogger(TrajectoryFitter.class);
    private PolynomialCurveFitter fitter;
    private double[] coeffAz;
    private double[] coeffEl;
    private double[] tra;
    private double[] coeffAzd;
    private double[] coeffEld;

    private PolynomialFunction polynomialAz;
    private PolynomialFunction derivateAz;
    private PolynomialFunction polynomialEl;
    private PolynomialFunction derivateEl;
    private int size;
    private final double DAYSEC=86400.;
    private int polDegree;

    public TrajectoryFitter(double[] trajectory) {
        this.tra = trajectory;
        size = trajectory.length;
    }

    private void setPolynomialFitterDegree(int deg) {
        polDegree=deg;
        fitter = PolynomialCurveFitter.create(deg);
    }

    public void fit(int deg) {
        setPolynomialFitterDegree(deg);
        WeightedObservedPoints dataAz = new WeightedObservedPoints();
        WeightedObservedPoints dataEl = new WeightedObservedPoints();
        for (int i = 0; i < size; i += 3) {

            dataAz.add(tra[i] - tra[0], tra[i + 1]);
            dataEl.add(tra[i] - tra[0], tra[i + 2]);
        }
        fitter.withStartPoint(new double[] { 0., tra[1] });
        coeffAz = fitter.fit(dataAz.toList());
        System.out.println(Arrays.toString(coeffAz));

        fitter.withStartPoint(new double[] { 0., tra[2] });
        coeffEl = fitter.fit(dataEl.toList());
        System.out.println(Arrays.toString(coeffEl));

        polynomialAz = new PolynomialFunction(coeffAz);
        derivateAz = polynomialAz.polynomialDerivative();
        coeffAzd = derivateAz.getCoefficients();
        System.out.println(Arrays.toString(coeffAzd));

        polynomialEl = new PolynomialFunction(coeffEl);
        derivateEl = polynomialEl.polynomialDerivative();
        coeffEld = derivateEl.getCoefficients();
        System.out.println(Arrays.toString(coeffEld));
    }

    public double[] getCoeffAz() {
        return coeffAz;
    }

    public double[] getCoeffEl() {
        return coeffEl;
    }

    public double[] getCoeffAzd() {
        return coeffAzd;
    }

    public double[] getCoeffEld() {
        return coeffEld;
    }

    public int getSize() {
        return size;
    }

    public int getPolDegree() {
        return polDegree;
    }

    //deg
    public double Az(double time) {
       
        if (tra[0] <=time && time <= tra[size - 3]) {
            return polynomialAz.value(time - tra[0]);
        }
        return -999;
    }
    //deg
    public double El(double time) {
        if (tra[0] <= time && time <= tra[size - 3]) {
            return polynomialEl.value(time - tra[0]);
        }
        return -999;
    }
    // deg/sec
    public double velocityAz(double time) {
        if (tra[0] <= time && time <= tra[size - 3]) {
            return derivateAz.value(time - tra[0])/DAYSEC;
        }
        return -999;
    }
    // deg/sec
    public double velocityEl(double time) {
        if (tra[0] <= time && time <= tra[size - 3]) {
            return derivateEl.value(time - tra[0])/DAYSEC;
        }
        return -999;
    }

    public static void saveArraysToCSV(double[] array1, double[] array2, double[] array3, double[] array4, double[] array5, double[] array6, double[] array7, String filename) {
        // Ensure all arrays are of the same length
        String path = "/home/coloti/Python/"; 
        int length = array1.length;
        if (array2.length != length || array3.length != length || array4.length != length || array5.length != length || array6.length != length || array7.length != length) {
            throw new IllegalArgumentException("All arrays must have the same length");
        }

        // StringBuilder to construct the CSV content
        StringBuilder csvContent = new StringBuilder();

        // Append header (optional)
        //csvContent.append("Time,Az,TheoryAz, vAz,El, TheroyEl, vEl\n");

        // Append array values row by row
        for (int i = 0; i < length; i++) {
            csvContent.append(array1[i]).append(',')
                      .append(array2[i]).append(',')
                      .append(array3[i]).append(',')
                      .append(array4[i]).append(',')
                      .append(array5[i]).append(',')
                      .append(array6[i]).append(',')
                      .append(array7[i]).append('\n');
        }

        // Write to CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path+filename))) {
            writer.write(csvContent.toString());
            //System.out.println("CSV file created successfully: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Observer
        Observer obs = new Observer("ASTRI", 1,
                //28.301025,
                //-16.50796944,
                //2359);
                43.4016667,
                12.3763888,
                487);
        // tpoint File - set the full path
        String BASE_DIR = "/home/coloti/coloti-tcs/comm/src/main/java/coloti/tcs/trajectory/";  //"/Users/gino/scada/six-telescope-aiv/generator";
        String tpointFile = BASE_DIR + "/config/tpoint/astri1-tp.json";

        // Weather
        double press = 770.;
        double temp = 15.0;
        double hum = 0.5;
        Weather atm = new Weather(press, temp, hum);

        // target
        Target target = new Target("HIP5447");
        target.setUseRefraction(true);
        target.setUsePointingModel(true);

        TrajectoryManager tm = new TrajectoryManager();
        tm.setBaseDir(BASE_DIR);
        tm.assignToTelescope(ETelescopes.ASTRI1);
        tm.setAstroObserver(obs);
        tm.setWeather(atm);
        tm.setTpointFile(tpointFile);
        tm.setElevationLimit(10.); 
        tm.setMinMoonDistance(1.); //10
        tm.setAcquisitionDuration(300.);
        tm.init();
        tm.setTarget(target);

        double[] tra = new double[183];
        if (tm.isDay() && tm.isTargetValid()) {
            JulianDate jd = TimeUtil.getJDNow(); 
            tra = tm.generateTrajectory(jd);
            tm.printTrajectory();
            logger.info("Is Trajectory Valid?:{}", tm.isTrajectoryValid());
        } else {
            logger.info("isNight?:{}", !tm.isDay());
            logger.info("is target Visible (el>0.)?:{}", tm.isVisible());
            logger.info("Is Above min elevation?:{}", tm.isAboveMinElevationOk());
            logger.info("Is Moon Distance Ok?:{}", tm.isMoonSeparationOk());
            logger.info("Target did not pass visibility criteria");
        }

        TrajectoryFitter tf = new TrajectoryFitter(tra);
        tf.fit(5);

        double[] timeJD = new double[tra.length];
        double[] theoryAz = new double[tra.length];
        double[] theoryEl = new double[tra.length];

        double[] y = new double[tra.length];
        double[] vy = new double[tra.length];
        double[] yEl = new double[tra.length];
        double[] vel = new double[tra.length];



        for (int i = 0; i < tra.length; i+=3) { // i+=3

            timeJD[i] = tra[i]; 
            y[i] = tf.Az(tra[i]);
            vy[i] = tf.velocityAz(tra[i]);
            yEl[i] = tf.El(tra[i]);
            vel[i] = tf.velocityEl(tra[i]);

            theoryAz[i] = tra[i + 1];
            theoryEl[i] = tra[i + 2];

            //System.out.println("AZ:"+tra[i] + " " + (y) + " " + tra[i + 1] + " " + (tra[i + 1] - y) * 3600 + " " + vy );
            //System.out.println("El:"+tra[i] + " " + (yEl) + " " + tra[i + 2] + " " + (tra[i + 2] - yEl) * 3600 + " " + vel);
        }

        


        saveArraysToCSV(timeJD, y, theoryAz, vy, yEl, theoryEl ,vel, "TrajectoryData.csv");

        
    }

}
