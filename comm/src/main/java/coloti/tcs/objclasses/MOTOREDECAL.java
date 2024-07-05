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
    public int JogDirection = 1;
    public double JogVelocity = 0.001;
    public double AbsJogVelocity;
    public double TelPosition;
    public double SkyPosition;
    public double SlewVelocity = 150;
    public double SlewAcceleration;
    public double SlewDeceleration;
    public double EncoderOffset;
    public double MinAcc;
    public double MaxAcc;
    public double MinDec;
    public double MaxDec;
    public double MinVel;
    public double MaxVel;
    public double TelMinPos;
    public double TelMaxPos;
    public double SkyMinPos;
    public double SkyMaxPos;
    public double LsOpLowPos;
    public double LsOpHighPos;
    public double PreLsOpLowPos;
    public double PreLsOpHighPos;
    public double LsEmergLowPos;
    public double LsEmergHighPos;
    public double ServoCoeff1;
    public double ServoCoeff2;
    public double ServoCoeff3;
    public double ParkPos;
    public double Offset;
    public double ObservLimitMin;
    public double ObservLimitMax;


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

        this.MaxVel = this.VelocitaMassima*3600.0;
        this.MaxAcc = this.VelocitaMassima*3600.0;
    }
}

