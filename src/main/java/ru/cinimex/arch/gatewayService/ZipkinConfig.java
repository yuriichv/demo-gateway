package ru.cinimex.arch.gatewayService;


import brave.Tracing;
import brave.context.slf4j.MDCCurrentTraceContext;
import brave.opentracing.BraveTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;


@Configuration
public class ZipkinConfig {

    @Value("${camel.zipkin.endpoint}")
    private String zipkinEndpoint;
    @Value("${camel.zipkin.serviceName}")
    private String localServiceName;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public io.opentracing.Tracer zipkinTracer() {
        OkHttpSender okHttpSender = OkHttpSender.create(zipkinEndpoint);
        AsyncReporter<Span> reporter = AsyncReporter.create(okHttpSender);
        Tracing tracer = Tracing.newBuilder()
                .localServiceName(localServiceName)
                .currentTraceContext(MDCCurrentTraceContext.create()) // до лучших времен. Сейчас инжектит только при вызове withSpanInScope(), в opentracing инетфейсах его нет, Camel его не юзает.
                .spanReporter(reporter)
                .build();

        return BraveTracer.create(tracer);
    }


}
