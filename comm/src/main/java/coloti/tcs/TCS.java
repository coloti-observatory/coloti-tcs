package coloti.tcs;

import coloti.tcs.configuration.MotoreArAz;
//import java.io.File;
//import java.io.IOException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import coloti.tcs.configuration.*;
import coloti.tcs.objclasses.*;
//import coloti.tcs.ConfigurationClass;
import java.util.concurrent.TimeUnit;
import java.lang.Math.*;
import org.jboss.util.state.DefaultStateMachineModel;
import org.jboss.util.state.State;
import org.jboss.util.state.StateMachine;
import java.util.concurrent.CompletableFuture;

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
    
    ACS AsseX, AsseY, AsseZ;
    ACS AsseCupola; //= new ACS("serial ID cupola");

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

    public TCS(){
        //Configure();
    }
    public TCS(boolean START){
        Configure();
    }

    // FIRST THINGS TO DO
    String X = "X";
    String Y = "Y";
    String Z = "Z";

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
    private double DPX;
    private double DPY;

    // opcua states
    private StateMachine mcsStateMachine;
    public State OFF = new State(0, "OFF");
    public State LOADED = new State(1, "LOADED");
    public State STANDBY = new State(2, "STANDBY");
    public State ONLINE = new State(3, "ONLINE");
    public State MAINTENANCE = new State(4, "MAINTENANCE");
    public State FAULT = new State(5, "FAULT");


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

    public void Sleep(int millisecondsTime) { // VERIFICATO 
        try {
          TimeUnit.MILLISECONDS.sleep(millisecondsTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }




    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    // OPCUA
    public StateMachine getMcsStateMachine() {
        return mcsStateMachine;
    }

    private void initHwStateMachine(State init) {

        StateMachine.Model model = new DefaultStateMachineModel();
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
        AsseX.GetMotorStatus(X);
        this.MotAZ.MotorStatus = AsseX.MOTORSTATUS[0];
        return MotAZ.MotorStatus; // cumulative status of the AZ motors: 0=both disabled; 1=both enabled; 2=degraded state i.e. 1 enabled; 1 in fault; 3=both in fault
    }

    public int GetElMotorStatus(){
        AsseY.GetMotorStatus(X);
        this.MotEL.MotorStatus = AsseY.MOTORSTATUS[0];
        return MotEL.MotorStatus; // status of the EL motor: 0=disabled; 1=enabled; 2=fault
    }

    public double GetAzTelPos(){
        AsseX.GetMotPos(X);
        this.MotAZ.TelPos = AsseX.PositionAx[0];
        return MotAZ.TelPos;
    }
    // quale dei due ci piace?
    public double GetAzMotorTelPos(){
        return MotAZ.MotorTelPos;
    }

    public double GetAzActVel(){
        AsseX.GetActualMotVel(X);
        this.MotAZ.ActualVel = AsseX.ActualVelAx[0];
        return MotAZ.ActualVel;
    }

    public double GetAzActAcc(){
        return MotAZ.ActualAcc;
    }

    public double GetAzCommandedPos(){
        AsseX.GetAbsTargPos(X);
        this.MotAZ.TelPosition = AsseX.AbsTargPosAx[0];
        return MotAZ.TelPosition;
    }

    public double GetAzCommandedVel(){
        AsseX.GetMotVel(X);
        this.MotAZ.CommandedVel = AsseX.VelAx[0];
        return MotAZ.CommandedVel;
    }

    public double GetAzCommandedAcc(){
        AsseX.GetMotAcc(X);
        this.MotAZ.CommandedAcc = AsseX.AccAx[0];
        return MotAZ.CommandedAcc;
    }

    public double GetElTelPos(){
        AsseY.GetMotPos(X);
        this.MotEL.TelPos = AsseY.PositionAx[0];
        return MotEL.TelPos;
    }

    public double GetElActVel(){
        AsseY.GetActualMotVel(X);
        this.MotEL.ActualVel = AsseY.ActualVelAx[0];
        return MotEL.ActualVel;
    }

    public double GetElActAcc(){
        return MotEL.ActualAcc;
    }

    public double GetElCommandedPos(){
        AsseY.GetAbsTargPos(X);
        this.MotEL.TelPosition = AsseY.AbsTargPosAx[0];
        return MotEL.TelPosition;
    }
    
    public double GetElCommandedVel(){
        AsseY.GetMotVel(X);
        this.MotEL.ActualVel = AsseY.VelAx[0];
        return MotEL.CommandedVel;
    }

    public double GetElCommandedAcc(){
        AsseY.GetMotAcc(X);
        this.MotEL.CommandedAcc = AsseY.AccAx[0];
        return MotEL.CommandedAcc;
    }

    public int GetAzMotionState(){
        return MotAZ.MotionState;
    }

    public int GetElMotionState(){
        return MotEL.MotionState;
    }

    public double GetAzEncOffset(){
        return MotAZ.EncOffset;
    }

    public double GetElEncOffset(){
        return MotEL.EncOffset;
    }
    
    public int GetMachineState(){
        return TEL.MachineState;
    }
    
    public int GetMachineStatePhase(){
        return TEL.MachineStatePhase;
    }
    
    public int GetTCUMode(){
        return TEL.TCUMode;
    }
    
    public String GegGoLoadedInfo(){
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
        return MotAZ.EnableMotorsInfo;
    }
    
    public String GetAzDisableMotorsInfo(){
        return MotAZ.DisableMotorsInfo;
    }

    public String GetElEnableMotorsInfo(){
        return MotEL.EnableMotorsInfo;
    }
    
    public String GetElDisableMotorsInfo(){
        return MotEL.DisableMotorsInfo;
    }
    
    public String GetStartMotionInfo(){
        return TEL.StartMotionInfo;
    }
    
    public String GetStopMotionInfo(){
        return TEL.StopMotionInfo;
    }
    
    public String GetAzStartMotionInfo(){
        return MotAZ.StartMotionInfo;
    }
    
    public String GetAzStopMotionInfo(){
        return MotAZ.StopMotionInfo;
    }
    
    public String GetElStartMotionInfo(){
        return MotEL.StartMotionInfo;
    }
    
    public String GetElStopMotionInfo(){
        return MotEL.StopMotionInfo;
    }
    
    public String GetEmergencyStopInfo(){
        return TEL.EmergencyStopInfo;
    }
    
    public String GetAzStartEncInitInfo(){
        return MotAZ.StartEncInitInfo;
    }
    
    public String GetAzStopEncInitInfo(){
        return MotAZ.StopEncInitInfo;
    }
    
    public int GetErrorNumber(){
        return GEN.ErrorNumber;
    }
    
    public String GetErrorBuffer(){
        return GEN.ErrorBuffer;
    }
    
    public boolean GetErrorBufferOutOfRange(){
        return GEN.ErrorBufferOutOfRange;
    }
    
    public int GetErrorBufferSize(){
        return GEN.ErrorBufferSize;
    }
    
    public int GetHeartBeat(){
        return GEN.HeartBeat;
    }
    




    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------




    // SETTERS 

    public void SetAzTelPosition(double value){
        AsseX.SetAbsTargPos(X, value);
        AsseX.GetAbsTargPos(X);
        this.MotAZ.TelPosition = AsseX.AbsTargPosAx[0];
    }

    public void SetAzJogDirection(int value){
        if (value == -1 || value == 1)
            this.MotAZ.JogDirection = value;
    }

    public void SetAzJogVelocity(double value){
        /*
        AsseX.GetMotionMode(X);
        if (AsseX.MOTIONMODE[0] == 10){
            AsseX.SetMotVel(X, value);
            this.MotAZ.JogVelocity = value*MotAZ.JogDirection;
        }
        */
        this.MotAZ.JogVelocity = value*MotAZ.JogDirection;

    }

    public void SetElTelPosition(double value){
        AsseY.SetAbsTargPos(X, value);
        AsseY.GetAbsTargPos(X);
        this.MotEL.TelPosition = AsseY.AbsTargPosAx[0];
    }

    public void SetElJogDirection(int value){
        if (value == -1 || value == 1)
            this.MotEL.JogDirection = value;
    }

    public void SetElJogVelocity(double value){
        /*
        AsseY.GetMotionMode(X);
        if (AsseY.MOTIONMODE[0] == 10){
            AsseY.SetMotVel(X, value);
            this.MotEL.JogVelocity = value*MotEL.JogDirection;
        }
        */
        this.MotEL.JogVelocity = value*MotEL.JogDirection;

    }
    
    public void SetMotionType(int value){
        if (value == 0){
            AsseX.SetSlewMode(X);
            AsseY.SetSlewMode(X);

            AsseX.SetMotVel(X, MotAZ.SlewVelocity);
            AsseY.SetMotVel(X, MotEL.SlewVelocity);
        }
        else if (value == 1){
            AsseX.SetTrackMode(X);
            AsseY.SetTrackMode(X);

            AsseX.SetMotVel(X, MotAZ.JogVelocity);
            AsseY.SetMotVel(X, MotEL.JogVelocity);
        }
        this.TEL.MotionType = value;
    }

    public void SetAzSlewVelocity(double value){
        
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

    public void SetAzSlewAcceleration(double value){
        AsseX.SetMotAcc(X, value);
        AsseX.GetMotAcc(X);
        this.MotAZ.SlewAcceleration = AsseX.AccAx[0];        
    }

    public void SetAzSlewDeceleration(double value){
        AsseX.SetMotDec(X, value);
        AsseX.GetMotDec(X);
        this.MotAZ.SlewAcceleration = AsseX.DecAx[0];
    }

    public void SetElSlewVelocity(double value){
         
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

    public void SetElSlewAcceleration(double value){
        AsseY.SetMotAcc(X, value);
        AsseY.GetMotAcc(X);
        this.MotEL.SlewAcceleration = AsseY.AccAx[0];
    }

    public void SetElSlewDeceleration(double value){
        AsseY.SetMotDec(X, value);
        AsseY.GetMotDec(X);
        this.MotEL.SlewAcceleration = AsseY.AccAx[0];
    }

    public void SetAzMinAcc(double value){
        AsseX.SetMaxMinAcc(X, MotAZ.MaxAcc, value);
        this.MotAZ.MinAcc = AsseX.MinAcc[0];
    }

    public void SetAzMaxAcc(double value){
        AsseX.SetMaxMinAcc(X, value, MotAZ.MinAcc);
        this.MotAZ.MaxAcc = AsseX.MaxAcc[0];
    }

    public void SetAzMinDec(double value){ 
        this.MotAZ.MinDec = value;
    }

    public void SetAzMaxDec(double value){
        this.MotAZ.MaxDec = value;
    }
    
    public void SetAzMinVel(double value){
        AsseX.SetMaxMinVel(X, MotAZ.MaxVel, value);
        this.MotAZ.MinVel = AsseX.MinVel[0];
    }

    public void SetAzMaxVel(double value){
        AsseX.SetMaxMinVel(X, value, MotAZ.MinVel);
        this.MotAZ.MaxVel = AsseX.MaxVel[0];
    }

    public void SetAzTelMinPos(double value){
        AsseX.SetMaxMinPos(X, MotAZ.TelMaxPos, value);
        this.MotAZ.TelMinPos = AsseX.MinPos[0];
    }

    public void SetAzTelMaxPos(double value){
        AsseX.SetMaxMinPos(X, value, MotAZ.TelMinPos);
        this.MotAZ.TelMaxPos = AsseX.MaxPos[0];
    }

    public void SetElMinAcc(double value){
        AsseY.SetMaxMinAcc(X, MotEL.MaxAcc, value);
        this.MotEL.MinAcc = AsseY.MinAcc[0];
    }

    public void SetElMaxAcc(double value){
        AsseY.SetMaxMinAcc(X, value, MotEL.MinAcc);
        this.MotEL.MaxAcc = AsseY.MaxAcc[0];
    }

    public void SetElMinDec(double value){
        this.MotEL.MinDec = value;
    }

    public void SetElMaxDec(double value){
        this.MotEL.MaxDec = value;
    }
    
    public void SetElMinVel(double value){
        AsseY.SetMaxMinVel(X, MotEL.MaxVel, value);
        this.MotEL.MinVel = AsseY.MinVel[0];
    }

    public void SetElMaxVel(double value){
        AsseY.SetMaxMinVel(X, value, MotEL.MinVel);
        this.MotEL.MaxVel = AsseY.MaxVel[0];
    }

    public void SetElTelMinPos(double value){
        AsseY.SetMaxMinPos(X, MotEL.TelMaxPos, value);
        this.MotEL.TelMinPos = AsseY.MinPos[0];
    }

    public void SetElTelMaxPos(double value){
        AsseY.SetMaxMinPos(X, value, MotEL.TelMinPos);
        this.MotEL.TelMaxPos = AsseY.MaxPos[0];
    }

    public void SetAzLsOpCwPos(double value){
        this.MotAZ.LsOpCwPos = value;
    }

    public void SetAzLsOpCcwPos(double value){
        this.MotAZ.LsOpCcwPos = value;
    }

    public void SetElLsOpLowPos(double value){
        this.MotEL.LsOpLowPos = value;
    }

    public void SetElLsOpHighPos(double value){
        this.MotEL.LsOpHighPos = value;
    }

    public void SetObserverLat(double value){
        this.OSS.Latitudine = value; // oppure disaccoppiare: ObserverLat 
    }

    public void SetObserverLong(double value){
        this.OSS.Longitudine = value;
    }

    public void SetObserverAlt(int value){
        this.OSS.Altitudine = value;
    }


    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------





    // COMANDI OPCUA        boolean dove va? è il result o è un parametro?

    public void CmdGoLoaded(boolean value){}
    public void CmdGoStanby(boolean value){}
    public void CmdGoOnline(boolean value){}
    public void CmdGoMaintenance(boolean value){}


    public void CmdEnableAzMotors(boolean value){
        int err;
        long ValoX;
        if (!AsseX.CommStatus){
            AsseX.OpenCommunications();
            AsseX.SetMotorOn(X);
            err = AsseX.GetMotEncPos(X);
            ValoX = AsseX.VALUECR;
        }
    }

    public void CmdDisableAzMotors(boolean value){
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

    public void CmdEnableElMotors(boolean value){
        int err;
        long ValoY;
        if (!AsseY.CommStatus){
            AsseY.OpenCommunications();
            AsseY.SetMotorOn(X);
            err = AsseY.GetMotEncPos(X);
            ValoY = AsseY.VALUECR;
        }
    }

    public void CmdDisableElMotors(boolean value){
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

    public void CmdStartMotion(boolean value){
        if (AsseX.CommStatus && AsseY.CommStatus){

            AsseX.StopMove(X);
            if(AsseX.IsMoving(X) == 1)
                Sleep(200);
            AsseY.StopMove(X);
            if(AsseY.IsMoving(X) == 1)
                Sleep(200);

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

            Sleep(300);
        }
    }

    public void CmdStopMotion(boolean value){
        if (AsseX.IsMoving(X) == 1)
            AsseX.StopMove(X);
        if (AsseY.IsMoving(X) == 1)
            AsseY.StopMove(X);
        FermaCupola();
    }

    public void CmdStartAzMotion(boolean value){
        if (AsseX.CommStatus){
            AsseX.StopMove(X);
            if(AsseX.IsMoving(X) == 1)
                Sleep(200);

            AsseX.SetSlewMode(X);
            AsseX.SetMotAcc(X, MotAZ.MaxAcc);
            AsseX.SetMotDec(X, MotAZ.MaxAcc);

            AsseX.Move(X, MotAZ.TelPosition, MotAZ.SlewVelocity);

            Sleep(300);

            PuntaCupola(MotAZ.TelPosition);
        }
    }
    
    public void CmdStopAzMotion(boolean value){
        if (AsseX.IsMoving(X) == 1)
            AsseX.StopMove(X);
        FermaCupola();
    }

    public void CmdStartElMotion(boolean value){
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
    
    public void CmdStopElMotion(boolean value){
        if (AsseY.IsMoving(X) == 1)
            AsseY.StopMove(X);
    }

    public void CmdEmergencyStop(boolean value){
        AsseX.StopMove(X);
        AsseY.StopMove(X);
        FermaCupola();

        Sleep(300);

        AsseX.StopMove(X);
        AsseY.StopMove(X);
        FermaCupola();

        Sleep(300);

        AsseX.StopMove(X);
        AsseY.StopMove(X);
        FermaCupola();
    }

    public void CmdStartParking(boolean value){
        CmdStartAzParking(value);
        CmdStartElParking(value);
        CmdCupolaParking(value);
    }

    public void CmdStopParking(boolean value){
        CmdStopMotion(value);
    }

    public void CmdStartAzParking(boolean value){
        SetAzTelPosition(MotAZ.ParkPos);
        CmdStartAzMotion(true);
    }

    public void CmdStopAzParking(boolean value){
        CmdStopMotion(value);
    }

    public void CmdStartElParking(boolean value){
        SetElTelPosition(MotEL.ParkPos);
        CmdStartElMotion(true);
    }

    public void CmdStopElParking(boolean value){
        CmdStopMotion(value);
    }

    public void CmdStartTracking(boolean value){
        SetMotionType(0);
        CmdStartMotion(value);
        SetMotionType(1);
        CmdStartMotion(value);
    }

    public void CmdStopTracking(boolean value){
        CmdStopMotion(value);
    }

    public void CmdStartPointing(boolean value){
        SetMotionType(0);
        CmdStartMotion(value);
    }

    public void CmdStopPointing(boolean value){
        CmdStopMotion(value);
    }



    // ALTRI COMANDI

    public void SetAzParkingPosition(double value){
        this.MotAZ.ParkPos = value;
    }

    public void SetElParkingPosition(double value){
        this.MotEL.ParkPos = value;
    }

    public void SetCupolaParkingPosition(double value){
        this.CUP.ParkPos = value;
    }
    

    public void CmdOpenCupola(boolean value){
        CupolaApertura();
    }

    public void CmdCloseCupola(boolean value){
        CupolaChiusura();
    }

    public void CmdStartCupolaPointing(boolean value) {
        PuntaCupola(MotAZ.TelPosition);
    }

    public void CmdCupolaParking(boolean value) {
        PuntaCupola(CUP.ParkPos);
    }

    public void CmdStopCupola(boolean value){
        FermaCupola();
    }

    public void CmdSetZeroCupola(boolean value){
        CupolaSetZero();
    }

    public void CmdSetHomePos(boolean value){
        SettaPosHome();
    }

    public void CmdStopPointMotion(boolean value){
        FermaMoto();
    }








    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------



    // NON SERVONO
    
    public double[] AzEl2HaDec(double az, double el, double phi) {
        double sa, ca, se, ce, sp, cp, x, y, z, r;
        double[] hadec = new double[2];
        double ha, dec;
        // Useful trig functions 
        az = az*D2R;
        el = el*D2R;
        phi = phi*D2R;
        sa = Math.sin(az);
        ca = Math.cos(az);
        se = Math.sin(el);
        ce = Math.cos(el);
        sp = Math.sin(phi);
        cp = Math.cos(phi);

        // HA,Dec as x,y,z 
        x = - ca * ce * sp + se * cp;
        y = - sa * ce;
        z = ca * ce * cp + se * sp;

        // To spherical 
        r = Math.sqrt(x*x + y*y);
        if (r == 0.0){
            ha = 0.0;
        }
        else{
            ha = Math.atan(y/x);
        }
        dec = Math.atan(z/r);
        ha = ha*R2H;
        if (ha < 0)
            ha = ha + 24.;
        dec = dec*R2D;

        hadec[0] = ha;
        hadec[1] = dec;

        return hadec;
    }
    
    public double[] HaDec2AzEl(double ha, double dec, double phi){
        double sh, ch, sd, cd, sp, cp, x, y, z, r, a;
        double[] azel = new double[2];
        double az, el;
        ha = ha*H2R;
        dec = dec*D2R;
        phi = phi*D2R;
        /* Useful trig functions */
        sh = Math.sin(ha);
        ch = Math.cos(ha);
        sd = Math.sin(dec);
        cd = Math.cos(dec);
        sp = Math.sin(phi);
        cp = Math.cos(phi);

        /* Az,El as x,y,z */
        x = - ch * cd * sp + sd * cp;
        y = - sh * cd;
        z = ch * cd * cp + sd * sp;

        /* To spherical */
        r = Math.sqrt(x*x + y*y);
        if (r == 0.0)
            a = 0.0;
        else
            a = Math.atan(y/x);

        if (a < 0.0)
            az = a + 2*pi;
        else
            az = a;
        az = az * R2D;
        el = Math.atan(y/x);
        el = el * R2D;

        azel[0] = az;
        azel[1] = el;

        return azel;
    }


    // FUNZIONI COMPLESSE DA FARE

    public void InitStar(){}

    public void EseguiPuntamento(){
        int setTrackCup = 0;
        int setTrackY = 0;
        int setTracX = 0;
        int noCentered = 0;
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

    public void TelescopioSettaZeroStar(){}

    // INCOMPLETO
    public void SettaPosHome(){
        long ValoX = 0, ValoY = 0;
        // modificare per tre assi?
        
        // aprire file Zeri.dat e prendere i valori degli zeri 
        long ZeroX=0, ZeroY=0; // non sono assegnati, vengono dal file?

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
    
    public void Timer(){}

    // INCOMPLETO
    public void SetZeroFromFile(){
        int valx = 1, valy = 1, valc = 1;
        // assegnati da file lastpos.dat
        byte[] istruzione = AsseX.sbld("SXZP");
        AsseX.CommandSet(istruzione,valx);
        Sleep(100);
        AsseY.CommandSet(istruzione,valy);
        Sleep(100);
        AsseCupola.CommandSet(istruzione,valc);
    }






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



    // FUNZIONI come apm

    // INCOMPLETO
    public void Exit(){
        // ofstream lastopos("lastpos.dat")
        long ValoX, ValoY, ValoC;
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

    public int PuntaCupola(double azObj){
        if (AsseCupola.CommStatus){
            int az = (int) (3600*azObj*AsseCupola.CONVFACTOR[0]);
            int Err;
            byte[] command = AsseCupola.sbld("AVSE");
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
            int az = (int) (3600*MotAZ.TelPos*AsseCupola.CONVFACTOR[0]);
            int Err;
            byte[] command = AsseCupola.sbld("AVSE");
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






    public static void main(String[] a){ // sudo chmod 777 /dev/ttyS0     sudo chmod 777 /dev/ttyUSB0
        System.out.println("\nHello World\n");
        TCS tcs = new TCS();



      }
}
