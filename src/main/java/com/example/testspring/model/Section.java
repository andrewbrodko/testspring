package com.example.testspring.model;

import com.example.testspring.model.GeoClass;
import org.hibernate.service.spi.InjectService;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

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
    @Size(min = 3, max = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Transient
    private List<GeoClass> geoClasses;

    // Getters and Setters (Omitted for brevity)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public List<GeoClass> getGeoClasses() {
        return geoClasses;
    }

    public void setGeoClasses(List<GeoClass> geoClasses) {
        this.geoClasses = geoClasses;
    }
}