//package com.nitish.portfolio.portfolio_api.repository;
//
//import com.nitish.portfolio.portfolio_api.model.HeroContent;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.Optional;
//
//public interface HeroContentRepository extends JpaRepository<HeroContent, Long> {
//    Optional<HeroContent> findByContentKey(String contentKey);
//}

package com.nitish.portfolio.portfolio_api.repository;

import com.nitish.portfolio.portfolio_api.model.HeroContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HeroContentRepository extends JpaRepository<HeroContent, Long> {
    Optional<HeroContent> findByContentKey(String contentKey);
}
