package co.edu.uniandes.mati.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Data
public class PaymentOption {
    private String optionType;
}
