package com.cignacmb.member.mis.inteceptors;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cignacmb.member.mis.service.ActivityProductService;

/**
 * 系统日志方法描述
 * @author Edison.zou
 * @date 
 * @version V1.0 
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {

    /** 标题 */
    String title() default "";

    /** 动作的名称
     *  为了后续方便扩展目前update代表更新，""代表其他操作
     * 
     */
    String action() default "";

    /** 是否保存请求的参数 */
    boolean isSaveRequestData() default false;

    /** 渠道 */
    String channel() default "web";
    
    /**调用的方法
     * 更新记录时需要查询更新前的记录，所以需要将查询的方法传入以便调用查询，
     * 这里设置成数组是因为有的操作涉及到多表更新，所以会需要查询不同表的不同记录
     * */
    String[] method() default "";
    
    /**查询的类名
     * 这里随便传入一个继承CommonService的类Class就可以，实质是调用了该类方法的queryOneByObject方法
     * 不直接用CommonService.class的原因是该接口在Web上下文事没有实例的
     * 
     * */
    Class<?> clazz() default ActivityProductService.class;
    
    /**查询条件
     * 于method中一一对应，length一定需要相等，主要是用于查询记录的条件
     * method={"id","productCode"}
     * 例如 select * from tableName where id=?
     * method[0]="id"，会去对应的Map中查找Key为id的键值对，然后将value赋值给上述sql语句的id值
     * */
    String[] condition() default "";
    
    /***
     * 需要显示的列(默认显示全部的列)只用于添加，删除操作
     */
    String[] showLines() default "";
}
