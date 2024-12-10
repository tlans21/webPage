package HomePage.config.aspect;

import HomePage.exception.JoinRequestProcessingTimeoutException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Aspect
@Component
public class AsyncTimeoutAspect {

    @Around("execution(* HomePage.controller.join.JoinRestController.checkUsername(..))")
    public Object checkTimeout(ProceedingJoinPoint joinPoint) throws JoinRequestProcessingTimeoutException, Throwable {
        long startTime = System.currentTimeMillis();
        long timeout = 5000; // 5초 타임아웃

        Future<?> future = CompletableFuture.supplyAsync(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new JoinRequestProcessingTimeoutException("요청 처리 시간이 초과되었습니다.");
        }
    }
}
