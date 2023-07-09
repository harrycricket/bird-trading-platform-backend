package com.gangoffive.birdtradingplatform.service;


import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Test
@Slf4j
public class PackageOrderServiceTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private PackageOrderService packageOrderService;

    public PackageOrderServiceTest() {
    }

    public UserOrderDto dataUserOrder() {
        return UserOrderDto.builder()
                .fullName("Cao Nhat Thien")
                .phoneNumber("0857975552")
                .address("Cay co don, Lam Dong, DaLat")
                .build();
    }

    public CartDto dataCartInfo() {
        ItemByShopDto itemByShopOne = ItemByShopDto.builder()
                .totalShopPrice(141.8)
                .shippingFee(7.5)
                .distance(150)
                .shopId(1L)
                .listItems(Map.of(90026L, 4, 90043L, 2))
                .build();
        ItemByShopDto itemByShopThree = ItemByShopDto.builder()
                .totalShopPrice(338.62)
                .shippingFee(11.99)
                .distance(342.5)
                .shopId(3L)
                .listItems(Map.of(5L, 2, 6L, 3, 9L, 5, 12L, 2, 18L, 1, 90029L, 2))
                .build();
        ItemByShopDto itemByShopFour = ItemByShopDto.builder()
                .totalShopPrice(199)
                .shippingFee(5.5)
                .distance(100)
                .shopId(4L)
                .listItems(Map.of(15L, 1))
                .build();
        List<ItemByShopDto> itemsByShop = new ArrayList<>();
        itemsByShop.add(itemByShopOne);
        itemsByShop.add(itemByShopThree);
        itemsByShop.add(itemByShopFour);
//        List<Long> promotionIds = null;
        TotalOrderDto totalOrder = TotalOrderDto.builder()
                .subTotal(679.42)
                .shippingTotal(24.99)
                .promotionFee(0)
                .paymentTotal(704.41)
                .build();
        return CartDto.builder()
                .itemsByShop(itemsByShop)
                .promotionIds(null)
                .paymentMethod(PaymentMethod.DELIVERY)
                .total(totalOrder)
                .build();
    }

    public Map<Long, Integer> getMapProductQuantity() {
        Map<Long, Integer> mapProductQuantity = new HashMap<>();
        CartDto cart = dataCartInfo();
        cart.getItemsByShop().forEach(item -> mapProductQuantity.putAll(item.getListItems()));
        return mapProductQuantity;
    }

    public PackageOrderRequestDto dataPackageOrderDto() {
        return PackageOrderRequestDto.builder()
                .userInfo(dataUserOrder())
                .cartInfo(dataCartInfo())
                .build();
    }

    @DataProvider(name = "testCaseUserInfo")
    public Object[][] testCaseUserInfo() {
        return new Object[][]{
                {dataUserOrder(), true}
        };
    }

    @DataProvider(name = "testCaseProduct")
    public Object[][] testCaseProduct() {
        return new Object[][]{
                {Map.of(90026L, 4), true},
                {Map.of(90043L, 2), true},
                {Map.of(5L, 2), true},
                {Map.of(6L, 3), true},
                {Map.of(9L, 5 ), true},
                {Map.of(12L, 2), true},
                {Map.of(18L, 1), true},
                {Map.of(90029L, 2), true},
                {Map.of(15L, 1), true}
        };
    }

    @Test(dataProvider = "testCaseUserInfo", priority = 1)
    public void checkDataUserInfo(UserOrderDto userOrder, boolean expectedValue) {
        boolean actualValue = packageOrderService.checkUserOrderDto(userOrder);
        Assert.assertEquals(actualValue, expectedValue);
    }


    @Test(dataProvider = "testCaseProduct",priority = 2)
    public void checkListProduct(Map<Long, Integer> mapProductQuantity, boolean expectedValue) {
        boolean actualValue = packageOrderService.checkListProduct(mapProductQuantity);
        Assert.assertEquals(actualValue, expectedValue);
    }

    @Test(priority = 3)
    public void checkDataPromotion() {
        boolean actualValue = packageOrderService.checkPromotion(dataPackageOrderDto(), getMapProductQuantity());
        Assert.assertTrue(actualValue);
    }

    @Test(priority = 4)
    @Transactional
    public void checkTotalShopPrice() {
        boolean actualValue = packageOrderService.checkTotalShopPrice(dataPackageOrderDto().getCartInfo().getItemsByShop());
        Assert.assertTrue(actualValue);
    }

    @Test(priority = 5)
    @Transactional
    public void checkSubTotal() {
        double expectedValue = 679.42;
        Assert.assertTrue(packageOrderService.checkSubTotal(expectedValue, getMapProductQuantity()));
    }

    @Test(priority = 6)
    @Transactional
    public void checkTotalShippingFee() {
        boolean actualValue = packageOrderService.checkTotalShippingFee(dataPackageOrderDto());
        Assert.assertTrue(actualValue);
    }

    @Test(priority = 7)
    @Transactional
    public void checkTotalDiscount() {
        boolean actualValue = packageOrderService.checkTotalDiscount(dataPackageOrderDto());
        Assert.assertTrue(actualValue);
    }

    @Test(priority = 8)
    @Transactional
    public void checkTotalPayment() {
        boolean actualValue = packageOrderService.checkTotalPayment(dataPackageOrderDto().getCartInfo().getTotal());
        Assert.assertTrue(actualValue);
    }
}

