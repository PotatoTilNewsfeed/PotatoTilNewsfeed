package com.example.potatotilnewsfeed.domain.user.repository;

import com.example.potatotilnewsfeed.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

}
