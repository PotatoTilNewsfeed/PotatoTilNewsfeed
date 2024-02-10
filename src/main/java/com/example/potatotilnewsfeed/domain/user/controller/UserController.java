package com.example.potatotilnewsfeed.domain.user.controller;

import com.example.potatotilnewsfeed.domain.user.dto.SignupRequestDto;
import com.example.potatotilnewsfeed.domain.user.dto.UserRequestDto;
import com.example.potatotilnewsfeed.domain.user.dto.UserResponseDto;
import com.example.potatotilnewsfeed.domain.user.exception.UserNotFoundException;
import com.example.potatotilnewsfeed.domain.user.service.UserService;
import com.example.potatotilnewsfeed.global.dto.ResponseDto;
import com.example.potatotilnewsfeed.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    public static final String SIGN_UP_API = "회원 가입 API";
    public static final String SIGN_UP_SUCCESS = "회원 가입 성공";

    @PostMapping("/v1/users/signup")
    public ResponseEntity<ResponseDto<Void>> signup(
        @RequestBody @Valid SignupRequestDto requestDto) {
        log.info(SIGN_UP_API);

        userService.signup(requestDto);

        return ResponseEntity.ok()
            .body(ResponseDto.<Void>builder()
                .message(SIGN_UP_SUCCESS)
                .build());
    }


    public static final String PROFILE_API = "프로필 API";
    public static final String GET_PROFILE_SUCCESS = "프로필 조회 성공";
    public static final String GET_PROFILE_FAIL = "프로필 조회 실패";
    public static final String UPDATE_PROFILE_SUCCESS = "프로필 수정 성공";
    public static final String UPDATE_PROFILE_FAIL = "프로필 수정 실패";
    public static final String UPDATE_PROFILE_PASSWORD_API = "프로필 비밀번호 수정 API";
    public static final String UPDATE_PROFILE_PASSWORD_SUCCESS = "프로필 비밀번호 수정 성공";
    public static final String UPDATE_PROFILE_PASSWORD_FAIL = "프로필 비밀번호 수정 실패";


    @GetMapping("/v1/users")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(PROFILE_API);
        try {
            UserResponseDto userResponseDto = userService.getUser(userDetails);
            return ResponseEntity.ok()
                .body(ResponseDto.<UserResponseDto>builder()
                    .message(GET_PROFILE_SUCCESS)
                    .data(userResponseDto)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ResponseDto.<UserResponseDto>builder()
                    .message(GET_PROFILE_FAIL)
                    .build());
        }
    }

    @PutMapping("/v1/users")
    public ResponseEntity<ResponseDto<UserResponseDto>> updateUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody UserRequestDto userRequestDto) {
        log.info(PROFILE_API);
        try {
            UserResponseDto userResponseDto = userService.updateUser(userDetails,
                userRequestDto);
            return ResponseEntity.ok()
                .body(ResponseDto.<UserResponseDto>builder()
                    .message(UPDATE_PROFILE_SUCCESS)
                    .data(userResponseDto)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ResponseDto.<UserResponseDto>builder()
                    .message(UPDATE_PROFILE_FAIL)
                    .build());
        }
    }

    @PutMapping("/v1/users/password")
    public ResponseEntity<ResponseDto<UserResponseDto>> updatePassword(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody UserRequestDto userRequestDto) {

        log.info(UPDATE_PROFILE_PASSWORD_API);
        try {
            UserResponseDto userResponseDto = userService.updatePassword(userDetails,
                userRequestDto);
            return ResponseEntity.ok()
                .body(ResponseDto.<UserResponseDto>builder()
                    .message(UPDATE_PROFILE_PASSWORD_SUCCESS)
                    .data(userResponseDto)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ResponseDto.<UserResponseDto>builder()
                    .message(UPDATE_PROFILE_PASSWORD_FAIL)
                    .build());
        }
    }

    @DeleteMapping("/v1/users")
    public void deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserRequestDto userRequestDto) {
        log.info(PROFILE_API);
        userService.deleteUser(userDetails, userRequestDto);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}

