package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.ResponseCode;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.BirdMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.BirdRepository;
import com.gangoffive.birdtradingplatform.repository.ProductSummaryRepository;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.AuthenticationService;
import com.gangoffive.birdtradingplatform.service.BirdService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BirdServiceImpl implements BirdService {
    private final BirdRepository birdRepository;
    private final TagRepository tagRepository;
    private final BirdMapper birdMapper;
    private final ProductService productService;
    private final ProductSummaryService productSummaryService;
    private final ProductSummaryRepository productSummaryRepository;
    private final AccountRepository accountRepository;
    private AuthenticationService authenticationService;
    @Override
    public List<BirdDto> retrieveAllBird() {
        List<BirdDto> birds = birdRepository
                .findAll()
                .stream()
                .map(bird -> (BirdDto)productService.ProductToDto(bird))
                .collect(Collectors.toList());
        return birds;
    }

    @Override
    public ResponseEntity<?> retrieveBirdsByShopId(Long shopId, int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
                    Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastUpDated"));

            Optional<Page<Product>> pageAble = birdRepository.findByShopOwner_IdAndDeletedIsFalse(shopId, pageRequest);
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
    public ResponseEntity<?> retrieveBirdByPageNumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE);
            Page<Bird> pageAble = birdRepository.findAllByDeletedFalseAndQuantityGreaterThan(0, pageRequest);
            List<BirdDto> birds = pageAble.getContent()
                    .stream()
                    .map(bird -> (BirdDto)productService.ProductToDto(bird))
                    .collect(Collectors.toList());
            PageNumberWraper<BirdDto> result = new PageNumberWraper<>(birds, pageAble.getTotalPages());
            return ResponseEntity.ok(result);
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<BirdDto> findBirdByName(String name) {
        List<BirdDto> birds = birdRepository
                .findByNameLike("%" + name + "%")
                .get()
                .stream()
                .map(bird -> (BirdDto)productService.ProductToDto(bird))
                .collect(Collectors.toList());
        return birds;
    }

    @Override
    public void updateBird(BirdDto birdDto) {
        birdRepository.save(birdMapper.toModel(birdDto));
    }

    @Override
    public void deleteBirdById(Long id) {
        birdRepository.deleteById(id);
    }

    @Override
    public List<BirdDto> findTopBirdProduct() {
        List<Bird> listBirds = birdRepository.findAllById(productSummaryService.getIdTopBird());
        if(listBirds != null) {
            List<BirdDto> birdDtos = listBirds.stream().map(bird -> (BirdDto)productService.ProductToDto(bird)).toList();
            return birdDtos;
        }
        return null;
    }

    @Override
    public ResponseEntity<?> getAllBirdByShop(int pageNumber) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("emaoil {}", email);
//        email = "YamamotoEmi37415@gmail.com"; //just for test after must delete
        var account = accountRepository.findByEmail(email);
        if(account.isPresent()) {
            ShopOwner shopOwner = account.get().getShopOwner();
            if(shopOwner != null) {
                long shopId = shopOwner.getId();
                if(pageNumber > 0){
                    pageNumber--;
                }
                PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE);
                var listBird = birdRepository.findByShopOwner_IdAndHiddenIsFalse(shopId, pageRequest);
                if(listBird.isPresent()) {
                    List<ProductShopDto> listBirdShopDto = listBird.get().stream().map(bird ->  this.birdToProductDto(bird)).toList();
                    PageNumberWraper resutl = new PageNumberWraper();
                    resutl.setLists(listBirdShopDto);
                    resutl.setTotalProduct(listBird.get().getTotalElements());
                    resutl.setPageNumber(listBird.get().getTotalPages());
                    return ResponseEntity.ok(resutl);
                }
            }else {
                var error = ErrorResponse.builder().errorCode(ResponseCode.THIS_ACCOUNT_NOT_HAVE_SHOP.getCode()+"")
                        .errorMessage(ResponseCode.THIS_ACCOUNT_NOT_HAVE_SHOP.getMessage()).build();
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        }else {
            throw new CustomRuntimeException("400", "Some thing went wrong");
        }
        return null;
    }

    private ProductShopDto birdToProductDto(Product bird) {
        if(bird != null) {
            return productService.productToProductShopDto(bird);
        }
        return null;
    }

}
