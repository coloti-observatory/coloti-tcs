package coloti.tcs;

//import java.io.File;
//import java.io.IOException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import coloti.tcs.configuration.*;
import coloti.tcs.objclasses.*;
//import coloti.tcs.ConfigurationClass;
import java.util.concurrent.TimeUnit;
import java.lang.Math.*;

/*
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DrbgParameters.NextBytes;
import java.util.*;

import java.util.function.IntPredicate;
import javax.lang.model.util.ElementScanner6;
*/

//import coloti.tcs.ACSv5;


public class TCS {
    
    ACS AsseX, AsseY, AsseZ;
    ACS AsseCupola; //= new ACS("serial ID cupola");

    // Parametri   D = degrees, R = radians, AS = arcseconds, H = hours, S = seconds
    double pi = Math.PI;
    double D2R = pi/180.0;
    double R2D = 180.0/pi;
    double AS2R = pi/(180.0*3600.0);
    double R2AS = (180.0*3600.0)/pi;
    double H2R = pi/12.0;
    double R2H = 12.0/pi;
    double S2R = pi/(12.0*3600.0);
    double R2S = (12.0*3600.0)/pi;

    double[] CostX = new double[6];
    double[] CostY = new double[6];

    public TCS(){
        //Configure();
    }
    public TCS(boolean START){
        Configure();
    }

    // FIRST THINGS TO DO
    String X = "X";
    String Y = "Y";
    String Z = "Z";

    ConfigurationClass CFG;

    GENERALE GEN;
    OSSERVATORIO OSS;
    CUPOLA CUP;
    TELESCOPIO TEL;
    MOTOREARAZ MotAR;
    MOTOREDECAL MotDEC;
    SB129X SB;
    PADDLE PAD;
    POSZERO PZ;

    public void Configure(){
        this.CFG = new ConfigurationClass();
        this.GEN = new GENERALE(CFG);
        this.OSS = new OSSERVATORIO(CFG);
        this.CUP = new CUPOLA(CFG);
        this.TEL = new TELESCOPIO(CFG);
        this.MotAR = new MOTOREARAZ(CFG);
        this.MotDEC = new MOTOREDECAL(CFG);
        this.SB = new SB129X(CFG);
        this.PAD = new PADDLE(CFG);
        this.PZ = new POSZERO(CFG);

        // Sole
        // Luna
    }

