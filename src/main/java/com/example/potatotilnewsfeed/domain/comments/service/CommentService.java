package com.example.potatotilnewsfeed.domain.comments.service;

import com.example.potatotilnewsfeed.domain.comments.dto.CommentRequestDto;
import com.example.potatotilnewsfeed.domain.comments.dto.CommentResponseDto;
import com.example.potatotilnewsfeed.domain.comments.entity.Comment;
import com.example.potatotilnewsfeed.domain.comments.repository.CommentRepository;
import com.example.potatotilnewsfeed.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
  // 사용자에게 어떤 공통적인(?) 기능을 제공할 것인가?(비즈니스 로직)

  private final CommentRepository commentRepository;


  // 댓글 작성
  public CommentResponseDto createComment(Comment comment, Long commentId) {

  Comment create = commentRepository.findById(commentId).get();
    return commentRepository.save(create);


  }

  // 댓글 수정
  public Comment updateComment(CommentResponseDto commentRequestDto, Long commentId) {
    // 닉네임과 댓글이 일치하면 수정 아니면 (예외)존재하지 않습니다. exist
   Comment change = commentRepository.findById(commentId).get();
   change.setContent(commentRequestDto.content);
   commentRepository.save(,commentRequestDto);
  }

  // 댓글 삭제
  public void deleteComment(Long commentId) {
    // 댓글이 존재하고 삭제할꺼면 true 아니면 false
    Comment exit = commentRepository.findById(commentId).get();
    commentRepository.delete(exit);
  }

}