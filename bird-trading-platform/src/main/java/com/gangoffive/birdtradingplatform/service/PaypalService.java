package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PaymentDto;
import com.gangoffive.birdtradingplatform.dto.PayoutDto;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaypalService {
    private final APIContext apiContext;

    public Payment createPayment(PaymentDto paymentDto) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(paymentDto.getCurrency());
        paymentDto.setTotal(new BigDecimal(paymentDto.getTotal()).setScale(2, RoundingMode.HALF_UP).doubleValue());
        amount.setTotal(String.format("%.2f", paymentDto.getTotal()));

        Transaction transaction = new Transaction();
        transaction.setDescription(paymentDto.getDescription());
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(paymentDto.getMethod().toString());

        Payment payment = new Payment();
        payment.setIntent(paymentDto.getIntent().toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(paymentDto.getCancelUrl());
        redirectUrls.setReturnUrl(paymentDto.getSuccessUrl());
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    public void createPayout(PayoutDto payoutDto) {
        try {
            // Create a payout
            Payout payout = new Payout();
            payout.setSenderBatchHeader(createSenderBatchHeader(payoutDto.getEmailSubject()));
            payout.setItems(
                    createPayoutItems(
                            payoutDto.getEmail(), payoutDto.getCurrency(),
                            payoutDto.getTotal(), payoutDto.getDescription())
            );

            // Create the payout request
            Map<String, String> headers = new HashMap<>();

            // Execute the payout
            PayoutBatch payoutBatch = payout.create(apiContext, headers);

            System.out.println("Payout successful!");

        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }
    }

    private PayoutSenderBatchHeader createSenderBatchHeader(String emailSubject) {
        PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();
        senderBatchHeader.setSenderBatchId("PAYPAL_" + UUID.randomUUID().toString());
        senderBatchHeader.setEmailSubject(emailSubject);
        return senderBatchHeader;
    }

    private List<PayoutItem> createPayoutItems(String email, String currency, double total, String note) {
        List<PayoutItem> items = new ArrayList<>();

        // Create a single payout item
        PayoutItem item = new PayoutItem();
        item.setRecipientType("EMAIL");
        item.setReceiver(email);
        item.setAmount(new Currency(currency, String.valueOf(total)));
        item.setNote(note);
        items.add(item);
        return items;
    }
}
