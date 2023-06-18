package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.DataBumpChartDto;
import com.gangoffive.birdtradingplatform.dto.DataLineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderRepository;
import com.gangoffive.birdtradingplatform.service.AdminService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final ShopOwnerService shopOwnerService;
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    @Override
    public DataBumpChartDto dataBumpChartRankOfShop() {
        List<ShopOwner> allShopOwnerWithRankOfEight = getAllShopOwnerWithRankOfEight();
        log.info("allShopOwnerWithRankOfEight.get(0).getAccount() {}", allShopOwnerWithRankOfEight.get(0).getAccount().getEmail());
        List<Order> orderList = shopOwnerService.getAllOrdersNumberPreviousWeek(allShopOwnerWithRankOfEight.get(0).getAccount(), 4);

        for (Order s : orderList) {
            log.info("s.getId() {}", s.getId());
        }
        return null;
    }

    @Override
    public List<PieChartDto> dataPieChartRankOfShop() {
        List<PieChartDto> pieChartDtoList = new ArrayList<>();
        List<ShopOwner> allShopOwnerWithRankOfEight = getAllShopOwnerWithRankOfEight();
        List<Order> allOrdersPreviousFourToOneWeek = getAllOrdersNumberPreviousWeek(4, 1).stream()
                .filter(order -> allShopOwnerWithRankOfEight.contains(order.getShopOwner()))
                .toList();
        for (ShopOwner shopOwner : allShopOwnerWithRankOfEight) {
            double totalPrice = 0;
            for (Order order : allOrdersPreviousFourToOneWeek) {
                log.info("order.getId() {}", order.getId());
                if (order.getShopOwner().equals(shopOwner)) {
                    totalPrice += order.getTotalPrice();
                }
            }
            PieChartDto pieChartDto = PieChartDto.builder()
                    .id(shopOwner.getShopName())
                    .label(shopOwner.getShopName())
                    .value(totalPrice)
                    .build();
            pieChartDtoList.add(pieChartDto);
        }
        return pieChartDtoList;
    }

    private List<ShopOwner> getAllShopOwnerWithRankOfEight() {
        List<Order> allOrdersPreviousFourToOneWeek = getAllOrdersNumberPreviousWeek(4, 1);
        List<ShopOwner> shopOwnerDistictList = allOrdersPreviousFourToOneWeek.stream().map(Order::getShopOwner).distinct().toList();
        Map<ShopOwner, Double> shopOwnerIncome = new HashMap<>();
        for (ShopOwner shopOwner : shopOwnerDistictList) {
            double totalPrice = 0;
            for (Order order : allOrdersPreviousFourToOneWeek) {
                if (order.getShopOwner().equals(shopOwner)) {
                    totalPrice += order.getTotalPrice();
                }
            }
            shopOwnerIncome.put(shopOwner, totalPrice);
        }
        return shopOwnerIncome.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(8)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public List<Order> getAllOrdersNumberPreviousWeek(int startWeekBefore, int endWeekBefore) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Get the date of the previous week
        LocalDate previousStartWeekDate = currentDate.minusWeeks(startWeekBefore);
        LocalDate previousEndWeekDate = currentDate.minusWeeks(endWeekBefore);

        // Get the start and end dates of the previous week
        LocalDate previousWeekStartDate = previousStartWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate previousWeekEndDate = previousEndWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1);

        //Get list Order of Shop Owner
        log.info("previousWeekStartDate {}", Date.from(previousWeekStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        log.info("previousWeekEndDate {}", Date.from(previousWeekEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        List<Order> orders = orderRepository.findAllByCreatedDateBetween(
                Date.from(previousWeekStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(previousWeekEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return orders;
    }
}
