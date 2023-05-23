package com.gangoffive.birdtradingplatform.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	
	public List<Product> retrieveAllProduct(){
		PageRequest page = PageRequest.of(0, 8);
		Page<Product> firstPage =  productRepository.findAll(page);
		return firstPage.getContent();
	}
	
}
