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
    public double CommendedAcc;









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
    }
}
