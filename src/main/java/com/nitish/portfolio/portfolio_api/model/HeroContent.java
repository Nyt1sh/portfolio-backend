package com.nitish.portfolio.portfolio_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hero_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeroContent {

    // Using a fixed ID (1L) for the single hero description entry
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;


    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, unique = true)
    private String contentKey; // e.g., "hero_description"
}