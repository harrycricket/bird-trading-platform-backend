package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ApiResponse;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.dto.PackageOrderDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.enums.PromotionType;
import com.gangoffive.birdtradingplatform.enums.TransactionStatus;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.service.PackageOrderService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageOrderServiceImpl implements PackageOrderService {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final ProductService productService;
    private final AccountRepository accountRepository;
    private final PackageOrderRepository packageOrderRepository;
    private final TransactionRepository transactionRepository;
    //check totalPrice
    //check and save userOrderDto to DB
    //check paymentMethod
    // if paypal
    // // PaypalPayDto
    // if cod
    // response ok -> redirect to page Order list


    @Override
    public ResponseEntity<?> packageOrder(PackageOrderDto packageOrderDto) {
        if (
                checkPromotion(packageOrderDto.getTransactionDto().getPromotionId())
                        && checkListProduct(packageOrderDto.getProductOrder())
                        && checkUserOrderDto(packageOrderDto.getUserOrderDto())
                        && checkTotalPrice(packageOrderDto)
        ) {
            PaymentMethod paymentMethod = packageOrderDto.getTransactionDto().getPaymentMethod();
            if (paymentMethod.equals(PaymentMethod.PAYPAL)) {

                return ResponseEntity.ok().body("");
            } else if (paymentMethod.equals(PaymentMethod.DELIVERY)) {
                Transaction transaction = Transaction.builder()
                        .amount(packageOrderDto.getTransactionDto().getTotalPrice())
                        .status(TransactionStatus.PROCESSING)
                        .build();
                Account account = saveUserOrderDto(packageOrderDto.getUserOrderDto());
                double shippingFee = 0;
                double discount = 0;
                Optional<Promotion> promotion = promotionRepository.findById(packageOrderDto.getTransactionDto()
                        .getPromotionId());
                if (promotion.isPresent()) {
                    if (promotion.get().getType().equals(PromotionType.SHIPPING)) {
                        shippingFee = 0;
                    } else if (promotion.get().getType().equals(PromotionType.DISCOUNT)) {
                        discount = promotionRepository.findById(packageOrderDto.getTransactionDto().getPromotionId())
                                .get()
                                .getDiscount();
                        shippingFee = calculateShippingFee(packageOrderDto);
                    }
                }
                savePackageOrder(packageOrderDto, account, transaction, discount, shippingFee);

                SuccessResponse successResponse = SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.OK.value()))
                        .successMessage("Order successfully")
                        .build();
                ApiResponse apiResponse = new ApiResponse(LocalDateTime.now(), "Order successfully");
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Something went wrong");
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        } else {
            ErrorResponse error = new ErrorResponse(
                    String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
                    "Something went wrong");
            return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public boolean checkPromotion(Long promotionId) {
        if (promotionId == null) {
            return true;
        }
        Optional<Promotion> promotionOptional = promotionRepository.findById(promotionId);
        if (promotionOptional.isPresent()) {
            if (promotionOptional.get().getEndDate().after(new Date())) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean checkListProduct(Map<Long, Integer> productOrder) {
        return productOrder.keySet()
                .stream()
                .allMatch(
                        productId -> productRepository.findById(productId).isPresent()
                );
    }

    public boolean checkUserOrderDto(UserOrderDto userOrderDto) {
        if (
                !(userOrderDto.getName().isEmpty() || userOrderDto.getName() == null)
                && !(userOrderDto.getPhoneNumber().isEmpty() || userOrderDto.getPhoneNumber() == null)
                && !(userOrderDto.getStreet().isEmpty() || userOrderDto.getStreet() == null)
                && !(userOrderDto.getWard().isEmpty() || userOrderDto.getWard() == null)
                && !(userOrderDto.getDistrict().isEmpty() || userOrderDto.getDistrict() == null)
                && !(userOrderDto.getCity().isEmpty() || userOrderDto.getCity() == null)
        ) {
            return true;
        }
        return false;
    }

    private boolean checkTotalPrice(PackageOrderDto packageOrderDto) {
        double totalPriceOfAllProduct = calculateTotalPriceOfAllProduct(packageOrderDto.getProductOrder());
        Optional<Promotion> promotion = promotionRepository.findById(packageOrderDto.getTransactionDto().getPromotionId());
        double totalPriceAfterAddVoucher = calculatePriceAfterAddVoucher(totalPriceOfAllProduct, promotion.get());
        if (packageOrderDto.getTransactionDto().getTotalPrice() == totalPriceAfterAddVoucher)
            return true;
        return false;
    }

    private double calculatePriceAfterAddVoucher(double totalPrice, Promotion promotion) {
        if (promotion.getType().equals(PromotionType.DISCOUNT)) {
            return totalPrice - promotion.getDiscount();
        } else {
            //5 % for shipping fee để tạm
            return totalPrice - totalPrice * 0.05;
        }
    }

    private double calculateTotalPriceOfAllProduct(Map<Long, Integer> productOrder) {
        double totalPrice = productOrder.entrySet().stream().mapToDouble(
                entry -> {
                    Long productId = entry.getKey();
                    Integer quantity = entry.getValue();
                    Optional<Product> productOptional = productRepository.findById(productId);
                    Product product = productOptional.get();
                    double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
                    double discountPrice = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
                    return product.getPrice() * quantity - discountPrice * quantity;
                }
        ).sum();
        return totalPrice;
    }

    private double calculateShippingFee(PackageOrderDto packageOrderDto) {
        double totalPriceProduct = calculateTotalPriceOfAllProduct(packageOrderDto.getProductOrder());
        //5 % for shipping fee để tạm
        return totalPriceProduct * 0.05;
    }

    private Account saveUserOrderDto(UserOrderDto userOrderDto) {
        Optional<Account> account = accountRepository.findByEmail(userOrderDto.getEmail());
        if (account.isPresent()) {
            account.get().setFullName(userOrderDto.getName());
            account.get().setPhoneNumber(userOrderDto.getPhoneNumber());
            Address address = account.get().getAddress();
            address.setPhone(userOrderDto.getPhoneNumber());
            address.setStreet(userOrderDto.getStreet());
            address.setWard(userOrderDto.getWard());
            address.setDistrict(userOrderDto.getDistrict());
            address.setCity(userOrderDto.getCity());
            accountRepository.save(account.get());
            return account.get();
        }
        return account.get();
    }

    private boolean savePackageOrder(
            PackageOrderDto packageOrderDto, Account account,
            Transaction transaction, double discount, double shippingFee
    ) {
        PackageOrder packageOrder = PackageOrder.builder()
                .totalPrice(packageOrderDto.getTransactionDto().getTotalPrice())
                .discount(discount)
                .shippingFee(shippingFee)
                .paymentMethod(packageOrderDto.getTransactionDto().getPaymentMethod())
                .account(account)
                .transaction(transaction)
                .shippingAddress(account.getAddress())
                .build();
        packageOrderRepository.save(packageOrder);
        return true;
    }
}
