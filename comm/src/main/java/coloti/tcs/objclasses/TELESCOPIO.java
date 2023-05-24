package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class TELESCOPIO {
    public String NomeTelescopio;
    public int DiametroSpecchio;
    public int LunghezzaFocale;
    public int RapportoRiduzioneAR;
    public int RapportoRiduzioneDEC;
    public int RapportoRiduzioneAZ;
    public int RapportoRiduzioneAL;
    public int RapportoRiduzioneDE;
    public int CampoDiVista;

    // info
    public int MonType = 0;
    public double PosX;
    public double PosY;
    public double PosZ;
    public double H;
    public double AZ;
    public double RA;
    public double EL;
    public double DEC;
    public double PA;
    public double SlewVelX;
    public double AccX;
    public double SlewVelY;
    public double AccY;
    public double SlewVelZ;
    public double AccZ;


    public TELESCOPIO(ConfigurationClass cfg){
        this.NomeTelescopio = cfg.getNomeTelescopio();
        this.DiametroSpecchio = cfg.getDiametroSpecchio();
        this.LunghezzaFocale = cfg.getLunghezzaFocale();
        this.RapportoRiduzioneAR = cfg.getRapportoRiduzioneAR();
        this.RapportoRiduzioneDEC = cfg.getRapportoRiduzioneDEC();
        this.RapportoRiduzioneAZ = cfg.getRapportoRiduzioneAZ();
        this.RapportoRiduzioneAL = cfg.getRapportoRiduzioneAL();
        this.RapportoRiduzioneDE = cfg.getRapportoRiduzioneDE();
        this.CampoDiVista = cfg.getCampoDiVista();
    }

}
