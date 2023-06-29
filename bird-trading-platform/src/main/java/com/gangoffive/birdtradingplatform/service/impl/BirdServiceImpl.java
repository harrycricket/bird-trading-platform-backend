package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.common.ProductStatusConstant;
import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.dto.ProductShopDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.*;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.BirdMapper;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.service.AuthenticationService;
import com.gangoffive.birdtradingplatform.service.BirdService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
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
    private final TypeBirdRepository typeBirdRepository;
    private AuthenticationService authenticationService;

    @Override
    public List<BirdDto> retrieveAllBird() {
        List<BirdDto> birds = birdRepository
                .findAll()
                .stream()
                .map(bird -> (BirdDto) productService.ProductToDto(bird))
                .collect(Collectors.toList());
        return birds;
    }

    @Override
    public ResponseEntity<?> retrieveBirdsByShopId(Long shopId, int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                    Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastUpDated"));

            Optional<Page<Product>> pageAble = birdRepository.findByShopOwner_IdAndStatusIn(shopId,
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
    public ResponseEntity<?> retrieveBirdByPageNumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE);
            Page<Bird> pageAble = birdRepository.findAllByQuantityGreaterThanAndStatusIn(0,
                    ProductStatusConstant.LIST_STATUS_GET_FOR_USER, pageRequest);
            List<BirdDto> birds = pageAble.getContent()
                    .stream()
                    .map(bird -> (BirdDto) productService.ProductToDto(bird))
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
                .map(bird -> (BirdDto) productService.ProductToDto(bird))
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
        if (listBirds != null) {
            List<BirdDto> birdDtos = listBirds.stream().map(bird -> (BirdDto) productService.ProductToDto(bird)).toList();
            return birdDtos;
        }
        return null;
    }

    @Override
    public ResponseEntity<?> getAllBirdByShop(int pageNumber) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("email {}", email);
