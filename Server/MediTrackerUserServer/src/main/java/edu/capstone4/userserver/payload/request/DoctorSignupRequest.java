package edu.capstone4.userserver.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DoctorSignupRequest {

    @NotBlank
    private Long userId;

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

    public @NotBlank Long getUserId() {
        return userId;
    }

    public void setUserId(@NotBlank Long userId) {
        this.userId = userId;
    }

    public @NotBlank @Size(max = 20) String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(@NotBlank @Size(max = 20) String professionalId) {
        this.professionalId = professionalId;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
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
}
