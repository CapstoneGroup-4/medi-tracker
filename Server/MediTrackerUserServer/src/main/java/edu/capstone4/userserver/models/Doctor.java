package edu.capstone4.userserver.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "doctors",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "personalId"),
                @UniqueConstraint(columnNames = "user_id"),
                @UniqueConstraint(columnNames = "personalId")
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


    public Doctor(String professionalId, String personalId, String license, String licenseAuthority,
                  String jobTitle, String specialization, String clinicName) {
        super();
        this.professionalId = professionalId;
        this.personalId = personalId;
        this.license = license;
        this.licenseAuthority = licenseAuthority;
        this.jobTitle = jobTitle;
        this.specialization = specialization;
        this.clinicName = clinicName;
    }

    public Doctor() {
    }

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

    public @NotBlank @Size(max = 20) String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(@NotBlank @Size(max = 20) String professionalId) {
        this.professionalId = professionalId;
    }

    public @NotBlank @Size(max = 20) String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(@NotBlank @Size(max = 20) String personalId) {
        this.personalId = personalId;
    }

    public @NotBlank @Size(max = 20) String getLicense() {
        return license;
    }

    public void setLicense(@NotBlank @Size(max = 20) String license) {
        this.license = license;
    }

    public @NotBlank String getLicenseAuthority() {
        return licenseAuthority;
    }

    public void setLicenseAuthority(@NotBlank String licenseAuthority) {
        this.licenseAuthority = licenseAuthority;
    }

    public @NotBlank String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(@NotBlank String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public @NotBlank String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(@NotBlank String specialization) {
        this.specialization = specialization;
    }

    public @NotBlank String getClinicName() {
        return clinicName;
    }

    public void setClinicName(@NotBlank String clinicName) {
        this.clinicName = clinicName;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }
}
