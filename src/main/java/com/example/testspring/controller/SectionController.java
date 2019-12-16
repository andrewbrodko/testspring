package com.example.testspring.controller;

import com.example.testspring.exception.ResourceNotFoundException;
import com.example.testspring.model.GeoClass;
import com.example.testspring.model.Section;
import com.example.testspring.repository.GeoClassRepository;
import com.example.testspring.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SectionController {

    @Autowired
    private GeoClassRepository geoClassRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping("/sections/by-code")
    public Page<Section> getQuestions(@RequestParam String code, Pageable pageable) throws UnsupportedOperationException {
        Page<Section> sections = sectionRepository.findAll(pageable);
        List<Section> sectionList = sections.getContent();

        sections.forEach(section -> {
            List<GeoClass> geoClasses = geoClassRepository.findBySectionId(section.getId());
            if (geoClasses.stream().anyMatch(gc -> gc.getCode().equals(code))) {
                section.setGeoClasses(geoClasses);
            } else {
                sectionList.remove(section);
            }
        });

        return new PageImpl<>(sectionList, pageable, sectionList.size());
    }

    @GetMapping("/sections")
    public Page<Section> getQuestions(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }


    @PostMapping("/sections")
    public Section createQuestion(@Valid @RequestBody Section section) {
        return sectionRepository.save(section);
    }

    @PutMapping("/sections/{sectionId}")
    public Section updateQuestion(@PathVariable Long sectionId,
                                  @Valid @RequestBody Section sectionRequest) {
        return sectionRepository.findById(sectionId)
                .map(section -> {
                    section.setName(sectionRequest.getName());
                    return sectionRepository.save(section);
                }).orElseThrow(() -> new ResourceNotFoundException("Section not found with id " + sectionId));
    }


    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long sectionId) {
        return sectionRepository.findById(sectionId)
                .map(section -> {
                    sectionRepository.delete(section);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Section not found with id " + sectionId));
    }
}