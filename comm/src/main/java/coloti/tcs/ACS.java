package coloti.tcs;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/* Altri import superflui 
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DrbgParameters.NextBytes;
import java.util.function.IntPredicate;
import javax.lang.model.util.ElementScanner6;
import coloti.tcs.CommClass;
*/

public class ACS {

  private CommClass communication;
  public int ERROR;
  boolean PRINT = true;
  int ACSOK = -1;
  int ACSposoverflow = 999;
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

  double[] AbsTargPosAx;
  double[] PositionAx;
  double[] VelAx;
  double[] AccAx;
  double[] DecAx;

  double[] ActualVelAx;

  byte[] serialAnswer; 
  byte[] serialCommand;
  String answerString;
  int[] answerInt = new int[50];
  char[] serialCommandCaratteri;



  //public ACS(){}

  public ACS() { // VERIFICATO 
    //this.communication = new CommClass(SerialID);
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
    this.AbsTargPosAx = new double[1];
    this.VelAx = new double[1];
    this.ActualVelAx = new double[1];
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

    //SetUserUnit(X, )

  }


  public ACS(String SerialID) { // VERIFICATO 
    this();
    this.communication = new CommClass(SerialID);
  }

  public ACS(String SerialID, int nax) { // VERIFICATO 
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
    this.AbsTargPosAx = new double[nax];
    this.VelAx = new double[nax];
    this.ActualVelAx = new double[nax];
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
    
    this.communication = new CommClass(SerialID);
  }

  // STARTING

  public boolean OpenCommunications(int baud, byte bytesize, byte stop, byte parity, int timeout) { // VERIFICATO 
    boolean status = this.communication.GetStatus();
    if (!this.communication.GetStatus()) {
      status = this.communication.Open(baud, bytesize, stop, parity, timeout);
    }
    this.CommStatus = status;
    return status;
  }

  public boolean OpenCommunications() { // VERIFICATO 
    boolean status = this.communication.GetStatus();
    if (!this.communication.GetStatus()) {
      status = this.communication.Open();
      this.communication.SetTimeouts(7000);
    }
    this.CommStatus = status;
    return status;
  }

  public boolean CloseComm(){
    boolean status = this.communication.GetStatus();
    if (this.communication.GetStatus())
      status = this.communication.Close();
    this.CommStatus = status;
    return status;
  }

  /*public void SetSimpleStart(int mode){ // VERIFICATO 
    if (OpenCommunications()) {
      SetMode(mode); // 0 host mode, 1 terminal mode
    }
  }*/

  public boolean SetSimpleStart(int mode){ // VERIFICATO 
    OpenCommunications();
    if (this.CommStatus) {
      SetMode(mode); // 0 host mode, 1 terminal mode
    }
    return this.CommStatus;
  }

  public boolean SetSimpleStart(int mode, int baud, byte bytesize, byte stop, byte parity, int timeout){ // VERIFICATO 
    OpenCommunications(baud, bytesize, stop, parity, timeout);
    if (this.CommStatus) {
      SetMode(mode); // 0 host mode, 1 terminal mode
    }
    return this.CommStatus;
  }

  public int InitAxes() { // VERIFICATO 
    int Err = ACSOK;
    if (this.CommStatus) {
      for (int i = 0; i < this.NAXES; i++) {
        Err = GetEncoderRes(this.axes[i]);
        this.GEARRATIO[i] = 1;
        this.CONVFACTOR[i] = 1;
        Err = GetMotionMode(this.axes[i]);
        this.MaxAbsVel[i] = this.MaxVel[i] = this.ENCODERRES[i];
        this.MinAbsVel[i] = this.MinVel[i] = 0;
        this.MaxAbsAcc[i] = this.MaxAcc[i] = this.ENCODERRES[i];
        this.MinAbsAcc[i] = this.MinAcc[i] = 1000;
        Err = GetMotMaxMinPos(this.axes[i]);
      }
      return Err;
    }
    return -7;
  }

  // UTILITY

  public double SetUserUnit(String ax, int um, double gr) { // VERIFICATO 
    int axI = AxesNumber(ax);
    switch (um) {
      case RAD:
        this.UM = RAD;
        this.CONVFACTOR[axI] = gr * this.ENCODERRES[axI] / this.MAXMIS[RAD];
        return CONVFACTOR[axI]; //break;

      case GRAD:
        this.UM = GRAD;
        this.CONVFACTOR[axI] = gr * this.ENCODERRES[axI] / this.MAXMIS[GRAD];
        return CONVFACTOR[axI]; //break;

      case HOUR:
        this.UM = HOUR;
        this.CONVFACTOR[axI] = gr * this.ENCODERRES[axI] / this.MAXMIS[HOUR];
        return CONVFACTOR[axI];

      case ARCSECS:
        this.UM = ARCSECS;
        this.CONVFACTOR[axI] = gr * this.ENCODERRES[axI] / this.MAXMIS[ARCSECS];
        return CONVFACTOR[axI];

      default:
        return 1.0;
    }
  }

  public void MotConfig(String ax, int um, double gr, double rev) {
    SetUserUnit(ax, um, gr);
    int axI = AxesNumber(ax);
    this.MaxVel[axI] = this.MaxAbsVel[axI] = 0.85 * (rev / 60) * this.ENCODERRES[axI] / this.CONVFACTOR[axI];
    this.MaxAbsAcc[axI] = this.MaxAcc[axI] = this.MaxVel[axI];
    this.MinAbsVel[axI] = this.MinVel[axI] = -this.MaxVel[axI];
    this.MinAbsAcc[axI] = 1000 / this.CONVFACTOR[axI];
  }

