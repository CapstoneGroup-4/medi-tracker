package edu.capstone4.userserver.payload.request;

import java.util.Set;

import jakarta.validation.constraints.*;

public class SignupRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private Set<String> role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  @NotNull
  private Integer gender;  // gender changed to Integer to handle NotNull constraint

  @Pattern(regexp = "^[a-zA-Z0-9]{9}$", message = "SIN number must be 9 digits")
  private String sin;

  @Min(value = 0, message = "Age must be at least 0")
  @Max(value = 120, message = "Age must be less than or equal to 120")
  private Integer age;

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

  public Set<String> getRole() {
    return this.role;
  }

  public void setRole(Set<String> role) {
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
