package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.PackageOrderDto;
import com.gangoffive.birdtradingplatform.dto.TransactionDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.service.PackageOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PackageOrderController {

    private final PackageOrderService packageOrderService;
    @RequestMapping(value = "api/v1/package-order", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getPackageOrder(
            @RequestBody PackageOrderDto packageOrderDto1,
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestParam(value = "PayerID", required = false) String payerId
    ) {
        packageOrderDto1.getProductOrder().entrySet().forEach(pro -> log.info("pro {}", pro.getKey()));
        PackageOrderDto packageOrderDto = new PackageOrderDto();
        Map<Long, Integer> productOrder = new HashMap<>();
        productOrder.put(1L, 2);
        productOrder.put(2L, 2);
        productOrder.put(4L, 1);
        UserOrderDto userOrderDto = new UserOrderDto();
        userOrderDto.setName("Day la test");
        //        userOrderDto.setEmail("BianchiSofia61433@gmail.com");
        userOrderDto.setEmail("DupontPierre85570@gmail.com");
//        userOrderDto.setEmail("LeblancMarie57338@gmail.com");
//        userOrderDto.setEmail("LambertThomas77441@gmail.com");
//        userOrderDto.setEmail("Young-hoChoi58997@gmail.com");
        userOrderDto.setPhoneNumber("Day la test");
        userOrderDto.setStreet("Day la test");
        userOrderDto.setWard("Day la test");
        userOrderDto.setDistrict("Day la test");
        userOrderDto.setCity("Day la test");

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setPaymentMethod(PaymentMethod.DELIVERY);
        transactionDto.setTotalPrice(96.65);//1 2 4
//        transactionDto.setTotalPrice(53.6025);//2 4

//        transactionDto.setTotalPrice(69.9);//1 2
//        transactionDto.setTotalPrice(78.35);//1 4
//      transactionDto.setTotalPrice(101.6325);
        transactionDto.setPromotionId(Arrays.asList(1L, 2L));
        packageOrderDto.setProductOrder(productOrder);
        packageOrderDto.setUserOrderDto(userOrderDto);
        packageOrderDto.setTransactionDto(transactionDto);
        return packageOrderService.packageOrder(packageOrderDto1, paymentId, payerId);
    }
}
