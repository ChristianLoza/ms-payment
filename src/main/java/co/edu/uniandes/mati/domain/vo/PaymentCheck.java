package co.edu.uniandes.mati.domain.vo;


import lombok.Data;

import java.util.Date;

@Data
public class PaymentCheck {
    private Long idTest;
    private Boolean status;
    private Date paymentDate;
    private String email;
}
