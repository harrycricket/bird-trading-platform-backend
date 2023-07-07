package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.BumpChartDto;
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
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final ShopOwnerService shopOwnerService;
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    @Override
    public List<DataBumpChartDto> dataBumpChartRankOfShop() {
        List<DataBumpChartDto> dataBumpChartDtoList = new ArrayList<>();
        List<ShopOwner> allShopOwnerWithRankOfEight = getAllShopOwnerWithRankOfEight();
//        log.info("allShopOwnerWithRankOfEight.get(0).getAccount() {}", allShopOwnerWithRankOfEight.get(0).getAccount().getEmail());
        //Rank of shop by price previous now one week
        Map<ShopOwner, Integer> shopOwnerWithRankPreviousOneWeek = getShopOwnerWithRankByWeek(allShopOwnerWithRankOfEight, 1);
        Map<ShopOwner, Integer> shopOwnerWithRankPreviousTwoWeek = getShopOwnerWithRankByWeek(allShopOwnerWithRankOfEight, 2);
        Map<ShopOwner, Integer> shopOwnerWithRankPreviousThreeWeek = getShopOwnerWithRankByWeek(allShopOwnerWithRankOfEight, 3);
        Map<ShopOwner, Integer> shopOwnerWithRankPreviousFourWeek = getShopOwnerWithRankByWeek(allShopOwnerWithRankOfEight, 4);
        for (ShopOwner shopOwner : allShopOwnerWithRankOfEight) {
            List<BumpChartDto> bumpChartDtoList = new ArrayList<>();
            BumpChartDto bumpChartDto4 = BumpChartDto.builder()
                    .x("Week 4")
                    .build();
            List<Order> orderListWeekFour = orderRepository.findByShopOwnerAndCreatedDateBetween(shopOwner, getStartDateOfPreviousWeek(4), getEndDateOfPreviousWeek(4));
//            log.info(" getStartDateOfPreviousWeek(4) {}, getEndDateOfPreviousWeek(4) {}",  getStartDateOfPreviousWeek(4), getEndDateOfPreviousWeek(4));
//            log.info("orderListWeekFour {}", orderListWeekFour.size());
//            log.info("orderListWeekFour.isEmpty() {}", orderListWeekFour.isEmpty());
            if (orderListWeekFour.isEmpty()) {
                bumpChartDto4.setY(null);
            } else {
                bumpChartDto4.setY(shopOwnerWithRankPreviousFourWeek.get(shopOwner));
            }
            bumpChartDtoList.add(bumpChartDto4);
            BumpChartDto bumpChartDto3 = BumpChartDto.builder()
                    .x("Week 3")
                    .build();
            List<Order> orderListWeekThree = orderRepository.findByShopOwnerAndCreatedDateBetween(shopOwner, getStartDateOfPreviousWeek(3), getEndDateOfPreviousWeek(3));
            log.info("orderListWeekThree {}", orderListWeekThree.size());
            if (orderListWeekThree.isEmpty()) {
                bumpChartDto3.setY(null);
            } else {
                bumpChartDto3.setY(shopOwnerWithRankPreviousThreeWeek.get(shopOwner));
            }
            bumpChartDtoList.add(bumpChartDto3);
            BumpChartDto bumpChartDto2 = BumpChartDto.builder()
                    .x("Week 2")
                    .build();
            List<Order> orderListWeekTwo = orderRepository.findByShopOwnerAndCreatedDateBetween(shopOwner, getStartDateOfPreviousWeek(2), getEndDateOfPreviousWeek(2));
            if (orderListWeekTwo.isEmpty()) {
                bumpChartDto2.setY(null);
            } else {
                bumpChartDto2.setY(shopOwnerWithRankPreviousTwoWeek.get(shopOwner));
            }
            bumpChartDtoList.add(bumpChartDto2);
            BumpChartDto bumpChartDto1 = BumpChartDto.builder()
                    .x("Week 1")
                    .build();
            List<Order> orderListWeekOne = orderRepository.findByShopOwnerAndCreatedDateBetween(shopOwner, getStartDateOfPreviousWeek(1), getEndDateOfPreviousWeek(1));
            if (orderListWeekOne.isEmpty()) {
                bumpChartDto1.setY(null);
            } else {
                bumpChartDto1.setY(shopOwnerWithRankPreviousOneWeek.get(shopOwner));
            }
            bumpChartDtoList.add(bumpChartDto1);
            DataBumpChartDto dataBumpChartDto = DataBumpChartDto.builder()
                    .id(shopOwner.getShopName())
                    .data(bumpChartDtoList)
                    .build();
            dataBumpChartDtoList.add(dataBumpChartDto);
        }
        return dataBumpChartDtoList;
    }

    private Map<ShopOwner, Integer> getShopOwnerWithRankByWeek(List<ShopOwner> allShopOwnerWithRankOfEight, int week) {
        Map<ShopOwner, Double> shopOwnerTotalPriceOfWeekMap = new HashMap<>();
        for (ShopOwner shopOwner : allShopOwnerWithRankOfEight) {
            List<Order> orderList = shopOwnerService.getAllOrdersNumberPreviousWeek(shopOwner.getAccount(), week);
            if (!orderList.isEmpty()) {
                double totalPriceOneShopInOneWeek = orderList.stream().mapToDouble(Order::getTotalPrice).sum();
                shopOwnerTotalPriceOfWeekMap.put(shopOwner, totalPriceOneShopInOneWeek);
            }
        }
        List<ShopOwner> shopOwnersWithEqualKeys = new ArrayList<>();
        log.info("--------------------------------------- Week {}------------", week);
        for (Map.Entry<ShopOwner, Double> entry1 : shopOwnerTotalPriceOfWeekMap.entrySet()) {
            ShopOwner shopOwner1 = entry1.getKey();
            double value1 = entry1.getValue();

            for (Map.Entry<ShopOwner, Double> entry2 : shopOwnerTotalPriceOfWeekMap.entrySet()) {
                ShopOwner shopOwner2 = entry2.getKey();
                double value2 = entry2.getValue();

                if (shopOwner1 != shopOwner2 && value1 == value2) {
                    shopOwnersWithEqualKeys.add(shopOwner1);
                    shopOwnersWithEqualKeys.add(shopOwner2);
                }
            }
        }
        List<ShopOwner> shopOwnersWithEqualKeyDistinctList = shopOwnersWithEqualKeys.stream().distinct().toList();
        Map<ShopOwner, Double> shopOwnerTotalQuantityOfWeekMap = new HashMap<>();
        if (!shopOwnersWithEqualKeyDistinctList.isEmpty()) {
            for (ShopOwner shopOwner : shopOwnersWithEqualKeyDistinctList) {
                List<Order> orderList = shopOwnerService.getAllOrdersNumberPreviousWeek(shopOwner.getAccount(), week);
                double totalQuantity = 0;
                if (!orderList.isEmpty()) {
                    for (Order order : orderList) {
                        List<OrderDetail> orderDetailList = order.getOrderDetails();
                        for (OrderDetail orderDetail : orderDetailList) {
                            totalQuantity += orderDetail.getQuantity();
                        }
                    }
//                    shopOwnerTotalPriceOfWeekMap.put(shopOwner, totalPriceOneShopInOneWeek);
                }
                shopOwnerTotalQuantityOfWeekMap.put(shopOwner, totalQuantity);
                log.info("ShopOwner: {}", shopOwner.getShopName());
                log.info("totalQuantity: {}", totalQuantity);
            }
        }
        if (!shopOwnersWithEqualKeys.isEmpty()) {
            for (ShopOwner shopOwner : shopOwnersWithEqualKeys.stream().distinct().toList()) {
                log.info("shopOwnersWithEqualKeys {}", shopOwner.getShopName());
            }
        }

        List<ShopOwner> shopOwnerByQuantityDecreaseList = shopOwnerTotalQuantityOfWeekMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();
        for (ShopOwner shopOwner : shopOwnerByQuantityDecreaseList) {
            log.info("shopOwner sort {}", shopOwner.getShopName());
        }
        log.info("---------------------------------------------------");
        List<ShopOwner> shopOwnerByRankDecreaseList = shopOwnerTotalPriceOfWeekMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .toList();
        List<ShopOwner> updatedList = new ArrayList<>(shopOwnerByRankDecreaseList); // Create a mutable copy of the list
        if (!shopOwnerByQuantityDecreaseList.isEmpty()) {
            int index = 0;
            for (int i = 0; i < updatedList.size(); i++) {
                if (shopOwnerByQuantityDecreaseList.contains(updatedList.get(i))) {
                    log.info("Go here {}", index + 1);
                    log.info("shopOwnerByQuantityDecreaseList.get(index) {}", shopOwnerByQuantityDecreaseList.get(index).getShopName());
                    log.info("shopOwnerByRankDecreaseList.get(i) {}", updatedList.get(i).getShopName());
                    updatedList.set(i, shopOwnerByQuantityDecreaseList.get(index));
                    index++;
                }
            }

            // Assign the updated list back to shopOwnerByRankDecreaseList if necessary
            shopOwnerByRankDecreaseList = updatedList;
        }
        Map<ShopOwner, Integer> shopOwnerRankMap = new HashMap<>();
        for (int i = 0; i < shopOwnerByRankDecreaseList.size(); i++) {
            shopOwnerRankMap.put(shopOwnerByRankDecreaseList.get(i), i + 1);
        }
        return shopOwnerRankMap;
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
                    .value(Math.round(totalPrice * 100.0) / 100.0)
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

    private Date getStartDateOfPreviousWeek(int week) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusWeeks(week).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getEndDateOfPreviousWeek(int week) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.minusWeeks(week).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusDays(1);
        return Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
