package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class CUPOLA {
    public int ControlloreCupola;
    public int CupolaEncoderRis;
    public int StadioRiduzione;

    public double Pos; //arcsec
    public double AZ;
    public double CommandedAZ;
    public int StatusApertura;
    public int StatusRotazione;
    public double Direzione;
    public double ParkPos;

    public CUPOLA(ConfigurationClass cfg){
        this.ControlloreCupola = cfg.getControlloreCupola();
        this.CupolaEncoderRis = cfg.getCupolaEncoderRis();
        this.StadioRiduzione = cfg.getStadioRiduzione();
    }

}
