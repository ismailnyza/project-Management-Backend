package com.pm.auth;

import com.pm.auth.dto.AuthResponse;
import com.pm.auth.dto.LoginRequest;
import com.pm.auth.dto.RegisterRequest;
import com.pm.auth.entity.RefreshToken;
import com.pm.user.Role;
import com.pm.user.User;
import com.pm.user.UserRepository;
import com.pm.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepo;
    private static final SecureRandom random = new SecureRandom();

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already taken");
        }
        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        // ponytail: first registered user becomes ADMIN
        if (userRepo.count() == 0) user.setRole(Role.ADMIN);
        user = userRepo.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.email())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        // Revoke old refresh tokens for this user
        refreshTokenRepo.deleteByUserId(user.getId());
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshToken stored = refreshTokenRepo.findByToken(refreshToken)
            .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (stored.isRevoked() || stored.isExpired()) {
            throw new BadCredentialsException("Refresh token expired or revoked");
        }
        stored.setRevoked(true);
        refreshTokenRepo.save(stored);
        return buildAuthResponse(stored.getUser());
    }

    public void logout(String refreshToken) {
        refreshTokenRepo.findByToken(refreshToken)
            .ifPresent(rt -> { rt.setRevoked(true); refreshTokenRepo.save(rt); });
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generate(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = generateRefreshToken(user);
        UserDto dto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        return new AuthResponse(dto, accessToken, refreshToken);
    }

    private String generateRefreshToken(User user) {
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(token);
        rt.setExpiresAt(LocalDateTime.now().plusDays(30));
        rt.setRevoked(false);
        refreshTokenRepo.save(rt);
        return token;
    }
}
