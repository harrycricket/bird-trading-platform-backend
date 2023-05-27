package com.gangoffive.birdtradingplatform.controller;

import java.util.List;

import com.gangoffive.birdtradingplatform.dto.ProductDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gangoffive.birdtradingplatform.service.ProductService;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public List<ProductDto> retrieveAllProduct() {
        return productService.retrieveAllProduct();
    }

    @GetMapping("/pages/{pagenumber}")
    public List<ProductDto> retrieveProductByPagenumber(@PathVariable int pagenumber) {
        return productService.retrieveProductByPagenumber(pagenumber);
    }

    @GetMapping("/products/topproduct")
    public List<ProductDto> retrieveTopProduct() {
        return productService.retrieveTopProduct();
    }

    @GetMapping("/search")
    public List<ProductDto> findProductByName(@RequestParam String name) {
        return productService.findProductByName(name);
    }
}
