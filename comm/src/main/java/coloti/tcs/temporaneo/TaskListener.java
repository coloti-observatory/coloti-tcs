package coloti.tcs.temporaneo;

public interface TaskListener {
    public void onStart(Object input);
    public void onWorking(Object... v);
    public void onDone(Object output);
    public void onError(Object output);
}
