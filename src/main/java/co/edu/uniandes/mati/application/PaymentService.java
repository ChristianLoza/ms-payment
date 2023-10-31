package co.edu.uniandes.mati.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import co.edu.uniandes.mati.infraestructure.client.ClientPayUService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import lombok.SneakyThrows;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.springframework.data.domain.Sort;

import co.edu.uniandes.mati.domain.entity.Payment;
import co.edu.uniandes.mati.domain.entity.User;
import co.edu.uniandes.mati.domain.vo.GeneratePayment;
import co.edu.uniandes.mati.domain.vo.PaymentOption;
import co.edu.uniandes.mati.infraestructure.repository.PaymentRepository;
import co.edu.uniandes.mati.infraestructure.repository.UserRepository;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Blocking
public class PaymentService {

	@Inject
	Logger log;
	@Inject
	PaymentRepository paymentRepository;
	@Inject
	UserRepository userRepository;

	@Inject
	ObjectMapper mapper;

	ClientPayUService clientPayUService;

	List<Payment> listPostPayment = new ArrayList<>();

	public Uni<List<Payment>> getAllPayments() {
		return Uni.createFrom().item(paymentRepository.findAll());
	}

	public Uni<Payment> createPayment(Payment payment) {
		return Uni.createFrom().item(paymentRepository.save(payment));

	}

	public Payment createPayment(GeneratePayment getPayment) {
		Payment payment = new Payment();
		payment.setIdTest(getPayment.getIdTest());
		payment.setAmount(new BigDecimal("125.000"));
		payment.setStatus(Boolean.FALSE);

		PaymentOption paymentOption = new PaymentOption();
		paymentOption.setOptionType("CREDIT-CARD");
		payment.setPaymentOption(paymentOption);
		payment.setPaymentDate(new Date());

		Optional<User> user = userRepository.findByEmail(getPayment.getEmail());
		User newUser;
		if (user.isPresent()) {
			newUser = user.get();
		} else {
			newUser = new User();
			newUser.setEmail(getPayment.getEmail());
		}
		payment.setUser(newUser);
		return paymentRepository.save(payment);
	}

	public Payment updatePayment(Long idPayment) {
		Payment getPayment = findPaymentById(idPayment);
		if (getPayment.getIdPayment() != null) {
			getPayment.setStatus(Boolean.TRUE);
			paymentRepository.save(getPayment);
			return getPayment;
		}
		return new Payment();
	}

	public Payment updatePostPayment(Long idPayment) {
		Payment getPayment = findPaymentById(idPayment);
		if (getPayment.getIdPayment() != null) {
			getPayment.setStatus(Boolean.TRUE);
			getPayment.setTypePayment("POST-PAGO");
			paymentRepository.save(getPayment);
			return getPayment;
		}
		return new Payment();
	}


	@Incoming("post-payment")
	@SneakyThrows
	public void updatePaymentStatus(String postPaymentObject) {
		System.out.println("Updated..." + postPaymentObject);
		Payment getPayment = mapper.readValue(postPaymentObject, Payment.class);
		getListPostPayment().add(getPayment);
	}

	public List<Payment> getListPostPayment() {
		return  this.listPostPayment;
	}

	private Payment findPaymentByEmail(String email) {
		List<Payment> getPayment = paymentRepository.findByUserEmail(email);
		if (!getPayment.isEmpty()) {
			getPayment.get(0).setStatus(Boolean.TRUE);
			return getPayment.get(0);
		}
		return new Payment();
	}

	private Payment findPaymentById(Long idPayment) {
		return paymentRepository.findById(idPayment).get();
	}

}
