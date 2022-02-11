package com.example.demo.controller;

import com.example.demo.dto.AnuncioExternoDTO;
import com.example.demo.dto.AnuncioInternoDTO;
import com.example.demo.dto.VendasProdutoDTO;
import com.example.demo.entity.Anuncio;
import com.example.demo.entity.OrdemEntrada;
import com.example.demo.enums.Tipos;
import com.example.demo.exception.AnunciosVaziosException;
import com.example.demo.interfaces.VendasProdutoInterface;
import com.example.demo.service.AnuncioService;
import com.example.demo.service.CarrinhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fresh-products")
public class AnuncioController {

    @Autowired
    private AnuncioService anuncioService;

    @GetMapping("")
    public ResponseEntity<List<AnuncioExternoDTO>> listAnuncios() {
        List<AnuncioExternoDTO> listaAnuncios = anuncioService.listAnunciosValidos();
        return ResponseEntity.status(HttpStatus.OK).body(listaAnuncios);
    }

    @GetMapping("/list") // ?categoria=FRESCO
    public ResponseEntity<List<AnuncioExternoDTO>> listAnunciosPorTipo(@RequestParam("categoria") Tipos categoria) {
        List<AnuncioExternoDTO> listaAnuncios = anuncioService.listAnunciosByCategory(categoria);
        return ResponseEntity.status(HttpStatus.OK).body(listaAnuncios);
    }

//    @GetMapping("/list") // ?anuncio_id=FRESCO
    @GetMapping("/list/anuncio/{anuncio_id}") // /anuncio/1
    public ResponseEntity<AnuncioInternoDTO> getAnuncioPorId(@PathVariable("anuncio_id") Long anuncioId) {
        AnuncioInternoDTO anuncioInternoDTO = anuncioService.getAnuncioById(anuncioId);
        return ResponseEntity.status(HttpStatus.OK).body(anuncioInternoDTO);
    }

    @GetMapping("/list/anuncio") // ?anuncio_id=1&orderBy=L
    public ResponseEntity<AnuncioInternoDTO> getAnuncioPorIdOrdenados(@RequestParam("anuncioId") Long anuncioId, @RequestParam("orderBy") String orderBy ) {
        AnuncioInternoDTO anuncioInternoDTO = anuncioService.getAnuncioByIdOrdered(anuncioId, orderBy);
        return ResponseEntity.status(HttpStatus.OK).body(anuncioInternoDTO);
    }

    @GetMapping("/vendas/{id}")
    public ResponseEntity<List<VendasProdutoDTO>> getVendasProduto(@PathVariable Long id) {
        List<VendasProdutoDTO> vendasProdutoDTO = anuncioService.findAllVendasProdutoById(id);
        return  ResponseEntity.status(HttpStatus.OK).body(vendasProdutoDTO);
    }
}
