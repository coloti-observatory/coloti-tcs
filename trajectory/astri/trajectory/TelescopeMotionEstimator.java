package astri.trajectory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelescopeMotionEstimator {

    private static final Logger logger = LoggerFactory.getLogger(TelescopeMotionEstimator.class);

    public TelescopeMotionEstimator() {

    }

    public void checkTrajectory(double[] currentTrajectory, double currentSkyAz, double currentTelAz) {

        for (int i = 0; i < currentTrajectory.length; i += 3) {
            currentTelAz = azTelPosEstimator(currentTrajectory[i + 1], currentSkyAz, currentTelAz);
            currentSkyAz = currentTrajectory[i + 1];
        }

    }

    public double azTelPosEstimator(double commandedAz, double currentSkyAz, double currentTelAz) {
        final double xi = currentSkyAz;
        final double oldAzVal = currentTelAz;
        final double az = commandedAz;
        int oldpath = 0;
        int path = 0;
        if (oldAzVal > 0) {
            oldpath = 1; // CCW=-1 CW=1
        } else {
            oldpath = -1; // CCW=-1 CW=1
        }

        final double xf = az;
        final double i = xi / 90.; // initial quadrant 0= [0,90); 1=[90,180); 2=[180,270); 3=[270,360)
        final double j = xf / 90.;
        int ip = (int) i;
        int jp = (int) j;
        if (ip == 4) {
            ip = 0;
        }
        if (jp == 4) {
            jp = 0;
        }
        double val = 0;

        if (ip < 4 && jp == 1) {
            path = 1;
            val = xf - 90.;
        } else if ((ip == 1 && jp == 0) || ((ip == 2 || ip == 3) && jp == 0 && oldpath == -1)) {
            path = -1;
            val = xf - 90.;
        } else if ((ip == 2 || ip == 3 || (ip == 0)) && (jp == 3 || jp == 2) && oldpath == -1) {
            val = xf - 450.;
            path = -1;
        } else if ((ip == 2 || ip == 3) && (jp == 3 || jp == 2) && oldpath == 1) {
            val = xf - 90;
            path = 1;
        } else if ((ip == 2 || ip == 3) && jp == 0 && oldpath == 1) {
            val = xf - 90.;
            path = -1;
        } else {
            val = xf - 90.;
            path = 1;
        }

        if (path == -1) {
            if (val < -261.0 && val > -270.) {
                logger.warn(
                        "Desidered Pos:" + val + " --> WARNING: the CCW pre-limit will be reached, going in CW");
                val = val + 360.;
                path = 1;
            }
        }
        if (path == 1) {
            if (val > 261.0 && val < 270.) {
                logger.warn(
                        "Desidered Pos:" + val + " --> WARNING: the CW pre-limit will be reached, going in CCW");
                val = val - 360.;
                path = -1;
            }
        }

        logger.info(
                " xi:" + xi + " xf:" + xf + " i:" + ip + " j:" + jp + "  " + " val:" + val + " path:" + path);
        return val;

    }

    public static void main(String[] args) {

        TelescopeMotionEstimator tme = new TelescopeMotionEstimator();

        tme.azTelPosEstimator(165., 359.88, -90.12);
        tme.azTelPosEstimator(265., 165., 75.);
        tme.azTelPosEstimator(320., 265., 175.);
        tme.azTelPosEstimator(20., 320, 230.);
        tme.azTelPosEstimator(320., 20., -70);
    }

}
