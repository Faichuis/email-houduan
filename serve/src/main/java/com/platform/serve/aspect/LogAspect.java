package com.platform.serve.aspect;

import com.alibaba.fastjson.JSON;
import com.platform.serve.common.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * 日志切面AOP
 *
 * @Author lds
 * @Date 2021/3/22 16:52
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

//    //切入点注解的表达式：就是需要AOP的地方(一般是业务逻辑层service,当然服务接口调用层controller也行,两者一起打印日志也行
//    //这个类似正则表达式,可以控制日志的精度(包下,类下,方法下)和切面的类型(业务层面,服务接口层面)相当灵活)
//    @Pointcut("execution(* com.platform.serve.*(..))")
//    // @Pointcut("execution(* com.example.nba.repository.PlayerRep.*(..))")
//    //切入点签名的方法，注意返回值必须是void,相当于切入点的无参构造
//    public void mypointcut() {
//    }


    @Pointcut("@annotation(com.platform.serve.aspect.RunTime)")
    public void runTime() {

    }

    //@Pointcut("execution(public * com.platform.serve..*.*Controller.*(..))")
    //public void webLog() {

    //}

    /**
     * 定义增强
     * 在切点的什么位置增加新的功能
     *
     * @Before：方法执行前
     * @After方法执行后
     * @Around方法执行前+方法执行后
     * @AfterThrowing：方法抛出异常后
     * @AfterReturning：方法返回后
     */
    //@Around("webLog()")
    @Around("execution(public * com.platform.serve..*.*Controller.*(..))")
    public Object doWebLogAround(ProceedingJoinPoint jp) throws Throwable {
        String url = null;
        InetAddress serversIP = null;
        String clientIP = null;
        Object inParameters = null;
        try {
            HttpServletRequest request = RequestUtils.getHttpServletRequest();
            //访问URL
            url = request.getRequestURI();
            //服务器地址
            serversIP = InetAddress.getLocalHost();
            //客户端地址
            clientIP = RequestUtils.getIp(request);
            //参数
            inParameters = null;
            Object[] args = jp.getArgs();
            if (url.equals("/api/file/") && (args != null && args.length > 0)) {
                inParameters = JSON.toJSON(args);
            }
        } catch (Exception e) {
            log.error("==>> 环绕增强：doWebLogAround前置参数处理异常：URL：{}，IN-Parameters：{}，Servers-IP：{}，Client-IP：{}，Exception{} <<==", url, inParameters, serversIP, clientIP, e);
        }

        try {
            log.info("==>> URL：{}，IN-Parameters：{}，Servers-IP：{}，Client-IP：{} ==>>", url, inParameters, serversIP, clientIP);
            //log.info("调用【{}】的【{}】方法。方法入参", jp.getTarget().getClass().getSimpleName(), jp.getSignature().getName(), Arrays.toString(jp.getArgs()));

            Object result = jp.proceed();

            //log.info(jp.getTarget() + "的【" + jp.getSignature().getName() + "】方法。方法返回值：" + result);
            log.info("<<== URL：{}，IN-Parameters：{}，Servers-IP：{}，Client-IP：{} <<==", url, inParameters, result, serversIP, clientIP);
            return result;
        } catch (Throwable e) {
            log.error("==>> 环绕增强：doWebLogAround发生异常：URL：{}，IN-Parameters：{}，Servers-IP：{}，Client-IP：{}，Exception{} <<==", url, inParameters, serversIP, clientIP, e);
            throw e;
        }
    }


    @Around("runTime()")
    public Object doRunTimeAround(ProceedingJoinPoint jp) throws Throwable {
        String url = null;
        InetAddress serversIP = null;
        String clientIP = null;
        Object inParameters = null;
        try {
            HttpServletRequest request = RequestUtils.getHttpServletRequest();
            //访问URL
            url = request.getRequestURI();
            //服务器地址
            serversIP = InetAddress.getLocalHost();
            //客户端地址
            clientIP = RequestUtils.getIp(request);
            //参数
            inParameters = null;
            Object[] args = jp.getArgs();
            if (url.equals("/api/file/") && (args != null && args.length > 0)) {
                inParameters = JSON.toJSON(args);
            }
        } catch (Exception e) {
            log.error("==>> 环绕增强：doRunTimeAround前置参数处理异常：URL：{}，IN-Parameters：{}，Servers-IP：{}，Client-IP：{}，Exception{} <<==", url, inParameters, serversIP, clientIP, e);
        }

        try {
            long start = System.currentTimeMillis();
            Object result = jp.proceed();
            log.info("==>> URL：{}，IN-Parameters：{}，Servers-IP：{}，Client-IP：{}，Time：{}ms<<==", url, inParameters, result, serversIP, clientIP, (System.currentTimeMillis() - start));
            return result;
        } catch (Throwable e) {
            log.error("==>> 环绕增强：doRunTimeAround发生异常：URL：{}，IN-Parameters：{}，Servers-IP：{}，Client-IP：{}，Exception{} <<==", url, inParameters, serversIP, clientIP, e);
            throw e;
        }
    }


