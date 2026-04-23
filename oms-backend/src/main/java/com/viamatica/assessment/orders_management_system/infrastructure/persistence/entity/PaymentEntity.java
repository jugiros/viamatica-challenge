package com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity;

import com.viamatica.assessment.orders_management_system.domain.model.PaymentMethod;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "referencia_externa", length = 255)
    private String externalReference;

    @Column(name = "fecha_pago", nullable = false)
    @CreationTimestamp
    private LocalDateTime paymentDate;
}
