package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.dto.PackageOrderDto;
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
    public ResponseEntity<?> packageOrder(PackageOrderDto packageOrderDto, String paymentId, String payerId) {
        // Capture the start time
        Instant startTime = Instant.now();
        // Your existing code...

        log.info("checkPromotion(packageOrderDto.getTransactionDto().getPromotionId()) {}", checkPromotion(packageOrderDto.getTransactionDto().getPromotionId()));
        log.info("checkListProduct(packageOrderDto.getProductOrder()) {}", checkListProduct(packageOrderDto.getProductOrder()));
        log.info("checkUserOrderDto(packageOrderDto.getUserOrderDto()) {}", checkUserOrderDto(packageOrderDto.getUserOrderDto()));
        log.info("checkTotalPrice(packageOrderDto) {}", checkTotalPrice(packageOrderDto));
        if (paymentId != null && payerId != null) {
            return handleSuccessPayment(packageOrderDto, paymentId, payerId);
        }
        if (
                checkPromotion(packageOrderDto.getTransactionDto().getPromotionId())
                        && checkListProduct(packageOrderDto.getProductOrder())
                        && checkUserOrderDto(packageOrderDto.getUserOrderDto())
                        && checkTotalPrice(packageOrderDto)
        ) {
            PaymentMethod paymentMethod = packageOrderDto.getTransactionDto().getPaymentMethod();
            if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
                return handleInitialPayment(packageOrderDto, paymentId, payerId);
            } else if (paymentMethod.equals(PaymentMethod.DELIVERY)) {
                saveAll(packageOrderDto);
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
        ZoneId databaseTimeZone = ZoneId.of("UTC");

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
        return productOrder.keySet()
                .stream()
                .allMatch(
                        productId -> productRepository.findById(productId).isPresent()
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

    private boolean checkTotalPrice(PackageOrderDto packageOrderDto) {
        double totalPriceOfAllProduct = calculateTotalPriceOfAllProduct(packageOrderDto.getProductOrder());
        double totalPriceAfterAddVoucher = 0;
        boolean hasShippingPromotion = false;
        log.info("totalPriceOfAllProduct {}", totalPriceOfAllProduct);
        if (packageOrderDto.getTransactionDto().getPromotionId() == null || packageOrderDto.getTransactionDto().getPromotionId().isEmpty()) {
            return (int) packageOrderDto.getTransactionDto().getTotalPrice() == (int) (totalPriceOfAllProduct + totalPriceOfAllProduct * 0.05);
        }
        List<Promotion> promotions = promotionRepository.findAllById(packageOrderDto.getTransactionDto().getPromotionId());
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
        return (int) packageOrderDto.getTransactionDto().getTotalPrice() == (int) totalPriceAfterAddVoucher;
    }

    private double calculatePriceAfterAddVoucher(double totalPrice, List<Promotion> promotions) {
        return promotions.stream()
                .mapToDouble(
                        promotion -> {
                            if (promotion.getType().equals(PromotionType.DISCOUNT)) {
                                return promotion.getDiscount();
                            } else if (promotion.getType().equals(PromotionType.SHIPPING)) {
                                return 0;
                            }
                            return 0;
                        })
                .reduce(totalPrice, (subtotal, discount) -> subtotal - discount);
    }

    private double calculateTotalPriceOfAllProduct(Map<Long, Integer> productOrder) {
        return productOrder.entrySet().stream().mapToDouble(
                entry -> {
                    Long productId = entry.getKey();
                    Integer quantity = entry.getValue();
                    Optional<Product> productOptional = productRepository.findById(productId);
                    Product product = productOptional.get();
                    double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
                    double discountPrice = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
                    return discountPrice * quantity;
                }
        ).sum();
    }

    private double calculateShippingFee(PackageOrderDto packageOrderDto) {
        double totalPriceProduct = calculateTotalPriceOfAllProduct(packageOrderDto.getProductOrder());
        //5 % for shipping fee để tạm
        double shippingFee = totalPriceProduct * 0.05;
        String formattedShippingFee = String.format("%.2f", shippingFee);
        return Double.parseDouble(formattedShippingFee);
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
            Address address = account.get().getAddress();
            if (address == null) {
                address = new Address();
            }
            address.setPhone(userOrderDto.getPhoneNumber());
            address.setStreet(userOrderDto.getStreet());
            address.setWard(userOrderDto.getWard());
            address.setDistrict(userOrderDto.getDistrict());
            address.setCity(userOrderDto.getCity());
            addressRepository.save(address);
            account.get().setAddress(address);
            accountRepository.save(account.get());
            return account.get();
        }
        return account.get();
    }

    private PackageOrder savePackageOrder(
            PackageOrderDto packageOrderDto, Account account,
            Transaction transaction, double discount, double shippingFee
    ) {
        log.info("packageOrderDto {}", packageOrderDto.toString());
        log.info("transaction {}", transaction.toString());
        log.info("discount {}", discount);
        transactionRepository.save(transaction);
        log.info("shippingFee {}", shippingFee);
        PackageOrder packageOrder = PackageOrder.builder()
                .totalPrice(packageOrderDto.getTransactionDto().getTotalPrice())
                .discount(discount)
                .shippingFee(shippingFee)
                .paymentMethod(packageOrderDto.getTransactionDto().getPaymentMethod())
                .account(account)
                .transaction(transaction)
                .shippingAddress(account.getAddress())
                .build();
        if (
                packageOrderDto.getTransactionDto().getPromotionId() != null
                        && !packageOrderDto.getTransactionDto().getPromotionId().isEmpty()
        ) {
            packageOrder.setPromotions(promotionRepository.findAllById(packageOrderDto.getTransactionDto().getPromotionId()));
        }
        packageOrderRepository.save(packageOrder);
        return packageOrder;
    }

    private List<Order> saveOrder(PackageOrder packageOrder, PackageOrderDto packageOrderDto) {
        List<Order> orderList = new ArrayList<>();
        List<Long> productListId = getListProductId(packageOrderDto);
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
                                            return priceAfterDiscount * packageOrderDto.getProductOrder().get(product.getId());
                                        }
                                )
                        )
                );

        totalPriceByShop.entrySet()
                .stream()
                .forEach(
                        price -> log.info("key {} value {}", price.getKey().toString(), price.getValue().toString())
                );
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

    private List<OrderDetail> saveOrderDetails(List<Order> orders, PackageOrderDto packageOrderDto) {
        List<Long> productListId = getListProductId(packageOrderDto);
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
//                                    .quantity(packageOrderDto.getProductOrder().get(product.getId()))
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
                            double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
                            double discountedPrice = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
                            OrderDetail orderDetail = OrderDetail.builder()
                                    .order(order)
                                    .product(product)
                                    .price(discountedPrice)
                                    .quantity(packageOrderDto.getProductOrder().get(product.getId()))
                                    .build();
                            orderDetails.add(orderDetail);
                            orderDetailRepository.save(orderDetail);
                        })
                );
        return orderDetails;
    }

    @Transactional
    public void saveAll(PackageOrderDto packageOrderDto) {
        Transaction transaction = Transaction.builder()
                .amount(packageOrderDto.getTransactionDto().getTotalPrice())
                .status(TransactionStatus.PROCESSING)
                .build();
        if (packageOrderDto.getTransactionDto().getPaymentMethod().equals(PaymentMethod.PAYPAL)) {
            transaction.setStatus(TransactionStatus.SUCCESS);
        }
        Account account = saveUserOrderDto(packageOrderDto.getUserOrderDto());
        double shippingFee = 0;
        double discount = 0;
        if (
                packageOrderDto.getTransactionDto().getPromotionId() != null
                        && !packageOrderDto.getTransactionDto().getPromotionId().isEmpty()
        ) {
            List<Promotion> promotions = promotionRepository
                    .findAllById(packageOrderDto.getTransactionDto().getPromotionId());
            boolean isFreeShip = promotions.stream().anyMatch(promotion -> promotion.getType().equals(PromotionType.SHIPPING));

            for (Promotion promotion : promotions) {
                if (promotion.getType().equals(PromotionType.SHIPPING)) {
                    shippingFee = 0;
                    log.info("go here");
                } else if (promotion.getType().equals(PromotionType.DISCOUNT)) {
                    discount = promotion.getDiscount();
                    if (!isFreeShip) {
                        shippingFee = calculateShippingFee(packageOrderDto);
                    }
                }
            }

        } else {
            shippingFee = calculateShippingFee(packageOrderDto);
        }
        PackageOrder packageOrder = savePackageOrder(packageOrderDto, account, transaction, discount, shippingFee);
        List<Order> orders = saveOrder(packageOrder, packageOrderDto);
        saveOrderDetails(orders, packageOrderDto);
    }

    private ResponseEntity<?> handleInitialPayment(PackageOrderDto packageOrderDto, String paymentId, String payerId) {
        // Handle initial payment request
        try {
            String description = packageOrderDto.getUserOrderDto().getEmail()
                    + " pay with paypal for " + packageOrderDto.getTransactionDto().getTotalPrice();
            PaymentDto paymentDto = PaymentDto.builder()
                    .total(packageOrderDto.getTransactionDto().getTotalPrice())
                    .currency(Currency.USD.toString())
                    .method(PaymentMethod.PAYPAL)
                    .intent(PaypalPaymentIntent.SALE)
                    .description(description)
                    .successUrl("http://localhost:8080/api/v1/package-order?status=success")
                    .cancelUrl("https://www.birdland2nd.store/")
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

    private ResponseEntity<?> handleSuccessPayment(PackageOrderDto packageOrderDto, String paymentId, String payerId) {
        // Handle success payment
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            log.info("Payment {}", payment.toJSON());
            log.info("payerId id{}", payerId);
            log.info("paymentId id{}", paymentId);
            if (payment.getState().equals("approved")) {
                saveAll(packageOrderDto);
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

    private List<Long> getListProductId(PackageOrderDto packageOrderDto) {
        return packageOrderDto.getProductOrder()
                .entrySet()
                .stream()
                .map(
                        entry -> entry.getKey()
                )
                .collect(Collectors.toList());
    }
}
