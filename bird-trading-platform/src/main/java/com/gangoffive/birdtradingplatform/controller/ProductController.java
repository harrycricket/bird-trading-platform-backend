package com.gangoffive.birdtradingplatform.controller;

import java.util.List;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gangoffive.birdtradingplatform.service.impl.ProductServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
	private final ProductServiceImpl productServiceImpl;
	
	@GetMapping("/products")
	public List<ProductDto> retrieveAllProdcuct() {
		return productServiceImpl.retrieveAllProduct();
	}

	@GetMapping("/products/pages/{pagenumber}")
	public List<ProductDto> retrieveProductByPagenumber(@PathVariable int pagenumber) {
		return productServiceImpl.retrieveProductByPagenumber(pagenumber);
	}

	@GetMapping("/products/topproduct")
	public List<ProductDto> retrieveTopProduct(){
		return productServiceImpl.retrieveTopProduct();
	}
}
