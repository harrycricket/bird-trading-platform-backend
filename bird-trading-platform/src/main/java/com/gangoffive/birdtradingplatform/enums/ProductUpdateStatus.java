package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.dto.ProductStatusShopChangeDto;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public enum ProductUpdateStatus {
    DELETE(-1, true, true),
    INACTIVE (0, true, false),
    ACTIVE(1, false, false);
    private int status;
    private boolean delete;
    private boolean hidden;

    ProductUpdateStatus(int status, boolean delete, boolean hidden) {
        this.status = status;
        this.delete = delete;
        this.hidden = hidden;
    }

    public int getStatus() {
        return status;
    }

    public boolean isDelete() {
        return delete;
    }
    public boolean isHidden() {
        return hidden;
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
