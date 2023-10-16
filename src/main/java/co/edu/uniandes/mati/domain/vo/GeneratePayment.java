package co.edu.uniandes.mati.domain.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeneratePayment {
    private Long idTest;
    private String email;

    public GeneratePayment() {
    }

    public GeneratePayment(Long idTest, String email) {
        this.idTest = idTest;
        this.email = email;
    }
}
