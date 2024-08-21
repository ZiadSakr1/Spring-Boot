package com.org.vitaproject.service.impl;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class BlobStorageService {

    private final BlobServiceClient blobServiceClient;

    public BlobStorageService(@Value("${azure.storage.connection-string}") String connectionString) {
        this.blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    }

    public void uploadFile(String containerName, String blobName, InputStream data, long length) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
        }
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        blobClient.upload(data, length, true);

    }

    public InputStream downloadFile(String containerName, String blobName) throws IOException {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.download(outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public void deleteFile(String containerName, String blobName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.delete();
    }
}
