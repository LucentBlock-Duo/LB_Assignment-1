package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.model.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserInfoDTO fetchUserInfo(String userEmail) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

        return UserInfoDTO.UserEntityToUserInfoDTO(user);
    }

    public UserInfoDTO updateUserInfo(UserInfoDTO userInfoDTO) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(userInfoDTO.getUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException(userInfoDTO.getUserEmail()));

        User savedUser = userRepository.saveAndFlush(user.UpdateUserBasedOnUserInfoDTO(userInfoDTO));
        return UserInfoDTO.UserEntityToUserInfoDTO(savedUser);
    }

    public ResponseEntity deleteUserInfo(String userEmail) {
        User retrievedUser = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));
//        retrievedUser.setDeletedAt(LocalDateTime.now());
        retrievedUser.delete();
        userRepository.saveAndFlush(retrievedUser);

        return ResponseEntity.ok().build();
    }
}
