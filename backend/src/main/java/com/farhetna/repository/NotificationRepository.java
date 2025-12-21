package com.farhetna.repository;

import com.farhetna.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User recipient);
    List<Notification> findByRecipientId(Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId " +
           "AND (:unreadOnly = false OR n.isRead = false) " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndReadStatus(
        @Param("userId") Long userId,
        @Param("unreadOnly") Boolean unreadOnly
    );
    
    Long countByRecipientIdAndIsReadFalse(Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.sent = false")
    List<Notification> findPendingNotifications();
}
