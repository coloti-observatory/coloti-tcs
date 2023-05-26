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

    public GENERALE(ConfigurationClass cfg){
        this.TipoCupola = cfg.getTipoCupola();
        this.TipoTelescopio = cfg.getTipoTelescopio();
        this.Montatura = cfg.getMontatura();
        this.TipoControlloreAssi = cfg.getTipoControlloreAssi();
        this.NumeroAssi = cfg.getNumeroAssi();
        this.NumeroControllori = cfg.getNumeroControllori();
    }


    
}
