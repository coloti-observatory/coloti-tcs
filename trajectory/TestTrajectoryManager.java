package coloti.tcs.trajectory;

import org.jastronomy.jsofa.JSOFA.JulianDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import astri.astron.Observer;
import astri.astron.Target;
import astri.astron.TimeUtil;
import astri.astron.Weather;

public class TestTrajectoryManager {

    private static final Logger logger = LoggerFactory.getLogger(TestTrajectoryManager.class);

    public static void main(String[] args) {
        // Observer
        Observer obs = new Observer("ASTRI1", 1,
                28.301025,
                -16.50796944,
                2359);
        // tpoint File - set the full path
        String BASE_DIR = "/Users/gino/scada/six-telescope-aiv/generator";
        String tpointFile = BASE_DIR + "/config/tpoint/astri1-tp.json";
        // Weather
        double press = 770.;
        double temp = 15.0;
        double hum = 0.5;
        Weather atm = new Weather(press, temp, hum);
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
        if (!tm.isDay() && tm.isTargetValid()) {
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
        // TelescopeMotionEstimator tme = new TelescopeMotionEstimator();
        // tme.checkTrajectory(tra,359.88, -90.12);
        // HorizonCoordinate
        // moon=tm.getMoonHorizonCoordinate(TimeUtil.getCurrentJuliandDay());
        // tm.logger.info("MOON (az,el):{},{}", moon.az,moon.el);

        // HorizonCoordinate
        // sun=tm.getSunHorizonCoordinate(TimeUtil.getCurrentJuliandDay());
        // tm.logger.info("SUN (az,el):{},{}", sun.az,sun.el);

    }
}
