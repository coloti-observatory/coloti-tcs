package coloti.tcs;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.lang.model.util.ElementScanner6;
import coloti.tcs.CommClass;

public class ACSv4 {

  private CommClass communication;
  int ACSOK = -1;
  int ACSposoverflow = -2;
  int ACSmotorerror = -3;
  int[] MOTORSTATUS = { 0, 0, 0 };
  long VALUE1 = 0L;
  long VALUE2 = 0L;
  long VALUE3 = 0L;
  long VALUECR = 0L;
  static final double D2PI = 2 * Math.PI;
  static final int X = 0, Y = 1, Z = 2;
  static final int RAD = 0, GRAD = 1, HOUR = 2, ENC = 3, ARCSECS = 4;
  String[] axes = { "X", "Y", "Z" };
  double[] CONVFACTOR = new double[3];
  double[] GEARRATIO = new double[3];
  double[] MAXMIS = new double[5];
  boolean HostStatus; // true host (0) mode, false terminal (1) mode
  int UM;
  int MAXINP;
  int NAXES;
  boolean CommStatus;
  double temporaryValue;
  double val, val1;
  int MAXSYSINP;
  double[] ENCODERRES;
  int[] MOTIONMODE;
  double[] MaxAbsVel;
  double[] MaxVel;
  double[] MinAbsVel;
  double[] MinVel;
  double[] MaxAbsAcc;
  double[] MaxAcc;
  double[] MinAbsAcc;
  double[] MinAcc;
  double[] MaxPos;
  double[] MinPos;

  double[] PositionAx;
  double[] VelAx;
  double[] AccAx;
  double[] DecAx;

  byte[] serialAnswer; //=new byte[10];
  byte[] serialCommand;
  String answerString;

  public ACSv4(){}

  public ACSv4(String SerialID) {
    this.communication = new CommClass(SerialID);
    this.NAXES = 1;
    this.CONVFACTOR[X] = 1.;
    this.CONVFACTOR[Y] = 1.;
    this.CONVFACTOR[Z] = 1.;
    this.GEARRATIO[X] = 1.;
    this.GEARRATIO[Y] = 1.;
    this.GEARRATIO[Z] = 1.;
    this.MAXMIS[RAD] = D2PI;
    this.MAXMIS[GRAD] = 360.;
    this.MAXMIS[HOUR] = 24.;
    this.MAXMIS[ENC] = 1.;
    this.MAXMIS[ARCSECS] = 1296000.0;
    this.UM = ENC;
    this.MAXINP = 8;
    this.CommStatus = false;

    this.PositionAx = new double[1];
    this.VelAx = new double[1];
    this.AccAx = new double[1];
    this.DecAx = new double[1];
    this.ENCODERRES = new double[1];
    this.MOTIONMODE = new int[1];
    this.MaxAbsVel = new double[1];
    this.MaxVel = new double[1];
    this.MinAbsVel = new double[1];
    this.MinVel = new double[1];
    this.MaxAbsAcc = new double[1];
    this.MaxAcc = new double[1];
    this.MinAbsAcc = new double[1];
    this.MinAcc = new double[1];
    this.MaxPos = new double[1];
    this.MinPos = new double[1];

  }

  public ACSv4(String SerialID, int nax) {
    this.communication = new CommClass(SerialID);
    this.NAXES = nax;
    this.CONVFACTOR[X] = 1.;
    this.CONVFACTOR[Y] = 1.;
    this.CONVFACTOR[Z] = 1.;
    this.GEARRATIO[X] = 1.;
    this.GEARRATIO[Y] = 1.;
    this.GEARRATIO[Z] = 1.;
    this.MAXMIS[RAD] = D2PI;
    this.MAXMIS[GRAD] = 360.;
    this.MAXMIS[HOUR] = 24.;
    this.MAXMIS[ENC] = 1.;
    this.MAXMIS[ARCSECS] = 1296000.0;
    this.UM = ENC;
    this.MAXINP = 8;
    this.CommStatus = false;

    this.PositionAx = new double[nax];
    this.VelAx = new double[nax];
    this.AccAx = new double[nax];
    this.DecAx = new double[nax];
    this.ENCODERRES = new double[nax];
    this.MOTIONMODE = new int[nax];
    this.MaxAbsVel = new double[nax];
    this.MaxVel = new double[nax];
    this.MinAbsVel = new double[nax];
    this.MinVel = new double[nax];
    this.MaxAbsAcc = new double[nax];
    this.MaxAcc = new double[nax];
    this.MinAbsAcc = new double[nax];
    this.MinAcc = new double[nax];
    this.MaxPos = new double[nax];
    this.MinPos = new double[nax];
  }

  // STARTING

  public boolean OpenCommunications(int baud, byte bytesize, byte stop, byte parity, int timeout) {
    boolean status = false;
    if (!this.communication.GetStatus()) {
      status = this.communication.Open(baud, bytesize, stop, parity, timeout);
    }
    this.CommStatus = status;
    return status;
  }

  public boolean OpenCommunications() {
    boolean status = false;
    if (!this.communication.GetStatus()) {
      status = this.communication.Open();
      this.communication.SetTimeouts(7000);
    }
    this.CommStatus = status;
    return status;
  }

  public void SetSimpleStart(int mode){ // 0 host mode, 1 terminal mode
    if (OpenCommunications()) {
      SetMode(mode); // 0 host mode, 1 terminal mode
    }
  }

