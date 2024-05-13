package coloti.tcs.weather;
import java.io.IOException;
import java.util.*;
//import java.util.ResourceBundle.Control;

import coloti.tcs.CommClass;
import coloti.tcs.temporaneo.TestWeather;

public class WeatherData {
    //private long timestamp;

    private CommClass communication;
    public boolean connected;

    public WeatherData() {
        try{
            this.communication = new CommClass();
            this.connected = communication.Open(19200);
            trytest();
        } catch (ArrayIndexOutOfBoundsException e) {
            //e.printStackTrace();
            this.connected = false;
            //System.out.println("not connected");
        }
    }

    public WeatherData(String PortName) {
        this.communication = new CommClass(PortName);
        //System.out.println("Porta identificata");
        communication.Open(19200);
        //System.out.println("Comunicazione Aperta");
        trytest();
        //System.out.println("test funzionante");
    }


    public Map <String, int[]> mapWeather = new HashMap <>(){
        {
            put("BarTrend", new int[] {3,1, 4});
            put("Barometer", new int[] {7,2, 3});
            put("insideTemperature", new int[] {9,2, 1});
            put("insideHumidity", new int[] {11,1, 2});
            put("outsideTemperature", new int[] {12,2, 5});
            put("windSpeed", new int[] {14,1, 6});
            put("averageWindSpeed", new int[] {15,1, 7});
            put("windDirection", new int[] {16,2, 8});
            put("extraTemperature", new int[] {18,7, 100});
            put("soilTemperature", new int[] {25,4, 100});
            put("leafTemperature", new int[] {29,4, 100});

            put("dewPoint", new int[] {30,2,111});

            put("outsideHumidity", new int[] {33,1, 9});
            put("extraHumidity", new int[] {34,7, 100});
            put("rainRate", new int[] {41,2, 10});
            put("indexUV", new int[] {43,1, 100});
            put("solarRadiation", new int[] {44, 2, 100});
            put("stormRain", new int[] {46,2, 100});
            put("startStorm", new int[] {48,2, 100});
            put("dayRain", new int[] {50,2, 100});
            put("monthRain", new int[] {52,2, 100});
            put("yearRain", new int[] {54,2, 100});
            put("dayET", new int[] {56,2, 100});
            put("monthET", new int[] {58,2, 100});
            put("yearET", new int[] {60,2, 100});
            put("soilMoistures", new int[] {62,4, 100});
            put("leafWetnesses", new int[] {66,4, 100});
            put("insideAlarms", new int[] {70,1, 12});
            put("rainAlarms", new int[] {71,1, 13});
            put("outsideAlarms", new int[] {72,2, 14});
            put("exTempHumAlarms", new int[] {74,8, 100});
            put("soilLeafAlarms", new int[] {82,4, 100});
            put("transmitterBatteryStatus", new int[] {86,1, 100});
            put("consoleBatteryVoltage", new int[] {87,2, 100});
            put("forecastIcons", new int[] {89,1, 11});
            put("forecastRuleNumber", new int[] {90,1, 100});
            put("timeofSunrise", new int[] {91,2, 100});
            put("timeofSunset", new int[] {93,2, 100});
        }
    };


    public int getOffSet(String name){
        int OffSet = mapWeather.get(name)[0];
        return OffSet;
    }

    public int getSize(String name){
        int Size = mapWeather.get(name)[1];
        return Size;
    }

    public int getObjectIndex(String name){
        int ObjectIndex = mapWeather.get(name)[2];
        return ObjectIndex;
    }


