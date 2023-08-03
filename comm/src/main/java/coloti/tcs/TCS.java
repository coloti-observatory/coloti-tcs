package coloti.tcs;

import coloti.tcs.configuration.Telescopio;
//import coloti.tcs.configuration.MotoreArAz;
//import java.io.File;
//import java.io.IOException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import coloti.tcs.configuration.*;
import coloti.tcs.objclasses.*;
import coloti.tcs.task.Task;
import coloti.tcs.task.TaskExecutor;
import coloti.tcs.task.TaskListener;

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

//import java.lang.Math.*;
import org.jboss.util.state.DefaultStateMachineModel;
import org.jboss.util.state.State;
import org.jboss.util.state.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.util.concurrent.CompletableFuture;

/*
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DrbgParameters.NextBytes;
import java.util.*;

import java.util.function.IntPredicate;
import javax.lang.model.util.ElementScanner6;
*/

//import coloti.tcs.ACSv5;


public class TCS {
    
    public final ACS AsseX;
    public final ACS AsseY;
    
    public final ACS AsseCupola; //= new ACS("serial ID cupola");
    ACS AsseZ;

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
    
    double ConversionFactorX;
    double ConversionFactorY;
    static final int RAD = 0, GRAD = 1, HOUR = 2, ENC = 3, ARCSECS = 4;
    int UnitMeasure = ARCSECS;
    
    boolean xAxisConnection = true;
    boolean yAxisConnection = true;
    boolean domeAxisConnection = true;

    private EHardwareStatePhase statePhase;

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public int error;
    public int nErrors = 0;
    public String errorBuffer;
    public String errorText = "";

