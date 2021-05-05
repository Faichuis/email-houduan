package com.platform.serve.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import springfox.documentation.service.Parameter;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Swagger配置类
 *
 * @Author lds
 * @Date 2020/8/22 9:48
 */
@Configuration //配置类
@EnableSwagger2//开启Swagger2的自动配置
public class SwaggerConfig {

    @Bean //配置docket以配置Swagger具体参数
    public Docket docket() {
        //在配置好的配置类中增加此段代码即可
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("userCode").description("登录用户")//name表示名称，description表示描述
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).defaultValue("ceshi001").build();//required表示是否必填，defaultvalue表示默认值
        pars.add(ticketPar.build());//添加完此处一定要把下边的带***的也加上否则不生效


        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true) //配置是否启用Swagger，如果是false，在浏览器将无法访问
                .select()//通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
                //.apis(RequestHandlerSelectors.basePackage("com.platform.serve.inner.controller"))
                .apis(RequestHandlerSelectors.basePackage("com.platform.serve"))
                //配置如何通过path过滤,即这里只扫描请求以/kuang开头的接口
                //.paths(PathSelectors.ant("/**"))
                //.paths(regex("/email/.*"))
                .paths(regex("/..*"))
                .build()
                .globalOperationParameters(pars)//************把消息头添加;
                ;
    }


    //配置文档信息
    private ApiInfo apiInfo() {
        Contact contact = new Contact("东指羲和", "https://bbs.chcoin.com/u/186489?a=post", "lds0303@163.com");
        return new ApiInfo(
                "东指羲和的Swagger文档", //标题
                "redis复杂使用", //描述
                "v1.0", //版本
                "https://org.modao.cc/app/dc6JfHt9vYfdjTOyz38w4p3UBEky5kR?simulator_type=device&sticky=#screen=skbsynufxta76ew", //组织链接
                contact, //联系人信息
                "Redis许可", //许可
                "https://redis.io/documentation", //许可连接
                new ArrayList<>()//扩展
        );
    }


}

