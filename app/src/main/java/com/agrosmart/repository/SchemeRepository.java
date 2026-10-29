package com.agrosmart.repository;

import com.agrosmart.models.GovernmentScheme;

import java.util.ArrayList;
import java.util.List;

public class SchemeRepository {

    private static SchemeRepository instance;
    private final List<GovernmentScheme> verifiedSchemes = new ArrayList<>();

    public static synchronized SchemeRepository getInstance() {
        if (instance == null) {
            instance = new SchemeRepository();
        }
        return instance;
    }

    public SchemeRepository() {
        populateSchemes();
    }

    private void populateSchemes() {
        verifiedSchemes.clear();

        // 1. PM-KISAN (SCH01) - Financial Income Support
        GovernmentScheme sch01 = new GovernmentScheme();
        sch01.setId(1L);
        sch01.setSchemeId("SCH01");
        sch01.setSchemeName("PM Kisan Samman Nidhi (PM-KISAN)");
        sch01.setCategory("Financial Assistance");
        sch01.setLevel("CENTRAL");
        sch01.setMinistry("Ministry of Agriculture & Farmers Welfare");
        sch01.setSubsidyAmount("₹6,000 / Year");
        sch01.setDescription("Central sector scheme providing income support to all landholding farmer families across the country.");
        sch01.setBenefits("Direct financial grant of ₹6,000 per annum paid in 3 four-monthly cycles of ₹2,000 directly into Aadhaar-linked bank accounts via DBT without middlemen.");
        sch01.setEligibility("All landholding farmer families with cultivable land in their names across India. Tenant farmers without land titles and institutional landholders are excluded.");
        sch01.setRequiredDocuments("1. Aadhaar Card\n2. Landholding ownership documents (7/12 & 8-A extract)\n3. Bank Account details linked with Aadhaar\n4. Active mobile number");
        sch01.setApplicationProcess("1. Visit pmkisan.gov.in -> Farmers Corner -> New Farmer Registration.\n2. Enter Aadhaar and mobile number, verify OTP.\n3. Upload land survey number and 7/12 details.\n4. Self-registration is validated by State Nodal Officer.");
        sch01.setOfficialPortalUrl("https://pmkisan.gov.in");
        sch01.setSourceUrl("https://pmkisan.gov.in/Guidelines.aspx");
        sch01.setHelpline("155261 / 011-24300606");
        sch01.setImportantDates("Ongoing (Disbursements in April, August, and December)");
        sch01.setApplicationStatus("OPEN");
        sch01.setLastVerifiedDate("2026-10-25");
        sch01.setApplicableState("ALL_INDIA");
        sch01.setMinLandAcres(0.1);
        sch01.setMaxLandAcres(null); // No upper land ceiling for owners
        sch01.setAllowedFarmerCategories("ALL");
        sch01.setAllowedCrops("ALL");
        sch01.setMaxAnnualIncome(null);
        sch01.setRequiresLandOwnership(true);
        sch01.setActive(true);
        verifiedSchemes.add(sch01);

        // 2. PMFBY (SCH02) - Comprehensive Crop Insurance
        GovernmentScheme sch02 = new GovernmentScheme();
        sch02.setId(2L);
        sch02.setSchemeId("SCH02");
        sch02.setSchemeName("Pradhan Mantri Fasal Bima Yojana (PMFBY)");
        sch02.setCategory("Crop Insurance");
        sch02.setLevel("CENTRAL");
        sch02.setMinistry("Ministry of Agriculture & Farmers Welfare");
        sch02.setSubsidyAmount("Up to 90% Premium Subsidy");
        sch02.setDescription("Comprehensive crop insurance scheme providing financial support to farmers suffering crop loss or damage arising out of natural calamities.");
        sch02.setBenefits("Full sum insured coverage for yield loss, localized disasters, and post-harvest cyclone damage. Farmer pays nominal premium: 2% for Kharif food/oilseeds, 1.5% for Rabi, 5% for commercial/horticultural crops.");
        sch02.setEligibility("All farmers including sharecroppers and tenant farmers growing notified crops in notified areas during the season.");
        sch02.setRequiredDocuments("1. Sowing certificate or declaration\n2. Land revenue records (7/12 extract) or tenancy agreement\n3. Bank account passbook\n4. Aadhaar Card");
        sch02.setApplicationProcess("Register through National Crop Insurance Portal (pmfby.gov.in), authorized bank branch, or CSC before seasonal cut-off date.");
        sch02.setOfficialPortalUrl("https://pmfby.gov.in");
        sch02.setSourceUrl("https://pmfby.gov.in/pdf/Revised_Operational_Guidelines.pdf");
        sch02.setHelpline("14447");
        sch02.setImportantDates("Kharif cut-off date: 31st July; Rabi cut-off date: 31st December");
        sch02.setApplicationStatus("OPEN");
        sch02.setLastVerifiedDate("2026-10-25");
        sch02.setApplicableState("ALL_INDIA");
        sch02.setMinLandAcres(null);
        sch02.setMaxLandAcres(null);
        sch02.setAllowedFarmerCategories("ALL");
        sch02.setAllowedCrops("Soybean,Cotton,Wheat,Rice,Maize,Pulses,ALL");
        sch02.setMaxAnnualIncome(null);
        sch02.setRequiresLandOwnership(false);
        sch02.setActive(true);
        verifiedSchemes.add(sch02);

        // 3. SMAM (SCH03) - Farm Machinery Subsidy (Strict <= 5.0 acres ceiling)
        GovernmentScheme sch03 = new GovernmentScheme();
        sch03.setId(3L);
        sch03.setSchemeId("SCH03");
        sch03.setSchemeName("Sub-Mission on Agricultural Mechanization (SMAM)");
        sch03.setCategory("Farm Machinery");
        sch03.setLevel("CENTRAL");
        sch03.setMinistry("Ministry of Agriculture & Farmers Welfare");
        sch03.setSubsidyAmount("40% - 50% Subsidy on Farm Equipment");
        sch03.setDescription("Promotes farm mechanization among small and marginal farmers by subsidizing purchase of modern agricultural machinery.");
        sch03.setBenefits("Subsidy ranging from 40% to 50% on tractors, power tillers, rotavators, seed drills, and multi-crop threshers depending on farmer category.");
        sch03.setEligibility("Small and marginal farmers (<= 5.0 acres / 2 hectares), SC/ST, and women farmers purchasing certified equipment. Large landholders (> 5.0 acres) are ineligible.");
        sch03.setRequiredDocuments("1. Aadhaar Card\n2. Land record documents showing <= 5.0 acres (7/12 & 8-A)\n3. Caste certificate (if applicable)\n4. Quotation from authorized dealer\n5. Bank details");
        sch03.setApplicationProcess("Apply through DBT Agriculture Portal (agrimachinery.nic.in) or State Mahadbt portal. Upload quotation and land records for sanction.");
        sch03.setOfficialPortalUrl("https://agrimachinery.nic.in");
        sch03.setSourceUrl("https://agrimachinery.nic.in/Guidelines/SMAM_Guidelines.pdf");
        sch03.setHelpline("011-23382012");
        sch03.setImportantDates("Annual Quota Window");
        sch03.setApplicationStatus("OPEN");
        sch03.setLastVerifiedDate("2026-10-25");
        sch03.setApplicableState("ALL_INDIA");
        sch03.setMinLandAcres(0.1);
        sch03.setMaxLandAcres(5.0); // STRICT CEILING: <= 5.0 acres
        sch03.setAllowedFarmerCategories("SMALL_MARGINAL,SC_ST,WOMEN");
        sch03.setAllowedCrops("ALL");
        sch03.setMaxAnnualIncome(null);
        sch03.setRequiresLandOwnership(true);
        sch03.setActive(true);
        verifiedSchemes.add(sch03);

        // 4. KCC (SCH04) - Kisan Credit Card
        GovernmentScheme sch04 = new GovernmentScheme();
        sch04.setId(4L);
        sch04.setSchemeId("SCH04");
        sch04.setSchemeName("Kisan Credit Card (KCC) Scheme");
        sch04.setCategory("Agricultural Credit");
        sch04.setLevel("CENTRAL");
        sch04.setMinistry("Ministry of Finance & RBI");
        sch04.setSubsidyAmount("4% Effective Concessional Loan Interest");
        sch04.setDescription("Adequate and timely credit support from the banking system for agricultural crop production and investment needs.");
        sch04.setBenefits("Short term crop loans up to ₹3 Lakh at standard 7% interest rate, with 3% prompt repayment incentive bringing effective interest down to 4% per annum. No collateral required up to ₹1.6 Lakh.");
        sch04.setEligibility("All farmers - individuals/joint cultivators, tenant farmers, oral lessees, sharecroppers, and SHGs.");
        sch04.setRequiredDocuments("1. Duly filled KCC application form\n2. Land record documents (7/12) or crop tenancy agreement\n3. Identity Proof (Aadhaar / Voter ID)\n4. Address Proof\n5. 2 Passport size photos");
        sch04.setApplicationProcess("Submit KCC one-page form at any Commercial Bank, RRB, or Cooperative Bank branch, or apply via digital state portals.");
        sch04.setOfficialPortalUrl("https://myscheme.gov.in/schemes/kcc");
        sch04.setSourceUrl("https://rbi.org.in/Scripts/NotificationUser.aspx?Id=11488");
        sch04.setHelpline("1800-180-1551");
        sch04.setImportantDates("Ongoing (Continuous enrollment)");
        sch04.setApplicationStatus("OPEN");
        sch04.setLastVerifiedDate("2026-10-25");
        sch04.setApplicableState("ALL_INDIA");
        sch04.setMinLandAcres(0.1);
        sch04.setMaxLandAcres(25.0);
        sch04.setAllowedFarmerCategories("ALL");
        sch04.setAllowedCrops("ALL");
        sch04.setMaxAnnualIncome(null);
        sch04.setRequiresLandOwnership(false);
        sch04.setActive(true);
        verifiedSchemes.add(sch04);

        // 5. PMKSY (SCH05) - Micro-Irrigation (Ceiling <= 12.5 acres / 5 ha)
        GovernmentScheme sch05 = new GovernmentScheme();
        sch05.setId(5L);
        sch05.setSchemeId("SCH05");
        sch05.setSchemeName("PMKSY - Per Drop More Crop (Micro-Irrigation)");
        sch05.setCategory("Irrigation & Water");
        sch05.setLevel("CENTRAL");
        sch05.setMinistry("Ministry of Agriculture & Farmers Welfare");
        sch05.setSubsidyAmount("Up to 55% Subsidy for Drip / Sprinkler");
        sch05.setDescription("Promotes water-use efficiency through micro-irrigation technologies (drip and sprinkler systems), saving up to 50% irrigation water.");
        sch05.setBenefits("55% subsidy of total benchmark cost for Small & Marginal farmers; 45% subsidy for other farmers installing ISI-marked micro-irrigation systems.");
        sch05.setEligibility("All landholding farmers with assured water source (well, tube-well, farm pond) and cultivable land up to 12.5 acres (5 hectares).");
        sch05.setRequiredDocuments("1. Aadhaar Card\n2. Land ownership record (7/12 extract)\n3. Electricity bill or water source certificate\n4. Drip layout design and quotation\n5. Bank passbook");
        sch05.setApplicationProcess("Apply online via State Agriculture Portal (Mahadbt in Maharashtra or state PMKSY portal), undergo pre-sanction inspection, install and verify.");
        sch05.setOfficialPortalUrl("https://pmksy.gov.in");
        sch05.setSourceUrl("https://pmksy.gov.in/pdmc/Guidelines.aspx");
        sch05.setHelpline("011-23383370");
        sch05.setImportantDates("Annual Budget Allocation");
        sch05.setApplicationStatus("OPEN");
        sch05.setLastVerifiedDate("2026-10-25");
        sch05.setApplicableState("ALL_INDIA");
        sch05.setMinLandAcres(0.5);
        sch05.setMaxLandAcres(12.5); // CEILING: 12.5 acres / 5 ha
        sch05.setAllowedFarmerCategories("ALL");
        sch05.setAllowedCrops("ALL");
        sch05.setMaxAnnualIncome(null);
        sch05.setRequiresLandOwnership(true);
        sch05.setActive(true);
        verifiedSchemes.add(sch05);

        // 6. Soil Health Card (SCH06) - 100% Free Soil Testing
        GovernmentScheme sch06 = new GovernmentScheme();
        sch06.setId(6L);
        sch06.setSchemeId("SCH06");
        sch06.setSchemeName("Soil Health Card Scheme");
        sch06.setCategory("Soil Health");
        sch06.setLevel("CENTRAL");
        sch06.setMinistry("Ministry of Agriculture & Farmers Welfare");
        sch06.setSubsidyAmount("100% Free Soil Nutrient Testing");
        sch06.setDescription("Provides comprehensive soil fertility status report every two years to optimize fertilizer usage and restore soil health.");
        sch06.setBenefits("Free laboratory testing of 12 crucial parameters: N, P, K, S, Zn, Fe, Cu, Mn, Bo, pH, EC, and Organic Carbon, with crop-specific fertilizer dosage recommendations.");
        sch06.setEligibility("All farmers cultivating agricultural land in India across all categories and holding sizes.");
        sch06.setRequiredDocuments("1. Aadhaar Card or Farmer ID\n2. Land Survey / Gut Number details");
        sch06.setApplicationProcess("Soil samples collected from farmer fields by agri department staff, tested at labs, and printed cards distributed or downloaded from soilhealth.dac.gov.in.");
        sch06.setOfficialPortalUrl("https://soilhealth.dac.gov.in");
        sch06.setSourceUrl("https://soilhealth.dac.gov.in/about-scheme");
        sch06.setHelpline("011-24305948");
        sch06.setImportantDates("Continuous (Biennial)");
        sch06.setApplicationStatus("CONTINUOUS");
        sch06.setLastVerifiedDate("2026-10-25");
        sch06.setApplicableState("ALL_INDIA");
        sch06.setMinLandAcres(null);
        sch06.setMaxLandAcres(null);
        sch06.setAllowedFarmerCategories("ALL");
        sch06.setAllowedCrops("ALL");
        sch06.setMaxAnnualIncome(null);
        sch06.setRequiresLandOwnership(false);
        sch06.setActive(true);
        verifiedSchemes.add(sch06);

        // 7. Dr. Ambedkar Swavalamban Yojana (SCH07) - State Scheme (Maharashtra, SC/ST, <= 15.0 acres)
        GovernmentScheme sch07 = new GovernmentScheme();
        sch07.setId(7L);
        sch07.setSchemeId("SCH07");
        sch07.setSchemeName("Dr. Babasaheb Ambedkar Krishi Swavalamban Yojana");
        sch07.setCategory("Irrigation & Water");
        sch07.setLevel("STATE");
        sch07.setMinistry("Department of Agriculture, Govt. of Maharashtra");
        sch07.setSubsidyAmount("100% Grant up to ₹2.5 Lakh for Irrigation");
        sch07.setDescription("State-sponsored scheme for SC farmers in Maharashtra to establish assured irrigation through new wells, solar pumps, and micro-irrigation.");
        sch07.setBenefits("100% financial assistance: New Well up to ₹2,50,000, In-well boring up to ₹20,000, Drip irrigation up to ₹50,000, Sprinkler up to ₹25,000.");
        sch07.setEligibility("Scheduled Caste (SC) / Nav-Bouddha farmers in Maharashtra owning cultivable land between 1.0 to 15.0 acres with annual income <= ₹1.5 Lakh.");
        sch07.setRequiredDocuments("1. Caste Certificate issued by competent authority\n2. Land 7/12 and 8-A records\n3. Tahsildar Income Certificate (<= ₹1.5 Lakh)\n4. GSDA water feasibility certificate\n5. Bank Passbook and Aadhaar");
        sch07.setApplicationProcess("Register and submit application on Mahadbt Farmer Portal (mahadbt.maharashtra.gov.in). Selection via online lottery.");
        sch07.setOfficialPortalUrl("https://mahadbt.maharashtra.gov.in");
        sch07.setSourceUrl("https://krishi.maharashtra.gov.in");
        sch07.setHelpline("022-49150800");
        sch07.setImportantDates("Annual State Quota Window");
        sch07.setApplicationStatus("OPEN");
        sch07.setLastVerifiedDate("2026-10-25");
        sch07.setApplicableState("Maharashtra");
        sch07.setMinLandAcres(1.0);
        sch07.setMaxLandAcres(15.0); // CEILING: 15.0 acres
        sch07.setAllowedFarmerCategories("SC_ST");
        sch07.setAllowedCrops("ALL");
        sch07.setMaxAnnualIncome(150000.0);
        sch07.setRequiresLandOwnership(true);
        sch07.setActive(true);
        verifiedSchemes.add(sch07);

        // 8. Kharif Emergency Relief (SCH08) - CLOSED State Scheme
        GovernmentScheme sch08 = new GovernmentScheme();
        sch08.setId(8L);
        sch08.setSchemeId("SCH08");
        sch08.setSchemeName("Kharif Special Emergency Drought Relief Grant 2025");
        sch08.setCategory("Disaster Relief");
        sch08.setLevel("STATE");
        sch08.setMinistry("State Disaster Management Authority");
        sch08.setSubsidyAmount("₹6,800 / Hectare Input Subsidy");
        sch08.setDescription("Emergency financial relief compensation for rainfall deficit crop damage.");
        sch08.setBenefits("Ex-gratia DBT compensation of ₹6,800 per hectare for rainfed crops, directly deposited to eligible farmers bank accounts.");
        sch08.setEligibility("Farmers in declared drought-affected revenue circles who suffered > 33% crop loss during Kharif 2025.");
        sch08.setRequiredDocuments("1. Crop Damage Panchnama Certificate\n2. Land 7/12 record\n3. Aadhaar linked bank account");
        sch08.setApplicationProcess("Application cycle closed on 15 September 2025 following final district audit disbursements. No new applications are accepted for this fiscal cycle.");
        sch08.setOfficialPortalUrl("https://mahadbt.maharashtra.gov.in");
        sch08.setSourceUrl("https://maharashtra.gov.in/gr/drought_relief_archive_2025.pdf");
        sch08.setHelpline("1800-120-8040");
        sch08.setImportantDates("Closed on 15 Sep 2025");
        sch08.setApplicationStatus("CLOSED"); // CLOSED STATUS
        sch08.setLastVerifiedDate("2026-10-25");
        sch08.setApplicableState("Maharashtra");
        sch08.setMinLandAcres(null);
        sch08.setMaxLandAcres(null);
        sch08.setAllowedFarmerCategories("ALL");
        sch08.setAllowedCrops("ALL");
        sch08.setMaxAnnualIncome(null);
        sch08.setRequiresLandOwnership(false);
        sch08.setActive(true);
        verifiedSchemes.add(sch08);
    }

