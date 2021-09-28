package mwp202109.cs_learning.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

@Aspect
/**
 * 把页面注入IOC容器中
 */
@Component
public class InterfaceAop {

    @Pointcut("execution(public * mwp202109.cs_learning.controller..*.*(..))")
    public void aopWebLog() {
    }

    /**
     * @param joinPoint
     */
    @Before("aopWebLog()")
    public void doBefore(JoinPoint joinPoint) {

    }

    @AfterReturning(pointcut = "aopWebLog()")
    public void doAfterReturning() {
//        Logger.getGlobal().info("doAfterReturning()");
    }

    @After("aopWebLog()")
    public void doAfter() {
//        Logger.getGlobal().info("doAfter()");
    }

    /**
     * 方法抛出异常退出时，执行的通知
     *
     * @param e
     */
    @AfterThrowing(pointcut = "aopWebLog()", throwing = "e")
    public void doAfterThrowing(Exception e) {
        Logger.getGlobal().info("doAfterThrowing = " + e);
    }

    /**
     * Around方法体报错则不会进入程序
     *
     * @param proceedingJoinPoint
     * @throws Throwable
     */
    @Around("aopWebLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        Logger.getGlobal().info("Around start ......");
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
//        Logger.getGlobal().info("Around end ......");
        long end = System.currentTimeMillis();
        if (proceedingJoinPoint.getArgs().length > 0) {
            Logger.getGlobal().info("调用的接口名：" + proceedingJoinPoint.getSignature().getName() + " 接口参数: " + Arrays.asList(proceedingJoinPoint.getArgs()).toString() + " 调用时间：" + new Date() + " 耗时：" + (end - start));
        } else {
            Logger.getGlobal().info("调用的接口名：" + proceedingJoinPoint.getSignature().getName() + " 调用时间：" + new Date() + " 耗时：" + (end - start));
        }
        return result;
    }
}
