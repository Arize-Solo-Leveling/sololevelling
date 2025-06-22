/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.service;

import com.sololevelling.gym.sololevelling.auth.JwtUtil;
import com.sololevelling.gym.sololevelling.model.Stats;
import com.sololevelling.gym.sololevelling.model.User;
import com.sololevelling.gym.sololevelling.model.dto.auth.AuthRequest;
import com.sololevelling.gym.sololevelling.model.dto.auth.AuthResponse;
import com.sololevelling.gym.sololevelling.model.dto.user.StatAllocationRequest;
import com.sololevelling.gym.sololevelling.model.dto.user.UserDto;
import com.sololevelling.gym.sololevelling.model.dto.user.UserMapper;
import com.sololevelling.gym.sololevelling.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserMapper userMapper;

    public AuthResponse registerUser(AuthRequest req) {
        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPassword(encoder.encode(req.password));
        user.setUserClass(req.userClass);
        user.setStats(new Stats());
        user.setStatPoints(20);

        userRepo.save(user);
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()), user.getName());
    }

    public AuthResponse loginUser(AuthRequest req) {
        User user = userRepo.findByEmail(req.email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!encoder.matches(req.password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new AuthResponse(jwtUtil.generateToken(user.getEmail()), user.getName());
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority("USER")));
    }


    public UserDto getCurrentUserProfile(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        return userMapper.toDto(user);
    }

    public UserDto allocateStats(String email, StatAllocationRequest request) {
        User user = userRepo.findByEmail(email).orElseThrow();
        Stats stats = user.getStats();

        int totalRequested =
                request.strength +
                        request.endurance +
                        request.agility +
                        request.intelligence +
                        request.luck;

        if (user.getStatPoints() < totalRequested) {
            throw new IllegalArgumentException("Not enough stat points.");
        }

        stats.setStrength(stats.getStrength() + request.strength);
        stats.setEndurance(stats.getEndurance() + request.endurance);
        stats.setAgility(stats.getAgility() + request.agility);
        stats.setIntelligence(stats.getIntelligence() + request.intelligence);
        stats.setLuck(stats.getLuck() + request.luck);

        user.setStatPoints(user.getStatPoints() - totalRequested);

        userRepo.save(user);
        return userMapper.toDto(user);
    }

    public void addExperience(User user, int expGained) {
        int exp = user.getExperience() + expGained;
        int level = user.getLevel();

        while (exp >= getExpForNextLevel(level)) {
            exp -= getExpForNextLevel(level);
            level++;
            user.setStatPoints(user.getStatPoints() + 5); // Grant 5 points on level up
        }

        user.setExperience(exp);
        user.setLevel(level);
        userRepo.save(user);
    }

    private int getExpForNextLevel(int level) {
        int exp = 100;

        for (int i = 1; i < level; i++) {
            int growthRate;

            if (i <= 5) {
                growthRate = 50;
            } else if (i <= 10) {
                growthRate = 45;
            } else if (i <= 20) {
                growthRate = 40;
            } else if (i <= 30) {
                growthRate = 30;
            } else if (i <= 40) {
                growthRate = 20;
            } else if (i <= 50) {
                growthRate = 10;
            } else {
                growthRate = 5;
            }
            exp = exp + (exp * growthRate / 100);
        }

        return exp;
    }

}
