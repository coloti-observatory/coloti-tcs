package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class CUPOLA {
    public int ControlloreCupola;
    public int CupolaEncoderRis;
    public int StadioRiduzione;

    public double Pos = 0; //arcsec
    public double AZ = 0;
    public double CommandedAZ = 0;
    public int StatusApertura;
    public int StatusRotazione;
    public double Direzione;
    public double ParkPos;
    public double TriggerAngleDome;

    public String ZeroDomeInfo;

    public CUPOLA(ConfigurationClass cfg){
        this.ControlloreCupola = cfg.getControlloreCupola();
        this.CupolaEncoderRis = cfg.getCupolaEncoderRis();
        this.StadioRiduzione = cfg.getStadioRiduzione();
    }

}
