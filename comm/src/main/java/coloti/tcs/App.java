package coloti.tcs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.Year;
//import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.lang.model.util.ElementScanner6;

import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import com.fazecast.jSerialComm.*;

import java.util.Scanner;

import coloti.tcs.weather.WeatherData;
import coloti.tcs.ACS;

public class App {

  private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
  

  public CommClass communication;
  public ACS acs;


  public App() {
    this.communication = new CommClass("/dev/ttyUSB0");
  }

  public App(String PortName) {
      this.communication = new CommClass(PortName);
  }

  public boolean OpenComm(int baud, byte bytesize, byte stop, byte parity, int timeout){
    return communication.Open(baud, bytesize, stop, parity, timeout);
  }

  public byte[] TestingComm(String command){
    //communication.Timeout(3000);
    System.out.println("Sending command...");
    communication.Write(command);
    System.out.println("Receiving answer...");
    byte[] answer = communication.Read(254);
    communication.Timeout(3000);
    return answer;
    }




  //-----------------------------------------

  public static void PrintBits(String stringa){
    byte[] textBytes = String.valueOf(stringa).getBytes();
    BigInteger textBits = new BigInteger(textBytes);
    System.out.println(textBits.toString(2));
  }

  public static void PrintBits(int intero){
    byte[] textBytes = String.valueOf(intero).getBytes();
    BigInteger textBits = new BigInteger(textBytes);
    System.out.println(textBits.toString(2));
  }

  public static void PrintBits(byte Bytino){
    String string0 = String.format("%8s", Integer.toBinaryString(Bytino&0xFF).replace(' ','0'));
    System.out.println(string0);
  }

  

  public static void PrintInt(String stringa){
    byte[] textBytes = String.valueOf(stringa).getBytes();
    BigInteger textBits = new BigInteger(textBytes);
    System.out.println(textBits.toString(10));
  }



  public static void main(String[] args) throws InterruptedException { // sudo chmod 777 /dev/ttyS0
    
    System.out.println();

    /* 
    byte blablabla = '1';
    int aaaaaa = (blablabla) & (1<<1);
    System.out.println(aaaaaa);
    //if ((blablabla) & (1) == 0){}
    if (blablabla=='1')
      System.out.println("ciao ");
    System.out.println(blablabla);
    String string0 = String.format("%8s", Integer.toBinaryString(blablabla&0xFF).replace(' ','0'));
    System.out.println(string0);

    byte[] commandBytes = "1".getBytes();
    String string1 = new String(commandBytes, StandardCharsets.UTF_8);
    System.out.println(string1);
    BigInteger bits1 = new BigInteger(commandBytes);
    System.out.println(bits1.toString(2));
    */

    
    //Scanner myObject = new Scanner(System.in);

    //String ANS;

    byte val = '?';

    System.out.println((char) val);
    System.out.println("\nin bit risulta essere: ");
    PrintBits(val);





    /*
    boolean cond = false;
    if (cond){
      App app = new App();
      app.getData();
      app.runTest();
      TimeUnit.SECONDS.sleep(30);
      app.stop();
    }
    //*/

    // String comando = new String();
    // byte[] command = sbld("S%sMO", "X");


    
     //ACS acs = new ACS("/dev/ttyUSB0", 9600);

     //acs.SetHostMode();






  }
}
