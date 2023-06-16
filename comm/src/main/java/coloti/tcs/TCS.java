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




    // GETTERS

    public boolean GetAzCw(){
        return MotAZ.EmergencySwitchCW;
    } // ClockWise
    
    public boolean GetAzCcw(){
        return MotAZ.EmergencySwitchCCW;
    } // CounterClockWise
    
    public boolean GetElHigh(){
        return MotEL.EmergencySwitchHigh;
    }
    
    public boolean GetElLow(){
        return MotEL.EmergencySwitchLow;
    }

    public boolean GetStopButton(){
        return GEN.StopBotton;
    }
    
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
        return MotAZ.MotorStatus; // cumulative status of the AZ motors: 0=both disabled; 1=both enabled; 2=degraded state i.e. 1 enabled; 1 in fault; 3=both in fault
    }

    public int GetAzMotorEncoderStatus(){
        return MotAZ.MotorEncoderStatus; // status of the AZ motor with encoder: 0=disabled; 1=enabled; 2=fault
    }

    public int GetElMotorStatus(){
        return MotEL.MotorStatus; // status of the EL motor: 0=disabled; 1=enabled; 2=fault
    }

    public double GetAzSkyPos(){
        return MotAZ.SkyPos;
    }

    public double GetAzTelPos(){
        return MotAZ.TelPos;
    }

    public double GetAzMotorTelPos(){
        return MotAZ.MotorTelPos;
    }

    public double GetAzActVel(){
        return MotAZ.ActualVel;
    }

    public double GetAzActAcc(){
        return MotAZ.ActualAcc;
    }

    public double GetAzCommandedPos(){
        return MotAZ.CommandedPos;
    }

    public double GetAzCommandedVel(){
        return MotAZ.CommandedVel;
    }

    public double GetAzCommandedAcc(){
        return MotAZ.CommandedAcc;
    }

    public double GetElSkyPos(){
        return MotEL.SkyPos;
    }

    public double GetElTelPos(){
        return MotEL.TelPos;
    }

    public double GetElActVel(){
        return MotEL.ActualVel;
    }

    public double GetElActAcc(){
        return MotEL.ActualAcc;
    }

    public double GetElCommandedPos(){
        return MotEL.CommandedPos;
    }
    
    public double GetElCommandedVel(){
        return MotEL.CommandedVel;
    }

    public double GetElCommandedAcc(){
        return MotEL.CommandedAcc;
    }

    public int GetAzMotionState(){
        return MotAZ.MotionState;
    }

    public int GetElMotionState(){
        return MotEL.MotionState;
    }

    public boolean GetAzIsParking(){
        return MotAZ.IsParking;
    }

    public boolean GetElIsParking(){
        return MotEL.IsParking;
    }

    public boolean GetAzIsParked(){
        return MotAZ.IsParked;
    }

    public boolean GetElIsParked(){
        return MotEL.IsParked;
    }

    public double GetAzEncOffset(){
        return MotAZ.EncOffset;
    }

    public double GetElEncOffset(){
        return MotEL.EncOffset;
    }

    public boolean GetTargetOnTracking(){
        return TEL.TargetOnTracking;
    }

    public int GetTrackingMaxDuration(){
        return TEL.TrackingMaxDuration;
    }

    public int GetTrackingNodes(){
        return TEL.TrackingNodes;
    }

    public boolean GetRefractionStatus(){
        return TEL.RefractionStatus;
    }
    
    public boolean GetPointingModelStatus(){
        return TEL.PointingModelStatus;
    }

    public boolean GetTargetPointed(){
        return TEL.TargetPointed;
    }

    public boolean GetTARGET_NOT_VALID(){
        return TEL.TargetNotValid;
    }

    public double GetAzPointingOffset(){
        return MotAZ.PointingOffset;
    }

    public double GetElPointingOffset(){
        return MotEL.PointingOffset;
    }
    
    public double GetAzTpoingCorrection(){
        return MotAZ.TPointCorrection;
    }

    public double GetElTpoingCorrection(){
        return MotEL.TPointCorrection;
    }
    
    public double GetRefractionCorrection(){
        return TEL.RefractionCorrection;
    }
    
    public double GetTimeToTarget(){
        return TEL.TimeToTarget;
    }
    
    public boolean GetSimulationActive(){
        return TEL.SimulationActive;
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
    
    public String GetAzStartParkingInfo(){
        return MotAZ.StartParkingInfo;
    }
    
    public String GetAzStopParkingInfo(){
        return MotAZ.StopParkingInfo;
    }
    
    public String GetElStartParkingInfo(){
        return MotEL.StartParkingInfo;
    }
    
    public String GetElStopParkingInfo(){
        return MotEL.StopParkingInfo;
    }    

    public String GetStartParkingInfo(){
        return TEL.StartParkingInfo;
    }
    
    public String GetStopParkingInfo(){
        return TEL.StopParkingInfo;
    }

    public String GetStartTrackingInfo(){
        return TEL.StartTrackingInfo;
    }
    
    public String GetStopTrackingInfo(){
        return TEL.StopTrackingInfo;
    }    

    public String GetUPDATE_TRAJECTORY_INFO(){
        return TEL.UpdateTrajectoryInfo;
    }

    public String GetStartPointingInfo(){
        return TEL.StartPointingInfo;
    }
    
    public String GetStopPointingInfo(){
        return TEL.StopPointingInfo;
    }

    public String GetAzResetAxisInfo(){
        return MotAZ.ResetAxisInfo;
    }
    
    public String GetElResetAxisInfo(){
        return MotEL.ResetAxisInfo;
    }

    //GEN

    public String GetResetAlarmInfo(){
        return GEN.ResetAlarmsInfo;
    }
    
    public String GetPCShutdownInfo(){
        return GEN.PCShutdownInfo;
    }
    
    public String GetPcRestartInfo(){
        return GEN.PCRestartInfo;
    }
    
    public String GetM2onInfo(){
        return GEN.M2onInfo;
    }
    
    public String GetM2offInfo(){
        return GEN.M2offInfo;
    }
    
    public String GetDrive400VAConInfo(){
        return GEN.Drive400VAConInfo;
    }
    
    public String GetDrive400VACoffInfo(){
        return GEN.Drive400VACoffInfo;
    }
    
    public String GetPMConInfo(){
        return GEN.PMConInfo;
    }
    
    public String GetPMCoffInfo(){
        return GEN.PMCoffInfo;
    }
    
    public String GetClearErrorBufferInfo(){
        return GEN.ClearErrorBufferInfo;
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
    
    public String GetSwVersion(){
        return GEN.SwVersion;
    }
    
    public String GetLogMessage(){
        return GEN.LogMessage;
    }





    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------




    // SETTERS 

    public void SetAzTelPosition(double value){
        this.MotAZ.TelPosition = value;
    }

    public void SetAzSkyPosition(double value){
        this.MotAZ.SkyPosition = value;
    }

    public void SetAzJogDirection(int value){
        this.MotAZ.JogDirection = value;
    }

    public void SetAzJogVelocity(double value){
        this.MotAZ.JogVelocity = value;
    }

    public void SetElTelPosition(double value){
        this.MotEL.TelPosition = value;
    }

    public void SetElSkyPosition(double value){
        this.MotEL.SkyPosition = value;
    }

    public void SetElJogDirection(int value){
        this.MotEL.JogDirection = value;
    }

    public void SetElJogVelocity(double value){
        this.MotEL.JogVelocity = value;
    }
    
    public void SetMotionType(int value){
        this.TEL.MotionType = value;
    }

    public void SetAzPositionTypeSky(boolean value){
        this.MotAZ.PositionTypeSky = value;
    }

    public void SetAzSlewVelocity(double value){
        this.MotAZ.SlewVelocity = value;        
    }

    public void SetAzSlewAcceleration(double value){
        this.MotAZ.SlewAceleration = value;        
    }

    public void SetAzSlewDeceleration(double value){
        this.MotAZ.SlewDeceleration = value;        
    }

    public void SetElSlewVelocity(double value){
        this.MotEL.SlewVelocity = value;        
    }

    public void SetElSlewAcceleration(double value){
        this.MotEL.SlewAceleration = value;        
    }

    public void SetElSlewDeceleration(double value){
        this.MotEL.SlewDeceleration = value;        
    }

    public void SetAzAbsoluteEncOffset(double value){
        this.MotAZ.AbsEncOffset = value;
    }

    public void SetAzIncrementalEncOffset(double value){
        this.MotAZ.IncrementalEncOffset = value;
    }

    public void SetElEncoderOffset(double value){
        this.MotEL.EncoderOffset = value;
    }

    public void SetTrackFollowingError(double value){
        this.GEN.TrackFollowingError = value;
    }

    public void SetAzMinAcc(double value){
        this.MotAZ.MinAcc = value;
    }

    public void SetAzMaxAcc(double value){
        this.MotAZ.MaxAcc = value;
    }

    public void SetAzMinDec(double value){
        this.MotAZ.MinDec = value;
    }

    public void SetAzMaxDec(double value){
        this.MotAZ.MaxDec = value;
    }
    
    public void SetAzMinVel(double value){
        this.MotAZ.MinVel = value;
    }

    public void SetAzMaxVel(double value){
        this.MotAZ.MaxVel = value;
    }

    public void SetAzTelMinPos(double value){
        this.MotAZ.TelMinPos = value;
    }

    public void SetAzTelMaxPos(double value){
        this.MotAZ.TelMaxPos = value;
    }

    public void SetAzSkyMinPos(double value){
        this.MotAZ.SkyMinPos = value;
    }

    public void SetAzSkyMaxPos(double value){
        this.MotAZ.SkyMaxPos = value;
    }

    public void SetElMinAcc(double value){
        this.MotEL.MinAcc = value;
    }

    public void SetElMaxAcc(double value){
        this.MotEL.MaxAcc = value;
    }

    public void SetElMinDec(double value){
        this.MotEL.MinDec = value;
    }

    public void SetElMaxDec(double value){
        this.MotEL.MaxDec = value;
    }
    
    public void SetElMinVel(double value){
        this.MotEL.MinVel = value;
    }

    public void SetElMaxVel(double value){
        this.MotEL.MaxVel = value;
    }

    public void SetElTelMinPos(double value){
        this.MotEL.TelMinPos = value;
    }

    public void SetElTelMaxPos(double value){
        this.MotEL.TelMaxPos = value;
    }

    public void SetElSkyMinPos(double value){
        this.MotEL.SkyMinPos = value;
    }

    public void SetElSkyMaxPos(double value){
        this.MotEL.SkyMaxPos = value;
    }

    public void SetAzLsOpCwPos(double value){
        this.MotAZ.LsOpCwPos = value;
    }

    public void SetAzLsOpCcwPos(double value){
        this.MotAZ.LsOpCcwPos = value;
    }

    public void SetAzPreLsOpCwPos(double value){
        this.MotAZ.PreLsOpCwPos = value;
    }

    public void SetAzPreLsOpCcwPos(double value){
        this.MotAZ.PreLsOpCcwPos = value;
    }

    public void SetElLsOpLowPos(double value){
        this.MotEL.LsOpLowPos = value;
    }

    public void SetElLsOpHighPos(double value){
        this.MotEL.LsOpHighPos = value;
    }

    public void SetElPreLsOpLowPos(double value){
        this.MotEL.PreLsOpLowPos = value;
    }

    public void SetElPreLsOpHighPos(double value){
        this.MotEL.PreLsOpHighPos = value;
    }

    public void SetAzLsEmergCwPos(double value){
        this.MotAZ.LsEmergCwPos = value;
    }

    public void SetAzLsEmergCcwPos(double value){
        this.MotAZ.LsEmergCcwPos = value;
    }

    public void SetElLsEmergLowPos(double value){
        this.MotEL.LsEmergLowPos = value;
    }

    public void SetElLsEmergHighPos(double value){
        this.MotEL.LsEmergHighPos = value;
    }

    public void SetAzCounterTorque(double value){
        this.MotAZ.CounterTorque = value;
    }

    public void SetAzServoCoeff1(double value){
        this.MotAZ.ServoCoeff1 = value;
    }

    public void SetAzServoCoeff2(double value){
        this.MotAZ.ServoCoeff2 = value;
    }

    public void SetAzServoCoeff3(double value){
        this.MotAZ.ServoCoeff3 = value;
    }

    public void SetElServoCoeff1(double value){
        this.MotEL.ServoCoeff1 = value;
    }

    public void SetElServoCoeff2(double value){
        this.MotEL.ServoCoeff2 = value;
    }

    public void SetElServoCoeff3(double value){
        this.MotEL.ServoCoeff3 = value;
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

    public void SetParkingStowPinMode(boolean value){
        this.GEN.ParkingStowPinMode = value;
    }

    public void SetAzParkPos(double value){
        this.MotAZ.ParkPos = value;
    }

    public void SetElParkPos(double value){
        this.MotEL.ParkPos = value;
    }

    public void SetResetHeartBeatInterval(int value){
        this.GEN.ResetHeartBeatInterval = value;
    }

    public void SetEnableSunAvoidanceWindow(boolean value){
        this.GEN.EnableSunAvoidanceWindow = value;
    }
    
    public void SetTPointCoeff1(double value){
        this.GEN.TPointCoeff1 = value;
    }

    public void SetTPointCoeff2(double value){
        this.GEN.TPointCoeff2 = value;
    }
    
    public void SetTPointCoeff3(double value){
        this.GEN.TPointCoeff3 = value;
    }
    
    public void SetTPointCoeff4(double value){
        this.GEN.TPointCoeff4 = value;
    }
    
    public void SetTPointCoeff5(double value){
        this.GEN.TPointCoeff5 = value;
    }
    
    public void SetTPointCoeff6(double value){
        this.GEN.TPointCoeff6 = value;
    }
    
    public void SetTPointCoeff7(double value){
        this.GEN.TPointCoeff7 = value;
    }
    
    public void SetTPointCoeff8(double value){
        this.GEN.TPointCoeff8 = value;
    }
    
    public void SetTPointCoeff9(double value){
        this.GEN.TPointCoeff9 = value;
    }
    
    public void SetTPointCoeff10(double value){
        this.GEN.TPointCoeff10 = value;
    }
    
    public void SetTPointCoeff11(double value){
        this.GEN.TPointCoeff11 = value;
    }
    
    public void SetTPointCoeff12(double value){
        this.GEN.TPointCoeff12 = value;
    }
    
    public void SetTPointCoeff13(double value){
        this.GEN.TPointCoeff13 = value;
    }
    
    public void SetTPointCoeff14(double value){
        this.GEN.TPointCoeff14 = value;
    }
    
    public void SetTPointCoeff15(double value){
        this.GEN.TPointCoeff15 = value;
    }
    
    public void SetTPointCoeff16(double value){
        this.GEN.TPointCoeff16 = value;
    }
    
    public void SetTPointCoeff17(double value){
        this.GEN.TPointCoeff17 = value;
    }
    
    public void SetTPointCoeff18(double value){
        this.GEN.TPointCoeff18 = value;
    }
    
    public void SetTPointCoeff19(double value){
        this.GEN.TPointCoeff19 = value;
    }
    
    public void SetTPointCoeff20(double value){
        this.GEN.TPointCoeff20 = value;
    }
    
    public void SetTPointCoeff21(double value){
        this.GEN.TPointCoeff21 = value;
    }
    
    public void SetTPointCoeff22(double value){
        this.GEN.TPointCoeff22 = value;
    }
    
    public void SetTPointCoeff23(double value){
        this.GEN.TPointCoeff23 = value;
    }
    
    public void SetTPointCoeff24(double value){
        this.GEN.TPointCoeff24 = value;
    }
    
    public void SetTPointCoeff25(double value){
        this.GEN.TPointCoeff25 = value;
    }

    public void SetTrajectoryGenerationMode(int value){
        this.TEL.TrajectoryGenerationMode = value;
    }

    public void SetPointingModelOnOff(boolean value){
        this.TEL.PointingModelOnOff = value;
    }

    public void SetTrackingDuration(int value){
        this.TEL.TrackingDuration = value;
    }

    public void SetTrajectoryNodeArray(double value){
        this.TEL.TrajectoryNodeArray = value;
    }

    public void SetAzOffset(double value){
        this.MotAZ.Offset = value;
    }

    public void SetElOffset(double value){
        this.MotEL.Offset = value;
    }

    public void SetRefractionOnOff(boolean value){
        this.TEL.Refraction = value;
    }
    
    public void SetTargetName(String value){
        this.TEL.TargetName = value;
    }

    public void SetTargetRA(double value){
        this.TEL.TargetRA = value;
    }

    public void SetTargetDEC(double value){
        this.TEL.TargetDEC = value;
    }
    
    public void SetTargetPmRA(double value){
        this.TEL.TargetPmRA = value;
    }

    public void SetTargetPmDEC(double value){
        this.TEL.TargetPmDEC = value;
    }

    public void SetTargetPX(double value){
        this.TEL.TargetPX = value;
    }

    public void SetTargetRV(double value){
        this.TEL.TargetRV = value;
    }

    public void SetTargetEquinox(double value){
        this.TEL.TargetEquinox = value;
    }

    public void SetTargetEpoch(double value){
        this.TEL.TargetEpoch = value;
    }

    public void SetTargetCoordFrame(int value){
        this.TEL.TargetCoordFrame = value;
    }

    public void SetTargetCoordType(int value){
        this.TEL.TargetCoordType = value;
    }

    public void SetWeatherTemp(double value){
        this.GEN.WeatherTemp = value;
    }

    public void SetWeatherPr(double value){
        this.GEN.WeatherPr = value;
    }

    public void SetWeatherHum(double value){
        this.GEN.WeatherHum = value;
    }

    public void SetWeatherWi(double value){
        this.GEN.WeatherWi = value;
    }

    public void SetWeatherWiDir(double value){
        this.GEN.WeatherWiDir = value;
    }

    public void SetWeatherWLen(double value){
        this.GEN.WeatherWlen = value;
    }

    public void SetIersDut1(double value){
        this.GEN.IersDut1 = value;
    }

    public void SetIersTaiUtc(double value){
        this.GEN.IersTaiUtc = value;
    }

    public void SetIersXpp(double value){
        this.GEN.IersXpp = value;
    }

    public void SetIersYpp(double value){
        this.GEN.IersYpp = value;
    }
    
    public void SetElObservLimitMin(double value){
        this.MotEL.ObservLimitMin = value;
    }

    public void SetElObservLimitMax(double value){
        this.MotEL.ObservLimitMax = value;
    }




    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------



    // Getters dei setters

    public double GetAzTelPosition(){
        return MotAZ.TelPosition;
    }

    public double GetAzSkyPosition(){
        return MotAZ.SkyPosition;
    }

    public int GetAzJogDirection(){
        return MotAZ.JogDirection;
    }

    public double GetAzJogVelocity(){
        return MotAZ.JogVelocity;
    }

    public double GetElTelPosition(){
        return MotEL.TelPosition;
    }

    public double GetElSkyPosition(){
        return MotEL.SkyPosition;
    }

    public int GetElJogDirection(){
        return MotEL.JogDirection;
    }

    public double GetElJogVelocity(){
        return MotEL.JogVelocity;
    }
    
    public int GetMotionType(){
        return TEL.MotionType;
    }

    public boolean GetAzPositionTypeSky(){
        return MotAZ.PositionTypeSky;
    }

    public double GetAzSlewVelocity(){
        return MotAZ.SlewVelocity;        
    }

    public double GetAzSlewAcceleration(){
        return MotAZ.SlewAceleration;        
    }

    public double GetAzSlewDeceleration(){
        return MotAZ.SlewDeceleration;        
    }

    public double GetElSlewVelocity(){
        return MotEL.SlewVelocity;        
    }

    public double GetElSlewAcceleration(){
        return MotEL.SlewAceleration;        
    }

    public double GetElSlewDeceleration(){
        return MotEL.SlewDeceleration;        
    }

    public double GetAzAbsoluteEncOffset(){
        return MotAZ.AbsEncOffset;
    }

    public double GetAzIncrementalEncOffset(){
        return MotAZ.IncrementalEncOffset;
    }

    public double GetElEncoderOffset(){
        return MotEL.EncoderOffset;
    }

    public double GetTrackFollowingError(){
        return GEN.TrackFollowingError;
    }

    public double GetAzMinAcc(){
        return MotAZ.MinAcc;
    }

    public double GetAzMaxAcc(){
        return MotAZ.MaxAcc;
    }

    public double GetAzMinDec(){
        return MotAZ.MinDec;
    }

    public double GetAzMaxDec(){
        return MotAZ.MaxDec;
    }
    
    public double GetAzMinVel(){
        return MotAZ.MinVel;
    }

    public double GetAzMaxVel(){
        return MotAZ.MaxVel;
    }

    public double GetAzTelMinPos(){
        return MotAZ.TelMinPos;
    }

    public double GetAzTelMaxPos(){
        return MotAZ.TelMaxPos;
    }

    public double GetAzSkyMinPos(){
        return MotAZ.SkyMinPos;
    }

    public double GetAzSkyMaxPos(){
        return MotAZ.SkyMaxPos;
    }

    public double GetElMinAcc(){
        return MotEL.MinAcc;
    }

    public double GetElMaxAcc(){
        return MotEL.MaxAcc;
    }

    public double GetElMinDec(){
        return MotEL.MinDec;
    }

    public double GetElMaxDec(){
        return MotEL.MaxDec;
    }
    
    public double GetElMinVel(){
        return MotEL.MinVel;
    }

    public double GetElMaxVel(){
        return MotEL.MaxVel;
    }

    public double GetElTelMinPos(){
        return MotEL.TelMinPos;
    }

    public double GetElTelMaxPos(){
        return MotEL.TelMaxPos;
    }

    public double GetElSkyMinPos(){
        return MotEL.SkyMinPos;
    }

    public double GetElSkyMaxPos(){
        return MotEL.SkyMaxPos;
    }

    public double GetAzLsOpCwPos(){
        return MotAZ.LsOpCwPos;
    }

    public double GetAzLsOpCcwPos(){
        return MotAZ.LsOpCcwPos;
    }

    public double GetAzPreLsOpCwPos(){
        return MotAZ.PreLsOpCwPos;
    }

    public double GetAzPreLsOpCcwPos(){
        return MotAZ.PreLsOpCcwPos;
    }

    public double GetElLsOpLowPos(){
        return MotEL.LsOpLowPos;
    }

    public double GetElLsOpHighPos(){
        return MotEL.LsOpHighPos;
    }

    public double GetElPreLsOpLowPos(){
        return MotEL.PreLsOpLowPos;
    }

    public double GetElPreLsOpHighPos(){
        return MotEL.PreLsOpHighPos;
    }

    public double GetAzLsEmergCwPos(){
        return MotAZ.LsEmergCwPos;
    }

    public double GetAzLsEmergCcwPos(){
        return MotAZ.LsEmergCcwPos;
    }

    public double GetElLsEmergLowPos(){
        return MotEL.LsEmergLowPos;
    }

    public double GetElLsEmergHighPos(){
        return MotEL.LsEmergHighPos;
    }

    public double GetAzCounterTorque(){
        return MotAZ.CounterTorque;
    }

    public double GetAzServoCoeff1(){
        return MotAZ.ServoCoeff1;
    }

    public double GetAzServoCoeff2(){
        return MotAZ.ServoCoeff2;
    }

    public double GetAzServoCoeff3(){
        return MotAZ.ServoCoeff3;
    }

    public double GetElServoCoeff1(){
        return MotEL.ServoCoeff1;
    }

    public double GetElServoCoeff2(){
        return MotEL.ServoCoeff2;
    }

    public double GetElServoCoeff3(){
        return MotEL.ServoCoeff3;
    }

    public double GetObserverLat(){
        return OSS.Latitudine; // oppure disaccoppiare: ObserverLat 
    }

    public double GetObserverLong(){
        return OSS.Longitudine;
    }

    public int GetObserverAlt(){
        return OSS.Altitudine;
    }

    public boolean GetParkingStowPinMode(){
        return GEN.ParkingStowPinMode;
    }

    public double GetAzParkPos(){
        return MotAZ.ParkPos;
    }

    public double GetElParkPos(){
        return MotEL.ParkPos;
    }

    public int GetResetHeartBeatInterval(){
        return GEN.ResetHeartBeatInterval;
    }

    public boolean GetEnableSunAvoidanceWindow(){
        return GEN.EnableSunAvoidanceWindow;
    }

    public double GetTPointCoeff1(){
        return GEN.TPointCoeff1;
    }

    public double GetTPointCoeff2(){
        return GEN.TPointCoeff2;
    }
    
    public double GetTPointCoeff3(){
        return GEN.TPointCoeff3;
    }
    
    public double GetTPointCoeff4(){
        return GEN.TPointCoeff4;
    }
    
    public double GetTPointCoeff5(){
        return GEN.TPointCoeff5;
    }
    
    public double GetTPointCoeff6(){
        return GEN.TPointCoeff6;
    }
    
    public double GetTPointCoeff7(){
        return GEN.TPointCoeff7;
    }
    
    public double GetTPointCoeff8(){
        return GEN.TPointCoeff8;
    }
    
    public double GetTPointCoeff9(){
        return GEN.TPointCoeff9;
    }
    
    public double GetTPointCoeff10(){
        return GEN.TPointCoeff10;
    }
    
    public double GetTPointCoeff11(){
        return GEN.TPointCoeff11;
    }
    
    public double GetTPointCoeff12(){
        return GEN.TPointCoeff12;
    }
    
    public double GetTPointCoeff13(){
        return GEN.TPointCoeff13;
    }
    
    public double GetTPointCoeff14(){
        return GEN.TPointCoeff14;
    }
    
    public double GetTPointCoeff15(){
        return GEN.TPointCoeff15;
    }
    
    public double GetTPointCoeff16(){
        return GEN.TPointCoeff16;
    }
    
    public double GetTPointCoeff17(){
        return GEN.TPointCoeff17;
    }
    
    public double GetTPointCoeff18(){
        return GEN.TPointCoeff18;
    }
    
    public double GetTPointCoeff19(){
        return GEN.TPointCoeff19;
    }
    
    public double GetTPointCoeff20(){
        return GEN.TPointCoeff20;
    }
    
    public double GetTPointCoeff21(){
        return GEN.TPointCoeff21;
    }
    
    public double GetTPointCoeff22(){
        return GEN.TPointCoeff22;
    }
    
    public double GetTPointCoeff23(){
        return GEN.TPointCoeff23;
    }
    
    public double GetTPointCoeff24(){
        return GEN.TPointCoeff24;
    }
    
    public double GetTPointCoeff25(){
        return GEN.TPointCoeff25;
    }

    public int GetTrajectoryGenerationMode(){
        return TEL.TrajectoryGenerationMode;
    }

    public boolean GetPointingModelOnOff(){
        return TEL.PointingModelOnOff;
    }

    public int GetTrackingDuration(){
        return TEL.TrackingDuration;
    }

    public double GetTrajectoryNodeArray(){
        return TEL.TrajectoryNodeArray;
    }

    public double GetAzOffset(){
        return MotAZ.Offset;
    }

    public double GetElOffset(){
        return MotEL.Offset;
    }

    public boolean GetRefractionOnOff(){
        return TEL.Refraction;
    }
    
    public String GetTargetName(){
        return TEL.TargetName;
    }

    public double GetTargetRA(){
        return TEL.TargetRA;
    }

    public double GetTargetDEC(){
        return TEL.TargetDEC;
    }
    
    public double GetTargetPmRA(){
        return TEL.TargetPmRA;
    }

    public double GetTargetPmDEC(){
        return TEL.TargetPmDEC;
    }

    public double GetTargetPX(){
        return TEL.TargetPX;
    }

    public double GetTargetRV(){
        return TEL.TargetRV;
    }

    public double GetTargetEquinox(){
        return TEL.TargetEquinox;
    }

    public double GetTargetEpoch(){
        return TEL.TargetEpoch;
    }

    public int GetTargetCoordFrame(){
        return TEL.TargetCoordFrame;
    }

    public int GetTargetCoordType(){
        return TEL.TargetCoordType;
    }

    public double GetWeatherTemp(){
        return GEN.WeatherTemp;
    }

    public double GetWeatherPr(){
        return GEN.WeatherPr;
    }

    public double GetWeatherHum(){
        return GEN.WeatherHum;
    }

    public double GetWeatherWi(){
        return GEN.WeatherWi;
    }

    public double GetWeatherWiDir(){
        return GEN.WeatherWiDir;
    }

    public double GetWeatherWLen(){
        return GEN.WeatherWlen;
    }

    public double GetIersDut1(){
        return GEN.IersDut1;
    }

    public double GetIersTaiUtc(){
        return GEN.IersTaiUtc;
    }

    public double GetIersXpp(){
        return GEN.IersXpp;
    }

    public double GetIersYpp(){
        return GEN.IersYpp;
    }
    
    public double GetElObservLimitMin(){
        return MotEL.ObservLimitMin;
    }

    public double GetElObservLimitMax(){
        return MotEL.ObservLimitMax;
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
            AsseX.SetMotorOn("X");
            err = AsseX.GetMotEncPos("X");
            ValoX = AsseX.VALUECR;
        }
    }

    public void CmdDisableAzMotors(boolean value){
        int err;
        long ValoX;
        if (AsseX.CommStatus){
            if (AsseX.IsMoving("X") == 1){
                AsseX.StopMove("X");
            }
            AsseX.SetMotorOff("X");
            err = AsseX.GetMotEncPos("X");
            ValoX = AsseX.VALUECR;
            AsseX.CloseComm();
        }
    }

    public void CmdEnableElMotors(boolean value){
        int err;
        long ValoY;
        if (!AsseY.CommStatus){
            AsseY.OpenCommunications();
            AsseY.SetMotorOn("X");
            err = AsseY.GetMotEncPos("X");
            ValoY = AsseY.VALUECR;
        }
    }

    public void CmdDisableElMotors(boolean value){
        int err;
        long ValoY;
        if (AsseY.CommStatus){
            if (AsseY.IsMoving("X") == 1){
                AsseY.StopMove("X");
            }
            AsseY.SetMotorOff("X");
            err = AsseY.GetMotEncPos("X");
            ValoY = AsseY.VALUECR;
            AsseY.CloseComm();
        }
    }


    public void CmdStartMotion(boolean value){
        if (AsseX.CommStatus && AsseY.CommStatus){

            AsseX.StopMove("X");
            if(AsseX.IsMoving("X") == 1)
                Sleep(200);
            AsseY.StopMove("X");
            if(AsseY.IsMoving("X") == 1)
                Sleep(200);

            AsseX.SetSlewMode("X");
            AsseY.SetSlewMode("X");
            AsseX.SetMotAcc("X", MotAZ.MaxAcc);
            AsseX.SetMotDec("X", MotAZ.MaxAcc);
            AsseY.SetMotAcc("X", MotEL.MaxAcc);
            AsseY.SetMotDec("X", MotEL.MaxAcc);

            AsseX.Move("X", TEL.TargetRA, MotAZ.SlewVelocity);
            AsseY.Move("X", TEL.TargetDEC, MotEL.SlewVelocity);

            Sleep(300);

            PuntaCupola(TEL.TargetRA);
        }
    }

    public void CmdStopMotion(boolean value){
        if (AsseX.IsMoving("X") == 1)
            AsseX.StopMove("X");
        if (AsseY.IsMoving("X") == 1)
            AsseY.StopMove("X");
        FermaCupola();
    }

    public void CmdStartAzMotion(boolean value){
        if (AsseX.CommStatus){
            AsseX.StopMove("X");
            if(AsseX.IsMoving("X") == 1)
                Sleep(200);

            AsseX.SetSlewMode("X");
            AsseX.SetMotAcc("X", MotAZ.MaxAcc);
            AsseX.SetMotDec("X", MotAZ.MaxAcc);

            AsseX.Move("X", TEL.TargetRA, MotAZ.SlewVelocity);

            Sleep(300);

            PuntaCupola(TEL.TargetRA);
        }
    }
    
    public void CmdStopAzMotion(boolean value){
        if (AsseX.IsMoving("X") == 1)
            AsseX.StopMove("X");
        FermaCupola();
    }

    public void CmdStartElMotion(boolean value){
        if (AsseY.CommStatus){
            AsseY.StopMove("X");
            if(AsseY.IsMoving("X") == 1)
                Sleep(200);

            AsseY.SetSlewMode("X");
            AsseY.SetMotAcc("X", MotEL.MaxAcc);
            AsseY.SetMotDec("X", MotEL.MaxAcc);

            AsseY.Move("X", TEL.TargetDEC, MotEL.SlewVelocity);

            Sleep(300);
        }
    }
    
    public void CmdStopElMotion(boolean value){
        if (AsseY.IsMoving("X") == 1)
            AsseY.StopMove("X");
    }

    public void CmdEmergencyStop(boolean value){ /* ? */ }
    public void CmdStartAzEncInit(boolean value){ /* ? */ }
    public void CmdStopAzEncInit(boolean value){ /* ? */ }

    public void CmdStartParking(boolean value){}
    public void CmdStopParking(boolean value){}
    public void CmdStartAzParking(boolean value){}
    public void CmdStopAzParking(boolean value){}
    public void CmdStartElParking(boolean value){}
    public void CmdStopElParking(boolean value){}

    public void CmdStartTracking(boolean value){}
    public void CmdStopTracking(boolean value){}
    public void CmdUpdateTrajectory(boolean value){}

    public void CmdStartPointing(boolean value){}
    public void CmdStopPointing(boolean value){}

    public void CmdResetAlarms(boolean value){}
    public void CmdResetAzAxis(boolean value){}
    public void CmdResetElAxis(boolean value){}

    public void CmdPCshutdown(boolean value){}
    public void CmdPCrestart(boolean value){}

    public void CmdM2on(boolean value){}
    public void CmdM2off(boolean value){}

    public void CmdClearErrorBuffer(boolean value){}
    public void CmdSaveParameters(boolean value){}
    public void CmdResetParameters(boolean value){}


    // ALTRI COMANDI

    public void CmdOpenCupola(boolean value){
        CupolaApertura();
    }

    public void CmdCloseCupola(boolean value){
        CupolaChiusura();
    }

    public void CmdStartCupolaPointing(boolean value) {
        PuntaCupola(TEL.TargetRA);
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
        if (AsseX.CommStatus){
            // killertimer (2)
            AsseX.StopMove(X);
            if (AsseX.IsMoving(X) == 1){
                Sleep(200) ;
            }

            AsseY.StopMove(X);
            if (AsseY.IsMoving(X) == 1){
                Sleep(200) ;
            }

            AsseX.SetSlewMode(X);
            AsseY.SetSlewMode(X);

            TraiettoriaX();
            TraiettoriaY();

            //AsseX.SetMotAcc("X",TEL.MaxAccX);



            // Cupola
            //AsseX.SetMotAcc("X", TEL.Maz)

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

    public void TelescopioSetHome(){
        double valAZ, valEL;
        //calcolo astronomico
        //if (TEL.MonType == 0){}
        valAZ = (180 - TEL.TargetAZ)*3600;
        valEL = TEL.TargetEL*3600;
        this.DPX = TEL.PosX - valAZ; //desired position x
        this.DPY = TEL.PosY - valEL;

        AsseX.SetAxisZeroPos("X", valAZ);
        AsseY.SetAxisZeroPos("X", valEL);

        //modifica zeri dat file
    }

    public void TelescopioSettaZeroStar(){}


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

        AsseX.StopMove("X");
        AsseY.StopMove("X");
    }
    
    public void Timer(){}

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
    public void OnSettaZezoTelFile(){}
    public void CorreggiAZ(){}
    public void CorreggiEL(){}



    // FUNZIONI

    public void Exit(){
        // ofstream lastopos("lastpos.dat")
        long ValoX, ValoY, ValoC;
        int err;
        // KillTimer(1);
        // KillTimer(2);

        if (AsseX.CommStatus){
            if (AsseX.IsMoving("X") == 1){
                AsseX.StopMove("X");
                // update tcs log
            }
            AsseX.SetMotorOff("X");
            err = AsseX.GetMotEncPos("X");
            ValoX = AsseX.VALUECR;
            AsseX.CloseComm();
        }

        if (AsseY.CommStatus){
            if (AsseY.IsMoving("X") == 1){
                AsseY.StopMove("X");
                // update tcs log
            }
            AsseX.SetMotorOff("X");
            err = AsseX.GetMotEncPos("X");
            ValoX = AsseX.VALUECR;
            AsseX.CloseComm();
        }

        if (AsseCupola.CommStatus){
            if (CUP.StatusApertura == 1){
                CupolaChiusura();
                Sleep(8000);
            }
            
            err = AsseCupola.GetMotEncPos("X");
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
                err = AsseX.GetMotEncPos("X");
                valo = AsseX.VALUECR;
                PosX = valo/AsseX.CONVFACTOR[0] - CostX[0];
                TEL.PosX = PosX;
                PosX = (180*3600.0 - PosX);
                TEL.AZ = PosX/3600.0;
            }
            
            // caso Tel MonTipo = 1
            else{
                err = AsseX.GetMotEncPos("X");
                valo = AsseX.VALUECR;
                PosX = valo/AsseX.CONVFACTOR[0];
                TEL.PosX = PosX;
                TEL.H = PosX/54000.0;
                if (TEL.H < 0.0)
                    TEL.H += 24;
            }

            err = AsseX.GetMotVel("X");
            TEL.SlewVelX = AsseX.VelAx[0];

            err = AsseX.GetMotAcc("X");
            TEL.AccX = AsseX.AccAx[0];
        }
    }

    public void GetTelInfoY(){
        double PosY;
        long valo;
        int err;

        if (AsseY.CommStatus){
            err = AsseY.GetMotEncPos("X");
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

            err = AsseY.GetMotVel("X");
            TEL.SlewVelY = AsseY.VelAx[0];

            err = AsseY.GetMotAcc("X");
            TEL.AccY = AsseY.AccAx[0];
        }
    }

    public void GetTelInfoZ(){
        double PosZ;
        long valo;
        int err;

        if (AsseZ.CommStatus){
            err = AsseZ.GetMotEncPos("X");
            valo = AsseZ.VALUECR;
            PosZ = valo/AsseZ.CONVFACTOR[0];
            TEL.PosZ = PosZ;
            TEL.PA = PosZ/3600.0;

            err = AsseZ.GetMotVel("X");
            TEL.SlewVelZ = AsseZ.VelAx[0];

            err = AsseZ.GetMotAcc("X");
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
            AsseCupola.GetMotEncPos("X");
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
