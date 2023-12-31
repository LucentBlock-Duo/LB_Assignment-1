package com.lucentblock.assignment2.security;

import com.lucentblock.assignment2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {

        return new PrincipalDetails(
                userRepository.findByEmailAndDeletedAtIsNull(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found")));
    }
}
