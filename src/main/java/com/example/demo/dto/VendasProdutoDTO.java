package com.example.demo.dto;

import com.example.demo.interfaces.VendasProdutoInterface;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class VendasProdutoDTO {
    private Long vendedorId;
    private Long produtoId;
    private String nomeProduto;
    private Integer totalVendas;
    private LocalDateTime dataCriacao;

    public static VendasProdutoDTO converte (VendasProdutoInterface v) {
        VendasProdutoDTO dto = VendasProdutoDTO.builder()
                .vendedorId(v.getVendedor())
                .produtoId(v.getProduto())
                .nomeProduto(v.getNomeProduto())
                .totalVendas(v.getTotalVendas())
                .dataCriacao(v.getDataCriacao())
                .build();
        return dto;
    }

    public static List<VendasProdutoDTO> converteDTO(List<VendasProdutoInterface> v){
        return v.stream().map(VendasProdutoDTO::converte).collect(Collectors.toList());
    }

}
