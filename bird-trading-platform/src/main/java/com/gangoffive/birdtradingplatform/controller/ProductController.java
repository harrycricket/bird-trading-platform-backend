package com.gangoffive.birdtradingplatform.controller;

import java.util.List;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	
	@GetMapping("/products")
	public List<ProductDto> retrieveAllProdcuct() {
		return productService.retrieveAllProduct();
	}

	@GetMapping("/products/pages/{pagenumber}")
	public  List<ProductDto> retrieveProductByPagenumber(@PathVariable int pagenumber) {
		return productService.retrieveProductByPagenumber(pagenumber);
	}
}
