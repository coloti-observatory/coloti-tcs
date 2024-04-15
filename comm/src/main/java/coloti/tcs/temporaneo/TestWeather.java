package coloti.tcs.temporaneo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import coloti.tcs.CommClass;
import coloti.tcs.weather.WeatherData;

public class TestWeather {

    //public CommClass communication;

    WeatherData wdata = new WeatherData("/dev/ttyUSB0");

    public TestWeather() {
    }

    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public void runTest() {
        service.scheduleAtFixedRate(() -> {
            try {
                getData();
            } catch (Exception e) {
            }
        }, 1L, 10L, TimeUnit.SECONDS);
    }

    public void getData() {
        // wdata.printData(wdata.ExtractAllData());
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

        // getInsideTemperature(dataFromWeatherStation("LOOP 1\r"));
        // oppure ...
        // byte[] answer = dataFromWeatherStation("LOOP 1\r");
        // getOutSideTemperature(answer)
        // getOutsideHumidity(answer)

    }

    public void stop() {
        service.shutdown();
    }

    public static void main(final String[] a){ 

        TestWeather TW = new TestWeather();

        TW.runTest();




    }
}