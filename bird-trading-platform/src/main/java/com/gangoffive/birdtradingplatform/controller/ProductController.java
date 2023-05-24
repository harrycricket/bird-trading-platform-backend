package com.gangoffive.birdtradingplatform.controller;

import java.util.List;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	
	@GetMapping("/v1/products")
	public List<ProductDto> retrieveAllProdcuct(){
		return productService.retrieveAllProduct();
	}

	@GetMapping("v1/products/pages/{pagenumber}")
	public  List<ProductDto> retrieveProductByPagenumber(@PathVariable int pagenumber){
		return productService.retrieveProductByPagenumber(pagenumber);
	}
}
