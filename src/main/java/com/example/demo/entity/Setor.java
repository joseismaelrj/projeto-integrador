package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setor_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="id", nullable=false) // insertable = false, updatable = false)
    private Armazem armazem;

    @OneToMany(mappedBy="setor")
    private Set<OrdemEntrada> ordemEntradas;

}
