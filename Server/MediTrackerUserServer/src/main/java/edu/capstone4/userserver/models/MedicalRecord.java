package edu.capstone4.userserver.models;

import javax.persistence.*;

@Entity
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;
    private String diagnosis;
    private String doctor;
    private Boolean isDeleted = false; // 用于逻辑删除标识

    // Getters and Setters
}
