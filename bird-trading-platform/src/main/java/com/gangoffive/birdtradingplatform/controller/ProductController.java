package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.dto.ProductFilterDto;
import com.gangoffive.birdtradingplatform.dto.ShopFilterDto;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;
import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping()
    public List<ProductDto> retrieveAllProduct() {
        return productService.retrieveAllProduct();
    }

    @GetMapping("pages/{pageNumber}")
    public ResponseEntity<?> retrieveProductByPageNumber(@PathVariable int pageNumber) {
        return productService.retrieveProductByPageNumber(pageNumber);
    }

    @GetMapping("by-shop-id")
    public ResponseEntity retrieveAllProduct(@RequestParam int pageNumber, @RequestParam Long shopId) {
        return productService.retrieveProductByShopId(shopId, pageNumber);
    }

    @GetMapping("top-product")
    public ResponseEntity<?> retrieveTopProduct() {
        List<ProductDto> result = productService.retrieveTopProduct();
        if(result == null){
            ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                    "Not found product top product: ");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("search")
    public List<ProductDto> findProductByName(@RequestParam String name) {
        return productService.findProductByName(name);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findProductById(@PathVariable Long id) {
        return productService.retrieveProductById(id);
    }

    @GetMapping("id")
    public ResponseEntity<?> findProductByListId(@RequestParam("id") long[] ids ) {
        return productService.retrieveProductByListId(ids);
    }

    @GetMapping("filter")
    public ResponseEntity<?> filter(ProductFilterDto productFilterDto){
        log.info("dto {}", productFilterDto);
        return productService.filter(productFilterDto);
    }
    @GetMapping("filter-shop")
    public ResponseEntity<?> filterByShop(ShopFilterDto shopFilterDto){
        log.info("dto {}", shopFilterDto);
        return productService.filterByShop(shopFilterDto);
    }

    @GetMapping("/bird/shop-owner/{pageNumber}")
    public ResponseEntity retrieveAllProduct(@PathVariable int pageNumber) {
        return productService.retrieveProductByShopIdForSO(pageNumber);
    }
//    @GetMapping("/bird/shop-owner/{pageNumber}")
//    public ResponseEntity retrieveAllProduct(@PathVariable int pageNumber) {
//        return productService.retrieveProductByShopIdForSO(pageNumber);
//    }
//    @GetMapping("/bird/shop-owner/{pageNumber}")
//    public ResponseEntity retrieveAllProduct(@PathVariable int pageNumber) {
//        return productService.retrieveProductByShopIdForSO(pageNumber);
//    }
}
