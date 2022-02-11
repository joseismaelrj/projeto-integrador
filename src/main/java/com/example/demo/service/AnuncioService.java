package com.example.demo.service;

import com.example.demo.dto.AnuncioExternoDTO;
import com.example.demo.dto.AnuncioInternoDTO;
import com.example.demo.dto.EstoqueInternoDTO;
import com.example.demo.dto.VendasProdutoDTO;
import com.example.demo.entity.Anuncio;
import com.example.demo.entity.Estoque;
import com.example.demo.enums.Tipos;
import com.example.demo.exception.AnunciosVaziosException;
import com.example.demo.interfaces.VendasProdutoInterface;
import com.example.demo.repository.AnuncioRepository;
import com.example.demo.repository.EstoqueRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class AnuncioService {

    @Autowired
    private AnuncioRepository anuncioRepository;

    private EstoqueRepository estoqueRepository;

    public List<AnuncioExternoDTO> listAnunciosValidos() {
        // Listar todos os anúncios válidos com quantidade atual maior
        // que zero e validade de pelo menos 3 semanas

        LocalDate dataValidadeMais3Semanas = LocalDate.now().plusWeeks(3);
        List<Anuncio> anuncios = this.anuncioRepository.findAllAnunciosWithStockAndDueDateValid(dataValidadeMais3Semanas);
        if (anuncios.isEmpty()) throw new AnunciosVaziosException("Nenhum anúncio encontrado.");
        List<AnuncioExternoDTO> anunciosDTO = AnuncioExternoDTO.converte(anuncios);
        return anunciosDTO;
    }

    public List<AnuncioExternoDTO> listAnunciosByCategory(Tipos categoria) {
        // Listar todos os anúncios válidos com quantidade atual maior
        // que zero e validade de pelo menos 3 semanas, por categoria
        // FRESCO, REFRIGERADO ou CONGELADO
        LocalDate dataValidadeMais3Semanas = LocalDate.now().plusWeeks(3);
        List<Anuncio> anuncios = this.anuncioRepository.findAllAnunciosByCategoryWithStockAndDueDateValid(dataValidadeMais3Semanas, categoria);
        if (anuncios.isEmpty()) throw new AnunciosVaziosException("Nenhum anúncio encontrado.");
        List<AnuncioExternoDTO> anunciosDTO = AnuncioExternoDTO.converte(anuncios);
        return anunciosDTO;
    }

    public AnuncioInternoDTO getAnuncioById(Long anuncioId) {
        // Pega o Anúncio pelo seu ID

        Optional<Anuncio> anuncioOptional = this.anuncioRepository.findById(anuncioId);
        if (anuncioOptional.isEmpty()) throw new AnunciosVaziosException("Nenhum anúncio encontrado.");
        Anuncio anuncio = anuncioOptional.get();
        AnuncioInternoDTO anuncioInternoDTO = AnuncioInternoDTO.converte(anuncio);
        return anuncioInternoDTO;
    }

    public AnuncioInternoDTO getAnuncioByIdOrdered(Long anuncioId, String orderBy) {
        // Pega o Anúncio pelo seu ID, ordenando pelos estoques

        Optional<Anuncio> anuncioOptional = this.anuncioRepository.findById(anuncioId);
        if (anuncioOptional.isEmpty()) throw new AnunciosVaziosException("Nenhum anúncio encontrado.");
        Anuncio anuncio = anuncioOptional.get();

        // Ordena os estoques
        AnuncioInternoDTO anuncioInternoDTO = AnuncioInternoDTO.converte(anuncio);

        List<Estoque> estoques = getEstoquesOrdenados(anuncio.getEstoques(), orderBy);

        // Converte para DTO
        List<EstoqueInternoDTO> estoquesDTO = EstoqueInternoDTO.converte(estoques);

        // Passa os estoques ordenados para anuncio
        anuncioInternoDTO.setEstoques(estoquesDTO);

        return anuncioInternoDTO;
    }

    private List<Estoque> getEstoquesOrdenados(Set<Estoque> estoques, String orderBy) {
        List<Estoque> estoqueList = new ArrayList<Estoque>(estoques);
        switch (orderBy) {
            case "L": // ordenados por lote
                estoqueList.sort(Comparator.comparing(Estoque::getId));
//                List<Estoque> estoqueList2 = estoques.stream().sorted((e1, e2) ->
//                        e1.getId().compareTo(e2.getId())).collect(Collectors.toList());
                break;
            case "C": // ordenados por quantidade atual
                estoqueList.sort(Comparator.comparing(Estoque::getQuantidadeAtual));
                break;
            case "F": // ordenados por data de vencimento
                estoqueList.sort(Comparator.comparing(Estoque::getDataValidade));
                break;
        }
        return estoqueList;
    }

    public List<VendasProdutoDTO>findAllVendasProdutoById(Long id) {
        List<VendasProdutoDTO> listaVendasDTO = new ArrayList<>();
        List<VendasProdutoInterface> listaVendas = anuncioRepository.findAllVendasProdutoById(id);
        listaVendas.forEach((item -> {
            VendasProdutoDTO result = new VendasProdutoDTO(item.getProduto(),item.getNomeProduto(),item.getTotalVendas(), item.getDataCriacao());
            listaVendasDTO.add(result);
        }));
        return listaVendasDTO;
    }
}
