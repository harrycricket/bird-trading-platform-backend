package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.DateRangeDto;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.dto.PromotionFilterDto;
import com.gangoffive.birdtradingplatform.entity.Promotion;
import com.gangoffive.birdtradingplatform.enums.FieldPromotionTable;
import com.gangoffive.birdtradingplatform.enums.Operator;
import com.gangoffive.birdtradingplatform.enums.PromotionType;
import com.gangoffive.birdtradingplatform.enums.SortPromotionColumn;
import com.gangoffive.birdtradingplatform.mapper.PromotionMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.PromotionRepository;
import com.gangoffive.birdtradingplatform.service.NotificationService;
import com.gangoffive.birdtradingplatform.service.PromotionService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import com.gangoffive.birdtradingplatform.util.MyUtils;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    @Override
    public ResponseEntity<?> getAllPromotion() {
        List<Promotion> promotions = promotionRepository.findAll();
        List<PromotionDto> promotionDtoList = promotions.stream()
                .filter(promotion -> promotion.getStartDate().compareTo(new Date()) <= 0
                        && promotion.getEndDate().after(new Date()))
                .map(promotion -> {
                    PromotionDto promotionDto = promotionMapper.toDto(promotion);
                    promotionDto.setEndDate(promotion.getEndDate().getTime());
                    return promotionDto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(promotionDtoList);
    }

    @Override
    public ResponseEntity<?> createPromotion(PromotionDto createPromotion) {
        if (createPromotion.getEndDate() - createPromotion.getStartDate() > 0) {
            Date startDate = new Date(createPromotion.getStartDate());

            // Define the desired timezone (GMT+7)
            TimeZone gmtPlus7TimeZone = TimeZone.getTimeZone("GMT+7");

            // Get the current date and time in the desired timezone
            Calendar calendar = Calendar.getInstance(gmtPlus7TimeZone);
            Date currentDate = calendar.getTime();
            log.info("date start {}", startDate.toString());
            log.info("date server {}", currentDate.toString());
            if (!startDate.before(currentDate)) {
                Promotion promotion = promotionMapper.dtoToModel(createPromotion);
                promotion.setStartDate(new Date(createPromotion.getStartDate()));
                promotion.setEndDate(new Date(createPromotion.getEndDate()));
                if (createPromotion.getType().equals(PromotionType.SHIPPING)) {
                    promotion.setDiscount(0);
                }
                promotion.setUsed(0);
                promotionRepository.save(promotion);
                SuccessResponse successResponse = SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.CREATED.value()))
                        .successMessage("Create promotion successfully.")
                        .build();
                //send notification for all user
                Optional<List<Long>> listUserId = accountRepository.findAllAccountIdInActive();
                if(listUserId.isPresent() && listUserId.get().size() > 0){
                    NotificationDto noti = new NotificationDto();
                    noti.setName(NotifiConstant.NEW_PROMOTION_NAME);
                    noti.setNotiText(String.format(NotifiConstant.NEW_PROMOTION_CONTENT, createPromotion.getType(),
                            MyUtils.formatDateToDDMMYYForm(startDate),MyUtils.formatDateToDDMMYYForm(new Date(createPromotion.getEndDate())),createPromotion.getUsageLimit()));
                    noti.setRole(NotifiConstant.NOTI_USER_ROLE);
                    notificationService.pushNotificationForListUserID(listUserId.get(), noti);
                }
                return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.CONFLICT))
                        .errorMessage("Start date no invalid")
                        .build();
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.CONFLICT))
                .errorMessage("End date < Start date")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @Override
    public ResponseEntity<?> filterAllPromotion(PromotionFilterDto promotionFilter) {
        if (promotionFilter.getPageNumber() > 0) {
            int pageNumber = promotionFilter.getPageNumber() - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
            PageRequest pageRequestWithSort = null;
            if (promotionFilter.getSortDirection() != null
                    && !promotionFilter.getSortDirection().getSort().isEmpty()
                    && !promotionFilter.getSortDirection().getField().isEmpty()
            ) {
                if (
                        !SortPromotionColumn.checkField(promotionFilter.getSortDirection().getField())
                ) {
                    return ResponseUtils.getErrorResponseNotFoundSortColumn();
                }
                if (promotionFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
                    pageRequestWithSort = getPageRequest(promotionFilter, pageNumber, Sort.Direction.ASC);
                } else if (promotionFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
                    pageRequestWithSort = getPageRequest(promotionFilter, pageNumber, Sort.Direction.DESC);
                } else {
                    return ResponseUtils.getErrorResponseNotFoundSortDirection();
                }
            }

            if (
                    promotionFilter.getPromotionSearchInfo().getField().isEmpty()
                            && promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getPromotionSearchInfo().getOperator().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                return filterAllPromotionAllFieldEmpty(pageRequest);
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().isEmpty()
                            && promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getPromotionSearchInfo().getOperator().isEmpty()
                            && !promotionFilter.getSortDirection().getField().isEmpty()
                            && !promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                return filterAllPromotionAllFieldEmpty(pageRequestWithSort);
            }

            if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.ID.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPromotionByIdEqual(promotionFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.NAME.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPromotionsByNameContain(promotionFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.NAME.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPromotionsByNameContain(promotionFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.DESCRIPTION.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPromotionsByDescriptionContain(promotionFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.DESCRIPTION.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPromotionsByDescriptionContain(promotionFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.DISCOUNT.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPromotionsByDiscountGreaterThan(promotionFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.DISCOUNT.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPromotionsByDiscountGreaterThan(promotionFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.MINIMUM_ORDER_VALUE.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPromotionsByMinimumOrderValueGreaterThan(promotionFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.MINIMUM_ORDER_VALUE.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPromotionsByMinimumOrderValueGreaterThan(promotionFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.USAGE_LIMIT.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPromotionsByUsageLimitGreaterThan(promotionFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.USAGE_LIMIT.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPromotionsByUsageLimitGreaterThan(promotionFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.USED.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPromotionsByUsedGreaterThan(promotionFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.USED.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPromotionsByUsedGreaterThan(promotionFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.PROMOTION_TYPE.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPromotionsByPromotionTypeContain(promotionFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.PROMOTION_TYPE.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPromotionsByPromotionTypeContain(promotionFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.START_DATE.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(promotionFilter.getPromotionSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterPromotionByStartDateGreaterThanOrEqual(dateRange, pageRequest);
                    } else {
                        return filterPromotionByStartDateFromTo(dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.START_DATE.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(promotionFilter.getPromotionSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterPromotionByStartDateGreaterThanOrEqual(dateRange, pageRequestWithSort);
                    } else {
                        return filterPromotionByStartDateFromTo(dateRange, pageRequestWithSort);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.END_DATE.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
                            && promotionFilter.getSortDirection().getField().isEmpty()
                            && promotionFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(promotionFilter.getPromotionSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterPromotionByEndDateGreaterThanOrEqual(dateRange, pageRequest);
                    } else {
                        return filterPromotionByEndDateFromTo(dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    promotionFilter.getPromotionSearchInfo().getField().equals(FieldPromotionTable.END_DATE.getField())
                            && !promotionFilter.getPromotionSearchInfo().getValue().isEmpty()
            ) {
                if (promotionFilter.getPromotionSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(promotionFilter.getPromotionSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterPromotionByEndDateGreaterThanOrEqual(dateRange, pageRequestWithSort);
                    } else {
                        return filterPromotionByEndDateFromTo(dateRange, pageRequestWithSort);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else {
                return ResponseUtils.getErrorResponseBadRequest("Promotion filter is not correct.");
            }

        } else {
            return ResponseUtils.getErrorResponseBadRequestPageNumber();
        }
    }

    private ResponseEntity<?> filterPromotionByEndDateFromTo(DateRangeDto dateRange, PageRequest pageRequest) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByEndDateBetween(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                calendar.getTime(),
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found promotion have end date between this range.");
    }

    private ResponseEntity<?> filterPromotionByEndDateGreaterThanOrEqual(DateRangeDto dateRange, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByEndDateGreaterThanEqual(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found promotion have end date greater than or equal.");
    }

    private ResponseEntity<?> filterPromotionByStartDateFromTo(DateRangeDto dateRange, PageRequest pageRequest) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByStartDateBetween(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                calendar.getTime(),
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found promotion have start date between this range.");
    }

    private ResponseEntity<?> filterPromotionByStartDateGreaterThanOrEqual(DateRangeDto dateRange, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByStartDateGreaterThanEqual(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }

        return ResponseUtils.getErrorResponseNotFound("Not found promotion have start date greater than or equal.");
    }

    private ResponseEntity<?> filterPromotionsByPromotionTypeContain(
            PromotionFilterDto promotionFilter, PageRequest pageRequest) {
        List<PromotionType> promotionTypes;
        if (Integer.parseInt(promotionFilter.getPromotionSearchInfo().getValue()) == 9) {
            promotionTypes = List.of(PromotionType.values());
        } else {
            promotionTypes = Arrays.asList(
                    PromotionType.getPromotionTypeBaseOnStatusCode(
                            Integer.parseInt(promotionFilter.getPromotionSearchInfo().getValue()) - 1
                    )
            );
        }

        Optional<Page<Promotion>> promotions = promotionRepository.findAllByTypeIn(
                promotionTypes,
                pageRequest
        );
        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found this promotion have promotion type.");
    }

    private ResponseEntity<?> filterPromotionsByUsedGreaterThan(
            PromotionFilterDto promotionFilter, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByUsedGreaterThanEqual(
                Integer.parseInt(promotionFilter.getPromotionSearchInfo().getValue()),
                pageRequest
        );
        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found this promotion have used greater than.");
    }

    private ResponseEntity<?> filterPromotionsByUsageLimitGreaterThan(
            PromotionFilterDto promotionFilter, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByUsageLimitGreaterThanEqual(
                Integer.parseInt(promotionFilter.getPromotionSearchInfo().getValue()),
                pageRequest
        );
        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found this promotion have usage limit greater than.");
    }

    private ResponseEntity<?> filterPromotionsByMinimumOrderValueGreaterThan(
            PromotionFilterDto promotionFilter, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByMinimumOrderValueGreaterThanEqual(
                Double.parseDouble(promotionFilter.getPromotionSearchInfo().getValue()),
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found this promotion have minimum order value greater than.");
    }

    private ResponseEntity<?> filterPromotionsByDiscountGreaterThan(
            PromotionFilterDto promotionFilter, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByDiscountGreaterThanEqual(
                Double.parseDouble(promotionFilter.getPromotionSearchInfo().getValue()),
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found this promotion have discount greater than.");
    }

    private ResponseEntity<?> filterPromotionsByDescriptionContain(
            PromotionFilterDto promotionFilter, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByDescriptionLike(
                "%" + promotionFilter.getPromotionSearchInfo().getValue() + "%",
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found this promotion have description contain this.");
    }

    private ResponseEntity<?> filterPromotionsByNameContain(PromotionFilterDto promotionFilter, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findAllByNameLike(
                "%" + promotionFilter.getPromotionSearchInfo().getValue() + "%",
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found this promotion have name contain this.");
    }

    private ResponseEntity<?> filterPromotionByIdEqual(PromotionFilterDto promotionFilter, PageRequest pageRequest) {
        Optional<Page<Promotion>> promotions = promotionRepository.findById(
                Long.valueOf(promotionFilter.getPromotionSearchInfo().getValue()),
                pageRequest
        );

        if (promotions.isPresent()) {
            return getPageNumberWrapperWithPromotions(promotions.get());
        }
        return ResponseUtils.getErrorResponseNotFound("Not found this promotion id.");
    }

    private ResponseEntity<?> filterAllPromotionAllFieldEmpty(PageRequest pageRequest) {
        Page<Promotion> promotions = promotionRepository.findAll(
                pageRequest
        );

        if (promotions.getSize() > 0) {
            return getPageNumberWrapperWithPromotions(promotions);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found promotions.");
        }
    }

    private ResponseEntity<?> getPageNumberWrapperWithPromotions(Page<Promotion> promotions) {
        List<PromotionDto> promotionDtoList = promotions.stream()
                .map(promotionMapper::toDto)
                .collect(Collectors.toList());
        PageNumberWrapper<PromotionDto> result = new PageNumberWrapper<>(
                promotionDtoList,
                promotions.getTotalPages(),
                promotions.getTotalElements()
        );
        return ResponseEntity.ok(result);
    }

    private PageRequest getPageRequest(PromotionFilterDto promotionFilter, int pageNumber, Sort.Direction sortDirection) {
        return PageRequest.of(
                pageNumber,
                PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                Sort.by(sortDirection,
                        SortPromotionColumn.getColumnByField(promotionFilter.getSortDirection().getField())
                )
        );
    }
}
