package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class MOTOREDECAL {
    public int RisoluzioneEncoder1;
    public int RisoluzioneEncoder2;
    public int NumeroGiriMotore;
    public double VelocitaMassima;
    public int PosizioneLimiteInf;
    public int PosizioneLimiteSup;
    public int RiduzioneMotore;
    public int PosizioneEncoder1;
    public int PosizioneEncoder2;
    //
    public boolean EmergencySwitchLow;
    public boolean EmergencySwitchHigh;
    public boolean StatusLimitSwitchLow;
    public boolean StatusLimitSwitchHigh;
    public int MotorStatus; // status of the EL motor: 0=disabled; 1=enabled; 2=fault
    public double SkyPos;
    public double TelPos;
    public double ActualVel;
    public double ActualAcc;
    public double CommandedPos;
    public double CommandedVel;
    public double CommandedAcc;
    public int MotionState; // motion state in azimuth: 0=Stopped; 1=Stopping; 2=Slewing; 3=Tracking; 4=Jogging
    public boolean IsParking;
    public boolean IsParked;
    public double EncOffset;
    public double PointingOffset;
    public double TPointCorrection;
    public String EnableMotorsInfo;
    public String DisableMotorsInfo;
    public String StartMotionInfo;
    public String StopMotionInfo;
    public String StartParkingInfo; 
    public String StopParkingInfo; 
    public String ResetAxisInfo;
    public int JogDirection;
    public double JogVelocity;
    public double TelPosition;
    public double SkyPosition;
    public double SlewVelocity;
    public double SlewAceleration;
    public double SlewDeceleration;






    public MOTOREDECAL(ConfigurationClass cfg) {
        this.RisoluzioneEncoder1 = cfg.getElRisoluzioneEncoder1();
        this.RisoluzioneEncoder2 = cfg.getElRisoluzioneEncoder2();
        this.NumeroGiriMotore = cfg.getElNumeroGiriMotore();
        this.VelocitaMassima = cfg.getElVelocitaMassima();
        this.PosizioneLimiteInf = cfg.getElPosizioneLimiteInf();
        this.PosizioneLimiteSup = cfg.getElPosizioneLimiteSup();
        this.RiduzioneMotore = cfg.getElRiduzioneMotore();
        this.PosizioneEncoder1 = cfg.getElPosizioneEncoder1();
        this.PosizioneEncoder2 = cfg.getElPosizioneEncoder2();
    }
}

