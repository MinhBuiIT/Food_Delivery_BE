package com.dev.service;

import com.dev.config.VNPAYConfig;
import com.dev.dto.request.PaymentRequest;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.models.User;
import com.dev.repository.UserRepository;
import com.dev.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final UserRepository userRepository;
    public String createVnPayPayment(PaymentRequest body,HttpServletRequest request) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorEnum.NOT_FOUND_USER));
        long amount = body.getAmount() * 100L;
        String bankCode = body.getBankCode();
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return paymentUrl;
    }
}