package coloti.tcs;

import java.nio.Buffer;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;
import java.io.BufferedInputStream;
import java.io.IOException;
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
    private int Status;
    

    public byte NOPARITY = 0;
    public byte ODDPARITY = 1;
    public byte EVENPARITY = 2;
    public byte MARKPARITY = 3;
    public byte SPACEPARITY = 4;
    public byte ONESTOPBIT = 0;
    public byte ONE5STOPBITS = 1;
    public byte TWOSTOPBITS = 2;
    
    public SerialPort port;

    public CommClass() {    
        Status = 0;
        MAXCHA = 1024;
        BAUD = 19200; // 9600
        BYTESIZE = 8;
        STOP = this.ONESTOPBIT;
        PARITY = this.NOPARITY;
        TIMEOUT = 2900; 
        
        port = SerialPort.getCommPorts()[0];
    }

        //BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());
        //OutputStream outputStream = p.getOutputStream();

    public CommClass(String IdSeriale) {
        Status = 0;
        MAXCHA = 1024;
        BAUD = 9600; // 9600
        BYTESIZE = 8;
        STOP = this.ONESTOPBIT;
        PARITY = this.NOPARITY;
        TIMEOUT = 2000; 
        port = SerialPort.getCommPort(IdSeriale);
    }
    
    
    public void Open(){
        port.setComPortParameters(this.BAUD, this.BYTESIZE, this.STOP, this.PARITY);
        port.setComPortTimeouts(1, this.TIMEOUT, this.TIMEOUT); // read and write timeout        
        port.openPort();
        Status = 1;
    }

    public void Open(int baud, byte bytesize, byte stop, byte parity, int timeout) {
        port.setComPortParameters(baud, bytesize, stop, parity);
        port.setComPortTimeouts(1, timeout, timeout); // read and write timeout
        port.openPort();
        Status = 1;
    }

    public void Open(int baud) {
        port.setBaudRate(baud);
        port.openPort();
        Status = 1;
    }

    public void Close() {
        port.closePort();
        Status = 0;
    }

    public int GetStatus(){
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
        BufferedInputStream inputStream = new BufferedInputStream(port.getInputStream());
        byte[] answer = null;
        try {
            answer = inputStream.readNBytes(numberBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answer;
    }



    // a piu alto livello
    /* 
    public void sbld(byte[] dest, String control){
        
        byte[] buff = new byte[255];
        Formatter formatter = new Formatter();
        Object args;
        formatter.format(buff, control);


        String result = formatter.toString();
        System.arraycopy(result.toCharArray(), 0, dest, 0, result.length());
    }
    */




    // altre funzioni:
    // GetCommHandle()

}
