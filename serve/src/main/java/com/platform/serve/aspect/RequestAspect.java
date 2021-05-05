package com.platform.serve.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP使用
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/13 22:27
 */
@Slf4j
@Aspect
@Component
public class RequestAspect {

//    

//    public Object apiAround(ProceedingJoinPoint pPoint) throws Throwable {
//        HttpServletRequest request = RequestUtils.getHttpServletRequest();
//        String url = request.getRequestURI();
//        log.info("请求URL:[{}],来源IP:[{}]", url, RequestUtils.getIp(request));
//        Object[] args = pPoint.getArgs();
//        //获取参数名称和值
//        MethodSignature methodSignature = (MethodSignature) pPoint.getSignature();
//        String[] parameterNames = methodSignature.getParameterNames();
//        for (int i = 0; i < parameterNames.length; i++) {
//            if (i >= args.length) {
//                break;
//            }
//            Object value = args[i];
//            String name = parameterNames[i];
//            if (!(value instanceof HttpServletRequest) && !(value instanceof HttpServletResponse)) {
//                log.info("请求参数[{}]：{}", name, JSONObject.toJSONString(value));
//            }
//        }
//        Object object = null;
//        try {
//            object = pPoint.proceed(args);
//        } catch (Exception e) {
//            //object = HttpResult.error(e.getMessage(), -1);
//        }
////        String json = JSON.toJSONString(object, SerializeConfig.getGlobalInstance());
////        log.info("响应报文: {}", json);
//        return object;
//    }

}