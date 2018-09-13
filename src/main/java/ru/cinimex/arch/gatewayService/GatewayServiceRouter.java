package ru.cinimex.arch.gatewayService;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;


@Component
public class GatewayServiceRouter extends RouteBuilder{

    @Override
    public void configure() throws Exception {

        restConfiguration().host("localhost").port("{{service.port}}").bindingMode(RestBindingMode.auto);

        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody().constant("{\"status\":\"fault\"}");

        rest("/user/{id}")
                .get()
                .to("direct:discount")
         ;

        from("direct:discount")
                .process("MDCInjectProcessor")
              //  .removeHeader(Exchange.HTTP_PATH)
             //   .removeHeader(Exchange.HTTP_URI)
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .setHeader(Exchange.HTTP_PATH,simple("/account/${header.id}"))
                .toD("{{account-service}}")
                .unmarshal().json(JsonLibrary.Jackson)
                .process("CategoryParseProcessor")
                .marshal().json(JsonLibrary.Jackson)
            //    .delay(simple("${random(0,30)}"))
                .setBody(constant(null))
               // .log(simple(jsonpath("$.category")))
               // .log("${body}")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .setHeader(Exchange.HTTP_PATH, simple("/category/${header.category}"))
                .toD("{{discount-service}}")
                .unmarshal().json(JsonLibrary.Jackson)
                ;


    }

}
