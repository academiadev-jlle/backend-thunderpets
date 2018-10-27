package br.com.academiadev.thunderpets.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class Foto {

    @Id
    @GeneratedValue
    private UUID id;

    @Lob
    @NotNull
    private byte[] image;

    @ManyToOne
    private Pet pet;
}
