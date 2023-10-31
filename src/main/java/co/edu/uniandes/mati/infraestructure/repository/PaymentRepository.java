package co.edu.uniandes.mati.infraestructure.repository;

import co.edu.uniandes.mati.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByUserEmail(String email);


}
