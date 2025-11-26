// package: com.nitish.portfolio.portfolio_api.dto

package com.nitish.portfolio.portfolio_api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSettingsDto {
    private Boolean notificationsEnabled;
    private String notificationEmail;
}
