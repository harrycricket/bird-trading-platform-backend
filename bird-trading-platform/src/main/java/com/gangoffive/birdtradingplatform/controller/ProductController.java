package com.gangoffive.birdtradingplatform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	
	@GetMapping("/v1/products")
	public List<Product> retrieveFirstPage(){
		return productService.retrieveAllProduct();
	}
}
