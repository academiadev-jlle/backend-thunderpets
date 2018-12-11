package br.com.academiadev.thunderpets.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class LoginSocial {

    @Id
    @GeneratedValue
    private UUID id;

    private String idSocial;

    @ManyToOne
    private Usuario usuario;
}