    public Object[] ExtractAllData(){

        Object object[] = new Object[15]; 

        byte[] answer = dataFromWeatherStation("LOOP 1\r");
        
        object[0] = "Weather Data";
        object[1] = getInsideTemperature(answer); //double
        object[2] = getInsideHumidity(answer); //int
        object[3] = getBarometer(answer); //double
        object[4] = getBarTrend(answer); //String
        object[5] = getOutsideTemperature(answer); //double
        object[6] = getWindSpeed(answer); //int
        object[7] = getAverageWindSpeed(answer); //int
        object[8] = getWindDirection(answer); //int
        object[9] = getOutsideHumidity(answer); //int
        object[10] = getRainRate(answer); //double
        object[11] = getForecastIcons(answer); //String
        object[12] = getInsideAlarms(answer); //int[]
        object[13] = getRainAlarms(answer); //int[]
        object[14] = getOutsideAlarms(answer); //int[]

        
        //finalResults results = new finalResults(object);

        return object;
    }

    public void trytest(){
        boolean conditionPrint = true;
        String teststring = "TEST\n";
        byte[] instruction = String.valueOf(teststring).getBytes();
        for (int j = 0; j<3; j++){
            //System.out.print("\nrisposta numero "+j);
            communication.Write(instruction);
            //byte[] rispostaTest = communication.Read(8);
            byte[] rispostaTest = communication.Read();
            System.out.println(rispostaTest.length);
            for (int i = 0; i<rispostaTest.length; i++){
                System.out.println(rispostaTest[i]);

                teststring += (char) rispostaTest[i];
                if (conditionPrint){
                    System.out.print((char) rispostaTest[i]);
                    if (i == rispostaTest.length - 1){
                        System.out.print("\n "+teststring);
                    }
                }
            }
        }
        
    }

    /*  // EndOfOperations
    public void EndOfOperations(){
        for (int i = 0; i<6; i++){
            System.out.print("\n");
        }
        communication.Timeout(1000);
    }
    */

    public byte[] dataFromWeatherStation(String commandLoop){
        communication.Timeout(3000);
        System.out.println("Command for weather data");
        communication.Write(commandLoop);
        byte[] answer = communication.Read(99);
        communication.Timeout(3000);
        return answer;
    }

    //public void setNewDataFromDevice(byte[] buffer){ timestamp = System.currentTimeMillis();}

    public int getValueField(String name, byte[] answer){
        int[] values = new int[getSize(name)];
        int valueZero = 0;
        for (int i = 0; i < getSize(name); i++){
            values[i] = valueZero + Integer.valueOf(answer[getOffSet(name) + 1 + i]).shortValue();
        }
        int FinalValue = 0;
        for (int i = 0; i < getSize(name); i++){
            if (i == 0){
                FinalValue += values[i]&0xFF;
            }
            else{
                FinalValue += values[i]<<8*i;
            }
        }
    return FinalValue;
    }

    public double getInsideTemperature(byte[] answer) {
        String name = "insideTemperature";
        int tenthFar = getValueField(name, answer);
        double insideTemperature = ((tenthFar/10-32)*5)/9;
        return insideTemperature;
    }

    public int getInsideHumidity(byte[] answer) {
        String name = "insideHumidity";
        int insideHumidity = answer[getOffSet(name) + 1];
        return insideHumidity;
    }

    public double getBarometer(byte[] answer){
        String name = "Barometer";
        int inHg = getValueField(name, answer);
        double Barometer = ((double) inHg/1000) * 33.86389 ;
        return Barometer;
    }

    public String getBarTrend(byte[] answer){
        String name = "BarTrend";
        int numericTrend = getValueField(name, answer);
        String BarTrend = "";
        if (numericTrend == -60 || numericTrend == 196)
            BarTrend = "Falling Rapidly";
        else if (numericTrend == -20 || numericTrend == 236)
            BarTrend = "Falling Slowly";
        else if (numericTrend == 0)
            BarTrend = "Steady";
        else if (numericTrend == 20)
            BarTrend = "Rising Slowly";
        else if (numericTrend == 60)
            BarTrend = "Rising Rapidly";
        else if (numericTrend == 80)
            BarTrend = "No trend available ";
        return BarTrend;
    }

    public double getOutsideTemperature(byte[] answer) {
        String name = "outsideTemperature";
        int tenthFar = getValueField(name, answer);
        double outsideTemperature = ((tenthFar/10-32)*5)/9;
        return outsideTemperature;
    }

