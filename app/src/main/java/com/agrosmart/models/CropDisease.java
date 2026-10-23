package com.agrosmart.models;

import java.io.Serializable;

public class CropDisease implements Serializable {
    private String id;
    private String cropName;
    private String diseaseName;
    private String causalOrganism; // Fungal / Bacterial / Viral / Pest
    private String severity; // HIGH, MEDIUM, LOW
    private String symptoms;
    private String favorableConditions;
    private String preventiveMeasures;
    private String organicRemedy;
    private String chemicalControl;
    private String region; // "Western Maharashtra", "Vidarbha", "Marathwada", "Konkan", "All Maharashtra"

    public CropDisease() {}

    public CropDisease(String id, String cropName, String diseaseName, String causalOrganism,
                       String severity, String symptoms, String favorableConditions,
                       String preventiveMeasures, String organicRemedy, String chemicalControl) {
        this(id, cropName, diseaseName, causalOrganism, severity, symptoms, favorableConditions,
                preventiveMeasures, organicRemedy, chemicalControl, "All Maharashtra");
    }

    public CropDisease(String id, String cropName, String diseaseName, String causalOrganism,
                       String severity, String symptoms, String favorableConditions,
                       String preventiveMeasures, String organicRemedy, String chemicalControl,
                       String region) {
        this.id = id;
        this.cropName = cropName;
        this.diseaseName = diseaseName;
        this.causalOrganism = causalOrganism;
        this.severity = severity;
        this.symptoms = symptoms;
        this.favorableConditions = favorableConditions;
        this.preventiveMeasures = preventiveMeasures;
        this.organicRemedy = organicRemedy;
        this.chemicalControl = chemicalControl;
        this.region = region != null ? region : "All Maharashtra";
    }

    public String getId() { return id; }
    public String getCropName() { return cropName; }
    public String getDiseaseName() { return diseaseName; }
    public String getCausalOrganism() { return causalOrganism; }
    public String getSeverity() { return severity; }
    public String getSymptoms() { return symptoms; }
    public String getFavorableConditions() { return favorableConditions; }
    public String getPreventiveMeasures() { return preventiveMeasures; }
    public String getOrganicRemedy() { return organicRemedy; }
    public String getChemicalControl() { return chemicalControl; }
    public String getRegion() { return region != null ? region : "All Maharashtra"; }
    public void setRegion(String region) { this.region = region; }
}
