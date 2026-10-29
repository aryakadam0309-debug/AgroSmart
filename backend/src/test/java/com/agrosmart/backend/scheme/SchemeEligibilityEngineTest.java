package com.agrosmart.backend.scheme;

import com.agrosmart.backend.entities.GovernmentScheme;
import com.agrosmart.backend.payload.FarmerProfileDto;
import com.agrosmart.backend.payload.SchemeEligibilityResultDto;
import com.agrosmart.backend.services.SchemeEligibilityEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SchemeEligibilityEngineTest {

    private SchemeEligibilityEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new SchemeEligibilityEngine();
    }

    @Test
    public void testEligibleFarmer_PMKisan() {
        GovernmentScheme pmKisan = new GovernmentScheme();
        pmKisan.setId("SCH01");
        pmKisan.setTitle("PM-KISAN Samman Nidhi");
        pmKisan.setApplicableState("ALL_INDIA");
        pmKisan.setApplicationStatus("OPEN");
        pmKisan.setRequiresLandOwnership(true);
        pmKisan.setMinLandAcres(0.1);

        FarmerProfileDto profile = new FarmerProfileDto(
                "9876543210", "Ramesh Patil", "Maharashtra", "Baramati",
                2.5, "SMALL_MARGINAL", "Soybean, Cotton", 120000.0, true, "OWNER"
        );

        SchemeEligibilityResultDto result = engine.evaluateEligibility(pmKisan, profile);

        assertNotNull(result);
        assertEquals("Likely Eligible", result.getEligibilityStatus());
        assertEquals("#2E7D32", result.getStatusColor());
        assertTrue(result.getMissingFields().isEmpty());
        assertFalse(result.getCriteriaChecks().isEmpty());
    }

    @Test
    public void testIneligibleFarmer_LandExceedsMaxCeiling() {
        // SMAM mechanization scheme with 5.0 acres max ceiling
        GovernmentScheme smam = new GovernmentScheme();
        smam.setId("SCH03");
        smam.setTitle("Sub-Mission on Agricultural Mechanization (SMAM)");
        smam.setApplicableState("ALL_INDIA");
        smam.setApplicationStatus("OPEN");
        smam.setMaxLandAcres(5.0);
        smam.setRequiresLandOwnership(true);

        FarmerProfileDto largeFarmer = new FarmerProfileDto(
                "9876543211", "Large Landholder", "Maharashtra", "Nashik",
                15.0, "LARGE", "Grapes, Sugarcane", 850000.0, true, "OWNER"
        );

        SchemeEligibilityResultDto result = engine.evaluateEligibility(smam, largeFarmer);

        assertNotNull(result);
        assertEquals("Not Eligible", result.getEligibilityStatus());
        assertEquals("#D32F2F", result.getStatusColor());
        assertTrue(result.getSummaryExplanation().contains("does not meet one or more mandatory eligibility requirements"));

        // Check that unmet criterion is specifically highlighted
        boolean foundUnmetLand = result.getCriteriaChecks().stream()
                .anyMatch(c -> "Maximum Land Limit".equals(c.getCriterionName()) && Boolean.FALSE.equals(c.getIsSatisfied()));
        assertTrue(foundUnmetLand, "Must identify landholding exceeding threshold");
    }

    @Test
    public void testIneligibleFarmer_StateMismatch() {
        GovernmentScheme mahaScheme = new GovernmentScheme();
        mahaScheme.setId("SCH07");
        mahaScheme.setTitle("Dr. Babasaheb Ambedkar Krishi Swavalamban");
        mahaScheme.setApplicableState("Maharashtra");
        mahaScheme.setApplicationStatus("OPEN");

        FarmerProfileDto punjabFarmer = new FarmerProfileDto(
                "9876543212", "Gurpreet Singh", "Punjab", "Ludhiana",
                3.0, "SMALL_MARGINAL", "Wheat, Paddy", 140000.0, true, "OWNER"
        );

        SchemeEligibilityResultDto result = engine.evaluateEligibility(mahaScheme, punjabFarmer);

        assertNotNull(result);
        assertEquals("Not Eligible", result.getEligibilityStatus());
        boolean stateUnmet = result.getCriteriaChecks().stream()
                .anyMatch(c -> "State Residence".equals(c.getCriterionName()) && Boolean.FALSE.equals(c.getIsSatisfied()));
        assertTrue(stateUnmet, "Must identify state residency mismatch");
    }

    @Test
    public void testClosedScheme_ReturnsApplicationClosed() {
        GovernmentScheme closedScheme = new GovernmentScheme();
        closedScheme.setId("SCH08");
        closedScheme.setTitle("Kharif Special Emergency Drought Relief Grant 2025");
        closedScheme.setApplicationStatus("CLOSED");
        closedScheme.setDeadline("15 September 2025");

        FarmerProfileDto profile = new FarmerProfileDto(
                "9876543210", "Ramesh Patil", "Maharashtra", "Baramati",
                2.5, "SMALL_MARGINAL", "Soybean", 120000.0, true, "OWNER"
        );

        SchemeEligibilityResultDto result = engine.evaluateEligibility(closedScheme, profile);

        assertNotNull(result);
        assertEquals("Application Closed", result.getEligibilityStatus());
        assertTrue(result.getSummaryExplanation().contains("expired") || result.getSummaryExplanation().contains("closed"));
    }

    @Test
    public void testMissingInformation_ReturnsPossiblyEligible() {
        GovernmentScheme smam = new GovernmentScheme();
        smam.setId("SCH03");
        smam.setTitle("Sub-Mission on Agricultural Mechanization (SMAM)");
        smam.setApplicableState("ALL_INDIA");
        smam.setApplicationStatus("OPEN");
        smam.setMaxLandAcres(5.0);

        // Profile with null land size
        FarmerProfileDto incompleteProfile = new FarmerProfileDto();
        incompleteProfile.setFarmerPhone("9876543210");
        incompleteProfile.setState("Maharashtra");
        incompleteProfile.setLandAreaAcres(null); // Missing!

        SchemeEligibilityResultDto result = engine.evaluateEligibility(smam, incompleteProfile);

        assertNotNull(result);
        assertEquals("Possibly Eligible – More Information Required", result.getEligibilityStatus());
        assertEquals("#F57C00", result.getStatusColor());
        assertTrue(result.getMissingFields().contains("Landholding Area (Acres)"));
    }

    @Test
    public void testUnverifiedStatus() {
        GovernmentScheme unverifiedScheme = new GovernmentScheme();
        unverifiedScheme.setId("SCH99");
        unverifiedScheme.setTitle("Proposed State Agro Subsidies 2027");
        unverifiedScheme.setApplicationStatus("UNVERIFIED");

        FarmerProfileDto profile = new FarmerProfileDto();
        profile.setState("Maharashtra");

        SchemeEligibilityResultDto result = engine.evaluateEligibility(unverifiedScheme, profile);

        assertNotNull(result);
        assertEquals("Status Unverified", result.getEligibilityStatus());
        assertEquals("#757575", result.getStatusColor());
    }
}
