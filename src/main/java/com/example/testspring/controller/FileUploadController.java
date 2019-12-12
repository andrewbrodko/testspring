package com.example.testspring.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.example.testspring.model.Answer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController
{
    String status = "none";

    @RequestMapping(value = "/import",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException
    {
        this.status = "IN_PROGRESS";
        File convertFile = new File("C:/Users/SD_CODEPC/Documents/upload/" + file.getOriginalFilename());
        boolean sucss = convertFile.createNewFile();

        try (FileOutputStream fout = new FileOutputStream(convertFile))
        {
            fout.write(file.getBytes());
        }
        catch (Exception exe)
        {
            this.status = "ERROR";
            exe.printStackTrace();
        }
        this.status = "DONE";
        return "createNewFile(): " + sucss;
    }


    @GetMapping("/import/{fileId}")
    public String getAnswersByQuestionId(@PathVariable Long fileId) {
        return "id: " + fileId + "; status: " + this.status;
    }
}