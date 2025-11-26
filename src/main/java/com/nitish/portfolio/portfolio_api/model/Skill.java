package com.nitish.portfolio.portfolio_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // title of the skill group (e.g. "Back-End")
    @Column(nullable = false)
    private String title;

    // description under the title
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // comma separated chips (e.g. "C#,Java,Node")
    @Column(columnDefinition = "TEXT")
    private String chips;
}
