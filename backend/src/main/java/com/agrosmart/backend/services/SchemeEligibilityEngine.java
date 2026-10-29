package com.agrosmart.backend.services;

import com.agrosmart.backend.entities.GovernmentScheme;
import com.agrosmart.backend.payload.CriterionCheckDto;
import com.agrosmart.backend.payload.FarmerProfileDto;
import com.agrosmart.backend.payload.SchemeEligibilityResultDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Domain-specific Government Scheme Eligibility Matching Engine (PS 5.3 Requirement 4).
 * Pure domain service evaluating official rules against a farmer's profile, returning:
 * 1. Likely Eligible
 * 2. Possibly Eligible – More Information Required
 * 3. Not Eligible
 * 4. Application Closed
 * 5. Status Unverified
 */
@Service
public class SchemeEligibilityEngine {

    public SchemeEligibilityResultDto evaluateEligibility(GovernmentScheme scheme, FarmerProfileDto profile) {
        if (scheme == null) return null;

        List<CriterionCheckDto> checks = new ArrayList<>();
        List<String> missingFields = new ArrayList<>();

        // 1. Check Application Window Status
        String appStatus = scheme.getApplicationStatus() != null ? scheme.getApplicationStatus().toUpperCase() : "OPEN";
        if ("CLOSED".equalsIgnoreCase(appStatus)) {
            String deadlineNote = scheme.getDeadline() != null ? "Application deadline (" + scheme.getDeadline() + ") has expired." : "Applications are currently closed by the official authority.";
            return new SchemeEligibilityResultDto(
                    scheme.getId(),
                    scheme.getTitle(),
                    "Application Closed",
                    "#D32F2F", // Red
                    deadlineNote + " Submissions cannot be accepted at this time. Please monitor the official portal for future sanction rounds.",
                    checks,
                    missingFields,
                    scheme
            );
        }

        if ("UNVERIFIED".equalsIgnoreCase(appStatus)) {
            return new SchemeEligibilityResultDto(
                    scheme.getId(),
                    scheme.getTitle(),
                    "Status Unverified",
                    "#757575", // Grey
                    "Official application status or eligibility guidelines are currently unverified. Direct verification at the portal is recommended.",
                    checks,
                    missingFields,
                    scheme
            );
        }

        // If no profile provided, everything is missing information
        if (profile == null) {
            missingFields.add("State & Location");
            missingFields.add("Landholding Size");
            missingFields.add("Farmer Category");
            return new SchemeEligibilityResultDto(
                    scheme.getId(),
                    scheme.getTitle(),
                    "Possibly Eligible – More Information Required",
                    "#F57C00", // Orange
                    "Complete your farmer profile (land size, state, and crop) to verify your eligibility.",
                    checks,
                    missingFields,
                    scheme
            );
        }

        boolean hasUnmetCriteria = false;

        // 2. Geographic / State Eligibility Check
        String reqState = scheme.getApplicableState();
        if (reqState != null && !reqState.trim().isEmpty() && !"ALL_INDIA".equalsIgnoreCase(reqState)) {
            String farmerState = profile.getState();
            if (farmerState == null || farmerState.trim().isEmpty()) {
                missingFields.add("State / Region");
                checks.add(new CriterionCheckDto("State Residence", null, "Not Provided", reqState, "Farmer domicile state is required for this state-specific scheme."));
            } else if (farmerState.equalsIgnoreCase(reqState) || reqState.toLowerCase().contains(farmerState.toLowerCase())) {
                checks.add(new CriterionCheckDto("State Residence", true, farmerState, reqState, "Farm location matches the scheme's designated state."));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheckDto("State Residence", false, farmerState, reqState,
                        String.format(Locale.US, "Scheme is restricted to %s. Registered farm is in %s.", reqState, farmerState)));
            }
        } else {
            checks.add(new CriterionCheckDto("State Jurisdiction", true, profile.getState() != null ? profile.getState() : "All India", "Pan-India", "Open to farmers across all Indian states and Union Territories."));
        }

