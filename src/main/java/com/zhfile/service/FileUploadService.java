package com.zhfile.service;

import com.zhfile.dto.Response;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    Response saveFile(String modelCode, MultipartFile[] uploadFiles, String orgCode);

    ResponseEntity<Resource> loadFile(String url);

    void deleteFile(String id);

}
