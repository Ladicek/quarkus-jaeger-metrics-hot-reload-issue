package cz.ladicek.reproducer;

import io.opentracing.Tracer;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleResource {
    private static final Logger LOG = Logger.getLogger(ExampleResource.class);

    @Inject
    ExampleService service;

    @Inject
    Tracer tracer;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> get(@QueryParam("name") @DefaultValue("World") String name) {
        LOG.info("HelloContextPropagationResource called");
        tracer.activeSpan().log("HelloContextPropagationResource called");
        return service.get(name);
    }
}
