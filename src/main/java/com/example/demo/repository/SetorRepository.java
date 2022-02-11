package com.example.demo.repository;

import com.example.demo.entity.Setor;
import com.example.demo.interfaces.CapacidadeSetor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SetorRepository extends JpaRepository <Setor, Long>  {

    @Query(value = "SELECT s. nome, s.volume - SUM(a.volume * e.quantidade_atual) AS volume " +
            "FROM ordem_entrada oe INNER JOIN estoque e ON e.ordem_entrada_id = oe.id " +
            "INNER JOIN anuncio a ON a.id = e.anuncio_id " +
            "INNER JOIN setor s ON s.id = oe.setor_id " +
            "WHERE oe.setor_id = :setorid " +
            "GROUP BY s.nome, s.volume", nativeQuery = true)
    CapacidadeSetor getCapacidadeSetorById(Long setorid);

}
