package com.agrosmart.models;

import java.io.Serializable;

public class SoilHealthRecord implements Serializable {
    private String id;
    private String farmerPhone;
    private String sampleDate;
    private String fieldName;
    private Double ph;
    private Double electricalConductivity;
    private Double organicCarbon;
    private Double nitrogenKgHa;
    private Double phosphorusKgHa;
    private Double potassiumKgHa;
    private Double zincPpm;
    private Double ironPpm;
    private String fertilityRating;
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
