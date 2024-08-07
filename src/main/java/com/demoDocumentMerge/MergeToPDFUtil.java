package com.demoDocumentMerge;

import com.adobe.pdfservices.operation.PDFServices;
import com.adobe.pdfservices.operation.PDFServicesMediaType;
import com.adobe.pdfservices.operation.PDFServicesResponse;
import com.adobe.pdfservices.operation.auth.Credentials;
import com.adobe.pdfservices.operation.auth.ServicePrincipalCredentials;
import com.adobe.pdfservices.operation.exception.SDKException;
import com.adobe.pdfservices.operation.exception.ServiceApiException;
import com.adobe.pdfservices.operation.exception.ServiceUsageException;
import com.adobe.pdfservices.operation.io.Asset;
import com.adobe.pdfservices.operation.io.StreamAsset;
import com.adobe.pdfservices.operation.pdfjobs.jobs.DocumentMergeJob;
import com.adobe.pdfservices.operation.pdfjobs.params.documentmerge.DocumentMergeParams;
import com.adobe.pdfservices.operation.pdfjobs.params.documentmerge.OutputFormat;
import com.adobe.pdfservices.operation.pdfjobs.result.DocumentMergeResult;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MergeToPDFUtil {

    static String CLIENT_ID = "40d4340358a4402fb9ec1f46c15ab153";
    static String CLIENT_SECRET = "p8e-auYV3thJ4LR-aKV_trxL8V4xovEKh2Li";

    public static void mergeToPDF(JSONObject jsonObject, InputStream template) {
        try {
            // make necessary credential for merge process
            Credentials credentials = new ServicePrincipalCredentials(CLIENT_ID, CLIENT_SECRET);

            PDFServices pdfServices = new PDFServices(credentials);

            Asset asset = pdfServices.upload(template, PDFServicesMediaType.DOCX.getMediaType());

            DocumentMergeParams documentMergeParams = DocumentMergeParams.documentMergeParamsBuilder()
                    .withJsonDataForMerge(jsonObject)
                    .withOutputFormat(OutputFormat.PDF)
                    .build();


            DocumentMergeJob documentMergeJob = new DocumentMergeJob(asset, documentMergeParams);
            String location = pdfServices.submit(documentMergeJob);

            PDFServicesResponse<DocumentMergeResult> pdfServicesResponse = pdfServices.getJobResult(location, DocumentMergeResult.class);

            // get content from the resulting asset(s)
            Asset resultAsset = pdfServicesResponse.getResult().getAsset();
            StreamAsset streamAsset = pdfServices.getContent(resultAsset);

            // copy & save merged content to a new File in resources
            OutputStream outputStream = Files.newOutputStream(new File(createOutputFilePath()).toPath());
            IOUtils.copy(streamAsset.getInputStream(), outputStream);
            outputStream.close();

        } catch (ServiceApiException | IOException | SDKException | ServiceUsageException e) {

        }
    }

    // Generates a string containing a directory structure and file name for the output file
    public static String createOutputFilePath() throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String timeStamp = dateTimeFormatter.format(now);
        Files.createDirectories(Paths.get("MergeDocumentToPDF"));
        return ("MergeDocumentToPDF/merge" + timeStamp + ".pdf");
    }
}
