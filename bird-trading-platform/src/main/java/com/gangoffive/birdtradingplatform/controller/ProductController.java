package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/topproduct")
    public List<ProductDto> retrieveTopProduct() {
        return productService.retrieveTopProduct();
    }

    @GetMapping("/search")
    public List<ProductDto> findProductByName(@RequestParam String name) {
        return productService.findProductByName(name);
    }
}
