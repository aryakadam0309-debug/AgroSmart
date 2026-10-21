package com.agrosmart.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // "ROLE_FARMER", "ROLE_ADMIN"

    private String village;
    private String district;
    private String state;
    private Double landAreaAcres;

    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}

    public User(String fullName, String phone, String email, String password, String role, String village, String district, String state, Double landAreaAcres) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.village = village;
        this.district = district;
        this.state = state;
        this.landAreaAcres = landAreaAcres;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getVillage() { return village; }
    public String getDistrict() { return district; }
    public String getState() { return state; }
    public Double getLandAreaAcres() { return landAreaAcres; }
}
