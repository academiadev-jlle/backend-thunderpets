package br.com.academiadev.thunderpets.service;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.exception.PetNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface PetService {

    Page<PetDTO> buscar(LocalDate dataAchado,
                        LocalDate dataRegistro,
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
                        String campoOrdenacao,
                        boolean ativo);

    PetDTO buscarPorId(UUID id) throws PetNaoEncontradoException;

    PetDTO salvar(PetDTO petDTO);

    void excluir(UUID id);
}
