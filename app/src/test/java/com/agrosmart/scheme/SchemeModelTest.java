package com.agrosmart.scheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.agrosmart.models.CriterionCheck;
import com.agrosmart.models.FarmerProfile;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.models.SchemeEligibilityResult;

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
}
