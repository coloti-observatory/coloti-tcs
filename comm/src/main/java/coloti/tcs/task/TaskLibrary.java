package coloti.tcs.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TaskLibrary {

    private TaskListener defaultListener = new TaskListener() {

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

    private Map<String, Task<?>> taskList = new ConcurrentHashMap<String, Task<?>>();

    public TaskLibrary() {
    }

    public void addTask(String name, Task<?> task) {

        taskList.put(name, task);
    }

    public Task<?> getTask(String name) {
        return taskList.get(name);
    }

    public TaskListener getDefaultListener() {
        return defaultListener;
    }

    public Map<String, Task<?>> getTaskList() {
        return taskList;
    }

    public Task<Integer> mioTask = new Task<Integer>() {

        private int sum = 0;
        private TaskListener listener;

        @Override
        public void setVal(Integer b) {
            this.sum = b;
        }

        @Override
        public Integer call() throws Exception {
            listener.onStart(null);
            // System.out.println("I'm a Integer callable");
            listener.onWorking(null);
            TimeUnit.SECONDS.sleep(1);
            listener.onDone(null);
            return 10 + sum;
        }

        @Override
        public void interrupt() {
        }

        @Override
        public void setTaskListener(TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
            // TODO Auto-generated method stub
            return null;
        }
    };

    public Task<Double> mioTask1 = new Task<Double>() {

        private TaskListener listener = defaultListener;

        @Override
        public Double call() throws Exception {
            listener.onStart(null);
            // System.out.println("I'm a double callable");
            listener.onWorking(null);
            TimeUnit.SECONDS.sleep(1);
            listener.onDone(null);
            return 100.0;
        }

        @Override
        public void setVal(Double a) {
        }

        @Override
        public void interrupt() {

        }

        @Override
        public void setTaskListener(TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
            return null;
        }
    };

    public Task<Void> mioTask2 = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener = defaultListener;
        private double curVal=10;
        private Void v;
        @Override
        public Void call() throws Exception {
            
            listener.onStart(null);
            while (isInterrupted) {
                //System.out.println("I'm a double callable");
                System.out.println("mioTask2 is running");
                //listener.onWorking(null);
                TimeUnit.SECONDS.sleep(1);
            }
            curVal=100;
            return v;
        }

        @Override
        public void setVal(Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            listener.onDone(null);
        }

        @Override
        public void setTaskListener(TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
            return String.valueOf(curVal);
        }
    };



    public void Sleep(final int millisecondsTime) { // VERIFICATO 
        try {
          TimeUnit.MILLISECONDS.sleep(millisecondsTime);
        } catch (final InterruptedException e) {
          e.printStackTrace();
        }
    }


    public int numerino = 10;

    public int MisuraProva(){
        this.numerino += 1;
        Sleep(500);
        return numerino;
    }





    public Task<Void> taskPaolo = new Task<Void>() {
        boolean isInterrupted = true;
        private TaskListener listener = defaultListener;
        private double curVal=10;
        private Void v;
        @Override
        public Void call() throws Exception {
            
            listener.onStart(null);
            while (isInterrupted) {
                numerino = MisuraProva();
                System.out.println("taskPaolo is running, updating n: "+numerino);
                //this.curVal = numerino;
                //System.out.println("a:" +getCurrentVal());
            }
            //this.curVal = numerino;
            //System.out.println("A:" +getCurrentVal());
            return v;
        }

        @Override
        public void setVal(Void v) {
        }

        @Override
        public void interrupt() {
            isInterrupted = false;
            listener.onDone(null);
        }

        @Override
        public void setTaskListener(TaskListener listen) {
            listener = listen;
        }

        @Override
        public String getCurrentVal() {
            return String.valueOf(curVal);
        }
    };




}

