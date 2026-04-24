package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import com.viamatica.assessment.orders_management_system.application.usecase.ProcessPaymentUseCase;
import com.viamatica.assessment.orders_management_system.domain.entity.PaymentDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.PaymentNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.PaymentRepository;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.PaymentResponse;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.ProcessPaymentRequest;
import com.viamatica.assessment.orders_management_system.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final ProcessPaymentUseCase processPaymentUseCase;

    @PostMapping
    @Operation(summary = "Process payment", description = "Process a new payment for an order")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request,
            @CurrentUser Long userId) {
        ProcessPaymentUseCase.Command command = new ProcessPaymentUseCase.Command(
                request.orderId(),
                request.method(),
                request.amount(),
                request.externalReference()
        );
        PaymentDomain payment = processPaymentUseCase.execute(command);
        return ResponseEntity.ok(toPaymentResponse(payment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieve a specific payment by ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id) {
        PaymentDomain payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return ResponseEntity.ok(toPaymentResponse(payment));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID", description = "Retrieve payment for a specific order")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable Long orderId) {
        PaymentDomain payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
        return ResponseEntity.ok(toPaymentResponse(payment));
    }

    private PaymentResponse toPaymentResponse(PaymentDomain payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getMethod().name(),
                payment.getStatus().name(),
                payment.getAmount().amount(),
                payment.getExternalReference(),
                payment.getPaymentDate()
        );
    }
}
