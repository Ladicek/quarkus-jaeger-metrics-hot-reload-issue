package cz.ladicek.reproducer;

import io.opentracing.Tracer;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ExampleService {
    private static final Logger LOG = Logger.getLogger(ExampleService.class);

    @Inject
    ManagedExecutor executor;

    @Inject
    Tracer tracer;

    // @Traced // this is purely synchronous, the async log would be lost :-/
    public CompletionStage<String> get(String name) {
        LOG.info("HelloContextPropagationService called");
        tracer.activeSpan().log("HelloContextPropagationService called");

        return executor.supplyAsync(() -> {
            LOG.info("HelloContextPropagationService async processing");
            tracer.activeSpan().log("HelloContextPropagationService async processing");
            return "Hello, " + name + "!";
        });
    }
}