  public void SetStart(int mode, int baud, byte bytesize, byte stop, byte parity, int timeout){
    int Err = 0;
    if (OpenCommunications(baud, bytesize, stop, parity, timeout)) {
      SetMode(mode); // 0 host mode, 1 terminal mode
      if (this.CommStatus) {
        for (int i = 0; i < this.NAXES; i++) {
          Err = GetEncoderRes(this.axes[i]);
          this.GEARRATIO[i] = 1;
          this.CONVFACTOR[i] = 1;
          Err = GetMotionMode(this.axes[i]);
          if (this.ENCODERRES[i] == 0)
            this.MOTIONMODE[i] = 0;
          if (this.ENCODERRES[i] == 10)
            this.MOTIONMODE[i] = 1;
          this.MaxAbsVel[i] = this.MaxVel[i] = this.ENCODERRES[i];
          this.MinAbsVel[i] = this.MinVel[i] = 0;
          this.MaxAbsAcc[i] = this.MaxAcc[i] = this.ENCODERRES[i];
          this.MinAbsAcc[i] = this.MinAcc[i] = 1000;
          Err = GetMotMaxMinPos(this.axes[i]);
        }
      }
    }
  }

  public int InitAxes() {
    int Err = 0;
    if (this.CommStatus) {
      for (int i = 0; i < this.NAXES; i++) {
        Err = GetEncoderRes(this.axes[i]);
        this.GEARRATIO[i] = 1;
        this.CONVFACTOR[i] = 1;
        Err = GetMotionMode(this.axes[i]);
        if (this.ENCODERRES[i] == 0)
          this.MOTIONMODE[i] = 0;
        if (this.ENCODERRES[i] == 10)
          this.MOTIONMODE[i] = 1;
        this.MaxAbsVel[i] = this.MaxVel[i] = this.ENCODERRES[i];
        this.MinAbsVel[i] = this.MinVel[i] = 0;
        this.MaxAbsAcc[i] = this.MaxAcc[i] = this.ENCODERRES[i];
        this.MinAbsAcc[i] = this.MinAcc[i] = 1000;
        Err = GetMotMaxMinPos(this.axes[i]);
      }
      return Err;
    }
    return 1;
  }

  // UTILITY

  public class Tell0 {
    byte T0Control;
    byte TstateMotorX;
    byte TmodeMotX;
    byte TsmodeMotX;
    byte TstepX;
    byte TstateMotorY;
    byte TmodeMotY;
    byte TsmodeMotY;
    byte TstepY;
    byte TstateMotorZ;
    byte TmodeMotZ;
    byte TsmodeMotZ;
    byte TstepZ;
    byte Tinformation;
    byte T0CheckSum;
    byte[] T0DataX = new byte[256];
    byte[] T0DataY = new byte[256];
    byte[] T0DataZ = new byte[256];
  }

  Tell0 Tell0 = new Tell0();

  public class Tell1 {
    byte T1Control;
    byte T1Codex;
    byte T1DataX;
    byte T1DataY;
    byte T1DataZ;
    byte T1DataT;
    byte T1CheckSum;
    byte[] T1Data = new byte[80];
  }

  Tell1 Tell1 = new Tell1();

  public class Tell2 {
    byte T2Control;
    byte T2Codex;
    byte T2DataX;
    byte T2DataY;
    byte T2DataZ;
    byte T2DataT;
    byte T2CheckSum;
    byte[] T2Data = new byte[80];
  }

  Tell2 Tell2 = new Tell2();

  public int AxesNumber(String ax) {
    int axint = 0;
    if (ax.equals("X"))
      axint = 0;
    else if (ax.equals("Y"))
      axint = 1;
    else if (ax.equals("Z"))
      axint = 2;
    return axint;
  }

  public int mod(int i, int j) {
    return (i - (int) (i / j) * j);
  }

