package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/shop-owner")
@RequiredArgsConstructor
public class ShopOwnerController {
    private final ProductService productService;
    @GetMapping("/product")
    public ResponseEntity retrieveAllProduct() {
        return productService.retrieveProductByShopIdForSO(3);
    }

}
