package co.edu.uniandes.mati.service.graphql;

import co.edu.uniandes.mati.application.PaymentService;
import co.edu.uniandes.mati.domain.entity.Payment;
import co.edu.uniandes.mati.domain.vo.GeneratePayment;
import co.edu.uniandes.mati.domain.vo.Order;
import co.edu.uniandes.mati.domain.vo.OrderPayment;
import co.edu.uniandes.mati.domain.vo.PaymentCheck;
import co.edu.uniandes.mati.infraestructure.client.ClientPayUService;
import co.edu.uniandes.mati.infraestructure.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.Collections;
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
	PaymentRepository paymentRepository;
	@Inject
	ObjectMapper mapper;
	@Channel("TOPIC_PAYMENT")
	Emitter<String> emitter;

	@Channel("TOPIC_POSTPAYMENT")
	Emitter<String> postPaymentEmitter;

	@RestClient
	ClientPayUService clientPayUService;

	Payment paymentGenerate;

	@Query
	@Description("List data")
	public Uni<List<Payment>> getAllPayment() {
		return paymentService.getAllPayments();
	}

	@SneakyThrows
	@Incoming("uniandes-proof")
	@Transactional
	public Uni<Void> generateOrder(String generatePayment) {

		GeneratePayment getPayment = mapper.readValue(generatePayment, GeneratePayment.class);
		log.info("### Consume Kafka" + getPayment);

		paymentGenerate = paymentService.createPayment(getPayment);
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

	@Mutation
	@CircuitBreaker(failureRatio = 0.1, delay = 5000L)
	@Fallback(fallbackMethod = "fallbackGeneratePayment")
	public Payment generatePayment(Long idPayment) {
		Payment getPayment = paymentService.updatePayment(idPayment);
		log.info("### Payment Service" + getPayment);

		Order order = new Order();
		order.setIdPayment(getPayment.getIdPayment());
		order.setIdUser(getPayment.getUser().getIdUser());
		order.setAmount(getPayment.getAmount());

		log.info("### Call Rest Service" + order);
		OrderPayment orderPayment = clientPayUService.generatePayment(order);
		if(orderPayment.getReply()!=null) {
			getPayment.setTypePayment("PRE-PAGO");
			paymentRepository.save(getPayment);
		}
		return getPayment;
	}

	@SneakyThrows
	public Payment fallbackGeneratePayment(Long idPayment) {
		log.error("### Fallback Method:" + idPayment);
		Payment getPayment = paymentService.updatePostPayment(idPayment);
		log.error("### Fallback Payment" + getPayment);
		postPaymentEmitter.send(mapper.writeValueAsString(getPayment));
		return getPayment;
	}

//	@Scheduled(every = "5m")
//	public void updatePaymentStatus() {
//		if (!paymentService.getListPostPayment().isEmpty()) {
//			for (Payment payment : paymentService.getListPostPayment()) {
//				if (payment.getTypePayment().equals("POST-PAGO")) {
//					generatePayment(payment.getIdPayment());
//				}
//			}
//		}
//	}

}
