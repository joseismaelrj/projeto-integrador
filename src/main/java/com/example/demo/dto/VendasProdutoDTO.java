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
    private Long produtoId;
    private String nomeProduto;
    private Integer totalVendas;
    private LocalDateTime dataCriacao;

}
