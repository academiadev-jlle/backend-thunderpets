package br.com.academiadev.thunderpets.model;

import br.com.academiadev.thunderpets.enums.TipoContato;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Builder
@Data
@NoArgsConstructor
public class Contato {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private TipoContato tipo;

    @NotNull
    @ManyToOne
    private Usuario usuario;

    @NotNull
    private String descricao;
}
