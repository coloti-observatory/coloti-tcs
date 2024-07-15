package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class PADDLE {
    public int VelocitaBassa = 60;
    public int VelocitaMedia = 150;
    public int VelocitaAlta = 180;

    public PADDLE(ConfigurationClass cfg) {
        this.VelocitaBassa = cfg.getVelocitaBassa();
        this.VelocitaMedia = cfg.getVelocitaMedia();
        this.VelocitaAlta = cfg.getVelocitaAlta();
    }
}
