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

    public boolean EmergencySwitchLow;
    public boolean EmergencySwitchHigh;
    public boolean StatusLimitSwitchLow;
    public boolean StatusLimitSwitchHigh;
    public int MotorStatus; // status of the EL motor: 0=disabled; 1=enabled; 2=fault







    

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

