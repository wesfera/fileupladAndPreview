package com.zhfile.controller;

import com.zhfile.dto.Response;
import com.zhfile.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@CrossOrigin
@RestController
@Api(tags = "文件上传服务",description = "文件上传服务:上传，下载接口")
public class RestUploadController {

    private FileUploadService fileUploadService;

    @Autowired
    private RestUploadController(FileUploadService fileUploadService){
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/file")
    @ApiOperation(value = "下载文件")
    public ResponseEntity<Resource> findFileUrl(@ApiParam("文件路径") String url)  {
        return fileUploadService.loadFile(url);
    }

    @GetMapping("/deleteFile")
    @ApiOperation(value = "删除文件")
    public Response deleteFile(@ApiParam("fileId") String fileId)  {
        fileUploadService.deleteFile(fileId);
        return Response.OK;
    }

    @PostMapping("/multiUpload")
    @ApiOperation(value = "批量的保存文件")
    public Response uploadFileMulti(
            @RequestParam("modelCode") @ApiParam("菜单编码") String modelCode,
            @RequestParam(value = "file") @ApiParam("文件信息") MultipartFile[] uploadFiles,
            @RequestParam(value = "orgCode",required = false) @ApiParam("组织机构") String orgCode) {
        return fileUploadService.saveFile(modelCode,uploadFiles,orgCode);
    }

}
