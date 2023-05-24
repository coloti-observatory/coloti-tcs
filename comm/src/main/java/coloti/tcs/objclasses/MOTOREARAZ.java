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
