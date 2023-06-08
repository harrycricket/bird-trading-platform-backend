package com.gangoffive.birdtradingplatform.runner;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Runner implements CommandLineRunner {

    private final ProductRepository productRepository;

    private final ProductSummaryService productSummaryService;
    @Override
    @Transactional
    public void run(String... args) throws Exception {
//        List<Product> lists = productRepository.findAll();
//        log.info("list {}", lists.size());
//        for(Product pro : lists) {
//            productSummaryService.apply(pro);
////            productSummaryService.updateCategory(pro);
//        }
//        Product product = productRepository.findById(1L).get();
//        product.setDeleted(true);
//        productRepository.save(product);
    }

}


