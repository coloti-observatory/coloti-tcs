package coloti.tcs.trajectory;

import java.util.Properties;

import javax.swing.text.html.parser.TagElement;

import java.io.*;

import org.jastronomy.jsofa.JSOFA.HorizonCoordinate;
import org.jastronomy.jsofa.JSOFA.JulianDate;
import org.jastronomy.jsofa.JSOFAIllegalParameter;
import org.jastronomy.jsofa.JSOFAInternalError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import astri.astron.AstroTime;
import astri.astron.CoordOffset;
import astri.astron.CoordinatesUtil;
import astri.astron.ESolSystem;
import astri.astron.EquatorialDirection;
import astri.astron.EquatorialObject;
import astri.astron.FieldSourceObservability;
import astri.astron.HorizonDirection;
import astri.astron.Iers;
import astri.astron.IersData;
import astri.astron.Observer;
import astri.astron.PointingCoord;
import astri.astron.SolarSystem;
import astri.astron.Target;
import astri.astron.TimeUtil;
import astri.astron.Weather;

/**
 * Hello world!
 *
 */
public class TrajectoryManagerOLD {
    private static final Logger logger = LoggerFactory.getLogger(TrajectoryManagerOLD.class);
    private ModelTermsManager tPointManager;
    private TrajectoryGeneratorOLD tra = new TrajectoryGeneratorOLD();
    private Weather weather;
    private Iers iers;
    private Observer astroObserver;
    private Double taiutc;
    private boolean useRefraction = false;
    private boolean usePointingModel = false;
    private String ephemDir;
    private String iersDir;
    private String tpointFile = "";
    private String configFile = "/config/astrometryConfig";
    private String telName = "ASTRI1";
    private ETelescopes telescope = ETelescopes.ASTRI1;
    private Target skyTarget;
    private CoordOffset coordOffset;
    private boolean useOffset;
    private double elevationLimit;
    private double[] currentTrajectory = new double[183];
    private boolean isTargetValid =false;
    private double acqDuration = -999.;
    private SunMoonInfo smi;
    private double minMoonDistance=60;
    private boolean isDay=false;
    private boolean moonSeparationOk=false;
    private boolean aboveMinElevationOk=false;
    private boolean isTrajectoryValid=false;
    private String baseDir;
    private boolean visible=false;

    public TrajectoryManagerOLD() {

    }

