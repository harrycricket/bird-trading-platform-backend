package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.dto.ProductFilterDto;
import com.gangoffive.birdtradingplatform.dto.ProductShopOwnerDto;
import com.gangoffive.birdtradingplatform.enums.ContentType;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.util.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping("products")
    public List<ProductDto> retrieveAllProduct() {
        return productService.retrieveAllProduct();
    }

    @GetMapping("products/pages/{pageNumber}")
    public ResponseEntity<?> retrieveProductByPageNumber(@PathVariable int pageNumber) {
        return productService.retrieveProductByPagenumber(pageNumber);
    }

    @GetMapping("products/top-product")
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

    @GetMapping("products/{id}")
    public ResponseEntity<?> findProductById(@PathVariable Long id) {
        return productService.retrieveProductById(id);
    }

    @GetMapping("products/id")
    public ResponseEntity<?> findProductByListId(@RequestParam("id") long[] ids ) {
        return productService.retrieveProductByListId(ids);
    }

    @GetMapping("products/filter")
    public ResponseEntity<?> filter(ProductFilterDto productFilterDto){
        log.info("dto {}", productFilterDto);
        return productService.filter(productFilterDto);
    }

    @PostMapping("/product/add-new")
    public ResponseEntity<?> addNewProduct(
            @RequestParam("multipart") List<MultipartFile> multipartFiles,
            @RequestParam("multipart") MultipartFile multipartVideo,
//            @RequestParam("data") MultipartFile data,
            @RequestPart("data") ProductShopOwnerDto productShopOwnerDto
    ) {
        log.info("productShopOwnerDto {}", productShopOwnerDto);
        return productService.addNewProduct(multipartFiles, multipartVideo, productShopOwnerDto);
    }

}
