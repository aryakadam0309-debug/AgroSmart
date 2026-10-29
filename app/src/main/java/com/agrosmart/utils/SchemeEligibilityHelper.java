package com.agrosmart.utils;

import com.agrosmart.models.CriterionCheck;
import com.agrosmart.models.FarmerProfile;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.models.SchemeEligibilityResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Offline & Local Eligibility Matching Engine for Android Client.
 * Mirrors the Spring Boot SchemeEligibilityEngine with 100% logic parity (PS 5.3 Requirement 4).
 * Handles the 5 official outcomes:
 * 1. Likely Eligible
 * 2. Possibly Eligible – More Information Required
 * 3. Not Eligible
 * 4. Application Closed
 * 5. Status Unverified
 */
public class SchemeEligibilityHelper {

    public static SchemeEligibilityResult evaluate(GovernmentScheme scheme, FarmerProfile profile) {
        if (scheme == null) return null;

        List<CriterionCheck> checks = new ArrayList<>();
        List<String> missingFields = new ArrayList<>();

        // 1. Application Window Status
        String appStatus = scheme.getApplicationStatus() != null ? scheme.getApplicationStatus().toUpperCase() : "OPEN";
        if ("CLOSED".equalsIgnoreCase(appStatus)) {
            String deadlineNote = scheme.getDeadline() != null ? "Application deadline (" + scheme.getDeadline() + ") has expired." : "Applications are currently closed by the official authority.";
            SchemeEligibilityResult res = new SchemeEligibilityResult();
            res.setScheme(scheme);
            res.setSchemeId(scheme.getSchemeId());
            res.setSchemeTitle(scheme.getSchemeName());
            res.setEligibilityStatus("Application Closed");
            res.setStatusColor("#455A64");
            res.setSummaryExplanation(deadlineNote + " Submissions cannot be accepted at this time. Please monitor official announcements for subsequent budget allocations.");
            res.setCriteriaChecks(checks);
            res.setMissingFields(missingFields);
            return res;
        }

        if ("UNVERIFIED".equalsIgnoreCase(appStatus)) {
            SchemeEligibilityResult res = new SchemeEligibilityResult();
            res.setScheme(scheme);
            res.setSchemeId(scheme.getSchemeId());
            res.setSchemeTitle(scheme.getSchemeName());
            res.setEligibilityStatus("Status Unverified");
            res.setStatusColor("#757575");
            res.setSummaryExplanation("Official application status or eligibility guidelines cannot be confirmed from a verified government source.");
            res.setCriteriaChecks(checks);
            res.setMissingFields(missingFields);
            return res;
        }

        // Profile existence check
        if (profile == null) {
            missingFields.add("State & Location");
            missingFields.add("Landholding Size");
            missingFields.add("Farmer Category");
            SchemeEligibilityResult res = new SchemeEligibilityResult();
            res.setScheme(scheme);
            res.setSchemeId(scheme.getSchemeId());
            res.setSchemeTitle(scheme.getSchemeName());
            res.setEligibilityStatus("Possibly Eligible – More Information Required");
            res.setStatusColor("#F57C00");
            res.setSummaryExplanation("Complete your registered farm profile (land acreage, category, state) to evaluate official eligibility.");
            res.setCriteriaChecks(checks);
            res.setMissingFields(missingFields);
            return res;
        }

        boolean hasUnmetCriteria = false;

        // 2. Geographic / State Domicile Check
        String reqState = scheme.getApplicableState();
        if (reqState != null && !reqState.trim().isEmpty() && !"ALL_INDIA".equalsIgnoreCase(reqState)) {
            String farmerState = profile.getState();
            if (farmerState == null || farmerState.trim().isEmpty()) {
                missingFields.add("State / Region");
                checks.add(new CriterionCheck("State Residence", null, "Not Provided", reqState, "Farmer domicile state is required for this state-specific scheme."));
            } else if (farmerState.equalsIgnoreCase(reqState) || reqState.toLowerCase().contains(farmerState.toLowerCase())) {
                checks.add(new CriterionCheck("State Residence", true, farmerState, reqState, "Farm location matches the scheme's designated state jurisdiction."));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheck("State Residence", false, farmerState, reqState,
                        String.format(Locale.US, "Scheme is restricted to %s. Registered farm is in %s.", reqState, farmerState)));
            }
        } else {
            checks.add(new CriterionCheck("State Jurisdiction", true, profile.getState() != null ? profile.getState() : "All India", "Pan-India", "Open to farmers across all Indian states and Union Territories."));
        }

