package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class POSZERO {
    public int ZeroX;
    public int ZeroY;
    public int ZeroZ;
    public int ZeroCup;

    public POSZERO(ConfigurationClass cfg) {
        this.ZeroX = cfg.getZeroX();
        this.ZeroY = cfg.getZeroY();
        this.ZeroZ = cfg.getZeroZ();
        this.ZeroCup = cfg.getZeroCup();
    }
}
