// package: com.nitish.portfolio.portfolio_api.model

package com.nitish.portfolio.portfolio_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title of the project
    @Column(nullable = false)
    private String title;

    // Description / summary
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // comma separated tags (React,C#,Azure)
    @Column(columnDefinition = "TEXT")
    private String tags;

    // Cloudinary secure URL
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // Cloudinary public_id (for delete/overwrite)
    @Column(columnDefinition = "TEXT")
    private String imagePublicId;

    @Column
    private String liveUrl;

    @Column
    private String githubUrl;


    // For ordering on portfolio (optional)
    private Integer displayOrder;


}
