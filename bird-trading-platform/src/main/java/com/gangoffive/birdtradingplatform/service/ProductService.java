package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductDto> retrieveAllProduct();

    ResponseEntity<?> retrieveProductByPageNumber(int pageNumber);

    double CalculationRating(List<OrderDetail> orderDetails);

    double CalculateSaleOff(List<PromotionShop> listPromotion, double price);

    List<ProductDto> findProductByName(String name);

    List<ProductCartDto> retrieveTopProduct();

    List<ProductCartDto> listModelToDto(List<Product> products);

    ResponseEntity<?> retrieveProductById(Long id);

    double CalculateDiscountedPrice(double price, double saleOff);

    ProductDto ProductToDto(Product product);

    ResponseEntity<?> retrieveProductByListId(long[] ids);

    ResponseEntity<?> retrieveProductByShopId(long shopId, int pageNumber);

    ProductCartDto productToProductCart(Product product);

    ResponseEntity<?> filter(ProductFilterDto filterDto);

    ResponseEntity<?> addNewProduct(
            List<MultipartFile> multipartImgList,
            MultipartFile multipartVideo,
            ProductShopOwnerDto productShopOwnerDto
    );

    ProductShopDto productToProductShopDto(Product product);

    ResponseEntity<?> updateListProductStatus(ChangeStatusListIdDto changeStatusListIdDto);

    ResponseEntity<?> updateListProductQuantity(List<ProductQuantityShopChangeDto> listProductChange);

    ResponseEntity<?> filterAllProduct(ProductShopOwnerFilterDto productFilter, boolean isShopOwner, boolean isAdmin);

    ResponseEntity<?> getProductDetailForShop(long productId);

    ResponseEntity<?> updateProduct(
            List<MultipartFile> multipartImgList,
            MultipartFile multipartVideo,
            ProductUpdateDto productUpdate
    );

    ResponseEntity<?> getProductRelevantBaseOnId(long productId);
}

