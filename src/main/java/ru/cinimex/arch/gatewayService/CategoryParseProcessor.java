package ru.cinimex.arch.gatewayService;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component("CategoryParseProcessor")
public class CategoryParseProcessor implements Processor {
    public void process(Exchange exchange) throws Exception {
        LinkedHashMap<String,String> m = (LinkedHashMap)exchange.getMessage().getBody();
        exchange.getMessage().setHeader("category", m.get("category"));
      //  exchange.getMessage().getBody().getClass();
    }
}