    public Map <Integer, String> errorMap = new HashMap <>(){ // mappa da completare
        {
            put(100, "InitAxes");
            put(110, "GetAzAbsTargPos");
            put(120, "...");
        }
    };
    // 800 e qualcosa per i Begin Errors, 700 per program, 900 per general
    // 101 settato un modo sbagliato
    private final int[] nEncErr = new int[]{999,100,600,700,0,1,3,10,12,15,16,17,19,20,21,22,41,44,90,91};

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
            put(999, "relative position overflow");
            put(100, "initialization issue, mode not setted");
            put(600, "serial answer length is zero");
            put(700, "communication status is false during axes initialization");
            put(0, "checksum error detected in the received command or empty command");
            put(1, "command, or subcommand, was not executed, unrecognized");
            put(3, "SAVE operation has failed)");
            put(10, "command was not executed, requires special hardware");
            put(12, "servo process does not communicate with the main processor");
            put(15, "operation failed, many possible explanations, see the software guide for more informations");
            put(16, "command was not executed, many possible explanations, see the software guide for more informations");
            put(17, "command was not executed, command not supported in the current version");
            put(19, "array set command was not executed, invalid data");
            put(20, "command was not executed, missing data field");
            put(21, "non fatal, data field out of valid range, parameter set with the nearest valid value");
            put(22, "non fatal, unrecognized subcommand was found within data field");
            put(41, "non fatal, operation cannot be executed while a program is running");
            put(44, "non fatal, delete or overwrite operation are not allowed");
            put(90, "non fatal, memory checksum error");
            put(91, "non fatal, firmware checksum error");
        }
    };


    // FIRST THINGS TO DO
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

    // opcua states
    private StateMachine mcsStateMachine;
    public State OFF = new State(0, "OFF");
    public State LOADED = new State(1, "LOADED");
    public State STANDBY = new State(2, "STANDBY");
    public State ONLINE = new State(3, "ONLINE");
    public State MAINTENANCE = new State(4, "MAINTENANCE");
    public State FAULT = new State(5, "FAULT");

    


    private final TaskExecutor<Void> taskExecutor = new TaskExecutor<>();

    
    private final TaskListener defaultListener = new TaskListener() {
        long tStart = 0L;
        long tStop = 0L;
        String commandName = "";
        Field field;
        public void setField(String name, String state, long start, long stop, String err){
            try {
                field = TEL.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            
            try {
                field.set(TEL.getClass(),"commandname: "+name+"; busy: "+state+"; tstart: "+start+"; tstop: "+stop+"; error: "+err);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
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



    public TCS(){//boolean connectX, boolean connectY, boolean connectDome, String IDserX, String IDserY, String IDserDome){
        Configure();
        this.xAxisConnection = GEN.ConnessioneAz;
        this.yAxisConnection = GEN.ConnessioneEl;
        this.domeAxisConnection = GEN.ConnessioneDome;

        AsseX = new ACS(GEN.IdSerialAz);
        AsseY = new ACS(GEN.IdSerialEl);
        AsseCupola = new ACS(GEN.IdSerialDome);
        
        /*
        this.xAxisConnection = connectX;
        this.yAxisConnection = connectY;
        this.domeAxisConnection = connectDome;

        AsseX = new ACS(IDserX);
        AsseY = new ACS(IDserY);
        AsseCupola = new ACS(IDserDome);*/
    }

    
    public final boolean connect(){
        // AZIMUTH
        if (xAxisConnection){
            this.xAxisConnection= AsseX.SetSimpleStart(0);
            Sleep(500);
            Error(AsseX.InitAxes(), 100);
        }
        final double gearratioX = TEL.RapportoRiduzioneAZ*MotAZ.RiduzioneMotore;
        this.ConversionFactorX = AsseX.SetUserUnit(X, UnitMeasure, gearratioX); 


        // ELEVATION
        if (yAxisConnection){
            this.yAxisConnection = AsseY.SetSimpleStart(0);
            Sleep(500);
            Error(AsseY.InitAxes(), 100);
        }
        final double gearratioY = TEL.RapportoRiduzioneAL*MotEL.RiduzioneMotore;
        this.ConversionFactorY = AsseY.SetUserUnit(X, UnitMeasure, gearratioY);



        // DOME
        if(domeAxisConnection){
            this.domeAxisConnection = AsseCupola.SetSimpleStart(0);
            Sleep(500);
            Error(AsseCupola.InitAxes(), 100);
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
        if (xAxisConnection)
            AsseX.CloseComm();
        if (yAxisConnection)
            AsseY.CloseComm();
        if (domeAxisConnection)
            AsseCupola.CloseComm();

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

    public void Sleep(final int millisecondsTime) { // VERIFICATO 
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
    
    public void Error(final int err, final int IdErr){
        if(err != -1){
            this.nErrors += 1;
            this.error = IdErr;
            this.errorBuffer = errorMap.get(IdErr);
            //logger.warn(errorBuffer);
            this.errorText += "Error "+IdErr+": "+errorBuffer;
            if (check(nEncErr, err))
                this.errorText += ", "+errEncMap.get(err)+";";
            else
                this.errorText += ";";
        }
    }








    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    //      00000000000     00000000000     000000000000000
    //      00000000000     00000000000     000000000000000
    //      000             000                   000      
    //      000  000000     00000000000           000      
    //      000  000000     00000000000           000      
    //      000     000     000                   000      
    //      00000000000     00000000000           000      
    //      00000000000     00000000000           000      


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

    public int  GetAzMotorStatus(){
        if (xAxisConnection){
            AsseX.GetMotorStatus(X);
            this.MotAZ.MotorStatus = AsseX.MOTORSTATUS[0];
        }
        return MotAZ.MotorStatus; // cumulative status of the AZ motors: 0=both disabled; 1=both enabled; 2=degraded state i.e. 1 enabled; 1 in fault; 3=both in fault
    }

    public int GetElMotorStatus(){
        if (yAxisConnection){
            AsseY.GetMotorStatus(X);
            this.MotEL.MotorStatus = AsseY.MOTORSTATUS[0];
        }
        return MotEL.MotorStatus; // status of the EL motor: 0=disabled; 1=enabled; 2=fault
    }

    public double GetAzTelPos(){
        if (xAxisConnection){
            AsseX.GetMotPos(X);
            this.MotAZ.TelPos = AsseX.PositionAx[0];
        }
        return MotAZ.TelPos;
    }

    public double GetAzActVel(){
        if (xAxisConnection){
            AsseX.GetActualMotVel(X);
            this.MotAZ.ActualVel = AsseX.ActualVelAx[0];
        }
        return MotAZ.ActualVel;
    }

    public double GetAzActAcc(){
        return MotAZ.ActualAcc;
    }

    public double GetAzCommandedPos(){
        if (xAxisConnection){
            Error(AsseX.GetAbsTargPos(X), 110);
            this.MotAZ.TelPosition = AsseX.AbsTargPosAx[0];
        }
        return MotAZ.TelPosition;
    }

    public double GetAzCommandedVel(){
        if (xAxisConnection){
            AsseX.GetMotVel(X);
            this.MotAZ.CommandedVel = AsseX.VelAx[0];
        }
        return MotAZ.CommandedVel;
    }

    public double GetAzCommandedAcc(){
        if (xAxisConnection){
            AsseX.GetMotAcc(X);
            this.MotAZ.CommandedAcc = AsseX.AccAx[0];
        }
        return MotAZ.CommandedAcc;
    }

    public double GetCupolaPosition(){
        if (domeAxisConnection)
            GetCupolaInfo();
        return CUP.AZ;
    }

    public double GetElTelPos(){
        if (yAxisConnection){
            AsseY.GetMotPos(X);
            this.MotEL.TelPos = AsseY.PositionAx[0];
        }
        return MotEL.TelPos;
    }

    public double GetElActVel(){
        if (yAxisConnection){
            AsseY.GetActualMotVel(X);
            this.MotEL.ActualVel = AsseY.ActualVelAx[0];
        }
        return MotEL.ActualVel;
    }

    public double GetElActAcc(){
        return MotEL.ActualAcc;
    }

    public double GetElCommandedPos(){
        if (yAxisConnection){
            AsseY.GetAbsTargPos(X);
            this.MotEL.TelPosition = AsseY.AbsTargPosAx[0];
        }
        return MotEL.TelPosition;
    }
    
    public double GetElCommandedVel(){
        if (yAxisConnection){
            AsseY.GetMotVel(X);
            this.MotEL.ActualVel = AsseY.VelAx[0];
        }
        return MotEL.CommandedVel;
    }

    public double GetElCommandedAcc(){
        if (yAxisConnection){
            AsseY.GetMotAcc(X);
            this.MotEL.CommandedAcc = AsseY.AccAx[0];
        }
        return MotEL.CommandedAcc;
    }

    public int GetAzMotionState(){
        if (xAxisConnection){
            int MS = 0;
            AsseX.GetActualMotVel(X);
            final double ActualVelocity1 = AsseX.ActualVelAx[0];
            Sleep(200);
            AsseX.GetActualMotVel(X);
            final double ActualVelocity2 = AsseX.ActualVelAx[0];
            final double deltaV = ActualVelocity2 - ActualVelocity1;
            AsseX.GetMotionMode(X);

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
            AsseY.GetActualMotVel(X);
            final double ActualVelocity1 = AsseY.ActualVelAx[0];
            Sleep(200);
            AsseY.GetActualMotVel(X);
            final double ActualVelocity2 = AsseY.ActualVelAx[0];
            final double deltaV = ActualVelocity2 - ActualVelocity1;
            AsseY.GetMotionMode(X);

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

    public String GetEmergencyStopInfo(){
        return TEL.EmergencyStopInfo;
    }

    public String GetZeroDomeInfo(){
        return TEL.ZeroDomeInfo;
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

    public String GetStopPointingDomeInfo(){
        return TEL.StopPointingInfo;
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
    
    public String GetHomePosInfo(){
        return TEL.HomePosInfo;
    }

    
    public int GetErrorNumber(){
        this.GEN.ErrorNumber = nErrors;
        return GEN.ErrorNumber;
    }
    
    public String GetErrorBuffer(){
        this.GEN.ErrorBuffer = "{"+errorBuffer+"}";
        return GEN.ErrorBuffer;
    }
    
    public boolean GetErrorBufferOutOfRange(){
        return GEN.ErrorBufferOutOfRange;
    }
    
    public int GetErrorBufferSize(){
        return GEN.ErrorBufferSize;
    }
    // come lo faccio?
    public int GetHeartBeat(){
        return GEN.HeartBeat;
    }
    

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    //      00000000000     00000000000     000000000000000
    //      00000000000     00000000000     000000000000000
    //      000             000                   000      
    //      00000000000     00000000000           000      
    //      00000000000     00000000000           000      
    //              000     000                   000      
    //      00000000000     00000000000           000      
    //      00000000000     00000000000           000      


    // SETTERS 

    public void SetCupolaTargetPosition(final double value){
        this.CUP.CommandedAZ = value;
    }

    public void SetAzTelPosition(final double value){
        if (xAxisConnection){
            AsseX.SetAbsTargPos(X, value);
            AsseX.GetAbsTargPos(X);
            this.MotAZ.TelPosition = AsseX.AbsTargPosAx[0];
        }
    }

    public void SetAzJogDirection(final int value){
        if (value == -1 || value == 1)
            this.MotAZ.JogDirection = value;
    }

    public void SetAzJogVelocity(final double value){
        /*
        AsseX.GetMotionMode(X);
        if (AsseX.MOTIONMODE[0] == 10){
            AsseX.SetMotVel(X, value);
            this.MotAZ.JogVelocity = value*MotAZ.JogDirection;
        }
        */
        this.MotAZ.JogVelocity = value*MotAZ.JogDirection;

    }

    public void SetElTelPosition(final double value){
        if (yAxisConnection){
            AsseY.SetAbsTargPos(X, value);
            AsseY.GetAbsTargPos(X);
            this.MotEL.TelPosition = AsseY.AbsTargPosAx[0];
        }
    }

    public void SetElJogDirection(final int value){
        if (value == -1 || value == 1)
            this.MotEL.JogDirection = value;
    }

    public void SetElJogVelocity(final double value){
        /*
        AsseY.GetMotionMode(X);
        if (AsseY.MOTIONMODE[0] == 10){
            AsseY.SetMotVel(X, value);
            this.MotEL.JogVelocity = value*MotEL.JogDirection;
        }
        */
        this.MotEL.JogVelocity = value*MotEL.JogDirection;

    }
    
    public void SetMotionType(final int value){
        if (xAxisConnection && yAxisConnection){
            if (value == 0){
                AsseX.SetSlewMode(X);

                if (this.NumAxes == 2)
                    AsseY.SetSlewMode(X);

                AsseX.SetMotVel(X, MotAZ.SlewVelocity);

                if (this.NumAxes == 2)
                    AsseY.SetMotVel(X, MotEL.SlewVelocity);
            }
            else if (value == 1){
                AsseX.SetTrackMode(X);
                
                if (this.NumAxes == 2)
                    AsseY.SetTrackMode(X);

                AsseX.SetMotVel(X, MotAZ.JogVelocity);
                if (this.NumAxes == 2)
                    AsseY.SetMotVel(X, MotEL.JogVelocity);
            }
            this.TEL.MotionType = value;
        }
    }

    public void SetAzSlewVelocity(final double value){
        
        int sign = 1;
        if (value < 0)
            sign = -1;

        /*
        AsseX.GetMotionMode(X);
        if (AsseX.MOTIONMODE[0] == 0){
            AsseX.SetMotVel(X, value*sign);
            this.MotAZ.SlewVelocity = value*sign;
        }
        */

        this.MotAZ.SlewVelocity = value*sign;        
    }

    public void SetAzSlewAcceleration(final double value){
        if (xAxisConnection){
            AsseX.SetMotAcc(X, value);
            AsseX.GetMotAcc(X);
            this.MotAZ.SlewAcceleration = AsseX.AccAx[0];
        }        
    }

    public void SetAzSlewDeceleration(final double value){
        if (xAxisConnection){
            AsseX.SetMotDec(X, value);
            AsseX.GetMotDec(X);
            this.MotAZ.SlewAcceleration = AsseX.DecAx[0];
        }
    }

    public void SetElSlewVelocity(final double value){
         
        int sign = 1;
        if (value < 0)
            sign = -1;

        /*
        AsseY.GetMotionMode(X);
        if (AsseY.MOTIONMODE[0] == 0){
            AsseY.SetMotVel(X, value*sign);
            this.MotEL.SlewVelocity = value*sign;
        }
        */

        this.MotEL.SlewVelocity = value*sign;
    }

    public void SetElSlewAcceleration(final double value){
        if (yAxisConnection){
            AsseY.SetMotAcc(X, value);
            AsseY.GetMotAcc(X);
            this.MotEL.SlewAcceleration = AsseY.AccAx[0];
        }
    }

    public void SetElSlewDeceleration(final double value){
        if (yAxisConnection){
            AsseY.SetMotDec(X, value);
            AsseY.GetMotDec(X);
            this.MotEL.SlewAcceleration = AsseY.AccAx[0];
        }
    }

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


    public void SetAzParkingPosition(final double value){
        this.MotAZ.ParkPos = value;
    }

    public void SetElParkingPosition(final double value){
        this.MotEL.ParkPos = value;
    }

    public void SetCupolaParkingPosition(final double value){
        this.CUP.ParkPos = value;
    }
    


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    //      00000000000     00000      00000     00000000        
    //      00000000000     000000    000000     0000000000         
    //      000             000 000  000 000     000    0000         
    //      000             000  000000  000     000     0000 
    //      000             000   0000   000     000      0000
    //      000             000    00    000     000      0000
    //      00000000000     000          000     000000000000       
    //      00000000000     000          000     00000000000      


    // COMANDI OPCUA  
    
    // commandname: CommandGoLoaded; busy: FALSE; tstart: 1970-01-01-00:00:00.000; tstop: 1970-01-01-00:00:00.000; error:


    public void CmdGoLoaded(final boolean value){
        if (value){
            try {
                taskExecutor.runTask(goloadedTask, defaultListener);
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            this.TEL.GoLoadedInfo = "commandname: CommandGoLoaded; busy: FALSE; tstart: 0; tstop: 0; error:";

            //initHwStateMachine(LOADED)  */
        }
    }

    public void CmdGoStandby(final boolean value){
        if (value){
            try {
                taskExecutor.runTask(gostandbyTask, defaultListener);
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
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

    public void CmdGoOnline(final boolean value){
        if (value){

            try {
                taskExecutor.runTask(goonlineTask, defaultListener);
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
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

    public void CmdGoMaintenance(final boolean value){
        if (value){
            
            try {
                taskExecutor.runTask(gomaintenanceTask, defaultListener);
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
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

    
    public void CmdEnableAzMotors(final boolean value){
        if (value && xAxisConnection){
            final int err;
            //long ValoX;
            double posizioneX;
            if (!AsseX.CommStatus){
                AsseX.OpenCommunications();
                AsseX.SetMotorOn(X);
                posizioneX = GetAzTelPos();

                //err = AsseX.GetMotEncPos(X);
                //ValoX = AsseX.VALUECR;
            }
        }
    }

    public void CmdDisableAzMotors(final boolean value){
        if (value && xAxisConnection){
            int err;
            long ValoX;
            if (AsseX.CommStatus){
                if (AsseX.IsMoving(X) == 1){
                    AsseX.StopMove(X);
                }
                AsseX.SetMotorOff(X);
                err = AsseX.GetMotEncPos(X);
                ValoX = AsseX.VALUECR;
                AsseX.CloseComm();
            }
        }
    }

    public void CmdEnableElMotors(final boolean value){
        if (value && yAxisConnection){
            int err;
            long ValoY;
            if (!AsseY.CommStatus){
                AsseY.OpenCommunications();
                AsseY.SetMotorOn(X);
                err = AsseY.GetMotEncPos(X);
                ValoY = AsseY.VALUECR;
            }
        }
    }

    public void CmdDisableElMotors(final boolean value){
        if (value && yAxisConnection){
            int err;
            long ValoY;
            if (AsseY.CommStatus){
                if (AsseY.IsMoving(X) == 1){
                    AsseY.StopMove(X);
                }
                AsseY.SetMotorOff(X);
                err = AsseY.GetMotEncPos(X);
                ValoY = AsseY.VALUECR;
                AsseY.CloseComm();
            }
        }
    }

    public void CmdStartMotion(final boolean value){
        if (value && xAxisConnection && yAxisConnection){
            if (AsseX.CommStatus && AsseY.CommStatus){

                AsseX.StopMove(X);
                if(AsseX.IsMoving(X) == 1)
                    Sleep(100);
                AsseY.StopMove(X);
                if(AsseY.IsMoving(X) == 1)
                    Sleep(100);

                if(TEL.MotionType == 0){
                    //SetMotionType(0);
                    AsseX.SetMotAcc(X, MotAZ.MaxAcc);
                    AsseX.SetMotDec(X, MotAZ.MaxAcc);
                    AsseY.SetMotAcc(X, MotEL.MaxAcc);
                    AsseY.SetMotDec(X, MotEL.MaxAcc);

                    AsseX.Move(X, MotAZ.TelPosition, MotAZ.SlewVelocity); // TEL.SlewVelX
                    AsseY.Move(X, MotEL.TelPosition, MotEL.SlewVelocity); // TEL.SlewVelY
                }

                if(TEL.MotionType == 1){
                    AsseX.SetMotAcc(X, MotAZ.JogVelocity);
                    //AsseX.SetMotDec(X, MotAZ.JogVelocity);
                    AsseY.SetMotAcc(X, MotEL.JogVelocity);
                    //AsseY.SetMotDec(X, MotEL.JogVelocity);

                    AsseX.Move(X, MotAZ.TelPosition, MotAZ.JogVelocity); // TEL.SlewVelX
                    AsseY.Move(X, MotEL.TelPosition, MotEL.JogVelocity); // TEL.SlewVelY
                }

                Sleep(100);
            }
        }
    }

    public void CmdStopMotion(final boolean value){
        if (value){
            if (xAxisConnection){
                if (AsseX.IsMoving(X) == 1)
                    AsseX.StopMove(X);}

            if (yAxisConnection){
                if (AsseY.IsMoving(X) == 1)
                    AsseY.StopMove(X);}

            if (domeAxisConnection)
                FermaCupola();
        }
    }

    public void CmdStartAzMotion(final boolean value){
        if (value && xAxisConnection){
            if (AsseX.CommStatus){
                AsseX.StopMove(X);
                if(AsseX.IsMoving(X) == 1)
                    Sleep(200);

                AsseX.SetSlewMode(X);
                AsseX.SetMotAcc(X, MotAZ.MaxAcc);
                AsseX.SetMotDec(X, MotAZ.MaxAcc);

                AsseX.Move(X, MotAZ.TelPosition, MotAZ.SlewVelocity);

                Sleep(300);

                //PuntaCupola(MotAZ.TelPosition);
            }
        }
    }
    
    public void CmdStopAzMotion(final boolean value){
        if (value && xAxisConnection){
            if (AsseX.IsMoving(X) == 1)
                AsseX.StopMove(X);
        }
    }

    public void CmdStartElMotion(final boolean value){
        if (value && yAxisConnection){
            if (AsseY.CommStatus){
                AsseY.StopMove(X);
                if(AsseY.IsMoving(X) == 1)
                    Sleep(200);

                AsseY.SetSlewMode(X);
                AsseY.SetMotAcc(X, MotEL.MaxAcc);
                AsseY.SetMotDec(X, MotEL.MaxAcc);

                AsseY.Move(X, MotEL.TelPosition, MotEL.SlewVelocity);

                Sleep(300);
            }
        }
    }
    
    public void CmdStopElMotion(final boolean value){
        if (value && yAxisConnection){
            if (AsseY.IsMoving(X) == 1)
                AsseY.StopMove(X);
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

    public void CmdStartParking(final boolean value){
        if (value){
            CmdStartAzParking(value);
            CmdStartElParking(value);
            CmdCupolaParking(value);
        }
    }

    public void CmdStopParking(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    public void CmdStartAzParking(final boolean value){
        if (value){
            SetAzTelPosition(MotAZ.ParkPos);
            CmdStartAzMotion(true);
        }
    }

    public void CmdStopAzParking(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    public void CmdStartElParking(final boolean value){
        if (value){
            SetElTelPosition(MotEL.ParkPos);
            CmdStartElMotion(true);
        }
    }

    public void CmdStopElParking(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    public void CmdStartTracking(final boolean value){
        if (value){
            SetMotionType(0);
            CmdStartMotion(value);
            SetMotionType(1);
            CmdStartMotion(value);
        }
    }

    public void CmdStopTracking(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    public void CmdStartPointing(final boolean value){
        if (value){
            SetMotionType(0);
            CmdStartMotion(value);
        }
    }

    public void CmdStopPointing(final boolean value){
        if (value)
            CmdStopMotion(value);
    }

    public void CmdHomePos(final boolean value){
        if (value && xAxisConnection && yAxisConnection)
            try {
                taskExecutor.runTask(homeposTask, defaultListener);
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            this.TEL.HomePosInfo = "commandname: CommandGoLoaded; busy: FALSE; tstart: 0; tstop: 0; error:";
    }

    public void CmdStopPointMotion(final boolean value){
        if (value && xAxisConnection && yAxisConnection)
            FermaMoto();
    }

    //  task apertura e chiusura cupola. aggiungere status movimento cupola come boolean nei get dell'icd. anche per l'inizializzazione, true o false sul set a zero della cupola. aggiungere info relativi ai command della cupola. Usare il T3 per controllare lo stato della cupola nel set degli zeri.

    // Ricorda di sistemare la lettura seriale nella comm class: while non c'è niente aspetta, poi leggi e poi continua a leggere finché ci sono bit a disposizione

    // spostare tutti i command info  nella classe TEL ? 

    public void CmdOpenCupola(final boolean value){
        if (value && domeAxisConnection)
            CupolaApertura();
    }

    public void CmdCloseCupola(final boolean value){
        if (value && domeAxisConnection)
            CupolaChiusura();
    }

    public void CmdStartCupolaPointing(final boolean value) {
        if (value && domeAxisConnection)
            PuntaCupola(MotAZ.TelPosition);
    }

    public void CmdCupolaParking(final boolean value) {
        if (value && domeAxisConnection)
            PuntaCupola(CUP.ParkPos);
    }

    public void CmdStopCupola(final boolean value){
        if (value && domeAxisConnection)
            FermaCupola();
    }

    public void CmdSetZeroCupola(final boolean value){
        if (value && domeAxisConnection)
            try {
                taskExecutor.runTask(zerodomeTask, defaultListener);
            } catch (ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
            this.TEL.ZeroDomeInfo = "commandname: ZeroDomeInfo; busy: FALSE; tstart: 0; tstop: 0; error:";

            //CupolaSetZero();
    }

    public void CmdCupolaOvest(final boolean value){
        if (value && domeAxisConnection)
            CupolaOvest();
    }

    public void CmdCupolaEst(final boolean value){
        if (value && domeAxisConnection)
            CupolaEst();
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    //  000000000000000     00000000      00000000000     000     000
    //  000000000000000    0000  0000     00000000000     000     000
    //        000          000    000     000             000    000
    //        000          0000000000     00000000000     000000000
    //        000          0000000000     00000000000     000000000
    //        000          000    000             000     000    000
    //        000          000    000     00000000000     000     000
    //        000          000    000     00000000000     000     000



    private final Task<Void> goloadedTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener = defaultListener;
        
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


    private final Task<Void> gostandbyTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener = defaultListener;
        
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


    private final Task<Void> goonlineTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener = defaultListener;
        
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



    private final Task<Void> gomaintenanceTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener = defaultListener;
        
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
    

    private final Task<Void> zerodomeTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener = defaultListener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("ZeroDomeInfo");
                
            CupolaSetZero();
            
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(5000);
                AsseCupola.IsProgramRunning();
                System.out.println("The dome is running ... "+AsseCupola.isRunning);
                if (!AsseCupola.isRunning)
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


    private final Task<Void> homeposTask = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener = defaultListener;
        
        private Void v;
        @Override
        public Void call() throws Exception {
            if(listener!=null)
                listener.onStart("HomePosInfo");
                
            SettaPosHome();
            
            while(isInterrupted){
                if(listener!=null)
                    listener.onWorking(null);
                Sleep(5000);
                AsseX.IsProgramRunning();
                AsseY.IsProgramRunning();
                System.out.println("Az and El are running ... "+AsseX.isRunning+", "+AsseY.isRunning);
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











































    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------



    // FUNZIONI COMPLESSE DA FARE
    public void InitStar(){}
    public void Puntamento(){}
    public void ComandiTastierino(){}
    public void TraiettoriaX(){}
    public void TraiettoriaY(){}
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
    public void TelescopioSettaZeroStar(){}

    // INCOMPLETO
    public void TelescopioSetHome(){
        double valAZ, valEL;
        //calcolo astronomico
        //if (TEL.MonType == 0){}
        valAZ = (180 - TEL.TargetAZ)*3600;
        valEL = TEL.TargetEL*3600;
        this.DPX = TEL.PosX - valAZ; //desired position x
        this.DPY = TEL.PosY - valEL;

        AsseX.SetAxisZeroPos(X, valAZ);
        AsseY.SetAxisZeroPos(X, valEL);

        //modifica zeri dat file
    }

    public void EseguiPuntamento(){
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

    // INCOMPLETO
    public void SettaPosHome(){
        long ValoX = 0, ValoY = 0;
        
        // aprire file Zeri.dat e prendere i valori degli zeri 
        final long ZeroX=0, ZeroY=0; // non sono assegnati, vengono dal file?

        if (TEL.MonType == 0){
            ValoX += (long) (ZeroX*3600*AsseX.CONVFACTOR[0] + 0.5 - 30*AsseX.CONVFACTOR[0]);
            AsseX.CommandArray("AVSE", 8, (int) ValoX);
            ValoX = AsseX.VALUECR;
            AsseX.ExecProg("HOMEX");

            ValoY += (long)(ZeroY*3600*AsseY.CONVFACTOR[0]-60.*AsseY.CONVFACTOR[0]+0.5);
            AsseY.CommandArray("AVSE", 8, (int) ValoY);
            ValoY = AsseY.VALUECR;
            AsseY.ExecProg("HOMEX");
        }
        else{
            AsseX.CommandArray("AVSE", 8, (int) ValoX);
            ValoX = AsseX.VALUECR;
            AsseX.ExecProg("HOMEX");

            AsseY.CommandArray("AVSE", 8, (int) ValoY);
            ValoY = AsseY.VALUECR;
            AsseY.ExecProg("HOMEX");
        }
    }

    public void FermaMoto(){  // era dentro setta pos home
        AsseX.CommandMot("PS");
        AsseY.CommandMot("PS");

        AsseX.StopMove(X);
        AsseY.StopMove(X);
    }
    
    //public void Timer(){}

    // INCOMPLETO
    public void SetZeroFromFile(){
        final int valx = 1, valy = 1, valc = 1;
        // assegnati da file lastpos.dat
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
        if (TEL.MonType == 0){
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
    }

    public void GetTelInfoX(){
        double PosX;
        long valo;
        int err;

        if (AsseX.CommStatus){
            // caso TelMonTipo = 0
            if (TEL.MonType == 0){
                err = AsseX.GetMotEncPos(X);
                valo = AsseX.VALUECR;
                PosX = valo/AsseX.CONVFACTOR[0] - CostX[0];
                TEL.PosX = PosX;
                PosX = (180*3600.0 - PosX);
                TEL.AZ = PosX/3600.0;
            }
            
            // caso Tel MonTipo = 1
            else{
                err = AsseX.GetMotEncPos(X);
                valo = AsseX.VALUECR;
                PosX = valo/AsseX.CONVFACTOR[0];
                TEL.PosX = PosX;
                TEL.H = PosX/54000.0;
                if (TEL.H < 0.0)
                    TEL.H += 24;
            }

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
            if (TEL.MonType == 0){
                TEL.EL = PosY/3600.0;
            }
            else{
                TEL.DEC = PosY/3600.0;
            }

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
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("FERMACUP");
            CUP.StatusRotazione = 0;
            CUP.Direzione = 0;
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int PuntaCupola(final double azObj){
        if (AsseCupola.CommStatus){
            final int az = (int) (3600*azObj*AsseCupola.CONVFACTOR[0]);
            int Err;
            final byte[] command = AsseCupola.sbld("AVSE");
            AsseCupola.CommandArray(command, 10, az);
            Err = AsseCupola.ExecProg("PUNTA");
            if (Err != -1){
                return Err;
            }
        }
        CUP.StatusRotazione = 1;
        CUP.Direzione = -1;
        return -1;
    }

    public int PuntaCupola(){
        if (AsseCupola.CommStatus){
            final int az = (int) (3600*CUP.CommandedAZ*AsseCupola.CONVFACTOR[0]);
            int Err;
            final byte[] command = AsseCupola.sbld("AVSE");
            AsseCupola.CommandArray(command, 10, az);
            Err = AsseCupola.ExecProg("PUNTA");
            if (Err != -1){
                return Err;
            }
        }
        CUP.StatusRotazione = 1;
        CUP.Direzione = -1;
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


    public static void main(final String[] a){ // sudo chmod 777 /dev/ttyS0     sudo chmod 777 /dev/ttyUSB0
        System.out.println("\nHello World\n");
        final TCS tcs = new TCS();
        tcs.connect();
        
        if (tcs.domeAxisConnection){
            double angoloC = tcs.GetCupolaPosition();
            System.out.println("Posizione cupola: "+angoloC);

        //tcs.CmdSetZeroCupola(true);
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
        tcs.Sleep(5000);
        
        tcs.disconnect();



      }


}
