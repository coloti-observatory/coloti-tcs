package coloti.tcs.temporaneo;

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

    

    public static void main(String[] args) throws ExecutionException, TimeoutException, InterruptedException {
        TestTasks app = new TestTasks();
        TaskLibrary libr = new TaskLibrary();
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
        TaskExecutor<Void> tea = new TaskExecutor<>();
        tea.runTask(libr.taskPaolo, app.list);
        TimeUnit.SECONDS.sleep(10);
        libr.taskPaolo.interrupt();
        System.out.println("a:" +libr.taskPaolo.getCurrentVal());
        tea.shutdown();
    }
}
