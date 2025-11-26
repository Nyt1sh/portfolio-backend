package com.nitish.portfolio.portfolio_api.repository;

import com.nitish.portfolio.portfolio_api.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
}
