package com.example.testspring.repository;

import com.example.testspring.model.GeoClass;
import com.example.testspring.model.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SectionRepository class for handling access to PostgreSQL.
 * Implements methods for <code>'geoclasses'</code> data injection
 * into <code>{@link com.example.testspring.model.Section}</code> entity.
 */

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    default Page<Section> findAllByGeoCode(GeoClassRepository geoClassRepository, String code, Pageable pageable) {
        Page<Section> sections = this.findAll(pageable);
        List<Section> sectionList = new ArrayList<>();

        sections.forEach(section -> {
            List<GeoClass> geoClasses = geoClassRepository.findBySectionId(section.getId());
            if (geoClasses.stream().anyMatch(gc -> gc.getCode().equals(code))) {
                section.setGeoClasses(geoClasses);
                sectionList.add(section);
            }
        });

        return new PageImpl<>(sectionList, pageable, sectionList.size());
    }

    default List<Section> findAllWithGeoClasses(GeoClassRepository geoClassRepository) {
        List<Section> sections = this.findAll();
        List<Section> sectionList = new ArrayList<>();

        sections.forEach(section -> {
            section.setGeoClasses(geoClassRepository.findBySectionId(section.getId()));
            sectionList.add(section);
        });

        return sectionList;
    }
}