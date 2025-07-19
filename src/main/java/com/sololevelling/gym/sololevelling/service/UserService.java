/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.auth.JwtUtil;
import com.sololevelling.gym.sololevelling.model.*;
import com.sololevelling.gym.sololevelling.model.dto.auth.AuthRequest;
import com.sololevelling.gym.sololevelling.model.dto.auth.AuthResponse;
import com.sololevelling.gym.sololevelling.model.dto.auth.LoginRequest;
import com.sololevelling.gym.sololevelling.model.dto.user.StatAllocationRequest;
import com.sololevelling.gym.sololevelling.model.dto.user.UserDto;
import com.sololevelling.gym.sololevelling.model.dto.user.UserMapper;
import com.sololevelling.gym.sololevelling.repo.AccessTokenRepository;
import com.sololevelling.gym.sololevelling.repo.RefreshTokenRepository;
import com.sololevelling.gym.sololevelling.repo.RoleRepository;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import com.sololevelling.gym.sololevelling.util.exception.*;
import com.sololevelling.gym.sololevelling.util.log.SoloLogger;
import com.sololevelling.gym.sololevelling.util.pass.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RefreshTokenRepository refreshTokenRepo;
    @Autowired
    private AccessTokenRepository accessTokenRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordValidator passwordValidator;

    public String registerUser(AuthRequest req) {
        SoloLogger.info("📝 Registration attempt for email: {}", req.email);
        if (userRepo.existsByEmail(req.email)) {
            SoloLogger.warn("⚠️ Email already registered: {}", req.email);
            throw new UserAlreadyExistsException("Email already registered");
        }

        passwordValidator.validate(req.password);
        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> {
                    SoloLogger.error("❌ Default role not found");
                    return new RoleNotFoundException("Default role not found");
                });

        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPassword(encoder.encode(req.password));
        user.setUserClass(req.userClass);
        user.setStats(new Stats());
        user.setStatPoints(20);
        user.setRoles(Set.of(userRole));

        userRepo.save(user);
        SoloLogger.info("✅ New user registered: {}", req.email);
        return "Registration successful. Please log in.";
    }

    @Transactional
    public AuthResponse loginUser(LoginRequest req) {
        SoloLogger.info("🔐 Login attempt for email: {}", req.email);
        User user = userRepo.findByEmail(req.email)
                .orElseThrow(() -> {
                    SoloLogger.error("❌ User not found: {}", req.email);
                    return new UserNotFoundException("User not found");
                });

        if (user.isLocked()) {
            SoloLogger.warn("🔒 Account locked for user: {}", req.email);
            throw new AccountLockException("Account is locked. Try again at: " + user.getLockoutUntil());
        }

        if (!encoder.matches(req.password, user.getPassword())) {
            user.incrementFailedAttempts();
            userRepo.save(user);
            SoloLogger.warn("❌ Invalid password for user: {}", req.email);
            throw new InvalidPasswordException("Invalid password");
        }

        user.resetFailedAttempts();
        userRepo.save(user);

        deleteAccessTokensForUser(user);
        refreshTokenRepo.deleteByUser(user);

        String jwt = jwtUtil.generateToken(user.getEmail());
        saveAccessToken(jwt, user);
        RefreshToken refresh = createRefreshToken(user);

        SoloLogger.info("✅ Successful login for user: {}", req.email);
        return new AuthResponse(jwt, refresh.getToken());
    }

    public RefreshToken createRefreshToken(User user) {
        SoloLogger.debug("🔄 Creating refresh token for user: {}", user.getEmail());
        refreshTokenRepo.deleteByUser(user);
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));
        return refreshTokenRepo.save(token);
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {
        SoloLogger.info("🔄 Refreshing access token");
        Optional<RefreshToken> token = refreshTokenRepo.findByToken(refreshToken);
        if (token.isEmpty()) {
            SoloLogger.error("❌ Invalid refresh token");
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        User user = token.get().getUser();
        deleteAccessTokensForUser(user);
        String newAccessToken = jwtUtil.generateToken(user.getEmail());
        saveAccessToken(newAccessToken, user);

        SoloLogger.debug("✅ New access token generated for user: {}", user.getEmail());
        return newAccessToken;
    }

    public void saveAccessToken(String token, User user) {
        SoloLogger.debug("💾 Saving access token for user: {}", user.getEmail());
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setUser(user);
        accessToken.setExpiryDate(Instant.now().plus(Duration.ofMinutes(1440)));
        accessTokenRepo.save(accessToken);
    }

    @Transactional
    public void deleteAccessTokensForUser(User user) {
        SoloLogger.debug("🗑️ Deleting access tokens for user: {}", user.getEmail());
        accessTokenRepo.deleteByUser(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        SoloLogger.debug("👤 Loading user details for: {}", email);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    SoloLogger.error("❌ User not found: {}", email);
                    return new UserNotFoundException("User not found");
                });
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );
    }

    public UserDto getCurrentUserProfile(String email) {
        SoloLogger.info("👤 Fetching profile for user: {}", email);
        User user = userRepo.findByEmail(email).orElseThrow(() -> {
            SoloLogger.error("❌ User not found: {}", email);
            return new UserNotFoundException("User not found");
        });
        return UserMapper.toDto(user);
    }

    public UserDto allocateStats(String email, StatAllocationRequest request) {
        SoloLogger.info("➕ Allocating stats for user: {}", email);
        User user = userRepo.findByEmail(email).orElseThrow(() -> {
            SoloLogger.error("❌ User not found: {}", email);
            return new UserNotFoundException("User not found");
        });
        Stats stats = user.getStats();

        int totalRequested = request.strength + request.endurance +
                request.agility + request.intelligence + request.luck;

        if (user.getStatPoints() < totalRequested) {
            SoloLogger.warn("⚠️ Insufficient stat points for user {} (has: {}, needs: {})",
                    email, user.getStatPoints(), totalRequested);
            throw new NotEnoughStatPointsException("Not enough stat points.");
        }

        stats.setStrength(stats.getStrength() + request.strength);
        stats.setEndurance(stats.getEndurance() + request.endurance);
        stats.setAgility(stats.getAgility() + request.agility);
        stats.setIntelligence(stats.getIntelligence() + request.intelligence);
        stats.setLuck(stats.getLuck() + request.luck);

        user.setStatPoints(user.getStatPoints() - totalRequested);
        userRepo.save(user);

        SoloLogger.debug("📊 New stats for {}: STR {}, END {}, AGI {}, INT {}, LCK {}",
                email, stats.getStrength(), stats.getEndurance(),
                stats.getAgility(), stats.getIntelligence(), stats.getLuck());

        return UserMapper.toDto(user);
    }

    @Transactional
    public void logoutUser(String token) {
        String email = jwtUtil.extractUsername(token);
        SoloLogger.info("🚪 Logging out user: {}", email);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    SoloLogger.error("❌ User not found: {}", email);
                    return new UserNotFoundException("User not found");
                });

        deleteAccessTokensForUser(user);
        refreshTokenRepo.deleteByUser(user);

        user.setLastLogout(LocalDateTime.now());
        userRepo.save(user);
        SoloLogger.debug("✅ User {} logged out successfully", email);
    }
}