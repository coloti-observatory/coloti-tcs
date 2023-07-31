package coloti.tcs.temporaneo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskExecutor<T> {
    private ExecutorService executorService;
    private Logger logger = Logger.getAnonymousLogger();
    

    public TaskExecutor() {
        // It's a best practice utilize the maximum core capacity
        // until there is a valid reason to use the lesser value
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }


    private Future<T> submitCallable(Task<T> task) {
        Future<T> future = executorService.submit(task);
        return future;
    }

    

    public T execAndWaitTask(Task<T> task, int timeout) throws ExecutionException, TimeoutException {
        Future<T> future = submitCallable(task);
        try {
            return future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ExecutionException("You task has been interrupted", e);
        } catch (ExecutionException e) {
            throw new ExecutionException("You task has been completed exceptionally", e);
        } catch (TimeoutException e) {
            throw new TimeoutException("timeout reached");
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public T runAndWaitTask(Task<T> task, int timeout, TaskListener list) throws ExecutionException, TimeoutException {
        task.setTaskListener(list);
        T a = execAndWaitTask(task, timeout);
        return a;
    }
    
    public void runTask(Task<T> task,TaskListener list) throws ExecutionException, TimeoutException {
        task.setTaskListener(list);
        execTask(task);
    }

    private void execTask(Task<T> task) {
        Future<T> future = submitCallable(task);
    }
}