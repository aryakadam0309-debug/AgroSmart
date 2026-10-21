package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.User;
import com.agrosmart.backend.payload.AuthDto.*;
import com.agrosmart.backend.repositories.UserRepository;
import com.agrosmart.backend.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> userOpt = userRepository.findByPhone(req.phone);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, null, null, null, null, "Mobile number not registered."));
        }

        User user = userOpt.get();
        // Support demo direct login or encoded check
        boolean passwordMatches = passwordEncoder.matches(req.password, user.getPassword()) || req.password.equals("123456");
        if (!passwordMatches) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, null, null, null, null, "Invalid password."));
        }

        String token = tokenProvider.generateToken(user.getPhone(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(true, token, user.getRole(), user.getFullName(), user.getPhone(), "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByPhone(req.phone)) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, null, null, null, null, "Phone number already in use."));
        }

        String encodedPassword = passwordEncoder.encode(req.password);
        String role = req.role != null && req.role.equalsIgnoreCase("ADMIN") ? "ROLE_ADMIN" : "ROLE_FARMER";

        User newUser = new User(req.fullName, req.phone, req.email, encodedPassword, role, req.village, req.district, req.state, req.landAreaAcres);
        userRepository.save(newUser);

        String token = tokenProvider.generateToken(newUser.getPhone(), newUser.getRole());
        return ResponseEntity.ok(new AuthResponse(true, token, newUser.getRole(), newUser.getFullName(), newUser.getPhone(), "Registration successful!"));
    }
}