        // 3. Landholding Ownership Requirement
        if (Boolean.TRUE.equals(scheme.getRequiresLandOwnership())) {
            String ownership = profile.getOwnershipStatus();
            if (ownership == null || ownership.trim().isEmpty()) {
                missingFields.add("Land Ownership Status");
                checks.add(new CriterionCheckDto("Land Title (7/12)", null, "Not Provided", "Owner / Landholder", "Official land title ownership document is required."));
            } else if ("OWNER".equalsIgnoreCase(ownership) || "LANDHOLDER".equalsIgnoreCase(ownership)) {
                checks.add(new CriterionCheckDto("Land Title (7/12)", true, ownership, "Owner", "Farmer possesses registered land ownership titles."));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheckDto("Land Title (7/12)", false, ownership, "Owner", "Scheme requires registered cultivable land in the applicant's name. Tenant / sharecropper farmers are excluded by official rules."));
            }
        }

        // 4. Maximum Landholding Size (e.g. Small & Marginal Scheme limits: <= 5.0 acres / 2 hectares)
        Double maxLand = scheme.getMaxLandAcres();
        Double farmerLand = profile.getLandAreaAcres();
        if (maxLand != null && maxLand > 0) {
            if (farmerLand == null) {
                missingFields.add("Landholding Area (Acres)");
                checks.add(new CriterionCheckDto("Maximum Land Limit", null, "Not Provided", String.format(Locale.US, "Max %.1f Acres", maxLand), "Cultivable land acreage is needed to confirm small/marginal category."));
            } else if (farmerLand <= maxLand) {
                checks.add(new CriterionCheckDto("Maximum Land Limit", true, String.format(Locale.US, "%.1f Acres", farmerLand),
                        String.format(Locale.US, "Max %.1f Acres", maxLand),
                        String.format(Locale.US, "Landholding (%.1f acres) is within the small/marginal ceiling of %.1f acres.", farmerLand, maxLand)));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheckDto("Maximum Land Limit", false, String.format(Locale.US, "%.1f Acres", farmerLand),
                        String.format(Locale.US, "Max %.1f Acres", maxLand),
                        String.format(Locale.US, "Landholding (%.1f acres) exceeds the designated threshold of %.1f acres.", farmerLand, maxLand)));
            }
        }

        // 5. Minimum Landholding Size
        Double minLand = scheme.getMinLandAcres();
        if (minLand != null && minLand > 0) {
            if (farmerLand == null) {
                missingFields.add("Landholding Area (Acres)");
                checks.add(new CriterionCheckDto("Minimum Land Requirement", null, "Not Provided", String.format(Locale.US, "Min %.1f Acres", minLand), "Minimum land requirement must be verified."));
            } else if (farmerLand >= minLand) {
                checks.add(new CriterionCheckDto("Minimum Land Requirement", true, String.format(Locale.US, "%.1f Acres", farmerLand),
                        String.format(Locale.US, "Min %.1f Acres", minLand),
                        "Cultivable plot meets minimum viable acreage."));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheckDto("Minimum Land Requirement", false, String.format(Locale.US, "%.1f Acres", farmerLand),
                        String.format(Locale.US, "Min %.1f Acres", minLand),
                        String.format(Locale.US, "Landholding (%.1f acres) is below minimum requirement of %.1f acres.", farmerLand, minLand)));
            }
        }

        // 6. Farmer Category / Social Classification
        String allowedCat = scheme.getAllowedFarmerCategories();
        if (allowedCat != null && !allowedCat.trim().isEmpty() && !"ALL".equalsIgnoreCase(allowedCat)) {
            String farmerCat = profile.getFarmerCategory();
            if (farmerCat == null || farmerCat.trim().isEmpty()) {
                missingFields.add("Farmer Category");
                checks.add(new CriterionCheckDto("Farmer Category", null, "Not Provided", allowedCat, "Farmer social/scale category is required."));
            } else {
                List<String> allowedList = Arrays.asList(allowedCat.toUpperCase().split(","));
                if (allowedList.contains("ALL") || allowedList.contains(farmerCat.toUpperCase())) {
                    checks.add(new CriterionCheckDto("Farmer Category", true, farmerCat, allowedCat, "Farmer category satisfies scheme targeting guidelines."));
                } else {
                    hasUnmetCriteria = true;
                    checks.add(new CriterionCheckDto("Farmer Category", false, farmerCat, allowedCat,
                            String.format(Locale.US, "Scheme priority is for %s. Farmer profile indicates %s.", allowedCat, farmerCat)));
                }
            }
        }

        // 7. Crop Type Compatibility
        String allowedCrops = scheme.getAllowedCrops();
        if (allowedCrops != null && !allowedCrops.trim().isEmpty() && !"ALL".equalsIgnoreCase(allowedCrops)) {
            String farmerCrops = profile.getPrimaryCrops();
            if (farmerCrops == null || farmerCrops.trim().isEmpty()) {
                missingFields.add("Crop Types Cultivated");
                checks.add(new CriterionCheckDto("Notified Crop Check", null, "Not Provided", allowedCrops, "Cultivated crops must be specified for crop-specific subsidies."));
            } else {
                boolean cropMatched = false;
                String[] allowedArr = allowedCrops.toLowerCase().split(",");
                for (String c : allowedArr) {
                    if (farmerCrops.toLowerCase().contains(c.trim())) {
                        cropMatched = true;
                        break;
                    }
                }
                if (cropMatched) {
                    checks.add(new CriterionCheckDto("Notified Crop Check", true, farmerCrops, allowedCrops, "Farmer cultivates eligible notified crops."));
                } else {
                    hasUnmetCriteria = true;
                    checks.add(new CriterionCheckDto("Notified Crop Check", false, farmerCrops, allowedCrops,
                            String.format(Locale.US, "Grown crops (%s) are not in the notified list: %s.", farmerCrops, allowedCrops)));
                }
            }
        }

        // 8. Annual Income Ceiling
        Double maxIncome = scheme.getMaxAnnualIncome();
        if (maxIncome != null && maxIncome > 0) {
            Double farmerIncome = profile.getAnnualIncome();
            if (farmerIncome == null) {
                missingFields.add("Annual Farm Income");
                checks.add(new CriterionCheckDto("Income Ceiling", null, "Not Provided", String.format(Locale.US, "Max ₹%.0f", maxIncome), "Annual income is required to evaluate economic threshold."));
            } else if (farmerIncome <= maxIncome) {
                checks.add(new CriterionCheckDto("Income Ceiling", true, String.format(Locale.US, "₹%.0f", farmerIncome),
                        String.format(Locale.US, "Max ₹%.0f", maxIncome), "Farmer income falls within the welfare limit."));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheckDto("Income Ceiling", false, String.format(Locale.US, "₹%.0f", farmerIncome),
                        String.format(Locale.US, "Max ₹%.0f", maxIncome),
                        String.format(Locale.US, "Annual income (₹%.0f) exceeds eligibility ceiling of ₹%.0f.", farmerIncome, maxIncome)));
            }
        }

        // 9. Synthesize Outcome
        String finalStatus;
        String statusColor;
        String summary;

        if (hasUnmetCriteria) {
            finalStatus = "Not Eligible";
            statusColor = "#D32F2F"; // Red
            summary = "Profile does not meet one or more mandatory eligibility requirements. See detailed breakdown below for unmet criteria.";
        } else if (!missingFields.isEmpty()) {
            finalStatus = "Possibly Eligible – More Information Required";
            statusColor = "#F57C00"; // Orange
            summary = String.format(Locale.US, "Profile is compatible so far, but requires %d additional field(s) to verify: %s.",
                    missingFields.size(), String.join(", ", missingFields));
        } else {
            finalStatus = "Likely Eligible";
            statusColor = "#2E7D32"; // Green
            summary = "All verified official eligibility criteria are satisfied by your profile. You may proceed to prepare documents and apply on the official portal.";
        }

        return new SchemeEligibilityResultDto(
                scheme.getId(),
                scheme.getTitle(),
                finalStatus,
                statusColor,
                summary,
                checks,
                missingFields,
                scheme
        );
    }
}
