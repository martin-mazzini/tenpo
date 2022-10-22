package com.example.tenpo.mocks;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;

@Component
@Primary
public class SameThreadExecutorService extends ThreadPoolExecutor {

    /**
     * Thread pool que ejecuta las tasks en el mismo hilo,
     * para tener resultados deterministas en los tests
     */

    private final CountDownLatch signal = new CountDownLatch(1);

    public SameThreadExecutorService() {
        super(1, 1, 0, TimeUnit.DAYS, new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override public void shutdown() {
        super.shutdown();
        signal.countDown();
    }

    public static ExecutorService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        static ExecutorService instance = createInstance();
    }

    private static ExecutorService createInstance() {
        final SameThreadExecutorService instance
                = new SameThreadExecutorService();

        // The executor has one worker thread. Give it a Runnable that waits
        // until the executor service is shut down.
        // All other submitted tasks will use the RejectedExecutionHandler
        // which runs tasks using the  caller's thread.
        instance.submit(new Runnable() {
            @Override public void run() {
                boolean interrupted = false;
                try {
                    while (true) {
                        try {
                            instance.signal.await();
                            break;
                        } catch (InterruptedException e) {
                            interrupted = true;
                        }
                    }
                } finally {
                    if (interrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }});
        return Executors.unconfigurableExecutorService(instance);
    }
}
