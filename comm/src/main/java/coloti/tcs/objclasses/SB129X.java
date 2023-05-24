package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class SB129X {
    public int PortaComunicazione1;
    public int BaudRate1;
    public int PortaComunicazione2;
    public int BaudRate2;
    public int PortaComunicazione3;
    public int BaudRate3;

    public SB129X(ConfigurationClass cfg) {
        this.PortaComunicazione1 = cfg.getPortaComunicazione1();
        this.BaudRate1 = cfg.getBaudRate1();
        this.PortaComunicazione2 = cfg.getPortaComunicazione2();
        this.BaudRate2 = cfg.getBaudRate2();
        this.PortaComunicazione3 = cfg.getPortaComunicazione3();
        this.BaudRate3 = cfg.getBaudRate3();
    }
}
