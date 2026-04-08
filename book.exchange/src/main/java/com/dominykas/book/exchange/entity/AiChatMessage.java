package com.dominykas.book.exchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "ai_chat_message_notices",
            joinColumns = @JoinColumn(name = "ai_chat_message_id"),
            inverseJoinColumns = @JoinColumn(name = "notice_id")
    )
    @OrderColumn(name = "notice_order")
    private List<Notice> notices = new ArrayList<>();
}