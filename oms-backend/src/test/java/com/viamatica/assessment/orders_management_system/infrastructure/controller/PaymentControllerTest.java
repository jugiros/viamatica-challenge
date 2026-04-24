package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import com.viamatica.assessment.orders_management_system.application.usecase.ProcessPaymentUseCase;
import com.viamatica.assessment.orders_management_system.domain.entity.PaymentDomain;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentMethod;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentStatus;
import com.viamatica.assessment.orders_management_system.domain.port.PaymentRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = PaymentController.class)
@ActiveProfiles("test")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private ProcessPaymentUseCase processPaymentUseCase;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void PAY01_ProcessPayment_ValidData_ShouldReturnCreated() throws Exception {
        PaymentDomain payment = PaymentDomain.builder()
                .id(1L)
                .orderId(1L)
                .method(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .amount(Money.of(new BigDecimal("100.00")))
                .externalReference("REF-123")
                .paymentDate(LocalDateTime.now())
                .build();

        when(processPaymentUseCase.execute(any(ProcessPaymentUseCase.Command.class))).thenReturn(payment);

        String requestBody = "{\"orderId\":1, \"method\":\"CREDIT_CARD\", \"amount\":100.00, \"externalReference\":\"REF-123\"}";

        mockMvc.perform(post("/api/v1/payments")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.method").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser
    void PAY02_GetPaymentById_Exists_ShouldReturnPayment() throws Exception {
        PaymentDomain payment = PaymentDomain.builder()
                .id(1L)
                .orderId(1L)
                .method(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .amount(Money.of(new BigDecimal("100.00")))
                .externalReference("REF-123")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        mockMvc.perform(get("/api/v1/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.method").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser
    void PAY03_GetPaymentById_NotExists_ShouldReturn404() throws Exception {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/payments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void PAY04_GetPaymentByOrderId_Exists_ShouldReturnPayment() throws Exception {
        PaymentDomain payment = PaymentDomain.builder()
                .id(1L)
                .orderId(1L)
                .method(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.COMPLETED)
                .amount(Money.of(new BigDecimal("100.00")))
                .externalReference("REF-123")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        mockMvc.perform(get("/api/v1/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.method").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser
    void PAY05_GetPaymentByOrderId_NotExists_ShouldReturn404() throws Exception {
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/payments/order/1"))
                .andExpect(status().isNotFound());
    }
}
