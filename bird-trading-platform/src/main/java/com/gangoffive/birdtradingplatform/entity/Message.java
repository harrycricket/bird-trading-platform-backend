package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity(name = "tblMessage")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "message_text",
            columnDefinition = "TEXT")
    private String messageText;

    @CreationTimestamp
    private Date timestamp;

    @Column(name = "is_shop_send")
    private boolean shopSend;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private ShopOwner shopOwner;

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setShopSend(boolean shopSend) {
        this.shopSend = shopSend;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setShopOwner(ShopOwner shopOwner) {
        this.shopOwner = shopOwner;
    }
}
