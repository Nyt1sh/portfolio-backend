// package: com.nitish.portfolio.portfolio_api.model

package com.nitish.portfolio.portfolio_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Boolean readFlag = false;

    private LocalDateTime createdAt;
}
