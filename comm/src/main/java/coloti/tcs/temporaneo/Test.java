package coloti.tcs.temporaneo;

import java.io.UnsupportedEncodingException;

public class Test {

    public Test(){}

    public static void funz(int num){
        System.out.println(num);

        num = num*2+1;

        System.out.println(num);
    }

    public static int mod(int i, int j) { // VERIFICATO 
        return (i - ((int) (i / j) )* j);
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
      System.out.println((char) arr[i]+ " , "+ (int) (arr[i] & 0XFF) );
    }
    System.out.println("--");
  }

  public static void PrintArray(char[] arr) { // VERIFICATO 
    System.out.println("--");
    for (int i = 0; i < arr.length; i++) {
      System.out.println(arr[i]+ " , "+ (int) (arr[i] & 0XFF) );
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
    
        //byte[] Dati = new byte[]{0X0,0X0D,0X07,0X0};
        byte[] Dati = new byte[4];
            
        for (i = 0; i < c.length; i++) {
          CheckSum = CheckSum + (int) c[i];
          buffer += (char) c[i];
        }
        
        for (i = 0; i < 4; i++) {
          Dati[3 - i] = (byte) (Value >> 8 * i);
          //System.out.println("-.--.");
          //System.out.println((byte) Dati[3-i]);
        }
    
        for (i = 0; i < 4; i++) {
          if ((Dati[i] == 16) || (Dati[i] == 13))
            buffer += 0X10; //sedici
          CheckSum += (int) Dati[i];
          buffer += (char) (Dati[i]);
        }
    
        CheckSumFinal = mod(CheckSum, 256);
        //if ((CheckSum == 13) || (CheckSum == 16))
        //  CheckSumFinal += 128;
        System.out.println((CheckSumFinal & 0XFF));
        buffer += (char) (CheckSumFinal & 0XFF);
        buffer += '\r';
        System.out.println(buffer);
        
        char[] caratteri = buffer.toCharArray();
        System.out.println("lunghezza char[] da stringa: ");
        System.out.println(caratteri.length);

        int[] interi = new int[caratteri.length];
        for (i = 0; i < caratteri.length; i++){
            interi[i] = (int) (caratteri[i] & 0XFF);
        }

        System.out.println("char[] completo: ");
        PrintArray(caratteri);

        System.out.println("int[] completo: ");
        PrintArray(interi);



        byte[] finalBuffer = String.valueOf(buffer).getBytes();

        PrintArray(finalBuffer);
        //this.serialCommand = finalBuffer;
    
        //communication.Write(finalBuffer);
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
      
    
    public static void main(String[] argV) {
        System.out.println();

        int value = 42;
        System.out.println("valore inviato: "+value);
        SerialWrite(sbld("SXWT"), value);

        System.out.println();
        System.out.println("Checksum: ");
        int sommaComando = 83+88+87+84;
        System.out.println(mod((sommaComando + 42), 256));



    }
}
