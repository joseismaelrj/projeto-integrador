package com.example.demo.interfaces;

import java.time.LocalDateTime;

public interface VendasProdutoInterface {
    Long getProduto();
    String getNomeProduto();
    Integer getTotalVendas();
    LocalDateTime getDataCriacao();
}
