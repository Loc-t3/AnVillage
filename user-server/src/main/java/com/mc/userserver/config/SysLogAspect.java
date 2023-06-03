package com.mc.userserver.config;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.mc.common.utils.R;
import com.mc.common.utils.ResultsCode;
import com.mc.common.utils.SpringContextUtil;
import com.mc.userserver.entity.UserOperationLogTable;
import com.mc.userserver.filter.BaseContext;
import com.mc.userserver.mapper.UserOperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.mc.common.utils.UserCommon.setUUId;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-06-03 9:42
 * @类说明：填写类说明
 * @修改记录：
 */
@Aspect
@Component
public class SysLogAspect {
    @Autowired
    private UserOperationLogMapper logMapper;

    private static final Logger log = LoggerFactory.getLogger(SysLogAspect.class);

    private static final DefaultParameterNameDiscoverer DEFAULT_PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
    private static final TemplateParserContext TEMPLATE_PARSER_CONTEXT = new TemplateParserContext();
    private static final ThreadLocal<StandardEvaluationContext> StandardEvaluationContextThreadLocal = new ThreadLocal<>();

    /**
     * 开始时间
     */
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();


    @Pointcut("@annotation(com.mc.userserver.config.SysLog)")
    public void sysLogPointCut() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @SuppressWarnings("unused")
    @Before("sysLogPointCut()")
    public void doBeforeReturning(JoinPoint joinPoint) {
        // 设置请求开始时间
        START_TIME.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(
            pointcut = "sysLogPointCut()",
            returning = "result"
    )
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        printLog(joinPoint, result, null);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(
            pointcut = "sysLogPointCut()",
            throwing = "e"
    )
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        printLog(joinPoint, null, e);
    }

