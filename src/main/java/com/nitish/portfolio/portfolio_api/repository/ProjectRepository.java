// package: com.nitish.portfolio.portfolio_api.repository

package com.nitish.portfolio.portfolio_api.repository;

import com.nitish.portfolio.portfolio_api.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Optional: sort for public listing
    List<Project> findAllByOrderByDisplayOrderAscIdAsc();
}
