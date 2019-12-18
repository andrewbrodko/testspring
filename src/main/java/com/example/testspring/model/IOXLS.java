package com.example.testspring.model;

import javax.persistence.*;

/**
 * IOXLS entity.
 * Used by {@link com.example.testspring.controller.IOXLSController}
 */

@Entity
@Table(name = "fileio")
public class IOXLS extends AuditModel {
    @Id
    @GeneratedValue(generator = "fileupload_generator")
    @SequenceGenerator(
            name = "fileupload_generator",
            sequenceName = "fileupload_sequence",
            initialValue = 1000
    )
    private Long id;

    @Column(columnDefinition = "text")
    private String path;

    @Column(columnDefinition = "text")
    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public IOXLS setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPath() {
        return path;
    }

    public IOXLS setPath(String path) {
        this.path = path;
        return this;
    }

    @Enumerated(EnumType.STRING)
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public IOXLS setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }
}