package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.model.UpdateUserInfoRequestDTO;
import com.lucentblock.assignment2.security.model.UserInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserInfoResponseDTO fetchUserInfo(String userEmail) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

        return UserInfoResponseDTO.userEntityToUserInfoDTO(user);
    }

    public UserInfoResponseDTO updateUserInfo(String userEmail, UpdateUserInfoRequestDTO updateUserInfoRequestDTO) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));

        User savedUser = userRepository.saveAndFlush(user.updateUserBasedOnUserInfoDTO(updateUserInfoRequestDTO));
        return UserInfoResponseDTO.userEntityToUserInfoDTO(savedUser);
    }

    public ResponseEntity deleteUserInfo(String userEmail) {
        User retrievedUser = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));
        retrievedUser.delete();
        userRepository.saveAndFlush(retrievedUser);

        return ResponseEntity.ok().build();
    }
}
