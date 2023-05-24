package coloti.tcs;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.fazecast.jSerialComm.*;

public class CommClass{

    public static final byte CR = 0x0D;
    public static final byte DLE = 0x10;
    public static final byte ETX = 0x03;
    // grandezza dei buffer di scrittura e lettura
    public static final String C1 = "/dev/ttyS0";
    public static final String C2 = "\\\\.\\COM2";
    public static final String C3 = "\\\\.\\COM3";
    public static final String C4 = "\\\\.\\COM4";
    public static final String C5 = "\\\\.\\COM5";
    public static final String C6 = "\\\\.\\COM6";
    public static final String C7 = "\\\\.\\COM7";
    public static final String C8 = "\\\\.\\COM8";
    public static final String C9 = "\\\\.\\COM8";
    public static final String C10 = "\\\\.\\COM10";
    
    private int MAXCHA;
    private int BAUD;
    private byte BYTESIZE;
    private byte STOP;
    private byte PARITY;
    private int TIMEOUT;
    private boolean Status;
    

    public byte NOPARITY = 0;
    public byte ODDPARITY = 1;
    public byte EVENPARITY = 2;
    public byte MARKPARITY = 3;
    public byte SPACEPARITY = 4;
    public byte ONESTOPBIT = 0;  // era 0
    public byte ONE5STOPBITS = 1;
    public byte TWOSTOPBITS = 2;
    
    public SerialPort port;

    public CommClass() {    
        this.Status = false;
        this.MAXCHA = 1024;
        this.BAUD =  19200; // 19200 // 9600
        this.BYTESIZE = 8;
        this.STOP = this.ONESTOPBIT;
        this.PARITY = this.NOPARITY;
        this.TIMEOUT = 2900; 
        
        this.port = SerialPort.getCommPorts()[0];
    }

        //BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());
        //OutputStream outputStream = p.getOutputStream();

    public CommClass(String IdSeriale) {
        this.Status = false;
        this.MAXCHA = 1024;
        this.BAUD = 9600; // 19200 // 9600
        this.BYTESIZE = 8;
        this.STOP = this.ONESTOPBIT;
        this.PARITY = this.NOPARITY;
        this.TIMEOUT = 2900; 
        this.port = SerialPort.getCommPort(IdSeriale);
    }
    
    
    public boolean Open(){
        port.setComPortParameters(9600, 8, port.ONE_STOP_BIT,port.NO_PARITY);
        //port.setComPortTimeouts(1, this.TIMEOUT, this.TIMEOUT); // read and write timeout        
        return port.openPort();
    }

    public boolean Open(int baud, int bytesize, int stop, int parity, int timeout) {
        port.setComPortParameters(baud, bytesize, stop, parity);
        //port.setComPortTimeouts(1, timeout, timeout); // read and write timeout
        return port.openPort();
    }

    public boolean Open(int baud) {
        port.setBaudRate(baud);
        return port.openPort();
    }

    public void SetTimeouts(int timeout){
        port.setComPortTimeouts(1, timeout, timeout); // read and write timeout
    }

    public boolean Close() {
        return port.closePort();
    }

    public boolean GetStatus(){
        return Status;
    } 

    // Come fare?
    void SetMaxBufLen(int buf) {
        this.MAXCHA = buf;
    }



    public void Timeout (int SleepTime){
        try {TimeUnit.MILLISECONDS.sleep(SleepTime);} catch (InterruptedException e1) {e1.printStackTrace();}
    }

    // SCRITTURA E LETTURA

