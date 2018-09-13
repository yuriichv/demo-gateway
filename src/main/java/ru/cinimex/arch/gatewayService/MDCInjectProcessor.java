package ru.cinimex.arch.gatewayService;

import brave.opentracing.BraveSpan;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.apache.camel.opentracing.ActiveSpanManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;


import static brave.internal.HexCodec.writeHexLong;

@Component("MDCInjectProcessor")
public class MDCInjectProcessor implements Processor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public void process(Exchange exchange) throws Exception {

        //opentracing data
        BraveSpan span = (BraveSpan) ActiveSpanManager.getSpan(exchange);
        MDC.put("traceId", span.context().unwrap().traceIdString());
        char[] spanId = new char[16];
        writeHexLong(spanId,0, span.context().unwrap().spanId());
        MDC.put("spanId", new String(spanId));
        char[] parentId = new char[16];
        writeHexLong(parentId,0, span.context().unwrap().parentIdAsLong());
        MDC.put("parentId", new String(parentId));


        //other data
        MDC.put("RequestId",exchange.getExchangeId());
        MDC.put("ServiceName",exchange.getFromRouteId());
        //...

    }



}
