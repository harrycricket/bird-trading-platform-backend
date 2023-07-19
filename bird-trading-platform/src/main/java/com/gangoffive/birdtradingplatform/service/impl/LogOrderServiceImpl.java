package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.OrderStatusConstant;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.DateRangeDto;
import com.gangoffive.birdtradingplatform.dto.LogOrderFilterDto;
import com.gangoffive.birdtradingplatform.dto.LogOrderShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.LogOrder;
import com.gangoffive.birdtradingplatform.enums.FieldLogOrderTable;
import com.gangoffive.birdtradingplatform.enums.Operator;
import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import com.gangoffive.birdtradingplatform.enums.SortLogOrderColumn;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.LogOrderRepository;
import com.gangoffive.birdtradingplatform.service.LogOrderService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogOrderServiceImpl implements LogOrderService {
    private final AccountRepository accountRepository;
    private final LogOrderRepository logOrderRepository;

    @Override
    public ResponseEntity<?> filterAllLogOrder(LogOrderFilterDto logOrderFilter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Account> account = accountRepository.findByEmail(authentication.getName());
        Long shopId = account.get().getShopOwner().getId();
        if (logOrderFilter.getPageNumber() > 0) {
            int pageNumber = logOrderFilter.getPageNumber() - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
            PageRequest pageRequestWithSort = null;
            if (logOrderFilter.getSortDirection() != null
                    && !logOrderFilter.getSortDirection().getSort().isEmpty()
                    && !logOrderFilter.getSortDirection().getField().isEmpty()
            ) {
                if (
                        !SortLogOrderColumn.checkField(logOrderFilter.getSortDirection().getField())
                ) {
                    return ResponseUtils.getErrorResponseNotFoundSortColumn();
                }
                if (logOrderFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
                    pageRequestWithSort = getPageRequest(logOrderFilter, pageNumber, Sort.Direction.ASC);
                } else if (logOrderFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
                    pageRequestWithSort = getPageRequest(logOrderFilter, pageNumber, Sort.Direction.DESC);
                } else {
                    return ResponseUtils.getErrorResponseNotFoundSortDirection();
                }
            }

            if (
                    logOrderFilter.getLogOrderSearchInfo().getField().isEmpty()
                            && logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
                            && logOrderFilter.getLogOrderSearchInfo().getOperator().isEmpty()
                            && logOrderFilter.getSortDirection().getField().isEmpty()
                            && logOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                log.info("all no");

                pageRequestWithSort = PageRequest.of(
                        pageNumber,
                        PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                        Sort.by(Sort.Direction.DESC,
                                SortLogOrderColumn.TIMESTAMP.getColumn()
                        )
                );
                return filterAllLogOrdersAllFieldEmpty(shopId, pageRequestWithSort);
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().isEmpty()
                            && logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
                            && logOrderFilter.getLogOrderSearchInfo().getOperator().isEmpty()
                            && !logOrderFilter.getSortDirection().getField().isEmpty()
                            && !logOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                log.info("with sort");
                return filterAllLogOrdersAllFieldEmpty(shopId, pageRequestWithSort);
            }

            if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.ID.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterLogOrderByIdEqual(logOrderFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.ORDER_ID.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterLogOrderByOrderIdEqual(logOrderFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.STATUS.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
                            && logOrderFilter.getSortDirection().getField().isEmpty()
                            && logOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterLogOrdersByStatusEqual(logOrderFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.STATUS.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterLogOrdersByStatusEqual(logOrderFilter, shopId, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.TIMESTAMP.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
                            && logOrderFilter.getSortDirection().getField().isEmpty()
                            && logOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(logOrderFilter.getLogOrderSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterLogOrdersByTimestampGreaterThanOrEqual(shopId, dateRange, pageRequest);
                    } else {
                        return filterLogOrdersByTimestampFromTo(shopId, dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.TIMESTAMP.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(logOrderFilter.getLogOrderSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterLogOrdersByTimestampGreaterThanOrEqual(shopId, dateRange, pageRequestWithSort);
                    } else {
                        return filterLogOrdersByTimestampFromTo(shopId, dateRange, pageRequestWithSort);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.STAFF_ID.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterLogOrderByStaffIdEqual(logOrderFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.STAFF_USERNAME.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
                            && logOrderFilter.getSortDirection().getField().isEmpty()
                            && logOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterLogOrdersByStaffUserNameContain(logOrderFilter, shopId, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    logOrderFilter.getLogOrderSearchInfo().getField().equals(FieldLogOrderTable.STAFF_USERNAME.getField())
                            && !logOrderFilter.getLogOrderSearchInfo().getValue().isEmpty()
            ) {
                if (logOrderFilter.getLogOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterLogOrdersByStaffUserNameContain(logOrderFilter, shopId, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else {
                return ResponseUtils.getErrorResponseBadRequest("Log order filter is not correct.");
            }
        } else {
            return ResponseUtils.getErrorResponseBadRequestPageNumber();
        }
    }

    private ResponseEntity<?> filterLogOrdersByStaffUserNameContain(LogOrderFilterDto logOrderFilter, Long shopId, PageRequest pageRequest) {
        Optional<Page<LogOrder>> logOrders = logOrderRepository.findByShopStaff_UserNameLikeAndShopStaff_ShopOwner_Id(
                "%" + logOrderFilter.getLogOrderSearchInfo().getValue() + "%",
                shopId,
                pageRequest
        );
        if (logOrders.isPresent()) {
            return getPageNumberWrapperWithLogOrders(logOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found log order with this staff username in shop.");
        }
    }

    private ResponseEntity<?> filterLogOrderByStaffIdEqual(LogOrderFilterDto logOrderFilter, Long shopId, PageRequest pageRequest) {
        Optional<Page<LogOrder>> logOrders = logOrderRepository.findByShopStaff_IdAndShopStaff_ShopOwner_Id(
                Long.valueOf(logOrderFilter.getLogOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );
        if (logOrders.isPresent()) {
            return getPageNumberWrapperWithLogOrders(logOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found log order with this staff id in shop.");
        }
    }

    private ResponseEntity<?> filterLogOrdersByTimestampFromTo(Long shopId, DateRangeDto dateRange, PageRequest pageRequest) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<LogOrder>> logOrders = logOrderRepository.findByTimestampBetweenAndShopStaff_ShopOwner_Id(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                calendar.getTime(),
                shopId,
                pageRequest
        );
        if (logOrders.isPresent()) {
            return getPageNumberWrapperWithLogOrders(logOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found log order with this timestamp between in shop.");
        }
    }

    private ResponseEntity<?> filterLogOrdersByTimestampGreaterThanOrEqual(Long shopId, DateRangeDto dateRange, PageRequest pageRequest) {
        Optional<Page<LogOrder>> logOrders = logOrderRepository.findByTimestampGreaterThanEqualAndShopStaff_ShopOwner_Id(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                shopId,
                pageRequest
        );
        if (logOrders.isPresent()) {
            return getPageNumberWrapperWithLogOrders(logOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found log order with this timestamp from in shop.");
        }
    }

    private ResponseEntity<?> filterLogOrdersByStatusEqual(LogOrderFilterDto logOrderFilter, Long shopId, PageRequest pageRequest) {
        List<OrderStatus> orderStatuses;
        if (Integer.parseInt(logOrderFilter.getLogOrderSearchInfo().getValue()) == 9) {
            orderStatuses = OrderStatusConstant.VIEW_ALL_ORDER_STATUS;
        } else {
            orderStatuses = Arrays.asList(
                    OrderStatus.getOrderStatusBaseOnStatusCode(
                            Integer.parseInt(logOrderFilter.getLogOrderSearchInfo().getValue())
                    )
            );
        }

        Optional<Page<LogOrder>> logOrders = logOrderRepository.findByStatusInAndShopStaff_ShopOwner_Id(
                orderStatuses,
                shopId,
                pageRequest
        );
        if (logOrders.isPresent()) {
            return getPageNumberWrapperWithLogOrders(logOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found log order with this order status in shop.");
        }
    }

    private ResponseEntity<?> filterLogOrderByOrderIdEqual(LogOrderFilterDto logOrderFilter, Long shopId, PageRequest pageRequest) {
        Optional<Page<LogOrder>> logOrders = logOrderRepository.findByOrder_IdAndShopStaff_ShopOwner_Id(
                Long.valueOf(logOrderFilter.getLogOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );
        if (logOrders.isPresent()) {
            return getPageNumberWrapperWithLogOrders(logOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found log order with this order id in shop.");
        }
    }

    private ResponseEntity<?> filterLogOrderByIdEqual(LogOrderFilterDto logOrderFilter, Long shopId, PageRequest pageRequest) {
        Optional<Page<LogOrder>> logOrders = logOrderRepository.findByIdAndShopStaff_ShopOwner_Id(
                Long.valueOf(logOrderFilter.getLogOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );
        if (logOrders.isPresent()) {
            return getPageNumberWrapperWithLogOrders(logOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found log order with this id in shop.");
        }
    }

    private ResponseEntity<?> filterAllLogOrdersAllFieldEmpty(Long shopId, PageRequest pageRequest) {
        Optional<Page<LogOrder>> logOrders = logOrderRepository.findAllByShopStaff_ShopOwner_Id(
                shopId,
                pageRequest
        );
        if (logOrders.isPresent()) {
            return getPageNumberWrapperWithLogOrders(logOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found log orders in shop.");
        }
    }

    private PageRequest getPageRequest(LogOrderFilterDto logOrderFilter, int pageNumber, Sort.Direction sortDirection) {
        return PageRequest.of(
                pageNumber,
                PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                Sort.by(sortDirection,
                        SortLogOrderColumn.getColumnByField(logOrderFilter.getSortDirection().getField())
                )
        );
    }

    private ResponseEntity<PageNumberWrapper<?>> getPageNumberWrapperWithLogOrders(
            Optional<Page<LogOrder>> logOrders
    ) {
        List<LogOrderShopOwnerDto> logOrdersShopOwnerDto = logOrders.get().stream()
                .map(this::logOrderToLogOrderShopOwnerDto)
                .collect(Collectors.toList());
        PageNumberWrapper<LogOrderShopOwnerDto> result = new PageNumberWrapper<>(
                logOrdersShopOwnerDto,
                logOrders.get().getTotalPages(),
                logOrders.get().getTotalElements()
        );
        return ResponseEntity.ok(result);
    }

    private LogOrderShopOwnerDto logOrderToLogOrderShopOwnerDto(LogOrder logOrder) {
        return LogOrderShopOwnerDto.builder()
                .id(logOrder.getId())
                .orderId(logOrder.getOrder().getId())
                .orderStatus(logOrder.getStatus())
                .timestamp(logOrder.getTimestamp().getTime())
                .staffId(logOrder.getShopStaff().getId())
                .staffUsername(logOrder.getShopStaff().getUserName())
                .build();
    }
}
