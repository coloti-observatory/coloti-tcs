package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class OSSERVATORIO {
    public String NomeOsservatorio;
    public double Latitudine;
    public double Longitudine;
    public int Altitudine;
    public int Timezone;
    public int Gps;
    public int Meteo;

    public double Pressure;
    public double Temperature;
    public double Humidity;

    public OSSERVATORIO(ConfigurationClass cfg) {
        this.NomeOsservatorio = cfg.getNomeOsservatorio();
        this.Latitudine = cfg.getLatitudine();
        this.Longitudine = cfg.getLongitudine();
        this.Altitudine = cfg.getAltitudine();
        this.Timezone = cfg.getTimezone();
        this.Gps = cfg.getGps();
        this.Meteo = cfg.getMeteo();
    }
}