//    //	前置增强
//    @Before("mypointcut()")
//    public void Mybefore(JoinPoint jp) {
//        logger.info("*前置增强*调用了【" + jp.getTarget().getClass().getSimpleName() +
//                "】的【" + jp.getSignature().getName() + "】的方法，方法入参为【"
//                + Arrays.toString(jp.getArgs()) + "】");
//        // 接收到请求，记录请求内容(这里同样可以在前置增强配置请求的相关信息)
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        logger.info("请求的地址URL : " + request.getRequestURL().toString());
//        logger.info("请求的方式HTTP_METHOD : " + request.getMethod());
//        logger.info("请求的IP : " + request.getRemoteAddr());
//        logger.info("请求的全类名 : " + jp.getSignature().getDeclaringTypeName() + "." + jp.getSignature().getName());
//        logger.info("请求的参数(数组形式) : " + Arrays.toString(jp.getArgs()));
//
//    }
//
//    //后置增强
//    @AfterReturning(pointcut = "mypointcut()", returning = "result")
//    public void MyafterReturing(JoinPoint jp, Object result) {
//        logger.info("*后置增强*调用了【" + jp.getTarget().getClass().getSimpleName() +
//                "】的【" + jp.getSignature().getName() + "】的方法，方法返回值【" + result + "】");
//    }
//
//    //	异常抛出增强
//    @AfterThrowing(pointcut = "mypointcut()", throwing = "e")
//    public void afterThrowing(JoinPoint jp, RuntimeException e) {
//        logger.error("*异常增强*【" + jp.getSignature().getName().getClass().getSimpleName() + "】方法发生异常【" + e + "】");
//    }
//
//    //	最终增强
//    @After("mypointcut()")
//    public void afterLogger(JoinPoint jp) {
//        logger.info("*最终增强*【" + jp.getSignature().getName() + "】方法结束执行。");
//    }
//
//    //环绕增强
//    @Around("mypointcut()")
//    public Object aroundLogger(ProceedingJoinPoint jp) throws Throwable {
//        logger.info("在==>>" + jp.getTarget().getClass().getName() + "类里面使用AOP环绕增强==");
//        logger.info("*环绕增强*调用【" + jp.getTarget().getClass().getSimpleName() + "】的【 " + jp.getSignature().getName()
//                + "】方法。方法入参【" + Arrays.toString(jp.getArgs()) + "】");
//        try {
//            Object result = jp.proceed();
//            logger.info("*环绕增强*调用 " + jp.getTarget() + "的【 "
//                    + jp.getSignature().getName() + "】方法。方法返回值【" + result + "】");
//            return result;
//        } catch (Throwable e) {
//            logger.error(jp.getSignature().getName() + " 方法发生异常【" + e + "】");
//            throw e;
//        } finally {
//            logger.info("*环绕增强*执行finally【" + jp.getSignature().getName() + "】方法结束执行<<==。");
//        }
//    }

}
