package coloti.tcs.task;

import java.util.concurrent.TimeUnit;

public class Background implements Runnable {

    @Override
    public void run() {
        
        backgroundFunction();
    }

    public void Sleep(final int millisecondsTime) { // VERIFICATO 
        try {
          TimeUnit.MILLISECONDS.sleep(millisecondsTime);
        } catch (final InterruptedException e) {
          e.printStackTrace();
        }
    }

    public int result = 6;
    public boolean checkRun = false;

    private int backgroundFunction() {
        int i = 0;
        this.checkRun = true;
        for (i = 0; i<32; i++){
            System.out.println("--> "+i);
            if (i%3 == 0)
                System.out.println("Background function is running!");
            Sleep(500);
        }
        this.result = i+100;
        this.checkRun = false;
        return result;
    }

    public int GetBackgroundResult(){
        System.out.println("Il risultato è: "+result);
        return result;
    }

    public boolean IsRunning(){
        return checkRun;
    }


    
}
