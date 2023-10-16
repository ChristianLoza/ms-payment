package co.edu.uniandes.mati.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PaymentOption {
    private String optionType;
}
