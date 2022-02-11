package com.example.demo.repository;

import com.example.demo.entity.Anuncio;
import com.example.demo.entity.Estoque;
import com.example.demo.enums.Tipos;
import com.example.demo.interfaces.CapacidadeSetor;
import com.example.demo.interfaces.VendasProdutoInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;


@Repository
public interface AnuncioRepository extends JpaRepository <Anuncio, Long> {

    @Query("SELECT distinct a " +
            "from Anuncio a " +
            "JOIN fetch a.estoques estoques " +
            "where estoques.quantidadeAtual > 0 and estoques.dataValidade > ?1") // '2022-02-23'
    List<Anuncio> findAllAnunciosWithStockAndDueDateValid(LocalDate dataValidade);

    @Query("SELECT distinct a " +
            "from Anuncio a " +
            "JOIN fetch a.estoques estoques " +
            "where a.tipo = ?2 and estoques.quantidadeAtual > 0 and estoques.dataValidade > ?1") // '2022-02-23'
    List<Anuncio> findAllAnunciosByCategoryWithStockAndDueDateValid(LocalDate dataValidade, Tipos categoria);

    @Query(value = "select anuncio.id from anuncio " +
            "inner join estoque on anuncio.id = estoque.anuncio_id " +
            "where anuncio.id = :id " +
            "and estoque.quantidade_atual > :quantidade " +
            "and estoque.data_validade > curdate() + 21", nativeQuery = true)
    Long findAnuncioByIdStockAndDateValid(Long id, Integer quantidade);

//    @Query(value = "SELECT a.vendedor_id as vendedor, p.id as produto, p.nome as nomeProduto, sum(i.quantidade) AS totalVendas, c.data_criacao as dataCriacao" +
//            "From anuncio AS a " +
//            "INNER JOIN produto AS p ON p.id = a.produto_id " +
//            "INNER JOIN item_carrinho AS i ON i.anuncio_id = a.id " +
//            "INNER JOIN carrinho AS c ON c.id = i.carrinho_id " +
//            "WHERE a.vendedor_id = :vendedorid and status = 0 " +
//            "GRoUP BY a.id, p.id, p.nome, c.data_criacao", nativeQuery = true)
    @Query(value = "SELECT a.vendedor_id as vendedor, p.id as produto, p.nome as nomeProduto, sum(i.quantidade) AS totalVendas, c.data_criacao as dataCriacao " +
        "From anuncio AS a " +
        "INNER JOIN produto AS p ON p.id = a.produto_id " +
        "INNER JOIN item_carrinho AS i ON i.anuncio_id = a.id " +
        "INNER JOIN carrinho AS c ON c.id = i.carrinho_id " +
        "WHERE a.vendedor_id = :vendedorid and status = 0 " +
        "GRoUP BY a.id, p.id, p.nome, c.data_criacao", nativeQuery = true)
    List<VendasProdutoInterface> findAllVendasProdutoById(Long vendedorid);
}