  public void PrintArray(double[] arr) {
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i]);
      System.out.print(" ");
    }
    System.out.println();
  }

  public void PrintArray(byte[] arr) {
    System.out.println("--");
    for (int i = 0; i < arr.length; i++) {
      System.out.println((char) arr[i]+ " , "+ (int) arr[i]);
    }
    System.out.println("--");
  }

  public int Move(String ax, double pos) throws InterruptedException {
    if (this.MOTORSTATUS[AxesNumber(ax)] == 0) {
      if (SetMotorOn(ax) != this.ACSOK)
        return this.ACSmotorerror;
    }

    if (SetAbsTargPos(ax, pos) != ACSOK)
      return this.ACSmotorerror;

    if (StartMove(ax) != ACSOK)
      return this.ACSmotorerror;

    return this.ACSOK;
  }

  public int Move(String ax, double pos, double vel) throws InterruptedException {

    if (SetMotVel(ax, vel) != this.ACSOK)
      return this.ACSmotorerror;

    if (Move(ax, pos) != this.ACSOK)
      return this.ACSmotorerror;

    return this.ACSOK;
  }

  public int Move(String ax, double pos, double vel, double acc) throws InterruptedException {

    if (SetMotAcc(ax, acc) != this.ACSOK)
      return this.ACSmotorerror;

    if (Move(ax, pos, vel) != this.ACSOK)
      return this.ACSmotorerror;

    return ACSOK;
  }

  public int Move(String ax, double pos, double vel, double acc, double dec) throws InterruptedException {

    if (SetMotDec(ax, dec) != this.ACSOK)
      return this.ACSmotorerror;

    if (Move(ax, pos, vel, acc) != this.ACSOK)
      return this.ACSmotorerror;

    return this.ACSOK;
  }

  public int MoveTrack(String ax, double pos, double trackvel) throws InterruptedException {
    if (SetSlewMode(ax) != this.ACSOK)
      return this.ACSmotorerror;
    if (Move(ax, pos) != this.ACSOK)
      return this.ACSmotorerror;
    if (SetTrackMode(ax) != this.ACSOK)
      return this.ACSmotorerror;

    // while GetEndMotionStatus

    if (SetMotVel(ax, trackvel) != this.ACSOK)
      return this.ACSmotorerror;
    if (StartMove(ax) != this.ACSOK)
      return this.ACSmotorerror;

    return this.ACSOK;
  }
  
  public void MotConfig(String ax, int um, double gr, double rev) {
    SetUserUnit(ax, um, gr);
    int axI = AxesNumber(ax);
    this.MaxVel[axI] = this.MaxAbsVel[axI] = 0.85 * (rev / 60) * this.ENCODERRES[axI] / this.CONVFACTOR[axI];
    this.MaxAbsAcc[axI] = this.MaxAcc[axI] = this.MaxVel[axI];
    this.MinAbsVel[axI] = this.MinVel[axI] = -this.MaxVel[axI];
    this.MinAbsAcc[axI] = 1000 / this.CONVFACTOR[axI];
  }

  public void Sleep(int millisecondsTime){
    try {
      TimeUnit.MILLISECONDS.sleep(millisecondsTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  

  // SET FUNCTIONS

  public int SetHostMode(){
    int Err = DirectCommand("SHT1\r");
    Sleep(2000);
    Err = DirectCommand("SHT0\r");
    return Err;
  }

  public int SetTerminalMode(){
    int Err = DirectCommand("SHT0\r");
    Sleep(2000);
    Err = DirectCommand("SHT1\r");
    return Err;
  }

  public int SetMode(int Value){
    int Err = -6;
    if (Value == 0)
      Err = SetHostMode();
    else if (Value == 1)
      Err = SetTerminalMode();

    this.HostStatus = this.answerString.equals("00\r");
    return Err;
  }

  public int SetAbsTargPos(String ax, double pos) {
    int Value = 0;
    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * pos);
    byte[] command = sbld("S%sAP", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetRelTargPos(String ax, double pos) {
    int Value = 0;
    if (pos > MaxPos[AxesNumber(ax)] || pos < MinPos[AxesNumber(ax)])
      return ACSposoverflow;
    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * pos);
    byte[] command = sbld("S%sRP", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetAxisZeroPos(String ax, double pos) {
    int Value = 0;
    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * pos);
    byte[] command = sbld("S%sZP", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetMotVel(String ax, double vel) {
    int Value = 0;
    if (vel > MaxVel[AxesNumber(ax)])
      vel = MaxVel[AxesNumber(ax)];
    if (vel < MinVel[AxesNumber(ax)])
      vel = MinVel[AxesNumber(ax)];

    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * vel);
    byte[] command = sbld("S%sLV", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetMotAcc(String ax, double acc) {
    int Value = 0;
    if (acc > MaxAcc[AxesNumber(ax)])
      acc = MaxAcc[AxesNumber(ax)];
    if (acc < MinAcc[AxesNumber(ax)])
      acc = MinAcc[AxesNumber(ax)];

    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * acc);
    byte[] command = sbld("S%sLA", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetMotDec(String ax, double dec) {
    int Value = 0;
    if (dec > MaxAcc[AxesNumber(ax)])
      dec = MaxAcc[AxesNumber(ax)];

    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * dec);
    byte[] command = sbld("S%sLD", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetMotorOn(String ax) throws InterruptedException {
    if (GetMotorStatus(ax) == 0) { // etMotorStatus(ax) == 0
      byte[] command = sbld("S%sMO", ax);
      int ErroreCode = CommandSet(command, 1);
      if (ErroreCode == -1) {
        this.MOTORSTATUS[AxesNumber(ax)] = 1;
        return ACSOK;
      } else {
        return ErroreCode;
      }
    } else {
      return ACSOK;
    }
  }

  public int SetMotorOff(String ax) throws InterruptedException {
    if (GetMotorStatus(ax) == 1) {
      byte[] command = sbld("S%sMO", ax);
      int ErroreCode = CommandSet(command, 0);
      if (ErroreCode == -1) {
        this.MOTORSTATUS[AxesNumber(ax)] = 0;
        return ACSOK;
      } else {
        return ErroreCode;
      }
    } else {
      return ACSOK;
    }
  }

  public int SetSlewMode(String ax) {
    if (MOTIONMODE[AxesNumber(ax)] != 0) {
      byte[] command = sbld("S%sMM", ax);
      int Err = CommandSet(command, 0);
      if (Err == -1)
        MOTIONMODE[AxesNumber(ax)] = 0;
      return Err;
    }
    return ACSOK;
  }

  public int SetTrackMode(String ax) {
    if (MOTIONMODE[AxesNumber(ax)] != 1) {
      byte[] command = sbld("S%sMM", ax);
      int Err = CommandSet(command, 10);
      if (Err == -1)
        MOTIONMODE[AxesNumber(ax)] = 1;
      return Err;
    }
    return ACSOK;
  }

  public int SetOutPortOn(int ipno) {
    if (ipno > MAXINP)
      return -2;
    int Err = CommandSet("SHI", ipno);
    return Err;
  }

  public int SetOutPortOff(int ipno) {
    if (ipno > MAXINP)
      return -2;
    int Err = CommandSet("SLO", ipno);
    return Err;
  }

  public void SetNumberIOPort(int n) {
    this.MAXINP = n;
  }


  public int StartMove(String ax) {
    byte[] command = sbld("B%s", ax);
    int Err = CommandMot(command);
    return Err;
  }

  public int StopMove(String ax) throws InterruptedException {
    byte[] command = sbld("K%s", ax);
    int Err = CommandMot(command);
    if (IsMoving(ax) == 1)
      communication.Timeout(500);
    return Err;
  }

  public int ExecProg(int id) {
    byte[] command = sbld("PX%d", id);
    int Err = CommandMot(command);
    return Err;
  }

  public int ExecProg(String label) {
    byte[] command = sbld("PX%s", label);
    int Err = CommandMot(command);
    return Err;
  }


  public void SetMaxMinVel(String ax, double maxval, double minval) {
    int axI = AxesNumber(ax);
    this.MaxVel[axI] = maxval;
    if (this.MaxVel[axI] > this.MaxAbsVel[axI])
      this.MaxVel[axI] = this.MaxAbsVel[axI];

    this.MinVel[axI] = minval;
    if (this.MinVel[axI] > this.MinAbsVel[axI])
      this.MinVel[axI] = this.MinAbsVel[axI];
  }

  public void SetMaxMinAcc(String ax, double maxval, double minval) {
    int axI = AxesNumber(ax);
    this.MaxAcc[axI] = maxval;
    if (MaxAcc[axI] > MaxAbsAcc[axI])
      this.MaxAcc[axI] = MaxAbsAcc[axI];

    this.MinAcc[axI] = minval;
    if (MinAcc[axI] > MinAbsAcc[axI])
      this.MinAcc[axI] = MinAbsAcc[axI];
  }

  public void SetMaxMinPos(String ax, double maxval, double minval) {
    int axI = AxesNumber(ax);
    this.MaxPos[axI] = maxval;
    this.MinPos[axI] = minval;
  }

  public void SetUserUnit(String ax, int um, double gr) {
    int axI = AxesNumber(ax);
    switch (um) {
      case RAD:
        this.UM = RAD;
        this.CONVFACTOR[axI] = gr * this.ENCODERRES[axI] / this.MAXMIS[RAD];
        break;

      case GRAD:
        this.UM = GRAD;
        this.CONVFACTOR[axI] = gr * this.ENCODERRES[axI] / this.MAXMIS[GRAD];
        break;

      case HOUR:
        this.UM = HOUR;
        this.CONVFACTOR[axI] = gr * this.ENCODERRES[axI] / this.MAXMIS[HOUR];
        break;

      case ARCSECS:
        this.UM = ARCSECS;
        this.CONVFACTOR[axI] = gr * this.ENCODERRES[axI] / this.MAXMIS[ARCSECS];
        break;

      default:
        break;
    }
  }


  // GET FUNCTIONS

  public void GetAllInfo(String ax) {
    int nax = AxesNumber(ax);

    System.out.print("Motor Status: ");
    System.out.println(this.MOTORSTATUS[nax]);
    System.out.println();
    System.out.print("Conversion factor: ");
    System.out.println(this.CONVFACTOR[nax]);
    System.out.print("Gear ratio: ");
    System.out.println(this.GEARRATIO[nax]);
    System.out.print("MAXMIS: ");
    System.out.println(this.MAXMIS[nax]);
    System.out.print("UM: ");
    System.out.println(this.UM);
    System.out.print("MAXINP: ");
    System.out.println(this.CONVFACTOR[nax]);
    System.out.print("Number of axes: ");
    System.out.println(this.NAXES);
    System.out.print("Communication status: ");
    System.out.println(this.CommStatus);
    System.out.print("MAXSYSINP: ");
    System.out.println(this.MAXSYSINP);
    System.out.print("ENCODERRES: ");
    System.out.println(this.ENCODERRES[nax]);
    System.out.print("Motion Mode (0 linear p to p, 1 linear p to p repetitive, 3 move by sequence, ...): ");
    System.out.println(this.MOTIONMODE[nax]);
    System.out.println("--- Velocity ---");
    System.out.print("Maximum absolute velocity: ");
    System.out.println(this.MaxAbsVel[nax]);
    System.out.print("Minimum absolute velocity: ");
    System.out.println(this.MinAbsVel[nax]);
    System.out.print("Maximum velocity: ");
    System.out.println(this.MaxVel[nax]);
    System.out.print("Minimum velocity: ");
    System.out.println(this.MinVel[nax]);
    System.out.print("Velocity: ");
    System.out.println(this.VelAx[nax]);
    System.out.println("--- Acceleration ---");
    System.out.print("Maximum absolute acceleration: ");
    System.out.println(this.MaxAbsAcc[nax]);
    System.out.print("Minimum absolute acceleration: ");
    System.out.println(this.MinAbsAcc[nax]);
    System.out.print("Maximum acceleration: ");
    System.out.println(this.MaxAcc[nax]);
    System.out.print("Minimum acceleration: ");
    System.out.println(this.MinAcc[nax]);
    System.out.print("Acceleration: ");
    System.out.println(this.AccAx[nax]);
    System.out.println("--- Position ---");
    System.out.print("Maximum Position: ");
    System.out.println(this.MaxPos[nax]);
    System.out.print("Minimum Position: ");
    System.out.println(this.MinPos[nax]);
    System.out.print("Position: ");
    System.out.println(this.PositionAx[nax]);
  }

  public int GetEncoderRes(String ax) {
    this.VALUECR = 0L;
    double EncRes;
    byte[] command = sbld("R%sLR", ax);
    int ErrorCode = CommandReport(command);
    EncRes = this.VALUECR;
    if (ErrorCode != ACSOK)
      return ErrorCode;
    command = sbld("R%sLF", ax);
    ErrorCode = CommandReport(command);
    EncRes = (EncRes) * Math.round(Math.pow(2., this.VALUECR)); // dnint = Math.round

    System.out.print("Encoder Resolution: ");
    System.out.println(EncRes);

    this.ENCODERRES[AxesNumber(ax)] = EncRes;
    return ErrorCode;
  }

  public int GetMotionMode(String ax) {
    this.VALUECR = 0L;
    int axI = AxesNumber(ax);
    byte[] command = sbld("R%sMM", ax);
    int ErrorCode = CommandReport(command);
    this.MOTIONMODE[axI] = (int) this.VALUECR;
    return ErrorCode;
  }

  public int GetMotMaxMinPos(String ax) { // , double val2, double val12
    this.VALUECR = 0L;
    int axI = AxesNumber(ax);

    byte[] command = sbld("R%sPH", ax);
    int ErrorCode = CommandReport(command);
    if (ErrorCode == ACSOK)
      this.MaxPos[axI] = (double) this.VALUECR / this.CONVFACTOR[axI];
    else
      return ErrorCode;

    command = sbld("R%sPL", ax);
    ErrorCode = CommandReport(command);
    if (ErrorCode == ACSOK)
      this.MinPos[axI] = (double) this.VALUECR / this.CONVFACTOR[axI];
    else
      return ErrorCode;
    return 0;
  }

  public int GetMotorStatus(String ax) throws InterruptedException {
    int ErrorCode = TellCommand("T0\r");
    if (ax.equals("X")) {
      if (((this.Tell0.TstateMotorX) & (1 << 3)) == 0)
        this.MOTORSTATUS[0] = 0;
      else
        this.MOTORSTATUS[0] = 1;
    }

    if (ax.equals("Y")) {
      if (((this.Tell0.TstateMotorY) & (1 << 3)) == 0)
        this.MOTORSTATUS[1] = 0;
      else
        this.MOTORSTATUS[1] = 1;
    }

    if (ax.equals("Z")) {
      if (((this.Tell0.TstateMotorZ) & (1 << 3)) == 0)
        this.MOTORSTATUS[2] = 0;
      else
        this.MOTORSTATUS[2] = 1;
    }

    return ErrorCode;
  }

  public int GetEndMotionStatus(String ax) throws InterruptedException {
    int Err = TellCommand("T2\r");

    if (ax.equals("X"))
      return Tell2.T2DataX;

    if (ax.equals("Y"))
      return Tell2.T2DataY;

    if (ax.equals("Z"))
      return Tell2.T2DataZ;

    return ACSOK;
  }

  public int GetMotPos(String ax) {
    this.VALUECR = 0L;
    byte[] command = sbld("R%sCP", ax);
    int Err = CommandReport(command);
    this.PositionAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetMotEncPos(String ax) {
    byte[] command = sbld("R%sCP", ax);
    int Err = CommandReport(command);
    return Err;
  }

  public int GetMotVel(String ax) {
    this.VALUECR = 0L;
    byte[] command = sbld("R%sLV", ax);
    int Err = CommandReport(command);
    this.VelAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetMotAcc(String ax) {
    this.VALUECR = 0L;
    byte[] command = sbld("R%sLA", ax);
    int Err = CommandReport(command);
    this.AccAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetMotDec(String ax) {
    this.VALUECR = 0L;
    byte[] command = sbld("R%sLD", ax);
    int Err = CommandReport(command);
    this.DecAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetMoveInfo(boolean PRINT) {
    int Err = this.NAXES * 4; // se tutto va bene poi Err ritornato diventa 0
    for (int i = 0; i < this.NAXES; i++) {
      Err += GetMotPos(axes[i]);
      Err += GetMotVel(axes[i]);
      Err += GetMotAcc(axes[i]);
      Err += GetMotDec(axes[i]);
    }

    if (PRINT) {
      System.out.println("Position: ");
      PrintArray(this.PositionAx);
      System.out.println("Vel: ");
      PrintArray(this.VelAx);
      System.out.println("Acc: ");
      PrintArray(this.AccAx);
      System.out.println("Dec: ");
      PrintArray(this.DecAx);
    }
    return Err;
  }

  public int GetSysInp(int ipno) {
    if (ipno > MAXSYSINP)
      return -2;
    byte[] instruction = String.valueOf("RSI").getBytes();
    int ErrorCode = CommandReportParams(ipno, instruction);
    return ErrorCode;
  }

  public int GetInpLog(int ipno){
    if (ipno > MAXSYSINP)
      return -2;
    byte[] instruction = String.valueOf("RIL").getBytes();
    int ErrorCode = CommandReportParams(ipno, instruction);
    return ErrorCode;
  }

  public int GetInpPortStatus(int ipno){
    if (ipno > MAXSYSINP)
      return -2;
    byte[] instruction = String.valueOf("RIP").getBytes();
    int ErrorCode = CommandReportParams(ipno, instruction);
    return ErrorCode;
  }

  public int GetOutPortStatus(int ipno){
    if (ipno > MAXSYSINP)
      return -2;
    byte[] instruction = String.valueOf("ROP").getBytes();
    int ErrorCode = CommandReportParams(ipno, instruction);
    return ErrorCode;
  }

  public int IsMoving(String ax) throws InterruptedException {
    int res = 0;
    int Err = TellCommand("T0\r");

    if (ax.equals("X")) {
      if (((this.Tell0.TstateMotorX) & (1)) == 0)
        res = 0;
      else
        res = 1;
    }

    else if (ax.equals("Y")) {
      if (((this.Tell0.TstateMotorY) & (1)) == 0)
        res = 0;
      else
        res = 1;
    }

    else if (ax.equals("Z")) {
      if (((this.Tell0.TstateMotorZ) & (1)) == 0)
        res = 0;
      else
        res = 1;
    }

    return res;
  }





  // COMMUNICATIONS
  public int DirectCommand(String Instruction){
    byte[] instruction = String.valueOf(Instruction).getBytes();
    this.communication.Write(instruction);
    Sleep(200);
    this.answerString = this.communication.ReadMessage();
    this.serialAnswer = String.valueOf(answerString).getBytes();

    boolean PRINT = true;
    if (PRINT) {
      System.out.print("Comando inviato: ");
      PrintArray(instruction);
      System.out.println("Risposta al comando: ");
      //PrintArray(this.serialAnswer);
      System.out.println(this.answerString);
    }

    PrintArray(this.serialAnswer);
    return Error();
  }

  public int CommandSet(String Instruction, int Value) {
    byte[] instruction = String.valueOf(Instruction).getBytes();
    SerialWrite(instruction, Value);
    // print temporaneo, va poi eliminato
    int nBytes = SerialRead();
    boolean PRINT = true;
    if (PRINT) {
      System.out.print("Comando Set inviato: ");
      PrintArray(this.serialCommand);
      System.out.println("Risposta al comando: ");
      PrintArray(this.serialAnswer);
    }
    return Error();
  }

  public int CommandSet(String Instruction) {
    byte[] instruction = String.valueOf(Instruction).getBytes();
    SerialWrite(instruction);
    // print temporaneo, va poi eliminato
    int nBytes = SerialRead();
    boolean PRINT = true;
    if (PRINT) {
      System.out.print("Comando Set inviato: ");
      PrintArray(this.serialCommand);
      System.out.println("Risposta al comando: ");
      PrintArray(this.serialAnswer);
    }
    return Error();
  }

  public int CommandSet(byte[] Instruction, int Value) {
    SerialWrite(Instruction, Value);
    // print temporaneo, va poi eliminato
    int nBytes = SerialRead();
    boolean PRINT = true;
    if (PRINT) {
      System.out.print("Comando Set inviato: ");
      PrintArray(this.serialCommand);
      System.out.println("Risposta al comando: ");
      PrintArray(this.serialAnswer);
    }
    return Error();
  }

  public int CommandSet(byte[] Instruction) {
    SerialWrite(Instruction);
    // print temporaneo, va poi eliminato
    int nBytes = SerialRead();
    boolean PRINT = true;
    if (PRINT) {
      System.out.print("Comando Set inviato: ");
      PrintArray(this.serialCommand);
      System.out.println("Risposta al comando: ");
      PrintArray(this.serialAnswer);
    }
    return Error();
  }
  
  public int CommandReport(byte[] instruction) {
    this.VALUECR = 0;
    int Count, Err, nBytes = 0;
    byte[] DataNt = new byte[4];
    byte[] Data = new byte[4];
    NewSerialWrite(instruction);
    Sleep(200);


    nBytes = SerialRead();
    //this.serialAnswer[nBytes] = '\0';
    //System.out.println("Before DLE:" + nBytes);
    boolean PRINT = true;
    if (PRINT) {
      System.out.print("Comando inviato: ");
      PrintArray(this.serialCommand);
      System.out.println("Risposta al comando: ");
      System.out.println(this.answerString);
      PrintArray(this.serialAnswer);
      System.out.println(" ");
    }

    nBytes = RemoveDLE(this.serialAnswer);
    //System.out.println("After DLE:" + nBytes);
    if (nBytes == -2)
      return -2;
    Err = Error();
    if (Err != -1)
      return Err;
    for (Count = 4; Count < 8; Count++){
      Data[Count - 4] = this.serialAnswer[Count];
      System.out.println("> "+(char)this.serialAnswer[Count]);
    }

    this.VALUECR = DataConversionRX(Data);
    //this.VALUECR = ConversioneRX(Data, DataNt);
    System.out.println("Risultato convertito comando report: ");
    System.out.println(this.VALUECR);
    return -1;
  }

  public int CommandReportParams(int iopo, byte[] instruction) {
    int Count, Err, nBytes = 0;
    byte[] DataNt = new byte[4];
    byte[] Data = new byte[4];
    SerialWrite(instruction);
    nBytes = SerialRead();
    //this.serialAnswer[nBytes] = '\0';

    nBytes = RemoveDLE(this.serialAnswer);

    if (nBytes == -2)
      return -2;
    Err = Error();
    if (Err != -1)
      return Err;
    for (Count = 5; Count < 7; Count++)
      Data[Count - 3] = this.serialAnswer[Count];

    this.VALUECR = DataConversionRX(Data);
    this.VALUECR = (int) ((this.VALUECR >> (iopo - 1)) & (1));
    return -1;

  }

  public int RemoveDLE(byte[] serialAnswer) {
    int cont, cont1 = 0, minusChar = 0;
    int nBytes = serialAnswer.length;
    for (cont = 1; cont <= nBytes; cont++) {
      cont1 += 1;
      if (serialAnswer[cont - 1] == (byte) 0x10) { // 0x10 corrisponderebbe a 16
        if (serialAnswer[cont] == (byte) 0x0D || serialAnswer[cont] == (byte) 0x10) { // 0x0D corrisponderebbe a 13
          serialAnswer[cont1 - 1] = serialAnswer[cont];
          cont += 1;
          minusChar += 1;
        }
      } else {
        serialAnswer[cont1 - 1] = serialAnswer[cont - 1];
      }
    }
    nBytes = nBytes - minusChar;
    this.serialAnswer = serialAnswer;
    return nBytes;
  }

/* 
int ACS::Togli_DLE(int *Num_Carattere)
{
	int Cont,Cont1=0, Car_da_sottrarre=0;

	for (Cont = 1; Cont <= *Num_Carattere; Cont++)
	{
		Cont1 = Cont1 + 1;
		if (Risposta_Seriale[Cont-1] == (0x10))
		{
			if ((Risposta_Seriale[Cont + 1-1] == (0x0D)) || (Risposta_Seriale[Cont + 1-1] == (0x10)))
			{
				Risposta_Seriale[Cont1-1] = Risposta_Seriale[Cont + 1-1];
				Cont = Cont + 1; Car_da_sottrarre = Car_da_sottrarre + 1;
			}
		}
		else Risposta_Seriale[Cont1-1] = Risposta_Seriale[Cont-1];
	}
	*Num_Carattere = *Num_Carattere - Car_da_sottrarre;
	return 0;
}   /* Togli_DLE */


  
  public long ConversioneRX(byte[] data, byte[] dataNt) {
    long ValNum=0L;
    long[] NUM = new long[4];
    byte[] PIS = new byte[20];
    
    for (int i = 0; i < 4; i++) {
      dataNt[i] = data[i];
      NUM[i] = (data[i]);
      
      //System.out.println ("NUM["+i+"]="+NUM[i]);
    }
    if (NUM[0] > 127L) {
      ValNum = (NUM[3] - 256L) + (NUM[2] - 255L) * 256L + (NUM[1] - 255L) * 65536L + (NUM[0] - 255L) * 16777216L;
    } else {
      ValNum = NUM[3] + NUM[2] * 256L + NUM[1] * 65536L + NUM[0] * 16777216L;
    }
    PIS = sbld("%d", (int) ValNum);
    return ValNum;
  }
  
  public int DataConversionRX(byte[] data){
    int ValNum = 0;
    long[] NUM = new long[4];
    byte[] PIS = new byte[20];
    
    for (int i = 0; i < 4; i++) {
      NUM[i] = (data[i]);
      
      //System.out.println ("NUM["+i+"]="+NUM[i]);
    }
    //if (NUM[0] > 127L) {
      //ValNum = (NUM[3] - 256L) + (NUM[2] - 255L) * 256L + (NUM[1] - 255L) * 65536L + (NUM[0] - 255L) * 16777216L;
    //} else {
      //ValNum = NUM[3] + NUM[2] * 256L + NUM[1] * 65536L + NUM[0] * 16777216L;
    //}
    PIS = sbld("%d", (int) ValNum);
    return ValNum;
  }



  public int CommandMot(byte[] instruction) {
    long Value;
    SerialWrite(instruction, 0);
    int nBytes = SerialRead();

    PrintArray(this.serialAnswer);

    return Error();
  }

  public int TellCommand(byte[] c) {
    int Err, nBytes = 0;
    SerialWrite(c);
    nBytes = SerialRead();

    boolean PRINT = true;
    if (PRINT) {
      System.out.print("Comando tell inviato: ");
      PrintArray(this.serialCommand);
      System.out.println("Risposta al comando: ");
      System.out.println(answerString);
    }

    if (nBytes < 1)
      nBytes = -1;

    Err = Error();
    
    if (Err != -1) {
      return Err;
    } else {
      RemoveDLE(this.serialAnswer);
      TellScan(c[1], this.serialAnswer);
    }
    return -1;
  }

  public int TellCommand(String C) throws InterruptedException {
    int Err, nBytes = 0;
    DirectCommand(C);
    byte[] c = String.valueOf(C).getBytes();
    
    if (nBytes < 1)
      nBytes = -1;

    Err = Error();
    
    if (Err != -1) {
      return Err;
    } else {
      RemoveDLE(this.serialAnswer);
      TellScan(c[1], this.serialAnswer);
    }
    return -1;
  }
  
  public int TellScan(byte subCommand, byte[] serialAnswer) {
    switch (subCommand) {
      case '0':
        this.Tell0.T0Control = serialAnswer[0];
        this.Tell0.TstateMotorX = serialAnswer[1];
        this.Tell0.TmodeMotX = serialAnswer[2];
        this.Tell0.TsmodeMotX = serialAnswer[3];
        this.Tell0.TstepX = serialAnswer[4];
        this.Tell0.TstateMotorY = serialAnswer[5];
        this.Tell0.TmodeMotY = serialAnswer[6];
        this.Tell0.TsmodeMotY = serialAnswer[7];
        this.Tell0.TstepY = serialAnswer[8];
        this.Tell0.TstateMotorZ = serialAnswer[9];
        this.Tell0.TmodeMotZ = serialAnswer[10];
        this.Tell0.TsmodeMotZ = serialAnswer[11];
        this.Tell0.TstepZ = serialAnswer[12];
        this.Tell0.Tinformation = serialAnswer[5];
        this.Tell0.T0CheckSum = serialAnswer[6];
        break;
      case '1':
        this.Tell1.T1Control = serialAnswer[0];
        this.Tell1.T1Codex = serialAnswer[1];
        this.Tell1.T1DataX = serialAnswer[2];
        this.Tell1.T1DataY = serialAnswer[3];
        this.Tell1.T1DataZ = serialAnswer[4];
        this.Tell1.T1DataT = serialAnswer[5];
        this.Tell1.T1CheckSum = serialAnswer[6];
        break;
      case '2':
        this.Tell2.T2Control = serialAnswer[0];
        this.Tell2.T2Codex = serialAnswer[1];
        this.Tell2.T2DataX = serialAnswer[2];
        this.Tell2.T2DataY = serialAnswer[3];
        this.Tell2.T2DataZ = serialAnswer[4];
        this.Tell2.T2DataT = serialAnswer[5];
        this.Tell2.T2CheckSum = serialAnswer[6];
        break;
    }
    return 0;
  }

  public byte[] sbld(String control, String args) {
    //byte[] command = new byte[255];
    String formatted = String.format(control, args);
    byte[] commandBytes; // = String.valueOf(formatted).getBytes();

    try {
      commandBytes = String.valueOf(formatted).getBytes("US-ASCII");
      return commandBytes;
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // System.arraycopy(commandBytes, 0, command, 0, commandBytes.length);
    //System.out.println("sbld: ");
    //PrintArray(commandBytes);

    //return commandBytes;
    return new byte[0];
  }
  
  public byte[] sbld(String control, int args) {
    //byte[] command = new byte[255];
    String formatted = String.format(control, args);
    byte[] commandBytes;
    try {
      commandBytes = String.valueOf(formatted).getBytes("US-ASCII");
      return commandBytes;
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //System.arraycopy(commandBytes, 0, command, 0, commandBytes.length);
    //command = CutBuffer(command, commandBytes.length);
    
    //System.out.println("sbld: ");
    //PrintArray(commandBytes);

    return new byte[0];
  }

  public int NewSerialWrite(byte[] c) {
    int i;
    int CheckSum = 0;
    String buffer = ""; //new String();
    for (i = 0; i < c.length; i++) {
      CheckSum = CheckSum + (int) c[i];
      buffer += (char) c[i];
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13) || (CheckSum == 16))
      CheckSum += 128;


    buffer += (char) CheckSum;
    buffer += '\r';

    byte[] finalBuffer = String.valueOf(buffer).getBytes();
    this.serialCommand = finalBuffer;
    System.out.println("Buffer: "+buffer);
    System.out.println("Final Buffer: ");
    PrintArray(finalBuffer);

    communication.Write(finalBuffer);
    return 0;

  }
  
  
  public int SerialWrite(byte[] c) {
    int i;
    int CheckSum = 0;
    byte[] buffer = new byte[300];
    int index = 0;
    // System.out.println("cks in int: ");
    for (i = 0; i < c.length; i++) {
      CheckSum = CheckSum + (int) c[i];
      System.out.println((int) c[i]);
      buffer[index] = c[i];
      index+=1;
      //communication.Write(c[i]);
      //System.out.print((char) c[i]);
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13) || (CheckSum == 16))
      CheckSum += 128;


    //CheckSum += 50;
    //short cks1 = (short) (CheckSum&0XFF);
    //System.out.println("short cks: "+cks1);
    //
    //System.out.println("nuovi checksum: ");
    byte CR = 0x0D;
    //buffer[index] = (byte) (cks1&0XF0);
    //System.out.println(buffer[index]);
    //index++;
    //buffer[index] = (byte) (cks1&0X0F);
    //System.out.println(buffer[index]);
    buffer[index] = 72;
    index++;
    buffer[index] = CR;
    //index++;
    //communication.Write(cks);
    //communication.Write(CR);
    byte[] finalBuffer = CutBuffer(buffer,index);
    this.serialCommand = finalBuffer;
    System.out.println("Final Buffer: ");
    PrintArray(finalBuffer);

    communication.Write(finalBuffer);
    return 0;

  }

  public int SerialWrite(byte[] c, int Value) {
    int i, CheckSum = 0;
    byte[] Dati = new byte[4];
    byte sedici = 16;
    byte[] buffer = new byte[300];
    int index = 0;
    
    for (i = 0; i < c.length; i++) {
      CheckSum = CheckSum + c[i];
      buffer[index] = c[i];
      index++;
      //communication.Write(c[i]);
    }
    
    for (i = 0; i < 4; i++) {
      Dati[3 - i] = (byte) (Value >> 8 * i); // (int)
    }
    for (i = 0; i < 4; i++) {
      if ((Dati[i] == 16) || (Dati[i] == 13))
        //communication.Write(sedici);
        buffer[index] = 0x010; //sedici
        index++;

      CheckSum = CheckSum + (int) Dati[i];
      //communication.Write(Dati[i]);
      buffer[index] = Dati[i];
      index++;
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13) || (CheckSum == 16))
      CheckSum += 128;

    byte CR = 0x0D;
    byte cks = (byte) CheckSum;
    buffer[index] = cks;
    index++;
    buffer[index] = CR;
    //index++;
    //communication.Write(cks);
    //communication.Write(CR);
    byte[] finalBuffer = CutBuffer(buffer,index);
    communication.Write(finalBuffer);
    this.serialCommand = finalBuffer;
    return 0;

  }

  public byte[] CutBuffer(byte[] buffer, int index){
    byte[] finalBuffer = new byte[index+1];
    for (int i = 0; i < index + 1; i++){
      finalBuffer[i] = buffer[i];
    }
    return finalBuffer;
  }

  public int SerialRead() { // int
  this.answerString = this.communication.ReadMessage();
    this.serialAnswer = String.valueOf(answerString).getBytes();
    boolean PRINT = true;
    if (PRINT) {
      System.out.println("Risposta al comando: ");
      System.out.println(this.answerString);
    }
    int nBytes = serialAnswer.length;
    return nBytes;
  }
 
  public int SerialReadOld() { // int numberBytes
    try {
      TimeUnit.MILLISECONDS.sleep(200);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    this.answerString = communication.ReadMessage();
    //System.out.println("Read String size:"+answerString.length());
    try {
      TimeUnit.MILLISECONDS.sleep(300);
      this.serialAnswer = String.valueOf(this.answerString).getBytes("US-ASCII");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    int nBytes = serialAnswer.length;
    
    //if (nBytes > 1)
      //this.serialAnswer[nBytes - 1] = (byte) '\0';
    return nBytes;
  }
  
  public int SerialRead(int numberBytes) {
    this.serialAnswer = communication.Read(numberBytes);
    int nBytes = serialAnswer.length;
    //if (nBytes > 1)
      //this.serialAnswer[nBytes - 1] = (byte) '\0';
    return nBytes;
  }

  public int Error() {
    if (serialAnswer.length >= 1) {
      int indexQuestionMark = 0;
      byte qmark = '?';
      if (serialAnswer[0] == qmark)
        indexQuestionMark = 1;

      // if (serialAnswer[1] == qmark)
      // indexQuestionMark = 2;

      if (indexQuestionMark != 0)
        return serialAnswer[indexQuestionMark];
      else
        return -1;
    } else
      return -6;
  }

  public byte[] GetAnswer() {
    return this.serialAnswer;
  }

  public static void main(String[] a) throws InterruptedException { // sudo chmod 777 /dev/ttyS0     sudo chmod 777 /dev/ttyUSB0

    System.out.println();

    /* 
    ACSv4 acs = new ACSv4();
    String stringa = "ABC"+0X10+0X10+"abcz";
    System.out.println(stringa);
    byte[] dummyAns = String.valueOf(stringa).getBytes();
    acs.PrintArray(dummyAns);
    // (int) 0XAF          =  175
    // (byte) 0XAF         =  -81
    // ((byte) 0XAF) + 256 =  175
    int tot = ((byte) 0XAF) + 256;
    System.out.println(tot); 
    // */


    ///* 
    ACSv4 acs = new ACSv4("/dev/ttyUSB0");
    acs.SetSimpleStart(0);
    int ErrorCode;
    byte[] command;
    //ErrorCode = acs.DirectCommand("T0\r");
    
    command = acs.sbld("R%sLR", "X");
    ErrorCode = acs.CommandReport(command);
    //*/

    System.out.println();

    //System.out.println("Error code: " + ErrorCode);
  }

}
