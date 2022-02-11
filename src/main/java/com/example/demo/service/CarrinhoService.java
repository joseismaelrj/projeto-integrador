package com.example.demo.service;

import com.example.demo.dto.CarrinhoDTO;
import com.example.demo.dto.ItemCarrinhoDTO;
import com.example.demo.dto.PrecoTotalDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CarrinhoService {

    @Autowired
    CarrinhoRepository carrinhoRepository;

    @Autowired
    CompradorRepository compradorRepository;

    @Autowired
    AnuncioRepository anuncioRepository;

    @Autowired
    ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    EstoqueRepository estoqueRepository;

    public PrecoTotalDTO save(CarrinhoDTO carrinhoDTO) {
        // Faz validação da quantidade de estoque e data de validade
        if(!checkStockAndExpirationDate(carrinhoDTO)) {
            throw new RuntimeException("Estoque insuficiente ou validade a expirar");
        }

        // Salva o Carrinho no banco de dados
        Comprador comprador = this.compradorRepository.findById(carrinhoDTO.getComprador_id()).orElse(new Comprador());
        Carrinho carrinho = CarrinhoDTO.converte(carrinhoDTO, comprador);
        Carrinho carrinhoSalvo = this.carrinhoRepository.save(carrinho);

        // Salva o ItemCarrinho no banco de dados

        Set<ItemCarrinhoDTO> listaItemCarrinho = carrinhoDTO.getListaAnuncio();
        listaItemCarrinho.forEach((item -> {
            Anuncio anuncio = this.anuncioRepository.findById(item.getAnuncio_id()).orElse(new Anuncio());
            ItemCarrinho itemCarrinho = ItemCarrinhoDTO.converte(item, anuncio, carrinhoSalvo);
            decreaseStock(item.getAnuncio_id(), item.getQuantidade());
            this.itemCarrinhoRepository.save(itemCarrinho);
        }));

        // Instanciar precoTotalDTO e devolver pro controller
        return PrecoTotalDTO.builder()
                .precoTotal(this.itemCarrinhoRepository.totalPreco(carrinhoSalvo.getId()))
                .build();
    }

    public CarrinhoDTO getCarrinhoById(Long id) {
        // Retorna Carrinho pelo id
        Carrinho carrinho = this.carrinhoRepository.findById(id).orElse(new Carrinho());
        return CarrinhoDTO.converte(carrinho);
    }

    public CarrinhoDTO update(CarrinhoDTO carrinhoDTO, Long id) {
        // Faz validação da quantidade de estoque e data de validade
        if(!checkStockAndExpirationDate(carrinhoDTO)) {
            throw new RuntimeException("Estoque insuficiente ou validade a expirar");
        }

        // Atualiza o Carrinho no banco de dados
        Carrinho carrinho = this.carrinhoRepository.findById(id).orElse(new Carrinho());
        Carrinho carrinhoSalvo = this.carrinhoRepository.save(carrinho);

        // Atualiza o ItemCarrinho no banco de dados
        Set<ItemCarrinhoDTO> listaItemCarrinho = carrinhoDTO.getListaAnuncio();
        listaItemCarrinho.forEach((item -> {
            ItemCarrinho itemCarrinho = this.itemCarrinhoRepository.findById(item.getId()).orElse(new ItemCarrinho());
            Anuncio anuncio = this.anuncioRepository.findById(item.getAnuncio_id()).orElse(new Anuncio());
            itemCarrinho.setCarrinho(carrinhoSalvo);
            itemCarrinho.setAnuncio(anuncio);
            decreaseStock(item.getAnuncio_id(), item.getQuantidade());
            this.itemCarrinhoRepository.save(ItemCarrinhoDTO.converte(item, anuncio, carrinhoSalvo));
        }));
        return CarrinhoDTO.converte(carrinhoSalvo);
    }

    private Boolean checkStockAndExpirationDate(CarrinhoDTO dto) {
        // Flag para verificar se existe anúncio que atenda a esses requisitos
        for (ItemCarrinhoDTO item : dto.getListaAnuncio()) {
            Long idResultante = this.anuncioRepository.findAnuncioByIdStockAndDateValid(item.getAnuncio_id(), item.getQuantidade());
            if(idResultante == null) {
                return false;
            }
        }
        return true;
    }

    private void decreaseStock(Long anuncio_id, Integer quantidade) {
        // Decrementar estoque
        LocalDate dataValidade = LocalDate.now().plusDays(21);
        List<Estoque> listaEstoque = this.estoqueRepository.findAllAnuncio(anuncio_id);
        listaEstoque.sort(Comparator.comparing(Estoque::getDataValidade));
        listaEstoque.forEach((estoque -> {
            if(estoque.getDataValidade().compareTo(dataValidade) >= 0 && estoque.getQuantidadeAtual() >= quantidade){
                Integer quantidadeDecrementada = estoque.getQuantidadeAtual() - quantidade;
                estoque.setQuantidadeAtual(quantidadeDecrementada);
                this.estoqueRepository.save(estoque);
            }
        }));
    }

}

