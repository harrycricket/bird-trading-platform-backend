package com.gangoffive.birdtradingplatform.common;

import com.gangoffive.birdtradingplatform.enums.ShopOwnerStatus;

import java.util.Arrays;
import java.util.List;

public class ShopOwnerConstant {
    public static List<ShopOwnerStatus> STATUS_SHOP_PRODUCT_FOR_USER = Arrays.asList(ShopOwnerStatus.ACTIVE);

    public static List<String> STATUS_SHOP_PRODUCT_FOR_USER_STRING = Arrays.asList(ShopOwnerStatus.ACTIVE.name());
}
