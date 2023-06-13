package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.ColorChart;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.repository.OrderRepository;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import com.gangoffive.birdtradingplatform.service.JwtService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopOwnerServiceImpl implements ShopOwnerService {
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final JwtService jwtService;
    @Override
    public List<LineChartDto> getDataLineChart(String email, Date dateFrom) {
        Optional<Account> account = accountRepository.findByEmail(email);
        List<LineChartDto> lineChartDtoList = new ArrayList<>();
        LineChartDto lineChartDtoOfBird = LineChartDto.builder()
                .id(Bird.class.getSimpleName())
                .data(dataLineChartByTypeProduct(account.get(), Bird.class, dateFrom))
                .build();
        lineChartDtoList.add(lineChartDtoOfBird);
        LineChartDto lineChartDtoOfAccessory = LineChartDto.builder()
                .id(Accessory.class.getSimpleName())
                .data(dataLineChartByTypeProduct(account.get(), Accessory.class, dateFrom))
                .build();
        lineChartDtoList.add(lineChartDtoOfAccessory);
        LineChartDto lineChartDtoOfFood = LineChartDto.builder()
                .id(Food.class.getSimpleName())
                .data(dataLineChartByTypeProduct(account.get(), Food.class, dateFrom))
                .build();
        lineChartDtoList.add(lineChartDtoOfFood);
        return lineChartDtoList;
    }

    @Override
    public List<PieChartDto> getDataPieChart(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        List<PieChartDto> pieChartDtoList = new ArrayList<>();
        PieChartDto pieChartDtoOfBird = PieChartDto.builder()
                .id(Bird.class.getSimpleName())
                .label(Bird.class.getSimpleName())
                .color(ColorChart.BIRD.getColor())
                .value(dataPieChartByTypeProduct(account.get(), Bird.class))
                .build();
        pieChartDtoList.add(pieChartDtoOfBird);
        PieChartDto pieChartDtoOfAccessory = PieChartDto.builder()
                .id(Accessory.class.getSimpleName())
                .label(Accessory.class.getSimpleName())
                .color(ColorChart.ACCESSORY.getColor())
                .value(dataPieChartByTypeProduct(account.get(), Accessory.class))
                .build();
        pieChartDtoList.add(pieChartDtoOfAccessory);
        PieChartDto pieChartDtoOfFood = PieChartDto.builder()
                .id(Food.class.getSimpleName())
                .label(Food.class.getSimpleName())
                .color(ColorChart.FOOD.getColor())
                .value(dataPieChartByTypeProduct(account.get(), Food.class))
                .build();
        pieChartDtoList.add(pieChartDtoOfFood);
        return pieChartDtoList;
    }


    @Override
    public DataBarChartDto dataBarChartByPriceAllTypeProduct(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        List<BarChartDto> barChartDtoList;
        List<BarChartOneTypeDto> barChartFoodDtoList = dataBarChartByPriceEachTypeProduct(account.get(), Food.class, true, false);
        List<BarChartOneTypeDto> barChartBirdDtoList = dataBarChartByPriceEachTypeProduct(account.get(), Bird.class, true, false);
        List<BarChartOneTypeDto> barChartAccessoryDtoList = dataBarChartByPriceEachTypeProduct(account.get(), Accessory.class, true, false);
        barChartDtoList = getListBarChartDto(barChartFoodDtoList, barChartBirdDtoList, barChartAccessoryDtoList);

        DataBarChartDto dataBarChartDto = DataBarChartDto.builder()
                .barChartDtoList(barChartDtoList)
                .build();
        return dataBarChartDto;
    }

    @Override
    public DataBarChartDto dataBarChartByOrderAllTypeProduct(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        List<BarChartDto> barChartDtoList;
        List<BarChartOneTypeDto> barChartFoodDtoList = dataBarChartByPriceEachTypeProduct(account.get(), Food.class, false, true);
        List<BarChartOneTypeDto> barChartBirdDtoList = dataBarChartByPriceEachTypeProduct(account.get(), Bird.class, false, true);
        List<BarChartOneTypeDto> barChartAccessoryDtoList = dataBarChartByPriceEachTypeProduct(account.get(), Accessory.class, false, true);
        barChartDtoList = getListBarChartDto(barChartFoodDtoList, barChartBirdDtoList, barChartAccessoryDtoList);
        DataBarChartDto dataBarChartDto = DataBarChartDto.builder()
                .barChartDtoList(barChartDtoList)
                .build();
        return dataBarChartDto;
    }

    private List<BarChartDto> getListBarChartDto(
                    List<BarChartOneTypeDto> barChartFoodDtoList,
                    List<BarChartOneTypeDto> barChartBirdDtoList,
                    List<BarChartOneTypeDto> barChartAccessoryDtoList
    ) {
        List<BarChartDto> barChartDtoList = new ArrayList<>();
        for (int i = 0; i < barChartFoodDtoList.size(); i++) {
            BarChartDto barChartDto = BarChartDto.builder()
                    .date(barChartFoodDtoList.get(i).getDate())
                    .accessories(barChartAccessoryDtoList.get(i).getValue())
                    .colorAccessories(barChartAccessoryDtoList.get(i).getColor())
                    .birds(barChartBirdDtoList.get(i).getValue())
                    .colorBirds(barChartBirdDtoList.get(i).getColor())
                    .foods(barChartFoodDtoList.get(i).getValue())
                    .colorFoods(barChartFoodDtoList.get(i).getColor())
                    .build();
            barChartDtoList.add(barChartDto);
        }
        for (BarChartDto barChartDto : barChartDtoList) {
            log.info("barChartDto {}", barChartDto);
        }
        return barChartDtoList;
    }

    public List<BarChartOneTypeDto> dataBarChartByPriceEachTypeProduct(
            Account account, Class<?> productClass,
            boolean isCalcPrice, boolean isCalcQuantity
    ) {
        List<BarChartOneTypeDto> barChartOneTypeDtoList = new ArrayList<>();
        List<LocalDate> dateList = getAllDatePreviousWeek();
        List<Order> orderList = getAllOrdersPreviousWeek(account);
        //Get list OrderDetail of list Order
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrderIn(orderList);

        //Get OrderDetail of Product have instance of Food
        List<OrderDetail> listOrderDetailOfProduct = orderDetails.stream()
                .filter(
                        orderDetail -> productClass.isInstance(orderDetail.getProduct())
                ).toList();

        List<Order> listOrderOfProduct = listOrderDetailOfProduct.stream().map(OrderDetail::getOrder).distinct().toList();
        int countDate = 0;
        for (LocalDate date : dateList) {
            countDate++;
            double totalPrice = 0;
            double totalQuantity = 0;
            for (Order order : listOrderOfProduct) {
                if (order.getCreatedDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate().equals(date)) {
                    for (OrderDetail orderDetail : listOrderDetailOfProduct) {
                        if (orderDetail.getOrder().equals(order)) {
                            if (isCalcPrice) {
                                totalPrice += orderDetail.getPrice() * orderDetail.getQuantity();
                            }
                            if (isCalcQuantity) {
                                totalQuantity++;
                            }
                        }
                    }
                }
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String formattedTotalPrice = decimalFormat.format(totalPrice);
            BarChartOneTypeDto barChartDto = new BarChartOneTypeDto();
            if (isCalcPrice) {
                barChartDto.setValue(Double.parseDouble(formattedTotalPrice));
            }
            if (isCalcQuantity) {
                barChartDto.setValue(totalQuantity);
            }

            if (countDate == 1) {
                barChartDto.setDate(DayOfWeek.MONDAY.name().substring(0, 1).toUpperCase() + DayOfWeek.MONDAY.name().toLowerCase().substring(1, 3));
            } else if (countDate == 2) {
                barChartDto.setDate(DayOfWeek.TUESDAY.name().substring(0, 1).toUpperCase() + DayOfWeek.TUESDAY.name().toLowerCase().substring(1, 3));
            } else if (countDate == 3) {
                barChartDto.setDate(DayOfWeek.WEDNESDAY.name().substring(0, 1).toUpperCase() + DayOfWeek.WEDNESDAY.name().toLowerCase().substring(1, 3));
            } else if (countDate == 4) {
                barChartDto.setDate(DayOfWeek.THURSDAY.name().substring(0, 1).toUpperCase() + DayOfWeek.THURSDAY.name().toLowerCase().substring(1, 3));
            } else if (countDate == 5) {
                barChartDto.setDate(DayOfWeek.FRIDAY.name().substring(0, 1).toUpperCase() + DayOfWeek.FRIDAY.name().toLowerCase().substring(1, 3));
            } else if (countDate == 6) {
                barChartDto.setDate(DayOfWeek.SATURDAY.name().substring(0, 1).toUpperCase() + DayOfWeek.SATURDAY.name().toLowerCase().substring(1, 3));
            } else if (countDate == 7) {
                barChartDto.setDate(DayOfWeek.SUNDAY.name().substring(0, 1).toUpperCase() + DayOfWeek.SUNDAY.name().toLowerCase().substring(1, 3));
            }

            if (productClass.equals(Food.class)) {
                barChartDto.setColor(ColorChart.FOOD.getColor());
            } else if (productClass.equals(Accessory.class)) {
                barChartDto.setColor(ColorChart.ACCESSORY.getColor());
            } else if (productClass.equals(Bird.class)) {
                barChartDto.setColor(ColorChart.BIRD.getColor());
            }
            barChartOneTypeDtoList.add(barChartDto);
        }
        return barChartOneTypeDtoList;
    }

    @Override
    public String redirectToShopOwner(String email) {
        return jwtService.generateToken(UserPrincipal.create(accountRepository.findByEmail(email).get()));
    }

    public List<LocalDate> getAllDatePreviousWeek() {
        List<LocalDate> localDateList = new ArrayList<>();
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        // Get the date of the previous week
        LocalDate previousWeekDate = currentDate.minusWeeks(1);
        // Get the start and end dates of the previous week
        LocalDate previousWeekStartDate = previousWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate previousWeekEndDate = previousWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        log.info("Previous week start date: {}", previousWeekStartDate);
        log.info("Previous week end date: {}", previousWeekEndDate);
        while (!previousWeekStartDate.isAfter(previousWeekEndDate)) {
            localDateList.add(previousWeekStartDate);
            previousWeekStartDate = previousWeekStartDate.plusDays(1);
        }
        return localDateList;
    }

    public List<Order> getAllOrdersPreviousWeek(Account account) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Get the date of the previous week
        LocalDate previousWeekDate = currentDate.minusWeeks(1);

        // Get the start and end dates of the previous week
        LocalDate previousWeekStartDate = previousWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate previousWeekEndDate = previousWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        //Get list Order of Shop Owner
        List<Order> tmpOrders = orderRepository.findByShopOwner(account.getShopOwner());
        List<Order> orders = tmpOrders.stream()
                .filter(
                        order ->
                                (order.getCreatedDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate().equals(previousWeekStartDate)
                                || order.getCreatedDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate().isAfter(previousWeekStartDate))
                                && (order.getCreatedDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate().equals(previousWeekEndDate)
                                || order.getCreatedDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate().isBefore(previousWeekEndDate))
                )
                .collect(Collectors.toList());
        return orders;
    }

    private double dataPieChartByTypeProduct(Account account, Class<?> productClass) {
        //Get list Order of Shop Owner
        LocalDate currentDate = LocalDate.now();
//        String dateString = "2023-06-24";
//        LocalDate currentDate = LocalDate.parse(dateString);

        //Get list Orders before now 7 day
        LocalDate sevenDaysAgo = currentDate.minus(6, ChronoUnit.DAYS);
        log.info("sevenDaysAgo {}", sevenDaysAgo);
        Date sevenDaysAgoDate = Date.from(sevenDaysAgo.atStartOfDay(ZoneId.of("UTC")).toInstant());
        log.info("sevenDaysAgoDate {}", sevenDaysAgoDate);
        List<Order> orders = orderRepository.findByShopOwnerAndCreatedDateAfter(account.getShopOwner(), sevenDaysAgoDate);
        for (Order order : orders) {
            log.info("order.getId() {}", order.getId());
            log.info("order.getCreatedDate() {}", order.getCreatedDate());
        }

        //Get list OrderDetail of list Order
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrderIn(orders);

        List<OrderDetail> listOrderDetailOfProduct = orderDetails.stream()
                .filter(
//                        orderDetail -> orderDetail.getProduct() instanceof Food
                        orderDetail -> productClass.isInstance(orderDetail.getProduct())
                ).toList();

        for (OrderDetail order : listOrderDetailOfProduct) {
            log.info("order.getId() {}", order.getId());
            log.info("order.getOrder().getId() {}", order.getOrder().getId());
            log.info("order.getCreatedDate() {}", order.getProduct());
        }

        double totalPrice = listOrderDetailOfProduct.stream().mapToDouble(orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity()).sum();
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formattedTotalPrice = decimalFormat.format(totalPrice);
        log.info("totalPrice {}", totalPrice);
        return Double.parseDouble(formattedTotalPrice);
    }

    private List<DataLineChartDto> dataLineChartByTypeProduct(Account account, Class<?> productClass, Date dateFrom) {
        //Get list Order of Shop Owner
        List<Order> tmpOrders = orderRepository.findByShopOwner(account.getShopOwner());
        List<Order> orders = tmpOrders.stream().filter(order -> order.getCreatedDate().after(dateFrom)).collect(Collectors.toList());
        for (Order order : orders) {
            log.info("id {}", order.getId());
        }

        //Get list OrderDetail of list Order
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrderIn(orders);
        for (OrderDetail orderDetail : orderDetails) {
            log.info("id od {}", orderDetail.getId());
        }

        //Get OrderDetail of Product have instance of Food
        List<OrderDetail> listOrderDetailOfProduct = orderDetails.stream()
                .filter(
//                        orderDetail -> orderDetail.getProduct() instanceof Food
                        orderDetail -> productClass.isInstance(orderDetail.getProduct())
                ).toList();
        log.info("size od Food {}",listOrderDetailOfProduct.size());
        for (OrderDetail orderDetail : listOrderDetailOfProduct) {
            log.info("id od Food {}", orderDetail.getId());
        }

        //Get list Order of Food From orderDetailOfFoods
        List<Order> listOrderOfProduct = listOrderDetailOfProduct.stream().map(OrderDetail::getOrder).distinct().toList();
        log.info("size o Food {}",listOrderOfProduct.size());
        for (Order order : listOrderOfProduct) {
            log.info("id o Food {}", order.getId());
        }


        //Distinct date of orderOfBirds to get list LocalDate
//        List<LocalDate> listDistinctDateOfProduct = listOrderOfProduct.stream()
//                .map(order -> order.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
//                .distinct()
//                .collect(Collectors.toList());
//        for (LocalDate date : listDistinctDateOfProduct) {
//            log.info("distinctDateOfFoods {}", date);
//        }

        List<LocalDate> listDistinctDateOfProduct = new ArrayList<>();
        LocalDate now = LocalDate.now();
//        LocalDate now = LocalDate.of(2023, 06, 22);

        LocalDate currentDate = dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        log.info("currentDate {}", currentDate);
        while (!currentDate.isAfter(now)) {
            listDistinctDateOfProduct.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        List<DataLineChartDto> dataLineChartDtoListOfProduct = new ArrayList<>();
        for (LocalDate date : listDistinctDateOfProduct) {
            //One day have many orders
            double totalPrice = 0;
            for (Order order : listOrderOfProduct) {
                //One order have many OrderDetails
                log.info("order id {}", order.getId());
                log.info("order.getCreatedDate() {}", order.getCreatedDate());
                log.info("date {}", date);
                if (order.getCreatedDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDate().equals(date)) {
                    log.info("order id {}", order.getId());

                    for (OrderDetail orderDetail : listOrderDetailOfProduct) {
                        log.info("orderDetail id {}", orderDetail.getId());
                        if (orderDetail.getOrder().equals(order)) {
                            log.info("orderDetail.getPrice() {}, orderDetail.getQuantity() {}", orderDetail.getPrice(), orderDetail.getQuantity());
                            totalPrice += orderDetail.getPrice() * orderDetail.getQuantity();
                            log.info("total {}", totalPrice);
                        }
                    }
                }
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            String formattedTotalPrice = decimalFormat.format(totalPrice);
            DataLineChartDto dataLineChartDto = DataLineChartDto.builder()
                    .x(DateUtils.formatLocalDateToString(date))
                    .y(Double.parseDouble(formattedTotalPrice))
                    .build();
            dataLineChartDtoListOfProduct.add(dataLineChartDto);
        }
        for (DataLineChartDto dataLineChartDto : dataLineChartDtoListOfProduct) {
            log.info("dataLineChartDto {}", dataLineChartDto);
        }
        return dataLineChartDtoListOfProduct;
    }

}
