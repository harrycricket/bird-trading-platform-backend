package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface ProductService {
    double CalculationRating(List<OrderDetail> orderDetails);

}
