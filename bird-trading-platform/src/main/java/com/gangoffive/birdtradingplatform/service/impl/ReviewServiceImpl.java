package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Address;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Review;
import com.gangoffive.birdtradingplatform.enums.FieldReviewTable;
import com.gangoffive.birdtradingplatform.enums.Operator;
import com.gangoffive.birdtradingplatform.enums.ReviewRating;
import com.gangoffive.birdtradingplatform.enums.SortReviewColumn;
import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.repository.ReviewRepository;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import com.gangoffive.birdtradingplatform.service.ReviewService;
import com.gangoffive.birdtradingplatform.util.*;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AppProperties appProperties;
    private final ProductSummaryService productSummaryService;
    private final AccountMapper accountMapper;

    @Override
    public ResponseEntity<?> getAllReviewByOrderId(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Account> account = accountRepository.findByEmail(authentication.getName());
        Optional<List<Review>> reviews = reviewRepository.findAllByAccountAndOrderDetail_Order_Id(account.get(), orderId);
        if (reviews.isPresent() && reviews.get().size() > 0) {
            return ResponseEntity.ok(reviews.get().stream().map(this::reviewToReviewDto).collect(Collectors.toList()));
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found reviews of this order id")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> addNewReviewByOrderDetailId(List<MultipartFile> multipartFiles, ReviewDto review) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Account> account = accountRepository.findByEmail(authentication.getName());
        Optional<OrderDetail> orderDetails = orderDetailRepository.findByIdAndOrder_PackageOrder_Account(review.getOrderDetailId(), account.get());
        log.info("review.getId(), {}", review.getId());
        log.info("account.get() {}", account.get().getEmail());
        log.info("orderDetails.isPresent() {}", orderDetails.isPresent());
        if (orderDetails.isPresent()) {
            Review reviewSave = new Review();
            reviewSave.setOrderDetail(orderDetails.get());
            reviewSave.setAccount(account.get());
            reviewSave.setComment(review.getDescription());
            reviewSave.setRating(ReviewRating.getReviewRatingByStar(review.getRating()));

            String originUrl = appProperties.getS3().getUrl();
            List<String> urlImgList = new ArrayList<>();
            if (multipartFiles != null && !multipartFiles.isEmpty()) {
                for (MultipartFile multipartFile : multipartFiles) {
                    String newFilename = FileNameUtils.getNewImageFileName(multipartFile);
                    urlImgList.add(originUrl + newFilename);
                    try {
                        S3Utils.uploadFile(newFilename, multipartFile.getInputStream());
                    } catch (Exception ex) {
                        ErrorResponse errorResponse = ErrorResponse.builder()
                                .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                                .errorMessage("Upload file fail")
                                .build();
                        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                    }
                }
            }
            String imgUrl = urlImgList.stream()
                    .collect(Collectors.joining(","));
            reviewSave.setImgUrl(imgUrl);
            Review save = reviewRepository.save(reviewSave);
            productSummaryService.updateReviewTotal(save.getOrderDetail().getProduct());
            productSummaryService.updateProductStar(save.getOrderDetail().getProduct());
            return ResponseEntity.ok(reviewToReviewDto(save));
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found reviews of this order detail id")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> getAllReviewByProductId(Long productId, int pageNumber) {
        if (pageNumber > 0) {
            pageNumber--;
            PageRequest pageRequest = PageRequest.of(
                    pageNumber,
                    PagingAndSorting.DEFAULT_PAGE_SIZE,
                    Sort.by(Sort.Direction.DESC, "reviewDate")
            );
            Optional<Page<Review>> reviews = reviewRepository.findAllByOrderDetail_Product_Id(productId, pageRequest);
            return getPageNumberWrapperWithReviews(reviews, true, false);
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> getAllReviewByShopOwner(ReviewShopOwnerFilterDto reviewFilter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Account> account = accountRepository.findByEmail(authentication.getName());
        Long shopId = account.get().getShopOwner().getId();
        if (reviewFilter.getPageNumber() > 0) {
            int pageNumber = reviewFilter.getPageNumber() - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
            PageRequest pageRequestWithSort = null;
            if (reviewFilter.getSortDirection() != null
                    && !reviewFilter.getSortDirection().getSort().isEmpty()
                    && !reviewFilter.getSortDirection().getField().isEmpty()
            ) {
                if (
                        !SortReviewColumn.checkField(reviewFilter.getSortDirection().getField())
                ) {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                            .errorMessage("Not found this field in sort direction.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                }
                if (reviewFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
                    pageRequestWithSort = getPageRequest(reviewFilter, pageNumber, Sort.Direction.ASC);
                } else if (reviewFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
                    pageRequestWithSort = getPageRequest(reviewFilter, pageNumber, Sort.Direction.DESC);
                } else {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                            .errorMessage("Not found this direction.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                }
            }

            if (
                    reviewFilter.getReviewSearchInfo().getField().isEmpty()
                            && reviewFilter.getReviewSearchInfo().getValue().isEmpty()
                            && reviewFilter.getReviewSearchInfo().getOperator().isEmpty()
                            && reviewFilter.getSortDirection().getField().isEmpty()
                            && reviewFilter.getSortDirection().getSort().isEmpty()
            ) {
                log.info("all no");
                return filterAllReviewAllFieldEmpty(shopId, pageRequest);
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().isEmpty()
                            && reviewFilter.getReviewSearchInfo().getValue().isEmpty()
                            && reviewFilter.getReviewSearchInfo().getOperator().isEmpty()
                            && !reviewFilter.getSortDirection().getField().isEmpty()
                            && !reviewFilter.getSortDirection().getSort().isEmpty()
            ) {
                log.info("with sort");
                return filterAllReviewAllFieldEmpty(shopId, pageRequestWithSort);
            }

            if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.ID.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterReviewByIdEqual(reviewFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.ORDER_DETAIL_ID.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterReviewByOrderDetailIdEqual(reviewFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.CUSTOMER_NAME.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
                            && reviewFilter.getSortDirection().getField().isEmpty()
                            && reviewFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterReviewByCustomerNameContain(reviewFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.CUSTOMER_NAME.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterReviewByCustomerNameContain(reviewFilter, shopId, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.PRODUCT_NAME.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
                            && reviewFilter.getSortDirection().getField().isEmpty()
                            && reviewFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterReviewByProductNameContain(reviewFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.PRODUCT_NAME.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterReviewByProductNameContain(reviewFilter, shopId, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.RATING.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
                            && reviewFilter.getSortDirection().getField().isEmpty()
                            && reviewFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterReviewByRatingGreaterThanOrEqual(reviewFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.RATING.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterReviewByRatingGreaterThanOrEqual(reviewFilter, shopId, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.REVIEW_DATE.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
                            && reviewFilter.getSortDirection().getField().isEmpty()
                            && reviewFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(reviewFilter.getReviewSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterReviewByReviewDateGreaterThanOrEqual(shopId, dateRange, pageRequest);
                    } else {
                        return filterReviewByReviewDateFromTo(shopId, dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    reviewFilter.getReviewSearchInfo().getField().equals(FieldReviewTable.REVIEW_DATE.getField())
                            && !reviewFilter.getReviewSearchInfo().getValue().isEmpty()
            ) {
                if (reviewFilter.getReviewSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(reviewFilter.getReviewSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterReviewByReviewDateGreaterThanOrEqual(shopId, dateRange, pageRequestWithSort);
                    } else {
                        return filterReviewByReviewDateFromTo(shopId, dateRange, pageRequestWithSort);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                        .errorMessage("Review filter is not correct.")
                        .build();
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        } else {
            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                    "Page number cannot less than 1");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> getReviewByReviewId(Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Account> account = accountRepository.findByEmail(authentication.getName());
        Long shopId = account.get().getShopOwner().getId();
        Optional<Review> review = reviewRepository.findByIdAndOrderDetail_Product_ShopOwner_Id(reviewId, shopId);
        if (review.isPresent()) {
            return ResponseEntity.ok(reviewToReviewDetailShopOwnerDto(review.get()));
        } else {
            ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                    "Review id not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<?> filterReviewByReviewDateFromTo(
            Long shopId,
            DateRangeDto dateRange,
            PageRequest pageRequest
    ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<Review>> reviews = reviewRepository.findAllByReviewDateBetweenAndOrderDetail_Product_ShopOwner_Id(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                calendar.getTime(),
                shopId,
                pageRequest
        );

        if (reviews.isPresent()) {
            return getPageNumberWrapperWithReviews(reviews, false, true);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found review have review date between this range.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterReviewByReviewDateGreaterThanOrEqual(
            Long shopId,
            DateRangeDto dateRange,
            PageRequest pageRequest
    ) {
        Optional<Page<Review>> reviews = reviewRepository.findAllByReviewDateGreaterThanEqualAndOrderDetail_Product_ShopOwner_Id(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                shopId,
                pageRequest
        );

        if (reviews.isPresent()) {
            return getPageNumberWrapperWithReviews(reviews, false, true);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found review have review date greater than or equal.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterReviewByRatingGreaterThanOrEqual(
            ReviewShopOwnerFilterDto reviewFilter,
            Long shopId,
            PageRequest pageRequest
    ) {
        Optional<Page<Review>> reviews = reviewRepository.findAllByRatingGreaterThanEqualAndOrderDetail_Product_ShopOwner_Id(
                ReviewRating.getReviewRatingByStar(Integer.parseInt(reviewFilter.getReviewSearchInfo().getValue())),
                shopId,
                pageRequest
        );

        if (reviews.isPresent()) {
            return getPageNumberWrapperWithReviews(reviews, false, true);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found review have review rating greater or equal this value.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterReviewByProductNameContain(
            ReviewShopOwnerFilterDto reviewFilter,
            Long shopId,
            PageRequest pageRequest
    ) {
        Optional<Page<Review>> reviews = reviewRepository.findAllByOrderDetail_Product_NameLikeAndOrderDetail_Product_ShopOwner_Id(
                "%" + reviewFilter.getReviewSearchInfo().getValue() + "%",
                shopId,
                pageRequest
        );

        if (reviews.isPresent()) {
            return getPageNumberWrapperWithReviews(reviews, false, true);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this product name.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterReviewByCustomerNameContain(
            ReviewShopOwnerFilterDto reviewFilter,
            Long shopId,
            PageRequest pageRequest
    ) {
        Optional<Page<Review>> reviews = reviewRepository.findAllByAccount_FullNameLikeAndOrderDetail_Product_ShopOwner_Id(
                "%" + reviewFilter.getReviewSearchInfo().getValue() + "%",
                shopId,
                pageRequest
        );

        if (reviews.isPresent()) {
            return getPageNumberWrapperWithReviews(reviews, false, true);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this customer name.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterReviewByOrderDetailIdEqual(
            ReviewShopOwnerFilterDto reviewFilter,
            Long shopId,
            PageRequest pageRequest
    ) {
        Optional<Page<Review>> reviews = reviewRepository.findByOrderDetail_IdAndOrderDetail_Product_ShopOwner_Id(
                Long.valueOf(reviewFilter.getReviewSearchInfo().getValue()),
                shopId,
                pageRequest
        );

        if (reviews.isPresent()) {
            return getPageNumberWrapperWithReviews(reviews, false, true);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this order detail id.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterReviewByIdEqual(
            ReviewShopOwnerFilterDto reviewFilter,
            Long shopId,
            PageRequest pageRequest
    ) {
        Optional<Page<Review>> reviews = reviewRepository.findByIdAndOrderDetail_Product_ShopOwner_Id(
                Long.valueOf(reviewFilter.getReviewSearchInfo().getValue()),
                shopId,
                pageRequest
        );

        if (reviews.isPresent()) {
            return getPageNumberWrapperWithReviews(reviews, false, true);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this review id.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterAllReviewAllFieldEmpty(Long shopId, PageRequest pageRequest) {
        Optional<Page<Review>> reviews = reviewRepository.findAllByOrderDetail_Product_ShopOwner_Id(
                shopId,
                pageRequest
        );

        if (reviews.isPresent()) {
            return getPageNumberWrapperWithReviews(reviews, false, true);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                    .errorMessage("Not found review in shop.")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }


    private PageRequest getPageRequest(
            ReviewShopOwnerFilterDto reviewFilter,
            int pageNumber,
            Sort.Direction sortDirection) {
        return PageRequest.of(
                pageNumber,
                PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                Sort.by(sortDirection,
                        SortReviewColumn.getColumnByField(reviewFilter.getSortDirection().getField())
                )
        );
    }

    private ResponseEntity<PageNumberWrapper<?>> getPageNumberWrapperWithReviews(
            Optional<Page<Review>> reviews,
            boolean isReviewDto,
            boolean isReviewShopOwnerDto
    ) {
        if (isReviewDto) {
            List<ReviewDto> reviewList = reviews.get().stream()
                    .map(this::reviewToReviewDto)
                    .toList();
            PageNumberWrapper<ReviewDto> result = new PageNumberWrapper<>(
                    reviewList,
                    reviews.get().getTotalPages(),
                    reviews.get().getTotalElements()
            );
            return ResponseEntity.ok(result);
        } else if (isReviewShopOwnerDto) {
            List<ReviewShopOwnerDto> reviewShopOwnerList = reviews.get().stream()
                    .map(this::reviewToReviewShopOwnerDto)
                    .toList();
            PageNumberWrapper<ReviewShopOwnerDto> result = new PageNumberWrapper<>(
                    reviewShopOwnerList,
                    reviews.get().getTotalPages(),
                    reviews.get().getTotalElements()
            );
            return ResponseEntity.ok(result);
        }
        return null;
    }

    private ReviewShopOwnerDto reviewToReviewShopOwnerDto(Review review) {
        return ReviewShopOwnerDto.builder()
                .id(review.getId())
                .orderDetailId(review.getOrderDetail().getId())
                .customerName(review.getAccount().getFullName())
                .productName(review.getOrderDetail().getProduct().getName())
                .rating(review.getRating().getStar())
                .reviewDate(review.getReviewDate().getTime())
                .build();
    }

    private ReviewDetailShopOwnerDto reviewToReviewDetailShopOwnerDto(Review review) {
        Account account = review.getAccount();
        Address shippingAddress = review.getOrderDetail().getOrder().getPackageOrder().getShippingAddress();
        AccountReviewDto accountReview = AccountReviewDto.builder()
                .id(account.getId())
                .fullName(account.getFullName())
                .imgUrl(account.getImgUrl())
                .address(shippingAddress.getAddress())
                .phone(shippingAddress.getPhone())
                .build();
        if (review.getImgUrl() != null && !review.getImgUrl().isEmpty()) {
            return ReviewDetailShopOwnerDto.builder()
                    .id(review.getId())
                    .account(accountReview)
                    .orderId(review.getOrderDetail().getOrder().getId())
                    .orderDetailId(review.getOrderDetail().getId())
                    .productId(review.getOrderDetail().getProduct().getId())
                    .productName(review.getOrderDetail().getProduct().getName())
                    .description(review.getComment())
                    .rating(review.getRating().getStar())
                    .imgUrl(Arrays.asList(review.getImgUrl().split(",")))
                    .reviewDate(review.getReviewDate().getTime())
                    .build();
        }
        return ReviewDetailShopOwnerDto.builder()
                .id(review.getId())
                .account(accountReview)
                .orderId(review.getOrderDetail().getOrder().getId())
                .orderDetailId(review.getOrderDetail().getId())
                .productId(review.getOrderDetail().getProduct().getId())
                .productName(review.getOrderDetail().getProduct().getName())
                .description(review.getComment())
                .rating(review.getRating().getStar())
                .reviewDate(review.getReviewDate().getTime())
                .build();
    }

    private ReviewDto reviewToReviewDto(Review review) {
        Account account = review.getAccount();
        AccountReviewDto accountReview = AccountReviewDto.builder()
                .id(account.getId())
                .fullName(account.getFullName())
                .imgUrl(account.getImgUrl())
                .build();
        if (review.getImgUrl() != null && !review.getImgUrl().isEmpty()) {
            return ReviewDto.builder()
                    .id(review.getId())
                    .account(accountReview)
                    .orderDetailId(review.getOrderDetail().getId())
                    .productId(review.getOrderDetail().getProduct().getId())
                    .description(review.getComment())
                    .rating(review.getRating().getStar())
                    .imgUrl(Arrays.asList(review.getImgUrl().split(",")))
                    .reviewDate(review.getReviewDate().getTime())
                    .build();
        }
        return ReviewDto.builder()
                .id(review.getId())
                .account(accountReview)
                .orderDetailId(review.getOrderDetail().getId())
                .productId(review.getOrderDetail().getProduct().getId())
                .description(review.getComment())
                .rating(review.getRating().getStar())
                .reviewDate(review.getReviewDate().getTime())
                .build();
    }
}
