package com.project.Multi_Tenant_SaaS_Backend.features.auth.service.serviceImpl;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.data.models.RefreshToken;
import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.RefreshTokenRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.UserRepository;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.dto.request.LoginRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.dto.response.LoginResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.mapper.AuthMapper;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.service.AuthService;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.JWTUtil;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import com.project.Multi_Tenant_SaaS_Backend.security.config.CookieConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JWTUtil jwtUtil;
    private final CookieConfig cookieConfig;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public ApiResponse loginUser(LoginRequest loginRequest, HttpServletResponse httpResponse) {

        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.email());
        if (optionalUser.isEmpty()) {
            return ApiResponse.builder()
                    .success(0).code(404)
                    .message("User does not exist in the system.")
                    .data(loginRequest.email())
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        User user = optionalUser.get();

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );
        } catch (BadCredentialsException e) {
            return ApiResponse.builder()
                    .success(0).code(401)
                    .message("Incorrect Credentials")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;

        String accessToken = jwtUtil.generateAccessToken(
                new UserPrincipal(user.getId(), companyId, user.getRole(), user.getEmail(), null)
        );
        String rawRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        refreshTokenRepository.deleteAllByUser(user);
        refreshTokenRepository.save(RefreshToken.builder()
                .token(rawRefreshToken)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(
                        jwtUtil.REFRESH_TOKEN_VALID_TIME_MILLIS() / 1000))
                .revoked(false)
                .build());

        cookieConfig.addSecureCookie(httpResponse, "accessToken",
                accessToken, (int) (jwtUtil.ACCESS_TOKEN_VALID_TIME_MILLIS() / 1000), "/");
        cookieConfig.addSecureCookie(httpResponse, "refreshToken",
                rawRefreshToken, (int) (jwtUtil.REFRESH_TOKEN_VALID_TIME_MILLIS() / 1000), "/");

        LoginResponse loginResponse = AuthMapper.mapUserToLogInResponse(user);

        return ApiResponse.builder()
                .success(1).code(200)
                .message("Login Successfully.")
                .data(loginResponse)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse refreshToken(HttpServletRequest request, HttpServletResponse httpResponse) {
        String rawToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    rawToken = cookie.getValue();
                    break;
                }
            }
        }

        if (rawToken == null) {
            return ApiResponse.builder()
                    .success(0).code(401)
                    .message("Refresh token not found.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        // 2. Validate JWT signature + expiry
        Claims claims;
        try {
            claims = jwtUtil.extractAllClaims(rawToken);
        } catch (JwtException e) {
            return ApiResponse.builder()
                    .success(0).code(401)
                    .message("Invalid or expired refresh token.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        // 3. Check DB — exists and not revoked
        RefreshToken storedToken = refreshTokenRepository.findByToken(rawToken)
                .orElse(null);

        if (storedToken == null || storedToken.isRevoked()
                || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ApiResponse.builder()
                    .success(0).code(401)
                    .message("Refresh token is invalid or has been revoked.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        // 4. Load user and issue new access token
        User user = storedToken.getUser();
        Long companyId = user.getCompany() != null ? user.getCompany().getId() : null;

        String newAccessToken = jwtUtil.generateAccessToken(
                new UserPrincipal(user.getId(), companyId, user.getRole(), user.getEmail(), null)
        );

        cookieConfig.addSecureCookie(httpResponse, "accessToken",
                newAccessToken, (int) (jwtUtil.ACCESS_TOKEN_VALID_TIME_MILLIS() / 1000), "/");

        return ApiResponse.builder()
                .success(1).code(200)
                .message("Token refreshed successfully.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse logout(HttpServletRequest request, HttpServletResponse httpResponse) {

        // 1. Extract refresh token from cookie
        String rawToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    rawToken = cookie.getValue();
                    break;
                }
            }
        }

        // 2. Revoke in DB if found
        if (rawToken != null) {
            refreshTokenRepository.findByToken(rawToken).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }

        // 3. Clear cookies by setting Max-Age=0
        cookieConfig.addSecureCookie(httpResponse, "accessToken", "", 0, "/");
        cookieConfig.addSecureCookie(httpResponse, "refreshToken", "", 0, "/");

        return ApiResponse.builder()








                .success(1).code(200)
                .message("Logged out successfully.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }
}
