// package: com.nitish.portfolio.portfolio_api.repository

package com.nitish.portfolio.portfolio_api.repository;

import com.nitish.portfolio.portfolio_api.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    // unread first, then newest
    List<ContactMessage> findAllByOrderByReadFlagAscCreatedAtDesc();
    List<ContactMessage> findAllByOrderByCreatedAtDesc();

}
