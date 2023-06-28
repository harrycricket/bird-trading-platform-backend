package com.gangoffive.birdtradingplatform.common;

import com.gangoffive.birdtradingplatform.enums.ProductStatus;

import java.util.Arrays;
import java.util.List;

public class ProductStatusConstant {
    public static List<ProductStatus> LIST_STATUS_GET_FOR_USER = Arrays.asList(ProductStatus.ACTIVE);
    public static int QUANTITY_PRODUCT_FOR_USER = 0;
    public static List<ProductStatus> LIST_STATUS_GET_FOR_SHOP_OWNER = Arrays.asList(ProductStatus.INACTIVE, ProductStatus.ACTIVE, ProductStatus.BAN);
    public static List<ProductStatus> LIST_STATUS_GET_FOR_ADMIN = Arrays.asList(ProductStatus.INACTIVE, ProductStatus.ACTIVE, ProductStatus.BAN);
}
