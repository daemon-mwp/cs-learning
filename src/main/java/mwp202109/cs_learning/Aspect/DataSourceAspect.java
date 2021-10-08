package mwp202109.cs_learning.Aspect;

import mwp202109.cs_learning.config.mysql.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
//配置加载顺序
@Order(1)
@Component
public class DataSourceAspect {

    @Pointcut("@annotation(mwp202109.cs_learning.Aspect.DB)")
    public void doPointCut(){}


    @Around("doPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DB dataSource = getDataSource(point);
        if (!Objects.isNull(dataSource)){
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }

        try {
            return point.proceed();
        }
        finally {
            // 在执行方法之后 销毁数据源
            DynamicDataSourceContextHolder.clearDataSourceType();
        }

    }



    /**
     * 获取@DB注解
     */
    public DB getDataSource(ProceedingJoinPoint point){

        //获得当前访问的class
        Class<? extends Object> className = point.getTarget().getClass();

        // 判断是否存在@DateBase注解
        if (className.isAnnotationPresent(DB.class)) {
            //获取注解
            return className.getAnnotation(DB.class);
        }

        Method method = ((MethodSignature)point.getSignature()).getMethod();
        return method.getAnnotation(DB.class);

    }
}
