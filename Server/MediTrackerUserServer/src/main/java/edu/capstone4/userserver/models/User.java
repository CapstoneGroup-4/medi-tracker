package edu.capstone4.userserver.models;

import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    /**
     * Gender:
     * 0 - Male
     * 1 - Female
     * 2 - Other
     */
    @Schema(description = "Gender of the user: 0 - Male, 1 - Female, 2 - Other", example = "0")
    @NotNull
    private int gender;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // 可选字段：手机号、年龄、SIN 号码
    @Size(max = 15)
    private String phone;

    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 120, message = "Age must be less than or equal to 120")
    private Integer age;

    @Size(max = 9)
    private String sin;

    private boolean enabled;

    public User() {
    }

    public User(String username, String email, String password, int gender) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @NotNull
    public int getGender() {
        return gender;
    }

    public void setGender(@NotNull int gender) {
        this.gender = gender;
    }

    public @Size(max = 15) String getPhone() {
        return phone;
    }

    public void setPhone(@Size(max = 15) String phone) {
        this.phone = phone;
    }

    public @Min(value = 0, message = "Age must be at least 0") @Max(value = 120, message = "Age must be less than or equal to 120") Integer getAge() {
        return age;
    }

    public void setAge(@Min(value = 0, message = "Age must be at least 0") @Max(value = 120, message = "Age must be less than or equal to 120") Integer age) {
        this.age = age;
    }

    public @Size(max = 9) String getSin() {
        return sin;
    }

    public void setSin(@Size(max = 9) String sin) {
        this.sin = sin;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