  public class Tell0 {
    byte T0Control;
    byte T0MotorStateX;
    String T0MotorStateString;

    byte T0MotionModeX;
    byte T0SecMotionModeX;
    byte T0StepX;
    byte T0MotorStateY;
    byte T0MotionModeY;
    byte T0SecMotionModeY;
    byte T0StepY;
    byte T0MotorStateZ;
    byte T0MotionModeZ;
    byte T0SecMotionModeZ;
    byte T0StepZ;
    byte T0GeneralInfo;
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
  
  public boolean isRunning;
  

  public int AxesNumber(String ax) { // VERIFICATO 
    int axint = 0;
    if (ax.equals("X"))
      axint = 0;
    else if (ax.equals("Y"))
      axint = 1;
    else if (ax.equals("Z"))
      axint = 2;
    return axint;
  }

  public int mod(int i, int j) { // VERIFICATO 
    return (i - (int) (i / j) * j);
  }

  public void PrintArray(double[] arr) { // VERIFICATO 
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i]);
      System.out.print(" ");
    }
    System.out.println();
  }

  public void PrintArray(int[] arr) { // VERIFICATO 
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i]);
      System.out.print(" ");
    }
    System.out.println();
  }

  public void PrintArray(byte[] arr) { // VERIFICATO 
    System.out.println("--");
    for (int i = 0; i < arr.length; i++) {
      System.out.println((char) arr[i]+ " , "+ (int) arr[i]);
    }
    System.out.println("--");
  }

  public void PrintBytes(byte[] arr) { // VERIFICATO 
    int arrSize = arr.length;
    for (int i = 0; i < arrSize; i++) {
      System.out.print((char) arr[i]);
    }
  }

  public void PrintBytesln(byte[] arr) { // VERIFICATO 
    int arrSize = arr.length;
    for (int i = 0; i < arrSize; i++) {
      System.out.print((char) arr[i]);
    }
    System.out.println();
  }

  public void PrintCharln(char[] arr) { // VERIFICATO 
    int arrSize = arr.length;
    for (int i = 0; i < arrSize; i++) {
      System.out.print(arr[i]);
    }
    System.out.println();
  }


  // -------------

  public int Move(String ax, double pos) {
    if (this.MOTORSTATUS[AxesNumber(ax)] == 0) {
      this.ERROR = SetMotorOn(ax);
      if (ERROR != ACSOK)
        return ERROR;
    }
    
    this.ERROR = SetAbsTargPos(ax, pos);
    if (ERROR != ACSOK)
        return ERROR;

    this.ERROR = StartMove(ax);
    if (ERROR != ACSOK)
      return ERROR;

    return ACSOK;
  }

  public int Move(String ax, double pos, double vel) {

    this.ERROR = SetMotVel(ax, vel);
    if (ERROR != this.ACSOK)
      return ERROR;

    this.ERROR = Move(ax, pos);
    return ERROR;
  }

  public int Move(String ax, double pos, double vel, double acc) {

    this.ERROR = SetMotAcc(ax, acc);
    if (ERROR != this.ACSOK)
      return ERROR;

    this.ERROR = Move(ax, pos, vel);
    return ERROR;
  }

  public int Move(String ax, double pos, double vel, double acc, double dec) {

    this.ERROR = SetMotDec(ax, dec);
    if (ERROR != this.ACSOK)
      return ERROR;

    this.ERROR = Move(ax, pos, vel, acc);
    return ERROR;
  }

  public int MoveTrack(String ax, double pos, double trackvel) {
    this.ERROR = SetSlewMode(ax); 
    if (ERROR != this.ACSOK)
      return ERROR;

    this.ERROR = Move(ax, pos);
    if (ERROR != this.ACSOK)
      return ERROR;

    this.ERROR = SetTrackMode(ax);
    if (ERROR != this.ACSOK)
      return ERROR;

    // while GetEndMotionStatus
    this.ERROR = SetMotVel(ax, trackvel);
    if (ERROR != this.ACSOK)
      return ERROR;

    this.ERROR = StartMove(ax);
    if (ERROR != this.ACSOK)
      return ERROR;

    return ACSOK;
  }

  public void Sleep(int millisecondsTime) { // VERIFICATO 
    try {
      TimeUnit.MILLISECONDS.sleep(millisecondsTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  

  // SET FUNCTIONS

  public int SetHostMode() { // VERIFICATO 
    int Err = DirectCommand("SHT1\r");
    Sleep(2000);
    Err = DirectCommand("SHT0\r");
    return Err;
  }

  public int SetTerminalMode() { // VERIFICATO 
    int Err = DirectCommand("SHT0\r");
    Sleep(2000);
    Err = DirectCommand("SHT1\r");
    return Err;
  }

  public int SetMode(int Value) { // VERIFICATO 
    int Err = 100;
    if (Value == 0)
      Err = SetHostMode();
    else if (Value == 1)
      Err = SetTerminalMode();

    this.HostStatus = this.answerString.equals("00\r");

    if (this.HostStatus){
      System.out.println("You are in Host Mode (0)");
    }
    else{
      System.out.println("You are in Terminal Mode (1)");
    }

    return Err;
  }

  public int SetAbsTargPos(String ax, double pos) { // VERIFICATO 
    int Value = 0;

    if (PRINT){
      System.out.println("Check set abs targ function");
      System.out.println("Position: " + pos);
    }

    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * pos);

    if (PRINT){
      System.out.println("Conversion Factor: "+CONVFACTOR[AxesNumber(ax)]);
      System.out.println("Resulting Value: "+Value);
    }

    byte[] command = sbld("S%sAP", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetRelTargPos(String ax, double pos) { // VERIFICATO 
    int Value = 0;
    if (pos > MaxPos[AxesNumber(ax)] || pos < MinPos[AxesNumber(ax)])
      return this.ERROR = ACSposoverflow;
    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * pos);
    byte[] command = sbld("S%sRP", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetAxisZeroPos(String ax, double pos) { // VERIFICATO 
    int Value = 0;
    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * pos);
    byte[] command = sbld("S%sZP", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetMotVel(String ax, double vel) { // VERIFICATO 
    int Value = 0;
    /* 
    if (vel > MaxVel[AxesNumber(ax)])
      vel = MaxVel[AxesNumber(ax)];
    if (vel < MinVel[AxesNumber(ax)])
      vel = MinVel[AxesNumber(ax)];
      */

    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * vel);
    
    byte[] command = sbld("S%sLV", ax);

    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetMotAcc(String ax, double acc) { // VERIFICATO 
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

  public int SetMotDec(String ax, double dec) { // VERIFICATO 
    int Value = 0;
    if (dec > MaxAcc[AxesNumber(ax)])
      dec = MaxAcc[AxesNumber(ax)];

    Value = (int) Math.round(CONVFACTOR[AxesNumber(ax)] * dec);
    byte[] command = sbld("S%sLD", ax);
    int Err = CommandSet(command, Value);
    return Err;
  }

  public int SetMotorOn(String ax){ // VERIFICATO 
    if (GetMotorStatus(ax) == 0) {
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

  public int SetMotorOff(String ax){ // VERIFICATO 
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

  public int SetSlewMode(String ax) { // forse manca un GetMotionMode
    if (MOTIONMODE[AxesNumber(ax)] != 0) 
    {
      byte[] command = sbld("S%sMM", ax);
      int Err = CommandSet(command, 0);
      if (Err == -1)
        MOTIONMODE[AxesNumber(ax)] = 0;
      return Err;
    }
    return ACSOK;
  }

  public int SetTrackMode(String ax) { // forse manca un GetMotionMode
    if (MOTIONMODE[AxesNumber(ax)] != 1)
    {
      byte[] command = sbld("S%sMM", ax);
      int Err = CommandSet(command, 10);
      if (Err == -1)
        MOTIONMODE[AxesNumber(ax)] = 1;
      return Err;
    }
    return ACSOK;
  }

  public int SetOutPortOn(int ipno) { // VERIFICATO 
    if (ipno > MAXINP)
      return -2;
    
    byte[] command = sbld("SHI");
    int Err = CommandSet(command, ipno);
    return Err;
  }

  public int SetOutPortOff(int ipno) { // VERIFICATO 
    if (ipno > MAXINP)
      return -2;
    byte[] command = sbld("SLO");
    int Err = CommandSet(command, ipno);
    return Err;
  }

  public void SetNumberIOPort(int n) { // VERIFICATO 
    this.MAXINP = n;
  }


  public int StartMove(String ax) {
    byte[] command = sbld("B%s", ax);
    int Err = CommandMot(command);
    return Err;
  }

  public int StopMove(String ax){
    byte[] command = sbld("K%s", ax);
    int Err = CommandMot(command);
    if (IsMoving(ax) == 1)
      communication.Timeout(500);
    return Err;
  }

  // Per quanto riguarda i comandi di end motion (EC o E +XYZ), move command (M), wake command (@W1) ?

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

  public void SetMaxMinVel(String ax, double maxval, double minval) { // VERIFICATO 
    int axI = AxesNumber(ax);
    this.MaxVel[axI] = maxval;
    if (this.MaxVel[axI] > this.MaxAbsVel[axI])
      this.MaxVel[axI] = this.MaxAbsVel[axI];

    this.MinVel[axI] = minval;
    if (this.MinVel[axI] > this.MinAbsVel[axI])
      this.MinVel[axI] = this.MinAbsVel[axI];
  }

  public void SetMaxMinAcc(String ax, double maxval, double minval) { // VERIFICATO 
    int axI = AxesNumber(ax);
    this.MaxAcc[axI] = maxval;
    if (MaxAcc[axI] > MaxAbsAcc[axI])
      this.MaxAcc[axI] = MaxAbsAcc[axI];

    this.MinAcc[axI] = minval;
    if (MinAcc[axI] > MinAbsAcc[axI])
      this.MinAcc[axI] = MinAbsAcc[axI];
  }

  public void SetMaxMinPos(String ax, double maxval, double minval) { // VERIFICATO 
    int axI = AxesNumber(ax);
    this.MaxPos[axI] = maxval;
    this.MinPos[axI] = minval;
  }
  




  // GET FUNCTIONS

  public void GetAllInfo(String ax) { // VERIFICATO 
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

  public int GetEncoderRes(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    double EncRes;
    byte[] command = sbld("R%sLR", ax);
    int ErrorCode = CommandReport(command, PRINT);
    EncRes = this.VALUECR;

    //System.out.println("LR: ");
    //System.out.println(EncRes);

    if (ErrorCode != ACSOK)
      return ErrorCode;
    command = sbld("R%sLF", ax);
    ErrorCode = CommandReport(command, PRINT);
    EncRes = (EncRes) * Math.round(Math.pow(2., this.VALUECR)); // dnint = Math.round
    


    //System.out.println("LF: ");
    //System.out.println(this.VALUECR);
    //System.out.println(EncRes);

    //System.out.print("Encoder Resolution for axes " + ax + " : ");
    //System.out.println(EncRes);
    //System.out.println();

    this.ENCODERRES[AxesNumber(ax)] = EncRes;
    return ErrorCode;
  }

  public int GetMotionMode(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    int axI = AxesNumber(ax);
    byte[] command = sbld("R%sMM", ax);
    int ErrorCode = CommandReport(command, PRINT);
    if ((int) VALUECR == 0)
      this.MOTIONMODE[axI] = 0;
    else if ((int) VALUECR == 10)
      this.MOTIONMODE[axI] = 1;
    else
      this.MOTIONMODE[axI] = (int) this.VALUECR;
    //System.out.println(MOTIONMODE[axI]);
    return ErrorCode;
  }

  public int GetMotMaxMinPos(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    int axI = AxesNumber(ax);

    byte[] command = sbld("R%sPH", ax);
    int ErrorCode = CommandReport(command, false);
    if (ErrorCode == ACSOK)
      this.MaxPos[axI] = (double) this.VALUECR / this.CONVFACTOR[axI];
    else
      return ErrorCode;

    command = sbld("R%sPL", ax);
    ErrorCode = CommandReport(command, false);
    if (ErrorCode == ACSOK)
      this.MinPos[axI] = (double) this.VALUECR / this.CONVFACTOR[axI];
    else
      return ErrorCode;
    return 0;
  }

  public int GetMotorStatus(String ax){  // VERIFICATO 
    int ErrorCode = TellCommand("T0\r");
    if (ax.equals("X")) {
      if (((this.Tell0.T0MotorStateX) & (1 << 3)) == 0){
        this.MOTORSTATUS[0] = 0;
      }
      else{
        this.MOTORSTATUS[0] = 1;
      }
    }

    if (ax.equals("Y")) {
      if (((this.Tell0.T0MotorStateY) & (1 << 3)) == 0)
        this.MOTORSTATUS[1] = 0;
      else
        this.MOTORSTATUS[1] = 1;
    }

    if (ax.equals("Z")) {
      if (((this.Tell0.T0MotorStateZ) & (1 << 3)) == 0)
        this.MOTORSTATUS[2] = 0;
      else
        this.MOTORSTATUS[2] = 1;
    }

    return ErrorCode;
  }

  public int GetEndMotionStatus(String ax){ // VERIFICATO 
    TellCommand("T2\r");

    if (ax.equals("X"))
      return Tell2.T2DataX;

    if (ax.equals("Y"))
      return Tell2.T2DataY;

    if (ax.equals("Z"))
      return Tell2.T2DataZ;

    return ACSOK;
  }

  public int GetMotPos(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    byte[] command = sbld("R%sCP", ax);
    int Err = CommandReport(command, false);
    this.PositionAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetAbsTargPos(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    byte[] command = sbld("R%sAP", ax);
    int Err = CommandReport(command, PRINT);
    if (PRINT)
      System.out.println("Position setted before conversion: "+this.VALUECR);
    this.AbsTargPosAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetMotEncPos(String ax) { // VERIFICATO 
    byte[] command = sbld("R%sCP", ax);
    int Err = CommandReport(command, false);
    return Err;
  }

  public int GetActualMotVel(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    byte[] command = sbld("R%sAV", ax);
    int Err = CommandReport(command, false);
    this.ActualVelAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetMotVel(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    byte[] command = sbld("R%sLV", ax);
    int Err = CommandReport(command, PRINT);
    this.VelAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    System.out.println(VelAx[AxesNumber(ax)]);
    return Err;
  }

  public int GetMotAcc(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    byte[] command = sbld("R%sLA", ax);
    int Err = CommandReport(command, false);
    this.AccAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetMotDec(String ax) { // VERIFICATO 
    this.VALUECR = 0L;
    byte[] command = sbld("R%sLD", ax);
    int Err = CommandReport(command, false);
    this.DecAx[AxesNumber(ax)] = this.VALUECR / this.CONVFACTOR[AxesNumber(ax)];
    return Err;
  }

  public int GetMoveInfo() { // VERIFICATO 
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

  public int GetInpLog(int ipno) {
    if (ipno > MAXSYSINP)
      return -2;
    byte[] instruction = String.valueOf("RIL").getBytes();
    int ErrorCode = CommandReportParams(ipno, instruction);
    return ErrorCode;
  }

  public int GetInpPortStatus(int ipno) {
    if (ipno > MAXSYSINP)
      return -2;
    byte[] instruction = String.valueOf("RIP").getBytes();
    int ErrorCode = CommandReportParams(ipno, instruction);
    return ErrorCode;
  }

  public int GetOutPortStatus(int ipno) {
    if (ipno > MAXSYSINP)
      return -2;
    byte[] instruction = String.valueOf("ROP").getBytes();
    int ErrorCode = CommandReportParams(ipno, instruction);
    return ErrorCode;
  }

  public int IsMoving(String ax) { // VERIFICATO 
    int res = 0;
    TellCommand("T0");

    if (ax.equals("X")) {
      if (((this.Tell0.T0MotorStateX) & (1)) == 0)
        res = 0;
      else
        res = 1;
    }

    else if (ax.equals("Y")) {
      if (((this.Tell0.T0MotorStateY) & (1)) == 0)
        res = 0;
      else
        res = 1;
    }

    else if (ax.equals("Z")) {
      if (((this.Tell0.T0MotorStateZ) & (1)) == 0)
        res = 0;
      else
        res = 1;
    }

    return res;
  }

  public void GetAllTell0Info() { // VERIFICATO 
    TellCommand("T0");
    System.out.println(this.Tell0.T0Control);
    System.out.println(this.Tell0.T0MotorStateX);
    System.out.println(this.Tell0.T0MotorStateString);
    System.out.println(this.Tell0.T0MotionModeX);
    System.out.println(this.Tell0.T0SecMotionModeX);
    System.out.println(this.Tell0.T0StepX);
    //System.out.println(this.Tell0.T0MotorStateY);
    //System.out.println(this.Tell0.T0MotionModeY);
    //System.out.println(this.Tell0.T0SecMotionModeY);
    //System.out.println(this.Tell0.T0StepY);
    //System.out.println(this.Tell0.T0MotorStateZ);
    //System.out.println(this.Tell0.T0MotionModeZ);
    //System.out.println(this.Tell0.T0SecMotionModeZ);
    //System.out.println(this.Tell0.T0StepZ);
    //System.out.println(this.Tell0.T0GeneralInfo);
    //System.out.println(this.Tell0.T0CheckSum);

  }



  // COMMUNICATIONS

  public int DirectCommand(String Instruction) { // VERIFICATO 
    byte[] instruction = String.valueOf(Instruction).getBytes();
    this.communication.Write(instruction);
    //Sleep(200);
    this.answerString = this.communication.ReadMessage();
    this.serialAnswer = String.valueOf(answerString).getBytes();

    
    if (PRINT) {
      System.out.println("Comando inviato: ");
      System.out.println(Instruction);
      System.out.println("Risposta al comando: ");
      System.out.println(this.answerString);
    }
    return Error();
  }

  public int CommandSet(byte[] Instruction, int Value) { // VERIFICATO 
    SerialWrite(Instruction, Value);
    Sleep(200);
    SerialRead();
    if (PRINT) {
      System.out.print("Comando Set inviato: ");
      PrintCharln(this.serialCommandCaratteri);
      //System.out.println("Risposta al comando: ");
      //PrintBytesln(this.serialAnswer);
    }
    return Error();
  }

  public int CommandSet(byte[] Instruction) { // VERIFICATO 
    SerialWrite(Instruction);
    Sleep(200);
    SerialRead();
    if (PRINT) {
      System.out.print("Comando Set inviato: ");
      PrintCharln(this.serialCommandCaratteri);
      //System.out.println("Risposta al comando: ");
      //PrintBytesln(this.serialAnswer);
    }
    return Error();
  }
  
  public int CommandReport(byte[] instruction, boolean PRINT) { // VERIFICATO
    this.VALUECR = 0;
    int Count, Err, nBytes = 0;
    int[] Data = new int[4];
    SerialWrite(instruction);
    Sleep(200);

    nBytes = SerialRead();
    //this.serialAnswer[nBytes] = '\0';
    //System.out.println("Before DLE:" + nBytes);
    
    if (PRINT) {
      System.out.println("Comando inviato: ");
      PrintCharln(this.serialCommandCaratteri);
      System.out.println("Risposta al comando: ");
      PrintBytesln(this.serialAnswer);
    }

    if (this.HostStatus){
      nBytes = RemoveDLE();
      //System.out.println("After DLE:" + nBytes);
      /*
      if (nBytes == -2)
        return -2;
      Err = Error();
      if (Err != -1)
        return Err;
        */
      for (Count = 4; Count < 8; Count++){
        Data[Count - 4] = this.answerInt[Count];
      }

      this.VALUECR = DataConversionRX(Data);
      //this.VALUECR = ConversioneRX(Data, DataNt);
      //System.out.print("Risultato report ");
      //PrintBytesln(this.serialCommand);
      //System.out.println(this.VALUECR);
    }
    //System.out.println();
    return Error();
  }

  public int IsProgramRunning() { // VERIFICATO
    this.VALUECR = 0;
    int Count, Err, nBytes = 0;
    int[] Data = new int[4];
    SerialWrite(sbld("T3"));
    Sleep(200);

    nBytes = SerialRead();
    //this.serialAnswer[nBytes] = '\0';
    //System.out.println("Before DLE:" + nBytes);
    
    if (PRINT) {
      System.out.println("Comando inviato: ");
      PrintCharln(this.serialCommandCaratteri);
      System.out.println("Risposta al comando: ");
      PrintBytesln(this.serialAnswer);
    }

    if (this.HostStatus){
      nBytes = RemoveDLE();
      
      //PrintArray(this.serialAnswer);

      if (answerInt[3] == 0 && answerInt[4] == 0)
        this.isRunning = false;
      else
        this.isRunning = true;

    }
    return Error();
  }


  public int CommandReportParams(int iopo, byte[] instruction) { // VERIFICATO 
    this.VALUECR = 0;
    int Count, Err, nBytes = 0;
    int[] Data = new int[4];
    SerialWrite(instruction);
    Sleep(200);
    nBytes = SerialRead();

    if (this.HostStatus){
      nBytes = RemoveDLE();

      /* 
      if (nBytes == -2)
        return -2;
      Err = Error();
      if (Err != -1)
        return Err;
      */

      for (Count = 5; Count < 7; Count++){
        Data[Count - 3] = this.answerInt[Count];
      }

      this.VALUECR = DataConversionRX(Data);
      this.VALUECR = (int) ((this.VALUECR >> (iopo - 1)) & (1));
      //this.VALUECR = ConversioneRX(Data, DataNt);
      //System.out.print("Risultato report ");
      //PrintBytesln(this.serialCommand);
      //System.out.println(this.VALUECR);
    }
    //System.out.println();
    return -1;
  }

  public int RemoveDLE(){ // VERIFICATO 
    int cont, cont1 = 0, nRemoveBytes = 0;
    int nBytes = this.answerInt.length;
    for (cont = 1; cont <= nBytes; cont++){
      cont1 += 1;
      if (this.answerInt[cont - 1] == 16){
        if ((this.answerInt[cont] == 13) || (this.answerInt[cont] == 16)){
          this.answerInt[cont1 - 1] = this.answerInt[cont];
          cont += 1;
          nRemoveBytes += 1;
        }
      }
      else{
        this.answerInt[cont1 - 1] = this.answerInt[cont - 1];
      }
    }
    nBytes = nBytes - nRemoveBytes;
    return nBytes;
  }

  public int DataConversionRX(int[] data) { // VERIFICATO 
    int ValNum = 0;
    int[] NUM = new int[4];
    //byte[] PIS = new byte[20];
    for (int i = 0; i < 4; i++) {
      NUM[i] = (data[i]);
      //System.out.println ("NUM["+i+"]="+NUM[i]);
    }
    if (NUM[0] > 127) {
      ValNum = (NUM[3] - 256) + (NUM[2] - 255) * 256 + (NUM[1] - 255) * 65536 + (NUM[0] - 255) * 16777216;
    } else {
      ValNum = NUM[3] + NUM[2] * 256 + NUM[1] * 65536 + NUM[0] * 16777216;
    }
    //PIS = sbld("%d", (int) ValNum);
    return ValNum;
  }

  public int CommandMot(String instruction) { // VERIFICATO 
    byte[] Instruction = sbld(instruction);
    SerialWrite(Instruction, 0);
    SerialRead();

    return Error();
  }

  public int CommandMot(byte[] Instruction) { // VERIFICATO 
    SerialWrite(Instruction, 0);
    SerialRead();

    return Error();
  }

  public int CommandArray(String instruction, int element, int finalvalue){
    this.VALUECR = 0;
    byte[] Instruction = sbld(instruction);
    int Count, Err;
    int[] Data = new int[4];
    int[] Val = new int[2];
    if (Instruction[2] == 'S'){
      Val[0] = element;
      Val[1] = finalvalue;
      SerialWrite(Instruction, Val, 2);
      SerialRead();
      this.serialAnswer[0] = '\0';
    }
    else{
      SerialWrite(Instruction, element);
      SerialRead();
      RemoveDLE();
      for (Count = 1; Count < 5; Count++){
        Data[Count - 1] = this.answerInt[Count];
      }
      this.VALUECR = DataConversionRX(Data);
    }
    return Error();
  }

  public int CommandArray(byte[] Instruction, int element, int finalvalue){
    this.VALUECR = 0;
    int Count, Err;
    int[] Data = new int[4];
    int[] Val = new int[2];
    if (Instruction[2] == 'S'){
      Val[0] = element;
      Val[1] = finalvalue;
      SerialWrite(Instruction, Val, 2);
      SerialRead();
      this.serialAnswer[0] = '\0';
    }
    else{
      SerialWrite(Instruction, element);
      SerialRead();
      RemoveDLE();
      for (Count = 1; Count < 5; Count++){
        Data[Count - 1] = this.answerInt[Count];
      }
      this.VALUECR = DataConversionRX(Data);
    }
    return Error();
  }

  public int TellCommand(String tellstring) { // VERIFICATO 
    int Err, nBytes = 0;
    byte[] commandBytes = sbld(tellstring);
    
    if (this.HostStatus){
      SerialWrite(commandBytes);
      nBytes = SerialRead();
    }
    else{
      DirectCommand(tellstring+"\r");
    }

    
    if (PRINT & this.HostStatus) {
      System.out.println("Comando tell inviato: ");
      System.out.println(tellstring);
      System.out.println("Risposta al comando: ");
      System.out.println(this.answerString);
    }

    if (nBytes < 1)
      nBytes = -1;

    Err = Error();
    
    if (Err != -1) {
      return Err;
    } else if (this.HostStatus){
      RemoveDLE();
      TellScan(commandBytes[1], this.serialAnswer);
    }
  
    return Error();
  }
  
  public int TellScan(byte subCommand, byte[] serialAnswer) { // VERIFICATO 
    switch (subCommand) {
      case '0':
        this.Tell0.T0Control = serialAnswer[0];
        this.Tell0.T0MotorStateX = serialAnswer[1];
        this.Tell0.T0MotorStateString = "Motor State: ";
        BitSet bitsMState = BitSet.valueOf(new byte[]{this.Tell0.T0MotorStateX});

        if (bitsMState.get(0)){
          this.Tell0.T0MotorStateString += "is in motion, ";
        }
        else{
          this.Tell0.T0MotorStateString += "is not in motion, ";
        }
        if (bitsMState.get(1)){
          this.Tell0.T0MotorStateString += "is waiting berween motions, ";
        }
        else{
          this.Tell0.T0MotorStateString += "is not in waiting, ";
        }
        if (bitsMState.get(3)){
          this.Tell0.T0MotorStateString += "is enabled, ";
        }
        else{
          this.Tell0.T0MotorStateString += "is disabled, ";
        }
        if (bitsMState.get(4)){
          this.Tell0.T0MotorStateString += "is ready and waiting for an input or GO.";
        }
        else{
          this.Tell0.T0MotorStateString += "is not waiting for an input or GO command.";
        }

        this.Tell0.T0MotionModeX = serialAnswer[2];
        this.Tell0.T0SecMotionModeX = serialAnswer[3];
        this.Tell0.T0StepX = serialAnswer[4];
        this.Tell0.T0GeneralInfo = serialAnswer[5];
        this.Tell0.T0CheckSum = serialAnswer[6];
        
        this.Tell0.T0MotorStateY = serialAnswer[5];
        this.Tell0.T0MotionModeY = serialAnswer[6];
        this.Tell0.T0SecMotionModeY = serialAnswer[7];
        this.Tell0.T0StepY = serialAnswer[8];
        this.Tell0.T0MotorStateZ = serialAnswer[9];
        this.Tell0.T0MotionModeZ = serialAnswer[10];
        this.Tell0.T0SecMotionModeZ = serialAnswer[11];
        this.Tell0.T0StepZ = serialAnswer[12];
        
        break;
      case '1':
        this.Tell1.T1Control = serialAnswer[0];
        this.Tell1.T1Codex = serialAnswer[1];
        this.Tell1.T1DataX = serialAnswer[2];
        this.Tell1.T1CheckSum = serialAnswer[3];

        this.Tell1.T1DataY = serialAnswer[3];
        this.Tell1.T1DataZ = serialAnswer[4];
        this.Tell1.T1DataT = serialAnswer[5];
        break;
      case '2':
        this.Tell2.T2Control = serialAnswer[0];
        this.Tell2.T2Codex = serialAnswer[1];
        this.Tell2.T2DataX = serialAnswer[2];
        this.Tell2.T2CheckSum = serialAnswer[3];

        this.Tell2.T2DataY = serialAnswer[3];
        this.Tell2.T2DataZ = serialAnswer[4];
        this.Tell2.T2DataT = serialAnswer[5];
        break;
    }
    return 0;
  }

  public byte[] sbld(String control) { // VERIFICATO 
    byte[] commandBytes;

    try {
      commandBytes = String.valueOf(control).getBytes("US-ASCII");
      return commandBytes;
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new byte[0];
  }

  public byte[] sbld(String control, String args) { // VERIFICATO 
    String formatted = String.format(control, args);
    byte[] commandBytes;

    try {
      commandBytes = String.valueOf(formatted).getBytes("US-ASCII");
      return commandBytes;
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new byte[0];
  }
  
  public byte[] sbld(String control, int args) { // VERIFICATO 
    String formatted = String.format(control, args);
    byte[] commandBytes;
    try {
      commandBytes = String.valueOf(formatted).getBytes("US-ASCII");
      return commandBytes;
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new byte[0];
  }


  public int SerialWrite(byte[] c) { //  
    int i, CheckSum = 0;
    String buffer = "";
    //byte[] Dati = new byte[4];
        
    for (i = 0; i < c.length; i++) {
      CheckSum = CheckSum + (int) c[i];
      buffer += (char) c[i];
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13) || (CheckSum == 16))
      CheckSum += 128;

    buffer += (char) (CheckSum & 0XFF);
    buffer += '\r';

    char[] bufferCaratteri = buffer.toCharArray();
    int[] bufferInteri = new int[bufferCaratteri.length];
    for (i = 0; i < bufferCaratteri.length; i++){
        bufferInteri[i] = (int) (bufferCaratteri[i] & 0XFF);
    }

    this.serialCommandCaratteri = bufferCaratteri;
    //byte[] finalBuffer = String.valueOf(buffer).getBytes();
    //this.serialCommand = finalBuffer;

    communication.Write(bufferInteri);
    return 0;

  }


  public int SerialWrite(byte[] c, int Value) { //  
    int i, CheckSum = 0;
    String buffer = "";
    byte[] Dati = new byte[4];
        
    for (i = 0; i < c.length; i++) {
      CheckSum = CheckSum + (int) c[i];
      buffer += (char) c[i];
    }
    
    for (i = 0; i < 4; i++) {
      Dati[3 - i] = (byte) (Value >> 8 * i);
    }

    for (i = 0; i < 4; i++) {
      if ((Dati[i] == 16) || (Dati[i] == 13))
        buffer += 0x10; //sedici
      CheckSum += (int) Dati[i];
      buffer += (char) Dati[i];
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13) || (CheckSum == 16))
      CheckSum += 128;

    buffer += (char) (CheckSum & 0XFF);
    buffer += '\r';

    char[] bufferCaratteri = buffer.toCharArray();
    int[] bufferInteri = new int[bufferCaratteri.length];
    for (i = 0; i < bufferCaratteri.length; i++){
        bufferInteri[i] = (int) (bufferCaratteri[i] & 0XFF);
    }

    this.serialCommandCaratteri = bufferCaratteri;
    //PrintArray(bufferInteri);
    //byte[] finalBuffer = String.valueOf(buffer).getBytes();
    //this.serialCommand = finalBuffer;

    communication.Write(bufferInteri);
    return 0;

  }


  public int SerialWrite(byte[] c, int[] Value, int numdata) { //  
    int i, CheckSum = 0;
    String buffer = "";
    byte[] Dati = new byte[4];
        
    for (i = 0; i < c.length; i++) {
      CheckSum = CheckSum + (int) c[i];
      buffer += (char) c[i];
    }
    
    for (int j = 0; j < numdata; j++){
      for (i = 0; i < 4; i++) {
        Dati[3 - i] = (byte) (Value[j] >> 8 * i);
      }
      for (i = 0; i < 4; i++) {
        if ((Dati[i] == 16) || (Dati[i] == 13))
          buffer += 0x10; //sedici
        CheckSum += (int) Dati[i];
        buffer += (char) Dati[i];
      }
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13) || (CheckSum == 16))
      CheckSum += 128;

    buffer += (char) (CheckSum & 0XFF);
    buffer += '\r';

    char[] bufferCaratteri = buffer.toCharArray();
    int[] bufferInteri = new int[bufferCaratteri.length];
    for (i = 0; i < bufferCaratteri.length; i++){
        bufferInteri[i] = (int) (bufferCaratteri[i] & 0XFF);
    }

    this.serialCommandCaratteri = bufferCaratteri;
    //byte[] finalBuffer = String.valueOf(buffer).getBytes();
    //this.serialCommand = finalBuffer;

    communication.Write(bufferInteri);
    return 0;

  }







  public int SerialRead() { // VERIFICATO 
    int nBytes;
    this.answerString = "";
    if (this.HostStatus) {
      int[] localAnswerInt = this.communication.ReadMessageInt();
      this.answerInt = localAnswerInt;
      //this.answerInt = this.communication.ReadMessageInt();
      nBytes = localAnswerInt.length;
      this.serialAnswer = new byte[nBytes];
      for (int i = 0; i < nBytes; i++){
        this.serialAnswer[i] = (byte) localAnswerInt[i]; //his.answerInt[i];
        this.answerString += (char) localAnswerInt[i]; //this.answerInt[i];
      }
    }

    else{
      this.answerString = this.communication.ReadMessage();
      try {
        this.serialAnswer = String.valueOf(this.answerString).getBytes("US-ASCII");
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      nBytes = serialAnswer.length;
    }

    return nBytes;
  }
    
  public int Error() { // VERIFICATO 
    if (serialAnswer.length >= 1) {
      int indexQuestionMark = 0;
      byte qmark = '?';
      if (serialAnswer[0] == qmark)
        indexQuestionMark = 1;
      if (serialAnswer[1] == qmark)
        indexQuestionMark = 2;
      if (indexQuestionMark != 0)
        return this.ERROR = serialAnswer[indexQuestionMark];
      else
        return this.ERROR = ACSOK;
    } 
    else
      return this.ERROR = 600;
  }



      // if (serialAnswer[1] == qmark)
      // indexQuestionMark = 2;


  public byte[] GetAnswer() { // VERIFICATO 
    return this.serialAnswer;
  }

  public static void main(String[] a){ // sudo chmod 777 /dev/ttyS0     sudo chmod 777 /dev/ttyUSB0

    System.out.println();

    /*
    ACSv5 acs = new ACSv5();

    byte[] command = acs.sbld("S%sMO", "X");
    //acs.SerialWrite(command, 7159000);

    //int risultatoInt = ByteBuffer.wrap(Dati).getInt();
    //System.out.println("Value sent with set command: " + risultatoInt);


    /*
    byte[] pippo = new byte[]{1<<3};
    BigInteger bits1 = new BigInteger(pippo);
    System.out.println(bits1.toString(2));

    BitSet bitsarray = BitSet.valueOf(pippo);
    for (int i=0; i<bitsarray.length(); i++){
      System.out.println("bit "+i+": "+bitsarray.get(i));
    }


    /*

    //byte[] dummyAns = new byte[]{'0','X','L','R',0,0,15,-17,-66,-96,-17,-65,-107,13};
    String dummyString = "";
    int[] dummyINTarray = new int[]{0,12,15,16,63,60,134,160,200};
    char bufferChar;
    
    for (int i = 0; i < dummyINTarray.length; i++){
      bufferChar = (char) dummyINTarray[i];
      dummyString += bufferChar;
      System.out.println(">>>> "+ bufferChar + " , "+ (int) bufferChar);

    }
    System.out.println(dummyString);

    

    /*
    String dummyString = "";
    dummyString += 'A';
    dummyString += 'B';
    dummyString += 'C';
    dummyString += (char) 0X10;
    dummyString += (char) 0X0D;
    dummyString += 'D';
    dummyString += 'E';
    dummyString += (char) 0X10;
    dummyString += (char) 0X10;
    dummyString += 'F';
    System.out.println(dummyString);
    byte[] dummyAns = String.valueOf(dummyString).getBytes();
    System.out.println("starting dummy byte array: ");
    acs.PrintArray(dummyAns);
    System.out.println("con lunghezza uguale a: " + dummyAns.length);
    System.out.println();
    int numerobytes = 0;
    numerobytes = acs.provaRemoveDLE(dummyAns);
    System.out.println("la nuova lunghezza invece: " + numerobytes);
    System.out.println("e infine la stringa risultante: ");
    acs.PrintArray(dummyAns);

    // */

    

    ///* 
    ACS acs = new ACS("/dev/ttyUSB2",1);
    acs.SetSimpleStart(1);
    //acs.IsProgramRunning();
    //System.out.println(acs.isRunning);

  


    //if (!acs.HostStatus){
    boolean acceptInput = true;
    // boolean connected = false;
    // textIO = TextIoFactory.getTextIO();
    // TextTerminal<?> terminal = textIO.getTextTerminal();
    String cmd = "";
    Scanner myObj = new Scanner(System.in);  // Create a Scanner object

    while (acceptInput) {
      System.out.print("cmd>");
      cmd=myObj.nextLine();
      
      
      if (cmd.toUpperCase().equals("QUIT"))
        acceptInput = false;
      else
        acs.DirectCommand(cmd+"\r");
      
      
      /*if (acs.HostStatus){
        acs.CommandReport(acs.sbld(cmd), true);
        acs.PrintArray(acs.serialAnswer);
        System.out.println(acs.VALUECR);
      }*/
        
          

        
        
      
      }
    
    //acs.GetEncoderRes("X");


    //acs.SetSlewMode("X");






    //acs.GetAllTell0Info();
    //ErrorCode = acs.DirectCommand("T0\r");
    
    //command = acs.sbld("R%sLF", "X");
    //ErrorCode = acs.CommandReport(command);

    //acs.GetMotMaxMinPos("Y");

    //*/

    System.out.println();

    //System.out.println("Error code: " + ErrorCode);
  }

}
