package com.gangoffive.birdtradingplatform.entity;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity(name = "tblLog_Order")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class LogOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @Column(name = "timestamp")
    @CreationTimestamp
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "staff_id"
            , foreignKey = @ForeignKey(name = "FK_LOG_ORDER_SHOP_STAFF")
    )
    private ShopStaff shopStaff;

    @ManyToOne
    @JoinColumn(
            name = "order_id",
            foreignKey = @ForeignKey(name = "FK_LOG_ORDER_ORDER")
    )
    private Order order;
}
