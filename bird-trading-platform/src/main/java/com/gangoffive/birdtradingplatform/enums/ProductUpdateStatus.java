package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.dto.ProductStatusShopChangeDto;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public enum ProductUpdateStatus {
    DELETE(-1, ProductStatus.DELETE),
    INACTIVE (0, ProductStatus.INACTIVE),
    ACTIVE(1, ProductStatus.ACTIVE);
    private int status;

    private ProductStatus productStatus;

    ProductUpdateStatus(int status, ProductStatus productStatus) {
        this.status = status;
        this.productStatus = productStatus;
    }

    public int getStatus() {
        return status;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public static ProductUpdateStatus getProductUpdateStatusEnum (int status) {
        List<ProductUpdateStatus> listStatus = Arrays.asList(ProductUpdateStatus.values());
        try {
            ProductUpdateStatus result = listStatus.stream().filter(sta -> sta.getStatus() == status).findFirst().get();
            return result;
        }catch (Exception e) {
            throw new CustomRuntimeException(HttpStatus.NOT_FOUND.name(), "Not found this status");
        }
    }
}
