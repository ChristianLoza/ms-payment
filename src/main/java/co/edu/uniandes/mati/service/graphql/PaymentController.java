package co.edu.uniandes.mati.service.graphql;

import co.edu.uniandes.mati.application.PaymentService;
import co.edu.uniandes.mati.domain.entity.Payment;
import co.edu.uniandes.mati.domain.vo.GeneratePayment;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.List;

@GraphQLApi
@ApplicationScoped
@Blocking
public class PaymentController {
    @Inject
    PaymentService paymentService;

    @Inject
    ObjectMapper mapper;

    @Query
    @Description("List data")
    public Uni<List<Payment>> getAllPayment() {
        return paymentService.getAllPayments();
    }

    @SneakyThrows
    @Incoming("uniandes")
    @Transactional
    public Uni<Void> getExample(String generatePayment){
        GeneratePayment getPayment = mapper.readValue(generatePayment, GeneratePayment.class);
        System.out.println(getPayment);
        paymentService.createPayment(getPayment);
        return Uni.createFrom().voidItem();
    }

}
