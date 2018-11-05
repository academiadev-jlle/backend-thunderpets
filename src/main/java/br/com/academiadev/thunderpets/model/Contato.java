package br.com.academiadev.thunderpets.model;

import br.com.academiadev.thunderpets.enums.TipoContato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contato {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private TipoContato tipo;

    @NotNull
    @ManyToOne
    private Usuario usuario;

    @NotNull
    private String descricao;
}
