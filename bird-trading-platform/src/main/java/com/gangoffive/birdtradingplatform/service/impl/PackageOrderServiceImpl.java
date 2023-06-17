package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.dto.PackageOrderRequestDto;
import com.gangoffive.birdtradingplatform.dto.PaymentDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.Currency;
import com.gangoffive.birdtradingplatform.enums.*;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.service.PackageOrderService;
import com.gangoffive.birdtradingplatform.service.PaypalService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageOrderServiceImpl implements PackageOrderService {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductService productService;
    private final AccountRepository accountRepository;
    private final PackageOrderRepository packageOrderRepository;
    private final TransactionRepository transactionRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaypalService paypalService;
    //check totalPrice
    //check and save userOrderDto to DB
    //check paymentMethod
    // if Paypal
    // // PaypalPayDto
    // if cod
    // response ok -> redirect to page Order list


    @Override
    public ResponseEntity<?> packageOrder(PackageOrderRequestDto packageOrderRequestDto, String paymentId, String payerId) {
        // Capture the start time
        Instant startTime = Instant.now();
        // Your existing code...

//        log.info("checkPromotion(packageOrderRequestDto.getTransactionDto().getPromotionId()) {}", checkPromotion(packageOrderRequestDto.getTransactionDto().getPromotionId()));
        log.info("checkListProduct(packageOrderRequestDto.getProductOrder()) {}", checkListProduct(packageOrderRequestDto.getProductOrder()));
//        log.info("checkUserOrderDto(packageOrderRequestDto.getUserOrderDto()) {}", checkUserOrderDto(packageOrderRequestDto.getUserOrderDto()));
        log.info("checkTotalPrice(packageOrderRequestDto) {}", checkTotalPrice(packageOrderRequestDto));
        if (paymentId != null && payerId != null) {
            return handleSuccessPayment(packageOrderRequestDto, paymentId, payerId);
        }
        if (
                checkPromotion(packageOrderRequestDto.getTransactionDto().getPromotionId())
                        && checkListProduct(packageOrderRequestDto.getProductOrder())
                        && checkUserOrderDto(packageOrderRequestDto.getUserOrderDto())
                        && checkTotalPrice(packageOrderRequestDto)
        ) {
            PaymentMethod paymentMethod = packageOrderRequestDto.getTransactionDto().getPaymentMethod();
            if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
                return handleInitialPayment(packageOrderRequestDto);
            } else if (paymentMethod.equals(PaymentMethod.DELIVERY)) {
                saveAll(packageOrderRequestDto);
                // Capture the end time
                Instant endTime = Instant.now();
                // Calculate the duration
                Duration duration = Duration.between(startTime, endTime);
                long seconds = duration.getSeconds();
                log.info("Total time: {} seconds", seconds);
                SuccessResponse successResponse = SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.OK.value()))
                        .successMessage("Order successfully")
                        .build();
                return new ResponseEntity<>(successResponse, HttpStatus.OK);
            } else {
                ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Something went wrong");
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
        } else {
            ErrorResponse error = new ErrorResponse(
                    String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
                    "Something went wrong");
            log.info("here");
            return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public boolean checkPromotion(List<Long> listPromotionId) {
        if (listPromotionId == null || listPromotionId.isEmpty()) {
            return true;
        }
        List<Promotion> listPromotion = promotionRepository.findAllById(listPromotionId);
        if (listPromotion.size() != listPromotionId.size()) {
            return false;
        } else if (listPromotionId.size() > 2) {
            return false;
        }
        int shipping = 0;
        int discount = 0;
        for (Promotion promotion : listPromotion) {
            if (promotion.getType().equals(PromotionType.SHIPPING)) {
                shipping++;
            } else if (promotion.getType().equals(PromotionType.DISCOUNT)) {
                discount++;
            }
        }
        if (shipping > 1 || discount > 1) {
            return false;
        }

        boolean hasValidPromotion = false;
        // Assuming your database uses the UTC time zone
        ZoneId databaseTimeZone = ZoneId.of("Asia/Bangkok");

        LocalDateTime currentDate = new Date().toInstant().atZone(databaseTimeZone).toLocalDateTime();
//        log.info("currentDate {}", currentDate);
        for (Promotion promotion : listPromotion) {
            if (promotion.getEndDate().toInstant().atZone(databaseTimeZone).toLocalDateTime().isAfter(currentDate)) {
//                log.info("promotion.getEndDate() {}", promotion.getEndDate().toInstant().atZone(databaseTimeZone).toLocalDateTime());
                hasValidPromotion = true;
            } else {
                hasValidPromotion = false;
                break;
            }
        }
        return hasValidPromotion;
//        return listPromotion.stream().anyMatch(promotion -> promotion.getEndDate().after(new Date()));
    }

    public boolean checkListProduct(Map<Long, Integer> productOrder) {
//        for (Long id : productOrder.keySet()) {
//            if (!productRepository.findById(id).isPresent()) {
//                return false;
//            } else {
//                if (productRepository.findById(id).get().getQuantity() >= productOrder.get(id)) {
//                    return false;
//                }
//            }
//        }
//        return true;
        return productOrder.keySet()
                .stream()
                .allMatch(
                        productId -> {
                            Optional<Product> productOptional = productRepository.findById(productId);
                            if (!productOptional.isPresent()) {
                                return false;
                            } else {
                                Product product = productOptional.get();
                                return product.getQuantity() >= productOrder.get(productId);
                            }
                        }
                );
    }

    public boolean checkUserOrderDto(UserOrderDto userOrderDto) {
        return !(userOrderDto.getName() == null || userOrderDto.getName().isEmpty())
                && !(userOrderDto.getPhoneNumber() == null || userOrderDto.getPhoneNumber().isEmpty())
                && !(userOrderDto.getStreet() == null || userOrderDto.getStreet().isEmpty())
                && !(userOrderDto.getWard() == null || userOrderDto.getWard().isEmpty())
                && !(userOrderDto.getDistrict() == null || userOrderDto.getDistrict().isEmpty())
                && !(userOrderDto.getCity() == null || userOrderDto.getCity().isEmpty())
                && accountRepository.findByEmail(userOrderDto.getEmail()).isPresent();
    }

    private boolean checkTotalPrice(PackageOrderRequestDto packageOrderRequestDto) {
        double totalPriceOfAllProduct = calculateTotalPriceOfAllProduct(packageOrderRequestDto.getProductOrder());
        double totalPriceAfterAddVoucher = 0;
        boolean hasShippingPromotion = false;
        log.info("totalPriceOfAllProduct {}", totalPriceOfAllProduct);
        if (packageOrderRequestDto.getTransactionDto().getPromotionId() == null || packageOrderRequestDto.getTransactionDto().getPromotionId().isEmpty()) {
            log.info("Math.round((totalPriceOfAllProduct + totalPriceOfAllProduct * 0.05) * 100 / 100) {}", Math.round((totalPriceOfAllProduct + totalPriceOfAllProduct * 0.05) * 100.0) / 100.0);

            return packageOrderRequestDto.getTransactionDto().getTotalPrice() == Math.round((totalPriceOfAllProduct + totalPriceOfAllProduct * 0.05) * 100.0) / 100.0;
        }
        List<Promotion> promotions = promotionRepository.findAllById(packageOrderRequestDto.getTransactionDto().getPromotionId());
        for (Promotion promotion : promotions) {
            if (promotion.getType().equals(PromotionType.SHIPPING)) {
                totalPriceAfterAddVoucher = calculatePriceAfterAddVoucher(totalPriceOfAllProduct, promotions);
                hasShippingPromotion = true;
                break;
            }
        }
        if (!hasShippingPromotion) {
            totalPriceAfterAddVoucher = calculatePriceAfterAddVoucher(totalPriceOfAllProduct, promotions) + totalPriceOfAllProduct * 0.05;
        }

        log.info("totalPriceAfterAddVoucher {}", totalPriceAfterAddVoucher);
        log.info("Math.round(totalPriceAfterAddVoucher * 100 / 100) {}", (Math.round(totalPriceAfterAddVoucher * 100.0) / 100.0));
        return packageOrderRequestDto.getTransactionDto().getTotalPrice() == Math.round(totalPriceAfterAddVoucher * 100.0) / 100.0;
    }

    private double calculatePriceAfterAddVoucher(double totalPrice, List<Promotion> promotions) {
        return Math.round(
                (
                promotions.stream()
                .mapToDouble(
                        promotion -> {
                            if (promotion.getType().equals(PromotionType.DISCOUNT)) {
                                return promotion.getDiscount();
                            } else if (promotion.getType().equals(PromotionType.SHIPPING)) {
                                return 0;
                            }
                            return 0;
                        })
                .reduce(totalPrice, (subtotal, discount) -> subtotal - discount)
                ) * 100.0) / 100.0;
    }

    private double calculateTotalPriceOfAllProduct(Map<Long, Integer> productOrder) {
        return Math.round(
                (
                        productOrder.entrySet().stream().mapToDouble(
                                entry -> {
                                    Long productId = entry.getKey();
                                    Integer quantity = entry.getValue();
                                    Optional<Product> productOptional = productRepository.findById(productId);
                                    Product product = productOptional.get();
                                    double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
                                    double discountPrice = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
                                    return Math.round((discountPrice * quantity) * 100.0) / 100.0;
                                }
                        ).sum()
                ) * 100.0
        ) / 100.0;
    }

    private double calculateShippingFee(PackageOrderRequestDto packageOrderRequestDto) {
        double totalPriceProduct = calculateTotalPriceOfAllProduct(packageOrderRequestDto.getProductOrder());
        //5 % for shipping fee để tạm
        double shippingFee = totalPriceProduct * 0.05;
        return Math.round(shippingFee * 100.0) / 100.0;
    }

    private Account saveUserOrderDto(UserOrderDto userOrderDto) {
        log.info("userOrderDto.toString(); {}", userOrderDto.toString());
        Optional<Account> account = accountRepository.findByEmail(userOrderDto.getEmail());
        if (account.isPresent()) {
            log.info("true");
        }
        if (account.isPresent()) {
            account.get().setFullName(userOrderDto.getName());
            account.get().setPhoneNumber(userOrderDto.getPhoneNumber());
            accountRepository.save(account.get());
            return account.get();
        }
        return account.get();
    }

    private PackageOrder savePackageOrder(
            PackageOrderRequestDto packageOrderRequestDto, Account account,
            Transaction transaction, double discount, double shippingFee
    ) {
        log.info("packageOrderRequestDto {}", packageOrderRequestDto.toString());
        log.info("transaction {}", transaction.toString());
        log.info("discount {}", discount);
        transactionRepository.save(transaction);
        log.info("shippingFee {}", shippingFee);
        Address address = new Address();
        address.setPhone(packageOrderRequestDto.getUserOrderDto().getPhoneNumber());
        address.setStreet(packageOrderRequestDto.getUserOrderDto().getStreet());
        address.setWard(packageOrderRequestDto.getUserOrderDto().getWard());
        address.setDistrict(packageOrderRequestDto.getUserOrderDto().getDistrict());
        address.setCity(packageOrderRequestDto.getUserOrderDto().getCity());
        addressRepository.save(address);
        PackageOrder packageOrder = PackageOrder.builder()
                .totalPrice(packageOrderRequestDto.getTransactionDto().getTotalPrice())
                .discount(discount)
                .shippingFee(shippingFee)
                .paymentMethod(packageOrderRequestDto.getTransactionDto().getPaymentMethod())
                .account(account)
                .transaction(transaction)
                .shippingAddress(address)
                .build();
        if (
                packageOrderRequestDto.getTransactionDto().getPromotionId() != null
                        && !packageOrderRequestDto.getTransactionDto().getPromotionId().isEmpty()
        ) {
            packageOrder.setPromotions(promotionRepository.findAllById(packageOrderRequestDto.getTransactionDto().getPromotionId()));
        }
        packageOrderRepository.save(packageOrder);
        return packageOrder;
    }

    private List<Order> saveOrder(PackageOrder packageOrder, PackageOrderRequestDto packageOrderRequestDto) {
        List<Order> orderList = new ArrayList<>();
        List<Long> productListId = getListProductId(packageOrderRequestDto);
        List<Product> products = productRepository.findAllById(productListId);
        products.stream().forEach(s -> log.info("pro {}", s.getId()));
        List<ShopOwner> shops = getListShopOwners(products);

        shops.stream().forEach(s -> log.info("shop {}", s.getId()));

        Map<Long, Double> totalPriceByShop = products.stream()
                .collect(
                        Collectors.groupingBy(
                                product -> product.getShopOwner().getId(),
                                Collectors.summingDouble(
                                        product -> {
                                            double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
                                            double priceAfterDiscount = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
                                            return priceAfterDiscount * packageOrderRequestDto.getProductOrder().get(product.getId());
                                        }
                                )
                        )
                );

        totalPriceByShop.entrySet()
                .stream()
                .forEach(
                        price -> log.info("key {} value {}", price.getKey().toString(), price.getValue().toString())
                );

        for (Long id : totalPriceByShop.keySet()) {
            totalPriceByShop.put(id, Math.round(totalPriceByShop.get(id) * 100.0) / 100.0);
        }

        shops.stream().forEach(shopOwner -> {
            List<PromotionShop> promotionShops = products.stream()
                    .filter(
                            product -> product.getShopOwner().equals(shopOwner)
                    ).map(Product::getPromotionShops)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            Order order = Order.builder()
                    .packageOrder(packageOrder)
                    .status(OrderStatus.PROCESSING)
                    .shopOwner(shopOwner)
                    .promotionShops(promotionShops)
                    .totalPrice(totalPriceByShop.get(shopOwner.getId()))
                    .build();
            orderList.add(order);
            orderRepository.save(order);
        });
        return orderList;
    }

    private List<OrderDetail> saveOrderDetails(List<Order> orders, PackageOrderRequestDto packageOrderRequestDto) {
        List<Long> productListId = getListProductId(packageOrderRequestDto);
        List<Product> products = productRepository.findAllById(productListId);
        List<ShopOwner> shopOwners = getListShopOwners(products);
        List<OrderDetail> orderDetails = new ArrayList<>();
//        for (Order order : orders) {
//            for (ShopOwner shopOwner : shopOwners) {
//                if (order.getShopOwner().equals(shopOwner)) {
//                    for (Product product : products) {
//                        if (product.getShopOwner().equals(shopOwner)) {
//                            double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
//                            double discountedPrice = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
//                            OrderDetail orderDetail = OrderDetail.builder()
//                                    .order(order)
//                                    .product(product)
//                                    .price(discountedPrice)
//                                    .quantity(packageOrderRequestDto.getProductOrder().get(product.getId()))
//                                    .build();
//                            orderDetails.add(orderDetail);
//                            orderDetailRepository.save(orderDetail);
//                        }
//                    }
//                }
//            }
//        }

        orders.stream()
                .filter(order -> shopOwners.contains(order.getShopOwner()))
                .forEach(order -> products.stream()
                        .filter(product -> product.getShopOwner().equals(order.getShopOwner()))
                        .forEach(product -> {
                            int newQuantity = product.getQuantity() - packageOrderRequestDto.getProductOrder().get(product.getId());
                            double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
                            double discountedPrice = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
                            OrderDetail orderDetail = OrderDetail.builder()
                                    .order(order)
                                    .product(product)
                                    .price(discountedPrice)
                                    .quantity(packageOrderRequestDto.getProductOrder().get(product.getId()))
                                    .build();
                            orderDetails.add(orderDetail);
                            product.setQuantity(newQuantity);
                            productRepository.save(product);
                            orderDetailRepository.save(orderDetail);
                        })
                );
        return orderDetails;
    }

    @Transactional
    public void saveAll(PackageOrderRequestDto packageOrderRequestDto) {
        Transaction transaction = Transaction.builder()
                .amount(packageOrderRequestDto.getTransactionDto().getTotalPrice())
                .status(TransactionStatus.PROCESSING)
                .build();
        if (packageOrderRequestDto.getTransactionDto().getPaymentMethod().equals(PaymentMethod.PAYPAL)) {
            transaction.setStatus(TransactionStatus.SUCCESS);
        }
        Account account = saveUserOrderDto(packageOrderRequestDto.getUserOrderDto());
        double shippingFee = 0;
        double discount = 0;
        if (
                packageOrderRequestDto.getTransactionDto().getPromotionId() != null
                        && !packageOrderRequestDto.getTransactionDto().getPromotionId().isEmpty()
        ) {
            List<Promotion> promotions = promotionRepository
                    .findAllById(packageOrderRequestDto.getTransactionDto().getPromotionId());
            boolean isFreeShip = promotions.stream().anyMatch(promotion -> promotion.getType().equals(PromotionType.SHIPPING));

            for (Promotion promotion : promotions) {
                if (promotion.getType().equals(PromotionType.SHIPPING)) {
                    shippingFee = 0;
                    log.info("go here");
                } else if (promotion.getType().equals(PromotionType.DISCOUNT)) {
                    discount = promotion.getDiscount();
                    if (!isFreeShip) {
                        shippingFee = calculateShippingFee(packageOrderRequestDto);
                    }
                }
            }

        } else {
            shippingFee = calculateShippingFee(packageOrderRequestDto);
        }
        PackageOrder packageOrder = savePackageOrder(packageOrderRequestDto, account, transaction, discount, shippingFee);
        List<Order> orders = saveOrder(packageOrder, packageOrderRequestDto);
        saveOrderDetails(orders, packageOrderRequestDto);
    }

    private ResponseEntity<?> handleInitialPayment(PackageOrderRequestDto packageOrderRequestDto) {
        // Handle initial payment request
        try {
            String description = packageOrderRequestDto.getUserOrderDto().getEmail()
                    + " pay with paypal for " + packageOrderRequestDto.getTransactionDto().getTotalPrice();
            PaymentDto paymentDto = PaymentDto.builder()
                    .total(packageOrderRequestDto.getTransactionDto().getTotalPrice())
                    .currency(Currency.USD.toString())
                    .method(PaymentMethod.PAYPAL)
                    .intent(PaypalPaymentIntent.SALE)
                    .description(description)
                    .successUrl("https://thongtienthienphuot.shop/api/v1/package-order?status=success")
                    .cancelUrl("http://localhost:3000/checkout")
                    .build();
            Payment payment = paypalService.createPayment(paymentDto);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    log.info("link approval_url {}", link.getHref());
                    SuccessResponse successResponse = SuccessResponse.builder()
                            .successCode(String.valueOf(HttpStatus.OK.value()))
                            .successMessage("Redirect: " + link.getHref())
                            .build();
                    return new ResponseEntity<>(successResponse, HttpStatus.OK);
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        log.info("fail");
        ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()),
                "Payment with paypal failed.");
        return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
    }

    private ResponseEntity<?> handleSuccessPayment(PackageOrderRequestDto packageOrderRequestDto, String paymentId, String payerId) {
        // Handle success payment
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            log.info("Payment {}", payment.toJSON());
            log.info("payerId id{}", payerId);
            log.info("paymentId id{}", paymentId);
            if (payment.getState().equals("approved")) {
                saveAll(packageOrderRequestDto);
                SuccessResponse successResponse = SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.OK.value()))
                        .successMessage("Payment with paypal successful.")
                        .build();

                return ResponseEntity.status(HttpStatus.OK)
                        .body(successResponse);
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()),
                "Payment with paypal failed.");
        return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
    }

    private List<ShopOwner> getListShopOwners(List<Product> products) {
        Comparator<ShopOwner> shopOwnerComparator = Comparator.comparing(ShopOwner::getId);
        return products.stream()
                .map(Product::getShopOwner)
                .distinct()
                .sorted(shopOwnerComparator)
                .collect(Collectors.toList());
    }

    private List<Long> getListProductId(PackageOrderRequestDto packageOrderRequestDto) {
        return packageOrderRequestDto.getProductOrder()
                .entrySet()
                .stream()
                .map(
                        entry -> entry.getKey()
                )
                .collect(Collectors.toList());
    }
}
