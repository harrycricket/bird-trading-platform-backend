//package com.gangoffive.birdtradingplatform.listener;
//
//import com.gangoffive.birdtradingplatform.entity.Product;
//import com.gangoffive.birdtradingplatform.entity.ProductSummary;
//import com.gangoffive.birdtradingplatform.repository.ProductSummaryRepository;
//import jakarta.persistence.*;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
//@Slf4j
//@Component
//@Transactional
//public class ProductEntityListener {
////    @PersistenceContext
////    private EntityManager entityManager;
//    @PreUpdate
//    public void productUpdated(Product product) {
//        if (product.isDeleted()) {
//            ProductSummary productSummary = product.getProductSummary();
//            log.info("Before delete {}", productSummary);
//            if (productSummary != null) {
//                productSummary.setDeleted(true);
//                log.info("After delete {}", productSummary);
////                entityManager.merge(productSummary);
////                entityManager.flush();
//            }
//        }
//    }
//}
