package coloti.tcs.trajectory;

/***************************************************************************
* Copyright (C) 2022 INAF
* 
* This program is free software: you can redistribute it and/or modify it 
* under the terms of the GNU Lesser General Public License as published by 
* the Free Software Foundation, either version 3 of the License
* or (at your option) any later version. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
* the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU Lesser General Public License for more details. 
* You should have received a copy of the GNU Lesser General Public License 
* along with this program. If not, see <https://www.gnu.org/licenses/>.
* 
* Authors:
* 
*  Gino Tosti UNIPG/INAF gino.tosti@unipg.it 
*****************************************************************************/

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jastronomy.jsofa.JSOFA;
import org.jastronomy.jsofa.JSOFA.JulianDate;
import org.jastronomy.jsofa.JSOFA.RefCos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import astri.astron.CoordOffset;
import astri.astron.ESolSystem;
import astri.astron.EquatorialObject;
import astri.astron.HorizonDirection;
import astri.astron.Iers;
import astri.astron.JplHorizonReader;
import astri.astron.Observer;
import astri.astron.PointingCoord;
import astri.astron.SolarSystem;
import astri.astron.Target;
import astri.astron.Weather;



public class TrajectoryGenerator implements ITrajectoryGenerator {

    
    private double MINDURATION = 3600.; // seconds
    private double TOTALDURATION = 3600.; // seconds
    private double timeStep = 60.; // seconds
    private int numberOfNodes = (int)(TOTALDURATION/timeStep)+1;
    private static int nextIndex = 0;
    private static List<TrajectoryData> trajectoryNodes;
    private Iers iers;
    private Observer observer;
    private Weather atm;
    private Target target;
    private JulianDate startTime;
    private JulianDate endTime;
    private static double elapsedTime = 0;
    private static double nextTstart = 0;
    private static double initialTstart = 0;
    private static double maxTime = 0;
    private static double expTime = 3600.;
    private double[] trajectoryArray = new double[numberOfNodes * 3];

    private Logger logger = LoggerFactory.getLogger(TrajectoryGeneratorOLD.class);
    private boolean useOffset=false;
    private CoordOffset cordOff = new CoordOffset(0.0, 0.0);
    private ModelTermsManager tPointModelManager;
    private String solSystemDir="./data/solsystem/";
    private double elevationLimit=20.;

    public TrajectoryGenerator() {
    }

    public TrajectoryGenerator(final Target source, final Iers iers, final Observer obs) {
        trajectoryNodes = new LinkedList<>();
        this.iers = iers;
        this.observer = obs;
        this.target = source;
        
    }

    public TrajectoryGenerator(final Iers iers, final Observer obs) {
        trajectoryNodes = new LinkedList<>();
        this.iers = iers;
        this.observer = obs;
        
    }

    

    public void setMINDURATION(double mINDURATION) {
        MINDURATION = mINDURATION;
    }

    public  void setTOTALDURATION(double tOTALDURATION) {
        TOTALDURATION = tOTALDURATION;
    }

    public double getElevationLimit() {
        return elevationLimit;
    }

    public void setElevationLimit(double elevationLimit) {
        this.elevationLimit = elevationLimit;
    }

    public String getSolSystemDir() {
        return solSystemDir;
    }

    public void setSolSystemDir(String solSystemDir) {
        this.solSystemDir = solSystemDir;
    }

    public void settPointModelManager(ModelTermsManager tPointModelManager) {
        this.tPointModelManager = tPointModelManager;
    }

    public double getMINDURATION() {
        return MINDURATION;
    }

