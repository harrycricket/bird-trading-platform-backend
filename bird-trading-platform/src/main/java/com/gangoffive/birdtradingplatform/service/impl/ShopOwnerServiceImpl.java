package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.BumpChartDto;
import com.gangoffive.birdtradingplatform.dto.DataBumpChartDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.repository.OrderRepository;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public List<BumpChartDto> getTotalPriceAllOrderByEachDate(String email, Date dateFrom) {
        Optional<Account> account = accountRepository.findByEmail(email);
        List<BumpChartDto> bumpChartDtoList = new ArrayList<>();
        BumpChartDto bumpChartDtoOfBird = BumpChartDto.builder()
                .type(Bird.class.getSimpleName())
                .dataBumpCharts(dataBumpChartByTypeProduct(account.get(), Bird.class, dateFrom))
                .build();
        bumpChartDtoList.add(bumpChartDtoOfBird);
        BumpChartDto bumpChartDtoOfAccessory = BumpChartDto.builder()
                .type(Accessory.class.getSimpleName())
                .dataBumpCharts(dataBumpChartByTypeProduct(account.get(), Accessory.class, dateFrom))
                .build();
        bumpChartDtoList.add(bumpChartDtoOfAccessory);
        BumpChartDto bumpChartDtoOfFood = BumpChartDto.builder()
                .type(Food.class.getSimpleName())
                .dataBumpCharts(dataBumpChartByTypeProduct(account.get(), Food.class, dateFrom))
                .build();
        bumpChartDtoList.add(bumpChartDtoOfFood);
        return bumpChartDtoList;
    }

    private List<DataBumpChartDto> dataBumpChartByTypeProduct(Account account, Class<?> productClass, Date dateFrom) {
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
        List<DataBumpChartDto> dataBumpChartDtoListOfProduct = new ArrayList<>();
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
                            totalPrice += orderDetail.getPrice() * orderDetail.getQuantity();
                            log.info("total {}", totalPrice);
                        }
                    }
                }
            }
            DataBumpChartDto dataBumpChartDto = DataBumpChartDto.builder()
                    .dateOfPrice(date)
                    .price(totalPrice)
                    .build();
            dataBumpChartDtoListOfProduct.add(dataBumpChartDto);
        }
        for (DataBumpChartDto dataBumpChartDto : dataBumpChartDtoListOfProduct) {
            log.info("dataBumpChartDto {}", dataBumpChartDto);
        }
        return dataBumpChartDtoListOfProduct;
    }

}
