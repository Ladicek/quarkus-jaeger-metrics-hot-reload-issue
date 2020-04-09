# Jaeger metrics hot reload issue reproducer

1. Build Quarkus:

    1. Either from the upstream `master` branch, or from my fork which has a fix that is easy to uncomment and verify:
        1. `git clone https://github.com/quarkusio/quarkus.git quarkus-test`
        1. `git clone -b metrics-hot-reload-problem https://github.com/Ladicek/quarkus.git quarkus-test`
    1. `cd quarkus-test`
    1. `mvn clean install -DskipTests -DskipITs -DskipDocs`

1. Run Jaeger locally: `docker run -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 jaegertracing/all-in-one:1.17`

1. Run this reproducer in dev mode:

    1. `git clone https://github.com/Ladicek/quarkus-jaeger-metrics-hot-reload-issue.git`
    1. `cd quarkus-jaeger-metrics-hot-reload-issue`
    1. `mvn clean quarkus:dev`

1. Hit the endpoint once: `curl http://localhost:8080`

1. Change something in `ExampleResource` or `ExampleService`

1. Hit the endpoint again: `curl http://localhost:8080`

1. Observe exception in the Quarkus log:

    ```
    2020-04-09 10:17:46,624 ERROR [io.qua.ver.htt.run.QuarkusErrorHandler] (executor-thread-63) HTTP Request to / failed, error id: 6d513f7b-e361-47c8-b86b-2a8074791b40-1: org.jboss.resteasy.spi.UnhandledException: java.lang.RuntimeException: No reflection exceptions should be thrown unless there is a fundamental error in your code set up.
            at org.jboss.resteasy.core.ExceptionHandler.handleException(ExceptionHandler.java:381)
            at org.jboss.resteasy.core.SynchronousDispatcher.writeException(SynchronousDispatcher.java:216)
            at org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:515)
            at org.jboss.resteasy.core.SynchronousDispatcher.lambda$invoke$4(SynchronousDispatcher.java:259)
            at org.jboss.resteasy.core.SynchronousDispatcher.lambda$preprocess$0(SynchronousDispatcher.java:160)
            at org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext.filter(PreMatchContainerRequestContext.java:362)
            at org.jboss.resteasy.core.SynchronousDispatcher.preprocess(SynchronousDispatcher.java:163)
            at org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:245)
            at io.quarkus.resteasy.runtime.standalone.RequestDispatcher.service(RequestDispatcher.java:73)
            at io.quarkus.resteasy.runtime.standalone.VertxRequestHandler.dispatch(VertxRequestHandler.java:122)
            at io.quarkus.resteasy.runtime.standalone.VertxRequestHandler.access$000(VertxRequestHandler.java:36)
            at io.quarkus.resteasy.runtime.standalone.VertxRequestHandler$1.run(VertxRequestHandler.java:87)
            at io.quarkus.runtime.CleanableExecutor$CleaningRunnable.run(CleanableExecutor.java:231)
            at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
            at java.util.concurrent.FutureTask.run(FutureTask.java:266)
            at org.jboss.threads.ContextClassLoaderSavingRunnable.run(ContextClassLoaderSavingRunnable.java:35)
            at org.jboss.threads.EnhancedQueueExecutor.safeRun(EnhancedQueueExecutor.java:2027)
            at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.doRunTask(EnhancedQueueExecutor.java:1551)
            at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1442)
            at org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:29)
            at org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:29)
            at java.lang.Thread.run(Thread.java:748)
            at org.jboss.threads.JBossThread.run(JBossThread.java:479)
    Caused by: java.lang.RuntimeException: No reflection exceptions should be thrown unless there is a fundamental error in your code set up.
            at io.jaegertracing.internal.metrics.Metrics.createMetrics(Metrics.java:76)
            at io.jaegertracing.internal.metrics.Metrics.<init>(Metrics.java:32)
            at io.jaegertracing.internal.metrics.Metrics.<init>(Metrics.java:28)
            at io.jaegertracing.Configuration.getTracerBuilder(Configuration.java:221)
            at io.quarkus.jaeger.runtime.QuarkusJaegerTracer.tracer(QuarkusJaegerTracer.java:40)
            at io.quarkus.jaeger.runtime.QuarkusJaegerTracer.buildSpan(QuarkusJaegerTracer.java:59)
            at io.opentracing.util.GlobalTracer.buildSpan(GlobalTracer.java:133)
            at io.quarkus.smallrye.opentracing.runtime.TracerProducer_ProducerMethod_tracer_96dadb3d6afa0cccadfe742c3e06ad433737c844_ClientProxy.buildSpan(TracerProducer_ProducerMethod_tracer_96dadb3d6afa0cccadfe742c3e06ad433737c844_ClientProxy.zig:231)
            at io.opentracing.contrib.jaxrs2.server.ServerTracingFilter.filter(ServerTracingFilter.java:69)
            at org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext.filter(PreMatchContainerRequestContext.java:310)
            at org.jboss.resteasy.core.ResourceMethodInvoker.invokeOnTarget(ResourceMethodInvoker.java:439)
            at org.jboss.resteasy.core.ResourceMethodInvoker.invoke(ResourceMethodInvoker.java:400)
            at org.jboss.resteasy.core.ResourceMethodInvoker.invoke(ResourceMethodInvoker.java:374)
            at org.jboss.resteasy.core.ResourceMethodInvoker.invoke(ResourceMethodInvoker.java:67)
            at org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:488)
            ... 20 more
    Caused by: java.lang.IllegalArgumentException: A metric with metricID MetricID{name='jaeger_tracer_reporter_queue_length', tags=[]} already exists
            at io.smallrye.metrics.MetricsRegistryImpl.register(MetricsRegistryImpl.java:131)
            at io.quarkus.jaeger.runtime.QuarkusJaegerMetricsFactory.createGauge(QuarkusJaegerMetricsFactory.java:48)
            at io.jaegertracing.internal.metrics.Metrics.createMetrics(Metrics.java:66)
            ... 34 more
    ```
