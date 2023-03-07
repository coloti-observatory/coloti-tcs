package coloti.tcs;
import java.util.*;

import javax.lang.model.util.ElementScanner6;

import coloti.tcs.CommClass;


public class Motors {

  private CommClass communicationMotors;

  public Motors() {
    communicationMotors = new CommClass();
    communicationMotors.Open();

    int motorstatusX0 = 0;
    int motorstatusY1 = 0;
    int motorstatusZ2 = 0;

    int m_Asse1RisEnc1 = 0;
    int m_Asse1ResEnc2 = 0;
    int m_Mot1Giri = 0;
    double m_Asse1Ph = 0.0;
    double m_Asse1Pl = 0.0;
    double m_Asse1MaxVel = 0.0;
    int m_Asse2RisEnc1 = 0;
    int m_Asse2ResEnc2 = 0;
    int m_Mot2Giri = 0;
    double m_Asse2Ph = 0.0;
    double m_Asse2Pl = 0.0;
    double m_Asse2MaxVel = 0.0;
    int m_Asse3RisEnc1 = 0;
    int m_Asse3ResEnc2 = 0;
    int m_Mot3Giri = 0;
    double m_Asse3Ph = 0.0;
    double m_Asse3Pl = 0.0;
    double m_Asse3MaxVel = 0.0;
    double m_RidMot1 = 0.0;
    double m_RidMot2 = 0.0;
    double m_RidMot3 = 0.0;
  }

  public int ACSOK = -1;
  public int[] MOTORSTATUS = {0,0,0};

  public class Tell0{
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
  Tell0 Tell0;

  public class Tell1{
    byte T1Control;
    byte T1Codex;
    byte T1DataX;
    byte T1DataY;
    byte T1DataZ;
    byte T1DataT;
    byte T1CheckSum;
    byte[] T1Data = new byte[80];
  }
  Tell1 Tell1;
  
  public class Tell2{
    byte T2Control;
    byte T2Codex;
    byte T2DataX;
    byte T2DataY;
    byte T2DataZ;
    byte T2DataT;
    byte T2CheckSum;
    byte[] T2Data = new byte[80];
  }
  Tell2 Tell2;

  public byte[] sbld(String control, String args) {

    byte[] command = new byte[255];

    String formatted;
    formatted = String.format(control, args);
    byte[] commandBytes = String.valueOf(formatted).getBytes();
    System.arraycopy(commandBytes, 0, command, 0, commandBytes.length);


    // Di seguito print superflui per controllare i risultati
    /* 
    System.out.println("La stringa control diventa: ");
    String string1 = new String(commandBytes, StandardCharsets.UTF_8);
    System.out.println(string1);

    System.out.println("In bit corrisponde a: ");
    BigInteger bits1 = new BigInteger(commandBytes);
    System.out.println(bits1.toString(2));

    System.out.println();

    System.out.println("Il comando risultante come stringa: ");
    String string2 = new String(command, StandardCharsets.UTF_8);
    System.out.println(string2);

    System.out.println("In bit corrisponde a: ");
    BigInteger bits2 = new BigInteger(command);
    System.out.println(bits2.toString(2));
    */

    return command;
  }

  public int mod(int i, int j){
    return (i - (int)(i/j)*j);
  }

  public int SerialWrite(byte[] c, long[] Value, int NumData){
    //CommClass communicationMotors = new CommClass();
    //communicationMotors.Open();
    int i, j, CheckSum = 0;
    byte[] Dati = new byte[4];
    byte sedici = 16;

    for(i = 0; i < c.length; i++){
      CheckSum = CheckSum + c[i];
      communicationMotors.Write(c[i]); 
    }
    
    for (j = 0;j < NumData;j++){

      for(i = 0;i < 4;i++)
        Dati[3-i] = (byte) (Value[j] >> 8*i);  //(int)
        

      for(i = 0; i < 4; i++) {
        if ((Dati[i] == 16)||(Dati[i] == 13))
          communicationMotors.Write(sedici);

        CheckSum = CheckSum + Dati[i];
        communicationMotors.Write(Dati[i]); 
      }
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13)||(CheckSum == 16))
      CheckSum += 128;
      

    communicationMotors.Write(Integer.toString(CheckSum));
    communicationMotors.Write("\r");
    return 0; 

  }

