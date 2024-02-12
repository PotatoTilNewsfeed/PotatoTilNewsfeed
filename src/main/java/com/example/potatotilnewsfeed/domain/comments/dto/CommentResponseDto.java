package com.example.potatotilnewsfeed.domain.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CommentResponseDto {

  public String content;
  public Long tilId;
  public Long userId;
  public Long commentId;

}