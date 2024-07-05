package coloti.tcs;

import coloti.tcs.configuration.Telescopio;
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
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
//import coloti.tcs.ConfigurationClass;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;

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



/*
import java.util.concurrent.CompletableFuture;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DrbgParameters.NextBytes;
import java.util.*;

import java.util.function.IntPredicate;
import javax.lang.model.util.ElementScanner6;
mport coloti.tcs.ACSv5;
*/

public class TCS {
    
    public ACS AsseX; // public final
    public ACS AsseY;
    public ACS AsseCupola; //= new ACS("serial ID cupola");
    public ACS AsseZ;
    public final WeatherData weatherdata;
    // Parametri   D = degrees, R = radians, AS = arcseconds, H = hours, S = seconds
    double pi = Math.PI;
    double D2R = pi/180.0;
    double R2D = 180.0/pi;
    double AS2R = pi/(180.0*3600.0);
    double R2AS = (180.0*3600.0)/pi;
    double H2R = pi/12.0;
    double R2H = 12.0/pi;
    double S2R = pi/(12.0*3600.0);
    double R2S = (12.0*3600.0)/pi;
    double[] CostX = new double[6];
    double[] CostY = new double[6];
    int SetPointX;
    int SetTrackX;
    int SetPointY;
    int SetTrackY;
    boolean AzPointing = false;
    boolean ElPointing = false;
    boolean AzTracking = false;
    boolean ElTracking = false;
    double ConversionFactorX;
    double ConversionFactorY;
    static final int RAD = 0;
    static final int GRAD = 1;
    static final int HOUR = 2;
    static final int ENC = 3;
    static final int ARCSECS = 4;
    int UnitMeasure = ARCSECS;
    boolean xAxisConnection = false;
    boolean yAxisConnection = false;
    boolean domeAxisConnection = false;
    boolean weatherConnection = false;
    boolean tcsConnection = false;
    private EHardwareStatePhase statePhase;
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public int TemporaryErr;
    public int error;
    public int nErrors = 0;
    public String errorBuffer;
    public String errorText = "";
    String X = "X";
    String Y = "Y";
    String Z = "Z";
    int NumAxes = 2;
    ConfigurationClass CFG;
    GENERALE GEN;
    OSSERVATORIO OSS;
    CUPOLA CUP;
    TELESCOPIO TEL;
    MOTOREARAZ MotAZ;
    MOTOREDECAL MotEL;
    SB129X SB;
    PADDLE PAD;
    POSZERO PZ;
    double DPX;
    double DPY;
    TrajectoryFitter tf;
    // opcua states
    private StateMachine mcsStateMachine;
    public State OFF = new State(0, "OFF");
    public State LOADED = new State(1, "LOADED");
    public State STANDBY = new State(2, "STANDBY");
    public State ONLINE = new State(3, "ONLINE");
    public State MAINTENANCE = new State(4, "MAINTENANCE");
    public State FAULT = new State(5, "FAULT");
    public Field fieldcmd;

    public TCS(){//boolean connectX, boolean connectY, boolean connectDome, String IDserX, String IDserY, String IDserDome)
        Configure();
        this.xAxisConnection = GEN.ConnessioneAz;
        this.yAxisConnection = GEN.ConnessioneEl;
        this.domeAxisConnection = GEN.ConnessioneDome;
        this.weatherConnection = GEN.ConnessioneMeteo;

        //this.tcsConnection = xAxisConnection & yAxisConnection & domeAxisConnection
        //AsseCupola = new ACS("/dev/ttyUSB0",1)  
        AsseX = new ACS(GEN.IdSerialAz,1);
        AsseY = new ACS(GEN.IdSerialEl,1);
        AsseCupola = new ACS(GEN.IdSerialDome,1);
        
        weatherdata = new WeatherData(GEN.IdSerialWeather);
        
        this.CostX[0] = 1;
        this.CostX[1] = 1;
        this.CostX[2] = 1;
        this.CostY[0] = 1;
        this.CostY[1] = 1;
        this.CostY[2] = 1;

        /*
        this.xAxisConnection = connectX;
        this.yAxisConnection = connectY;
        this.domeAxisConnection = connectDome;

        AsseX = new ACS(IDserX);
        AsseY = new ACS(IDserY);
        AsseCupola = new ACS(IDserDome);*/
    }

    public void tcsError(final int err, final int IdErr){
        this.error = -1;
        this.errorBuffer = "none";
        this.TemporaryErr = err;
        if(err != -1){
            this.nErrors += 1;
            // se stringa: inizializzazione come String errorstring = "Least recent call: "
            //  this.errorstring += IdErr+", ";
            this.error = IdErr;
            this.errorBuffer = errorMap.get(IdErr);
            //logger.warn(errorBuffer);
            this.errorText += "Error "+IdErr+": "+errorBuffer;
            if (check(nEncErr, err))
                this.errorText += ", ("+err+")"+errEncMap.get(err)+";";
            else
                this.errorText += ";";
        }
    }
    
    public final boolean connect(){
        // AZIMUTH
        if (xAxisConnection){
            this.xAxisConnection = AsseX.SetSimpleStart(0);
            Sleep(500);
            tcsError(AsseX.InitAxes(), 1100);

            this.AsseX.ENCODERRES[0] = MotAZ.RisoluzioneEncoder1;
            this.AsseX.MaxAbsVel[0] = this.AsseX.MaxVel[0] = MotAZ.RisoluzioneEncoder1;
            this.AsseX.MinAbsVel[0] = this.AsseX.MinVel[0] = -MotAZ.RisoluzioneEncoder1;
            this.AsseX.MaxAbsAcc[0] = this.AsseX.MaxAcc[0] = MotAZ.RisoluzioneEncoder1;

            final double gearratioX = (double) TEL.RapportoRiduzioneAZ*MotAZ.RiduzioneMotore;
            this.ConversionFactorX = AsseX.SetUserUnit(X, UnitMeasure, gearratioX);
            this.AsseX.SetMaxMinVel(X, MotAZ.VelocitaMassima*3600, -MotAZ.VelocitaMassima*3600);
            this.AsseX.SetMotMaxMinPos(X, MotAZ.PosizioneLimiteSup*3600, MotAZ.PosizioneLimiteInf*3600);            
        }

        // ELEVATION
        if (yAxisConnection){
            this.yAxisConnection = AsseY.SetSimpleStart(0);
            Sleep(500);
            tcsError(AsseY.InitAxes(), 1200);

            this.AsseY.ENCODERRES[0] = MotEL.RisoluzioneEncoder1;
            this.AsseY.MaxAbsVel[0] = this.AsseY.MaxVel[0] = MotEL.RisoluzioneEncoder1;
            this.AsseY.MinAbsVel[0] = this.AsseY.MinVel[0] = -MotEL.RisoluzioneEncoder1;
            this.AsseY.MaxAbsAcc[0] = this.AsseY.MaxAcc[0] = MotEL.RisoluzioneEncoder1;

            final double gearratioY = (double) TEL.RapportoRiduzioneAL*MotEL.RiduzioneMotore;
            this.ConversionFactorY = AsseY.SetUserUnit(X, UnitMeasure, gearratioY);
            this.AsseY.SetMaxMinVel(X, MotEL.VelocitaMassima*3600, -MotEL.VelocitaMassima*3600);
            this.AsseY.SetMotMaxMinPos(X, MotEL.PosizioneLimiteSup*3600, MotEL.PosizioneLimiteInf*3600);
        }
        
        // DOME
        if(domeAxisConnection){
            this.domeAxisConnection = AsseCupola.SetSimpleStart(0);
            Sleep(500);
            //tcsError(AsseCupola.InitAxes(), 1300) ?
        }

        // WEATHER
        if(weatherConnection){
            weatherdata.OpenCommunications();
            Sleep(500);
        }

        // machine state
        if (xAxisConnection || yAxisConnection || domeAxisConnection){
            initHwStateMachine(OFF);
            TEL.MachineState = mcsStateMachine.getCurrentState().value;
            TEL.MachineStatePhase = EHardwareStatePhase.ACTIVE.ordinal();
            return true;
        }
        else
            return false;
    }

    private void disconnect() {
        boolean status = true;
        if (xAxisConnection){
            status = AsseX.CloseComm();
            if (status)
                tcsError(0,1101);
        }
        if (yAxisConnection){
            status = AsseY.CloseComm();
            if (status)
                tcsError(0,1201);
        }
        if (domeAxisConnection){
            status = AsseCupola.CloseComm();
            if (status)
                tcsError(0,1301);
        }
        taskExecutor.shutdown();
    }
    
    public void Configure(){ // CambiaConfig SalvaConfig ReadConfig
        this.CFG = new ConfigurationClass();
        this.GEN = new GENERALE(CFG);
        this.OSS = new OSSERVATORIO(CFG);
        this.CUP = new CUPOLA(CFG);
        this.TEL = new TELESCOPIO(CFG);
        this.MotAZ = new MOTOREARAZ(CFG);
        this.MotEL = new MOTOREDECAL(CFG);
        this.SB = new SB129X(CFG);
        this.PAD = new PADDLE(CFG);
        this.PZ = new POSZERO(CFG);

        // Sole
        // Luna
    }

    public void Sleep(final int millisecondsTime) { 
        try {
          TimeUnit.MILLISECONDS.sleep(millisecondsTime);
        } catch (final InterruptedException e) {
          e.printStackTrace();
        }
    }

    public StateMachine getMcsStateMachine() {
        return mcsStateMachine;
    }

    public void initHwStateMachine(final State init) {
        final StateMachine.Model model = new DefaultStateMachineModel();
        model.addState(OFF, new State[] { LOADED, MAINTENANCE, FAULT });
        model.addState(LOADED, new State[] { STANDBY, MAINTENANCE, FAULT });
        model.addState(STANDBY, new State[] { LOADED, ONLINE, MAINTENANCE, FAULT });
        model.addState(ONLINE, new State[] { STANDBY, MAINTENANCE, FAULT });
        model.addState(MAINTENANCE, new State[] { STANDBY });
        model.addState(FAULT, new State[] { MAINTENANCE });
        // Set the initial state
        model.setInitialState(init);
        mcsStateMachine = new StateMachine(model);
    }

