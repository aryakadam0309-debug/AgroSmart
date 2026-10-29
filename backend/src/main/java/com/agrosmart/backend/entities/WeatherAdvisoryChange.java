package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_advisory_changes")
public class WeatherAdvisoryChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String farmerPhone;
    private String locationName;
    private String forecastDate;
    private String dayName;

    private String activity; // "Sowing", "Pesticide Spraying", "Harvesting"
    private String previousStatus; // "Suitable", "Caution", "Avoid/Delay"
    private String updatedStatus;  // "Suitable", "Caution", "Avoid/Delay"

    @Column(columnDefinition = "TEXT")
    private String previousForecastSummary;

    @Column(columnDefinition = "TEXT")
    private String updatedForecastSummary;

    @Column(columnDefinition = "TEXT")
    private String changeReason;

    private boolean notificationSent = false;
    private String notificationDeliveryStatus; // "DELIVERED", "SAVED_OFFLINE", "SKIPPED_DUPLICATE"

    @Column(length = 128)
    private String dedupKey;

    private LocalDateTime previousRetrievedAt;
    private LocalDateTime updatedRetrievedAt = LocalDateTime.now();
    private LocalDateTime detectedAt = LocalDateTime.now();

    public WeatherAdvisoryChange() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFarmerPhone() { return farmerPhone; }
    public void setFarmerPhone(String farmerPhone) { this.farmerPhone = farmerPhone; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getForecastDate() { return forecastDate; }
    public void setForecastDate(String forecastDate) { this.forecastDate = forecastDate; }

    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getUpdatedStatus() { return updatedStatus; }
    public void setUpdatedStatus(String updatedStatus) { this.updatedStatus = updatedStatus; }

    public String getPreviousForecastSummary() { return previousForecastSummary; }
    public void setPreviousForecastSummary(String previousForecastSummary) { this.previousForecastSummary = previousForecastSummary; }

    public String getUpdatedForecastSummary() { return updatedForecastSummary; }
    public void setUpdatedForecastSummary(String updatedForecastSummary) { this.updatedForecastSummary = updatedForecastSummary; }

    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }

    public boolean isNotificationSent() { return notificationSent; }
    public void setNotificationSent(boolean notificationSent) { this.notificationSent = notificationSent; }

    public String getNotificationDeliveryStatus() { return notificationDeliveryStatus; }
    public void setNotificationDeliveryStatus(String notificationDeliveryStatus) { this.notificationDeliveryStatus = notificationDeliveryStatus; }

    public String getDedupKey() { return dedupKey; }
    public void setDedupKey(String dedupKey) { this.dedupKey = dedupKey; }

    public LocalDateTime getPreviousRetrievedAt() { return previousRetrievedAt; }
    public void setPreviousRetrievedAt(LocalDateTime previousRetrievedAt) { this.previousRetrievedAt = previousRetrievedAt; }

    public LocalDateTime getUpdatedRetrievedAt() { return updatedRetrievedAt; }
    public void setUpdatedRetrievedAt(LocalDateTime updatedRetrievedAt) { this.updatedRetrievedAt = updatedRetrievedAt; }

    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
}