//        email = "YamamotoEmi37415@gmail.com"; //just for test after must delete
        var account = accountRepository.findByEmail(email);
        if (account.isPresent()) {
            ShopOwner shopOwner = account.get().getShopOwner();
            if (shopOwner != null) {
                long shopId = shopOwner.getId();
                if (pageNumber > 0) {
                    pageNumber--;
                }
                PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
                var listBird = birdRepository.findByShopOwner_IdAndStatusIn(shopId,
                        ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequest);
                if (listBird.isPresent()) {
                    List<ProductShopDto> listBirdShopDto = listBird.get().stream().map(bird -> this.birdToProductDto(bird)).toList();
                    PageNumberWraper result = new PageNumberWraper();
                    result.setLists(listBirdShopDto);
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

//    @Override
//    public ResponseEntity<?> filterAllBirdByShop(ProductShopOwnerFilterDto productFilter) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        Optional<Account> account = accountRepository.findByEmail(email);
//        Long shopId = account.get().getShopOwner().getId();
//        log.info("productFilter.getPageNumber() {}", productFilter.getPageNumber());
//        if (productFilter.getPageNumber() > 0) {
//            int pageNumber = productFilter.getPageNumber() - 1;
//            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE);
//            PageRequest pageRequestWithSort = null;
//            if (productFilter.getSortDirection() != null) {
//                if (!SortColumn.checkField(productFilter.getSortDirection().getField())) {
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Not found this field in sort direction.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                if (productFilter.getSortDirection().getSort().name() == Sort.Direction.ASC.name()) {
//                    pageRequestWithSort = PageRequest.of(
//                            pageNumber,
//                            PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
//                            Sort.by(Sort.Direction.ASC,
//                                    SortColumn.getColumnByField(productFilter.getSortDirection().getField())
//                            )
//                    );
//                } else {
//                    pageRequestWithSort = PageRequest.of(
//                            pageNumber,
//                            PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE,
//                            Sort.by(Sort.Direction.DESC,
//                                    SortColumn.getColumnByField(productFilter.getSortDirection().getField())
//                            )
//                    );
//                }
//            }
//
//            if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.ID.getField())
//                    && productFilter.getProductSearchInfo().getValue() != null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
//                    Optional<Page<Bird>> birds = birdRepository.findByIdAndShopOwner_IdAndStatusIn(
//                            Long.valueOf(productFilter.getProductSearchInfo().getValue()),
//                            shopId,
//                            ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER,
//                            pageRequest
//                    );
//
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Not found this id.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.NAME.getField())
//                            && productFilter.getProductSearchInfo().getValue() != null
//                            && productFilter.getSortDirection() == null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.LIKE.getOperator())) {
//                    String nameLike = "%" + productFilter.getProductSearchInfo().getValue() + "%";
//                    Optional<Page<Bird>> birds = birdRepository.findAllByNameLikeAndShopOwner_IdAndStatusIn(
//                            nameLike, shopId, ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequest
//                    );
//
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Not found this name.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.NAME.getField())
//                            && productFilter.getProductSearchInfo().getValue() != null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.LIKE.getOperator())) {
//                    String nameLike = "%" + productFilter.getProductSearchInfo().getValue() + "%";
//                    Optional<Page<Bird>> birds = birdRepository.findAllByNameLikeAndShopOwner_IdAndStatusIn(
//                            nameLike, shopId, ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequestWithSort
//                    );
//
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Not found this name.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.TYPE.getField())
//                            && productFilter.getProductSearchInfo().getValue() != null
//                            && productFilter.getSortDirection() == null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.LIKE.getOperator())) {
//                    List<TypeBird> typeBirdIds = typeBirdRepository.findAllByNameLike("%" + productFilter.getProductSearchInfo().getValue() + "%");
//                    Optional<Page<Bird>> birds = birdRepository.findAllByShopOwner_IdAndTypeBird_IdInAndStatusIn(
//                            shopId, typeBirdIds.stream().map(TypeBird::getId).collect(Collectors.toList()),
//                            ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequest
//                    );
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Not found this type name.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.TYPE.getField())
//                            && productFilter.getProductSearchInfo().getValue() != null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.LIKE.getOperator())) {
//                    List<TypeBird> typeBirdIds = typeBirdRepository.findAllByNameLike("%" + productFilter.getProductSearchInfo().getValue() + "%");
//                    Optional<Page<Bird>> birds = birdRepository.findAllByShopOwner_IdAndTypeBird_IdInAndStatusIn(
//                            shopId, typeBirdIds.stream().map(TypeBird::getId).collect(Collectors.toList()),
//                            ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequestWithSort
//                    );
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Not found this type name.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.PRICE.getField())
//                            && productFilter.getProductSearchInfo().getValue() != null
//                            && productFilter.getSortDirection() == null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
//                    Optional<Page<Bird>> birds = birdRepository.findAllByShopOwner_IdAndPriceGreaterThanEqualAndStatusIn(
//                            shopId, Double.parseDouble(productFilter.getProductSearchInfo().getValue()),
//                            ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequest
//                    );
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Do not have product greater than this price.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.PRICE.getField())
//                    && productFilter.getProductSearchInfo().getValue() != null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
//                    Optional<Page<Bird>> birds = birdRepository.findAllByShopOwner_IdAndPriceGreaterThanEqualAndStatusIn(
//                            shopId, Double.parseDouble(productFilter.getProductSearchInfo().getValue()),
//                            ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequestWithSort
//                    );
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Do not have product greater than this price.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            }  else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.DISCOUNTED_PRICE.getField())
//                            && productFilter.getProductSearchInfo().getValue() != null
//                            && productFilter.getSortDirection() == null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
//                    Optional<Page<Bird>> birds = birdRepository.findAllByShopOwner_IdAndProductSummary_DiscountedPriceGreaterThanEqualAndStatusIn(
//                            shopId, Double.parseDouble(productFilter.getProductSearchInfo().getValue()),
//                            ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequest
//                    );
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Do not have product greater than this price.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.DISCOUNTED_PRICE.getField())
//                    && productFilter.getProductSearchInfo().getValue() != null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
//                    Optional<Page<Bird>> birds = birdRepository.findAllByShopOwner_IdAndProductSummary_DiscountedPriceGreaterThanEqualAndStatusIn(
//                            shopId, Double.parseDouble(productFilter.getProductSearchInfo().getValue()),
//                            ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER, pageRequestWithSort
//                    );
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Do not have product greater than this discounted price.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.STATUS.getField())
//                            && productFilter.getProductSearchInfo().getValue() != null
//                            && productFilter.getSortDirection() == null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
//                    Optional<Page<Bird>> birds = birdRepository.findAllByShopOwner_IdAndStatus(
//                            shopId,
//                            ProductUpdateStatus.getProductUpdateStatusEnum(
//                                    Integer.parseInt(productFilter.getProductSearchInfo().getValue())
//                            ).getProductStatus(),
//                            pageRequest
//                    );
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Do not have product have this status.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else if (
//                    productFilter.getProductSearchInfo().getField().equals(FieldTable.STATUS.getField())
//                    && productFilter.getProductSearchInfo().getValue() != null
//            ) {
//                if (productFilter.getProductSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
//                    Optional<Page<Bird>> birds = birdRepository.findAllByShopOwner_IdAndStatus(
//                            shopId,
//                            ProductUpdateStatus.getProductUpdateStatusEnum(
//                                    Integer.parseInt(productFilter.getProductSearchInfo().getValue())
//                            ).getProductStatus(),
//                            pageRequestWithSort
//                    );
//                    if (birds.isPresent()) {
//                        List<ProductShopDto> listBirdShopDto = birds.get().stream().map(productService::productToProductShopDto).toList();
//                        PageNumberWraper result = new PageNumberWraper<>(listBirdShopDto, birds.get().getTotalPages(), birds.get().getTotalElements());
//                        return ResponseEntity.ok(result);
//                    }
//                    ErrorResponse errorResponse = ErrorResponse.builder()
//                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                            .errorMessage("Do not have product have this status.")
//                            .build();
//                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//                }
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
//                        .errorMessage("Not found this operator.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//            } else {
//                ErrorResponse errorResponse = ErrorResponse.builder()
//                        .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
//                        .errorMessage("Product filter is not correct.")
//                        .build();
//                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//            }
//        } else {
//            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
//                    "Page number cannot less than 1");
//            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//        }
//    }

    private ProductShopDto birdToProductDto(Product bird) {
        if (bird != null) {
            return productService.productToProductShopDto(bird);
        }
        return null;
    }

}
