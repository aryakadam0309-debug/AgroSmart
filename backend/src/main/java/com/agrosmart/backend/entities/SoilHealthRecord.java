package com.agrosmart.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "soil_health_records")
public class SoilHealthRecord {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "farmer_phone", length = 20, nullable = false)
    private String farmerPhone;

    @Column(name = "sample_date", length = 30)
    private String sampleDate;

    @Column(name = "field_name", length = 100)
    private String fieldName;

    @Column(name = "soil_ph")
    private Double ph;

    @Column(name = "electrical_conductivity")
    private Double electricalConductivity;

    @Column(name = "organic_carbon")
    private Double organicCarbon;

    @Column(name = "nitrogen_kg_ha")
    private Double nitrogenKgHa;

    @Column(name = "phosphorus_kg_ha")
    private Double phosphorusKgHa;

    @Column(name = "potassium_kg_ha")
    private Double potassiumKgHa;

    @Column(name = "zinc_ppm")
    private Double zincPpm;

    @Column(name = "iron_ppm")
    private Double ironPpm;

    @Column(name = "fertility_rating", length = 50)
    private String fertilityRating;

    @Column(name = "advisory_notes", columnDefinition = "TEXT")
    private String advisoryNotes;

    public SoilHealthRecord() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getSampleDate() { return sampleDate; }
    public void setSampleDate(String sampleDate) { this.sampleDate = sampleDate; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }

    public Double getElectricalConductivity() { return electricalConductivity; }
    public void setElectricalConductivity(Double electricalConductivity) { this.electricalConductivity = electricalConductivity; }

    public Double getOrganicCarbon() { return organicCarbon; }
    public void setOrganicCarbon(Double organicCarbon) { this.organicCarbon = organicCarbon; }

    public Double getNitrogenKgHa() { return nitrogenKgHa; }
    public void setNitrogenKgHa(Double nitrogenKgHa) { this.nitrogenKgHa = nitrogenKgHa; }

    public Double getPhosphorusKgHa() { return phosphorusKgHa; }
    public void setPhosphorusKgHa(Double phosphorusKgHa) { this.phosphorusKgHa = phosphorusKgHa; }

    public Double getPotassiumKgHa() { return potassiumKgHa; }
    public void setPotassiumKgHa(Double potassiumKgHa) { this.potassiumKgHa = potassiumKgHa; }

    public Double getZincPpm() { return zincPpm; }
    public void setZincPpm(Double zincPpm) { this.zincPpm = zincPpm; }

    public Double getIronPpm() { return ironPpm; }
    public void setIronPpm(Double ironPpm) { this.ironPpm = ironPpm; }

    public String getFertilityRating() { return fertilityRating; }
    public void setFertilityRating(String fertilityRating) { this.fertilityRating = fertilityRating; }

    public String getAdvisoryNotes() { return advisoryNotes; }
    public void setAdvisoryNotes(String advisoryNotes) { this.advisoryNotes = advisoryNotes; }
}
