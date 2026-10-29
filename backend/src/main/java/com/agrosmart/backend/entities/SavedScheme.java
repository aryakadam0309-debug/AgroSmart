package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Saved Scheme Bookmark Entity (PS 5.3).
 * Persists bookmarked government schemes for authenticated farmers.
 */
@Entity
@Table(name = "saved_schemes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"farmerPhone", "schemeId"})
})
public class SavedScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String farmerPhone;

    @Column(nullable = false, length = 30)
    private String schemeId;

    private LocalDateTime savedAt = LocalDateTime.now();

    public SavedScheme() {}

    public SavedScheme(String farmerPhone, String schemeId) {
        this.farmerPhone = farmerPhone;
        this.schemeId = schemeId;
        this.savedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getSchemeId() { return schemeId; }
    public void setSchemeId(String schemeId) { this.schemeId = schemeId; }

    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
}