        // 3. Landholding Ownership Requirement (7/12 & 8-A Title)
        if (Boolean.TRUE.equals(scheme.getRequiresLandOwnership())) {
            String ownership = profile.getOwnershipStatus();
            if (ownership == null || ownership.trim().isEmpty()) {
                missingFields.add("Land Ownership Status");
                checks.add(new CriterionCheck("Land Title (7/12)", null, "Not Provided", "Owner / Landholder", "Official land title ownership document is required."));
            } else if ("OWNER".equalsIgnoreCase(ownership) || "LANDHOLDER".equalsIgnoreCase(ownership)) {
                checks.add(new CriterionCheck("Land Title (7/12)", true, ownership, "Owner", "Farmer possesses registered land ownership titles."));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheck("Land Title (7/12)", false, ownership, "Owner", "Scheme requires registered cultivable land in the applicant's name. Tenant / sharecropper farmers are excluded by official rules."));
            }
        }

        // 4. Maximum Landholding Size (Land Ceiling Check)
        Double maxLand = scheme.getMaxLandAcres();
        Double farmerLand = profile.getLandAreaAcres();
        if (maxLand != null && maxLand > 0) {
            if (farmerLand == null) {
                missingFields.add("Landholding Area (Acres)");
                checks.add(new CriterionCheck("Maximum Land Limit", null, "Not Provided", String.format(Locale.US, "Max %.1f Acres", maxLand), "Cultivable land acreage is needed to confirm eligibility."));
            } else if (farmerLand <= maxLand) {
                checks.add(new CriterionCheck("Maximum Land Limit", true, String.format(Locale.US, "%.1f Acres", farmerLand),
                        String.format(Locale.US, "Max %.1f Acres", maxLand),
                        String.format(Locale.US, "Landholding (%.1f acres) is within the eligible ceiling of %.1f acres.", farmerLand, maxLand)));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheck("Maximum Land Limit", false, String.format(Locale.US, "%.1f Acres", farmerLand),
                        String.format(Locale.US, "Max %.1f Acres", maxLand),
                        String.format(Locale.US, "Landholding (%.1f acres) exceeds designated threshold of %.1f acres.", farmerLand, maxLand)));
            }
        }

        // 5. Minimum Landholding Size
        Double minLand = scheme.getMinLandAcres();
        if (minLand != null && minLand > 0) {
            if (farmerLand == null) {
                missingFields.add("Landholding Area (Acres)");
                checks.add(new CriterionCheck("Minimum Land Requirement", null, "Not Provided", String.format(Locale.US, "Min %.1f Acres", minLand), "Minimum land requirement must be verified."));
            } else if (farmerLand >= minLand) {
                checks.add(new CriterionCheck("Minimum Land Requirement", true, String.format(Locale.US, "%.1f Acres", farmerLand),
                        String.format(Locale.US, "Min %.1f Acres", minLand), "Cultivable plot meets minimum viable acreage."));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheck("Minimum Land Requirement", false, String.format(Locale.US, "%.1f Acres", farmerLand),
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
                checks.add(new CriterionCheck("Farmer Category", null, "Not Provided", allowedCat, "Farmer social/scale category is required."));
            } else {
                List<String> allowedList = Arrays.asList(allowedCat.toUpperCase().split(","));
                if (allowedList.contains("ALL") || allowedList.contains(farmerCat.toUpperCase())) {
                    checks.add(new CriterionCheck("Farmer Category", true, farmerCat, allowedCat, "Farmer category satisfies scheme targeting guidelines."));
                } else {
                    hasUnmetCriteria = true;
                    checks.add(new CriterionCheck("Farmer Category", false, farmerCat, allowedCat,
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
                checks.add(new CriterionCheck("Notified Crop Check", null, "Not Provided", allowedCrops, "Cultivated crops must be specified for crop-specific subsidies."));
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
                    checks.add(new CriterionCheck("Notified Crop Check", true, farmerCrops, allowedCrops, "Farmer cultivates eligible notified crops."));
                } else {
                    hasUnmetCriteria = true;
                    checks.add(new CriterionCheck("Notified Crop Check", false, farmerCrops, allowedCrops,
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
                checks.add(new CriterionCheck("Income Ceiling", null, "Not Provided", String.format(Locale.US, "Max ₹%.0f", maxIncome), "Annual income is required to evaluate economic threshold."));
            } else if (farmerIncome <= maxIncome) {
                checks.add(new CriterionCheck("Income Ceiling", true, String.format(Locale.US, "₹%.0f", farmerIncome),
                        String.format(Locale.US, "Max ₹%.0f", maxIncome), "Farmer income falls within the welfare limit."));
            } else {
                hasUnmetCriteria = true;
                checks.add(new CriterionCheck("Income Ceiling", false, String.format(Locale.US, "₹%.0f", farmerIncome),
                        String.format(Locale.US, "Max ₹%.0f", maxIncome),
                        String.format(Locale.US, "Annual income (₹%.0f) exceeds eligibility ceiling of ₹%.0f.", farmerIncome, maxIncome)));
            }
        }

        // Synthesize Outcome
        String finalStatus;
        String statusColor;
        String summary;

        if (hasUnmetCriteria) {
            finalStatus = "Not Eligible";
            statusColor = "#D32F2F";
            summary = "Profile does not meet one or more mandatory eligibility requirements. See detailed breakdown below for unmet criteria.";
        } else if (!missingFields.isEmpty()) {
            finalStatus = "Possibly Eligible – More Information Required";
            statusColor = "#F57C00";
            summary = String.format(Locale.US, "Profile is compatible so far, but requires %d additional field(s) to verify: %s.",
                    missingFields.size(), String.join(", ", missingFields));
        } else {
            finalStatus = "Likely Eligible";
            statusColor = "#2E7D32";
            summary = "All verified official eligibility criteria are satisfied by your profile. You may proceed to prepare documents and apply on the official portal.";
        }

        SchemeEligibilityResult res = new SchemeEligibilityResult();
        res.setScheme(scheme);
        res.setSchemeId(scheme.getSchemeId());
        res.setSchemeTitle(scheme.getSchemeName());
        res.setEligibilityStatus(finalStatus);
        res.setStatusColor(statusColor);
        res.setSummaryExplanation(summary);
        res.setCriteriaChecks(checks);
        res.setMissingFields(missingFields);
        return res;
    }
}
