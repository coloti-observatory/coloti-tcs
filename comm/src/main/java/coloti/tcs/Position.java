package coloti.tcs;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import coloti.tcs.configuration.Telescopio;
import coloti.tcs.gui.GUItcs;
//import coloti.tcs.configuration.MotoreArAz;
//import java.io.File;
//import java.io.IOException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import coloti.tcs.configuration.*;
import coloti.tcs.objclasses.*;
import coloti.tcs.task.DefaultListener;
import coloti.tcs.task.Task;
import coloti.tcs.task.TaskExecutor;
import coloti.tcs.task.TaskListener;
import coloti.tcs.trajectory.ETelescopes;
import coloti.tcs.trajectory.TrajectoryFitter;
import coloti.tcs.trajectory.TrajectoryManager;
import coloti.tcs.weather.WeatherData;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
//import coloti.tcs.ConfigurationClass;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jastronomy.jsofa.JSOFA.JulianDate;
//import java.lang.Math.*;
import org.jboss.util.state.DefaultStateMachineModel;
import org.jboss.util.state.State;
import org.jboss.util.state.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import astri.astron.Observer;
import astri.astron.Target;
import astri.astron.TimeUtil;
import astri.astron.Weather;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import javax.swing.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

//import jparsec.observer.ObserverElement;
//import jparsec.time.TimeElement;
//import jparsec.astronomy.CoordinateSystem;
//import jparsec.astronomy.LocationElement;
/*
 * <dependency>
      <groupId>jparsec</groupId>
      <artifactId>jparsec</artifactId>
      <version>2.0</version>
    </dependency>
 */

public class Position {

    private double latitude; // observer latitude in radians
    private double longitude; // observer longitude in radians

    public double RA;
    public double DEC;

    public double COLOTI_LONGITUDE = 12.3763888; // Longitude in degrees for Rome (East is positive)

    public static double calculateLocalSiderealTime(LocalDateTime utcTime, double longitude) {
        // Step 1: Calculate the Julian Date for the given UTC time
        double julianDate = calculateJulianDate(utcTime);

        // Step 2: Calculate Julian Date at 0h UT on the same day (JD0)
        LocalDateTime startOfDay = utcTime.truncatedTo(ChronoUnit.DAYS);
        double jd0 = calculateJulianDate(startOfDay);

        // Step 3: Calculate the time in UT (fractional days) since 0h UT
        double ut = utcTime.toLocalTime().toSecondOfDay() / 86400.0;

        // Step 4: Compute Greenwich Sidereal Time (GST) at 0h UT
        double t = (jd0 - 2451545.0) / 36525.0; // Julian centuries since J2000.0
        double gst0 = 100.46061837 + 36000.770053608 * t + 0.000387933 * t * t - (t * t * t / 38710000.0);
        gst0 = normalizeAngle(gst0); // Normalize to [0, 360) degrees

        // Step 5: Calculate GST in degrees for the current time
        double gst = gst0 + 360.98564736629 * ut;
        gst = normalizeAngle(gst);

        // Step 6: Convert GST to LST by adding observer's longitude
        double lst = gst + longitude;
        lst = normalizeAngle(lst);

        // Convert LST from degrees to hours (1 hour = 15 degrees)
        return lst / 15.0;
    }

    // Helper method to calculate Julian Date from LocalDateTime in UTC
    private static double calculateJulianDate(LocalDateTime utcTime) {
        int year = utcTime.getYear();
        int month = utcTime.getMonthValue();
        int day = utcTime.getDayOfMonth();
        double hour = utcTime.getHour() + utcTime.getMinute() / 60.0 + utcTime.getSecond() / 3600.0;

        // Adjust months and years for January and February
        if (month <= 2) {
            year -= 1;
            month += 12;
        }

        int A = year / 100;
        int B = 2 - A + (A / 4);

        // Julian Date calculation
        return Math.floor(365.25 * (year + 4716))
                + Math.floor(30.6001 * (month + 1))
                + day + hour / 24.0 + B - 1524.5;
    }

    // Helper method to normalize an angle to the range [0, 360) degrees
    private static double normalizeAngle(double angle) {
        angle = angle % 360.0;
        if (angle < 0) {
            angle += 360.0;
        }
        return angle;
    }

    
    public void Converter(double latitude, double longitude) {
        this.latitude = Math.toRadians(latitude);
        this.longitude = Math.toRadians(longitude);
    }

    public void AzAltToRaDec( double azimuth, double altitude, double lst) {
        // Convert input altitude and azimuth from degrees to radians
        altitude = Math.toRadians(altitude);
        azimuth = Math.toRadians(azimuth);

        // Calculate DEC
        double sinDec = Math.sin(latitude) * Math.sin(altitude)
                        + Math.cos(latitude) * Math.cos(altitude) * Math.cos(azimuth);
        double declination = Math.asin(sinDec);

        // Calculate HA
        double cosHA = (Math.sin(altitude) - Math.sin(latitude) * sinDec) /
                        (Math.cos(latitude) * Math.cos(declination));
        double hourAngle = Math.acos(cosHA);

        // Adjust hour angle based on azimuth
        if (Math.sin(azimuth) > 0) { // If azimuth is in the eastern half
            hourAngle = 2 * Math.PI - hourAngle;
        }

        // Convert LST from hours to radians
        lst = Math.toRadians(lst * 15); // LST in radians

        // Calculate RA
        double rightAscension = lst - hourAngle;
        if (rightAscension < 0) {
            rightAscension += 2 * Math.PI; // Keep RA in [0, 2π] range
        }

        // Convert RA and DEC from radians to degrees

        this.RA = Math.toDegrees(rightAscension) / 15;
        this.DEC = Math.toDegrees(declination);

        //return new double[] {Math.toDegrees(rightAscension) / 15, Math.toDegrees(declination)};
        
    }

    public static void main(String[] a) {
        //"Latitudine":43.4016667,
        //"Longitudine":12.3763888,
        Position pos = new Position();

        double latitudeDeg = 43.4016667;
        double longitudeDeg = 12.3763888;

        pos.Converter(latitudeDeg, longitudeDeg);

        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
        double lst = calculateLocalSiderealTime(currentTime, pos.COLOTI_LONGITUDE);

        pos.AzAltToRaDec(0, 90, lst);

        System.out.println("RA: "+pos.RA);
        System.out.println("DEC: "+pos.DEC);




    }
    
}
