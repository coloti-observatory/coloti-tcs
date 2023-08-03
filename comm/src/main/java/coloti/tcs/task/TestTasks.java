package coloti.tcs.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



public class TestTasks{

    private TaskListener list = new TaskListener() {

        @Override
        public void onStart(Object in) {
            System.out.println("Task Started");

        }

        @Override
        public void onWorking(Object... v) {
            System.out.println("Task Working");

        }

        @Override
        public void onDone(Object out) {
            System.out.println("Task Done");

        }

        @Override
        public void onError(Object output) {
            // TODO Auto-generated method stub
            
        }

    };

    public TestTasks() {

    }

    public void Sleep(final int millisecondsTime) { // VERIFICATO 
        try {
          TimeUnit.MILLISECONDS.sleep(millisecondsTime);
        } catch (final InterruptedException e) {
          e.printStackTrace();
        }
    }

    public static void main(String[] args){
        /*Task<Integer> ab = libr.mioTask;
        TaskExecutor<Integer> te = new TaskExecutor<>();
        TaskExecutor<Double> ted = new TaskExecutor<>();

        for (int i = 0; i < 10; i++) {
            System.out.println("Cycle:"+i);
            ab.setVal(10+i);
            System.out.println("a:" + te.runAndWaitTask(ab, 2,app.list));
            System.out.println("b:" + ted.runAndWaitTask(libr.mioTask1, 2,app.list));
        }
        te.shutdown();
        ted.shutdown();*/

        TestTasks app = new TestTasks();
        TaskLibraryStructure libr = new TaskLibraryStructure();
        
        TaskExecutor<Void> tea = new TaskExecutor<>();
        try {
            tea.execAndWaitTask(libr.mioTask2, 20);
        } catch (ExecutionException | TimeoutException e) {
            libr.mioTask2.interrupt();
            System.out.println("Errore "+e.getMessage());
          
        }//app.list);
        //libr.mioTask2.interrupt();
        System.out.println("a:" +libr.mioTask2.getCurrentVal());
        //tea.shutdown();

        for(int i = 0; i<10; i++){
            System.out.println("-----");
            app.Sleep (500);
        }

        tea.shutdown();

        /* 

        TaskExecutor<Void> tea2 = new TaskExecutor<>();
        tea2.runTask(libr.taskPaolo, app.list);
        TimeUnit.SECONDS.sleep(10);
        libr.taskPaolo.interrupt();
        System.out.println("a:" +libr.taskPaolo.getCurrentVal());
        tea2.shutdown();





        TimeUnit.SECONDS.sleep(3);

        TaskExecutor<Void> tea3 = new TaskExecutor<>();
        tea3.runTask(libr.taskPaolo, app.list);
        TimeUnit.SECONDS.sleep(10);
        libr.taskPaolo.interrupt();
        System.out.println("a:" +libr.taskPaolo.getCurrentVal());
        tea3.shutdown();

        */





















        /*
        TaskExecutor<Void> tea = new TaskExecutor<>();
        tea.runTask(libr.taskPaolo, app.list);
        TimeUnit.SECONDS.sleep(10);
        libr.taskPaolo.interrupt();
        app.Sleep(2000);
        System.out.println("a.... :" +libr.taskPaolo.getCurrentVal());
        tea.shutdown();
        

        TaskExecutor<Void> tea = new TaskExecutor<>();
        tea.runAndWaitTask(libr.taskPaolo, 20, app.list);        
        app.Sleep(2000);
        System.out.println("a.... :" +libr.taskPaolo.getCurrentVal());
        tea.shutdown(); 

        */




        /* 
        TestTasks app = new TestTasks();

        Background bFunc = new Background();
        Thread backgroundThread = new Thread(bFunc);
        backgroundThread.start();

        //app.Sleep(200);

        for(int i = 0; i<10; i++){
            System.out.println("All work and no play makes Jack a dull boy.");
            app.Sleep (500);
        }

        int risultato = 0;
        while(bFunc.IsRunning()){

            if (!bFunc.IsRunning())
                risultato = bFunc.GetBackgroundResult();
        }

        if (!bFunc.IsRunning())
                risultato = bFunc.GetBackgroundResult();

        System.out.println("  ...  ");
        System.out.println("Confermiamo un risultato di "+risultato);
        System.out.println("end");

        //*/


    }
}
