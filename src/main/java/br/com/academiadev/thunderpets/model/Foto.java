package br.com.academiadev.thunderpets.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Foto {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Pet pet;

    @Lob
    private byte[] image;
}
