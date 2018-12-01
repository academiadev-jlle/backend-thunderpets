package br.com.academiadev.thunderpets.service;

import br.com.academiadev.thunderpets.dto.PetDTO;
import br.com.academiadev.thunderpets.enums.*;
import br.com.academiadev.thunderpets.exception.PetNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.UUID;

public interface PetService {

    PetDTO buscarPorId(UUID id) throws PetNaoEncontradoException;

    Page<PetDTO> buscar(LocalDate dataAchado,
                         LocalDate dataRegistro,
                         Especie especie,
                         Porte porte,
                         Sexo sexo,
                         Status status,
                         Idade idade,
                         Integer paginaAtual,
                         Integer tamanho,
                         Sort.Direction direcao,
                         String campoOrdenacao,
                         boolean ativo);

    PetDTO salvar(PetDTO petDTO);

    void excluir(UUID id);
}
