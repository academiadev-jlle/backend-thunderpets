package br.com.academiadev.thunderpets.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Foto {

    @Id
    @GeneratedValue
    private UUID id;

    @Lob
    @NotNull
    private byte[] image;

    @ManyToOne(optional = false)
    private Pet pet;
}
