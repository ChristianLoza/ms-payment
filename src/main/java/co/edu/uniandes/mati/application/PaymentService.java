package co.edu.uniandes.mati.application;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
	PaymentRepository paymentRepository;
	@Inject
	UserRepository userRepository;
	public Uni<List<Payment>> getAllPayments() {
		return Uni.createFrom().item(paymentRepository.findAll(Sort.by(Sort.Direction.DESC)));
	}

	public Uni<Payment> createPayment(Payment payment) {
		return Uni.createFrom().item(paymentRepository.save(payment));

	}

	public void updatePayment(Long idPayment) {
		Optional<Payment> getPayment = paymentRepository.findById(idPayment);
	}


	public Payment createPayment(GeneratePayment getPayment) {
		Payment payment = new Payment();
		payment.setIdTest(getPayment.getIdTest());
		payment.setAmount(new BigDecimal("125.000"));
		payment.setStatus(Boolean.TRUE);

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

}
