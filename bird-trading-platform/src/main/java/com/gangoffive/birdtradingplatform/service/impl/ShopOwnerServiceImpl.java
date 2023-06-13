package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.DataLineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.ColorPieChart;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.repository.OrderRepository;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopOwnerServiceImpl implements ShopOwnerService {
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
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
                .color(ColorPieChart.BIRD.getColor())
                .value(dataPieChartByTypeProduct(account.get(), Bird.class))
                .build();
        pieChartDtoList.add(pieChartDtoOfBird);
        PieChartDto pieChartDtoOfAccessory = PieChartDto.builder()
                .id(Accessory.class.getSimpleName())
                .label(Accessory.class.getSimpleName())
                .color(ColorPieChart.ACCESSORY.getColor())
                .value(dataPieChartByTypeProduct(account.get(), Accessory.class))
                .build();
        pieChartDtoList.add(pieChartDtoOfAccessory);
        PieChartDto pieChartDtoOfFood = PieChartDto.builder()
                .id(Food.class.getSimpleName())
                .label(Food.class.getSimpleName())
                .color(ColorPieChart.FOOD.getColor())
                .value(dataPieChartByTypeProduct(account.get(), Food.class))
                .build();
        pieChartDtoList.add(pieChartDtoOfFood);
        return pieChartDtoList;
    }

    public double dataPieChartByTypeProduct(Account account, Class<?> productClass) {
        //Get list Order of Shop Owner
//        LocalDate currentDate = LocalDate.now();
        String dateString = "2023-06-24";
        LocalDate currentDate = LocalDate.parse(dateString);

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
//        LocalDate now = LocalDate.now();
        LocalDate now = LocalDate.of(2023, 06, 22);

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
