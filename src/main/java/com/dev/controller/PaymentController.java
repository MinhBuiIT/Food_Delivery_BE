package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.PaymentRequest;
import com.dev.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${client.domain}")
    private  String clientDomain;

    private final PaymentService paymentService;
    @PostMapping("/vn-pay")
    public ResponseSuccess pay(@RequestBody PaymentRequest body,HttpServletRequest request) {
        var result = paymentService.createVnPayPayment(body,request);
        return ResponseSuccess.builder()
                .code(200)
                .message("Payment vn pay")
                .metadata(result)
                .build();
    }
    @GetMapping("/vn-pay-callback")
    public RedirectView payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        RedirectView redirectView = new RedirectView();

        if (status.equals("00")) {
            var clientDomainQuery = clientDomain + "/order/payment" + "?status=success";
            redirectView.setUrl(clientDomainQuery);
        } else {
            var clientDomainQuery = clientDomain + "/order/payment" + "?status=error";
            redirectView.setUrl(clientDomainQuery);
        }
        return redirectView;
    }
}
