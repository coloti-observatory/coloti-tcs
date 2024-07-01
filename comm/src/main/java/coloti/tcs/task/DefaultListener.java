package coloti.tcs.task;

import java.lang.reflect.Field;

import coloti.tcs.objclasses.TELESCOPIO;

public class DefaultListener implements TaskListener{

    private long tStart = 0L;
    private long tStop = 0L;
    private String commandName = "";
    private Field field;
    private TELESCOPIO TEL;


    public DefaultListener(TELESCOPIO tel) {
        this.TEL = tel;
    }
    


    private void setField(String name, String state, long start, long stop, String err){
        try {
            field = TELESCOPIO.class.getDeclaredField(name);
            field.setAccessible(true);
            //field = TEL.getClass().getField(name);
            String fieldstring = "commandname: "+name+"; busy: "+state+"; tstart: "+start+"; tstop: "+stop+"; error: "+err;
            field.set(TEL, fieldstring);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { // | IllegalAccessException
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(final Object in) {
        if (in != null){
            commandName = String.valueOf(in);
            System.out.println("Task Started");
            tStart = System.currentTimeMillis();
            setField(commandName, "TRUE", tStart, 0L, "");
        }
    }

    @Override
    public void onWorking(final Object... v) {
        //System.out.println("Task Working");
        tStop = System.currentTimeMillis();
        setField(commandName, "TRUE", tStart, tStop, "none");
    }

    @Override
    public void onDone(final Object out) {
        System.out.println("Task Done");
        tStop = System.currentTimeMillis();
        setField(commandName, "FALSE", tStart, tStop, "none");
    }

    @Override
    public void onError(final Object output) {
        tStop = System.currentTimeMillis();
        setField(commandName, "FALSE", tStart, tStop, String.valueOf(output));
    }

    
}
