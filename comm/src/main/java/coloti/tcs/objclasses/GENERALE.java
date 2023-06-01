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


    public GENERALE(ConfigurationClass cfg){
        this.TipoCupola = cfg.getTipoCupola();
        this.TipoTelescopio = cfg.getTipoTelescopio();
        this.Montatura = cfg.getMontatura();
        this.TipoControlloreAssi = cfg.getTipoControlloreAssi();
        this.NumeroAssi = cfg.getNumeroAssi();
        this.NumeroControllori = cfg.getNumeroControllori();
    }


    
}
