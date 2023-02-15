# Response Spring Boot Starter

[maven中央仓库地址](https://search.maven.org/artifact/tech.xiaoxian/response-spring-boot-starter)

## 功能描述

本项目会通过**ResponseBodyAdvice**切面控制和**ExceptionHandler**
异常处理对所有controller返回值或抛出的异常进行分析，最终项目接口会返回如下形式的HttpResponse

```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```
其中code和message对应返回状态和异常信息，data则是任意类型的返回数据

## 使用方法

### maven

```xml

<dependency>
    <groupId>tech.xiaoxian</groupId>
    <artifactId>response-spring-boot-starter</artifactId>
    <version>1.0.2</version>
</dependency>
```

### gradle

```groovy
implementation 'tech.xiaoxian:response-spring-boot-starter:1.0.2'
```

## starter 配置方法

默认情况下，仅需使用包管理工具导入本包即可使用，额外配置包含application.yml配置

```yaml
xiaoxian:
  response:
    log:
      print-error: true # 配置是否在全局异常处理切面类中向控制台输出详细异常堆栈信息
```

## starter 基础使用方法

默认情况下，仅需使用包管理工具导入本包即可使用
本项目通过全局异常处理切面类进行捕获并相应返回值
### 编写controller
```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.xiaoxian.response.HttpBusinessCode;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/test1")
    public String test1(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            throw HttpBusinessCode.INTERNAL_SERVER_ERROR.error();
        }
        return "hello" + name;
    }
}
```
访问上述接口获取返回值
```shell
# 抛出异常
curl http://127.0.0.1:8080/test/test1
# {"code":500,"data":null,"message":"内部错误"}

# 正常返回
curl http://127.0.0.1:8080/test/test1\?name\=123
#{"code":200,"data":"hello123","message":"success"}
```
* 当接口函数正常执行后的返回值，本项目会对其进行包装为**UnifiedResponse**结构，并将原返回值塞入data中
* 当接口函数抛出项目定义的**BusinessException**异常后，本项目会解析异常中包裹的**UnifiedResponse**结构并返回
* 当接口函数抛出其他异常类型时，项目会根据**AbstractUnifiedResponseAdvice**或其子类中的**ExceptionHandler**按情况匹配，并进行处理返回
## starter 额外使用方法

### 自定义BusinessCode

```java
import tech.xiaoxian.response.UnifiedResponse;

/**
 * 业务代码，用于定义和排查错误类型
 */
public enum BusinessCode implements UnifiedResponse.UnifiedResponseCode {
    //请将系统中所有需要特殊区分的异常信息填写到该文件中 建议从600开始（0-5xx 被内置的http配置使用）
    // 10xx
    NAME_INVALID(1000, "姓名无效"),
    // ... 其他异常枚举
    ;
    private final int code;
    private final String msg;

    BusinessCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
```

使用上述异常时可以通过直接抛出实现，本项目通过全局异常处理切面类进行捕获并相应返回值

```java

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/test1")
    public String test1(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            throw BusinessCode.NAME_INVALID.error();
        }
        return "hello" + name;
    }
}
```

### 自定义全局异常处理切面类

自定义全局异常处理切面需要

* 继承本项目的**AbstractUnifiedResponseAdvice**类，实现相应方法
* 添加类注解@RestControllerAdvice，以进行控制器切面配置，同时会屏蔽本项目UnifiedResponseAutoConfigure自动配置中的默认切面
* 最后添加你自定义的@ExceptionHandler异常处理函数以处理特定的异常类型并返回UnifiedResponse对象

```java

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.xiaoxian.response.AbstractUnifiedResponseAdvice;
import tech.xiaoxian.response.BusinessException;
import tech.xiaoxian.response.UnifiedResponse;

@RestControllerAdvice
public class UnifiedResponseAdvice extends AbstractUnifiedResponseAdvice {

    @Override
    protected Boolean isPrintError() {
        // 不打印异常
        return false;
    }
    @Override
    protected boolean ignoreBodyRewrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 所有返回值均重写返回体为UnifiedResponse结构
        return false;
    }
    // 以下函数均为异常处理函数

    /**
     * 系统自身业务异常处理
     *
     * @param businessException 业务异常
     * @return 统一返回信息
     */
    @ExceptionHandler(value = BusinessException.class)
    public UnifiedResponse handleBusinessException(BusinessException businessException) {
        UnifiedResponse response = businessException.getResponse();
        if (isPrintError()) {
            logger.error("异常码: {} 异常原因: {}", response.getCode(), response.getMessage());
        } else {
            logger.error("异常码: {} 异常原因: {}", response.getCode(), response.getMessage(), businessException);
        }
        return response;
    }
}
```

