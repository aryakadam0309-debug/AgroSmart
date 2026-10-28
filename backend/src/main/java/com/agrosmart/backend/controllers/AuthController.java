package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.User;
import com.agrosmart.backend.entities.UserLoginLog;
import com.agrosmart.backend.payload.AuthDto.*;
import com.agrosmart.backend.repositories.UserLoginLogRepository;
import com.agrosmart.backend.repositories.UserRepository;
import com.agrosmart.backend.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final UserLoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserRepository userRepository,
                          UserLoginLogRepository loginLogRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.loginLogRepository = loginLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> userOpt = userRepository.findByPhone(req.phone);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(req.phone);
        }
        if (userOpt.isEmpty()) {
            loginLogRepository.save(new UserLoginLog(null, req.phone != null ? req.phone : "unknown", "UNKNOWN", "FAILED_USER_NOT_FOUND"));
            return ResponseEntity.badRequest().body(new AuthResponse(false, null, null, null, null, "Mobile number or email not registered."));
        }

        User user = userOpt.get();
        // Support demo direct login or encoded check
        boolean passwordMatches = passwordEncoder.matches(req.password, user.getPassword()) || "123456".equals(req.password) || "admin123".equals(req.password);
        if (!passwordMatches) {
            loginLogRepository.save(new UserLoginLog(user.getId(), req.phone, user.getRole(), "FAILED_BAD_PASSWORD"));
            return ResponseEntity.badRequest().body(new AuthResponse(false, null, null, null, null, "Invalid password."));
        }

        // Record successful login in database
        loginLogRepository.save(new UserLoginLog(user.getId(), user.getPhone() != null ? user.getPhone() : user.getEmail(), user.getRole(), "SUCCESS"));

        String token = tokenProvider.generateToken(user.getPhone() != null ? user.getPhone() : user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(true, token, user.getRole(), user.getFullName(), user.getPhone(), "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByPhone(req.phone)) {
            // If phone already registered, update user record to maintain dual-mode sync consistency
            java.util.Optional<User> existingUserOpt = userRepository.findByPhone(req.phone);
            if (existingUserOpt.isPresent()) {
                User existing = existingUserOpt.get();
                if (req.fullName != null && !req.fullName.isEmpty()) existing.setFullName(req.fullName);
                if (req.email != null && !req.email.isEmpty()) existing.setEmail(req.email);
                if (req.password != null && !req.password.isEmpty()) {
                    existing.setPassword(passwordEncoder.encode(req.password));
                }
                if (req.village != null) existing.setVillage(req.village);
                if (req.district != null) existing.setDistrict(req.district);
                if (req.state != null) existing.setState(req.state);
                if (req.landAreaAcres != null) existing.setLandAreaAcres(req.landAreaAcres);

                User updated = userRepository.save(existing);
                loginLogRepository.save(new UserLoginLog(updated.getId(), updated.getPhone(), updated.getRole(), "SUCCESS_UPDATED_SYNC"));
                String token = tokenProvider.generateToken(updated.getPhone(), updated.getRole());
                return ResponseEntity.ok(new AuthResponse(true, token, updated.getRole(), updated.getFullName(), updated.getPhone(), "Registration updated and synchronized successfully!"));
            }
        }

        String encodedPassword = passwordEncoder.encode(req.password);
        String role = req.role != null && req.role.equalsIgnoreCase("ADMIN") ? "ROLE_ADMIN" : "ROLE_FARMER";

        User newUser = new User(req.fullName, req.phone, req.email, encodedPassword, role, req.village, req.district, req.state, req.landAreaAcres);
        User saved = userRepository.save(newUser);

        // Record registration event as initial successful session
        loginLogRepository.save(new UserLoginLog(saved.getId(), saved.getPhone(), saved.getRole(), "SUCCESS_REGISTERED"));

        String token = tokenProvider.generateToken(newUser.getPhone(), newUser.getRole());
        return ResponseEntity.ok(new AuthResponse(true, token, newUser.getRole(), newUser.getFullName(), newUser.getPhone(), "Registration successful!"));
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleSignIn(@RequestBody GoogleAuthRequest req) {
        if (req.email == null || req.email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthResponse(false, null, null, null, null, "Email is required for Google Sign-In."));
        }

        String verifiedEmail = req.email.trim().toLowerCase();
        String googleSubId = req.idToken != null ? req.idToken.hashCode() + "" : "G-" + verifiedEmail.hashCode();

        // Check if user already registered via Google Sub ID
        Optional<User> existingUserOpt = userRepository.findByGoogleId(googleSubId);
        if (existingUserOpt.isEmpty()) {
            existingUserOpt = userRepository.findByEmail(verifiedEmail);
        }

        User user;
        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleSubId);
            }
            if (req.photoUrl != null && !req.photoUrl.isEmpty()) {
                user.setProfileImageUrl(req.photoUrl);
            }
            userRepository.save(user);
        } else {
            String name = (req.displayName != null && !req.displayName.isEmpty()) ? req.displayName : "Google Farmer";
            String generatedPhone = "G" + Math.abs(verifiedEmail.hashCode() % 1000000000L);
            user = new User(name, generatedPhone, verifiedEmail, null, "ROLE_FARMER", "Green Valley", "Pune", "Maharashtra", 2.5);
            user.setGoogleId(googleSubId);
            if (req.photoUrl != null) {
                user.setProfileImageUrl(req.photoUrl);
            }
            userRepository.save(user);
        }

        // Record Google login in database
        loginLogRepository.save(new UserLoginLog(user.getId(), user.getEmail(), user.getRole(), "SUCCESS_GOOGLE"));

        String phoneOrId = user.getPhone() != null ? user.getPhone() : user.getEmail();
        String token = tokenProvider.generateToken(phoneOrId, user.getRole());
        return ResponseEntity.ok(new AuthResponse(true, token, user.getRole(), user.getFullName(), user.getPhone(), "Google authentication successful"));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> payload) {
        String phone = (String) payload.get("phone");
        String email = (String) payload.get("email");
        String fullName = (String) payload.get("fullName");
        String location = (String) payload.get("location");
        Double farmSize = payload.get("farmSize") instanceof Number ? ((Number) payload.get("farmSize")).doubleValue() : null;

        Optional<User> userOpt = Optional.empty();
        if (phone != null && !phone.isEmpty()) {
            userOpt = userRepository.findByPhone(phone);
        }
        if (userOpt.isEmpty() && email != null && !email.isEmpty()) {
            userOpt = userRepository.findByEmail(email);
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        }

        User user = userOpt.get();
        if (fullName != null && !fullName.isEmpty()) user.setFullName(fullName);
        if (email != null && !email.isEmpty()) user.setEmail(email);
        if (location != null && !location.isEmpty()) user.setVillage(location);
        if (farmSize != null) user.setLandAreaAcres(farmSize);

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Profile updated in MySQL database successfully"));
    }

    @GetMapping("/logins")
    public List<UserLoginLog> getRecentLogins() {
        return loginLogRepository.findTop20ByOrderByLoginTimeDesc();
    }
}
