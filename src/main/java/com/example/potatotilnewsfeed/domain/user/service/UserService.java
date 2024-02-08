package com.example.potatotilnewsfeed.domain.user.service;

import com.example.potatotilnewsfeed.domain.user.dto.SignupRequestDto;
import com.example.potatotilnewsfeed.domain.user.dto.UserRequestDto;
import com.example.potatotilnewsfeed.domain.user.dto.UserResponseDto;
import com.example.potatotilnewsfeed.domain.user.entity.User;
import com.example.potatotilnewsfeed.domain.user.repository.UserRepository;
import com.example.potatotilnewsfeed.global.security.UserDetailsImpl;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();

        validateUserDuplicate(userRepository.findByNickname(nickname));

        User user = new User(nickname, password, email);
        userRepository.save(user);
    }

    private void validateUserDuplicate(Optional<User> checkUsername) {
        if (checkUsername.isPresent()) {
            throw new DuplicateKeyException("중복된 사용자가 존재합니다.");
        }
    }

    public UserResponseDto getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("선택한 유저가 존재하지 않습니다."));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
        UserRequestDto userRequestDto) {
        // 토큰으로 id 가져오기
        Long userId = userDetails.getUser().getUserId();
        // DB에 접근
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("선택한 유저가 존재하지 않습니다."));
        // 변경
        user.setNickname(userRequestDto.getNickname());
        user.setIntroduce(userRequestDto.getIntroduce());
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updatePassword(UserDetailsImpl userDetails, UserRequestDto userRequestDto) {
        Long userId = userDetails.getUser().getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("선택한 유저가 존재하지 않습니다."));

        // 비밀번호 아는지 확인
        // 기존 비밀번호
        String currentPassword = user.getPassword();
        // 기존 비밀번호를 입력해주세요 (입력받은 값)
        String password = userRequestDto.getPassword();

        // raw  , check (인코딩 된것)
        validatePassword(password, currentPassword);
        log.info("확인 1");

        // 바꾸는 비밀번호
        String newPassword = userRequestDto.getNewPassword();
        String checkPassword = userRequestDto.getCheckPassword();
        // 바꾸는 비밀번호 확인
        validateNewPassword(newPassword, checkPassword);
        log.info("확인 2");

        newPassword = passwordEncoder.encode(userRequestDto.getNewPassword());
        user.setPassword(newPassword);
        log.info("확인 3");
        return new UserResponseDto(user);
    }

    @Transactional
    public void deleteProfile(UserDetailsImpl userDetails, UserRequestDto userRequestDto) {
        Long userId = userDetails.getUser().getUserId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("선택한 유저가 존재하지 않습니다."));
        String password = userRequestDto.getPassword();
        String checkPassword = user.getPassword();
        // raw  , check (인코딩 된것)
        validatePassword(password, checkPassword);
        userRepository.delete(user);
    }

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("패스워드를 잘못 입력하셨습니다.");
        }
    }

    private void validateNewPassword(String newPassword, String checkPassword) {
        if (!newPassword.equals(checkPassword)) {
            throw new BadCredentialsException("기존 비밀번호와 동일합니다.");
        }
    }

}
