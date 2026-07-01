package com.pm.user;

import com.pm.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepo;

    public List<UserDto> findAll() {
        return userRepo.findAll().stream()
            .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole().name()))
            .toList();
    }

    public UserDto findById(Long id) {
        User u = userRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole().name());
    }

    public String getRole(Long userId) {
        return userRepo.findById(userId).map(u -> u.getRole().name()).orElse("MEMBER");
    }
}
