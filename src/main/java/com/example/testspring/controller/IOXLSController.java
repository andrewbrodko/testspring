package com.example.testspring.controller;

import java.io.*;
import java.util.*;

import com.example.testspring.exception.ResourceNotFoundException;
import com.example.testspring.model.IOXLS;
import com.example.testspring.model.GeoClass;
import com.example.testspring.model.Section;
import com.example.testspring.model.JobStatus;
import com.example.testspring.repository.IOXLSRepository;
import com.example.testspring.repository.GeoClassRepository;
import com.example.testspring.repository.SectionRepository;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for XLS I/O.
 * Implements private methods <code>readXLS</code> and <code>writeXLS</code> for
 * parsing XLS and exporting database to temporary XLS file.
 * Handles: <ol>
 *     <li>POST /import %FILE%</li>
 *     <li>GET /import/{fileId}</li>
 *     <li>GET /export</li>
 *     <li>GET /export/{fileId}</li>
 *     <li>GET /export/{fileId}/file</li>
 *     <ol/>
 */

@RestController
public class IOXLSController
{
    @Autowired
    private GeoClassRepository geoClassRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private IOXLSRepository IOXLSRepository;

    @RequestMapping(value = "/import",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Long> importXLS(@RequestParam("file") MultipartFile file) {
        IOXLS ioxls = new IOXLS().setJobStatus(JobStatus.IN_PROGRESS);

        new Thread(() -> {
            try {
                this.readXLS(file.getInputStream());
                IOXLSRepository.save(ioxls.setJobStatus(JobStatus.DONE));
            } catch (IOException exe) {
                exe.printStackTrace();
                IOXLSRepository.save(ioxls.setJobStatus(JobStatus.ERROR));
            }
        }).start();

        Map<String, Long> resp = new HashMap<>();
        resp.put("id", IOXLSRepository.save(ioxls).getId());
        return resp;
    }

    @GetMapping("/import/{fileId}")
    public Object getImportStatus(@PathVariable Long fileId) {
        return IOXLSRepository.findById(fileId)
                .map(ioxls -> {
                    JobStatus jobStatus = ioxls.getJobStatus();
                    return Collections.singletonMap("status", jobStatus);
                }).orElseThrow(() -> new ResourceNotFoundException("FileIO not found with id " + fileId));
    }

    @GetMapping("/export")
    public Map<String, Long> exportXLS() {
        Map<String, Long> resp = new HashMap<>();
        IOXLS ioxls = new IOXLS().setJobStatus(JobStatus.IN_PROGRESS);

        new Thread(() -> {
            try {
                IOXLSRepository.save(ioxls.setPath(this.writeXLS()).setJobStatus(JobStatus.DONE));
            } catch (IOException exe) {
                exe.printStackTrace();
                IOXLSRepository.save(ioxls.setJobStatus(JobStatus.ERROR));
            }
        }).start();

        resp.put("id", IOXLSRepository.save(ioxls).getId());
        return resp;
    }

    @GetMapping("/export/{fileId}")
    public Object getExportStatus(@PathVariable Long fileId) {
        return IOXLSRepository.findById(fileId)
                .map(ioxls -> {
                    JobStatus jobStatus = ioxls.getJobStatus();
                    return Collections.singletonMap("status", jobStatus);
                }).orElseThrow(() -> new ResourceNotFoundException("FileIO not found with id " + fileId));
    }

    @GetMapping(value = "/export/{fileId}/file",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody ResponseEntity<InputStreamResource>
    getExportFile(@PathVariable Long fileId) {
        return IOXLSRepository.findById(fileId)
                .map(ioxls -> {
                    String path = ioxls.getPath();
                    System.out.println(path);
                    String filename = path.split("\\\\")[path.split("\\\\").length - 1];
                    File file = new File(path);

                    try {
                        return ResponseEntity.ok()
                                .header("Content-Disposition", "attachment; filename=" + filename)
                                .contentLength(file.length())
                                .lastModified(file.lastModified())
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(new InputStreamResource(new FileInputStream(file)));
                    } catch (FileNotFoundException exe) {
                        exe.printStackTrace();
                        return new ResponseEntity<InputStreamResource>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }).orElseThrow(() -> new ResourceNotFoundException("FileIO not found with id " + fileId));
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
                geoClass.setSection(section);

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
}