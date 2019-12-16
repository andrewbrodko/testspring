package com.example.testspring.controller;

import com.example.testspring.exception.ResourceNotFoundException;
import com.example.testspring.model.GeoClass;
import com.example.testspring.repository.GeoClassRepository;
import com.example.testspring.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
public class GeoClassController {

    @Autowired
    private GeoClassRepository geoClassRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping("/sections/{sectionId}/geoclasses")
    public List<GeoClass> getAnswersByQuestionId(@PathVariable Long sectionId) {
        return geoClassRepository.findBySectionId(sectionId);
    }

    @PostMapping("/sections/{sectionId}/geoclasses")
    public GeoClass addAnswer(@PathVariable Long sectionId,
                              @Valid @RequestBody GeoClass geoClass) {
        return sectionRepository.findById(sectionId)
                .map(section -> {
                    geoClass.setSection(section);
                    return geoClassRepository.save(geoClass);
                }).orElseThrow(() -> new ResourceNotFoundException("Section not found with id " + sectionId));
    }

    @PutMapping("/sections/{sectionId}/geoclasses/{geoclassId}")
    public GeoClass updateAnswer(@PathVariable Long sectionId,
                                 @PathVariable Long geoclassId,
                                 @Valid @RequestBody GeoClass geoClassRequest) {
        if(!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section not found with id " + sectionId);
        }

        return geoClassRepository.findById(geoclassId)
                .map(geoClass -> {
                    geoClass.setName(geoClassRequest.getName());
                    return geoClassRepository.save(geoClass);
                }).orElseThrow(() -> new ResourceNotFoundException("GeoClass not found with id " + geoclassId));
    }

    @DeleteMapping("/sections/{sectionId}/geoclasses/{geoclassId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long sectionId,
                                          @PathVariable Long geoclassId) {
        if(!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section not found with id " + sectionId);
        }

        return geoClassRepository.findById(geoclassId)
                .map(geoClass -> {
                    geoClassRepository.delete(geoClass);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("GeoClass not found with id " + geoclassId));

    }
}