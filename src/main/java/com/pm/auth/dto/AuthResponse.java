package com.pm.auth.dto;

import com.pm.user.dto.UserDto;

public record AuthResponse(UserDto user, String token, String refreshToken) {}
