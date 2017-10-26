package com.cignacmb.member.mis.inteceptors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cignacmb.base.util.FastJsonUtil;
import com.cignacmb.member.mis.bean.SystemLogBean;
import com.cignacmb.member.mis.service.SystemLogService;
import com.cignacmb.member.mis.util.CommonUtil;
import com.cignacmb.member.mis.util.Json;
import com.cignacmb.member.mis.util.SpringContextUtil;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * 
 * @author Jason.liang
 * @date 2017年5月19日 下午12:27:00
 * @version V1.0 
 */
@Aspect
@Component
public class SystemLogAspect {
    
    private Log logger = LogFactory.getLog(SystemLogAspect.class);

    @Autowired
    private SystemLogService systemLogService;
    /**
     * 获取方法传入的参数map
     * 
     * @param joinPoint
     * @param systemLog
     * @return
     * @throws Throwable
     */
    Map<String,Object> anotherBuildSystemLogBean(ProceedingJoinPoint joinPoint,SystemLog systemLog) throws Throwable {
        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzName = clazz.getName();
        String methodName = joinPoint.getSignature().getName();
        // 获取参数名称
        String[] paramNames = getFieldsName(this.getClass(), clazzName, methodName);
        Object[] args = joinPoint.getArgs();
        Map<String,Object> paramValue=new HashMap<>();
        //放入方法名
        paramValue.put("methodName", methodName);
        for (int k = 0; k < args.length; k++) {
            Object arg = args[k];
            // 根据对象类型获取kv
            if (arg != null) {
                if (arg instanceof Integer
                        || arg instanceof Double
                        || arg instanceof Float
                        || arg instanceof Long
                        || arg instanceof Short
                        || arg instanceof Byte
                        || arg instanceof Boolean
                        || arg instanceof String
                        || arg instanceof Integer) {
                	if(arg instanceof Long)
                		paramValue.put(paramNames[k], Long.parseLong(arg.toString()));
                	else if(arg instanceof String){
                		try {
                			System.out.println(arg.toString());
                			List<Map<String, Object>> lm = FastJsonUtil.toListMap(arg.toString());
                			List<Object> il=new ArrayList<Object>();
                			for (Map<String, Object> map : lm) {
                				for(String s: map.keySet()){
                					if(map.get(s)!=null&&!map.get(s).toString().trim().equals("")){
                						if(s.equals("id")){
                							il.add(map.get(s));
                						}
                					}
                				}
                			}
                			paramValue.put("ids", il);
						} catch (Exception e) {
							// TODO: handle exception
	                		paramValue.put(paramNames[k], arg!=null?arg.toString():"");
						}
                	}
                	else{
                		paramValue.put(paramNames[k], arg!=null?arg.toString():"");
                	}
                } else if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse) ) {
                    try {
                    	//使用反射而不使用先转成json然后转成map的方法主要是为了确保类型的一致性
                        Class<?> cla=arg.getClass();
                        Field[] fields = cla.getDeclaredFields();
                        for(Field field: fields){
                        	field.setAccessible(true);
                        	paramValue.put(paramNames[k]+"."+field.getName(), field.get(arg)==null?"":field.get(arg));
                        }
                    } catch (Exception e) {
                    }
                    }
                }
            }
        return paramValue;
    }
    @Pointcut("@annotation(com.cignacmb.member.mis.inteceptors.SystemLog)")
    public void webLog() {
    }
    @SuppressWarnings("unchecked")
	@Around(value = "webLog()")
    public Object doBeforeUpdate(ProceedingJoinPoint joinPoint) throws Throwable{
    	// 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Object adminName = request.getAttribute("userName");

        // 获取记录日志
        SystemLog systemLog = getAnnotationSystemLog(joinPoint);
        //如果调用方法为默认值，则是普通查询的操作
        if(!systemLog.action().equals("update")){
			Object proceed = joinPoint.proceed();
			// 获取对象
			SystemLogBean systemLogBean = buildSystemLogBean(joinPoint);
			// 保存log
			systemLogService.saveLog(systemLogBean);
			return proceed;
		}else{
        //获取spring容器上下文，获取Bean(不用反射是因为反射之后自动注入属性失效)
        WebApplicationContext wac = (WebApplicationContext)SpringContextUtil.getApplicationContext(); 
        Object bean = wac.getBean(systemLog.clazz());
        //获取字节码文件
    	Class<?>  cls = bean.getClass();
    	//得到cls父类的queryOneByObject方法
    	Method method = cls.getSuperclass().getDeclaredMethod("queryOneByObject",new Class[]{String.class,Object.class});
        //获取拦截方法的参数键值对
    	Map<String, Object> pv=anotherBuildSystemLogBean(joinPoint, systemLog);
        //查询更新之前的值
        Map<String, Object> beforeValue=new LinkedHashMap<>();
        for(int i=0;i<systemLog.condition().length;i++){
        	if(systemLog.condition()[i].equals("ids")&&pv.containsKey("ids")){
        		try {
        			pv.put("ids", FastJsonUtil.toList(pv.get("ids").toString(), Long.class));
				} catch (Exception e) {
					// TODO: handle exception
					pv.put("ids", FastJsonUtil.toList(pv.get("ids").toString(), String.class));
				}
        		Method method1 = cls.getSuperclass().getDeclaredMethod("queryListByObject",new Class[]{String.class,Object.class});
        		List<?> invoke=null;
        		if(pv.get(systemLog.condition()[i])!=null){
        		invoke = (List<?>) method1.invoke(bean, systemLog.method()[i],
        				pv.get(systemLog.condition()[i]));
        		}
        		Map<String,Object> idsMap=new HashMap<String,Object>();
        		idsMap=getVauleByReflect(invoke);
        		if(!(idsMap==null||idsMap.size()<=0))
        			beforeValue.put("ids", idsMap);
        	}else{
        		if(pv.get(systemLog.condition()[i])!=null&&
        				!pv.get(systemLog.condition()[i]).toString().equals("")){
        		
        		if(Json.toMap(
    					Json.toJson(
    							method.invoke(bean, systemLog.method()[i], 
    									pv.get(systemLog.condition()[i]))))!=null){
        	beforeValue.put(systemLog.condition()[i], 
        			Json.toMap(
					Json.toJson(
							method.invoke(bean, systemLog.method()[i], 
									pv.get(systemLog.condition()[i]))))
        			);
        		}
        		}
        }
        }
        Object proceed=null;
        //执行拦截的方法
        	proceed=joinPoint.proceed();
        	//判断beforeValue是否为空(添加)
        	if(beforeValue==null||beforeValue.size()<=0){
        		pv=anotherBuildSystemLogBean(joinPoint, systemLog);
        	}
      //查询更新之后的值
        Map<String, Object> afterValue=new LinkedHashMap<>();
        for(int j=0;j<systemLog.condition().length;j++){
        	if(systemLog.condition()[j].equals("ids")&&pv.containsKey("ids")){
        		Method method1 = cls.getSuperclass().getDeclaredMethod("queryListByObject",new Class[]{String.class,Object.class});
        		List<?> invoke = (List<?>) method1.invoke(bean, systemLog.method()[j],
        				pv.get(systemLog.condition()[j]));
        		Map<String,Object> idsMap=new HashMap<String,Object>();
        		idsMap=getVauleByReflect(invoke);
        		if(!(idsMap==null||idsMap.size()<=0))
        			afterValue.put("ids", idsMap);
        	}else{
        		if(Json.toMap(
    					Json.toJson(
    							method.invoke(bean, systemLog.method()[j], 
    									pv.get(systemLog.condition()[j]))))!=null){
        	afterValue.put(systemLog.condition()[j], 
        			Json.toMap(
					Json.toJson(
							method.invoke(bean, systemLog.method()[j], 
									pv.get(systemLog.condition()[j]))))
        			);
        		}
        	}
        }
        //比较变化
        StringBuffer sb=new StringBuffer("");
        for(int k=0;k<systemLog.condition().length;k++){
        	if(afterValue!=null&&beforeValue!=null&&afterValue.size()>0&&beforeValue.size()>0){
        		Map<String,Object> bMap=(Map<String,Object>)beforeValue.get(systemLog.condition()[k]);
                Map<String,Object> aMap=(Map<String,Object>)afterValue.get(systemLog.condition()[k]);
                sb.append(CommonUtil.eaqualsChar(aMap, bMap,Arrays.asList(systemLog.showLines())));
        }else if((afterValue==null||afterValue.size()<=0)&&beforeValue!=null&&beforeValue.size()>0){
        	//删除只记录beforeValue
        	//sb.append("删除的记录为:");
        	sb.append(CommonUtil.getMapValueNotNull(beforeValue,systemLog.showLines()));
        }else if((beforeValue==null||beforeValue.size()<=0)&&afterValue!=null&&afterValue.size()>0){
        	//删除只记录beforeValue
        	//sb.append("添加的记录为:");
        	sb.append(CommonUtil.getMapValueNotNull(afterValue,systemLog.showLines()));
        }else if((beforeValue==null||beforeValue.size()<=0)&&(afterValue==null||afterValue.size()<=0)){
        	//删除只记录beforeValue
        	//sb.append("添加的记录为:");
        	sb.append(CommonUtil.getMapValueNotNull(pv,systemLog.showLines()));
        }
    }
        // 创建空对象
        SystemLogBean systemLogBean = new SystemLogBean();
        systemLogBean.setTitle(systemLog.title());
        systemLogBean.setMethod(pv.get("methodName").toString());
        systemLogBean.setParam(sb.toString());
        systemLogBean.setIp(getIpAddr(request));
        if (adminName != null) {
            systemLogBean.setUserId(adminName.toString());
            systemLogBean.setCreateBy(adminName.toString());
            systemLogBean.setUpdateBy(adminName.toString());
        }
        systemLogBean.setCreateDate(new Date());
        systemLogBean.setUpdateDate(new Date());
        // 保存log
        systemLogService.saveLog(systemLogBean);
        return proceed;
		}
    }

    private SystemLogBean buildSystemLogBean(JoinPoint joinPoint) throws Throwable {
        
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Object adminName = request.getAttribute("userName");

        // 获取记录日志
        SystemLog systemLog = getAnnotationSystemLog(joinPoint);

       /* String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzName = clazz.getName();
        String methodName = joinPoint.getSignature().getName();
        // 获取参数名称
        String[] paramNames = getFieldsName(this.getClass(), clazzName, methodName);
        // 获取参数名称值对
        String paramkeyValue = getParamkeyValue(paramNames, joinPoint);
        if (StringUtils.isNotBlank(paramkeyValue) && paramkeyValue.length() > 300) {
            paramkeyValue = paramkeyValue.substring(0, 300);
        }*/
        
        // 创建空对象
        SystemLogBean systemLogBean = new SystemLogBean();
        systemLogBean.setTitle(systemLog.title());
       /* systemLogBean.setMethod(methodName);*/
        /*systemLogBean.setParam(paramkeyValue);*/
        systemLogBean.setIp(getIpAddr(request));
        if (adminName != null) {
            systemLogBean.setUserId(adminName.toString());
            systemLogBean.setCreateBy(adminName.toString());
            systemLogBean.setUpdateBy(adminName.toString());
        }
        systemLogBean.setCreateDate(new Date());
        systemLogBean.setUpdateDate(new Date());
        return systemLogBean;
    }

    @AfterThrowing(pointcut = "webLog()", throwing = "throwable")
    public void logException(JoinPoint joinPoint, Throwable throwable) throws Throwable {
        
        // 获取对象
        SystemLogBean systemLogBean = buildSystemLogBean(joinPoint);

        logger.info("=====LOG=====STATUS: 接口访问异常");
        logger.info("=====LOG=====TITLE: " + systemLogBean.getTitle());
        logger.info("=====LOG=====METHOD: " + systemLogBean.getMethod());
        logger.info("=====LOG=====PARAM: " + systemLogBean.getParam());
        logger.info("=====LOG=====IP: " + systemLogBean.getIp());
        if (systemLogBean.getUserId() != null) {
            logger.info("=====LOG=====USERID: " + systemLogBean.getUserId());
        }
        logger.info("=====LOG=====MSG: " + throwable.getMessage());
    }
    /**
     * 获取反射之后的值封装为Map
     * @param method1
     * @param invoke
     * @return
     * @throws Exception
     */
    public Map<String,Object> getVauleByReflect(List<?> invoke) throws Exception{
		Map<String,Object> idsMap=new HashMap<String,Object>();
		if(invoke!=null&&invoke.size()>0)
		for(int k=0;k<invoke.size();k++){
			if(invoke.get(k)!=null){
				Class<?> cla=invoke.get(k).getClass();
                Field[] fields = cla.getDeclaredFields();
                Map<String,Object> temp=new HashMap<String,Object>();
                for (Field field : fields) {
                	field.setAccessible(true);
					if(field.get(invoke.get(k))!=null){
						temp.put(field.getName(), field.get(invoke.get(k)));
					}
				}
                idsMap.put("ids_"+k, temp);
			}
		}
		return idsMap;
    }
    /**
     * 得到方法参数的名称
     * @param cls
     * @param clazzName
     * @param methodName
     * @return
     * @throws NotFoundException
     */
    private String[] getFieldsName(Class<?> cls, String clazzName, String methodName) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);
        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        String[] paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }

    /**
     * 获取参数名称值对
     * @param paramNames
     * @param joinPoint
     * @return
     * String
     */
    private String getParamkeyValue(String[] paramNames, JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < args.length; k++) {
            Object arg = args[k];
            // 根据对象类型获取kv
            if (arg != null) {
                if (arg instanceof Integer
                        || arg instanceof Double
                        || arg instanceof Float
                        || arg instanceof Long
                        || arg instanceof Short
                        || arg instanceof Byte
                        || arg instanceof Boolean
                        || arg instanceof String
                        || arg instanceof Integer) {
                    // 参数名称
                    sb.append(paramNames[k]);
                    // 参数值
                    sb.append("=" + arg + ",");
                } else if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse) ) {
                    
                    String json = "";
                    try {
                        Json.toJsonNoNull(arg);
                    } catch (Exception e) {
                    }
                    Map<String, Object> map = Json.toMap(json);
                    if (map != null && !map.isEmpty()) {
                        // 如果存在密码，进行敏感处理
                        if (map.containsKey("password")) {
                            map.put("password", "******");
                        }

                        // 参数名称
                        sb.append(paramNames[k]);
                        // 参数值
                        sb.append("=" + Json.toJsonNoNull(map) + ",");
                    }
                }
            } else {
                // 参数名称
                sb.append(paramNames[k]);
                // 参数值
                sb.append("=" + arg + ",");
            }
        }
        String param = sb.toString();
        param = param.length() > 0 ? param.substring(0, param.length() - 1) : "";

        return param;
    }

    /**
     * 获取客户端IP
     * @param request
     * @return
     * String
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.substring(ip.indexOf(",")==-1?0:ip.indexOf(",")+1, ip.length());
    }

    /**
    * 是否存在注解，如果存在就记录日志
    * @param joinPoint
    * @param controllerLog
    * @return
    * @throws Exception
    */
    private SystemLog getAnnotationSystemLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(SystemLog.class);
        }
        return null;
    }
}
