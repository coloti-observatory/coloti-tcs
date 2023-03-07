package coloti.tcs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
//import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import java.io.BufferedInputStream;
//import java.io.UnsupportedEncodingException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.http.HttpResponse.ResponseInfo;
//import java.util.Arrays;
//import java.util.concurrent.TimeUnit;

//import com.fazecast.jSerialComm.*;

import coloti.tcs.weather.WeatherData;

public class App {

  private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
  


  /* 
  
  WeatherData wdata = new WeatherData();

  public App() {}

  public void runTest() {
    service.scheduleAtFixedRate(() -> {try {getData();} catch (Exception e) {}}, 1L, 10L, TimeUnit.SECONDS);
  }

  public void getData(){
    //wdata.printData(wdata.ExtractAllData());
    double[] controlData = wdata.getControlData();
    System.out.println("Outside Temperature:");
    System.out.println(controlData[0]);
    System.out.println("Dew Point Temperature: ");
    System.out.println(controlData[1]);
    System.out.println("Pressure (mb): ");
    System.out.println(controlData[2]);
    System.out.println("Wind Speed:");
    System.out.println(controlData[3]);
    System.out.println("Wind Direction:");
    System.out.println(controlData[4]);

     
    //  getInsideTemperature(dataFromWeatherStation("LOOP 1\r"));
    //oppure ...
    //byte[] answer = dataFromWeatherStation("LOOP 1\r");
    //getOutSideTemperature(answer)
    //getOutsideHumidity(answer)

  }


  public void stop() {
    service.shutdown();
  }

  */

  /*
  public static final CommClass communicationMotors = new CommClass();

  public int ACSOK = -1;
  public int[] MOTORSTATUS = {0,0,0};

  //public static final void Motors(){
    //communicationMotors.Open();}

  // perché static? E nel main?
  public byte[] sbld(String control, String args) {

    byte[] command = new byte[255];

    String formatted;
    formatted = String.format(control, args);
    byte[] commandBytes = String.valueOf(formatted).getBytes();
    System.arraycopy(commandBytes, 0, command, 0, commandBytes.length);


    // Di seguito print superflui per controllare i risultati

    
    //System.out.println("La stringa control diventa: ");
    //String string1 = new String(commandBytes, StandardCharsets.UTF_8);
    //System.out.println(string1);

    //System.out.println("In bit corrisponde a: ");
    //BigInteger bits1 = new BigInteger(commandBytes);
    //System.out.println(bits1.toString(2));

    //System.out.println();

    //System.out.println("Il comando risultante come stringa: ");
    //String string2 = new String(command, StandardCharsets.UTF_8);
    //System.out.println(string2);

    //System.out.println("In bit corrisponde a: ");
    //BigInteger bits2 = new BigInteger(command);
    //System.out.println(bits2.toString(2));
    

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

  public byte[] SerialRead(int numberBytes){
    //CommClass communicationMotors = new CommClass();
    //communicationMotors.Open();
    byte[] serialAnswer = communicationMotors.Read(numberBytes);
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
    byte[] serialAnswer = SerialRead(4);
    return Error(serialAnswer);

    
    //int Err;
    //if ((Err = Error(serialAnswer))!=-1) 
    //  return Err;
    //else 
    //  return -1;   
    
  }

  public int ComandoSet(byte[] instruction, long Value){
    communicationMotors.Open();
    SerialWrite(instruction, Value, 1);
    byte[] serialAnswer = SerialRead(4);
    return Error(serialAnswer);
  }



  public int CommandTell(byte[] instruction)
  {
    int Err, Num_Carattere = 0;
    long Val;
  
    SerialWrite(instruction,Val,0);
  #ifdef PRINTA
    printf("FATTA SCRITTURA SERIALE");
  #endif
    NumCar = SerialRead( );
  #ifdef PRINTA
    printf("FATTA LETTURA SERIALE");
  #endif
    if ((Err=Errore())!=-1) return Err;
    else
    {
    Togli_DLE(&Num_Carattere);
  #ifdef PRINTA
    printf("FATTO TOGLI DLE");
  #endif
    Scansione_Tell(Istruzione[1]);

    }
    return -1;
  }  

  




  
  //-----------------------------------
  
  public int GetMotorStatus(String ax){



    return 0;
  }

  


  public int SetMotorOn(String ax){
    if (GetMotorStatus(ax) == 0){
      byte[] command = sbld("S%sMO", ax);
      int Err = ComandoSet(command, 1L);
      if(Err == -1){
        MOTORSTATUS[Integer.valueOf(ax)] = 1;
        return ACSOK;
      }
    }
    return 0;
  }
  


  */

  public static byte[] sbld(String control, String c) {

    byte[] command = new byte[255];

    String formatted;
    formatted = String.format(control, c);
    byte[] commandBytes = String.valueOf(formatted).getBytes();
    System.arraycopy(commandBytes, 0, command, 0, commandBytes.length);

    
    // Di seguito print superflui per controllare i risultati
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
    
    //System.out.println("In bit corrisponde a: ");
    //BigInteger bits2 = new BigInteger(command);
    //System.out.println(bits2.toString(2));
    

    return command;
  }





  public static void main(String[] args) throws InterruptedException { // sudo chmod 777 /dev/ttyS0
    
    System.out.println();

    //String comando = new String();
    /* 
    App app = new App();
    app.runTest();
    TimeUnit.SECONDS.sleep(30);
    app.stop();
    */


    byte[] command = sbld("S%sMO", "X");
    



  }
}
