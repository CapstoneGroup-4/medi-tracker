package edu.capstone4.userserver.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "personalId"),
                @UniqueConstraint(columnNames = "user_id")
        })
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @NotBlank
    @Size(max = 20)
    private String professionalId;

    @NotBlank
    @Size(max = 20)
    private String personalId;

    @NotBlank
    @Size(max = 20)
    private String license;

    @NotBlank
    private String licenseAuthority;

    @NotBlank
    private String jobTitle;

    @NotBlank
    private String specialization;

    @NotBlank
    private String clinicName;

    private String membership;

    @Column(name = "is_activated")
    private boolean isActivated = false;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharePermission> sharedRecords = new ArrayList<>();

    // Default constructor
    public Doctor() {
    }

    public Doctor(String professionalId, String personalId, String license, String licenseAuthority,
                  String jobTitle, String specialization, String clinicName) {
        this.professionalId = professionalId;
        this.personalId = personalId;
        this.license = license;
        this.licenseAuthority = licenseAuthority;
        this.jobTitle = jobTitle;
        this.specialization = specialization;
        this.clinicName = clinicName;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseAuthority() {
        return licenseAuthority;
    }

    public void setLicenseAuthority(String licenseAuthority) {
        this.licenseAuthority = licenseAuthority;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public List<SharePermission> getSharedRecords() {
        return sharedRecords;
    }

    public void setSharedRecords(List<SharePermission> sharedRecords) {
        this.sharedRecords = sharedRecords;
    }
}
