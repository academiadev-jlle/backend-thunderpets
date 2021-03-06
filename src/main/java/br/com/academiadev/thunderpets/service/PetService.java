package br.com.academiadev.thunderpets.service;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.dto.PetRespostaDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.exception.PetNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface PetService {

    Page<PetRespostaDTO> buscar(String nome,
                                LocalDate dataAchado,
                                Especie especie,
                                Porte porte,
                                Sexo sexo,
                                Status status,
                                Idade idade,
                                TipoPesquisaLocalidade tipoPesquisaLocalidade,
                                String cidade,
                                String estado,
                                String latitude,
                                String longitude,
                                Double raioDistancia,
                                Integer paginaAtual,
                                Integer tamanho,
                                Sort.Direction direcao,
                                String campoOrdenacao);

    PetRespostaDTO buscarPorId(UUID id) throws PetNaoEncontradoException;

    PetRespostaDTO salvar(PetDTO petDTO);

    void excluir(UUID id);
}
