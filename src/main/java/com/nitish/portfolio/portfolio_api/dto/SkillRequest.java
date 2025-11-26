package com.nitish.portfolio.portfolio_api.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {
    private String title;
    private String description;
    private List<String> chips;
}
