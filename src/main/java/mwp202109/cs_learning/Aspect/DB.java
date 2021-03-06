package mwp202109.cs_learning.Aspect;


import mwp202109.cs_learning.config.mysql.DataSourceType;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DB
{
    /**
     * 切换数据源名称
     */
     DataSourceType value() default DataSourceType.DB1;
}
