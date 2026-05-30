package com.example.Synchro.repository;

import com.example.Synchro.entity.Like;
import com.example.Synchro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByFromUserAndToUser(User fromUser, User toUser);
    List<Like> findByToUser(User toUser);
    List<Like> findByFromUser(User fromUser);
    boolean existsByFromUserAndToUser(User fromUser, User toUser);
    void deleteByFromUserAndToUser(User fromUser, User toUser);

    @Query("SELECT DISTINCT l.fromUser FROM Like l WHERE l.toUser = :user AND l.fromUser IN (SELECT l2.toUser FROM Like l2 WHERE l2.fromUser = :user)")
    List<User> findMutualUsers(@Param("user") User user);

    @Query("SELECT u FROM User u WHERE (SELECT COUNT(l) FROM Like l WHERE l.toUser = u) > (SELECT AVG(cnt) FROM (SELECT COUNT(l) as cnt FROM Like l GROUP BY l.toUser) avg)")
    List<User> findUsersWithLikesAboveAverage();
}