package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("products")
    public List<ProductDto> retrieveAllProduct() {
        return productService.retrieveAllProduct();
    }

    @GetMapping("products/pages/{pagenumber}")
    public ResponseEntity<?> retrieveProductByPagenumber(@PathVariable int pagenumber) {
        return productService.retrieveProductByPagenumber(pagenumber);
    }

    @GetMapping("products/topproduct")
    public ResponseEntity<?> retrieveTopProduct() {
        List<ProductDto> result = productService.retrieveTopProduct();
        if(result == null){
            ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                    "Not found product top product: ");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("products/search")
    public List<ProductDto> findProductByName(@RequestParam String name) {
        return productService.findProductByName(name);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findProductById(@PathVariable Long id) {
        ProductDto product = productService.retrieveProductById(id);
        if (product == null) {
            ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                    "Not found product with id: " + id);
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(product);
    }
}
