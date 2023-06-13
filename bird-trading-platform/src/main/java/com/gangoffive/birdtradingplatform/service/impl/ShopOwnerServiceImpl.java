package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.DataLineChartDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.repository.OrderRepository;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
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
    public List<LineChartDto> getTotalPriceAllOrderByEachDate(String email, Date dateFrom) {
        Optional<Account> account = accountRepository.findByEmail(email);
        List<LineChartDto> lineChartDtoList = new ArrayList<>();
        LineChartDto lineChartDtoOfBird = LineChartDto.builder()
                .id(Bird.class.getSimpleName())
                .data(dataBumpChartByTypeProduct(account.get(), Bird.class, dateFrom))
                .build();
        lineChartDtoList.add(lineChartDtoOfBird);
        LineChartDto lineChartDtoOfAccessory = LineChartDto.builder()
                .id(Accessory.class.getSimpleName())
                .data(dataBumpChartByTypeProduct(account.get(), Accessory.class, dateFrom))
                .build();
        lineChartDtoList.add(lineChartDtoOfAccessory);
        LineChartDto lineChartDtoOfFood = LineChartDto.builder()
                .id(Food.class.getSimpleName())
                .data(dataBumpChartByTypeProduct(account.get(), Food.class, dateFrom))
                .build();
        lineChartDtoList.add(lineChartDtoOfFood);
        return lineChartDtoList;
    }

    private List<DataLineChartDto> dataBumpChartByTypeProduct(Account account, Class<?> productClass, Date dateFrom) {
        //Get list Order of Shop Owner
        List<Order> tmpOrder = orderRepository.findByShopOwner(account.getShopOwner());
        List<Order> orders = tmpOrder.stream().filter(order -> order.getCreatedDate().after(dateFrom)).collect(Collectors.toList());
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
                if (order.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(date)) {
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
            DataLineChartDto dataLineChartDto = DataLineChartDto.builder()
                    .x(DateUtils.formatLocalDateToString(date))
                    .y(totalPrice)
                    .build();
            dataLineChartDtoListOfProduct.add(dataLineChartDto);
        }
        for (DataLineChartDto dataLineChartDto : dataLineChartDtoListOfProduct) {
            log.info("dataLineChartDto {}", dataLineChartDto);
        }
        return dataLineChartDtoListOfProduct;
    }

}
