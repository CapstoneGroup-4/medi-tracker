package edu.capstone4.userserver.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  /**
   * Role:
   * Admin
   * Docker
   * User
   */
  @Schema(description = "Role of the user: ADMIN (not allow to set), DOCTOR, USER (default)", example = "USER")
  @NotBlank
  @Size(min = 1, max = 10)
  private String role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  /**
   * Gender:
   * 0 - Male
   * 1 - Female
   * 2 - Other
   */
  @Schema(description = "Gender of the user: 0 - Male, 1 - Female, 2 - Other", example = "0")
  @NotNull
  private Integer gender;  // gender changed to Integer to handle NotNull constraint

  @Size(min = 9, max = 20)
  @Pattern(regexp = "^[a-zA-Z0-9]{9}$", message = "SIN number must be 9 digits")
  private String sin;

  @Min(value = 0, message = "Age must be at least 0")
  @Max(value = 120, message = "Age must be less than or equal to 120")
  private Integer age;

  @Size(max = 15)
  @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
  private String phone;

  // Getters and Setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Integer getGender() {
    return gender;
  }

  public void setGender(Integer gender) {
    this.gender = gender;
  }

  public String getSin() {
    return sin;
  }

  public void setSin(String sin) {
    this.sin = sin;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
}
