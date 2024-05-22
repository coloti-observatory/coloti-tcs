package coloti.tcs.trajectory;

import astri.astron.AstronomicalObject;
import astri.astron.SkyMapListener;
import astri.astron.SphericalCoord;
import astri.astron.Target;

public class SkyMapLstnr implements SkyMapListener{

    @Override
    public void notifyMoonPosition(SphericalCoord arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void notifyObj(AstronomicalObject arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void notifySkyPosition(SphericalCoord arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void notifySkyTarget(Target arg0) {
        // TODO Auto-generated method stub
        System.out.println(arg0);



        
    }

    @Override
    public void notifySunPosition(SphericalCoord arg0) {
        // TODO Auto-generated method stub
    }
    

}
