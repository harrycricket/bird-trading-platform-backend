package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import com.gangoffive.birdtradingplatform.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> retrieveProductByPagenumber(@PathVariable int pagenumber) {
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

    @GetMapping("/{id}")
    public ResponseEntity<?> findProductById(@PathVariable Long id){
        ProductDto product = productService.retrieveProductById(id);
        if(product == null){
            ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                    "Not found product with id: " + id);
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(product);
    }
}