    public int getWindSpeed (byte[] answer){
        String name = "windSpeed";
        int windSpeed = answer[getOffSet(name) + 1]&0xFF;
        return windSpeed;
    }

    public int getAverageWindSpeed (byte[] answer){
        String name = "averageWindSpeed";
        int averageWindSpeed = answer[getOffSet(name) + 1]&0xFF;
        return averageWindSpeed;
    }

    public int getWindDirection(byte[] answer) {
        String name = "windDirection";
        int windDirection = getValueField(name, answer);
        return windDirection;
    }

    public int getOutsideHumidity(byte[] answer) {
        String name = "outsideHumidity";
        int outsideHumidity = answer[getOffSet(name) + 1]&0xFF;
        return outsideHumidity;
    }

    public double getRainRate(byte[] answer) {
        String name = "rainRate";
        double RainRate = getValueField(name, answer)/10;
        return RainRate;
    }

    public String getForecastIcons(byte[] answer){
        String name = "forecastIcons";
        int numericforecastIcons = answer[getOffSet(name) + 1];
        String forecastIcons = "";
        if (numericforecastIcons == 8 )
            forecastIcons = "Mostly Clear";
        else if (numericforecastIcons == 6 )
            forecastIcons = "Partly Cloudy";
        else if (numericforecastIcons == 2 )
            forecastIcons = "Mostly Cloudy";
        else if (numericforecastIcons == 3 )
            forecastIcons = "Mostly Cloudy, Rain (within 12 hours)";
        else if (numericforecastIcons == 18 )
            forecastIcons = "Mostly Cloudy, Snow (within 12 hours)";
        else if (numericforecastIcons == 19 )
            forecastIcons = "Mostly Cloudy, Rain or Snow (within 12 hours)";
        else if (numericforecastIcons == 7 )
            forecastIcons = "Partly Cloudy, Rain (within 12 hours)";
        else if (numericforecastIcons == 22 )
            forecastIcons = "Partly Cloudy, Snow (within 12 hours)";
        else if (numericforecastIcons == 23 )
            forecastIcons = "Partly Cloudy, Rain or Snow (within 12 hours)";
        else
            forecastIcons = "No information available";

        return forecastIcons;
    }
    
    public int[] getInsideAlarms(byte[] answer){
        String name = "insideAlarms";
        int alarms = answer[getOffSet(name) + 1];
        int[] insideAlarms = new int[8];
        for (int i = 0; i < 8; i++){
            insideAlarms[i] = (alarms>>i)&0x01;
        }
        return insideAlarms;
    } 

    public int[] getRainAlarms(byte[] answer){
        String name = "rainAlarms";
        int alarms = answer[getOffSet(name) + 1];
        int[] rainAlarms = new int[8];
        for (int i = 0; i < 8; i++){
            rainAlarms[i] = (alarms>>i)&0x01;
        }
        return rainAlarms;
    } 

    public int[] getOutsideAlarms(byte[] answer){
        String name = "outsideAlarms";
        int alarms = answer[getOffSet(name) + 1];
        int[] outsideAlarms = new int[8];
        for (int i = 0; i < 8; i++){
            outsideAlarms[i] = (alarms>>i)&0x01;
        }
        return outsideAlarms;
    } 

    /* 
    public double getDewPointAlarm(byte[] answer){
        int[] outAl = getOutsideAlarms(answer);
        double DewPointAlarm = 
        return DewPointAlarm
    }
    */

    ///* For Davis Vantage Pro 2 with LPS command 
    public double getDewPoint(byte[] answer){
        String name = "dewPoint";
        int dew = getValueField(name, answer);
        double dewPoint = ((dew/10-32)*5)/9;
        return dewPoint;
    }
    //*/

