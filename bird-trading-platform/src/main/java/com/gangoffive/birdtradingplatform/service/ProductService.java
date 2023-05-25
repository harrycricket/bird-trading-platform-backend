package com.gangoffive.birdtradingplatform.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.mapper.AccessoryMapper;
import com.gangoffive.birdtradingplatform.mapper.BirdMapper;
import com.gangoffive.birdtradingplatform.mapper.FoodMapper;
import com.gangoffive.birdtradingplatform.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.gangoffive.birdtradingplatform.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final ReviewRepository reviewRepository;
	private  final BirdMapper birdMapper;
	private final FoodMapper foodMapper;
	private final AccessoryMapper accessoryMapper;
	
	public List<ProductDto> retrieveAllProduct() {
		List<ProductDto> lists = productRepository.findAll().stream()
				.map(product -> {
					if(product instanceof Bird) {
						var bird = birdMapper.toDto((Bird) product);
						bird.setStar(this.CalculationRating(product.getOrderDetails()));
						return bird;
					}
					else if (product instanceof Food){
						var food =  foodMapper.toDto((Food)product);
						food.setStar(this.CalculationRating(product.getOrderDetails()));
						return food;
					}

					else if(product instanceof Accessory){
						var accessory =  accessoryMapper.toDto((Accessory) product);
						accessory.setStar(this.CalculationRating(product.getOrderDetails()));
						return accessory;
					}
					return null;
				}).
				collect(Collectors.toList());
		return lists;
	}

	public List<ProductDto> retrieveProductByPagenumber(int pageNumber) {
		PageRequest page = PageRequest.of(pageNumber,8);
		List<ProductDto> lists = productRepository.findAll(page).getContent().stream()
				.map(product -> {
					if(product instanceof Bird) {
						var bird = birdMapper.toDto((Bird) product);
						bird.setStar(this.CalculationRating(product.getOrderDetails()));
						bird.setDiscountRate(this.CalculateSaleOff(product.getPromotionShops(),bird.getPrice()));
						return bird;
					}
					else if (product instanceof Food){
						var food =  foodMapper.toDto((Food)product);
						food.setStar(this.CalculationRating(product.getOrderDetails()));
						food.setDiscountRate(this.CalculateSaleOff(product.getPromotionShops(),food.getPrice()));
						return food;
					}

					else if(product instanceof Accessory){
						var accessory =  accessoryMapper.toDto((Accessory) product);
						accessory.setStar(this.CalculationRating(product.getOrderDetails()));
						accessory.setDiscountRate(this.CalculateSaleOff(product.getPromotionShops(),accessory.getPrice()));
						return accessory;
					}
					return null;
				}).
				collect(Collectors.toList());
		return lists;
	}

	public int CalculationRating(List<OrderDetail> orderDetails) {
		if(orderDetails != null && orderDetails.size() != 0) {
			List<Long> orderDetailId = orderDetails.stream().map(id -> id.getId()).collect(Collectors.toList());
			List<Review> listReivew = reviewRepository.findAllByOrderDetailIdIn(orderDetailId).get();
			if(listReivew != null && listReivew.size() != 0) {
				int sumRating = listReivew.stream()
						.map(rating -> rating.getRating().ordinal())
						.reduce(0, Integer::sum);
				return (int) Math.ceil(sumRating / listReivew.size());
			}
		}
		return 4;
	}

	public double CalculateSaleOff(List<PromotionShop>  listPromotion, double price){
		if(listPromotion != null && listPromotion.size() != 0){
			List<Integer> saleOff = listPromotion.stream().map(s -> s.getRate()).collect(Collectors.toList());
			double priceDiscount = price;
			for(double sale : saleOff){
				priceDiscount = priceDiscount - priceDiscount * sale / 100;
			}
			double percentDiscount = Math.round((price - priceDiscount) / price * 100.0) / 100.0 ;
			return percentDiscount ;
		}
		return 0.0;
	}

}
