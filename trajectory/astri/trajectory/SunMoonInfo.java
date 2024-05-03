package astri.trajectory;

import java.time.ZonedDateTime;

import org.jastronomy.jsofa.JSOFA.HorizonCoordinate;

import astri.astron.AstroTime;
import astri.astron.SunMoonCalculator;
import astri.astron.TimeUtil;

public class SunMoonInfo {

    private double latitude = 28.301025;
    private double longitude = -16.50796944;
    private double jd = 2460000.0;
    private String moonRiseTime = "";
    private String moonSetTime = "";
    private String sunRiseTime = "";
    private String sunSetTime = "";
    

    HorizonCoordinate sunHorizonCoordinates = null;
    HorizonCoordinate moonHorizonCoordinates = null;
    private boolean isDay = false;
    private double timeToNextTwilight;

    public SunMoonInfo() {}

    public SunMoonInfo(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public void calculate(double julianDay) {
        this.jd = julianDay;
        ZonedDateTime dat = TimeUtil.getDate(jd);
        SunMoonCalculator smc = new SunMoonCalculator(dat.getYear(), dat.getMonthValue(),
                dat.getDayOfMonth(), dat.getHour(), dat.getMinute(), dat.getSecond(),
                Math.toRadians(longitude), Math.toRadians(latitude));
        //MOON
        smc.setTwilightMode(SunMoonCalculator.TWILIGHT_MODE.TODAY_LT); // Default is TWILIGHT_MODE.CLOSEST
        smc.setTwilightModeTimeZone(0); // Only for TWILIGHT_MODE.TODAY_LT
        smc.calcSunAndMoon();
        moonHorizonCoordinates = new HorizonCoordinate(smc.moon.azimuth * SunMoonCalculator.RAD_TO_DEG,
                smc.moon.elevation * SunMoonCalculator.RAD_TO_DEG);
        moonRiseTime = getTriset(smc.moon.rise);
        moonSetTime = getTriset(smc.moon.set);
        //SUN
        smc.setTwilight(SunMoonCalculator.TWILIGHT.NAUTICAL); // Default is TWILIGHT_MODE.CLOSEST
        smc.setTwilightModeTimeZone(0); // Only for TWILIGHT_MODE.TODAY_LT
        smc.calcSunAndMoon();
        sunSetTime = getTriset(smc.sun.set);
        sunRiseTime = getTriset(smc.sun.rise);
        sunHorizonCoordinates = new HorizonCoordinate(smc.sun.azimuth * SunMoonCalculator.RAD_TO_DEG,
                smc.sun.elevation * SunMoonCalculator.RAD_TO_DEG); 
        if (sunHorizonCoordinates.el > -12.0) {
            isDay = true;
        } else {
            isDay = false;
        }
        this.timeToNextTwilight = ((smc.sun.set - jd) * 24.0);
        if (timeToNextTwilight < 0.)
            timeToNextTwilight= ((smc.sun.rise + 1 - jd) * 24.0);
    }

    public HorizonCoordinate getSunHorizonCoordinates() {
        return sunHorizonCoordinates;
    }

    public HorizonCoordinate getMoonHorizonCoordinates() {
        return moonHorizonCoordinates;
    }

    private String getTriset(double jd) {
        if (jd < 0)
            return "NO RISE/SET";
        ZonedDateTime dat = TimeUtil.getDate(jd);
        String h = "", m = "", s = "";
        if (dat.getHour() < 10)
            h = "0";
        if (dat.getMinute() < 10)
            m = "0";
        if (dat.getSecond() < 10)
            s = "0";

        return (h + dat.getHour() + ":" + m + dat.getMinute() + ":" + s + dat.getSecond());

    }

    public String getMoonRiseTime() {
        return moonRiseTime;
    }

    public String getMoonSetTime() {
        return moonSetTime;
    }

    public String getSunRiseTime() {
        return sunRiseTime;
    }

    public String getSunSetTime() {
        return sunSetTime;
    }

    public boolean isDay() {
        return isDay;
    }

    public String getMoonAzAsString() {
        return AstroTime.toSexagesimal(moonHorizonCoordinates.az);
    }

    public String getMoonElAsString() {
        return AstroTime.toSexagesimal(moonHorizonCoordinates.el);
    }

    public String getSunAzAsString() {
        return AstroTime.toSexagesimal(sunHorizonCoordinates.az);
    }

    public String getSunElAsString() {
        return AstroTime.toSexagesimal(sunHorizonCoordinates.el);
    }

    public String getTimeToNextTwilightAsString() {

        return AstroTime.toSexagesimal(timeToNextTwilight);
    }

    @Override
    public String toString() {
        return "SunMoonInfo [mjd=" + jd + ", moonRiseTime=" + moonRiseTime + ", moonSetTime=" + moonSetTime
                + ", sunRiseTime=" + sunRiseTime + ", sunSetTime=" + sunSetTime + ", sunHorizonCoordinates (az,el)="
                + sunHorizonCoordinates.az+" "+ sunHorizonCoordinates.el+ ", moonHorizonCoordinates(az,el)=" + moonHorizonCoordinates.az+" "+ moonHorizonCoordinates.el+ ", isDay=" + isDay
                + ", timeToNextTwilight=" + timeToNextTwilight + "]";
    }

    public static void main(String[] args) {
        SunMoonInfo smi = new SunMoonInfo();
        smi.calculate(2460284.9899858613);
        System.out.println(smi);
    }

}
