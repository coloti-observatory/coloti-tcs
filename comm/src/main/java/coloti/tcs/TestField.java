package coloti.tcs;
import java.lang.reflect.Field;

import coloti.tcs.objclasses.CUPOLA;
import coloti.tcs.objclasses.*;
import coloti.tcs.configuration.Telescopio;


public class TestField {


    public static void main(final String[] a){
        ConfigurationClass CFG = new ConfigurationClass();
        CUPOLA CUP = new CUPOLA(CFG);

        Field field = null;
        try {
            field = CUP.getClass().getDeclaredField("ZeroDomeInfo");
        } catch (NoSuchFieldException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            field.set(CUP, "ciao");
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
