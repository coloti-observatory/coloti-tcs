package coloti.tcs.trajectory;

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
import Target; //astri.astron.Target;
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

    public static void main(String[] args) {
        // Observer
        Observer obs = new Observer("COLOTI", 1,
                43.4016667,
                12.3763888,
                487);
        // tpoint File - set the full path
        String BASE_DIR = "/home/coloti";  //"/Users/gino/scada/six-telescope-aiv/generator";
        String tpointFile = BASE_DIR + "/config/tpoint/colotiTraj.json"; //"/config/tpoint/astri1-tp.json";

        // Weather
        //double press = 770.;
        //double temp = 15.0;
        //double hum = 0.5;
        //Weather atm = new Weather(press, temp, hum);

        // target
        Target target = new Target("Crab");
        target.setUseRefraction(true);
        target.setUsePointingModel(true);

        TrajectoryManager tm = new TrajectoryManager();
        tm.setBaseDir(BASE_DIR);
        tm.assignToTelescope(ETelescopes.ASTRI1);
        tm.setAstroObserver(obs);
        tm.setWeather(atm);
        tm.setTpointFile(tpointFile);
        tm.setElevationLimit(10.);
        tm.setMinMoonDistance(10.);
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

        for (int i = 0; i < tra.length; i += 3) {
            double y = tf.Az(tra[i]);
            double vy = tf.velocityAz(tra[i]);
            double yEl = tf.El(tra[i]);
            double vel = tf.velocityEl(tra[i]);

            System.out.println("AZ:"+tra[i] + " " + (y) + " " + tra[i + 1] + " " + (tra[i + 1] - y) * 3600 + " " + vy );
            System.out.println("El:"+
                    tra[i] + " " + (yEl) + " " + tra[i + 2] + " " + (tra[i + 2] - yEl) * 3600 + " " + vel);
        }
    }

}
