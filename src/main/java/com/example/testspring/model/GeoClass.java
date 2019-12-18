package com.example.testspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * GeoClass entity.
 * Used by {@link com.example.testspring.controller.GeoClassController}
 */

@Entity
@Table(name = "geoclasses")
public class GeoClass extends AuditModel {
    @Id
    @GeneratedValue(generator = "geoclass_generator")
    @SequenceGenerator(
            name = "geoclass_generator",
            sequenceName = "geoclass_sequence",
            initialValue = 1000
    )
    private Long id;

    @Column(columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Section section;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public GeoClass setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GeoClass setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public GeoClass setCode(String code) {
        this.code = code;
        return this;
    }

    public Section getSection() {
        return section;
    }

    public GeoClass setSection(Section section) {
        this.section = section;
        return this;
    }
}