    public void Write(String text){
        OutputStream outputStream = port.getOutputStream();
        try {
            outputStream.write(String.valueOf(text).getBytes());
            //outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public void Write(byte[] text){
        OutputStream outputStream = port.getOutputStream();
        try {
            outputStream.write(text);

            //outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Write(int[] text){
        OutputStream outputStream = port.getOutputStream();
        try {
            for (int i = 0; i<text.length; i++)
                outputStream.write(text[i]);
            
            //outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Write(byte text){
        OutputStream outputStream = port.getOutputStream();
        try {
            outputStream.write(text);
            //outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    public byte[] Read(int numberBytes){
        InputStream inputStream = new BufferedInputStream(port.getInputStream());
        byte[] answer = null;
        try {
            answer = inputStream.readNBytes(numberBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public byte[] Read(){
        InputStream inputStream = new BufferedInputStream(port.getInputStream()); //  BufferedInputStream
        byte[] answer;
        try {
            TimeUnit.MILLISECONDS.sleep(300);
            int Available = inputStream.available();
            //System.out.println("Av: ");
            //System.out.println(Available);


            answer = new byte[Available];
            answer = inputStream.readNBytes(Available);
            //System.out.println("length:");

            //System.out.println(answer.length);
            return answer;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public String ReadMessage(){
        InputStream inputStream = new BufferedInputStream(port.getInputStream()); //  BufferedInputStream
        String answer = "";
        int i = 0;
        int[] value = new int[4];
        //System.out.println("Read String results from CommClass: ");
        try {           
            while((inputStream.available())>=1){
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                char carattere = (char) (inputStream.readNBytes(1)[0] & (0XFF));
                if (i > 3 & i < 8){
                    value[i-4] = (int) carattere;
                    //System.out.println("--  " + value[i-4]);
                }
                //System.out.println(">> " + carattere + "  ,  " + (int) carattere);
                answer += (char) carattere; //(char) inputStream.readNBytes(1)[0];
                i+=1;
            }
            return answer;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "empty"; 
    }

    public int[] ReadMessageInt(){
        InputStream inputStream = new BufferedInputStream(port.getInputStream()); //  BufferedInputStream
        int[] answer = new int[64];
        int i = 0;
        int value;
        //System.out.println("Read int results from CommClass: ");
        try {           
            while((inputStream.available())>=1){
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                value = (int) (inputStream.readNBytes(1)[0] & (0XFF));
                answer[i] = value; 
                i+=1;
            }
            return answer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[0]; 
    }
    
    /* READ by Tosti
    public byte[] Read2(){
        InputStream inputStream = new BufferedInputStream(port.getInputStream()); //  BufferedInputStream
        byte[] answer;
        int i=0;
        try {
            System.out.println("Available:"+inputStream.available());            
            while((inputStream.available())>1){
                i+=1;
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                answer = inputStream.readNBytes(1);
                char aaa = (char) answer[0];
                System.out.println(aaa);
                if(i>10)
                    break;
            }
            if(i<10)
                return answer = inputStream.readNBytes(1);
            //return answer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    //*/


    public byte[] TestingComm(){
        Write((byte) 0x0D);
        byte[] answer = Read();
        return answer;
    }

    
    public static void main(String[] a) throws InterruptedException{
        CommClass com = new CommClass("/dev/ttyUSB0");
        if (com.Open()){
            
            byte[] comando0 = new byte[5];
            comando0[0] = (byte) 'S';
            comando0[1] = (byte) 'H';
            comando0[2] = (byte) 'T';
            comando0[3] = (byte) '0';
            comando0[4] = (byte) '\r';

            byte[] comando1 = new byte[5];
            comando1[0] = (byte) 'S';
            comando1[1] = (byte) 'H';
            comando1[2] = (byte) 'T';
            comando1[3] = (byte) '1';
            comando1[4] = (byte) '\r';


            byte[] comando2 = new byte[3];
            comando2[0] = (byte) 'T';
            comando2[1] = (byte) '0';
            comando2[2] = (byte) '\r';

            com.Write(comando2); 


            TimeUnit.MILLISECONDS.sleep(200);
            String risposta = com.ReadMessage();
            //System.out.println(risposta.length);
            //for (int i = 0; i < risposta.length; i++){System.out.print((char)risposta[i]);}
            System.out.print(risposta);
        }
    }
    
}