    public void init() {
        configFile=baseDir+"/config/astrometryConfig"; // "/home/coloti/coloti-tcs/comm/src/main/java/coloti/tcs/trajectory/config/astrometryConfig"; //
        loadConfig();
        loadTpoint();
        iers = new Iers(this.iersDir);
        coordOffset = new CoordOffset(0.0, 0.0);
        useOffset = false;
        smi = new SunMoonInfo();
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void setTpointFile(String tpointFile) {
        this.tpointFile = tpointFile;
    }

    public Observer getAstroObserver() {
        return astroObserver;
    }

    public TrajectoryGeneratorOLD getTra() {
        return tra;
    }

    public boolean isTargetValid() {
        return isTargetValid;
    }

    public double getAcqDuration() {
        return acqDuration;
    }

    public void setAstroObserver(Observer astroObserver) {
        this.astroObserver = astroObserver;
    }

    public void setElevationLimit(double f) {
        this.elevationLimit = f;
    }

    
    public HorizonCoordinate getSunHorizonCoordinate(double jd){
        smi.calculate(jd);
        return smi.sunHorizonCoordinates;
    }

    public HorizonCoordinate getMoonHorizonCoordinate(double jd){
        smi.calculate(jd);
        return smi.moonHorizonCoordinates;
    }

    public boolean isDay(){
        HorizonCoordinate sc = getSunHorizonCoordinate(TimeUtil.getCurrentJuliandDay());
        return (sc.el>-18.);
    }

    public SolarSystem getSolarSystemTarget(ESolSystem body) {
        return new SolarSystem(astroObserver, body, ephemDir);
    }

    

    public void setTarget(final Target skyTarget, CoordOffset offset) {
        setTarget(skyTarget);
        if (isTargetValid) {
            this.coordOffset = offset;
            Target targ = skyTarget;
            final JulianDate time = TimeUtil.getJDNow();
            final EquatorialObject tar = new EquatorialObject(time, this.iers, skyTarget, this.astroObserver,
                    weather, logger);

            PointingCoord pc = tar.getPointing(time);
            HorizonDirection ord = pc.getCommandedAzElRefracted();
            EquatorialDirection fakeCenter = tar.getActualRADec(time,
                    new HorizonDirection(ord.az + coordOffset.getOffLong(), ord.el + coordOffset.getOffLat()));
            targ.setRa2000(Math.toDegrees(fakeCenter.getRa()));
            targ.setDec2000(Math.toDegrees(fakeCenter.getDec()));
            this.skyTarget = targ;
        }
    }

    public void setTarget(final Target skyTarget) {
        this.skyTarget = skyTarget;
        if (!isObservable())
            isTargetValid = false;
        else
            isTargetValid = true;
    }

    private boolean isObservable() {

        final FieldSourceObservability sfo = CoordinatesUtil.getRisingAndSettingParameters(
                skyTarget.getSkyCoordinates(),
                astroObserver.getSITE_LAT(), astroObserver.getSITE_LONG(), AstroTime.getNow());
        logger.info(sfo.toString());
        skyTarget.setVisibility(sfo);
        visible = sfo.isVisibleNow();
        if (sfo.isVisibleNow() || sfo.getAlwaysVisible()) {
            final EquatorialObject tar = new EquatorialObject(TimeUtil.getJDNow(), iers, skyTarget, astroObserver, weather, logger);
            final PointingCoord coo = tar.getPointing(TimeUtil.getJDNow());
            logger.info(coo.toString());
            return checkLimits(coo);
        }
        return false;
    }

    public void assignToTelescope(ETelescopes tel) {
        this.telescope = tel;
        this.telName = tel.getTelName();
    }

    public String getTelescopeName() {
        return telName;
    }

    private void loadConfig() {
        try (InputStream input = new FileInputStream(configFile)) {

            Properties prop = new Properties();
            prop.load(input);
            this.taiutc = Double.valueOf(prop.getProperty("astrometry.taiutc"));
            this.iersDir = baseDir+ prop.getProperty("iers.dir");
            this.ephemDir = baseDir+ prop.getProperty("ephem.dir");
        } catch (NumberFormatException | IOException e) {
            logger.error("Unamble to read Configuration file{}", configFile);
        }
    }
    
    private IersData getIersData() {
        final double mjd = TimeUtil.getMJDNow();
        final IersData iersD = iers.getIersValues(mjd); // at the start
        logger.info("IERS:{}", iersD);
        return iersD;
    }

    private void loadTpoint() {
        tPointManager = new ModelTermsManager();
        if (!tpointFile.isEmpty())
            tPointManager.loadModelTermsFromFile(tpointFile);
    }

    public PointingCoord getObjActualCoord(final double timestamp) {
        try {
            final JulianDate jd = TimeUtil.getJulianDate(TimeUtil.getDateOfEpochMillis(timestamp));
            final EquatorialObject tar = new EquatorialObject(jd, iers, this.skyTarget, astroObserver,
                    weather,
                    logger);
            return tar.getPointing(jd);
        } catch (final JSOFAIllegalParameter e) {
            logger.error("bad jd parameter");
        } catch (final JSOFAInternalError e) {
            logger.error("error evaluating jd");
        }
        return null;
    }

    public PointingCoord getObjActualCoord(final JulianDate jd){
        return getObjActualCoord(jd, this.skyTarget);
    }

    public PointingCoord getObjActualCoord(final JulianDate jd, final Target st) {

        final EquatorialObject tar = new EquatorialObject(jd, iers, st, astroObserver,
                weather,
                logger);
        return tar.getPointing(jd);
    }

    public boolean isObservable(final Target skyT, final double elMin) {
        this.skyTarget = skyT;
        this.elevationLimit = elMin;
        return isObservable();
    }

    public boolean isTrajectoryValid() {
        return isTrajectoryValid;
    }

    public double[] generateTrajectory(final Target skyTarget, CoordOffset offset, final JulianDate start) {
        setTarget(skyTarget,offset);
        return generateTrajectory(start);
    }

    public double[] generateTrajectory(final JulianDate start){
        if (isTargetValid) {
            final TrajectoryGeneratorOLD p = new TrajectoryGeneratorOLD(iers, astroObserver);
            p.settPointModelManager(tPointManager);
            currentTrajectory = p.startTrajectoryGenerator(skyTarget, weather, start);
            isTrajectoryValid=validateTrajectory();
        } else {
            logger.warn("Please check if the Target is observable now");
        }
        return currentTrajectory;
    }

    public double[] generateTrajectory(final Target skyTarget, final JulianDate start) {
        setTarget(skyTarget);
        return generateTrajectory(start);
    }

    public void printTrajectory() {
        for (int i = 0; i < currentTrajectory.length; i += 3) {
            System.out.println(currentTrajectory[i] + " " + currentTrajectory[i + 1] + " " + currentTrajectory[i + 2]);
        }
    }

    private boolean validateTrajectory() {
        double tstart = currentTrajectory[0];
        for (int i = 0; i < currentTrajectory.length; i += 3) {
            if (currentTrajectory[i + 2] < elevationLimit) {
                double dur = (currentTrajectory[i] - tstart) * 24 * 3600.;
                logger.warn("The source is visible for:{} sec", dur);
                if (acqDuration > 0 && dur < acqDuration)
                    return false;
            }
        }
        return true;
    }

    public double sunDistance(PointingCoord coo){
        return CoordinatesUtil.targetSunSeparation(Math.toDegrees(coo.getEquatorialRADec().getRa()),Math.toDegrees(coo.getEquatorialRADec().getDec()));
    }

    public double moonDistance(PointingCoord coo){
        return CoordinatesUtil.targetMoonSeparation(Math.toDegrees(coo.getEquatorialRADec().getRa()),Math.toDegrees(coo.getEquatorialRADec().getDec()));
    }

    public double getMinMoonDistance() {
        return minMoonDistance;
    }

    public void setMinMoonDistance(double minMoonDistance) {
        this.minMoonDistance = minMoonDistance;
    }

    public boolean isMoonSeparationOk() {
        return moonSeparationOk;
    }

    public boolean isAboveMinElevationOk() {
        return aboveMinElevationOk;
    }

    private boolean checkLimits(PointingCoord coo) {
        double moonSep =moonDistance(coo);
        logger.info("Moon Separation:{}",moonSep);
        moonSeparationOk=(moonSep>minMoonDistance);
        aboveMinElevationOk=(Math.toDegrees(coo.getCommandedAzEl().el) > elevationLimit);
        return (moonSeparationOk && aboveMinElevationOk);
    }

    public boolean isUseRefraction() {
        return useRefraction;
    }

    public boolean isUsePointingModel() {
        return usePointingModel;
    }

    public void setAcquisitionDuration(double d) {
        this.acqDuration = d;
    }

    public void setBaseDir(String bDir) {
        this.baseDir=bDir;
    }

    public boolean isVisible() {
        return visible;
    }

}
