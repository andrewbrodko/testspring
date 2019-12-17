package com.example.testspring.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.example.testspring.model.IOXLS;
import com.example.testspring.model.GeoClass;
import com.example.testspring.model.Section;
import com.example.testspring.repository.IOXLSRepository;
import com.example.testspring.repository.GeoClassRepository;
import com.example.testspring.repository.SectionRepository;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;


@RestController
public class IOXLSController
{
    @Autowired
    private GeoClassRepository geoClassRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private IOXLSRepository IOXLSRepository;

    String status = "none";

    @RequestMapping(value = "/import",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public Map<String, Long> importXLS(@RequestParam("file") MultipartFile file) throws IOException{
        IOXLS ioxls = new IOXLS();
        ioxls.setStatus("IN_PROGRESS");

        new Thread(() -> {
            try {
                this.readXLS(file.getInputStream());
                IOXLSRepository.save(ioxls.setStatus("DONE"));
            } catch (IOException exe) {
                exe.printStackTrace();
                IOXLSRepository.save(ioxls.setStatus("ERROR"));
            }
        }).start();

        Map<String, Long> resp = new HashMap<>();
        resp.put("id", IOXLSRepository.save(ioxls).getId());

        return resp;
    }

    @GetMapping("/import/{fileId}")
    public Map<String, String> getImportStatus(@PathVariable Long fileId) throws NoSuchElementException {
        return getIOStatus(fileId);
    }

    @GetMapping("/export")
    public Map<String, Long> exportXLS(Pageable pageable) throws NoSuchElementException {
        Map<String, Long> resp = new HashMap<>();

        IOXLS ioxls = new IOXLS();
        ioxls.setStatus("IN_PROGRESS");

        new Thread(() -> {
            try {
                ioxls
                    .setPath(this.writeXLS())
                    .setStatus("DONE");
                IOXLSRepository.save(ioxls);
            } catch (IOException exe) {
                exe.printStackTrace();
                IOXLSRepository.save(ioxls.setStatus("ERROR"));
            }
        }).start();

        resp.put("id", IOXLSRepository.save(ioxls).getId());
        return resp;
    }

    @GetMapping("/export/{fileId}")
    public Map<String, String> getExportStatus(@PathVariable Long fileId) {
        return getIOStatus(fileId);
    }

    @GetMapping(value = "/export/{fileId}/file",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)

    public @ResponseBody ResponseEntity<ByteArrayResource> getExportFile(@PathVariable Long fileId) throws IOException {

        if (IOXLSRepository.findById(fileId).isPresent()) {
            if (IOXLSRepository.findById(fileId).get().getStatus().equals("DONE")) {
                Path path = Paths.get(new File(IOXLSRepository.findById(fileId).get().getPath()).getAbsolutePath());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                return new ResponseEntity<>(resource, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    private Map<String, String> getIOStatus(Long fileId) {
        String status = "NOT EXISTS";

        if (IOXLSRepository.findById(fileId).isPresent()) {
            status = IOXLSRepository.findById(fileId).get().getStatus();
        }

        return Collections.singletonMap("status", status);
    }

    private void readXLS(InputStream fileInputStream) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
        HSSFSheet sheet = workbook.getSheetAt(0);

        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow row = sheet.getRow(i);

            Section section = new Section();
            section.setName(row.getCell(0).getStringCellValue());
            sectionRepository.save(section);

            for (int j = 1; j < row.getLastCellNum(); j += 2) {
                GeoClass geoClass = new GeoClass();
                geoClass.setName(row.getCell(j).getStringCellValue()); // geoclass
                geoClass.setCode(row.getCell(j + 1).getStringCellValue()); // geocode
                geoClass.setSection(section); // geocode

                geoClassRepository.save(geoClass);
            }
        }
    }

    private String writeXLS() throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sheet 1");
        List<Section> sectionList = sectionRepository.findAllWithGeoClasses(geoClassRepository);

        for (int i = 0; i < sectionList.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            row.createCell(0).setCellValue(new HSSFRichTextString(sectionList.get(i).getName()));


            List<GeoClass> geoClassList = sectionList.get(i).getGeoClasses();
            for (int j = 0; j < geoClassList.size(); j++) {
                row.createCell(j * 2 + 1).setCellValue(geoClassList.get(j).getName());
                row.createCell(j * 2 + 2).setCellValue(geoClassList.get(j).getCode());
            }
        }

        File temp = File.createTempFile("ioxls", ".xls");

        FileOutputStream fout = new FileOutputStream(temp);
        workbook.write(fout);
        fout.close();
        return temp.getAbsolutePath();
    }

//    @GetMapping("/import/{fileId}")
//    public String getAnswersByQuestionId(@PathVariable Long fileId) {
//        return "id: " + fileId + "; status: " + this.status;
//    }
}