    public double[] getControlData(){
        double[] ControlData = new double[5];
        byte[] answer = dataFromWeatherStation("LOOP 1\r"); //"LPS 1 1\r"
        //byte[] answer = dataFromWeatherStation("LPS 1 1\r"); 

        ControlData[0] = (double) getOutsideTemperature(answer);
        ControlData[1] = (double) 0; //getDewPoint(answer);
        ControlData[2] = (double) getBarometer(answer);
        ControlData[3] = (double) getWindSpeed(answer);
        ControlData[4] = (double) getWindDirection(answer);
        
        return ControlData;
    }








    public void printData(Object results[]){
        //System.out.println(toString());
        System.out.println("Inside Temperature (Celsius): ");
        System.out.println(results[getObjectIndex("insideTemperature")]);
        
        System.out.println("Inside Humidity (%): ");
        System.out.println(results[getObjectIndex("insideHumidity")]);

        System.out.println("Barometer (mb): ");
        System.out.println(results[getObjectIndex("Barometer")]);

        System.out.println("Barometer Trend: ");
        System.out.println(results[getObjectIndex("BarTrend")]);

        System.out.println("Outside Temperature (Celsius): ");
        if ((double) results[getObjectIndex("outsideTemperature")] < 500)
            System.out.println(results[getObjectIndex("outsideTemperature")]);
        else
            System.out.println("No information available");

        System.out.println("Wind Speed: ");
        if ((int) results[getObjectIndex("windSpeed")]< 255)
            System.out.println(results[getObjectIndex("windSpeed")]);
        else
            System.out.println("No information available");

        System.out.println("Average wind Speed: ");
        if ((int) results[getObjectIndex("averageWindSpeed")] < 255)    
            System.out.println(results[getObjectIndex("averageWindSpeed")]);
        else
            System.out.println("No information available");

        System.out.println("Wind Direction (90 east, 180 south, 270 west, 360 north): ");
        if ((double) results[getObjectIndex("windDirection")] < 32767)
            System.out.println(results[getObjectIndex("windDirection")]);
        else
            System.out.println("No information available");

        System.out.println("Outside Humidity (%): ");
        if ((int) results[getObjectIndex("outsideHumidity")] < 255)
            System.out.println(results[getObjectIndex("outsideHumidity")]);
        else
            System.out.println("No information available");

        System.out.println("Rain rate: ");
        System.out.println(results[getObjectIndex("rainRate")]);

        System.out.println("Forecast Icons: ");
        System.out.println(results[getObjectIndex("forecastIcons")]);

        
        String[] textInsideAlarms = new String[] {"Falling bar trend", "Rising bar trend", "Low inside temperature", "High inside temperture", "Low inside humidity", "High inside Humidity", "Time alarm"};
        int[] insideAlarms = (int[]) results[getObjectIndex("insideAlarms")];
        for (int i = 0; i<7; i++){
            System.out.println(textInsideAlarms[i]+": "+insideAlarms[i]);
        }
        
        String[] textRainAlarms = new String[] {"High rain rate", "15 min rain, flash flood","24 hour rain", "storm total rain", "Daily ET"};
        int[] rainAlarms = (int[]) results[getObjectIndex("rainAlarms")]; 
        for (int i = 0; i<5; i++){
            System.out.println(textRainAlarms[i]+": "+rainAlarms[i]);
        }
        
        String[] textOutsideAlarms = new String[] {"Low outside temperature", "High outside temperature", "Wind speed", "10 min average speed", "Low dewpoint", "High dewpoint", "High heat", "Low wind chill"};
        int[] outsideAlarms = (int[]) results[getObjectIndex("outsideAlarms")];
        for (int i = 0; i<8; i++){
            System.out.println(textOutsideAlarms[i]+": "+outsideAlarms[i]);
        }




    }

    @Override
    public String toString() {
        //return "WeatherData [insideTemperature = " + ", timestamp = " + timestamp + "]";
        String toPrint = "\nWeatherData class, wdata.printData(wdata.ExtractAllData()) to get all data";
        //Object results = ExtractAllData();
        //printData(results);
        return toPrint;
    }

    public static void main(final String[] a){ 

        //WeatherData WD = new WeatherData("/dev/ttyUSB0");
        WeatherData WD = new WeatherData();


    }

}
