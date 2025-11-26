// package: com.nitish.portfolio.portfolio_api.dto

package com.nitish.portfolio.portfolio_api.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactMessageDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String subject;
    private String message;
    private Boolean readFlag;
    private LocalDateTime createdAt;
}
