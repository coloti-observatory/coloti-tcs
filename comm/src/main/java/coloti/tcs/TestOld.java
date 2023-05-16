package coloti.tcs;

public class TestOld {
    
    private String CodiceFiscale = "CRSPLA";
    public int annoNascita;
    private int giornoNascita = 6;
    public int mese = 5;
    public String nomemese;
    //public String nome;


    public TestOld(int annoNascita){
        
        this.annoNascita = annoNascita;
        this.nomemese = " (maggio)";

    }


    public TestOld(int giornoNascita, int mese, int annoNascita){
        this.giornoNascita = giornoNascita;
        this.mese = mese;
        this.annoNascita = annoNascita;
        this.nomemese = NomedelMese(this.mese);
    }

    String NomedelMese(int mese){
        String nome = " ";
        if (mese == 4){
            nome = " (aprile)";
        }
        return nome;
    }


    void compleanno(){
        System.out.println("Sei nato il:");
        System.out.println(this.giornoNascita);
        System.out.print(this.mese);
        System.out.println(this.nomemese);
        System.out.println(this.annoNascita);

    }

    int getGiornoNascita(){
        int g = this.giornoNascita;
        return g;
    }

    String getCodiceFiscale(){
        String stringa;
        stringa = "Il tuo codice fiscale: "+this.CodiceFiscale;
        return stringa;
    }







    // prova da copiare nel main
    /*
    classeprova datacompleannoPaolo = new classeprova(1996);

    classeprova datacompleannoIrene = new classeprova(15, 4, 1994);

    datacompleannoPaolo.compleanno();

    System.out.println();
    
    System.out.println("Riguardo a Irene invece...");

    datacompleannoIrene.compleanno();

    System.out.println(datacompleannoIrene.annoNascita);
    System.out.println("invece usiamo get per il giorno");
    System.out.println(datacompleannoIrene.getGiornoNascita());

    System.out.println();

    System.out.println(datacompleannoPaolo.getCodiceFiscale());
    */

