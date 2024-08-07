package com.demoDocumentMerge;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) {
        try {
            // define paths to the template and json file
            String templateFilePath = "src/main/resources/myTemplate.docx";
            String jsonDataFilePath = "src/main/resources/myData.json";

            InputStream template = Files.newInputStream(new File(templateFilePath).toPath());

            String content = new String(Files.readAllBytes(Paths.get(jsonDataFilePath)));
            JSONObject jsonDataForMerge = new JSONObject(content);

            // call adobe merger operation
            MergeToPDFUtil.mergeToPDF(jsonDataForMerge, template);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
