package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class MOTOREARAZ {
    public int RisoluzioneEncoder1;
    public int RisoluzioneEncoder2;
    public int NumeroGiriMotore;
    public double VelocitaMassima;
    public int PosizioneLimiteInf;
    public int PosizioneLimiteSup;
    public int RiduzioneMotore;
    public int PosizioneEncoder1;
    public int PosizioneEncoder2;
    // altro
    public boolean EmergencySwitchCW;
    public boolean EmergencySwitchCCW;
    public boolean StatusLimitSwitchCW;
    public boolean StatusLimitSwitchCCW;
    public int MotorStatus; // cumulative status of the AZ motors: 0=both disabled; 1=both enabled; 2=degraded state i.e. 1 enabled; 1 in fault; 3=both in fault
    public int MotorEncoderStatus; // status of the AZ motor with encoder: 0=disabled; 1=enabled; 2=fault
    public double SkyPos;
    public double TelPos;
    public double MotorTelPos;
    public double ActualVel; // positive CW, negative CCW
    public double ActualAcc; // positive CW, negative CCW
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
    public String StartEncInitInfo; //AZ
    public String StopEncInitInfo; //AZ
    public String StartParkingInfo; 
    public String StopParkingInfo; 
    public String ResetAxisInfo;
    public int JogDirection = 1;
    public double JogVelocity = 0.001;
    public double TelPosition;
    public double SkyPosition;
    public boolean PositionTypeSky;
    public double SlewVelocity = 150;
    public double SlewAcceleration;
    public double SlewDeceleration;
    public double AbsEncOffset;
    public double IncrementalEncOffset;
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
    public double LsOpCwPos;
    public double LsOpCcwPos;
    public double PreLsOpCwPos;
    public double PreLsOpCcwPos;
    public double LsEmergCwPos;
    public double LsEmergCcwPos;
    public double CounterTorque;
    public double ServoCoeff1;
    public double ServoCoeff2;
    public double ServoCoeff3;
    public double ParkPos;
    public double Offset;


    public MOTOREARAZ(ConfigurationClass cfg) {
        this.RisoluzioneEncoder1 = cfg.getAzRisoluzioneEncoder1();
        this.RisoluzioneEncoder2 = cfg.getAzPosizioneEncoder2();
        this.NumeroGiriMotore = cfg.getAzNumeroGiriMotore();
        this.VelocitaMassima = cfg.getAzVelocitaMassima();
        this.PosizioneLimiteInf = cfg.getAzPosizioneLimiteInf();
        this.PosizioneLimiteSup = cfg.getAzPosizioneLimiteSup();
        this.RiduzioneMotore = cfg.getAzRiduzioneMotore();
        this.PosizioneEncoder1 = cfg.getAzPosizioneEncoder1();
        this.PosizioneEncoder2 = cfg.getAzPosizioneEncoder2();

        this.MaxVel = this.VelocitaMassima*3600.0;
        this.MaxAcc = this.VelocitaMassima*3600.0;
    }
}
