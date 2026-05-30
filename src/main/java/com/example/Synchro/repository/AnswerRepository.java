package com.example.Synchro.repository;

import com.example.Synchro.entity.Answer;
import com.example.Synchro.entity.Question;
import com.example.Synchro.entity.Test;
import com.example.Synchro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a FROM Answer a WHERE a.user = :user AND a.question.test = :test")
    List<Answer> findByUserAndTest(@Param("user") User user, @Param("test") Test test);

    boolean existsByUserAndQuestion(User user, Question question);


    @Query("SELECT a FROM Answer a WHERE a.user IN :users AND a.question.test = :test")
    List<Answer> findByUsersAndTest(@Param("users") List<User> users, @Param("test") Test test);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Answer a WHERE a.user = :user AND a.question.test = :test")
    boolean existsByUserAndTest(@Param("user") User user, @Param("test") Test test);
}