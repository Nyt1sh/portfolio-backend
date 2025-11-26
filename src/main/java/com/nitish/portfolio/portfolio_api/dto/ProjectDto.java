// package: com.nitish.portfolio.portfolio_api.dto

package com.nitish.portfolio.portfolio_api.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private Long id;
    private String title;
    private String description;
    private List<String> tags;
    private String imageUrl;
    private Integer displayOrder;
    private String liveUrl;
    private String githubUrl;

}
