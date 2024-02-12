package com.example.potatotilnewsfeed.domain.comments.repository;

import com.example.potatotilnewsfeed.domain.comments.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

}