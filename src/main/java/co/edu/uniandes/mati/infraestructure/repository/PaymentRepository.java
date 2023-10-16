package co.edu.uniandes.mati.infraestructure.repository;

import co.edu.uniandes.mati.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
