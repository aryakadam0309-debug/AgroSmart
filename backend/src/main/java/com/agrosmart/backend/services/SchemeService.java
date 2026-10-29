package com.agrosmart.backend.services;

import com.agrosmart.backend.entities.GovernmentScheme;
import com.agrosmart.backend.entities.SavedScheme;
import com.agrosmart.backend.entities.User;
import com.agrosmart.backend.payload.FarmerProfileDto;
import com.agrosmart.backend.payload.SchemeEligibilityResultDto;
import com.agrosmart.backend.repositories.GovernmentSchemeRepository;
import com.agrosmart.backend.repositories.SavedSchemeRepository;
import com.agrosmart.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchemeService {

    private final GovernmentSchemeRepository schemeRepository;
    private final SavedSchemeRepository savedSchemeRepository;
    private final UserRepository userRepository;
    private final SchemeEligibilityEngine eligibilityEngine;

    public SchemeService(GovernmentSchemeRepository schemeRepository,
                         SavedSchemeRepository savedSchemeRepository,
                         UserRepository userRepository,
                         SchemeEligibilityEngine eligibilityEngine) {
        this.schemeRepository = schemeRepository;
        this.savedSchemeRepository = savedSchemeRepository;
        this.userRepository = userRepository;
        this.eligibilityEngine = eligibilityEngine;
    }

    public List<GovernmentScheme> listSchemes(String category, String level, String state, String status, String query) {
        return schemeRepository.filterSchemes(category, level, state, status, query);
    }

    public Optional<GovernmentScheme> getSchemeById(String id) {
        return schemeRepository.findById(id);
    }

    /**
     * Evaluates a single scheme against a farmer's profile.
     */
    public SchemeEligibilityResultDto evaluateSingleScheme(String schemeId, FarmerProfileDto profile) {
        GovernmentScheme scheme = schemeRepository.findById(schemeId).orElse(null);
        if (scheme == null) return null;

        SchemeEligibilityResultDto res = eligibilityEngine.evaluateEligibility(scheme, profile);
        if (profile != null && profile.getFarmerPhone() != null) {
            res.setSaved(savedSchemeRepository.existsByFarmerPhoneAndSchemeId(profile.getFarmerPhone(), schemeId));
        }
        return res;
    }

    /**
     * Generates personalized scheme recommendations ranked by eligibility match.
     */
    public List<SchemeEligibilityResultDto> getRecommendedSchemes(FarmerProfileDto profile) {
        List<GovernmentScheme> allSchemes = schemeRepository.findAll();
        List<SchemeEligibilityResultDto> evaluated = new ArrayList<>();

        Set<String> savedIds = new HashSet<>();
        if (profile != null && profile.getFarmerPhone() != null) {
            savedIds = savedSchemeRepository.findByFarmerPhoneOrderBySavedAtDesc(profile.getFarmerPhone())
                    .stream()
                    .map(SavedScheme::getSchemeId)
                    .collect(Collectors.toSet());
        }

        for (GovernmentScheme scheme : allSchemes) {
            SchemeEligibilityResultDto res = eligibilityEngine.evaluateEligibility(scheme, profile);
            if (res != null) {
                res.setSaved(savedIds.contains(scheme.getId()));
                evaluated.add(res);
            }
        }

        // Rank order: Likely Eligible -> Possibly Eligible -> Not Eligible -> Application Closed -> Status Unverified
        evaluated.sort((a, b) -> Integer.compare(getRankScore(a.getEligibilityStatus()), getRankScore(b.getEligibilityStatus())));
        return evaluated;
    }

    private int getRankScore(String status) {
        if ("Likely Eligible".equalsIgnoreCase(status)) return 1;
        if ("Possibly Eligible – More Information Required".equalsIgnoreCase(status)) return 2;
        if ("Not Eligible".equalsIgnoreCase(status)) return 3;
        if ("Application Closed".equalsIgnoreCase(status)) return 4;
        return 5;
    }

    /**
     * Saves / bookmarks a scheme for a farmer.
     */
    @Transactional
    public boolean saveScheme(String farmerPhone, String schemeId) {
        if (farmerPhone == null || schemeId == null) return false;
        if (!savedSchemeRepository.existsByFarmerPhoneAndSchemeId(farmerPhone, schemeId)) {
            savedSchemeRepository.save(new SavedScheme(farmerPhone, schemeId));
        }
        return true;
    }

    /**
     * Removes a scheme bookmark.
     */
    @Transactional
    public boolean unsaveScheme(String farmerPhone, String schemeId) {
        if (farmerPhone == null || schemeId == null) return false;
        savedSchemeRepository.deleteByFarmerPhoneAndSchemeId(farmerPhone, schemeId);
        return true;
    }

    /**
     * Retrieves all schemes saved by a farmer.
     */
    public List<GovernmentScheme> getSavedSchemes(String farmerPhone) {
        if (farmerPhone == null) return Collections.emptyList();
        List<SavedScheme> bookmarks = savedSchemeRepository.findByFarmerPhoneOrderBySavedAtDesc(farmerPhone);
        List<GovernmentScheme> schemes = new ArrayList<>();
        for (SavedScheme b : bookmarks) {
            schemeRepository.findById(b.getSchemeId()).ifPresent(schemes::add);
        }
        return schemes;
    }

    /**
     * Admin/Demonstration status toggle (PS 5.3 Section 12).
     * Demonstrates live dynamic update of a scheme between OPEN and CLOSED.
     */
    @Transactional
    public GovernmentScheme updateSchemeStatus(String schemeId, String newStatus, String deadlineNote) {
        GovernmentScheme s = schemeRepository.findById(schemeId).orElse(null);
        if (s == null) return null;
        s.setApplicationStatus(newStatus);
        if (deadlineNote != null) {
            s.setDeadline(deadlineNote);
        }
        return schemeRepository.save(s);
    }

    /**
     * Resolves or defaults a farmer's profile from the database if phone is given.
     */
    public FarmerProfileDto resolveProfile(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new FarmerProfileDto("9876543210", "Demo Farmer", "Maharashtra", "Baramati", 2.5, "SMALL_MARGINAL", "Soybean, Cotton", 120000.0, true, "OWNER");
        }
        User u = userRepository.findByPhone(phone).orElse(null);
        if (u != null) {
            return new FarmerProfileDto(
                    u.getPhone(),
                    u.getFullName(),
                    u.getState() != null ? u.getState() : "Maharashtra",
                    u.getDistrict() != null ? u.getDistrict() : "Baramati",
                    u.getLandAreaAcres() != null ? u.getLandAreaAcres() : 2.5,
                    (u.getLandAreaAcres() != null && u.getLandAreaAcres() <= 5.0) ? "SMALL_MARGINAL" : "LARGE",
                    "Soybean, Cotton",
                    120000.0,
                    true,
                    "OWNER"
            );
        }
        return new FarmerProfileDto(phone, "Demo Farmer", "Maharashtra", "Baramati", 2.5, "SMALL_MARGINAL", "Soybean, Cotton", 120000.0, true, "OWNER");
    }
}
