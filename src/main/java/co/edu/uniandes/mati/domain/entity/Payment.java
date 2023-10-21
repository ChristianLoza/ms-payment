package co.edu.uniandes.mati.domain.entity;

import co.edu.uniandes.mati.domain.vo.PaymentOption;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name ="payment")
public class Payment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPayment;
    private Long idTest;
    private BigDecimal amount;
    private Boolean status;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idUser")
    private User user;
    @Embedded
    private PaymentOption paymentOption;
    private Date paymentDate;
}
