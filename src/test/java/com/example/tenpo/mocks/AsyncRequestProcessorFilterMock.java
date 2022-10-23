package com.example.tenpo.mocks;

import com.example.tenpo.filters.AsyncRequestProcessorFilter;
import com.example.tenpo.repository.RequestLogRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

@Component
@Primary
public class AsyncRequestProcessorFilterMock extends AsyncRequestProcessorFilter {

    /**
     * Override del shouldNotFilter para que funcione en tests.
     */

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return false;
    }

    public AsyncRequestProcessorFilterMock(RequestLogRepository requestLogRepository, ExecutorService executorService) {
        super(requestLogRepository, executorService);
    }
}