  public int SerialWrite(byte[] c, long Value, int NumData){
    //CommClass communicationMotors = new CommClass();
    //communicationMotors.Open();
    int i, j, CheckSum = 0;
    byte[] Dati = new byte[4];
    byte sedici = 16;

    for(i = 0; i < c.length; i++){
      CheckSum = CheckSum + c[i];
      communicationMotors.Write(c[i]); 
    }
    
    for (j = 0;j < NumData;j++){

      for(i = 0;i < 4;i++)
        Dati[3-i] = (byte) (Value >> 8*i);  //(int)
        

      for(i = 0; i < 4; i++) {
        if ((Dati[i] == 16)||(Dati[i] == 13))
          communicationMotors.Write(sedici);

        CheckSum = CheckSum + Dati[i];
        communicationMotors.Write(Dati[i]); 
      }
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13)||(CheckSum == 16))
      CheckSum += 128;
      

    communicationMotors.Write(Integer.toString(CheckSum));
    communicationMotors.Write("\r");
    return 0; 

  }

  public int SerialWrite(byte[] c, int NumData){
    //CommClass communicationMotors = new CommClass();
    //communicationMotors.Open();
    int i, j, CheckSum = 0;
    byte[] Dati = new byte[4];
    byte sedici = 16;
    long Value = 0;

    for(i = 0; i < c.length; i++){
      CheckSum = CheckSum + c[i];
      communicationMotors.Write(c[i]); 
    }
    
    for (j = 0;j < NumData;j++){

      for(i = 0;i < 4;i++)
        Dati[3-i] = (byte) (Value >> 8*i);  //(int)
        

      for(i = 0; i < 4; i++) {
        if ((Dati[i] == 16)||(Dati[i] == 13))
          communicationMotors.Write(sedici);

        CheckSum = CheckSum + Dati[i];
        communicationMotors.Write(Dati[i]); 
      }
    }

    CheckSum = mod(CheckSum, 256);
    if ((CheckSum == 13)||(CheckSum == 16))
      CheckSum += 128;
      

    communicationMotors.Write(Integer.toString(CheckSum));
    communicationMotors.Write("\r");
    return 0; 

  }

  public byte[] SerialRead(){   //int numberBytes
    int maxBytes = 1023;
    //CommClass communicationMotors = new CommClass();
    //communicationMotors.Open();
    byte[] serialAnswer = communicationMotors.Read(maxBytes);
    int nBytes = serialAnswer.length;
    serialAnswer[nBytes] = '\0';
    return serialAnswer;
  }

  public int Error(byte[] serialAnswer){
    int indexQuestionMark = 0;
    String qmark = "?";
    if (serialAnswer[0] == qmark.getBytes()[0])
      indexQuestionMark = 1;
    if (serialAnswer[1] == qmark.getBytes()[0])
      indexQuestionMark = 2;

    if (indexQuestionMark != 0)
      return serialAnswer[indexQuestionMark];
    else
     return -1;
  }

  public int ComandoSet(byte[] instruction, long[] Value){
    communicationMotors.Open();
    SerialWrite(instruction, Value, 1);
    byte[] serialAnswer = SerialRead();
    return Error(serialAnswer);
    /* 
    int Err;
    if ((Err = Error(serialAnswer))!=-1) 
      return Err;
    else 
      return -1;   
    */
  }

  public int ComandoSet(byte[] instruction, long Value){
    communicationMotors.Open();
    SerialWrite(instruction, Value, 1);
    byte[] serialAnswer = SerialRead();
    return Error(serialAnswer);
  }

  public int RemoveDLE(byte[] serialAnswer, int nBytes){
    int cont, cont1 = 0, minusChar = 0;
    for (cont =1; cont <= nBytes; cont++){
      cont1 += 1;
      if (serialAnswer[cont-1] == 0x10){  // 0x10 corrisponderebbe a 16
        if (serialAnswer[cont] == 0x0D || serialAnswer[cont] == 0x10){  // 0x0D corrisponderebbe a 13
          serialAnswer[cont1-1] = serialAnswer[cont];
          cont += 1;
          minusChar += 1;
        }
      }
      else{
        serialAnswer[cont1-1] = serialAnswer[cont-1];
      }
    }
    nBytes = nBytes - minusChar;
    return 0;
  }

  public int TellScan(byte subCommand, byte[] serialAnswer){
    switch (subCommand) {
      case '0':
        Tell0.T0Control = serialAnswer[0];
        Tell0.TstateMotorX = serialAnswer[1];
        Tell0.TmodeMotX = serialAnswer[2];
        Tell0.TsmodeMotX = serialAnswer[3];
        Tell0.TstepX = serialAnswer[4];
        Tell0.TstateMotorY = serialAnswer[5];
        Tell0.TmodeMotY = serialAnswer[6];
        Tell0.TsmodeMotY = serialAnswer[7];
        Tell0.TstepY = serialAnswer[8];
        Tell0.TstateMotorZ = serialAnswer[9];
        Tell0.TmodeMotZ = serialAnswer[10];
        Tell0.TsmodeMotZ = serialAnswer[11];
        Tell0.TstepZ = serialAnswer[12];
        Tell0.Tinformation = serialAnswer[5];
        Tell0.T0CheckSum = serialAnswer[6];
        break;
      case '1':
        Tell1.T1Control = serialAnswer[0];
        Tell1.T1Codex = serialAnswer[1];
        Tell1.T1DataX = serialAnswer[2];
        Tell1.T1DataY = serialAnswer[3];
        Tell1.T1DataZ = serialAnswer[4];
        Tell1.T1DataT = serialAnswer[5];
        Tell1.T1CheckSum = serialAnswer[6];
        break;
      case '2':
        Tell2.T2Control = serialAnswer[0];
        Tell2.T2Codex = serialAnswer[1];
        Tell2.T2DataX = serialAnswer[2];
        Tell2.T2DataY = serialAnswer[3];
        Tell2.T2DataZ = serialAnswer[4];
        Tell2.T2DataT = serialAnswer[5];
        Tell2.T2CheckSum = serialAnswer[6];
        break;
    }
    return 0;
  }

  public int TellCommand(byte[] c){
    int Err, nBytes = 0;
    SerialWrite(c, 0);
    byte[] serialAnswer = SerialRead();
    nBytes = serialAnswer.length;
    if (nBytes < 1)
      nBytes = -1;

    Err = Error(serialAnswer);
    if (Err != -1){
      return Err;
    }
    else{
      RemoveDLE(serialAnswer, nBytes);
      TellScan(c[1], serialAnswer);
    }
    return -1;
  }
  
  //-----------------------------------
  
  public int GetMotorStatus(String ax){
    byte[] instruction = String.valueOf("T0").getBytes();
    int ErrorCode = TellCommand(instruction);
    int status = 0;
    if (ax == "X"){
      if (((Tell0.TstateMotorX)&(1<<3)) == 0)
        MOTORSTATUS[0] = 0;
      else
        MOTORSTATUS[0] = 1;
      status = MOTORSTATUS[0];
    }

    if (ax == "Y"){
      if (((Tell0.TstateMotorY)&(1<<3)) == 0)
        MOTORSTATUS[1] = 0;
      else
        MOTORSTATUS[1] = 1;
      status = MOTORSTATUS[1];
    }

    if (ax == "Z"){
      if (((Tell0.TstateMotorZ)&(1<<3)) == 0)
        MOTORSTATUS[2] = 0;
      else
        MOTORSTATUS[2] = 1;
      status = MOTORSTATUS[2];
    }

    return status;
  }

  public int SetMotorOn(String ax){
    if (GetMotorStatus(ax) == 0){
      byte[] command = sbld("S%sMO", ax);
      int ErroreCode = ComandoSet(command, 1L);
      if(ErroreCode == -1){
        MOTORSTATUS[Integer.valueOf(ax)] = 1;
        return ACSOK;
      }
      else{
        return ErroreCode;
      }
    }
    else{
        return ACSOK;
     }
  }

  public int SetMotorOff(String ax){
    if (GetMotorStatus(ax) == 1){
      byte[] command = sbld("S%sMO", ax);
      int ErroreCode = ComandoSet(command, 0L);
      if(ErroreCode == -1){
        MOTORSTATUS[Integer.valueOf(ax)] = 0;
        return ACSOK;
      }
      else{
        return ErroreCode;
      }
    }
    else{
        return ACSOK;
     }
  }
  

    
}
