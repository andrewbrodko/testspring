package com.example.testspring.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.HashMap;

import com.example.testspring.model.FileUpload;
import com.example.testspring.repository.FileUploadRepository;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;


@RestController
public class FileUploadController
{
    @Autowired
    private FileUploadRepository fileUploadRepository;

    String status = "none";

    @RequestMapping(value = "/import",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public Map<String, Long> fileUpload(@RequestParam("file") MultipartFile file) throws IOException{
        String pathname = "C:/Users/SD_CODEPC/Documents/upload/" + file.getOriginalFilename();
        File convertFile = new File(pathname);

        FileUpload fileUpload = new FileUpload();
        fileUpload.setPath(pathname);
        fileUpload.setStatus("IN_PROGRESS");

        try {
            if (!convertFile.createNewFile()) {
                throw new IOException("cannot create a file");
            }
            FileOutputStream fout = new FileOutputStream(convertFile);
            fout.write(file.getBytes());
            fout.close();
            fileUpload.setStatus("DONE");
        } catch (IOException exe) {
            fileUpload.setStatus("ERROR");
        }
        readFromExcel(pathname);

        Map<String, Long> resp = new HashMap<>();
        resp.put("id", fileUploadRepository.save(fileUpload).getId());

        return resp;
    }

    @GetMapping("/import/{fileId}")
    public Map<String, String> getFileUpload(@PathVariable Long fileId) throws NoSuchElementException {
        Map<String, String> resp = new HashMap<>();

        try {
            resp.put("status", fileUploadRepository.findById(fileId).get().getStatus());
        } catch (NoSuchElementException exe) {
            resp.put("status", "NOT EXISTS");
        }
        return resp;
    }

    public static void readFromExcel(String file) throws IOException{
        HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(file));
        HSSFSheet myExcelSheet = myExcelBook.getSheet("Sheet1");
        HSSFRow row = myExcelSheet.getRow(0);

        if(row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING){
            String name = row.getCell(0).getStringCellValue();
            System.out.println("name : " + name);
        }

        myExcelBook = null;
        return;
    }

//    @GetMapping("/import/{fileId}")
//    public String getAnswersByQuestionId(@PathVariable Long fileId) {
//        return "id: " + fileId + "; status: " + this.status;
//    }
}