    public interface SchemeCallback {
        void onSuccess(List<GovernmentScheme> schemes);
        void onError(String message);
    }

    public void getSchemes(String category, String searchQuery, SchemeCallback callback) {
        List<GovernmentScheme> result = new ArrayList<>();
        String query = searchQuery != null ? searchQuery.trim().toLowerCase() : "";

        for (GovernmentScheme s : verifiedSchemes) {
            boolean matchesCat = category == null || category.equalsIgnoreCase("All") ||
                    (s.getCategory() != null && s.getCategory().toLowerCase().contains(category.toLowerCase()));
            boolean matchesQuery = query.isEmpty() ||
                    s.getSchemeName().toLowerCase().contains(query) ||
                    (s.getDescription() != null && s.getDescription().toLowerCase().contains(query)) ||
                    (s.getBenefits() != null && s.getBenefits().toLowerCase().contains(query));

            if (matchesCat && matchesQuery) {
                result.add(s);
            }
        }
        callback.onSuccess(result);
    }

    public void addScheme(GovernmentScheme scheme) {
        verifiedSchemes.add(0, scheme);
    }

    public boolean removeScheme(long id) {
        for (int i = 0; i < verifiedSchemes.size(); i++) {
            if (verifiedSchemes.get(i).getId() != null && verifiedSchemes.get(i).getId() == id) {
                verifiedSchemes.remove(i);
                return true;
            }
        }
        return false;
    }

    public List<GovernmentScheme> getAllSchemes() {
        return new ArrayList<>(verifiedSchemes);
    }
}
