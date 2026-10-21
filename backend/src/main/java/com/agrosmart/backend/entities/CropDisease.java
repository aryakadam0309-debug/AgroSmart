package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "crop_diseases")
public class CropDisease {
    @Id
    private String id;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private String diseaseName;

    private String causalOrganism;
    private String severity;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String favorableConditions;

    @Column(columnDefinition = "TEXT")
    private String preventiveMeasures;

    @Column(columnDefinition = "TEXT")
    private String organicRemedy;

    @Column(columnDefinition = "TEXT")
    private String chemicalControl;

    public CropDisease() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }

    public String getCausalOrganism() { return causalOrganism; }
    public void setCausalOrganism(String causalOrganism) { this.causalOrganism = causalOrganism; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getFavorableConditions() { return favorableConditions; }
    public void setFavorableConditions(String favorableConditions) { this.favorableConditions = favorableConditions; }

    public String getPreventiveMeasures() { return preventiveMeasures; }
    public void setPreventiveMeasures(String preventiveMeasures) { this.preventiveMeasures = preventiveMeasures; }

    public String getOrganicRemedy() { return organicRemedy; }
    public void setOrganicRemedy(String organicRemedy) { this.organicRemedy = organicRemedy; }

    public String getChemicalControl() { return chemicalControl; }
    public void setChemicalControl(String chemicalControl) { this.chemicalControl = chemicalControl; }
}
