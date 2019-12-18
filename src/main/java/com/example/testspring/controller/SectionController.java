package com.example.testspring.controller;

import com.example.testspring.exception.ResourceNotFoundException;
import com.example.testspring.model.Section;
import com.example.testspring.repository.GeoClassRepository;
import com.example.testspring.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * REST controller for Section type requests.
 * Handles: <ol>
 *     <li>GET /sections/by-code?code=%CODE% - getting all sections by presented GeoCode</li>
 *     <li>GET /sections</li>
 *     <li>POST /sections</li>
 *     <li>PUT /sections/{sectionId}</li>
 *     <li>DELETE /sections/{sectionId}</li>
 *     <ol/>
 */

@RestController
public class SectionController {

    @Autowired
    private GeoClassRepository geoClassRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping("/sections/by-code")
    public Page<Section> getQuestions(@RequestParam String code, Pageable pageable) {
        return sectionRepository.findAllByGeoCode(geoClassRepository, code, pageable);
    }

    @GetMapping("/sections")
    public Page<Section> getSections(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }


    @PostMapping("/sections")
    public Section createSection(@Valid @RequestBody Section section) {
        return sectionRepository.save(section);
    }

    @PutMapping("/sections/{sectionId}")
    public Section updateSection(@PathVariable Long sectionId,
                                 @Valid @RequestBody Section sectionRequest) {
        return sectionRepository.findById(sectionId)
                .map(section -> {
                    section.setName(sectionRequest.getName());
                    return sectionRepository.save(section);
                }).orElseThrow(() -> new ResourceNotFoundException("Section not found with id " + sectionId));
    }


    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<?> deleteSection(@PathVariable Long sectionId) {
        return sectionRepository.findById(sectionId)
                .map(section -> {
                    sectionRepository.delete(section);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Section not found with id " + sectionId));
    }
}