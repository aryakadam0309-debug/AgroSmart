package com.agrosmart.scheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.agrosmart.models.CriterionCheck;
import com.agrosmart.models.FarmerProfile;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.models.SchemeEligibilityResult;
import com.agrosmart.repository.SchemeRepository;
import com.agrosmart.utils.SchemeEligibilityHelper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SchemeModelTest {

    @Test
    public void testGovernmentSchemeModelEncapsulation() {
        GovernmentScheme scheme = new GovernmentScheme();
        scheme.setSchemeId("SCH01");
        scheme.setSchemeName("PM-KISAN Samman Nidhi");
        scheme.setMinistry("Ministry of Agriculture & Farmers Welfare");
        scheme.setCategory("Financial Assistance");
        scheme.setLevel("CENTRAL");
        scheme.setApplicableState("ALL_INDIA");
        scheme.setBenefits("₹6,000 / Year via DBT");
        scheme.setOfficialPortalUrl("https://pmkisan.gov.in");
        scheme.setSourceUrl("https://pmkisan.gov.in/Guidelines.aspx");
        scheme.setDeadline("Ongoing (Open)");
        scheme.setApplicationStatus("OPEN");
        scheme.setLastVerifiedDate("2026-10-25");
        scheme.setMinLandAcres(0.1);
        scheme.setRequiresLandOwnership(true);

        assertEquals("SCH01", scheme.getSchemeId());
        assertEquals("PM-KISAN Samman Nidhi", scheme.getSchemeName());
        assertEquals("PM-KISAN Samman Nidhi", scheme.getTitle());
        assertEquals("CENTRAL", scheme.getLevel());
        assertEquals("ALL_INDIA", scheme.getApplicableState());
        assertEquals("https://pmkisan.gov.in", scheme.getOfficialPortalUrl());
        assertEquals("OPEN", scheme.getApplicationStatus());
        assertEquals("2026-10-25", scheme.getLastVerifiedDate());
        assertTrue(scheme.getRequiresLandOwnership());
        assertEquals(0.1, scheme.getMinLandAcres(), 0.01);
    }

    @Test
    public void testSchemeEligibilityResultModel() {
        SchemeEligibilityResult result = new SchemeEligibilityResult();
        result.setSchemeId("SCH03");
        result.setSchemeTitle("Sub-Mission on Agricultural Mechanization");
        result.setEligibilityStatus("Not Eligible");
        result.setStatusColor("#D32F2F");
        result.setSummaryExplanation("Landholding (15.0 acres) exceeds the designated threshold of 5.0 acres.");

        List<CriterionCheck> checks = new ArrayList<>();
        checks.add(new CriterionCheck("Maximum Land Limit", false, "15.0 Acres", "Max 5.0 Acres", "Exceeds small farmer ceiling"));
        result.setCriteriaChecks(checks);

        assertEquals("SCH03", result.getSchemeId());
        assertEquals("Not Eligible", result.getEligibilityStatus());
        assertEquals("#D32F2F", result.getStatusColor());
        assertEquals(1, result.getCriteriaChecks().size());
        assertEquals(Boolean.FALSE, result.getCriteriaChecks().get(0).getIsSatisfied());
        assertTrue(result.getCriteriaChecks().get(0).getExplanation().contains("Exceeds"));
    }

    @Test
    public void testFarmerProfileModel() {
        FarmerProfile profile = new FarmerProfile(
                "9876543210", "Suresh Jadhav", "Maharashtra", "Baramati",
                2.5, "SMALL_MARGINAL", "Soybean, Cotton", 120000.0, true, "OWNER"
        );

        assertEquals("9876543210", profile.getFarmerPhone());
        assertEquals("Suresh Jadhav", profile.getFullName());
        assertEquals("Maharashtra", profile.getState());
        assertEquals(2.5, profile.getLandAreaAcres(), 0.01);
        assertEquals("SMALL_MARGINAL", profile.getFarmerCategory());
        assertEquals("Soybean, Cotton", profile.getPrimaryCrops());
        assertEquals("OWNER", profile.getOwnershipStatus());
        assertTrue(profile.getHasIrrigation());
    }

    @Test
    public void testFiftyAcresFarmer_SMAM_IsNotEligible() {
        // SMAM Mechanization with 5.0 acres max ceiling
        GovernmentScheme smam = new GovernmentScheme();
        smam.setSchemeId("SCH03");
        smam.setSchemeName("Sub-Mission on Agricultural Mechanization");
        smam.setApplicableState("ALL_INDIA");
        smam.setApplicationStatus("OPEN");
        smam.setMinLandAcres(0.1);
        smam.setMaxLandAcres(5.0);
        smam.setAllowedFarmerCategories("SMALL_MARGINAL,SC_ST,WOMEN");
        smam.setRequiresLandOwnership(true);

        // Farmer with 50 acres
        FarmerProfile largeFarmer = new FarmerProfile(
                "9876543210", "Large Farmer", "Maharashtra", "Baramati",
                50.0, "LARGE", "Soybean, Cotton", 500000.0, true, "OWNER"
        );

        SchemeEligibilityResult result = SchemeEligibilityHelper.evaluate(smam, largeFarmer);

        assertNotNull(result);
        assertEquals("Not Eligible", result.getEligibilityStatus());
        assertEquals("#D32F2F", result.getStatusColor());

        // Ensure maximum land ceiling failure is recorded in criteria checklist
        boolean foundLandFailure = false;
        for (CriterionCheck c : result.getCriteriaChecks()) {
            if ("Maximum Land Limit".equals(c.getCriterionName()) && Boolean.FALSE.equals(c.getIsSatisfied())) {
                foundLandFailure = true;
                assertTrue(c.getExplanation().contains("exceeds designated threshold"));
                break;
            }
        }
        assertTrue("Must fail on Maximum Land Limit when farmer enters 50 acres", foundLandFailure);
    }

    @Test
    public void testTwoAndHalfAcresFarmer_SMAM_IsLikelyEligible() {
        GovernmentScheme smam = new GovernmentScheme();
        smam.setSchemeId("SCH03");
        smam.setSchemeName("Sub-Mission on Agricultural Mechanization");
        smam.setApplicableState("ALL_INDIA");
        smam.setApplicationStatus("OPEN");
        smam.setMinLandAcres(0.1);
        smam.setMaxLandAcres(5.0);
        smam.setAllowedFarmerCategories("SMALL_MARGINAL,SC_ST,WOMEN");
        smam.setRequiresLandOwnership(true);

        FarmerProfile smallFarmer = new FarmerProfile(
                "9876543210", "Small Farmer", "Maharashtra", "Baramati",
                2.5, "SMALL_MARGINAL", "Soybean, Cotton", 120000.0, true, "OWNER"
        );

        SchemeEligibilityResult result = SchemeEligibilityHelper.evaluate(smam, smallFarmer);

        assertNotNull(result);
        assertEquals("Likely Eligible", result.getEligibilityStatus());
        assertEquals("#2E7D32", result.getStatusColor());
    }

    @Test
    public void testClosedScheme_ReturnsApplicationClosed() {
        GovernmentScheme closedScheme = new GovernmentScheme();
        closedScheme.setSchemeId("SCH08");
        closedScheme.setSchemeName("Kharif Emergency Flood & Drought Relief");
        closedScheme.setApplicationStatus("CLOSED");
        closedScheme.setDeadline("Closed on 15 Sep 2025");

        FarmerProfile farmer = new FarmerProfile();
        farmer.setLandAreaAcres(50.0);

        SchemeEligibilityResult result = SchemeEligibilityHelper.evaluate(closedScheme, farmer);

        assertNotNull(result);
        assertEquals("Application Closed", result.getEligibilityStatus());
        assertEquals("#455A64", result.getStatusColor());
    }

    @Test
    public void testSchemeRepositoryPopulatesAllEightSchemesWithConstraints() {
        List<GovernmentScheme> all = SchemeRepository.getInstance().getAllSchemes();
        assertEquals(8, all.size());

        // Check SMAM has maxLandAcres = 5.0
        GovernmentScheme smam = all.stream().filter(s -> "SCH03".equals(s.getSchemeId())).findFirst().orElse(null);
        assertNotNull(smam);
        assertEquals(5.0, smam.getMaxLandAcres(), 0.01);

        // Check PMKSY has maxLandAcres = 12.5
        GovernmentScheme pmksy = all.stream().filter(s -> "SCH05".equals(s.getSchemeId())).findFirst().orElse(null);
        assertNotNull(pmksy);
        assertEquals(12.5, pmksy.getMaxLandAcres(), 0.01);

        // Check Dr. Ambedkar has maxLandAcres = 15.0
        GovernmentScheme ambedkar = all.stream().filter(s -> "SCH07".equals(s.getSchemeId())).findFirst().orElse(null);
        assertNotNull(ambedkar);
        assertEquals(15.0, ambedkar.getMaxLandAcres(), 0.01);

        // Check SCH08 is CLOSED
        GovernmentScheme relief = all.stream().filter(s -> "SCH08".equals(s.getSchemeId())).findFirst().orElse(null);
        assertNotNull(relief);
        assertEquals("CLOSED", relief.getApplicationStatus());
    }
}
