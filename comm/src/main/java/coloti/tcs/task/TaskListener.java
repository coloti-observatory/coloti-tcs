package coloti.tcs.task;

public interface TaskListener {
    public void onStart(Object input);
    public void onWorking(Object... v);
    public void onDone(Object output);
    public void onError(Object output);
}