    public void setFieldCmd(TELESCOPIO tel, String name, String state, long start, long stop, String err){
        try {
            fieldcmd = tel.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        /*try {
            fieldcmd.set(tel.getClass(),"commandname: "+name+"; busy: "+state+"; tstart: "+start+"; tstop: "+stop+"; error: "+err);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }


    private final TaskExecutor<Void> taskExecutor = new TaskExecutor<>();

    /* 
    private final TaskListener defaultListener = new TaskListener() {
        long tStart = 0L;
        long tStop = 0L;
        String commandName = "";
        Field field;
        public void setField(String name, String state, long start, long stop, String err){
            try {
                field = TEL.getClass().getDeclaredField(name);
                //field = TEL.getClass().getField(name);
                String fieldstring = "commandname: "+name+"; busy: "+state+"; tstart: "+start+"; tstop: "+stop+"; error: "+err;
                field.set(TEL.getClass(), fieldstring);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { // | IllegalAccessException
                e.printStackTrace();
            }
        }

        public void setCommandName(final String commandname){
            commandName = commandname;
        }

        @Override
        public void onStart(final Object in) {
            commandName = String.valueOf(in);
            System.out.println("Task Started");
            tStart = System.currentTimeMillis();
            setField(commandName, "TRUE", tStart, 0L, "");
        }

        @Override
        public void onWorking(final Object... v) {
            System.out.println("Task Working");
            tStop = System.currentTimeMillis();
            setField(commandName, "TRUE", tStart, tStop, "none");
        }

        @Override
        public void onDone(final Object out) {
            System.out.println("Task Done");
            tStop = System.currentTimeMillis();
            setField(commandName, "FALSE", tStart, tStop, "none");
        }

        @Override
        public void onError(final Object output) {
            tStop = System.currentTimeMillis();
            setField(commandName, "FALSE", tStart, tStop, String.valueOf(output));
        }

    };
    //*/




    //#region Errors

    public Map <Integer, String> errorMap = new HashMap <>() {
        {
            put(1100, "Az axis connection issue during initialization");
            put(1200, "El axis connection issue during initialization");
            put(1300, "Dome axis connection issue during initialization");
            put(1101, "Close communication issue, Az axis not disconnected");
            put(1201, "Close communication issue, El axis not disconnected");
            put(1301, "Close communication issue, Dome axis not disconnected");
            put(1151, "Error in get Az motor status");
            put(1251, "Error in get El motor status");
            put(1152, "Error in Get Az telescope position");
            put(1153, "Error in Get Az actual velocity");
            put(1154, "Error in Get Az commanded position");
            put(1155, "Error in Get Az commanded velocity");
            put(1156, "Error in Get Az commanded acceleration");
            put(1157, "Error in Get Az motion state");
            put(1158, "Error in Get Az commanded deceleration");
            put(1160, "Error in Set Az target position");
            put(1161, "Error in Set Az slew acceleration");
            put(1162, "Error in Set Az slew deceleration");
            put(1165, "Error in Set Az slew mode");
            put(1166, "Error in Set Az motor velocity");
            put(1167, "Error in Set Az track mode");
            put(1170, "Error in Set Az motor on");
            put(1171, "Error in Stop Az Move");
            put(1172, "Error in Set Az motor off");
            put(1190, "Error in move Az motor");
            put(1192, "Execution program issue in Az Home position");
            put(1252, "Error in Get El telescope position");
            put(1253, "Error in Get El actual velocity");
            put(1254, "Error in Get El commanded position");
            put(1255, "Error in Get El commanded velocity");
            put(1256, "Error in Get El commanded acceleration");
            put(1257, "Error in Get El motion state");
            put(1258, "Error in Get El commanded deceleration");
            put(1260, "Error in Set El target position");
            put(1261, "Error in Set El slew acceleration");
            put(1262, "Error in Set El slew deceleration");
            put(1265, "Error in Set El slew mode");
            put(1266, "Error in Set El motor velocity");
            put(1267, "Error in Set El track mode");
            put(1270, "Error in Set El motor on");
            put(1271, "Error in Stop El Move");
            put(1272, "Error in Set El motor off");
            put(1290, "Error in move Az motor");
            put(1292, "Execution program issue in Az Home position");
            put(1351, "Error in get Dome position");
            put(1360, "Error in Set Dome target position");
            put(1380, "Execution program issue in open Dome");
            put(1381, "Execution program issue in close Dome");
            put(1382, "Execution program issue in stop Dome");
            put(1383, "Execution program issue in move Dome to west");
            put(1384, "Execution program issue in move Dome to east");
            put(1386, "Execution program issue in move Dome to point");
            put(1387, "Execution program issue in move Dome to home position");
        }
    };


    // 800 per i Begin Errors, 700 per program, 900 per general
    // 101 settato un modo sbagliato
    private final int[] nEncErr = new int[]{100,101,102,103,104,701,702,703,704,707,708,740,741,742,743,744,750,751,752,753,754,755,756,757,758,759,760,761,762,763,764,765,766,767,768,769,770,771,772,773,774,775,776,777,778,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,801,809,810,811,812,814,819,820,821,822,823,824,825,890,891,900,901,903,910,912,915,916,917,919,920,921,922,941,944,990,991};

    private static boolean check(final int[] arr, final int toCheckValue)
    {
        for (final int element : arr) {
            if (element == toCheckValue) {
                return true;
            }
        }
        return false;
    }


    public Map <Integer, String> errEncMap = new HashMap <>(){
        {
            put(100, "initialization issue, mode not setted");
            put(101, "initialization issue, wrong mode setted");
            put(102, "serial answer length is zero");
            put(103, "communication status is false during axes initialization");
            put(104, "relative position overflow");
            put(105, "overflow in input or output request (RIP,ROP,RSI,RIL,SHI,SLO)");
        
            put(701 , "rogram finished successfully (message)");
            put(702 , "utomatic routine finished successfully (message)");
            put(703 , "rogram is paused by user (message)");
            put(704 , "rogram is in step mode (message)");
            put(707 , "rogram was stopped by user. (message)");
            put(708 , "fter a stop command: The program was not running (operational error)");
            put(740, "command is not available under host protocol  (edit error)");
            put(741, "command can't be executed while a program is running (edit error)");
            put(742, "illegal range was specified (edit error)");
            put(743, "unrecognized edit command (edit error)");
            put(744, "an attempt is made to delete or overwrite a protected program statement (PP>0) (operational error)");
            put(750, "unrecognized command (insert error)");
            put(751, "unrecognized set variable (insert error)");
            put(752, "unrecognized state (insert error)");
            put(753, "unrecognized variable (insert error)");
            put(754, "unrecognized index (insert error)");
            put(755, "unrecognized address variable (insert error)");
            put(756, "unrecognized array element (insert error)");
            put(757, "unrecognized relation (insert error)");
            put(758, "unrecognized operation (insert error)");
            put(759, "missing an equal sign (insert error)");
            put(760, "missing a label (insert error)");
            put(761, "the label name is too large (insert error)");
            put(762, "illlegal label name (insert error)");
            put(763, "the label already exists (insert error)");
            put(764, "the specified index is not available (insert error)");
            put(765, "too many free constants (insert error)");
            put(766, "illegal constant (insert error)");
            put(767, "program is too large (insert error)");
            put(768, "statement is too large (insert error)");
            put(769, "illegal statement (insert error)");
            put(770, "loop without end (compile error)");
            put(771, "END without reason (compile error)");
            put(772, "a label was not found (compile error)");
            put(773, "loop nesting is too deep (compile error)");
            put(774, "missing END statement (compile error)");
            put(775, "IF nesting is too deep (compile error)");
            put(776, "Else statement without reason (compile error)");
            put(777, "CASE nesting is too deep (compile error)");
            put(778, "CASE already exists or CASE statement is missing (compile error)");
            put(780, "unrecognized command was referenced (internal error)");
            put(781, "unrecognized data was referenced (internal error)");
            put(782, "illegal datalocation was referenced (internal error)");
            put(783, "program is not compiled yet (Run-time error)");
            put(784, "zero divide (Run-time error)");
            put(785, "the specified start location is not found (Run-time error)");
            put(786, "illegal variable reference (Run-time error)");
            put(787, "Ran into ret statemnent without call (Run-time error)");
            put(788, "out of program (Run-time error)");
            put(789, "Call nesting is too deep (Run-time error)");
            put(790, "a checksum error is detected in the program area (Run-time error)");
            put(791, "executed END without reason (Run-time error)");
            put(792, "executed loop nesting is too deep (Run-time error)");
            put(793, "executing a square root of a negative number (Run-time error)");
            put(794, "executed CASE nesting is too deep (Run-time error)");

            put(801, "motion has begun succesfully");
            put(809, "the required motion is common and the other axis failed to move");
            put(810, "in continuous path mode, length of first segment is not enough (acceleration to velocity)");
            put(811, "in continuous path mode, length of first segment is not enough (deceleration to velocity)");
            put(812, "in master slave mode (12) required master not defined, or in path gegeneration mode (16) master axis not defined, or in continuous path mode (15) lower index > upper index");
            put(814, "in arbitrary path generation XUI>511");
            put(819, "the combination of the motion parameters does not enable the creation of desired profile, in multiple velocities mode (9) or in search mode (8)");
            put(820, "the motor is in a disable state");
            put(821, "the required motion mode is not valid");
            put(822, "either the required motion is common, but the two axies are not in the same motion mode");
            put(823, "the required target is out of the permitted range (position low - posigion high)");
            put(824, "the controller could not calculate the motion with the existing parameters");
            put(825, "the motion mode is common, but the begin command has been issued for one axis");
            put(890, "memory failure in the data area has been detected, it is reccomended to issue a RESET");
            put(891, "memory failure in the code area (firmware) has been detected, the firmware must be replaced");

            put(900, "checksum error detected in the received command or empty command");
            put(901, "command, or subcommand, was not executed, unrecognized");
            put(903, "SAVE operation has failed)");
            put(910, "command was not executed, requires special hardware");
            put(912, "servo process does not communicate with the main processor");
            put(915, "operation failed, many possible explanations, see the software guide for more informations");
            put(916, "command was not executed, many possible explanations, see the software guide for more informations");
            put(917, "command was not executed, command not supported in the current version");
            put(919, "array set command was not executed, invalid data");
            put(920, "command was not executed, missing data field");
            put(921, "non fatal, data field out of valid range, parameter set with the nearest valid value");
            put(922, "non fatal, unrecognized subcommand was found within data field");
            put(941, "non fatal, operation cannot be executed while a program is running");
            put(944, "non fatal, delete or overwrite operation are not allowed");
            put(990, "non fatal, memory checksum error");
            put(991, "non fatal, firmware checksum error");
        }
    };



    

    
    //#region GET


    // GETTERS
    
    public boolean GetAzLsOpCw(){
        return MotAZ.StatusLimitSwitchCW;
    }

    public boolean GetAzLsOpCcw(){
        return MotAZ.StatusLimitSwitchCCW;
    }

    public boolean GetElLsOpLow(){
        return MotEL.StatusLimitSwitchLow;
    }
    
    public boolean GetElLsOpHigh(){
        return MotEL.StatusLimitSwitchHigh;
    }

    public int GetAzMotorStatus(){
        if (xAxisConnection){
            tcsError(AsseX.GetMotorStatus(X),1151);
            this.MotAZ.MotorStatus = AsseX.MOTORSTATUS[0];
        }
        return MotAZ.MotorStatus; // cumulative status of the AZ motors: 0=both disabled; 1=both enabled; 2=degraded state i.e. 1 enabled; 1 in fault; 3=both in fault
    }

    public int GetElMotorStatus(){
        if (yAxisConnection){
            tcsError(AsseY.GetMotorStatus(X),1251);
            this.MotEL.MotorStatus = AsseY.MOTORSTATUS[0];
        }
        return MotEL.MotorStatus; // status of the EL motor: 0=disabled; 1=enabled; 2=fault
    }

    public double GetAzTelPos(){
        if (xAxisConnection){
            tcsError(AsseX.GetMotPos(X),1152);
            this.MotAZ.TelPos = AsseX.PositionAx[0];
        }
        return MotAZ.TelPos;
    }

    public double GetAzActVel(){
        if (xAxisConnection){
            tcsError(AsseX.GetActualMotVel(X),1153);
            this.MotAZ.ActualVel = AsseX.ActualVelAx[0];
        }
        return MotAZ.ActualVel;
    }

    public double GetAzActAcc(){
        return MotAZ.ActualAcc;
    }

    public double GetAzCommandedPos(){
        if (xAxisConnection){
            tcsError(AsseX.GetAbsTargPos(X), 1154);
            this.TEL.TargetAZ = AsseX.AbsTargPosAx[0];
        }
        return TEL.TargetAZ;
    }

    public double GetAzCommandedVel(){
        if (xAxisConnection){
            tcsError(AsseX.GetMotVel(X),1155);
            this.MotAZ.CommandedVel = AsseX.VelAx[0];
        }
        return MotAZ.CommandedVel;
    }

    public double GetAzCommandedAcc(){
        if (xAxisConnection){
            tcsError(AsseX.GetMotAcc(X),1156);
            this.MotAZ.CommandedAcc = AsseX.AccAx[0];
        }
        return MotAZ.CommandedAcc;
    }

    public double GetCupolaPosition(){
        
        long valo;
        if (true){
            //GetCupolaInfo();
            if (true){
                tcsError(AsseCupola.GetMotEncPos(X),1351);
                valo = AsseCupola.VALUECR;
                this.CUP.Pos = valo/AsseCupola.CONVFACTOR[0];
                this.CUP.AZ = CUP.Pos/3600.0;
                if (CUP.AZ >= 360.0)
                    this.CUP.AZ -= 360.0;
            }
        }
        return CUP.AZ;
    } 

    public double GetElTelPos(){
        if (yAxisConnection){
            tcsError(AsseY.GetMotPos(X),1252);
            this.MotEL.TelPos = AsseY.PositionAx[0];
        }
        return MotEL.TelPos;
    }

    public double GetElActVel(){
        if (yAxisConnection){
            tcsError(AsseY.GetActualMotVel(X),1253); 
            this.MotEL.ActualVel = AsseY.ActualVelAx[0];
        }
        return MotEL.ActualVel;
    }

    public double GetElActAcc(){
        return MotEL.ActualAcc;
    }

    public double GetElCommandedPos(){
        if (yAxisConnection){
            tcsError(AsseY.GetAbsTargPos(X),1254);
            this.TEL.TargetAZ = AsseY.AbsTargPosAx[0];
        }
        return TEL.TargetAZ;
    }
    
    public double GetElCommandedVel(){
        if (yAxisConnection){
            tcsError(AsseY.GetMotVel(X),1255);
            this.MotEL.ActualVel = AsseY.VelAx[0];
        }
        return MotEL.CommandedVel;
    }

    public double GetElCommandedAcc(){
        if (yAxisConnection){
            tcsError(AsseY.GetMotAcc(X),1256);
            this.MotEL.CommandedAcc = AsseY.AccAx[0];
        }
        return MotEL.CommandedAcc;
    }

    public int GetAzMotionState(){
        if (xAxisConnection){
            int MS = 0;
            tcsError(AsseX.GetActualMotVel(X),1153);
            final double ActualVelocity1 = AsseX.ActualVelAx[0];
            Sleep(200);
            tcsError(AsseX.GetActualMotVel(X),1153);
            final double ActualVelocity2 = AsseX.ActualVelAx[0];
            final double deltaV = ActualVelocity2 - ActualVelocity1;
            tcsError(AsseX.GetMotionMode(X),1157);

            if (AsseX.MOTIONMODE[0] == 0)
                MS = 2; //slewing
            else if (AsseX.MOTIONMODE[0] == 10)
                MS = 3; // MS = 4   // tracking

            if (deltaV > 0 && deltaV < 200)
                MS = 0;  // stopped
            else if (deltaV < 0)
                MS = 1;  // stopping

            this.MotAZ.MotionState = MS;
        }
        return MotAZ.MotionState;
    }

    public int GetElMotionState(){
        if (yAxisConnection){
            int MS = 0;
            tcsError(AsseY.GetActualMotVel(X),1253);
            final double ActualVelocity1 = AsseY.ActualVelAx[0];
            Sleep(200);
            tcsError(AsseY.GetActualMotVel(X),1253);
            final double ActualVelocity2 = AsseY.ActualVelAx[0];
            final double deltaV = ActualVelocity2 - ActualVelocity1;
            tcsError(AsseY.GetMotionMode(X),1257);

            if (AsseY.MOTIONMODE[0] == 0)
                MS = 2; //slewing
            else if (AsseY.MOTIONMODE[0] == 10)
                MS = 3; // MS = 4   // tracking

            if (deltaV > 0 && deltaV < 200)
                MS = 0;  // stopped
            else if (deltaV < 0)
                MS = 1;  // stopping

            this.MotEL.MotionState = MS;
        }
        return MotEL.MotionState;
    }
    // da eliminare?
    public double GetAzEncOffset(){
        return MotAZ.EncOffset;
    }
    // da eliminare?
    public double GetElEncOffset(){
        return MotEL.EncOffset;
    }

    public int GetMachineState(){
        return TEL.MachineState;  // 0 off, 1 loaded, 2 standby, 3 online, 4 maintenance, 5 fault
    }

    public int GetMachineStatePhase(){
        return TEL.MachineStatePhase;  // 0 entering, 1 active, 2 existing, 3 inactive, 4 unknown 
    }

    public int GetTCUMode(){
        return TEL.TCUMode;
    }

    // INFO 

    public String GetGoLoadedInfo(){
        return TEL.GoLoadedInfo;
    }

    public String GetGoStandbyInfo(){
        return TEL.GoStandbyInfo;
    }

    public String GetGoOnlineInfo(){
        return TEL.GoOnlineInfo;
    }

    public String GetGoMaintenanceInfo(){
        return TEL.GoMaintenanceInfo;
    }

    public String GetAzEnableMotorsInfo(){
        return TEL.EnableAzMotorsInfo;
    }
    /*
     * if (xAxisConnection){
            AsseX.IsMoving(X);
            
            final BitSet bitsMState = BitSet.valueOf(new byte[]{AsseX.Tell0.T0MotorStateX});
            String controlInfo = "";
            if (bitsMState.get(3))
                    controlInfo = "FALSE";
            else
                controlInfo = "";

            this.MotAZ.EnableMotorsInfo = controlInfo;
            
            final String INFO = "commandname: CommandEnableDriveAzimuth; busy: FALSE; tstart: 1970-01-01-00:00:00.000; tstop: 1970-01-01-00:00:00.000; error: ";
        }
     */

    public String GetAzDisableMotorsInfo(){
        return TEL.DisableAzMotorsInfo;
    }

    public String GetElEnableMotorsInfo(){
        return TEL.EnableElMotorsInfo;
    }

    public String GetElDisableMotorsInfo(){
        return TEL.DisableElMotorsInfo;
    }

    public String GetStartMotionInfo(){
        return TEL.StartMotionInfo;
    }

    public String GetStartTrackingInfo(){
        return TEL.StartTrackingInfo;
    }

    public String GetStopMotionInfo(){
        return TEL.StopMotionInfo;
    }

    public String GetAzStartMotionInfo(){
        return TEL.StartAzMotionInfo;
    }
    
    public String GetAzStopMotionInfo(){
        return TEL.StopAzMotionInfo;
    }

    public String GetElStartMotionInfo(){
        return TEL.StartElMotionInfo;
    }

    public String GetElStopMotionInfo(){
        return TEL.StopElMotionInfo;
    }

    public String GetElMoveUpInfo(){
        return TEL.ElMoveUpInfo;
    }

    public String GetElMoveDownInfo(){
        return TEL.ElMoveUpInfo;
    }

    public String GetAzMoveRightInfo(){
        return TEL.AzMoveRightInfo;
    }

    public String GetAzMoveLeftInfo(){
        return TEL.AzMoveLeftInfo;
    }

    public String GetEmergencyStopInfo(){
        return TEL.EmergencyStopInfo;
    }

    public String GetHomeDomeInfo(){
        return TEL.HomeDomeInfo;
    }

    public String GetOpenDomeInfo(){
        return TEL.OpenDomeInfo;
    }

    public String GetCloseDomeInfo(){
        return TEL.CloseDomeInfo;
    }

    public String GetStartPointingDomeInfo(){
        return TEL.StartPointingInfo;
    }

    public String GetStartParkingDomeInfo(){
        return TEL.StartParkingInfo;
    }
    
    public String GetStopDomeInfo(){
        return TEL.StopDomeInfo;
    }
    
    public String GetDomeWestInfo(){
        return TEL.DomeWestInfo;
    }
    
    public String GetDomeEastInfo(){
        return TEL.DomeEastInfo;
    }
    
    public String GetHomeTelInfo(){
        return TEL.HomeTelInfo;
    }

    // ultimo errore o numero totale di errori? Se lo metto come stringa posso avere entrambi
    public int GetErrorNumber(){ // adesso tiene il numero totale di errori ottenuti
        this.GEN.ErrorNumber = nErrors;
        return GEN.ErrorNumber;
    }
    
    public String GetErrorBuffer(){ // adesso tiene solo l'ultimo errore
        this.GEN.ErrorBuffer = "{"+errorText+"}";
        return GEN.ErrorBuffer;
    }
    // serve? Qual è il massimo?
    public boolean GetErrorBufferOutOfRange(){
        return GEN.ErrorBufferOutOfRange;
    }
    // serve saperlo?
    public int GetErrorBufferSize(){
        return GEN.ErrorBufferSize;
    }
    // come lo faccio?
    public int GetHeartBeat(){
        return GEN.HeartBeat;
    }
    


    
    


    //#region SET


    // SETTERS

    public void SetMotionType(final int value){ // 0 slew, 1 jog
        if (xAxisConnection){
            if (value == 0){
                tcsError(AsseX.SetSlewMode(X),1165);
                //tcsError(AsseX.SetMotVel(X, MotAZ.SlewVelocity),1166)
            }
            else if (value == 1){
                tcsError(AsseX.SetTrackMode(X),1167);
                //tcsError(AsseX.SetMotVel(X, MotAZ.JogVelocity),1166)
            }
        }
        if (yAxisConnection){
            if (value == 0){
                tcsError(AsseY.SetSlewMode(X),1265);
                //tcsError(AsseY.SetMotVel(X, MotEL.SlewVelocity),1266)
            }
            else if (value == 1){
                tcsError(AsseY.SetTrackMode(X),1267);
                //tcsError(AsseY.SetMotVel(X, MotEL.JogVelocity),1266)
            }
        }
        this.TEL.MotionType = value;
    }

    public void SetTrackingMode(){
        SetMotionType(1);
    }

    public void SetPointingMode(){
        SetMotionType(0);
    }

    public void SetCupolaTargetPosition(final double value){
        if (domeAxisConnection){
            tcsError(AsseCupola.SetAbsTargPos(X, value),1360);
            tcsError(AsseCupola.GetAbsTargPos(X),1351);
            this.CUP.CommandedAZ = AsseCupola.AbsTargPosAx[0];
        }
    }


    public void SetAzTelPosition(final double value){
        if (xAxisConnection){
            tcsError(AsseX.SetAbsTargPos(X, value),1160);
            tcsError(AsseX.GetAbsTargPos(X),1154);
            this.TEL.TargetAZ = AsseX.AbsTargPosAx[0];
        }
    }

    public void SetAzJogDirection(final int value){
        if (value == -1 || value == 1)
            this.MotAZ.JogDirection = value;
    }

    public void SetAzJogVelocity(final double value){
        boolean live = true;
        if (live) {
            AsseX.GetMotionMode(X);
            if (AsseX.MOTIONMODE[0] == 10){
                AsseX.SetMotVel(X, value);
                this.MotAZ.JogVelocity = value*MotAZ.JogDirection;
            }
        }
        else{
            this.MotAZ.JogVelocity = value*MotAZ.JogDirection;
        }
    }


    public void SetElTelPosition(final double value){
        if (yAxisConnection){
            tcsError(AsseY.SetAbsTargPos(X, value),1260);
            tcsError(AsseY.GetAbsTargPos(X),1254);
            this.TEL.TargetEL = AsseY.AbsTargPosAx[0];
        }
    }

    public void SetElJogDirection(final int value){
        if (value == -1 || value == 1)
            this.MotEL.JogDirection = value;
    }

    public void SetElJogVelocity(final double value){
        boolean live = true;
        if (live){
            AsseY.GetMotionMode(X);
            if (AsseY.MOTIONMODE[0] == 10){
                AsseY.SetMotVel(X, value);
                this.MotEL.JogVelocity = value*MotEL.JogDirection;
            }
        }
        else{
            this.MotEL.JogVelocity = value*MotEL.JogDirection;
        }
    }
    
    
    public void SetAzSlewVelocity(final double value){
        boolean live = true;
        int sign = 1;
        if (value < 0)
            sign = -1;

        if (live){
            AsseX.GetMotionMode(X);
            if (AsseX.MOTIONMODE[0] == 0){
                AsseX.SetMotVel(X, value*sign);
                this.MotAZ.SlewVelocity = value*sign;
            }
        }
        else{
            this.MotAZ.SlewVelocity = value*sign;       
        } 
    }

    public void SetAzSlewAcceleration(final double value){
        if (xAxisConnection){
            tcsError(AsseX.SetMotAcc(X, value),1161);
            tcsError(AsseX.GetMotAcc(X),1155);
            this.MotAZ.SlewAcceleration = AsseX.AccAx[0];
        }        
    }

    public void SetAzSlewDeceleration(final double value){
        if (xAxisConnection){
            tcsError(AsseX.SetMotDec(X, value),1162);
            tcsError(AsseX.GetMotDec(X),1158);
            this.MotAZ.SlewAcceleration = AsseX.DecAx[0];
        }
    }


    public void SetElSlewVelocity(final double value){
        boolean live = true;
        int sign = 1;
        if (value < 0)
            sign = -1;

        if (live){
            AsseY.GetMotionMode(X);
            if (AsseY.MOTIONMODE[0] == 0){
                AsseY.SetMotVel(X, value*sign);
                this.MotEL.SlewVelocity = value*sign;
            }
        }
        else{
            this.MotEL.SlewVelocity = value*sign;
        }
    }

    public void SetElSlewAcceleration(final double value){
        if (yAxisConnection){
            tcsError(AsseY.SetMotAcc(X, value),1261);
            tcsError(AsseY.GetMotAcc(X),1256);
            this.MotEL.SlewAcceleration = AsseY.AccAx[0];
        }
    }

    public void SetElSlewDeceleration(final double value){
        if (yAxisConnection){
            tcsError(AsseY.SetMotDec(X, value),1262);
            tcsError(AsseY.GetMotDec(X),1258);
            this.MotEL.SlewAcceleration = AsseY.AccAx[0];
        }
    }

    // MAX MIN

    public void SetAzMinAcc(final double value){
        if (xAxisConnection){
            AsseX.SetMaxMinAcc(X, MotAZ.MaxAcc, value);
            this.MotAZ.MinAcc = AsseX.MinAcc[0];
        }
    }

    public void SetAzMaxAcc(final double value){
        if (xAxisConnection){
            AsseX.SetMaxMinAcc(X, value, MotAZ.MinAcc);
            this.MotAZ.MaxAcc = value; //AsseX.MaxAcc[0];
        }
        
    }

    public void SetAzMinDec(final double value){ 
        this.MotAZ.MinDec = value;
    }

    public void SetAzMaxDec(final double value){
        this.MotAZ.MaxDec = value;
    }
    
    public void SetAzMinVel(final double value){
        if (xAxisConnection){
            AsseX.SetMaxMinVel(X, MotAZ.MaxVel, value);
            this.MotAZ.MinVel = AsseX.MinVel[0];
        }
    }

    public void SetAzMaxVel(final double value){
        if (xAxisConnection){
            AsseX.SetMaxMinVel(X, value, MotAZ.MinVel);
            this.MotAZ.MaxVel = AsseX.MaxVel[0];//AsseX.MaxVel[0];
        }
    }

    public void SetAzTelMinPos(final double value){
        if (xAxisConnection){
            AsseX.SetMaxMinPos(X, MotAZ.TelMaxPos, value);
            this.MotAZ.TelMinPos = AsseX.MinPos[0];
        }
    }

    public void SetAzTelMaxPos(final double value){
        if (xAxisConnection){
            AsseX.SetMaxMinPos(X, value, MotAZ.TelMinPos);
            this.MotAZ.TelMaxPos = AsseX.MaxPos[0];
        }
    }

    public void SetElMinAcc(final double value){
        if (yAxisConnection){
            AsseY.SetMaxMinAcc(X, MotEL.MaxAcc, value);
            this.MotEL.MinAcc = AsseY.MinAcc[0];
        }
    }

    public void SetElMaxAcc(final double value){
        if (yAxisConnection){
            AsseY.SetMaxMinAcc(X, value, MotEL.MinAcc);
            this.MotEL.MaxAcc = AsseY.MaxAcc[0];
        }
    }

    public void SetElMinDec(final double value){
        this.MotEL.MinDec = value;
    }

    public void SetElMaxDec(final double value){
        this.MotEL.MaxDec = value;
    }
    
    public void SetElMinVel(final double value){
        if (yAxisConnection){
            AsseY.SetMaxMinVel(X, MotEL.MaxVel, value);
            this.MotEL.MinVel = AsseY.MinVel[0];
        }
    }

    public void SetElMaxVel(final double value){
        if (yAxisConnection){
            AsseY.SetMaxMinVel(X, value, MotEL.MinVel);
            this.MotEL.MaxVel = AsseY.MaxVel[0];
        }
    }

    public void SetElTelMinPos(final double value){
        if (yAxisConnection){
            AsseY.SetMaxMinPos(X, MotEL.TelMaxPos, value);
            this.MotEL.TelMinPos = AsseY.MinPos[0];
        }
    }

    public void SetElTelMaxPos(final double value){
        if (yAxisConnection){
            AsseY.SetMaxMinPos(X, value, MotEL.TelMinPos);
            this.MotEL.TelMaxPos = AsseY.MaxPos[0];
        }
    }

    // OTHERS

    public void SetAzLsOpCwPos(final double value){
        this.MotAZ.LsOpCwPos = value;
    }

    public void SetAzLsOpCcwPos(final double value){
        this.MotAZ.LsOpCcwPos = value;
    }

    public void SetElLsOpLowPos(final double value){
        this.MotEL.LsOpLowPos = value;
    }

    public void SetElLsOpHighPos(final double value){
        this.MotEL.LsOpHighPos = value;
    }

    public void SetObserverLat(final double value){
        this.OSS.Latitudine = value; // oppure disaccoppiare: ObserverLat 
    }

    public void SetObserverLong(final double value){
        this.OSS.Longitudine = value;
    }

    public void SetObserverAlt(final int value){
        this.OSS.Altitudine = value;
    }

    public void SetTriggerAngleDome(final int value){
        this.CUP.TriggerAngleDome = value;
    }

    // TARGET

    public void SetTarget(final String value){
        if (value == ""){
            this.TEL.Target = new Target();
            this.TEL.Target.setRa(TEL.TargetRA); //setRa2000 ?
            this.TEL.Target.setDec(TEL.TargetDEC);  //setDec2000 ?
            this.TEL.Target.setEpoch(0);
            Trajectory();
        }
        else{
            this.TEL.Target = new Target(value);
            this.TEL.TargetName = value;
            //this.TEL.Target.setRa(21); //setRa2000 ?
            //this.TEL.Target.setDec(13);  //setDec2000 ?
            this.TEL.TargetRA2000 = TEL.Target.getRa2000(); // getRa2000()
            this.TEL.TargetDEC2000 = TEL.Target.getDec2000(); // getDec2000()

            Trajectory();
            
            //System.out.println(TEL.TargetRA);
            //System.out.println(TEL.TargetDEC);
        }
    }

    public void SetTarget(final double ra, final double dec){
        this.TEL.Target = new Target();
        this.TEL.Target.setRa2000(ra); //setRa2000 ?
        this.TEL.Target.setDec2000(dec);  //setDec2000 ?
        this.TEL.TargetRA2000 = ra; // getRa2000()
        this.TEL.TargetDEC2000 = dec;
        Trajectory();
    }

    public void SetTargetAz(final double value){
        this.TEL.TargetAZ = value;
    }

    public void SetTargetEl(final double value){
        this.TEL.TargetEL = value;
    }




    // PARKING

    public void SetAzParkingPosition(final double value){
        this.MotAZ.ParkPos = value;
    }

    public void SetElParkingPosition(final double value){
        this.MotEL.ParkPos = value;
    }

    public void SetCupolaParkingPosition(final double value){
        this.CUP.ParkPos = value;
    }
    

    




    //#region CMD



    // COMANDI OPCUA  
    
    // commandname: CommandGoLoaded; busy: FALSE; tstart: 1970-01-01-00:00:00.000; tstop: 1970-01-01-00:00:00.000; error:

    
    public void CmdGoLoaded(final boolean value){ // OK 
        if (value){
            try {
                goloadedTask.setTaskListener(new DefaultListener(TEL));
                taskExecutor.runTask(goloadedTask,  new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "GoLoadedInfo", "FALSE", 0L, 0L, "")
            this.TEL.GoLoadedInfo = "commandname: CommandGoLoaded; busy: FALSE; tstart: 0; tstop: 0; error:";

            //initHwStateMachine(LOADED)  
        }
    }

    public void CmdGoStandby(final boolean value){ // OK 
        if (value){
            try {
                taskExecutor.runTask(gostandbyTask,  new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "GoStandbyInfo", "FALSE", 0L, 0L, "")
            this.TEL.GoStandbyInfo = "commandname: CommandGoStandby; busy: FALSE; tstart: 0; tstop: 0; error:";


            /*long tStart = System.currentTimeMillis();
            this.TEL.GoStandbyInfo = "commandname: CommandGoStandby; busy: TRUE; tstart: "+tStart+"; tstop: 0; error:";
            if (getMcsStateMachine().isAcceptable(STANDBY)){
                getMcsStateMachine().transition(STANDBY);
                TEL.MachineState = mcsStateMachine.getCurrentState().value;
                TEL.MachineStatePhase=EHardwareStatePhase.ENTERING.ordinal();
                Sleep(5000);
                TEL.MachineStatePhase=EHardwareStatePhase.ACTIVE.ordinal();
                this.TEL.GoStandbyInfo = "commandname: CommandGoStandby busy: FALSE; tstart: "+tStart+"; tstop: "+System.currentTimeMillis()+"; error:";
            }
            else{
                logger.warn("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name);
                this.TEL.GoStandbyInfo = "commandname: CommandGoStandby; busy: FALSE; tstart: "+tStart+"; tstop: "+System.currentTimeMillis()+"; error: "+error;
            }
            //initHwStateMachine(STANDBY)*/
        }
    }

    public void CmdGoOnline(final boolean value){ // OK 
        if (value){

            try {
                taskExecutor.runTask(goonlineTask,  new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "GoOnlineInfo", "FALSE", 0L, 0L, "")
            this.TEL.GoOnlineInfo = "commandname: CommandGoOnline; busy: FALSE; tstart: 0; tstop: 0; error:";

            /*
            long tStart = System.currentTimeMillis();
            this.TEL.GoOnlineInfo = "commandname: CommandGoOnline; busy: TRUE; tstart: "+tStart+"; tstop: 0; error:";
            if (getMcsStateMachine().isAcceptable(ONLINE)){
                getMcsStateMachine().transition(ONLINE);
                TEL.MachineState = mcsStateMachine.getCurrentState().value;
                TEL.MachineStatePhase=EHardwareStatePhase.ENTERING.ordinal();

                // funzioni da eseguire nella transizione
                Sleep(5000);

                TEL.MachineStatePhase=EHardwareStatePhase.ACTIVE.ordinal();
                this.TEL.GoOnlineInfo = "commandname: CommandGoOnline busy: FALSE; tstart: "+tStart+"; tstop: "+System.currentTimeMillis()+"; error:";
            }
            else{
                logger.warn("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name);
                this.TEL.GoOnlineInfo = "commandname: CommandGoOnline; busy: FALSE; tstart: "+tStart+"; tstop: "+System.currentTimeMillis()+"; error: "+error;
            }
            //initHwStateMachine(ONLINE)*/

            /* 
            if (getMcsStateMachine().isAcceptable(ONLINE)){
                getMcsStateMachine().transition(ONLINE);
                TEL.MachineState = mcsStateMachine.getCurrentState().value;
                TEL.MachineStatePhase=EHardwareStatePhase.ENTERING.ordinal();
                Sleep(5000);
                TEL.MachineStatePhase=EHardwareStatePhase.ACTIVE.ordinal();
            }
            else{
                logger.warn("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name);
            }
                //initHwStateMachine(ONLINE)*/
        }
    }

    public void CmdGoMaintenance(final boolean value){ // OK 
        if (value){
            
            try {
                taskExecutor.runTask(gomaintenanceTask,  new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "GoMaintenanceInfo", "FALSE", 0L, 0L, "")
            this.TEL.GoMaintenanceInfo = "commandname: CommandGoMaintenance; busy: FALSE; tstart: 0; tstop: 0; error:";

            
            /*
            if (getMcsStateMachine().isAcceptable(MAINTENANCE)){
                getMcsStateMachine().transition(MAINTENANCE);
                TEL.MachineState = mcsStateMachine.getCurrentState().value;
                TEL.MachineStatePhase=EHardwareStatePhase.ENTERING.ordinal();
                Sleep(5000);
                TEL.MachineStatePhase=EHardwareStatePhase.ACTIVE.ordinal();
            }
            else{
                logger.warn("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name);
            }
            //initHwStateMachine(MAINTENANCE)*/
        }
    }

    
    public void CmdEnableAzMotors(final boolean value){ // OK 
        if (value && xAxisConnection){
            long tStart = System.currentTimeMillis();
            this.TEL.EnableAzMotorsInfo = "commandname: EnableAzMotors; busy: TRUE; tstart:"+tStart+"; tstop: 0; error:";
            tcsError(AsseX.SetMotorOn(X),1170);
            this.TEL.EnableAzMotorsInfo = "commandname: EnableAzMotors; busy: FALSE; tstart:"+tStart+"; tstop:"+System.currentTimeMillis()+"; error:"+errorBuffer;
            if (TemporaryErr == -1)
                this.TEL.EnableAzMotorsInfo = "commandname: EnableAzMotors; busy: FALSE; tstart: 0; tstop: 0; error:";
        }
    }

    public void CmdDisableAzMotors(final boolean value){ // OK 
        if (value && xAxisConnection){
            long tStart = System.currentTimeMillis();
            this.TEL.DisableAzMotorsInfo = "commandname: DisableAzMotors; busy: TRUE; tstart:"+tStart+"; tstop: 0; error:";
            if (AsseX.IsMoving(X) == 1){
                tcsError(AsseX.StopMove(X),1171);
            }
            tcsError(AsseX.SetMotorOff(X),1172);
            this.TEL.DisableAzMotorsInfo = "commandname: DisableAzMotors; busy: FALSE; tstart:"+tStart+"; tstop:"+System.currentTimeMillis()+"; error:"+errorBuffer;
            if (TemporaryErr == -1)
                this.TEL.DisableAzMotorsInfo = "commandname: DisableAzMotors; busy: FALSE; tstart: 0; tstop: 0; error:";
                
        }
    }

    public void CmdEnableElMotors(final boolean value){ // OK 
        if (value && yAxisConnection){
            long tStart = System.currentTimeMillis();
            this.TEL.EnableElMotorsInfo = "commandname: EnableElMotors; busy: TRUE; tstart:"+tStart+"; tstop: 0; error:";
            tcsError(AsseY.SetMotorOn(X),1270);
            this.TEL.EnableElMotorsInfo = "commandname: EnableElMotors; busy: FALSE; tstart:"+tStart+"; tstop:"+System.currentTimeMillis()+"; error:"+errorBuffer;
            if (TemporaryErr == -1)
                this.TEL.EnableElMotorsInfo = "commandname: EnableElMotors; busy: FALSE; tstart: 0; tstop: 0; error:";
        }
    }

    public void CmdDisableElMotors(final boolean value){ // OK 
        if (value && yAxisConnection){
            long tStart = System.currentTimeMillis();
            this.TEL.DisableElMotorsInfo = "commandname: DisableElMotors; busy: TRUE; tstart:"+tStart+"; tstop: 0; error:";
            if (AsseY.IsMoving(X) == 1){
                tcsError(AsseY.StopMove(X),1271);
            }
            tcsError(AsseY.SetMotorOff(X),1272);
            this.TEL.DisableElMotorsInfo = "commandname: DisableElMotors; busy: FALSE; tstart:"+tStart+"; tstop:"+System.currentTimeMillis()+"; error:"+errorBuffer;
            if (TemporaryErr == -1)
                this.TEL.DisableElMotorsInfo = "commandname: DisableElMotors; busy: FALSE; tstart: 0; tstop: 0; error:";
        }
    }

    // motion (slew) to position

    public void CmdMoveToPosition(final boolean value){ // OK
        if (value && xAxisConnection && yAxisConnection){
            try {
                taskExecutor.runTask(pointingTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "MoveToPositionInfo", "FALSE", 0L, 0L, "")
        }
    }
    
    public void CmdStopMotion(final boolean value){ // OK 
        if (value && xAxisConnection && yAxisConnection){
            try {
                taskExecutor.runTask(stopmotionTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StopMotionInfo", "FALSE", 0L, 0L, "")
        }
        else if(value && xAxisConnection){
            CmdStopAzMotion(value);
        }
        else if(value && yAxisConnection){
            CmdStopElMotion(value);
        }
    }

    
    public void CmdStopAzMotion(final boolean value){ // OK 
        if (value && xAxisConnection){
            try {
                taskExecutor.runTask(stopAZmotionTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StopAzMotionInfo", "FALSE", 0L, 0L, "")
        }
    }
    
    public void CmdStopElMotion(final boolean value){ // OK 
        if (value && yAxisConnection){
            try {
                taskExecutor.runTask(stopELmotionTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StopElMotionInfo", "FALSE", 0L, 0L, "")
        }
    }

    public void CmdElMoveUp(final boolean value){
        if (value && yAxisConnection){
            try {
                taskExecutor.runTask(elMoveUpTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "ElMoveUpInfo", "FALSE", 0L, 0L, "")
        }
    }

    public void CmdElMoveDown(final boolean value){
        if (value && yAxisConnection){
            try {
                taskExecutor.runTask(elMoveDownTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "ElMoveDownInfo", "FALSE", 0L, 0L, "")
        }
    }

    public void CmdAzMoveRight(final boolean value){
        if (value && xAxisConnection){
            try {
                taskExecutor.runTask(azMoveRightTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "AzMoveRightInfo", "FALSE", 0L, 0L, "")
        }
    }

    public void CmdAzMoveLeft(final boolean value){
        if (value && xAxisConnection){
            try {
                taskExecutor.runTask(azMoveLeftTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "AzMoveLeftInfo", "FALSE", 0L, 0L, "")
        }
    }





    public void CmdEmergencyStop(final boolean value){
        if (value){
            EmergencyStop();

            Sleep(300);

            EmergencyStop();

            Sleep(300);

            EmergencyStop();

        }
    }

    // PARKING

    public void CmdStartParking(final boolean value){   
        if (value){
            CmdStartAzParking(value);
            CmdStartElParking(value);
            CmdStartCupolaParking(value);
        }
    }

    public void CmdStopParking(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    public void CmdStartAzParking(final boolean value){
        if (value){
            SetAzTelPosition(MotAZ.ParkPos);
            CmdStartAzPointing(true);
        }
    }

    public void CmdStopAzParking(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    public void CmdStartElParking(final boolean value){
        if (value){
            SetElTelPosition(MotEL.ParkPos);
            CmdStartElPointing(true);
        }
    }

    public void CmdStopElParking(final boolean value){
        if (value)
            CmdStopMotion(value);
    }



    public void CmdStartTracking(final boolean value){
        if (value && xAxisConnection && yAxisConnection){
            try {
                taskExecutor.runTask(trackingTask,  new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StartTrackingInfo", "FALSE", 0L, 0L, "")
        }
    }

    public void CmdStopTracking(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    // fare il pointing come loop mantenendo un errore di posizione per controllo dopo il primo step (o anche no)
    public void CmdStartPointing(final boolean value){
        /*if (value){
            SetMotionType(0);
            CmdStartMotion(value);}*/
        if (value && xAxisConnection && yAxisConnection){
            try {
                taskExecutor.runTask(pointingAzTask, new DefaultListener(TEL));
                taskExecutor.runTask(pointingElTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StartPointingInfo", "FALSE", 0L, 0L, "")
        }
    }

    public void CmdStartAzPointing(final boolean value){
        /*if (value){
            SetMotionType(0);
            CmdStartMotion(value);}*/
        if (value && xAxisConnection){
            try {
                taskExecutor.runTask(pointingAzTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StartPointingInfo", "FALSE", 0L, 0L, "")
        }
    }

    public void CmdStartElPointing(final boolean value){
        /*if (value){
            SetMotionType(0);
            CmdStartMotion(value);}*/
        if (value && yAxisConnection){
            try {
                taskExecutor.runTask(pointingElTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StartPointingInfo", "FALSE", 0L, 0L, "")
        }
    }

    public void CmdStopPointing(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    public void CmdHomeTel(final boolean value){ // OK   
        if (value && xAxisConnection && yAxisConnection)
            try {
                taskExecutor.runTask(hometelTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "HomeTelInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdStopPointMotion(final boolean value){
        if (value && xAxisConnection && yAxisConnection)
            FermaMoto();
    }

    //  task apertura e chiusura cupola. aggiungere status movimento cupola come boolean nei get dell'icd. anche per l'inizializzazione, true o false sul set a zero della cupola.

    public void CmdOpenCupola(final boolean value){ // OK 
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(opendomeTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "OpenDomeInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdCloseCupola(final boolean value){ // OK 
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(closedomeTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "CloseDomeInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdStartCupolaPointing(final boolean value) { // OK 
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(startcupolapointingTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StartPointingDomeInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdStartCupolaParking(final boolean value) { // OK 
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(startcupolapointingTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StartParkingDomeInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdStopCupola(final boolean value){ // OK 
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(stopdomeTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "StopDomeInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdHomeCupola(final boolean value){ // OK 
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(homedomeTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //this.TEL.HomeDomeInfo = "commandname: HomeDomeInfo; busy: FALSE; tstart: 0; tstop: 0; error:"
            //setFieldCmd(this.TEL, "HomeDomeInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdCupolaOvest(final boolean value){ // OK 
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(domewestTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            
            //setFieldCmd(this.TEL, "DomeWestInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdCupolaEst(final boolean value){ // OK 
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(domeeastTask, new DefaultListener(TEL));
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            //setFieldCmd(this.TEL, "DomeEastInfo", "FALSE", 0L, 0L, "")
    }

    public void CmdHome(final boolean value){
        CmdHomeCupola(value);
        CmdHomeTel(value);
    }






    //#region TASK
    








    //#region T goloaded
    private final Task<Void> goloadedTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("GoLoadedInfo");
                
            if (getMcsStateMachine().isAcceptable(LOADED)){
                getMcsStateMachine().transition(LOADED);
                TEL.MachineState = mcsStateMachine.getCurrentState().value;
                TEL.MachineStatePhase=EHardwareStatePhase.ENTERING.ordinal();

                // Funzioni da eseguire in questa transizione
                int k=0;
                while (isInterrupted && k<30) {
                    //System.out.println("I'm a double callable");
                    System.out.println("Entering Loaded functions are running");
                    //listener.onWorking(null);
                    if(listener!=null)
                        listener.onWorking(null);
                    TimeUnit.SECONDS.sleep(1);
                    k++;
                }

                TEL.MachineStatePhase=EHardwareStatePhase.ACTIVE.ordinal();
                if(listener!=null)
                    listener.onDone(null);
                isInterrupted = false;
            }
            else{
                logger.warn("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name);
                if(listener!=null)
                    listener.onError(String.format("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name));
            }
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
          // Does this method need to be implemented?
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };
    
    //#region T gostandby
    private final Task<Void> gostandbyTask = new Task<Void>(){
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("GoStandbyInfo");
                
            if (getMcsStateMachine().isAcceptable(STANDBY)){
                getMcsStateMachine().transition(STANDBY);
                TEL.MachineState = mcsStateMachine.getCurrentState().value;
                TEL.MachineStatePhase=EHardwareStatePhase.ENTERING.ordinal();

                // Funzioni da eseguire in questa transizione
                int k=0;
                while (isInterrupted && k<30) {
                    //System.out.println("I'm a double callable");
                    System.out.println("Entering Standby functions are running");
                    //listener.onWorking(null);
                    if(listener!=null)
                        listener.onWorking(null);
                    TimeUnit.SECONDS.sleep(1);
                    k++;
                }

                TEL.MachineStatePhase=EHardwareStatePhase.ACTIVE.ordinal();
                if(listener!=null)
                    listener.onDone(null);
                isInterrupted = false;
            }
            else{
                logger.warn("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name);
                if(listener!=null)
                    listener.onError(String.format("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name));
            }
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    //#region T goonline
    private final Task<Void> goonlineTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("GoOnlineInfo");
                
            if (getMcsStateMachine().isAcceptable(ONLINE)){
                getMcsStateMachine().transition(ONLINE);
                TEL.MachineState = mcsStateMachine.getCurrentState().value;
                TEL.MachineStatePhase=EHardwareStatePhase.ENTERING.ordinal();

                // Funzioni da eseguire in questa transizione
                int k=0;
                while (isInterrupted && k<30) {
                    //System.out.println("I'm a double callable");
                    System.out.println("Entering Online functions are running");
                    //listener.onWorking(null);
                    if(listener!=null)
                        listener.onWorking(null);
                    TimeUnit.SECONDS.sleep(1);
                    k++;
                }

                TEL.MachineStatePhase=EHardwareStatePhase.ACTIVE.ordinal();
                if(listener!=null)
                    listener.onDone(null);
                isInterrupted = false;
            }
            else{
                logger.warn("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name);
                if(listener!=null)
                    listener.onError(String.format("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name));
            }
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    
    //#region T gomainten
    private final Task<Void> gomaintenanceTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("GoMaintenanceInfo");
                
            if (getMcsStateMachine().isAcceptable(MAINTENANCE)){
                getMcsStateMachine().transition(MAINTENANCE);
                TEL.MachineState = mcsStateMachine.getCurrentState().value;
                TEL.MachineStatePhase=EHardwareStatePhase.ENTERING.ordinal();

                // Funzioni da eseguire in questa transizione
                int k=0;
                while (isInterrupted && k<30) {
                    //System.out.println("I'm a double callable");
                    System.out.println("Entering Maintenance functions are running");
                    //listener.onWorking(null);
                    if(listener!=null)
                        listener.onWorking(null);
                    TimeUnit.SECONDS.sleep(1);
                    k++;
                }

                TEL.MachineStatePhase=EHardwareStatePhase.ACTIVE.ordinal();
                if(listener!=null)
                    listener.onDone(null);
                isInterrupted = false;
            }
            else{
                logger.warn("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name);
                if(listener!=null)
                    listener.onError(String.format("Transition not allowed from this state {}",mcsStateMachine.getCurrentState().name));
            }
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };


    //#region T homedome
    private final Task<Void> homedomeTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener; 
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("HomeDomeInfo");
                
            tcsError(AsseCupola.ExecProg("HOMECUP"),1387);
            
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(5000);
                AsseCupola.IsProgramRunning();
                //System.out.println("The dome going in Home position ... "+AsseCupola.isRunning);
                if (!AsseCupola.isRunning)
                    isInterrupted = false;
            }

            //CUP.StatusRotazione = 0;
            //CUP.Direzione = 0;

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    


    //#region T hometel
    private final Task<Void> hometelTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("HomeTelInfo");
                
            HomePosition();
            
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(10000);
                AsseX.IsProgramRunning();
                AsseY.IsProgramRunning();
                //System.out.println("Az and El are going in home position ... Az: "+AsseX.isRunning+", El: "+AsseY.isRunning);
                if (!AsseX.isRunning && !AsseY.isRunning)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };



    //#region T opendome
    private final Task<Void> opendomeTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("OpenDomeInfo");
                
            tcsError(AsseCupola.ExecProg("APRICUP"),1380);
                
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(3000);
                AsseCupola.IsProgramRunning();
                //System.out.println("Dome is opening ... ");
                if (AsseCupola.isRunning)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    

    //#region T closedome
    private final Task<Void> closedomeTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("CloseDomeInfo");
                
            tcsError(AsseCupola.ExecProg("CHIUDCUP"),1381);
                
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(3000);
                AsseCupola.IsProgramRunning();
                System.out.println("Dome is closing ... ");                
                if (AsseCupola.isRunning)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    


    //#region T domepoint
    private final Task<Void> startcupolapointingTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("StartPointingDomeInfo");
                
            PuntaCupola(TEL.TargetAZ);
                
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(5000);
                AsseCupola.IsProgramRunning();
                System.out.println("Dome is pointing ... ");
                if (AsseCupola.isRunning)
                    isInterrupted = false;
            }

            CUP.StatusRotazione = 0;
            CUP.Direzione = 0;

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };


    //#region T stopdome
    private final Task<Void> stopdomeTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener; 
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("StopDomeInfo");
                
            tcsError(AsseCupola.ExecProg("FERMACUP"),1382);
                
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(2000);
                AsseCupola.IsProgramRunning();
                System.out.println("Dome is stopping ... ");                
                if (AsseCupola.isRunning)
                    isInterrupted = false;
            }

            CUP.StatusRotazione = 0;
            CUP.Direzione = 0;

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };



    //#region T domewest
    private final Task<Void> domewestTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener; 
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("DomeWestInfo");
                
            tcsError(AsseCupola.ExecProg("SXCUP"),1383);
            CUP.StatusRotazione = 1;
            CUP.Direzione = 1;

            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(2000);
                AsseCupola.IsProgramRunning();
                System.out.println("Dome is moving to west ... ");
                if (AsseCupola.isRunning)
                    isInterrupted = false;
            }

            CUP.StatusRotazione = 0;
            CUP.Direzione = 0;

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    

    //#region T domeeast
    private final Task<Void> domeeastTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener; 
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("DomeEastInfo");
                
            tcsError(AsseCupola.ExecProg("DXCUP"),1384);
            CUP.StatusRotazione = 1;
            CUP.Direzione = -1;

            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(2000);
                AsseCupola.IsProgramRunning();
                System.out.println("Dome is moving to east ... ");
                if (AsseCupola.isRunning)
                    isInterrupted = false;
            }

            CUP.StatusRotazione = 0;
            CUP.Direzione = 0;

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };



    //#region T tracking
    private final Task<Void> trackingTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("StartTrackingInfo");
                
                if (TEL.MotionType != 1)
                    SetTrackingMode();

                IsAzTracking(true);
                IsElTracking(true);

                TraiettoriaX();
                TraiettoriaY();
                Trajectory();

            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(1000);

                UpdateInfoTarget();
                Tracking();

                AsseX.IsMoving(X);
                AsseY.IsMoving(X);
                //System.out.println("Tracking...");
                if (!AsseX.isMoving && !AsseY.isMoving)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    //#region T pointing Az

    // fare il pointing come loop mantenendo un errore di posizione per controllo dopo il primo step (o anche no)
    private final Task<Void> pointingAzTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("StartMotionInfo");
                
            if (TEL.MotionType != 0)
                SetPointingMode();
        
            StartPointingMotion(true,false);
            
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(1000); // da spostare alla fine del while?
                AsseX.IsMoving(X);
                if (!AsseX.isMoving){
                    IsAzPointing(false);
                    isInterrupted = false;
                }
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }
        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    //#region T pointing El


    private final Task<Void> pointingElTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("StartMotionInfo");
                
            if (TEL.MotionType != 0)
                SetPointingMode();
        
            StartPointingMotion(false,true);
            
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(1000); // da spostare alla fine del while?
                AsseY.IsMoving(X);
                if (!AsseY.isMoving){
                    IsElPointing(false);
                    isInterrupted = false;
                }
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }
        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    //#region T pointing
    private final Task<Void> pointingTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("MoveToPositionInfo");
                

            if (TEL.MotionType != 0)
                SetPointingMode();

            SetPointingMode();

            StartPointingMotion(true,true);
            
            
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(3000);
                AsseX.IsMoving(X);
                AsseY.IsMoving(X);
                System.out.println("Az and El are moving ... Az: "+AsseX.isMoving+", El: "+AsseY.isMoving);
                Sleep(3000);
                System.out.println("Az and El are moving ... Az: "+AsseX.isMoving+", El: "+AsseY.isMoving);

                if (!AsseX.isMoving && !AsseY.isMoving){
                    MovementDone();
                    isInterrupted = false;
                }
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };



    
    //#region T stop
    private final Task<Void> stopmotionTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener; 
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("StopMotionInfo");
                
            if (AsseX.IsMoving(X) == 1)
                tcsError(AsseX.StopMove(X),1171);
            if (AsseY.IsMoving(X) == 1)
                tcsError(AsseY.StopMove(X),1271);
            
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(100);
                AsseX.IsMoving(X);
                AsseY.IsMoving(X);
                //System.out.println("Az and El are stopping ... ");
                if (AsseX.isMoving && AsseY.isMoving)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };


    //#region T stop AZ
    private final Task<Void> stopAZmotionTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("StopAzMotionInfo");
                
            if (AsseX.IsMoving(X) == 1)
                tcsError(AsseX.StopMove(X),1171);
                
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(100);
                AsseX.IsMoving(X);
                //System.out.println("Az is stopping ... ");
                if (AsseX.isMoving)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };



    //#region T stop EL
    private final Task<Void> stopELmotionTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("StopElMotionInfo");
                
            if (AsseY.IsMoving(X) == 1)
                tcsError(AsseY.StopMove(X),1271);
                
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(100);
                AsseY.IsMoving(X);
                //System.out.println("El is stopping ... ");
                if (AsseY.isMoving)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };



    //#region T El Up
    private final Task<Void> elMoveUpTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("ElMoveUpInfo");
                
            if (TEL.MotionType != 1)
                SetTrackingMode();

            //AsseY.ExecProg("MUOVIDX")
            //AsseY.Move

            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(2000);
                AsseY.IsProgramRunning();
                if (AsseY.isRunning)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    //#region T El Down
    private final Task<Void> elMoveDownTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("ElMoveDownInfo");
                
            if (TEL.MotionType != 0)
                SetPointingMode();

            AsseY.ExecProg("MUOVISX");

            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(2000);
                AsseY.IsProgramRunning();
                if (AsseY.isRunning)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };

    //#region T Az Right
    private final Task<Void> azMoveRightTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("AzMoveRightInfo");
            
            if (TEL.MotionType != 0)
                SetPointingMode();

            AsseX.ExecProg("MUOVIDX");

            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(2000);
                AsseX.IsProgramRunning();
                if (AsseX.isRunning)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };


    //#region T Az Left
    private final Task<Void> azMoveLeftTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("AzMoveLeftInfo");
                
            if (TEL.MotionType != 0)
                SetPointingMode();

            AsseX.ExecProg("MUOVISX");

            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(2000);
                AsseX.IsProgramRunning();
                if (AsseX.isRunning)
                    isInterrupted = false;
            }

            if(listener!=null)
                listener.onDone(null);
            isInterrupted = false;
            
            
            return v;
        }

        @Override
        public void setVal(final Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            if(listener!=null)
                listener.onError("task interrupted");
        }

        @Override
        public void setTaskListener(final TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
           return null;
        }

        
    };






























    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    //#region FUNCTIONS

    // FUNZIONI COMPLESSE DA FARE
    public void InitStar(){}
    public void Puntamento(){}
    public void ComandiTastierino(){}
    
    public void IsAzPointing(boolean value){
        this.AzPointing = value;
    }
    public void IsAzTracking(boolean value){
        this.AzTracking = value;
    }
    public void IsElPointing(boolean value){
        this.ElPointing = value;
    }
    public void IsElTracking(boolean value){
        this.ElTracking = value;
    }

    public void TraiettoriaX(){
        double Vmax, Amax, Tnew = 0, Tmin = 0.1;
        double Told = 0, Vm, Dp;
        double Pi, Pf, Vi, Vf, Vs;
        double d, h, az, el, vaz, vel;
        double P0;
        
        // Telescopio
        GetTelInfoX();
        P0 = TEL.AZ * 3600;
        Vi = 0.0; // m_telescopeInfo.TrackVelX;
        Amax = TEL.MaxAccX;
        Vmax = MotAZ.MaxVel;
        
        // Oggetto
        // OggettoPuntato.CalcStarPos();
        // CALCOLO DELLA POSIZIONE OGGETTO
        Trajectory();

        Pf = TEL.TargetAZ * 3600;
        Vs = TEL.TargetVelAZ;
        Vf = Vs;
    
        do {
            Told = Tnew;
            double DP = Vs * Told; // ricalcolo le posizioni finali al tempo Told
            Dp = Pf - P0 + DP;
            double Dir = Dp / Math.abs(Dp);
            double A = Dir * Amax;
            Vm = Math.sqrt(A * Dp + 2.0 * (Vi * Vi + Vf * Vf));
            if (Vm > Vmax) Vm = Vmax;
            Vm = Dir * Vm;
            double T1 = (Vm - Vi) / A;
            double S1 = Vi * T1 + A * T1 * T1 / 2;
            double T3 = (Vm - Vf) / A;
            double S3 = Vm * T3 - A * T3 * T3 / 2;
            double S2 = Dp - S1 - S3;
            double T2 = S2 / Vm;
            if (T1 <= 0.0 || T2 <= 0.0 || T3 <= 0.0) break; // Puntare senza rampe;
            Tnew = T1 + T2 + T3;
        } while ((Tnew > Tmin) && (Math.abs(Tnew - Told) > Tmin));
    
        //TEL.SlewTimeX = Tnew;
        TEL.SlewVelX = Math.abs(Vm);
        CorreggiAZ(TEL.TargetAZ, TEL.TargetEL);
        TEL.TargetPosX = (180 * 3600 - (P0 + Dp)) + CostX[0];
    }

    public void TraiettoriaY(){
        double Vmax, Amax, Tnew = 0, Tmin = 0.1;
        double Told, Vm, Dp;
        double Pi, Pf, Vi, Vf;
        
        // Telescopio
        GetTelInfoY();
        Pi = TEL.PosY;
        Vi = 0.0; // m_telescopeInfo.TrackVelX;
        Amax = TEL.MaxAccY;
        Vmax = MotEL.MaxVel;
        
        // Oggetto
        // OggettoPuntato.CalcStarPos();
        // CALCOLO DELLA POSIZIONE OGGETTO
        Trajectory();

        Pf = TEL.TargetEL * 3600;
        Vf = TEL.TargetVelEL;
    
        do {
            Told = Tnew;
            double DP = Vf * Told; // ricalcolo le posizioni finali al tempo Told
            Dp = Pf - Pi + DP;
            double Dir = Dp / Math.abs(Dp);
            double A = Dir * Amax;
            Vm = Math.sqrt(A * Dp + 2.0 * (Vi * Vi + Vf * Vf));
            if (Vm > Vmax) Vm = Vmax;
            Vm = Dir * Vm;
            double T1 = (Vm - Vi) / A;
            double S1 = Vi * T1 + A * T1 * T1 / 2;
            double T3 = (Vm - Vf) / A;
            double S3 = Vm * T3 - A * T3 * T3 / 2;
            double S2 = Dp - S1 - S3;
            double T2 = S2 / Vm;
            if (T1 <= 0.0 || T2 <= 0.0 || T3 <= 0.0) break; // Puntare senza rampe;
            Tnew = T1 + T2 + T3;
        } while ((Tnew > Tmin) && (Math.abs(Tnew - Told) > Tmin));
    
        //TEL.SlewTimeY = Tnew;
        TEL.SlewVelY = Math.abs(Vm);
        CorreggiEL(TEL.TargetAZ, TEL.TargetEL);
        TEL.TargetPosY = ((Pi + Dp)) + CostY[0];
    }


    public void Target(){
        //this.TEL.Target = new Target(TEL.TargetName);
        double pmRa = 0;
        double pmDec = 0;
        double px = 0;
        double rv = 0;
        this.TEL.Target = new Target(TEL.TargetRA, TEL.TargetDEC, pmRa, pmDec, px, rv, "unknown"); //double ra2000, double dec2000, double pmRA, double pmDec, double px, double rv, String name
    }

    public void Target(double tRA, double tDec){
        //this.TEL.Target = new Target(TEL.TargetName);
        double pmRa = 0;
        double pmDec = 0;
        double px = 0;
        double rv = 0;
        this.TEL.Target = new Target(tRA, tDec, pmRa, pmDec, px, rv, "unknown"); //double ra2000, double dec2000, double pmRA, double pmDec, double px, double rv, String name
    }

    public void Trajectory(){
        ///*
        TrajectoryManager tm = new TrajectoryManager();
        String BASE_DIR = "/home/coloti/coloti-tcs/comm/src/main/java/coloti/tcs/trajectory/";
        String tpointFile = BASE_DIR + "/config/tpoint/astri1-tp.json";
        Observer obs = new Observer("COLOTI", 1,
                43.4016667,
                12.3763888,
                487);

        boolean wheaterconnection = CheckWheater(weatherdata);
        //double press = 1000.;
        //double temp = 15.0;
        //double hum = 0.5;
        //Weather atm = new Weather(press, temp, hum);
        Weather atm;
        if (wheaterconnection){
            atm = new Weather(OSS.Pressure*1000, OSS.Temperature, OSS.Humidity/100);
        }
        else{
            atm = new Weather(1000, 15, 0.5);
            System.out.println("Wheather not connected. Standard parameters used");
        }

        tm.setBaseDir(BASE_DIR);
        tm.assignToTelescope(ETelescopes.ASTRI1);
        tm.setAstroObserver(obs);
        tm.setWeather(atm);
        tm.setTpointFile(tpointFile);
        tm.setElevationLimit(10.);
        tm.setMinMoonDistance(10.);
        tm.setAcquisitionDuration(300.);
        tm.init();
        tm.setTarget(TEL.Target);
        double[] tra = new double[183];
        if (tm.isDay() && tm.isTargetValid()) {
            JulianDate jd = TimeUtil.getJDNow();
            tra = tm.generateTrajectory(jd);
            tm.printTrajectory();
        }
        
        this.tf = new TrajectoryFitter(tra);
        this.tf.fit(5); // posso provare polinomi diversi

        UpdateInfoTarget();

        // TimeUtil.getCurrentJuliandDay()

        //*/
    }

    public void InstantTrajectory(){  // serparare la traiettoria dal loop che aggiorna?

    }

    
    public void CorreggiAZ(double az, double el){
        double azr = az*D2R;
        double elr = el*D2R;
        double coel = Math.cos(elr);
        double daz = CostX[1];//+874.98 - 892.4*coel -256.*sin(elr) -24.*coel*sin(2*azr)
            //+ 36.*coel*sin(3.*azr);
        CostX[0] = daz;

    }

    public void CorreggiEL(double az, double el){
        double azr = az*D2R;
        double elr = el*D2R;
        double coel = Math.cos(elr);
        double del = CostY[1];//+874.98 - 892.4*coel -256.*sin(elr) -24.*coel*sin(2*azr) 
            //+ 36.*coel*sin(3.*azr);
        CostY[0] = del;
    }


    public void Tracking(){
        char[] buf = new char[80];
        char[] buf1 = new char[80];
        double DPX = 0;
        double DPY = 0;
        long val = 0;

        //OggettoPuntato.calcStarPos(); 
        // CALCOLO DELLA POSIZIONE OGGETTO
        
        // va sempre riletta la posizione per aggiornare Az ed El

        //CostX[2]=1.1;
		//CostY[2]=1.05;

        GetTelInfo();

        DPX = (TEL.AZ - TEL.TargetAZ) * 3600;
        DPY = (TEL.EL - TEL.TargetEL) * 3600;
        
        if (!AzPointing) { // Az ha finito di puntare
            if (AzTracking) { // Az è nel tracking
                if (Math.abs(DPX) > 1.0 && (SetTrackY == 1)) { 
                    if (DPX > 0.)
                        AsseX.SetMotVel(X, (1. * TEL.TargetVelAZ / CostX[2]));
                    if (DPX < 0.)
                        AsseX.SetMotVel(X, -1. * TEL.TargetVelAZ * CostY[2]);
                }
                else
                    AsseX.SetMotVel(X, MotAZ.JogDirection * TEL.TargetVelAZ);
            }
        }
        else{ // Az sta ancora puntando
            if (ElTracking) { // El è nel tracking
                if (Math.abs(DPX) >= 1.0) { 
                    if (DPX > 0.0)
                        AsseX.SetMotVel(X, (TEL.TargetVelAZ + DPX/1.5));
                    if (DPX < 0.0)
                        AsseX.SetMotVel(X, -1*(TEL.TargetVelAZ + Math.abs(DPX)/3));
                }
                else{
                    AsseX.SetMotVel(X, -1*TEL.TargetVelAZ);
                    SetPointX = 0;
                }
            }
        }    



        if (!ElPointing) { // El ha finito di puntare
            if (ElTracking){ // El è nel tracking
                if(Math.abs(DPY) >= 0.5 && (SetTrackX == 1)){
                    if (DPY > 0.0){
                        if(TEL.TargetVelEL<0.)
                            AsseY.SetMotVel(X, MotEL.JogDirection*TEL.TargetVelEL*1.1);
                        else
                            AsseY.SetMotVel(X, MotEL.JogDirection*TEL.TargetVelEL/1.1);
                    }
                    if (DPY < 0.0){
                        if(TEL.TargetVelEL < 0.0)
                            AsseY.SetMotVel(X, MotEL.JogDirection*TEL.TargetVelEL/1.1);
                        else
                            AsseY.SetMotVel(X, MotEL.JogDirection*TEL.TargetVelEL*1.1);
                    }
                }
                else{
                    AsseY.SetMotVel(X, TEL.TargetVelEL);
                }
            }
        }
        else{ // El sta ancora puntando
            if (AzTracking){ // Az è nel tracking
                if (Math.abs(DPY) >= 0.5){
                    if (DPY > 0.0){
                        if (TEL.TargetVelEL < 0.0)
                            AsseY.SetMotVel(X, MotEL.JogDirection*TEL.TargetVelEL*1.15);
                        else
                            AsseY.SetMotVel(X, MotEL.JogDirection*TEL.TargetVelEL/1.15);
                    }
                    if (DPY < 0.0){
                        if (TEL.TargetVelEL < 0.0)
                            AsseY.SetMotVel(X, MotEL.JogDirection*TEL.TargetVelEL/1.15);
                        else
                            AsseY.SetMotVel(X, MotEL.JogDirection*TEL.TargetVelEL*1.15);
                    }
                }
                else{
                    AsseY.SetMotVel(X, TEL.TargetVelEL);
                    SetPointY = 0;
                }
            }
        }
    }

    public void UpdateInfoTarget(){
        double timeJDnow = TimeUtil.getCurrentJuliandDay();
        this.TEL.TargetAZ = tf.Az(timeJDnow);
        this.TEL.TargetVelAZ = tf.velocityAz(timeJDnow);
        this.TEL.TargetEL = tf.El(timeJDnow);
        this.TEL.TargetVelEL = tf.velocityEl(timeJDnow);
        System.out.println("Target Az and El setted: ");
        System.out.println("Az: "+TEL.TargetAZ);
        System.out.println("El: "+TEL.TargetEL);
    }

    public boolean CheckWheater(WeatherData wd){
        boolean connected = wd.connected;
        if (connected){
            Object object[] = wd.ExtractAllData();
            this.OSS.Pressure = (double) object[3];
            this.OSS.Temperature = (double) object[5];
            this.OSS.Humidity = (double) object[9];
        }
        return connected;
    }
    

    public void CoordinatesConversion(double ra, double dec){
        double conversione = 1.0;
        SetAzTelPosition(dec*conversione);
        SetElTelPosition(ra*conversione);
    }
    

    public void FermaMoto(){  // era dentro setta pos home
        AsseX.CommandMot("PS");
        AsseY.CommandMot("PS");

        AsseX.StopMove(X);
        AsseY.StopMove(X);
    }
    
    public void StartPointingMotion(boolean AzAxis, boolean ElAxis){
        this.TEL.TelIsMoving = true;
        if(AzAxis && AsseX.IsMoving(X) == 1){
            tcsError(AsseX.StopMove(X),1171);
            Sleep(100);
        }
        if(ElAxis && AsseY.IsMoving(X) == 1){
            tcsError(AsseY.StopMove(X),1271);
            Sleep(100);
        }
        if(TEL.MotionType == 0){ // slew
            // accelerazione necessaria? Non basta nell'inizializzazione?
            if (AzAxis){
                tcsError(AsseX.SetMotAcc(X, MotAZ.MaxAcc),1161);
                tcsError(AsseX.SetMotDec(X, MotAZ.MaxAcc),1162);
            }
            if (ElAxis){
                tcsError(AsseY.SetMotAcc(X, MotEL.MaxAcc),1261);
                tcsError(AsseY.SetMotDec(X, MotEL.MaxAcc),1262);
            }

            if (AzAxis){
                tcsError(AsseX.Move(X, TEL.TargetAZ, MotAZ.SlewVelocity),1190); // TEL.SlewVelX
            }
            if (ElAxis){
                tcsError(AsseY.Move(X, TEL.TargetEL, MotEL.SlewVelocity),1290); // TEL.SlewVelY
            }
        }
        
        /*if(TEL.MotionType == 1){
            AsseX.SetMotAcc(X, MotAZ.JogVelocity);
            //AsseX.SetMotDec(X, MotAZ.JogVelocity);
            AsseY.SetMotAcc(X, MotEL.JogVelocity);
            //AsseY.SetMotDec(X, MotEL.JogVelocity);

            AsseX.Move(X, MotAZ.TelPosition, MotAZ.JogVelocity); // TEL.SlewVelX
            AsseY.Move(X, MotEL.TelPosition, MotEL.JogVelocity); // TEL.SlewVelY
        }*/
    }

    public void MovementDone(){
        this.TEL.TelIsMoving = false;
    }

    public void WaitMovement(int milliseconds){
        while(TEL.TelIsMoving){
            Sleep(milliseconds);
        }
        System.out.println("Telescopio arrivato");
    }

    public void WaitMovement(){
        Sleep(2000);
        while(TEL.TelIsMoving){
            Sleep(1000);
        }
        System.out.println("Telescopio arrivato");
    }

    /*
    public void Controllore(){} // vari, utilizza funzione consolle
    public void PuntamentoCoordinate(){}
    public void PuntamentoMinimo(){}
    public void UpdateTime(){}
    public void UpdatePos(){}
    public void Inizializzazione(){}
    public void SettaTempo(){}
    public void SettaMeteo(){}
    public void FormatCoord(){}
    public void VerificaVisibilitaAstro(){}
    public void PuntamentoCatalogo(){}
    public void TelescopioJoystic(){}
    public void TelescopioSettaZeroStar(){} */

    public void VecchioEseguiPuntamento(){ 
        final int setTrackCup = 0;
        final int setTrackY = 0;
        final int setTracX = 0;
        final int noCentered = 0;
        if (AsseX.CommStatus && AsseY.CommStatus){
            // killertimer (2)
            AsseX.StopMove(X);
            if (AsseX.IsMoving(X) == 1)
                Sleep(200) ;
            AsseY.StopMove(X);
            if (AsseY.IsMoving(X) == 1)
                Sleep(200) ;

            AsseX.SetSlewMode(X);
            AsseY.SetSlewMode(X);
            AsseX.SetMotAcc(X, MotAZ.MaxAcc);
            AsseX.SetMotDec(X, MotAZ.MaxAcc);
            AsseY.SetMotAcc(X, MotEL.MaxAcc);
            AsseY.SetMotDec(X, MotEL.MaxAcc);
            AsseX.Move(X, TEL.TargetPosX, TEL.SlewVelX);
            AsseY.Move(X, TEL.TargetPosY, TEL.SlewVelY);

            Sleep(300);
            PuntaCupola();

        }
    }

    // incompleto
    public void SetZeroStar(){
        double valAZ, valEL;
        //calcolo astronomico
        Trajectory();
        //if (TEL.MonType == 0){}
        valAZ = (180 - TEL.TargetAZ)*3600;
        valEL = TEL.TargetEL*3600;
        AsseX.GetMotEncPos(X);
        AsseY.GetMotEncPos(X);
        this.TEL.PosX = AsseX.EncoderPos[0];
        this.TEL.PosY = AsseY.EncoderPos[0];
        this.DPX = TEL.PosX - valAZ; //desired position x
        this.DPY = TEL.PosY - valEL;

        AsseX.SetAxisZeroPos(X, valAZ);
        AsseY.SetAxisZeroPos(X, valEL);
        //AsseCupola.SetAxisZeroPos(X, valAZ);

        //modifica zeri dat file
    }

    // Inizializzazione assi, procedura home telescope position
    public void HomePosition(){ // OK
        long ValoX = 0, ValoY = 0;
        
        // aprire file Zeri.dat e prendere ValoX e ValoY
        /* 
        String filezeri = "zeri.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filezeri))) {
            String line = br.readLine();
            if (line != null) {
                ValoX = Integer.parseInt(line.trim()); // Double.parseDouble
            }
            line = br.readLine();
            if (line != null) {
                ValoY = Integer.parseInt(line.trim()); // Double.parseDouble
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        final long ZeroX=this.PZ.ZeroX;
        final long ZeroY=this.PZ.ZeroY; // non sono assegnati, vengono dal file?

        ValoX += (long) (ZeroX*3600*AsseX.CONVFACTOR[0] + 0.5 - 30*AsseX.CONVFACTOR[0]);
        AsseX.CommandArray("AVSE", 8, (int) ValoX);
        ValoX = AsseX.VALUECR;
        tcsError(AsseX.ExecProg("HOMEX"),1192);

        //ValoY += (long)(ZeroY*3600*AsseY.CONVFACTOR[0]-60.*AsseY.CONVFACTOR[0]+0.5);
        //AsseY.CommandArray("AVSE", 8, (int) ValoY);
        //ValoY = AsseY.VALUECR;
        //tcsError(AsseY.ExecProg("HOMEX"),1291);
    }


    public void SetZeroFromFile(){
        final int valx = 1, valy = 1, valc = 1;
        // input da file lastpos.dat
        final byte[] istruzione = AsseX.sbld("SXZP");
        AsseX.CommandSet(istruzione,valx);
        Sleep(100);
        AsseY.CommandSet(istruzione,valy);
        Sleep(100);
        AsseCupola.CommandSet(istruzione,valc);
    }



    /* 

    // TELESCOPIO
    public void SetTelTrackVel(){}
    public void TelescoFermaMoto(){}
    public void OnTelescoStartMotoOrario(){}
    public void OnTelescoStopInseguimento(){}
    public void OnTelescoInitAssi(){}
    public void OnTelescoInitAsseX(){}
    public void OnTelescoInitAsseY(){}
    public void OnTelescoInitAsseZ(){}
    public void OnTelescoParametri(){}
    public void OnTelescoVerificap(){}

    // altro
    public void OnPuntamentoPianeti(){}
    public void OnExecuteRemote(){}   
    public void OnSetGPStime(){}
    public void OnGetGPStime(){}
    public void OnMostraDatiMeteo(){}
    public void OnExternalObj(){}
    public void ReadCostPun(){}
    public void OnSettaZeroTelFile(){}
    public void CorreggiAZ(){}
    public void CorreggiEL(){}

    */

    // FUNZIONI come apm

    // INCOMPLETO
    public void Exit(){
        // ofstream lastopos("lastpos.dat")
        long ValoX;
        final long ValoY;
        long ValoC;
        int err;
        // KillTimer(1);
        // KillTimer(2);

        if (AsseX.CommStatus){
            if (AsseX.IsMoving(X) == 1){
                AsseX.StopMove(X);
                // update tcs log
            }
            AsseX.SetMotorOff(X);
            err = AsseX.GetMotEncPos(X);
            ValoX = AsseX.VALUECR;
            AsseX.CloseComm();
        }

        if (AsseY.CommStatus){
            if (AsseY.IsMoving(X) == 1){
                AsseY.StopMove(X);
                // update tcs log
            }
            AsseX.SetMotorOff(X);
            err = AsseX.GetMotEncPos(X);
            ValoX = AsseX.VALUECR;
            AsseX.CloseComm();
        }

        if (AsseCupola.CommStatus){
            if (CUP.StatusApertura == 1){
                CupolaChiusura();
                Sleep(8000);
            }
            
            err = AsseCupola.GetMotEncPos(X);
            ValoC = AsseCupola.VALUECR;
        }

        // AGGIORNARE LASTPOS E CONNESSIONE REMOTA 

        Sleep(1000);
    }




    // SEMPLICI GET FATTI

    public void GetTelInfo(){
        //double ra;
        GetTelInfoX();
        GetTelInfoY();
        
        
            //double[] hadec = AzEl2HaDec(TEL.AZ, TEL.EL, OSS.Latitudine);
            //TEL.H = hadec[0];
            //TEL.DEC = hadec[1];
            //double teltimegetlsathour = 0; // ???
            //ra = teltimegetlsathour - TEL.H;
            //if (ra < 0.0)
            //    ra += 24.0;
            //if (ra > 24.0)
            //    ra -= 24.0;
            //TEL.RA = ra;
            //double[] azel = HaDec2AzEl(TEL.H, TEL.DEC, OSS.Latitudine);
            //TEL.AZ = azel[0];
            //TEL.EL = azel[1];
        
    }

    public void GetTelInfoX(){
        double PosX;
        long valo;
        int err;

        if (AsseX.CommStatus){
            // caso TelMonTipo = 0
            
            err = AsseX.GetMotEncPos(X);
            valo = AsseX.VALUECR;
            PosX = valo/AsseX.CONVFACTOR[0] - CostX[0];
            TEL.PosX = PosX;
            PosX = (180*3600.0 - PosX);
            TEL.AZ = PosX/3600.0;
        
            err = AsseX.GetMotVel(X);
            TEL.SlewVelX = AsseX.VelAx[0];

            err = AsseX.GetMotAcc(X);
            TEL.AccX = AsseX.AccAx[0];
        }
    }

    public void GetTelInfoY(){
        double PosY;
        long valo;
        int err;

        if (AsseY.CommStatus){
            err = AsseY.GetMotEncPos(X);
            valo = AsseY.VALUECR;
            PosY = valo/AsseY.CONVFACTOR[0] - CostY[0];
            TEL.PosY = PosY;
            PosY = (180*3600 - PosY);
            TEL.EL = PosY/3600.0;

            err = AsseY.GetMotVel(X);
            TEL.SlewVelY = AsseY.VelAx[0];

            err = AsseY.GetMotAcc(X);
            TEL.AccY = AsseY.AccAx[0];
        }
    }

    public void GetTelInfoZ(){
        double PosZ;
        long valo;
        int err;

        if (AsseZ.CommStatus){
            err = AsseZ.GetMotEncPos(X);
            valo = AsseZ.VALUECR;
            PosZ = valo/AsseZ.CONVFACTOR[0];
            TEL.PosZ = PosZ;
            TEL.PA = PosZ/3600.0;

            err = AsseZ.GetMotVel(X);
            TEL.SlewVelZ = AsseZ.VelAx[0];

            err = AsseZ.GetMotAcc(X);
            TEL.AccZ = AsseZ.AccAx[0];
        }
    }


    // CUPOLA

    public int CupolaApertura(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("APRICUP");
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaChiusura(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("CHIUDCUP");
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaOvest(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("SXCUP");
            CUP.StatusRotazione = 1;
            CUP.Direzione = 1;
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaEst(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("DXCUP");
            CUP.StatusRotazione = 1;
            CUP.Direzione = -1;
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaSetZero(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("HOMECUP");
        }
        else{
            Err = 0;
        }
        return Err;
    }

    /*public int CupolaVai(double az){

        if (AsseCupola.CommStatus){
            int azi = (int) (3600*az*AsseCupola.CONVFACTOR[0]);
            int Err;
            byte[] command = AsseCupola.sbld("AVSE");
            AsseCupola.CommandArray(command, 10, azi);
		    Err = AsseCupola.ExecProg("PUNTA");
            if (Err != -1){
                return Err;
            }
        }
        CUP.StatusRotazione = 1;
        CUP.Direzione = -1;

        return -1;
    }
    */

    public int FermaCupola(){
        if (AsseCupola.CommStatus){
            tcsError(AsseCupola.ExecProg("FERMACUP"),1382);
            this.CUP.StatusRotazione = 0;
            this.CUP.Direzione = 0;
        }
        return -1;
    }

    public int PuntaCupola(final double azObj){
        if (AsseCupola.CommStatus){
            final int az = (int) (3600*azObj*AsseCupola.CONVFACTOR[0]);
            final byte[] command = AsseCupola.sbld("AVSE");
            AsseCupola.CommandArray(command, 10, az);
            tcsError(AsseCupola.ExecProg("PUNTA"),1386);
        }
        this.CUP.StatusRotazione = 1;
        this.CUP.Direzione = -1;
        return -1;
    }

    public int PuntaCupola(){
        if (AsseCupola.CommStatus){
            final int az = (int) (3600*CUP.CommandedAZ*AsseCupola.CONVFACTOR[0]);
            final byte[] command = AsseCupola.sbld("AVSE");
            AsseCupola.CommandArray(command, 10, az);
            tcsError(AsseCupola.ExecProg("PUNTA"),1386);
        }
        this.CUP.StatusRotazione = 1;
        this.CUP.Direzione = -1;
        return -1;
    }

    /* public int PuntaCupolaAngle(double angle){
        int az = (int) (3600*angle*AsseCupola.CONVFACTOR[0]);
        int Err;
        byte[] command = AsseCupola.sbld("AVSE");
        AsseCupola.CommandArray(command, 10, az);
        Err = AsseCupola.ExecProg("PUNTA");
        if (Err != -1){
            return Err;
        }
        return -1;
    }
    */

    public void GetCupolaInfo(){
        long valo;
        if (AsseCupola.CommStatus){
            AsseCupola.GetMotEncPos(X);
            valo = AsseCupola.VALUECR;
            CUP.Pos = valo/AsseCupola.CONVFACTOR[0];
            CUP.AZ = CUP.Pos/3600.0;
            if (CUP.AZ >= 360.0)
                CUP.AZ -= 360.0;
        }
    }

    public void CupolaInseguimento(){}

    public void EmergencyStop(){

            if (xAxisConnection)
                AsseX.StopMove(X);
            if (yAxisConnection)
                AsseY.StopMove(X);
            if (domeAxisConnection)
                FermaCupola();


    }






    //#region TASTIERINO

    public void print(String string){
        System.out.println(string);
    }

    ActionListener actionMoveUP = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Going up...");
            if (yAxisConnection)
                CmdElMoveUp(true);
            else
                print("EL not connected");
        }
    };

    ActionListener actionMoveDOWN = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Going down...");
            if (yAxisConnection)
                CmdElMoveDown(true);
            else
                print("EL not connected");
        }
    };

    ActionListener actionMoveLEFT = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Going left...");
            if (xAxisConnection)
                CmdAzMoveLeft(true);
            else
                print("AZ not connected");
        }
    };

    ActionListener actionMoveRIGHT = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Going right...");
            if (xAxisConnection)
                CmdAzMoveRight(true);
            else
                print("AZ not connected");
        }
    };

    ActionListener actionstopEL = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (yAxisConnection)
                CmdStopElMotion(true);
            print("done.");
        }
    };

    ActionListener actionstopAZ = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (xAxisConnection)
                CmdStopAzMotion(true);
            print("done.");
        }
    };

    ActionListener actionSTOP = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Stop movements...");
            CmdEmergencyStop(true);
        }
    };




    ActionListener actionSlowSpeed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Set slow speed");
            if (xAxisConnection)
                SetAzSlewVelocity(60);
            else
                print("AZ not connected");
            if (yAxisConnection)
                SetElSlewVelocity(60);
            else
                print("EL not connected");
        }
    };
    ActionListener actionMediumSpeed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Set medium speed");
            if (xAxisConnection)
                SetAzSlewVelocity(150);
            else
                print("AZ not connected");
            if (yAxisConnection)
                SetElSlewVelocity(150);
            else
                print("EL not connected");
        }
    };
    ActionListener actionFastSpeed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Set fast speed");
            if (xAxisConnection)
                SetAzSlewVelocity(500); //180
            else
                print("AZ not connected");
            if (yAxisConnection)
                SetElSlewVelocity(500); //180
            else
                print("EL not connected");
        }
    };






    ActionListener actionDomeEAST = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Dome going east...");
            if (domeAxisConnection)
                CmdCupolaEst(true);
            else
                print("DOME not connected");
        }
    };

    ActionListener actionDomeWEST = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Dome going west...");
            if (domeAxisConnection)
                CmdCupolaOvest(true);
            else
                print("DOME not connected");
        }
    };

    ActionListener actionDomeStop = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (domeAxisConnection)
                CmdStopCupola(true);
            print("done.");
        }
    };

    ActionListener actionHome = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //CmdHomeTel(true);
            CmdHomeCupola(true);
            print("Dome home position procedure...");
        }
    };
    
    ActionListener actionHomeTel = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            CmdHomeTel(true);
            //CmdHomeCupola(true);
            print("Telescope home position procedure...");
        }
    };
    






    //#region MAIN




    public static void main(final String[] a){ // sudo chmod 777 /dev/ttyS0     sudo chmod 777 /dev/ttyUSB0
        //System.out.println("\nHello World\n");

        boolean conditionTest = false;

        // inizializzazione
        TCS tcs = new TCS();
        if (tcs.xAxisConnection){
            System.out.println("Before Connection: ");
            System.out.println("comm status: "+tcs.AsseX.CommStatus);
            System.out.print("Encoder Res: ");
            System.out.println(tcs.AsseX.ENCODERRES[0]);  // 18000
            System.out.print("Convfactor: ");
            System.out.println(tcs.AsseX.CONVFACTOR[0]); // 20
            System.out.print("Max Velocity: ");
            System.out.println(tcs.AsseX.MaxVel[0]);
            System.out.print("Max Absolute Velocity: ");
            System.out.println(tcs.AsseX.MaxAbsVel[0]);
            System.out.print("Max Acceleration: ");
            System.out.println(tcs.AsseX.MaxAcc[0]);
            System.out.print("Max Absolute Acceleration: ");
            System.out.println(tcs.AsseX.MaxAbsAcc[0]);

            // connect and initialization
            tcs.connect();
            
            System.out.println("-----------------------------");

            System.out.println("After Connection: ");
            System.out.println("comm status: "+tcs.AsseX.CommStatus);
            System.out.print("Encoder Res: ");
            System.out.println(tcs.AsseX.ENCODERRES[0]);  // 18000
            System.out.print("Convfactor: ");
            System.out.println(tcs.AsseX.CONVFACTOR[0]); // 20
            System.out.print("Max Velocity: ");
            System.out.println(tcs.AsseX.MaxVel[0]);
            System.out.print("Max Absolute Velocity: ");
            System.out.println(tcs.AsseX.MaxAbsVel[0]);
            System.out.print("Max Acceleration: ");
            System.out.println(tcs.AsseX.MaxAcc[0]);
            System.out.print("Max Absolute Acceleration: ");
            System.out.println(tcs.AsseX.MaxAbsAcc[0]);


            System.out.println("-----------------------------");

            tcs.AsseX.GetMotMaxMinPos("X");
            System.out.println("Max Min Pos: "+tcs.AsseX.MaxPos[0]+" , "+tcs.AsseX.MinPos[0]);
        }

        if (tcs.yAxisConnection){
            System.out.println("Before Connection: ");
            System.out.println("comm status: "+tcs.AsseY.CommStatus);
            System.out.print("Encoder Res: ");
            System.out.println(tcs.AsseY.ENCODERRES[0]);  // 18000
            System.out.print("Convfactor: ");
            System.out.println(tcs.AsseY.CONVFACTOR[0]); // 20
            System.out.print("Max Velocity: ");
            System.out.println(tcs.AsseY.MaxVel[0]);
            System.out.print("Max Absolute Velocity: ");
            System.out.println(tcs.AsseY.MaxAbsVel[0]);
            System.out.print("Max Acceleration: ");
            System.out.println(tcs.AsseY.MaxAcc[0]);
            System.out.print("Max Absolute Acceleration: ");
            System.out.println(tcs.AsseY.MaxAbsAcc[0]);

            // connect and initialization
            tcs.connect();
            
            System.out.println("-----------------------------");

            System.out.println("After Connection: ");
            System.out.println("comm status: "+tcs.AsseY.CommStatus);
            System.out.print("Encoder Res: ");
            System.out.println(tcs.AsseY.ENCODERRES[0]);  // 18000
            System.out.print("Convfactor: ");
            System.out.println(tcs.AsseY.CONVFACTOR[0]); // 20
            System.out.print("Max Velocity: ");
            System.out.println(tcs.AsseY.MaxVel[0]);
            System.out.print("Max Absolute Velocity: ");
            System.out.println(tcs.AsseY.MaxAbsVel[0]);
            System.out.print("Max Acceleration: ");
            System.out.println(tcs.AsseY.MaxAcc[0]);
            System.out.print("Max Absolute Acceleration: ");
            System.out.println(tcs.AsseY.MaxAbsAcc[0]);


            System.out.println("-----------------------------");

            tcs.AsseY.GetMotMaxMinPos("X");
            System.out.println("Max Min Pos: "+tcs.AsseY.MaxPos[0]+" , "+tcs.AsseY.MinPos[0]);

        }




    

        // settare orario (in automatico?)

        // apertura cupola
        //tcs.CmdCloseCupola(true);

        if (conditionTest){
            System.out.println("----------------TCS dome position-------------------");
            System.out.println(tcs.GetCupolaPosition());
            System.out.println("------------------------------------");
        }


        // home position di telescopio e cupola
        //tcs.CmdHome(true); // ha al suo interno sia cupola che telescopio

        // settare una stella luminosa target per poi fare gli zeri
        if (conditionTest){
            tcs.SetTarget("HIP69673"); // prende le coordinate in J2000  "HIP69673"
            System.out.println(tcs.TEL.TargetName);
            System.out.println(tcs.TEL.TargetRA2000);
            System.out.println(tcs.TEL.TargetDEC2000);
        }

        // muoversi al target
    

        if (conditionTest){
            tcs.CmdMoveToPosition(true); 
        /*
         * manda il task (pointingTask) che muove azimuth ed elevazione,
         * dove viene utilizzata la funzione StartMotion.
         * In input vuole Azimuth ed Elevazione, quindi bisogna prima settarle trasformando da J2000
        */
        // controllare che sia arrivato
            System.out.println("waiting...");
            tcs.Sleep(10000);
            tcs.WaitMovement(2000);

        // aggiustamento posizione
            tcs.CmdMoveToPosition(true); 
            tcs.WaitMovement(500);
        }

        // centrare il target con il tastierino
        if (conditionTest){
            ArrowPadFrame apframe = new ArrowPadFrame(new JFrame());
            apframe.SetButtonHome(tcs.actionHome);
            apframe.SetButtonHomeTel(tcs.actionHomeTel);
            apframe.SetButtonUP(tcs.actionMoveUP, tcs.actionstopEL);
            apframe.SetButtonDOWN(tcs.actionMoveDOWN, tcs.actionstopEL);
            apframe.SetButtonLEFT(tcs.actionMoveLEFT, tcs.actionstopAZ);
            apframe.SetButtonRIGHT(tcs.actionMoveRIGHT, tcs.actionstopAZ);
            apframe.SetButtonDomeEAST(tcs.actionDomeEAST, tcs.actionDomeStop);
            apframe.SetButtonDomeWEST(tcs.actionDomeWEST, tcs.actionDomeStop);
            apframe.SetButtonSTOP(tcs.actionSTOP);
            apframe.SetSlowSpeed(tcs.actionSlowSpeed);
            apframe.SetMediumSpeed(tcs.actionMediumSpeed);
            apframe.SetFastSpeed(tcs.actionFastSpeed);
            apframe.Show();
        }

        

        // settare gli zeri sulla stella nota 
        if (conditionTest)
            tcs.SetZeroStar();

        // settare un target per l'osservazione
        if (conditionTest){
        tcs.SetTarget("M5");
            System.out.println(tcs.TEL.TargetName);
            System.out.println(tcs.TEL.TargetRA2000);
            System.out.println(tcs.TEL.TargetDEC2000);
        }

        // arrivare al target e iniziare il tracking per l'osservazione
        if (conditionTest){
            tcs.CmdMoveToPosition(true);
            tcs.WaitMovement(); 
            tcs.CmdMoveToPosition(true); 
            tcs.WaitMovement(); 
        }

        // iniziare a seguire un nuovo target
        if (conditionTest){
            tcs.CmdStartTracking(true);
        }



        // interrompere il moto

        // parcheggiare il telescopio nella park position

        // spegnere tutto









        //tcs.Trajectory();
        



        /*

        if (tcs.domeAxisConnection){
            double angoloC = tcs.GetCupolaPosition();
            System.out.println("Posizione cupola: "+angoloC);

        //tcs.CmdHomeCupola(true);
        }
        //tcs.CmdGoStandby(true);


        if (tcs.xAxisConnection){
            tcs.Sleep(1000);
            System.out.println("...");
            double angoloAZ = tcs.GetAzTelPos();
            System.out.println("Posizione Azimuth: "+angoloAZ);
        }


        if (tcs.yAxisConnection){
            tcs.Sleep(1000);
            System.out.println("...");
            double angoloEL = tcs.GetElTelPos();
            System.out.println("Posizione Elevazione: "+angoloEL);
        }


        */


        //tcs.CmdSetHomePos(true);


        //tcs.CupolaApertura();
        /*
        //tcs.CmdCloseCupola(true);
        tcs.Sleep(3000);
        double angolo = tcs.GetCupolaPosition();
        System.out.println("Posizione cupola: "+angolo);
        tcs.SetCupolaTargetPosition(angolo-20.5);

        tcs.Sleep(3000);
        tcs.PuntaCupola();
        
        //tcs.CmdCupolaEst(true);
        tcs.Sleep(30000);
        tcs.FermaCupola();
        tcs.Sleep(3000);
        angolo = tcs.GetCupolaPosition();
        System.out.println("Posizione cupola: "+angolo);
        //tcs.Sleep(5000);
        //*/
        System.out.println("tutto okay");

        //if (connecting){
        tcs.Sleep(2000);
        tcs.disconnect();
        
        System.out.println("fine.");


      }


}