    public void setMINDURATION(final long mAXDURATION) {
        MINDURATION = mAXDURATION;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(final long timeStep) {
        this.timeStep = timeStep;
    }

    public Iers getIers() {
        return iers;
    }

    public void setIers(final Iers iers) {
        this.iers = iers;
    }

    public Observer getObserver() {
        return observer;
    }

    public void setObserver(final Observer observer) {
        this.observer = observer;
    }

    public void setWeather(final Weather dat) {
        this.atm = dat;
    }

    @Override
    public List<TrajectoryData> getTrajectory(final Target source, final SkyOffset offsets, final long startTime, final long timeStep) {

        return null;
    }

    public void printTrajectoryData(final TrajectoryData coo) {
        System.out.format("%s%n", coo.toString());
    }

    // private TrajectoryData getTrajectoryData(JulianDate time, PointingCoord coo)
    // {
    // return new TrajectoryData(time, Math.toDegrees(coo.getCommandedAzEl().az),
    // Math.toDegrees(coo.getCommandedAzEl().el));
    // }

    public TrajectoryData getTrajectoryData(final JulianDate time, final PointingCoord coo, final boolean useRefraction,
            final boolean useTpoint) {
        TrajectoryData tra;
        if (useRefraction) {
            //logger.info("USE REFRACTION");
            tra = new TrajectoryData(time, Math.toDegrees(coo.getCommandedAzElRefracted().az),
                    Math.toDegrees(coo.getCommandedAzElRefracted().el));
        } else
            tra = new TrajectoryData(time, Math.toDegrees(coo.getCommandedAzEl().az),
                    Math.toDegrees(coo.getCommandedAzEl().el));
        if (useTpoint) {
            tra = addTpoint(tra);
            //logger.info("USE TPOINT");
        }

        return tra;
    }

    public void clearTrajectoryBuffer() {
        trajectoryArray = new double[numberOfNodes * 3];
        trajectoryNodes.clear();
        trajectoryNodes = new LinkedList<>();
        nextIndex = 0;
        nextTstart = 0;
        elapsedTime = 0;
    }

    @Override
    public List<TrajectoryData> getTrajectory(final Weather atm, final JulianDate startTime) {
        generateTrajectory(atm, startTime);
        return trajectoryNodes;

    }

    public static double getElapsedTime() {
        return elapsedTime;
    }

    public boolean isUseOffset() {
        return useOffset;
    }

    public void setUseOffset(boolean useOffset) {
        this.useOffset = useOffset;
    }

    public CoordOffset getCordOff() {
        return cordOff;
    }

    public void setCordOff(CoordOffset cordOff) {
        this.cordOff = cordOff;
    }
    
    private void generateTrajectory(final Weather atm, final JulianDate startTime) {

        this.atm = atm;
        this.startTime = startTime;
        final EquatorialObject tar = new EquatorialObject(startTime, iers, target, observer, atm, logger);
        if(useOffset){
            tar.setUseOffset(useOffset);
            tar.setOffset(cordOff);
        }
        final double tStart = startTime.djm0 + startTime.djm1;
        final double tStop = tStart + TOTALDURATION / JSOFA.DAYSEC;
        final double tStep = timeStep / JSOFA.DAYSEC;
        int i = nextIndex;
        elapsedTime = 0.0;
        TrajectoryData tra = new TrajectoryData();
        TrajectoryData oldTra=tra;
        for (double time = tStart; time <= tStop && i < numberOfNodes; time += tStep) {
            final JulianDate jd = new JulianDate(time, 0.);
            final PointingCoord coo = tar.getPointing(jd);
            System.out.println(coo.getParallacticAngle()+" "+coo.getParallacticAngleVel());
            tra = getTrajectoryData(jd, coo, target.isUseRefraction(), target.isUsePointingModel());
            trajectoryNodes.add(i, tra);
            //printTrajectoryData(tra);
            elapsedTime += timeStep;
            i++;
        }
        nextIndex = i;
        nextTstart = tStop + tStep;
        if (i < numberOfNodes) {
            final TrajectoryData tra1 = tra;
            for (int k = i; k < numberOfNodes; k++) {
                tra1.setTime(new JulianDate(nextTstart + k * tStep, 0.));
                // logger.info(""+tra.getTime().toString());
                // trajectoryNodes.remove(k);
                trajectoryNodes.add(k, tra1);
            }
            logger.info("filling:" + i);
        } else {
            if (elapsedTime > expTime) {
                nextIndex = 0;
                //trajectoryNodes.clear();
            }
            logger.info("Resetting Buffers");
        }

    }

    public double[] updateTrajectory(final Weather atm) {
        getTrajectory(atm, new JulianDate(nextTstart, 0.));
        return getTrajectoryNodesAsArray();
    }

    public double[] startTrajectoryGenerator(final Target source, final Weather atm, final JulianDate startTime) {

        this.target = source;
        nextIndex = 0;
        // if(expTime<MINDURATION){
        // TOTALDURATION=MINDURATION;
        // }
        initialTstart = startTime.djm0 + startTime.djm1;
        maxTime = initialTstart + TOTALDURATION / JSOFA.DAYSEC;
        clearTrajectoryBuffer();
        generateTrajectory(atm, startTime);
        return getTrajectoryNodesAsArray();
    }

    public double[] startSolSystemTrajectoryGenerator(final ESolSystem body, final double jdTime) {
        double[] traj = new double[183];
        //SolarSystem source = new SolarSystem(this.observer, body);
        //traj = source.getTrajectory(jdTime);
        JplHorizonReader.setObserver(this.observer);
		traj=JplHorizonReader.getTrajectory(body.getName(), jdTime, jdTime+1/24.);
        return traj;
    }

    public double[] startSolSystemTrajectoryGeneratorNoJpl(final ESolSystem body, final double jdTime) {
        double[] traj = new double[183];
        final SolarSystem source = new SolarSystem(this.observer, body, solSystemDir);
        traj = source.getTrajectory(jdTime);
        return traj;
    }

    public TrajectoryData addTpoint(final TrajectoryData tra) {
        final TrajectoryData trajTpoint = tra;
        final HorizonDirection tpointCorr = getTpoint(tra);
        double az = tra.getAz() - tpointCorr.az;
        if(az>360.)
            az-=360;
        trajTpoint.setAz(az);
        //trajTpoint.setEl(tra.getEl() + tpointCorr.el);
        trajTpoint.setEl(tra.getEl() - tpointCorr.el);
        trajTpoint.setTpAzCorr(tpointCorr.az);
        trajTpoint.setTpElCorr(tpointCorr.el);
        //logger.info("ADDED TPOINT");
        return trajTpoint;
    }

    public double[] addRefraction(final double[] val1) {
        final double[] ret = new double[183];
        final List<TrajectoryData> tra = TrajectoryData.arrayToList(val1);
        int i = 0;
        for (TrajectoryData tr : tra) {
            tr = addRefraction(tr);
            ret[i] = val1[i];
            ret[i + 1] = tr.getAz();
            ret[i + 2] = tr.getEl();
            i += 3;
        }
        return ret;
    }

    public double[] addTpoint(final double[] val1) {
        final double[] ret=new double[183];
        final List<TrajectoryData> tra = TrajectoryData.arrayToList(val1);
        int i = 0;
        for (TrajectoryData tr : tra) {
            tr = addTpoint(tr);
            ret[i] = val1[i];
            ret[i + 1] = tr.getAz();
            ret[i + 2] = tr.getEl();
            i += 3;
        }
        return ret;
    }

    public TrajectoryData addRefraction(final TrajectoryData tra) {
        final TrajectoryData trajRef = tra;
        final double refCorr = getRefraction(atm, Math.toRadians(90. - tra.getEl()));
        trajRef.setEl(tra.getEl() + refCorr);
        trajRef.setRefCorr(refCorr);
        //logger.info("ADDED REFRACTION");
        return trajRef;
    }

    public  HorizonDirection getTpoint(final double az, final double el) {
        //logger.info("OLD:{}",applyModel(az, el));
        //logger.info("NEW:{}",applyTpoint(az, el));
        //return applyModel(az, el);
        return applyTpoint(az, el);

    }

    public HorizonDirection getTpoint(final TrajectoryData tra) {
        return getTpoint(tra.getAz(), tra.getEl());
    }

    public  HorizonDirection applyTpoint(final double azo, final double elo) {
        double da=0.;
        double de=0.0;
        final double az = Math.toRadians(azo);
        final double el = Math.toRadians(elo);
        
        da = tPointModelManager.getAzCorr(az,el);
        de = tPointModelManager.getElCorr(az,el);
        return new HorizonDirection(da, de);

    }


    private RefCos getRefractionParams(final Weather weather) {
        return JSOFA.jauRefco(weather.getPressure(), weather.getTemperature(), weather.getHumidity(), 0.55);
    }

    // deg
    public double getRefractionCorrection(final Weather weather, double za) {
        final RefCos coeff = getRefractionParams(weather);
        za=Math.toRadians(za);
        final double corr = coeff.a * Math.tan(za) + coeff.b * Math.pow(Math.tan(za), 3.);
        return Math.toDegrees(corr);
    }

    public double getRefraction(final Weather atm0, final double za) {

        return getRefractionCorrection(atm0, za);
    }


    public double getTOTALDURATION() {
        return TOTALDURATION;
    }

    public static int getNextIndex() {
        return nextIndex;
    }

    public static double getNextTstart() {
        return nextTstart;
    }

    public static double getInitialTstart() {
        return initialTstart;
    }

    public static double getMaxTime() {
        return maxTime;
    }

    @Override
    public TrajectoryData getHorizontal(final Target source, final long startTime) {
        return null;
    }

    @Override
    public TrajectoryData getHorizontal(final double ra, final double dec, final long startTime) {
        return null;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(final int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
        trajectoryArray = new double[numberOfNodes];
        trajectoryNodes = new LinkedList<>();
    }

    public List<TrajectoryData> getTrajectoryNodes() {
            return trajectoryNodes;
    }

    public double[] getTrajectoryNodesAsArray() {
        int i = 0;

        for (int k = 0; k < trajectoryNodes.size() && i < numberOfNodes * 3; k++) {
            final TrajectoryData t = trajectoryNodes.get(k);
            trajectoryArray[i] = t.getTime().djm0 + t.getTime().djm1;
            trajectoryArray[++i] = t.getAz();
            trajectoryArray[++i] = t.getEl();
            i++;
            // System.out.println("Point:"+(k+1)+"-->"+t);
        }

        return trajectoryArray;
    }

    public void setTrajectoryNodes(final List<TrajectoryData> trajectoryNodes) {
        this.trajectoryNodes = trajectoryNodes;
    }

}
