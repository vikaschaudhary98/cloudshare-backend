package in.vikaschaudhary.cloudshareapi.service;

import java.time.LocalDateTime;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import in.vikaschaudhary.cloudshareapi.document.PaymentTransaction;
import in.vikaschaudhary.cloudshareapi.document.ProfileDocument;
import in.vikaschaudhary.cloudshareapi.dto.PaymentDTO;
import in.vikaschaudhary.cloudshareapi.dto.PaymentVerificationDTO;
import in.vikaschaudhary.cloudshareapi.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    // ================= CREATE ORDER =================

    public PaymentDTO createOrder(PaymentDTO paymentDTO) {

        try {

            ProfileDocument currentProfile = profileService.getCurrentProfile();
            String clerkId = currentProfile.getClerkId();

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", paymentDTO.getAmount());
            orderRequest.put("currency", paymentDTO.getCurrency());
            orderRequest.put("receipt", "order_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);

            String orderId = order.get("id");

            PaymentTransaction transaction = PaymentTransaction.builder()
                    .clerkId(clerkId)
                    .orderId(orderId)
                    .planId(paymentDTO.getPlanId())
                    .amount(paymentDTO.getAmount())
                    .currency(paymentDTO.getCurrency())
                    .status("PENDING")
                    .transactionDate(LocalDateTime.now())
                    .userEmail(currentProfile.getEmail())
                    .userName(currentProfile.getFirstName() + " " + currentProfile.getLastName())
                    .build();

            paymentTransactionRepository.save(transaction);

            return PaymentDTO.builder()
                    .orderId(orderId)
                    .success(true)
                    .message("Order created successfully")
                    .build();

        } catch (Exception e) {

            return PaymentDTO.builder()
                    .success(false)
                    .message("Error creating order " + e.getMessage())
                    .build();
        }
    }

    // ================= VERIFY PAYMENT =================

    public PaymentDTO verifyPayment(PaymentVerificationDTO request) {

        try {

            ProfileDocument currentProfile = profileService.getCurrentProfile();
            String clerkId = currentProfile.getClerkId();

            String data = request.getRazorpay_order_id() + "|" + request.getRazorpay_payment_id();

            String generatedSignature = generateHmacSha256Signature(data, razorpayKeySecret);

            if (!generatedSignature.equals(request.getRazorpay_signature())) {

                updateTransactionStatus(request.getRazorpay_order_id(), "FAILED",
                        request.getRazorpay_payment_id(), null);

                return PaymentDTO.builder()
                        .success(false)
                        .message("Payment signature verification failed")
                        .build();
            }

            int creditsToAdd = 0;
            String plan = "BASIC";

            switch (request.getPlanId()) {

                case "premium":
                    creditsToAdd = 500;
                    plan = "PREMIUM";
                    break;

                case "ultimate":
                    creditsToAdd = 5000;
                    plan = "ULTIMATE";
                    break;
            }

            if (creditsToAdd > 0) {

                userCreditsService.addCredits(clerkId, creditsToAdd, plan);

                updateTransactionStatus(request.getRazorpay_order_id(),
                        "SUCCESS",
                        request.getRazorpay_payment_id(),
                        creditsToAdd);

                return PaymentDTO.builder()
                        .success(true)
                        .credits(userCreditsService.getUserCredits(clerkId).getCredits())
                        .message("Payment successful")
                        .build();
            }

            else {

                updateTransactionStatus(request.getRazorpay_order_id(),
                        "FAILED",
                        request.getRazorpay_payment_id(),
                        null);

                return PaymentDTO.builder()
                        .success(false)
                        .message("Invalid plan selected")
                        .build();
            }

        } catch (Exception e) {

            try {

                updateTransactionStatus(request.getRazorpay_order_id(),
                        "ERROR",
                        request.getRazorpay_payment_id(),
                        null);

            } catch (Exception e2) {

                throw new RuntimeException(e2);
            }

            return PaymentDTO.builder()
                    .success(false)
                    .message("Error verifying payment " + e.getMessage())
                    .build();
        }
    }

    // ================= SIGNATURE GENERATOR =================

    private String generateHmacSha256Signature(String data, String secret) throws Exception {

        String algorithm = "HmacSHA256";

        Mac mac = Mac.getInstance(algorithm);

        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), algorithm);

        mac.init(secretKey);

        byte[] hash = mac.doFinal(data.getBytes());

        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {

        StringBuilder hex = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {

            String s = Integer.toHexString(0xff & b);

            if (s.length() == 1) {
                hex.append('0');
            }

            hex.append(s);
        }

        return hex.toString();
    }

    // ================= UPDATE TRANSACTION =================

    private void updateTransactionStatus(String orderId,
                                         String status,
                                         String paymentId,
                                         Integer creditsAdded) {

        paymentTransactionRepository.findAll().stream()
                .filter(t -> t.getOrderId() != null && t.getOrderId().equals(orderId))
                .findFirst()
                .ifPresent(transaction -> {

                    transaction.setStatus(status);
                    transaction.setPaymentId(paymentId);

                    if (creditsAdded != null) {
                        transaction.setCreditsAdded(creditsAdded);
                    }

                    paymentTransactionRepository.save(transaction);
                });
    }
}