    /**
     * 打印日志
     *
     * @param point  切点
     * @param result 返回结果
     * @param e      异常
     */
    protected void printLog(JoinPoint point, Object result, Exception e) {

        //获取请求ip 请求url
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String remoteAddr = request.getRemoteAddr();
        String requestURL = request.getRequestURL().toString();
        String header = request.getHeader("user-agent");

        //获取执行方法类和方法名
        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = ClassUtils.getUserClass(point.getTarget()).getName();
        String methodName = point.getSignature().getName();
        //以数组的方式获取具体方法类型和参数类型
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        Method method;
        try {
            method = point.getTarget().getClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return;
        }

        // 获取注解相关信息
        SysLog sysLog = method.getAnnotation(SysLog.class);
        String logExpression = sysLog.value();
        String logLevel = sysLog.level();
        Integer printResult = ((R) result).getCode();

        // 解析日志中的表达式->获取传入参数
        Object[] args = point.getArgs();
        String[] parameterNames = DEFAULT_PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        Map<String, Object> params = new HashMap<>();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                params.put(parameterNames[i], args[i]);
            }
        }

        // 解析@SysLog表达式
        String logInfo = parseExpression(logExpression, params);

        Long costTime = null;
        // 请求开始时间
        Long startTime = START_TIME.get();
        String costT = null;

        if (startTime != null) {
            // 请求耗时
            costTime = System.currentTimeMillis() - startTime;
            costT = costTime.toString();
            // 清空开始时间
            START_TIME.remove();
        }

        // 如果发生异常，强制打印错误级别日志
        if(e != null) {
            log.error("{}: , exception: {}, costTime: {}ms", requestURL, logInfo, e.getMessage(), costT);
            return;
        }
        //将需要保存的日志信息存储
        HashMap<String, Object> map = new HashMap<>();
        map.put("remoteAddr",remoteAddr);
        map.put("requestURL",requestURL);
        map.put("header",header);
        map.put("params",params);
        map.put("logInfo",logInfo);
        if (((R) result).getCode()== ResultsCode.SUCCESS.code){
            map.put("logInfo",logInfo);
        }else{
            map.put("logInfo",((R) result).getMsg());
        }
        map.put("result",((R<?>) result).getCode());
        map.put("costTime",costT+"ms");

        // 以下为打印对应级别的日志
        printLevelData(logLevel,printResult,map);
        //保存用户操作记录
        saveLogInfo(map);

    }

    /**
     * 解析@SysLog表达式
     * @param template
     * @param params
     * @return
     */
    private String parseExpression(String template, Map<String, Object> params) {

        // 将ioc容器设置到上下文中
        ApplicationContext applicationContext = (ApplicationContext) new SpringContextUtil().getApplicationContext();

        // 线程初始化StandardEvaluationContext
        StandardEvaluationContext standardEvaluationContext = StandardEvaluationContextThreadLocal.get();
        if(standardEvaluationContext == null){
            standardEvaluationContext = new StandardEvaluationContext(applicationContext);
            standardEvaluationContext.addPropertyAccessor(new BeanFactoryAccessor());

            StandardEvaluationContextThreadLocal.set(standardEvaluationContext);
        }

        // 将自定义参数添加到上下文
        standardEvaluationContext.setVariables(params);

        // 解析表达式
        Expression expression = EXPRESSION_PARSER.parseExpression(template, TEMPLATE_PARSER_CONTEXT);

        return expression.getValue(standardEvaluationContext, String.class);
    }

    /**
     * 依据日志级别打印对应日志
     * @param logLevel
     */
    public void printLevelData(String logLevel,Integer printResult,HashMap map){
        if("info".equalsIgnoreCase(logLevel)){
            if (printResult>0) {
//                log.info("{}#{}(): {}, result: {}, costTime: {}ms", className, methodName, logInfo, result, costTime);
                log.info("<=====================================================");
                log.info("请求来源： =》" + map.get("remoteAddr"));
                log.info("请求URL：=》" + map.get("requestURL"));
                log.info("操作设备：=》" + map.get("header"));
                log.info("请求参数：=》" + map.get("params"));
                log.info("返回数据：=》"+map.get("result"));
                log.info("执行描述：=》"+map.get("logInfo"));
                log.info("costTime: {}ms =>"+map.get("costTime"));
                log.info("------------------------------------------------------");
            } else {
                log.info("{}:costTime: {}ms", map.get("requestURL"), map.get("costTime"));
            }
        }
       /* else if("debug".equalsIgnoreCase(logLevel)){
            if (printResult) {
                log.debug("{}#{}(): {}, result: {}, costTime: {}ms", className, methodName, logInfo, result, costTime);
            } else {
                log.debug("{}#{}(): {}, costTime: {}ms", className, methodName, logInfo, costTime);
            }
        } else if("trace".equalsIgnoreCase(logLevel)){
            if (printResult) {
                log.trace("{}#{}(): {}, result: {}, costTime: {}ms", className, methodName, logInfo, result, costTime);
            } else {
                log.trace("{}#{}(): {}, costTime: {}ms", className, methodName, logInfo, costTime);
            }
        } else if("warn".equalsIgnoreCase(logLevel)){
            if (printResult) {
                log.warn("{}#{}(): {}, result: {}, costTime: {}ms", className, methodName, logInfo, result, costTime);
            } else {
                log.warn("{}#{}(): {}, costTime: {}ms", className, methodName, logInfo, costTime);
            }
        } else if("error".equalsIgnoreCase(logLevel)){
            if (printResult) {
                log.error("{}#{}(): {}, result: {}, costTime: {}ms", className, methodName, logInfo, result, costTime);
            } else {
                log.error("{}#{}(): {}, costTime: {}ms", className, methodName, logInfo, costTime);
            }
        }*/

    }

    /**
     * 保存用户操作日志
     * @param maps
     */
    public void saveLogInfo(HashMap maps){
        UserOperationLogTable logTable = new UserOperationLogTable();
        logTable.setOperationId(setUUId());
        logTable.setUserId(BaseContext.getUser().getUserId());
        logTable.setOperationAddr(maps.get("remoteAddr").toString());
        logTable.setOperationUrl(maps.get("requestURL").toString());
        logTable.setOperationCost(maps.get("costTime").toString());
        logTable.setOperationDesc(maps.get("logInfo").toString());
        logTable.setOperationResult(maps.get("result").toString());
        logTable.setOperationContent(maps.get("params").toString());
        logTable.setOperationDev(maps.get("header").toString());
        logMapper.insert(logTable);



    }
}
