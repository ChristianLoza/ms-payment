package co.edu.uniandes.mati.service.graphql;

import co.edu.uniandes.mati.application.PaymentService;
import co.edu.uniandes.mati.domain.entity.Payment;
import co.edu.uniandes.mati.domain.vo.GeneratePayment;
import co.edu.uniandes.mati.domain.vo.PaymentCheck;
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
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.List;

@GraphQLApi
@ApplicationScoped
@Blocking
public class PaymentController {
    @Inject
    Logger log;
    @Inject
    PaymentService paymentService;
    @Inject
    ObjectMapper mapper;
    @Channel("TOPIC_PAYMENT")
    Emitter<String> emitter;

    @Query
    @Description("List data")
    public Uni<List<Payment>> getAllPayment() {
        return paymentService.getAllPayments();
    }

    @SneakyThrows
    @Incoming("uniandes-proof")
    @Transactional
    public Uni<Void> getExample(String generatePayment){
        GeneratePayment getPayment = mapper.readValue(generatePayment, GeneratePayment.class);
        log.info("### Consume Kafka" + getPayment);
        Payment paymentGenerate = paymentService.createPayment(getPayment);
        log.info("###  paymentGenerate:: " + paymentGenerate);
        PaymentCheck paymentCheck = new PaymentCheck();
        paymentCheck.setIdTest(paymentGenerate.getIdTest());
        paymentCheck.setEmail(paymentGenerate.getUser().getEmail());
        paymentCheck.setStatus(paymentGenerate.getStatus());
        paymentCheck.setPaymentDate(paymentGenerate.getPaymentDate());

        emitter.send(mapper.writeValueAsString(paymentCheck));
        log.info("### Send kafka Message" + paymentCheck);
        return Uni.createFrom().voidItem();
    }

}
