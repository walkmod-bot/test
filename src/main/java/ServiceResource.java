import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


import org.apache.abdera.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/")
public final class ServiceResource extends AbstractResource {

  private final AtomServiceDocumentFactory atomServiceDocumentFactory;

  @Autowired
  public ServiceResource(final UrlHelper urlHelper, final AtomServiceDocumentFactory atomServiceDocumentFactory) {
    this.atomServiceDocumentFactory = atomServiceDocumentFactory;
  }

  @GET
  @Produces("application/atomsvc+xml")
  public Response getServiceDocument() {
    final Service service = atomServiceDocumentFactory.create();
    return authorizedResponse(service, DEFAULT_CACHECONTROL_HEADER_TIME);
  }

}
