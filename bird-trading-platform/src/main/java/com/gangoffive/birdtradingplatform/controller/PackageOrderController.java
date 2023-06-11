package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.PackageOrderDto;
import com.gangoffive.birdtradingplatform.dto.TransactionDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.service.PackageOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PackageOrderController {

    private final PackageOrderService packageOrderService;
    @RequestMapping(value = "api/v1/package-order", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getPackageOrder(
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestParam(value = "PayerID", required = false) String payerId
    ) {
        PackageOrderDto packageOrderDto = new PackageOrderDto();
        Map<Long, Integer> productOrder = new HashMap<>();
        productOrder.put(1L, 2);
        productOrder.put(2L, 2);
        productOrder.put(4L, 1);
        UserOrderDto userOrderDto = new UserOrderDto();
        userOrderDto.setName("hoangtien");
        userOrderDto.setEmail("andanhgen@gmail.com");
        userOrderDto.setPhoneNumber("sfadfa");
        userOrderDto.setStreet("sdafsd");
        userOrderDto.setWard("uasfd");
        userOrderDto.setDistrict("sdfadsa");
        userOrderDto.setCity("asdfa");
        userOrderDto.setCity("hoangtien");

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setPaymentMethod(PaymentMethod.PAYPAL);
        transactionDto.setTotalPrice(96.63);
        transactionDto.setPromotionId(Arrays.asList(2L, 3L));
        packageOrderDto.setProductOrder(productOrder);
        packageOrderDto.setUserOrderDto(userOrderDto);
        packageOrderDto.setTransactionDto(transactionDto);
        return packageOrderService.packageOrder(packageOrderDto, paymentId, payerId);
    }
}