    public void Sleep(int millisecondsTime) { // VERIFICATO 
        try {
          TimeUnit.MILLISECONDS.sleep(millisecondsTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }


    // Utili
    
    public double[] AzEl2HaDec(double az, double el, double phi) {
        double sa, ca, se, ce, sp, cp, x, y, z, r;
        double[] hadec = new double[2];
        double ha, dec;
        // Useful trig functions 
        az = az*D2R;
        el = el*D2R;
        phi = phi*D2R;
        sa = Math.sin(az);
        ca = Math.cos(az);
        se = Math.sin(el);
        ce = Math.cos(el);
        sp = Math.sin(phi);
        cp = Math.cos(phi);

        // HA,Dec as x,y,z 
        x = - ca * ce * sp + se * cp;
        y = - sa * ce;
        z = ca * ce * cp + se * sp;

        // To spherical 
        r = Math.sqrt(x*x + y*y);
        if (r == 0.0){
            ha = 0.0;
        }
        else{
            ha = Math.atan(y/x);
        }
        dec = Math.atan(z/r);
        ha = ha*R2H;
        if (ha < 0)
            ha = ha + 24.;
        dec = dec*R2D;

        hadec[0] = ha;
        hadec[1] = dec;

        return hadec;
    }
    
    public double[] HaDec2AzEl(double ha, double dec, double phi){
        double sh, ch, sd, cd, sp, cp, x, y, z, r, a;
        double[] azel = new double[2];
        double az, el;
        ha = ha*H2R;
        dec = dec*D2R;
        phi = phi*D2R;
        /* Useful trig functions */
        sh = Math.sin(ha);
        ch = Math.cos(ha);
        sd = Math.sin(dec);
        cd = Math.cos(dec);
        sp = Math.sin(phi);
        cp = Math.cos(phi);

        /* Az,El as x,y,z */
        x = - ch * cd * sp + sd * cp;
        y = - sh * cd;
        z = ch * cd * cp + sd * sp;

        /* To spherical */
        r = Math.sqrt(x*x + y*y);
        if (r == 0.0)
            a = 0.0;
        else
            a = Math.atan(y/x);

        if (a < 0.0)
            az = a + 2*pi;
        else
            az = a;
        az = az * R2D;
        el = Math.atan(y/x);
        el = el * R2D;

        azel[0] = az;
        azel[1] = el;

        return azel;
    }


    // FUNZIONI COMPLESSE DA FARE

    public void InitStar(){}

    public void EseguiPuntamento(){
        int setTrackCup = 0;
        int setTrackY = 0;
        int setTracX = 0;
        int noCentered = 0;
        if (AsseX.CommStatus){
            // killertimer (2)
            AsseX.StopMove(X);
            if (AsseX.IsMoving(X) == 1){
                Sleep(200) ;
            }

            AsseY.StopMove(X);
            if (AsseY.IsMoving(X) == 1){
                Sleep(200) ;
            }

            AsseX.SetSlewMode(X);
            AsseY.SetSlewMode(X);

            TraiettoriaX();
            TraiettoriaY();

            // Cupola
            //AsseX.SetMotAcc("X", TEL.Maz)



















        }
    }

    public void Puntamento(){}

    public void ComandiTastierino(){}

    public void TraiettoriaX(){}

    public void TraiettoriaY(){}

    











    // FUNZIONI

    public void Exit(){
        // ofstream lastopos("lastpos.dat")
        long ValoX, ValoY, ValoC;
        int err;
        // KillTimer(1);
        // KillTimer(2);

        if (AsseX.CommStatus){
            if (AsseX.IsMoving("X") == 1){
                AsseX.StopMove("X");
                // update tcs log
            }
            AsseX.SetMotorOff("X");
            err = AsseX.GetMotEncPos("X");
            ValoX = AsseX.VALUECR;
            AsseX.CloseComm();
        }

        if (AsseY.CommStatus){
            if (AsseY.IsMoving("X") == 1)
                AsseY.StopMove("X");
                // update tcs log
        }

        if (AsseCupola.CommStatus){
            if (CUP.StatusApertura == 1){
                CupolaChiusura();
                Sleep(8000);
            }
            
            err = AsseCupola.GetMotEncPos("X");
            ValoC = AsseCupola.VALUECR;
        }

        // AGGIORNARE LASTPOS E CONNESSIONE REMOTA 

        Sleep(1000);
    }




    // SEMPLICI GET FATTI

    public void GetTelInfo(){
        double ra;
        if (TEL.MonType == 0){
            GetTelInfoX();
            GetTelInfoY();
            double[] hadec = AzEl2HaDec(TEL.AZ, TEL.EL, OSS.Latitudine);
            TEL.H = hadec[0];
            TEL.DEC = hadec[1];
            double teltimegetlsathour = 0; // ???
            ra = teltimegetlsathour - TEL.H;
            if (ra < 0.0)
                ra += 24.0;
            if (ra > 24.0)
                ra -= 24.0;
            TEL.RA = ra;

            double[] azel = HaDec2AzEl(TEL.H, TEL.DEC, OSS.Latitudine);
            TEL.AZ = azel[0];
            TEL.EL = azel[1];
        }
    }

    public void GetTelInfoX(){
        double PosX;
        long valo;
        int err;

        if (AsseX.CommStatus){
            // caso TelMonTipo = 0
            if (TEL.MonType == 0){
                err = AsseX.GetMotEncPos("X");
                valo = AsseX.VALUECR;
                PosX = valo/AsseX.CONVFACTOR[0] - CostX[0];
                TEL.PosX = PosX;
                PosX = (180*3600.0 - PosX);
                TEL.AZ = PosX/3600.0;
            }
            
            // caso Tel MonTipo = 1
            else{
                err = AsseX.GetMotEncPos("X");
                valo = AsseX.VALUECR;
                PosX = valo/AsseX.CONVFACTOR[0];
                TEL.PosX = PosX;
                TEL.H = PosX/54000.0;
                if (TEL.H < 0.0)
                    TEL.H += 24;
            }

            err = AsseX.GetMotVel("X");
            TEL.SlewVelX = AsseX.VelAx[0];

            err = AsseX.GetMotAcc("X");
            TEL.AccX = AsseX.AccAx[0];
        }
    }

    public void GetTelInfoY(){
        double PosY;
        long valo;
        int err;

        if (AsseY.CommStatus){
            err = AsseY.GetMotEncPos("X");
            valo = AsseY.VALUECR;
            PosY = valo/AsseY.CONVFACTOR[0] - CostY[0];
            TEL.PosY = PosY;
            PosY = (180*3600 - PosY);
            if (TEL.MonType == 0){
                TEL.EL = PosY/3600.0;
            }
            else{
                TEL.DEC = PosY/3600.0;
            }

            err = AsseY.GetMotVel("X");
            TEL.SlewVelY = AsseY.VelAx[0];

            err = AsseY.GetMotAcc("X");
            TEL.AccY = AsseY.AccAx[0];
        }
    }

    public void GetTelInfoZ(){
        double PosZ;
        long valo;
        int err;

        if (AsseZ.CommStatus){
            err = AsseZ.GetMotEncPos("X");
            valo = AsseZ.VALUECR;
            PosZ = valo/AsseZ.CONVFACTOR[0];
            TEL.PosZ = PosZ;
            TEL.PA = PosZ/3600.0;

            err = AsseZ.GetMotVel("X");
            TEL.SlewVelZ = AsseZ.VelAx[0];

            err = AsseZ.GetMotAcc("X");
            TEL.AccZ = AsseZ.AccAx[0];
        }
    }


    // CUPOLA 

    public int CupolaApertura(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("APRICUP");
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaChiusura(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("CHIUDCUP");
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaOvest(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("SXCUP");
            CUP.StatusRotazione = 1;
            CUP.Direzione = 1;
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaEst(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("DXCUP");
            CUP.StatusRotazione = 1;
            CUP.Direzione = -1;
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaSetZero(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("HOMECUP");
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int CupolaVai(double az){

        if (AsseCupola.CommStatus){
            int azi = (int) (3600*az*AsseCupola.CONVFACTOR[0]);
            int Err;
            byte[] command = AsseCupola.sbld("AVSE");
            AsseCupola.CommandArray(command, 10, azi);
		    Err = AsseCupola.ExecProg("PUNTA");
            if (Err != -1){
                return Err;
            }
        }
        CUP.StatusRotazione = 1;
        CUP.Direzione = -1;

        return -1;
    }

    public int CupolaFerma(){
        int Err;
        if (AsseCupola.CommStatus){
            Err = AsseCupola.ExecProg("FERMACUP");
            CUP.StatusRotazione = 0;
            CUP.Direzione = 0;
        }
        else{
            Err = 0;
        }
        return Err;
    }

    public int PuntaCupola(double azObj){
        int az = (int) (3600*azObj*AsseCupola.CONVFACTOR[0]);
        int Err;
        byte[] command = AsseCupola.sbld("AVSE");
        AsseCupola.CommandArray(command, 10, az);
        Err = AsseCupola.ExecProg("PUNTA");
        if (Err != -1){
            return Err;
        }
        return -1;
    }

    public int PuntaCupolaAngle(double angle){
        int az = (int) (3600*angle*AsseCupola.CONVFACTOR[0]);
        int Err;
        byte[] command = AsseCupola.sbld("AVSE");
        AsseCupola.CommandArray(command, 10, az);
        Err = AsseCupola.ExecProg("PUNTA");
        if (Err != -1){
            return Err;
        }
        return -1;
    }

    public void GetCupolaInfo(){
        long valo;
        if (AsseCupola.CommStatus){
            AsseCupola.GetMotEncPos("X");
            valo = AsseCupola.VALUECR;
            CUP.Pos = valo/AsseCupola.CONVFACTOR[0];
            CUP.AZ = CUP.Pos/3600.0;
            if (CUP.AZ >= 360.0)
                CUP.AZ -= 360.0;
        }
    }







    public static void main(String[] a){ // sudo chmod 777 /dev/ttyS0     sudo chmod 777 /dev/ttyUSB0
        System.out.println("\nHello World\n");
        TCS tcs = new TCS();



      }
}
