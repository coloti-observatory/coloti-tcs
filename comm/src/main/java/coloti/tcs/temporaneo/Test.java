package coloti.tcs.temporaneo;

import java.io.UnsupportedEncodingException;

import coloti.tcs.task.TaskListener;

public class Test {

  public Test() {}

  public static void funz(int num) {
    System.out.println(num);

    num = num * 2 + 1;

    System.out.println(num);
  }

  public static int mod(int i, int j) { // VERIFICATO
    return (i - ((int) (i / j)) * j);
  }

  public static void PrintArray(double[] arr) { // VERIFICATO
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i]);
      System.out.print(" ");
    }
    System.out.println();
  }

  public static void PrintArray(int[] arr) { // VERIFICATO
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i]);
      System.out.print(" ");
    }
    System.out.println();
  }

  public static void PrintArray(byte[] arr) { // VERIFICATO
    System.out.println("--");
    for (int i = 0; i < arr.length; i++) {
      System.out.println((char) arr[i] + " , " + (int) (arr[i] & 0XFF));
    }
    System.out.println("--");
  }

  public static void PrintArray(char[] arr) { // VERIFICATO
    System.out.println("--");
    for (int i = 0; i < arr.length; i++) {
      System.out.println(arr[i] + " , " + (int) (arr[i] & 0XFF));
    }
    System.out.println("--");
  }

  public static void PrintBytes(byte[] arr) { // VERIFICATO
    int arrSize = arr.length;
    for (int i = 0; i < arrSize; i++) {
      System.out.print((char) arr[i]);
    }
  }

  public static void PrintBytesln(byte[] arr) { // VERIFICATO
    int arrSize = arr.length;
    for (int i = 0; i < arrSize; i++) {
      System.out.print((char) arr[i]);
    }
    System.out.println();
  }

  public static int SerialWrite(byte[] c, int Value) { // ?
    int i, CheckSum = 0;
    int CheckSumFinal = 0;
    String buffer = "";

    // byte[] Dati = new byte[]{0X0,0X0D,0X07,0X0};
    byte[] Dati = new byte[4];

    for (i = 0; i < c.length; i++) {
      CheckSum = CheckSum + (int) c[i];
      buffer += (char) c[i];
    }

    for (i = 0; i < 4; i++) {
      Dati[3 - i] = (byte) (Value >> 8 * i);
      // System.out.println("-.--.");
      // System.out.println((byte) Dati[3-i]);
    }

    for (i = 0; i < 4; i++) {
      if ((Dati[i] == 16) || (Dati[i] == 13))
        buffer += 0X10; // sedici
      CheckSum += (int) Dati[i];
      buffer += (char) (Dati[i]);
    }

    CheckSumFinal = mod(CheckSum, 256);
    // if ((CheckSum == 13) || (CheckSum == 16))
    // CheckSumFinal += 128;
    System.out.println((CheckSumFinal & 0XFF));
    buffer += (char) (CheckSumFinal & 0XFF);
    buffer += '\r';
    System.out.println(buffer);

    char[] caratteri = buffer.toCharArray();
    System.out.println("lunghezza char[] da stringa: ");
    System.out.println(caratteri.length);

    int[] interi = new int[caratteri.length];
    for (i = 0; i < caratteri.length; i++) {
      interi[i] = (int) (caratteri[i] & 0XFF);
    }

    System.out.println("char[] completo: ");
    PrintArray(caratteri);

    System.out.println("int[] completo: ");
    PrintArray(interi);

    byte[] finalBuffer = String.valueOf(buffer).getBytes();

    PrintArray(finalBuffer);
    // this.serialCommand = finalBuffer;

    // communication.Write(finalBuffer);
    return 0;
  }

  public static byte[] sbld(String control) { // VERIFICATO
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


    // NON SERVONO
    /* 
    public double[] AzEl2HaDec(double az, double el, double phi) {
      double sa, ca, se, ce, sp, cp, x, y, z, r;
      final double[] hadec = new double[2];
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
      final double[] azel = new double[2];
      double az, el;
      ha = ha*H2R;
      dec = dec*D2R;
      phi = phi*D2R;
      //Useful trig functions 
      sh = Math.sin(ha);
      ch = Math.cos(ha);
      sd = Math.sin(dec);
      cd = Math.cos(dec);
      sp = Math.sin(phi);
      cp = Math.cos(phi);

      // Az,El as x,y,z 
      x = - ch * cd * sp + sd * cp;
      y = - sh * cd;
      z = ch * cd * cp + sd * sp;

      // To spherical 
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
  */



  public static void main(String[] argV) {
    
    







  }



    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    //      00000000000     00000000000     000000000000000
    //      00000000000     00000000000     000000000000000
    //      000             000                   000      
    //      000  000000     00000000000           000      
    //      000  000000     00000000000           000      
    //      000     000     000                   000      
    //      00000000000     00000000000           000      
    //      00000000000     00000000000           000      


    // GETTERS




    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    //      00000000000     00000000000     000000000000000
    //      00000000000     00000000000     000000000000000
    //      000             000                   000      
    //      00000000000     00000000000           000      
    //      00000000000     00000000000           000      
    //              000     000                   000      
    //      00000000000     00000000000           000      
    //      00000000000     00000000000           000      


    // SETTERS 






    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------


    //      00000000000     00000      00000     00000000        
    //      00000000000     000000    000000     0000000000         
    //      000             000 000  000 000     000    0000         
    //      000             000  000000  000     000     0000 
    //      000             000   0000   000     000      0000
    //      000             000    00    000     000      0000
    //      00000000000     000          000     000000000000       
    //      00000000000     000          000     00000000000      


    // COMANDI OPCUA     
}