    /*

  boolean condizione = false;
  if (condizione){
    SerialPort[] ports = SerialPort.getCommPorts();
    System.out.println(ports.length);
    
    if (ports.length == 0){
      System.out.println("no serial port seems to be connected");
    }
    System.out.println(ports[0]);

    ports[0].setBaudRate(19200);
    ports[0].setDTR();
    ports[0].setComPortTimeouts(1, 2900, 2900);
    ports[0].setParity(0);
    ports[0].setNumStopBits(1);
    

    SerialPort p = ports[0];
    p.openPort(); //1024, 1024, 1024);
    //
    System.out.println(p.getBaudRate());
    System.out.println(p.getParity());
    System.out.println(p.getNumStopBits());
    System.out.println(p.getNumDataBits());
    System.out.println(p.getDeviceReadBufferSize());
  
    System.out.println(p.isOpen());
    //
    

    // /* 
    BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());
    OutputStream outputStream = p.getOutputStream();

    // COMANDI TEST
    commandTestDavis(outputStream, inputStream);
    commandTestDavis(outputStream, inputStream);
    commandTestDavis(outputStream, inputStream);

    // TERZO COMANDO
    TIMEOUT(3000);
    //InputStream in = p.getInputStream();<
    System.out.println("Terza parte");
    try { 
      outputStream.write(String.valueOf("LOOP 1\r").getBytes());
      outputStream.flush();
      System.out.println("Risposta al terzo comando: ");
      //byte[] risposta3 = new byte[100];
      System.out.println("numro caratteri letti:" );
      byte[] risposta3 = inputStream.readNBytes(100);
      WeatherData wd =new WeatherData();
      wd.setNewDataFromDevice(risposta3);
      wd.printData();

      //      System.out.println(letti);
      System.out.println(risposta3.length);
      for (int i = 0; i<100; i++){
        System.out.print((int) risposta3[i]+" ");

      }
      System.out.println(" ");

      int temp=0;
      
      int temp1=temp+Integer.valueOf(risposta3[10]).shortValue();
      int temp2=temp+Integer.valueOf(risposta3[11]).shortValue();
      System.out.println("Temp 1:"+((temp1&0xFF)));
      System.out.println("Temp 2:"+((temp2<<8)));
      System.out.println("somma :"+((temp2<<8)+(temp1&0xFF)));
      //System.out.println("Temp:"+((int)temp));

      System.out.println("in bits: ");
      System.out.println(Integer.toBinaryString(temp1&0xFF));
      System.out.println(Integer.toBinaryString(temp2));
      
      //int risposta3 = 0;
      //for (int i = 0; i < 101; i++){
      //risposta3 = inputStream.read();
      //System.out.println(risposta3);
      //}
    } catch (Exception e) {
      e.printStackTrace();
    }




    p.closePort();


    }
"BarTrend", new int[] {3,1, 4});
"Barometer", new int[] {7,2, 3});
"insideTemperature", new int[] {9,2, 1});
"insideHumidity", new int[] {11,1, 2});
"outsideTemperature", new int[] {12,2, 5});
"windSpeed", new int[] {14,1, 6});
"averageWindSpeed", new int[] {15,1, 7});
"windDirection", new int[] {16,2, 8});
"extraTemperature", new int[] {18,7, 100});
"soilTemperature", new int[] {25,4, 100});
"leafTemperature", new int[] {29,4, 100});
"outsideHumidity", new int[] {33,1, 9});
"extraHumidity", new int[] {34,7, 100});
"rainRate", new int[] {41,2, 10});
"indexUV", new int[] {43,1, 100});
"solarRadiation", new int[] {44, 2, 100});
"stormRain", new int[] {46,2, 100});
"startStorm", new int[] {48,2, 100});
"dayRain", new int[] {50,2, 100});
"monthRain", new int[] {52,2, 100});
"yearRain", new int[] {54,2, 100});
"dayET", new int[] {56,2, 100});
"monthET", new int[] {58,2, 100});
"yearET", new int[] {60,2, 100});
"soilMoistures", new int[] {62,4, 100});
"leafWetnesses", new int[] {66,4, 100});
"insideAlarms", new int[] {70,1, 12});
"rainAlarms", new int[] {71,1, 13});
"outsideAlarms", new int[] {72,2, 14});
"exTempHumAlarms", new int[] {74,8, 100});
"soilLeafAlarms", new int[] {82,4, 100});
"transmitterBatteryStatus", new int[] {86,1, 100});
"consoleBatteryVoltage", new int[] {87,2, 100});
"forecastIcons", new int[] {89,1, 11});
"forecastRuleNumber", new int[] {90,1, 100});
"timeofSunrise", new int[] {91,2, 100});
"timeofSunset", new int[] {93,2, 100});
        }

    public class finalResults{

        public String test; //0
        public double insideTempearture; //1
        public int insideHumidity; //2
        public double Barometer; //3
        public String BarTrend; //4
        public double outsideTemperature; //5
        public int windSpeed; //6
        public int averageWindSpeed; //7
        public int windDirection; //8
        public int outsideHumidity; //9
        public double rainRate; //10
        public String forecastIcons; //11
        public int[] insideAlarms; //12
        public int[] rainAlarms; //13
        public int[] outsideAlarms; //14
      
        
        public finalResults(Object object[]){
            this.test = (String) object[0];
            this.insideTempearture = (double) object[1];
            this.insideHumidity = (int) object[2];
            this.Barometer = (double) object[3];
            this.BarTrend = (String) object[4];
            this.outsideTemperature = (double) object[5];
            this.windSpeed = (int) object[6];
            this.averageWindSpeed = (int) object[7];
            this.windDirection = (int) object[8];
            this.outsideHumidity = (int) object[9];
            this.rainRate = (double) object[10];
            this.forecastIcons = (String) object[11];
            this.insideAlarms = (int[]) object[12];
            this.rainAlarms = (int[]) object[13];
            this.outsideAlarms = (int[]) object[14];

        }


    }


  }
  public class Field{
        public String name;
        private int Offset;
        private int Size;
        private int objectIndex;

        // costruttore
        public Field(String name){
            this.name = name;
            this.Offset = mapWeather.get(name)[0];
            this.Size = mapWeather.get(name)[1];
            this.objectIndex = mapWeather.get(name)[2];
        }

        public int getOffset(){
            return this.Offset;
        }
        
        public int getSize(){
            return this.Size;
        }

        public int getObjectIndex(){
            return this.objectIndex;
        }

    }
  

  public static void TIMEOUT(int SleepTime){
    try {TimeUnit.MILLISECONDS.sleep(SleepTime);} catch (InterruptedException e1) {e1.printStackTrace();}
  }
  
  public static void commandTestDavis(OutputStream outputStream, BufferedInputStream inputStream){
  TIMEOUT(3000);
      try { 
        outputStream.write(String.valueOf("TEST\r").getBytes());  // "LPS 1 1\r"
        System.out.print("Risposta al comando di TEST: ");
        byte[] risposta = inputStream.readNBytes(8);
        String stringarisposta = new String(risposta, "UTF-8");
        char[] caratteri = stringarisposta.toCharArray();
        System.out.print(Arrays.toString(caratteri));
        System.out.println(" ");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    */

}
