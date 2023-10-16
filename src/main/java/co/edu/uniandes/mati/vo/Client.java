package co.edu.uniandes.mati.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Client{
    private String clientId;
    private String name;
}
