package com.gangoffive.birdtradingplatform.entity;

import com.gangoffive.birdtradingplatform.enums.MessageStatus;
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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "channel_id",
    foreignKey = @ForeignKey(name = "FK_MESSAGE_CHANNEL"))
    private Channel channel;


    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public MessageStatus getStatus() {
        return status;
    }
    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
