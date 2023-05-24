package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class PADDLE {
    public int VelocitaBassa;
    public int VelocitaMedia;
    public int VelocitaAlta;

    public PADDLE(ConfigurationClass cfg) {
        this.VelocitaBassa = cfg.getVelocitaBassa();
        this.VelocitaMedia = cfg.getVelocitaMedia();
        this.VelocitaAlta = cfg.getVelocitaAlta();
    }
}
