package edu.capstone4.userserver.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class RoleResponse {
    private String role;

    @Schema(description = "If the roles is USER, will set id to user_id; If the roles is DOCTOR, will set to doctor_id", example = "1")
    private Long id;

    public RoleResponse(String role, Long id) {
        this.role = role;
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
