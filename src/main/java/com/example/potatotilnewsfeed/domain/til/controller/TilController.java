package com.example.potatotilnewsfeed.domain.til.controller;

import com.example.potatotilnewsfeed.domain.til.dto.GetTilListResponseDto;
import com.example.potatotilnewsfeed.domain.til.dto.GetTilResponseDto;
import com.example.potatotilnewsfeed.domain.til.dto.TilData;
import com.example.potatotilnewsfeed.domain.til.dto.TilDto;
import com.example.potatotilnewsfeed.domain.til.dto.TilLikeResponseDto;
import com.example.potatotilnewsfeed.domain.til.dto.TilUpdateRequestDto;
import com.example.potatotilnewsfeed.domain.til.entity.Til;
import com.example.potatotilnewsfeed.domain.til.exception.CannotUpdateTilException;
import com.example.potatotilnewsfeed.domain.til.exception.TilNotFoundException;
import com.example.potatotilnewsfeed.domain.til.service.TilService;
import com.example.potatotilnewsfeed.global.dto.ResponseDto;
import com.example.potatotilnewsfeed.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TilController {

    private final TilService tilService;

    public static final String GET_TIL_API = "TIL 조회 API";

    @Autowired
    public TilController(TilService tilService) {
        this.tilService = tilService;
    }

    @PostMapping("/v1/tils")
    public ResponseEntity<ResponseDto<List<TilData>>> createTil(@RequestBody TilDto tilDto,
        HttpServletResponse response) {
        log.info("TIL 생성 API");

        Til createdTil = tilService.createTilPost(tilDto);
        // Location 헤더 설정
        URI location = URI.create(String.format("/v1/tils/%d", createdTil.getId()));
        response.setHeader("Location", location.toString());

        TilData tilData = new TilData(createdTil.getId(), createdTil.getTitle(),
            createdTil.getContent(), createdTil.getUser().getNickname(), createdTil.getCreatedAt());

        List<TilData> tilDataList = Arrays.asList(tilData);

        return ResponseEntity.created(location).body(
            ResponseDto.<List<TilData>>builder()
                .message("TIL이 등록되었습니다.")
                .data(tilDataList)
                .build()
        );
    }

    @PatchMapping("/v1/tils/{tilId}")
    public ResponseEntity<ResponseDto<List<TilData>>> patchTil(@PathVariable("tilId") Long tilId,
        @RequestBody TilUpdateRequestDto requestDto) {
        log.info("TIL 수정 API");

        Til updatedTil = tilService.updateTilPost(tilId, requestDto);
        TilData tilData = new TilData(updatedTil.getId(), updatedTil.getTitle(),
            updatedTil.getContent(), updatedTil.getUser().getNickname(), updatedTil.getCreatedAt());

        List<TilData> tilDataList = Arrays.asList(tilData);

        return ResponseEntity.ok().body(
            ResponseDto.<List<TilData>>builder()
                .message("TIL이 수정되었습니다.")
                .data(tilDataList)
                .build()
        );
    }

    @DeleteMapping("v1/tils/{tilId}")
    public ResponseEntity<Void> deleteTil(@PathVariable Long tilId) {
        tilService.deleteTil(tilId);
        log.info("TIL 삭제 API");

        tilService.deleteTil(tilId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(TilNotFoundException.class)
    public ResponseEntity<String> handleTilNotFoundException(TilNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CannotUpdateTilException.class)
    public ResponseEntity<String> handleCannotUpdateTilException(CannotUpdateTilException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @GetMapping("/v1/tils/all")
    public ResponseEntity<ResponseDto<List<GetTilListResponseDto>>> getAllTil() {
        log.info(GET_TIL_API);

        List<GetTilListResponseDto> responseDto = tilService.getAllTil();

        return ResponseEntity.ok().body(
            ResponseDto.<List<GetTilListResponseDto>>builder().message("TIL 전체 조회 성공")
                .data(responseDto).build());
    }

    @GetMapping("/v1/tils/{tilId}")
    public ResponseEntity<ResponseDto<GetTilResponseDto>> getTil(@PathVariable Long tilId) {
        log.info(GET_TIL_API);

        GetTilResponseDto responseDto = tilService.getTil(tilId);

        return ResponseEntity.ok().body(
            ResponseDto.<GetTilResponseDto>builder().message("TIL 선택 조회 성공").data(responseDto)
                .build());
    }

    @GetMapping("/v1/tils")
    public ResponseEntity<ResponseDto<GetTilListResponseDto>> getTil(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(GET_TIL_API);

        GetTilListResponseDto responseDto = tilService.getTil(userDetails.getUser());

        return ResponseEntity.ok().body(
            ResponseDto.<GetTilListResponseDto>builder().message("TIL 유저 조회 성공")
                .data(responseDto).build());
    }

    @PostMapping("/v1/tils/{tilId}/likes")
    public ResponseEntity<ResponseDto<TilLikeResponseDto>> likeTil(@PathVariable Long tilId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("TIL 좋아요 API");

        TilLikeResponseDto responseDto = tilService.likeTil(tilId, userDetails.getUser());

        URI location = URI.create(String.format("/v1/tils/%d", responseDto.getTilId()));

        return ResponseEntity.created(location).body(
            ResponseDto.<TilLikeResponseDto>builder().message("TIL 좋아요!")
                .data(responseDto).build());
    }

    @GetMapping("/v1/tils/likes")
    public ResponseEntity<ResponseDto<GetTilListResponseDto>> getLikeTil(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(GET_TIL_API);

        GetTilListResponseDto responseDto = tilService.getLikeTil(userDetails.getUser());

        return ResponseEntity.ok().body(
            ResponseDto.<GetTilListResponseDto>builder().message("TIL 좋아요 조회 성공")
                .data(responseDto).build());
    }

    @DeleteMapping("/v1/tils/{tilId}/likes")
    public ResponseEntity<ResponseDto<Long>> deleteLikeTil(@PathVariable Long tilId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        HttpServletResponse response) {
        log.info("TIL 좋아요 삭제 API");

        tilService.deleteLikeTil(tilId, userDetails.getUser());

        URI location = URI.create("/v1/tils/all");
        response.setHeader("Location", location.toString());

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateKeyException(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
