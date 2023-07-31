package coloti.tcs.temporaneo;

import java.util.concurrent.Callable;

public interface Task<T> extends Callable<T>{
    public void setVal(T a);
    public void interrupt();
    public void setTaskListener(TaskListener list);
    public String getCurrentVal();
}