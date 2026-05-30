package com.example.Synchro.repository;

import com.example.Synchro.entity.Message;
import com.example.Synchro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrderBySentAtAsc(User sender, User receiver);

    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    long countByReceiverAndIsReadFalse(User receiver);

    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2 OR m.sender = :user2 AND m.receiver = :user1) AND m.id > :lastMessageId ORDER BY m.sentAt ASC")
    List<Message> findNewMessages(@Param("user1") User user1, @Param("user2") User user2, @Param("lastMessageId") Long lastMessageId);

    @Query("SELECT m FROM Message m WHERE ((m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1)) AND m.id > :afterId ORDER BY m.sentAt ASC")
    List<Message> findMessagesAfterId(@Param("user1") User user1, @Param("user2") User user2, @Param("afterId") Long afterId);
}