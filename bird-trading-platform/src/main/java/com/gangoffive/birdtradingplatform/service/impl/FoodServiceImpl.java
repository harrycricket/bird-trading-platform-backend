package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.common.ProductStatusConstant;
import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.dto.ProductShopDto;
import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.ResponseCode;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.FoodMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.FoodRepository;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.FoodService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {
    private final FoodRepository foodRepository;
    private final TagRepository tagRepository;
    private final FoodMapper foodMapper;
    private final ProductService productService;
    private final ProductSummaryService productSummaryService;
    private final AccountRepository accountRepository;

    @Override
    public List<FoodDto> retrieveAllFood() {
        List<FoodDto> lists = foodRepository.findAll().stream()
                .map(food -> (FoodDto) productService.ProductToDto(food)).
                collect(Collectors.toList());
        return lists;
    }

    @Override
    public ResponseEntity<?> retrieveFoodsByShopId(Long shopId, int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
                    Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastUpDated"));

            Optional<Page<Product>> pageAble = foodRepository.findByShopOwner_IdAndStatusIn(shopId,
                    ProductStatusConstant.LIST_STATUS_GET_FOR_USER, pageRequest);
            if (pageAble.isPresent()) {
                List<ProductDto> list = pageAble.get().stream()
                        .map(productService::ProductToDto)
                        .toList();
                PageNumberWraper<ProductDto> pageNumberWraper = new PageNumberWraper<>(list, pageAble.get().getTotalPages());
                return ResponseEntity.ok(pageNumberWraper);
            } else {
                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                        "Not found product in shop.");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> retrieveFoodByPageNumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest page = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE);
            Page<Food> pageAble = foodRepository.findAllByQuantityGreaterThanAndStatusIn(0,
                    ProductStatusConstant.LIST_STATUS_GET_FOR_USER, page);
            List<FoodDto> lists = pageAble.getContent().stream()
                    .map(food -> (FoodDto) productService.ProductToDto(food)).
                    collect(Collectors.toList());
            PageNumberWraper<FoodDto> result = new PageNumberWraper<>(lists, pageAble.getTotalPages());
            return ResponseEntity.ok(result);
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<FoodDto> findFoodByName(String name) {
        List<FoodDto> lists = foodRepository
                .findByNameLike("%" + name + "%")
                .get()
                .stream()
                .map(food -> (FoodDto) productService.ProductToDto(food)).collect(Collectors.toList());
        return lists;
    }

    @Override
    public void updateFood(FoodDto foodDto) {
        foodRepository.save(foodMapper.toModel(foodDto));
    }

    @Override
    public void deleteFoodById(Long id) {
        foodRepository.deleteById(id);
    }

    @Override
    public List<FoodDto> findTopFood() {
        List<Food> lists = foodRepository.findAllById(productSummaryService.getIdTopFood());
        if (lists != null) {
            List<FoodDto> listDto = lists.stream().map(food -> (FoodDto) productService.ProductToDto(food)).toList();
            return listDto;
        }
        return null;
    }

    @Override
    public ResponseEntity<?> getFoodByShop(int pageNumber) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        email = "YamamotoEmi37415@gmail.com"; //just for test after must delete
        var account = accountRepository.findByEmail(email);
        if (account.isPresent()) {
            ShopOwner shopOwner = account.get().getShopOwner();
            if (shopOwner != null) {
                long shopId = shopOwner.getId();
                if (pageNumber > 0) {
                    pageNumber--;
                }
                PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE);
                var listBird = foodRepository.findByShopOwner_IdAndStatusIn(shopId,
                        ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequest);
                if (listBird.isPresent()) {
                    List<ProductShopDto> listFoodShopDto = listBird.get().stream().map(bird -> this.foodToProductDto(bird)).toList();
                    PageNumberWraper result = new PageNumberWraper();
                    result.setLists(listFoodShopDto);
                    result.setTotalElement(listBird.get().getTotalElements());
                    result.setPageNumber(listBird.get().getTotalPages());
                    return ResponseEntity.ok(result);
                }
            } else {
                var error = ErrorResponse.builder().errorCode(ResponseCode.THIS_ACCOUNT_NOT_HAVE_SHOP.getCode() + "")
                        .errorMessage(ResponseCode.THIS_ACCOUNT_NOT_HAVE_SHOP.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        } else {
            throw new CustomRuntimeException("400", "Some thing went wrong");
        }
        return null;
    }

    private ProductShopDto foodToProductDto(Product bird) {
        if (bird != null) {
            return productService.productToProductShopDto(bird);
        }
        return null;
    }

}
