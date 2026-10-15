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
        verifiedSchemes.add(new GovernmentScheme(
                1L,
                "PM Kisan Samman Nidhi (PM-KISAN)",
                "Financial",
                "Central sector scheme providing income support to all landholding farmer families across the country.",
                "Direct financial assistance of ₹6,000 per year transferred in three equal instalments of ₹2,000 directly into the bank accounts of farmers.",
                "Small and marginal landholder farmer families with cultivable landholding in their names. Institutional landholders and taxpayer farmers are excluded.",
                "1. Aadhaar Card\n2. Landholding ownership documents (7/12, 8-A extract)\n3. Bank Account details (Passbook copy linked with Aadhaar)",
                "Apply online via PM-KISAN portal (pmkisan.gov.in) or visit nearest Common Service Centre (CSC) with land and bank records.",
                "https://pmkisan.gov.in",
                "Ongoing scheme with disbursements in April-July, August-November, and December-March.",
                true
        ));

        verifiedSchemes.add(new GovernmentScheme(
                2L,
                "Pradhan Mantri Fasal Bima Yojana (PMFBY)",
                "Insurance",
                "Comprehensive crop insurance scheme providing financial support to farmers suffering crop loss or damage arising out of natural calamities.",
                "Full sum insured coverage with nominal premium rates: 2% for Kharif food and oilseed crops, 1.5% for Rabi crops, and 5% for annual commercial/horticultural crops.",
                "All farmers including sharecroppers and tenant farmers growing notified crops in notified areas during the season.",
                "1. Sowing certificate or declaration\n2. Land revenue records (7/12 extract)\n3. Bank account passbook\n4. Aadhaar Card",
                "Register through the National Crop Insurance Portal (pmfby.gov.in), through lending banks, or designated agricultural service centres.",
                "https://pmfby.gov.in",
                "Kharif cut-off date: 31st July; Rabi cut-off date: 31st December.",
                true
        ));

        verifiedSchemes.add(new GovernmentScheme(
                3L,
                "PM-KUSUM Solar Agriculture Pump Scheme",
                "Subsidy",
                "Provides energy security to farmers by installing standalone off-grid solar agricultural water pumps and solarizing grid-connected pumps.",
                "Up to 60% government subsidy (30% Central + 30% State), with 30% bank loan available, meaning the farmer invests only 10% of total cost.",
                "Individual farmers, water user associations, farmer producer organisations (FPOs), and primary agricultural credit societies owning agricultural land.",
                "1. Identity proof (Aadhaar/PAN)\n2. Land ownership documents (7/12 & 8A)\n3. Water source certificate (well/borewell existence)\n4. Bank details",
                "Applications are invited via State Nodal Renewable Energy Agencies (such as MEDA in Maharashtra) on their dedicated online portals.",
                "https://pmkusum.mnre.gov.in",
                "Applications open periodically per state allocation quotas.",
                true
        ));

        verifiedSchemes.add(new GovernmentScheme(
                4L,
                "Sub-Mission on Agricultural Mechanization (SMAM)",
                "Machinery",
                "Promotes farm mechanization among small and marginal farmers by subsidizing purchase of modern agricultural equipment.",
                "Subsidy ranging from 40% to 50% on tractors, power tillers, rotavators, seed drills, and multi-crop threshers depending on farmer category.",
                "Small, marginal, SC/ST, and women farmers given top priority. Land ownership records must reflect active farming.",
                "1. Aadhaar Card\n2. Land record documents\n3. Caste certificate (if applicable)\n4. Quotation from authorized dealer\n5. Bank details",
                "Apply through DBT Agriculture Portal (agrimachinery.nic.in) or State Mahadbt portal.",
                "https://agrimachinery.nic.in",
                "Annual application window according to fiscal budget allocation.",
                true
        ));
    }

    public interface SchemeCallback {
        void onSuccess(List<GovernmentScheme> schemes);
        void onError(String message);
    }

    public void getSchemes(String category, String searchQuery, SchemeCallback callback) {
        List<GovernmentScheme> result = new ArrayList<>();
        String query = searchQuery != null ? searchQuery.trim().toLowerCase() : "";

        for (GovernmentScheme s : verifiedSchemes) {
            boolean matchesCat = category == null || category.equalsIgnoreCase("All") || category.equalsIgnoreCase(s.getCategory());
            boolean matchesQuery = query.isEmpty() ||
                    s.getSchemeName().toLowerCase().contains(query) ||
                    s.getDescription().toLowerCase().contains(query) ||
                    s.getBenefits().toLowerCase().contains(query);

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
            if (verifiedSchemes.get(i).getId() == id) {
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
