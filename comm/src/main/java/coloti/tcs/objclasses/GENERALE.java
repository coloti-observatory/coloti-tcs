package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class GENERALE {
    public int TipoCupola;
    public int TipoTelescopio;
    public int Montatura;
    public int TipoControlloreAssi;
    public int NumeroAssi;
    public int NumeroControllori;

    public boolean StopBotton;

    public String ResetAlarmsInfo;
    public String PCShutdownInfo;
    public String PCRestartInfo;
    public String M2onInfo;
    public String M2offInfo;
    public String Drive400VAConInfo;
    public String Drive400VACoffInfo;
    public String PMConInfo;
    public String PMCoffInfo;
    public String ClearErrorBufferInfo;
    public int ErrorNumber;
    public String ErrorBuffer;
    public boolean ErrorBufferOutOfRange;
    public int ErrorBufferSize;
    public int HeartBeat;
    public String SwVersion;
    public String LogMessage;
    public double TrackFollowingError;
    public boolean ParkingStowPinMode;
    public int ResetHeartBeatInterval;
    public boolean EnableSunAvoidanceWindow;

    public double TPointCoeff1;
    public double TPointCoeff2;
    public double TPointCoeff3;
    public double TPointCoeff4;
    public double TPointCoeff5;
    public double TPointCoeff6;
    public double TPointCoeff7;
    public double TPointCoeff8;
    public double TPointCoeff9;
    public double TPointCoeff10;
    public double TPointCoeff11;
    public double TPointCoeff12;
    public double TPointCoeff13;
    public double TPointCoeff14;
    public double TPointCoeff15;
    public double TPointCoeff16;
    public double TPointCoeff17;
    public double TPointCoeff18;
    public double TPointCoeff19;
    public double TPointCoeff20;
    public double TPointCoeff21;
    public double TPointCoeff22;
    public double TPointCoeff23;
    public double TPointCoeff24;
    public double TPointCoeff25;
    public double WeatherTemp;
    public double WeatherPr;
    public double WeatherHum;
    public double WeatherWi;
    public double WeatherWiDir;
    public double WeatherWlen;
    public double IersDut1;
    public double IersTaiUtc;
    public double IersXpp;
    public double IersYpp;


    public GENERALE(ConfigurationClass cfg){
        this.TipoCupola = cfg.getTipoCupola();
        this.TipoTelescopio = cfg.getTipoTelescopio();
        this.Montatura = cfg.getMontatura();
        this.TipoControlloreAssi = cfg.getTipoControlloreAssi();
        this.NumeroAssi = cfg.getNumeroAssi();
        this.NumeroControllori = cfg.getNumeroControllori();
    }


    
}
