package coloti.tcs;


import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import coloti.tcs.configuration.*;

public class ConfigurationClass {
    
    ObjectMapper objectMapper = new ObjectMapper();
    GLOBAL global;

    public ConfigurationClass(){
        this.global = Configuration();
    }

    public GLOBAL Configuration(){
        try {
            return objectMapper.readValue(new File("/home/coloti/coloti-tcs/config.json"), GLOBAL.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // GENERALE
    public int getTipoCupola(){
        return this.global.getGenerale().getTipoCupola();
    }
    public int getTipoTelescopio(){
        return this.global.getGenerale().getTipoTelescopio();
    }
    public int getMontatura(){
        return this.global.getGenerale().getMontatura();
    }
    public int getTipoControlloreAssi(){
        return this.global.getGenerale().getTipoControlloreAssi();
    }
    public int getNumeroAssi(){
        return this.global.getGenerale().getNumeroAssi();
    }
    public int getNumeroControllori(){
        return this.global.getGenerale().getNumeroControllori();
    }

    public boolean getConnessioneAz(){
        return this.global.getGenerale().getConnessioneAz();
    }
    public boolean getConnessioneEl(){
        return this.global.getGenerale().getConnessioneEl();
    }
    public boolean getConnessioneDome(){
        return this.global.getGenerale().getConnessioneDome();
    }
    public boolean getConnessioneMeteo(){
        return this.global.getGenerale().getConnessioneMeteo();
    }

    public String getIdSerialAz(){
        return this.global.getGenerale().getIdSerialAz();
    }
    public String getIdSerialEl(){
        return this.global.getGenerale().getIdSerialEl();
    }
    public String getIdSerialDome(){
        return this.global.getGenerale().getIdSerialDome();
    }
    public String getIdSerialWeather(){
        return this.global.getGenerale().getIdSerialWeather();
    }

        // set
    /*
    public void setTipoCupola(int value){
        this.global.getGenerale().setTipoCupola(value);
    }
    public void setTipoTelescopio(int value){
        this.global.getGenerale().setTipoTelescopio(value);
    }
    public void setMontatura(int value){
        this.global.getGenerale().setMontatura(value);
    }
    public void setTipoControlloreAssi(int value){
        this.global.getGenerale().setTipoControlloreAssi(value);
    }
    public void setNumeroAssi(int value){
        this.global.getGenerale().setNumeroAssi(value);
    }
    public void setNumeroControllori(int value){
        this.global.getGenerale().setNumeroControllori(value);
    }
    */

    // OSSERVATORIO
    public String getNomeOsservatorio(){
        return this.global.getOsservatorio().getNome();
    }
    public double getLatitudine(){
        return this.global.getOsservatorio().getLatitudine();
    }
    public double getLongitudine(){
        return this.global.getOsservatorio().getLongitudine();
    }
    public int getAltitudine(){
        return this.global.getOsservatorio().getAltitudine();
    }
    public int getTimezone(){
        return this.global.getOsservatorio().getTimezone();
    }
    public int getGps(){
        return this.global.getOsservatorio().getGps();
    }
    public int getMeteo(){
        return this.global.getOsservatorio().getMeteo();
    }
        // set
    /*
    public void setNomeOsservatorio(String nome){
        this.global.getOsservatorio().setNome(nome);
    }
    public void setLatitudine(double value){
        this.global.getOsservatorio().setLatitudine(value);
    }
    public void setLongitudine(double value){
        this.global.getOsservatorio().setLongitudine(value);
    }
    public void setAltitudine(int value){
        this.global.getOsservatorio().setAltitudine(value);
    }
    public void setTimezone(int value){
        this.global.getOsservatorio().setTimezone(value);
    }
    public void setGps(int value){
        this.global.getOsservatorio().setGps(value);
    }
    public void setMeteo(int value){
        this.global.getOsservatorio().setMeteo(value);
    }
    */

    // TELESCOPIO
    public String getNomeTelescopio(){
        return this.global.getTelescopio().getNome();
    }
    public int getDiametroSpecchio(){
        return this.global.getTelescopio().getDiametroSpecchio();
    }
    public int getLunghezzaFocale(){
        return this.global.getTelescopio().getLunghezzaFocale();
    }
    public int getRapportoRiduzioneAR(){
        return this.global.getTelescopio().getRapportoRiduzioneAR();
    }
    public int getRapportoRiduzioneDEC(){
        return this.global.getTelescopio().getRapportoRiduzioneDEC();
    }
    public int getRapportoRiduzioneAZ(){
        return this.global.getTelescopio().getRapportoRiduzioneAZ();
    }
    public int getRapportoRiduzioneAL(){
        return this.global.getTelescopio().getRapportoRiduzioneAL();
    }
    public int getRapportoRiduzioneDE(){
        return this.global.getTelescopio().getRapportoRiduzioneDE();
    }
    public int getCampoDiVista(){
        return this.global.getTelescopio().getCampoDiVista();
    }
        // set
    /* 
    public void setNomeTelescopio(String nome){
        this.global.getTelescopio().setNome(nome);
    }
    public void setDiametroSpecchio(int value){
        this.global.getTelescopio().setDiametroSpecchio(value);
    }
    public void setLunghezzaFocale(int value){
        this.global.getTelescopio().setLunghezzaFocale(value);
    }
    public void setRapportoRiduzioneAR(int value){
        this.global.getTelescopio().setRapportoRiduzioneAR(value);
    }
    public void setRapportoRiduzioneDEC(int value){
        this.global.getTelescopio().setRapportoRiduzioneDEC(value);
    }
    public void setRapportoRiduzioneAZ(int value){
        this.global.getTelescopio().setRapportoRiduzioneAZ(value);
    }
    public void setRapportoRiduzioneAL(int value){
        this.global.getTelescopio().setRapportoRiduzioneAL(value);
    }
    public void setRapportoRiduzioneDE(int value){
        this.global.getTelescopio().setRapportoRiduzioneDE(value);
    }
    public void setCampoDiVista(int value){
        this.global.getTelescopio().setCampoDiVista(value);
    }
    */

    // MOTORE AR AZ
    public int getAzRisoluzioneEncoder1(){
        return this.global.getMotoreArAz().getRisoluzioneEncoder1();
    }
    public int getAzRisoluzioneEncoder2(){
        return this.global.getMotoreArAz().getRisoluzioneEncoder2();
    }
    public int getAzNumeroGiriMotore(){
        return this.global.getMotoreArAz().getNumeroGiriMotore();
    }
    public double getAzVelocitaMassima(){
        return this.global.getMotoreArAz().getVelocitaMassima();
    }
    public int getAzPosizioneLimiteInf(){
        return this.global.getMotoreArAz().getPosizioneLimiteInf();
    }
    public int getAzPosizioneLimiteSup(){
        return this.global.getMotoreArAz().getPosizioneLimiteSup();
    }
    public int getAzRiduzioneMotore(){
        return this.global.getMotoreArAz().getRiduzioneMotore();
    }
    public int getAzPosizioneEncoder1(){
        return this.global.getMotoreArAz().getPosizioneEncoder1();
    }
    public int getAzPosizioneEncoder2(){
        return this.global.getMotoreArAz().getPosizioneEncoder2();
    }
        // set
    /*
    public void setAzRisoluzioneEncoder1(int value){
        this.global.getMotoreArAz().setRisoluzioneEncoder1(value);
    }
    public void setAzRisoluzioneEncoder2(int value){
        this.global.getMotoreArAz().setRisoluzioneEncoder2(value);
    }
    public void setAzNumeroGiriMotore(int value){
        this.global.getMotoreArAz().setNumeroGiriMotore(value);
    }
    public void setAzVelocitaMassima(double value){
        this.global.getMotoreArAz().setVelocitaMassima(value);
    }
    public void setAzPosizioneLimiteInf(int value){
        this.global.getMotoreArAz().setPosizioneLimiteInf(value);
    }
    public void setAzPosizioneLimiteSup(int value){
        this.global.getMotoreArAz().setPosizioneLimiteSup(value);
    }
    public void setAzRiduzioneMotore(int value){
        this.global.getMotoreArAz().setRiduzioneMotore(value);
    }
    public void setAzPosizioneEncoder1(int value){
        this.global.getMotoreArAz().setPosizioneEncoder1(value);
    }
    public void setAzPosizioneEncoder2(int value){
        this.global.getMotoreArAz().setPosizioneEncoder2(value);
    }
    */

    // MOTORE DEC AL
    public int getElRisoluzioneEncoder1(){
        return this.global.getMotoreDecAl().getRisoluzioneEncoder1();
    }
    public int getElRisoluzioneEncoder2(){
        return this.global.getMotoreDecAl().getRisoluzioneEncoder2();
    }
    public int getElNumeroGiriMotore(){
        return this.global.getMotoreDecAl().getNumeroGiriMotore();
    }
    public double getElVelocitaMassima(){
        return this.global.getMotoreDecAl().getVelocitaMassima();
    }
    public int getElPosizioneLimiteInf(){
        return this.global.getMotoreDecAl().getPosizioneLimiteInf();
    }
    public int getElPosizioneLimiteSup(){
        return this.global.getMotoreDecAl().getPosizioneLimiteSup();
    }
    public int getElRiduzioneMotore(){
        return this.global.getMotoreDecAl().getRiduzioneMotore();
    }
    public int getElPosizioneEncoder1(){
        return this.global.getMotoreDecAl().getPosizioneEncoder1();
    }
    public int getElPosizioneEncoder2(){
        return this.global.getMotoreDecAl().getPosizioneEncoder2();
    }
        // set
    /*
    public void setElRisoluzioneEncoder1(int value){
        this.global.getMotoreDecAl().setRisoluzioneEncoder1(value);
    }
    public void setElRisoluzioneEncoder2(int value){
        this.global.getMotoreDecAl().setRisoluzioneEncoder2(value);
    }
    public void setElNumeroGiriMotore(int value){
        this.global.getMotoreDecAl().setNumeroGiriMotore(value);
    }
    public void setElVelocitaMassima(double value){
        this.global.getMotoreDecAl().setVelocitaMassima(value);
    }
    public void setElPosizioneLimiteInf(int value){
        this.global.getMotoreDecAl().setPosizioneLimiteInf(value);
    }
    public void setElPosizioneLimiteSup(int value){
        this.global.getMotoreDecAl().setPosizioneLimiteSup(value);
    }
    public void setElRiduzioneMotore(int value){
        this.global.getMotoreDecAl().setRiduzioneMotore(value);
    }
    public void setElPosizioneEncoder1(int value){
        this.global.getMotoreDecAl().setPosizioneEncoder1(value);
    }
    public void setElPosizioneEncoder2(int value){
        this.global.getMotoreDecAl().setPosizioneEncoder2(value);
    }
    */

    // CUPOLA
    public int getControlloreCupola(){
        return this.global.getCupola().getControlloreCupola();
    }    
    public int getCupolaEncoderRis(){
        return this.global.getCupola().getCupolaEncoderRis();
    } 
    public int getStadioRiduzione(){
        return this.global.getCupola().getStadioRiduzione();
    } 
        // set
    /*
    public void setControlloreCupola(int value){
        this.global.getCupola().setControlloreCupola(value);
    }   
    public void setCupolaEncoderRis(int value){
        this.global.getCupola().setCupolaEncoderRis(value);
    } 
    public void setStadioRiduzione(int value){
        this.global.getCupola().setStadioRiduzione(value);
    } 
    */

    // SB129X
    public int getPortaComunicazione1(){
        return this.global.getSb129x().getPortaComunicazione1();
    }
    public int getBaudRate1(){
        return this.global.getSb129x().getBaudRate1();
    }
    public int getPortaComunicazione2(){
        return this.global.getSb129x().getPortaComunicazione2();
    }
    public int getBaudRate2(){
        return this.global.getSb129x().getBaudRate2();
    }
    public int getPortaComunicazione3(){
        return this.global.getSb129x().getPortaComunicazione3();
    }
    public int getBaudRate3(){
        return this.global.getSb129x().getBaudRate3();
    }
        // set
    /*
    public void setPortaComunicazione1(int value){
        this.global.getSb129x().setPortaComunicazione1(value);
    }
    public void setBaudRate1(int value){
        this.global.getSb129x().setBaudRate1(value);
    }
    public void getPortaComunicazione2(int value){
        this.global.getSb129x().setPortaComunicazione2(value);
    }
    public void setBaudRate2(int value){
        this.global.getSb129x().setBaudRate2(value);
    }
    public void setPortaComunicazione3(int value){
        this.global.getSb129x().setPortaComunicazione3(value);
    }
    public void setBaudRate3(int value){
        this.global.getSb129x().setBaudRate3(value);
    }
    */

    // PADDLE
    public int getVelocitaBassa(){
        return this.global.getPaddle().getVelocitaBassa();
    }
    public int getVelocitaMedia(){
        return this.global.getPaddle().getVelocitaMedia();
    }
    public int getVelocitaAlta(){
        return this.global.getPaddle().getVelocitaAlta();
    }
        // set
    /*
    public void setVelocitaBassa(int value){
        this.global.getPaddle().getVelocitaBassa();
    }
    public void setVelocitaMedia(int value){
        this.global.getPaddle().setVelocitaMedia(value);
    }
    public void setVelocitaAlta(int value){
        this.global.getPaddle().setVelocitaAlta(value);
    }
    */

    // POSZERO
    public int getZeroX(){
        return this.global.getPoszero().getZeroX();
    }
    public int getZeroY(){
        return this.global.getPoszero().getZeroY();
    }
    public int getZeroZ(){
        return this.global.getPoszero().getZeroZ();
    }
    public int getZeroCup(){
        return this.global.getPoszero().getZeroCup();
    }
        // set
    /*
    public void setZeroX(int value){
        this.global.getPoszero().setZeroX(value);
    }
    public void setZeroY(int value){
        this.global.getPoszero().setZeroY(value);
    }
    public void setZeroZ(int value){
        this.global.getPoszero().setZeroZ(value);
    }
    public void setZeroCup(int value){
        this.global.getPoszero().setZeroCup(value);
    }
    */


    /* 
    public static void main(String[] a){ 
        System.out.println();

        ConfigurationClass cfg = new ConfigurationClass();

        System.out.println(cfg.getControlloreCupola());
    
    }
    */
}
