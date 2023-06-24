package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.PackageOrderStatus;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class PackageOrderResponseDto {
    private Date createdDate;
    private Double totalPrice;
    private PackageOrderStatus status;
}
