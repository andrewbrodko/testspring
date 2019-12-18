package com.example.testspring.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Section entity.
 * Used by {@link com.example.testspring.controller.SectionController}
 */

@Entity
@Table(name = "sections")
public class Section extends AuditModel {
    @Id
    @GeneratedValue(generator = "section_generator")
    @SequenceGenerator(
            name = "section_generator",
            sequenceName = "section_sequence",
            initialValue = 1000
    )
    private Long id;

    @NotBlank
    private String name;

    @Transient
    private List<GeoClass> geoClasses;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Section setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Section setName(String title) {
        this.name = title;
        return this;
    }

    public List<GeoClass> getGeoClasses() {
        return geoClasses;
    }

    public Section setGeoClasses(List<GeoClass> geoClasses) {
        this.geoClasses = geoClasses;
        return this;
    }
}