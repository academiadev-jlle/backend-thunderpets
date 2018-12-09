package br.com.academiadev.thunderpets.model;

import lombok.*;
import org.hibernate.annotations.Type;

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
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] image;

    @ManyToOne(optional = false)
    private Pet pet;
}
