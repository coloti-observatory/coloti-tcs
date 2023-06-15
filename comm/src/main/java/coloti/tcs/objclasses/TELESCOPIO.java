package coloti.tcs.objclasses;
import coloti.tcs.ConfigurationClass;

public class TELESCOPIO {
    public String NomeTelescopio;
    public int DiametroSpecchio;
    public int LunghezzaFocale;
    public int RapportoRiduzioneAR;
    public int RapportoRiduzioneDEC;
    public int RapportoRiduzioneAZ;
    public int RapportoRiduzioneAL;
    public int RapportoRiduzioneDE;
    public int CampoDiVista;

    // info
    public int MonType = 0;
    public double PosX;
    public double PosY;
    public double PosZ;
    public double H;
    public double AZ;
    public double RA;
    public double EL;
    public double DEC;
    public double PA;
    public double SlewVelX;
    public double AccX;
    public double SlewVelY;
    public double AccY;
    public double SlewVelZ;
    public double AccZ;

    public boolean TargetOnTracking;
    public int TrackingMaxDuration;
    public int TrackingNodes;
    public boolean RefractionStatus;
    public boolean PointingModelStatus;
    public boolean TargetPointed;
    public boolean TargetNotValid;
    public double RefractionCorrection;
    public double TimeToTarget;
    public boolean SimulationActive;
    public int MachineState;
    public int MachineStatePhase;
    public int TCUMode;
    public String GoLoadedInfo;
    public String GoStandbyInfo;
    public String GoOnlineInfo;
    public String GoMaintenanceInfo;
    public String StartMotionInfo;
    public String StopMotionInfo;
    public String EmergencyStopInfo;
    public String StartParkingInfo; 
    public String StopParkingInfo; 
    public String StartTrackingInfo; 
    public String StopTrackingInfo; 
    public String UpdateTrajectoryInfo;
    public String StartPointingInfo;
    public String StopPointingInfo;
    public int MotionType;
    public int TrajectoryGenerationMode;
    public boolean PointingModelOnOff;
    public int TrackingDuration;
    public double TrajectoryNodeArray;
    public boolean Refraction;
    public String TargetName;
    public double TargetRA;
    public double TargetDEC;
    public double TargetPmRA;
    public double TargetPmDEC;
    public double TargetPX;
    public double TargetRV;
    public double TargetEquinox;
    public double TargetEpoch;
    public int TargetCoordFrame;
    public int TargetCoordType;



    public TELESCOPIO(ConfigurationClass cfg){
        this.NomeTelescopio = cfg.getNomeTelescopio();
        this.DiametroSpecchio = cfg.getDiametroSpecchio();
        this.LunghezzaFocale = cfg.getLunghezzaFocale();
        this.RapportoRiduzioneAR = cfg.getRapportoRiduzioneAR();
        this.RapportoRiduzioneDEC = cfg.getRapportoRiduzioneDEC();
        this.RapportoRiduzioneAZ = cfg.getRapportoRiduzioneAZ();
        this.RapportoRiduzioneAL = cfg.getRapportoRiduzioneAL();
        this.RapportoRiduzioneDE = cfg.getRapportoRiduzioneDE();
        this.CampoDiVista = cfg.getCampoDiVista();
    }

}
