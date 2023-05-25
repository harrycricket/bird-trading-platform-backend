package com.gangoffive.birdtradingplatform.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.mapper.AccessoryMapper;
import com.gangoffive.birdtradingplatform.mapper.BirdMapper;
import com.gangoffive.birdtradingplatform.mapper.FoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
	private final BirdMapper birdMapper;
	private final FoodMapper foodMapper;
	private final AccessoryMapper accessoryMapper;
	
	public List<ProductDto> retrieveAllProduct(){
		List<ProductDto> lists = productRepository.findAll().stream()
				.map(product -> {
					if(product instanceof Bird)
						return birdMapper.toDto((Bird)product);
					else if (product instanceof Food)
						return foodMapper.toDto((Food)product);
					else if(product instanceof Accessory)
						return accessoryMapper.toDto((Accessory) product);
					return null;
				}).
				collect(Collectors.toList());
		return lists;
	}

	public List<ProductDto> retrieveProductByPagenumber(int pageNumber) {
		PageRequest page = PageRequest.of(pageNumber,8);
		List<ProductDto> lists = productRepository.findAll(page).getContent().stream()
				.map(product -> {
					if(product instanceof Bird)
						return birdMapper.toDto((Bird)product);
					else if (product instanceof Food)
						return foodMapper.toDto((Food)product);
					else if(product instanceof Accessory)
						return accessoryMapper.toDto((Accessory) product);
					return null;
				}).
				collect(Collectors.toList());
		return lists;
	}
}
