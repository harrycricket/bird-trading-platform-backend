package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.common.ProductStatusConstant;
import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.dto.ProductShopDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.ResponseCode;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.AccessoryMapper;
import com.gangoffive.birdtradingplatform.repository.AccessoryRepository;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.AccessoryService;
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
public class AccessoryServiceImpl implements AccessoryService {
    private final AccessoryRepository accessoryRepository;
    private final TagRepository tagRepository;
    private final AccessoryMapper accessoryMapper;
    private final ProductService productService;
    private final ProductSummaryService productSummaryService;
    private final AccountRepository accountRepository;

    @Override
    public List<AccessoryDto> retrieveAllAccessory() {
        List<AccessoryDto> accessories = accessoryRepository
                .findAll()
                .stream()
                .map(accessory -> (AccessoryDto) productService.ProductToDto(accessory))
                .collect(Collectors.toList());
        return accessories;
    }

    @Override
    public ResponseEntity<?> retrieveAccessoriesByShopId(Long shopId, int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
                    Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastUpDated"));

            Optional<Page<Product>> pageAble = accessoryRepository.findByShopOwner_IdAndStatusIn(shopId,ProductStatusConstant.LIST_STATUS_GET_FOR_USER ,pageRequest);
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
    public ResponseEntity<?> retrieveAccessoryByPageNumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE);
            Page<Accessory> pageAble = accessoryRepository.findAllByQuantityGreaterThanAndStatusIn(0,
                    ProductStatusConstant.LIST_STATUS_GET_FOR_USER, pageRequest);
            List<AccessoryDto> accessories = pageAble.getContent()
                    .stream()
                    .map(accessory -> (AccessoryDto) productService.ProductToDto(accessory))
                    .collect(Collectors.toList());
            PageNumberWraper<AccessoryDto> result = new PageNumberWraper<>(accessories, pageAble.getTotalPages());
            return ResponseEntity.ok(result);
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<AccessoryDto> findAccessoryByName(String name) {
        List<AccessoryDto> accessories = accessoryRepository
                .findByNameLikeAndStatusInAndQuantityGreaterThanEqual("%" + name + "%",
                        ProductStatusConstant.LIST_STATUS_GET_FOR_USER,
                        ProductStatusConstant.QUANTITY_PRODUCT_FOR_USER)
                .get()
                .stream()
                .map(accessory -> (AccessoryDto) productService.ProductToDto(accessory))
                .collect(Collectors.toList());
        return accessories;
    }

    @Override
    public void updateAccessory(AccessoryDto accessoryDto) {
        accessoryRepository.save(accessoryMapper.toModel(accessoryDto));
    }

    @Override
    public void deleteAccessoryById(Long id) {
        accessoryRepository.deleteById(id);
    }

    @Override
    public List<AccessoryDto> findTopAccessories() {
        List<Accessory> lists = accessoryRepository.findAllById(productSummaryService.getIdTopAccessories());
        if (lists != null) {
            List<AccessoryDto> listDto = lists.stream().map(accessory -> (AccessoryDto) productService.ProductToDto(accessory)).toList();
            return listDto;
        }
        return null;
    }

    @Override
    public ResponseEntity<?> getAllAccessoryByShop(int pageNumber) {
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
                var listBird = accessoryRepository.findByShopOwner_IdAndStatusIn(shopId,
                        ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequest);
                if(listBird.isPresent()) {
                    List<ProductShopDto> listAccessoryShopDto = listBird.get().stream().map(bird ->  this.accessoryToProductDto(bird)).toList();
                    PageNumberWraper result = new PageNumberWraper();
                    result.setLists(listAccessoryShopDto);
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

    private ProductShopDto accessoryToProductDto(Product bird) {
        if (bird != null) {
            return productService.productToProductShopDto(bird);
        }
        return null;
    }

}
