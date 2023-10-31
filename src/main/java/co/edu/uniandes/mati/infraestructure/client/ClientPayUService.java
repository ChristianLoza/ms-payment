package co.edu.uniandes.mati.infraestructure.client;


import co.edu.uniandes.mati.domain.vo.Order;
import co.edu.uniandes.mati.domain.vo.OrderPayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;



@ApplicationScoped
@RegisterRestClient(configKey = "service-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ClientPayUService {
    @POST
    @Path("payment")
    OrderPayment generatePayment(Order order);
}
