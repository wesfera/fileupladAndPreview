package com.zhfile.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.zhfile.configuration.FileConfiguration;
import com.zhfile.configuration.PdfUtil;
import com.zhfile.dao.FileInfoDao;
import com.zhfile.dto.DataResponse;
import com.zhfile.dto.FileInfoDto;
import com.zhfile.dto.Response;
import com.zhfile.exception.StorageFileNotFoundException;
import com.zhfile.model.FileInfo;
import com.zhfile.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private Path rootLocation;
    private FileInfoDao fileInfoDao;

    @Autowired
    public FileUploadServiceImpl(FileConfiguration fileConfiguration,FileInfoDao fileInfoDao) {
        this.rootLocation = Paths.get(fileConfiguration.getFilePath());
        this.fileInfoDao = fileInfoDao;
    }

    @Override
    public Response saveFile(String modelCode, MultipartFile[] uploadFiles,String orgCode) {
        List<FileInfoDto> infoDto;
        String uploadedFileName = Arrays.stream(uploadFiles).map(MultipartFile::getOriginalFilename)
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
        if (StringUtils.isEmpty(uploadedFileName)) {
            return Response.fail("请选择文件信息");
        }
        try {
            infoDto = saveUploadedFiles(modelCode,Arrays.asList(uploadFiles),orgCode);
        } catch (IOException e) {
            return Response.fail(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
        return DataResponse.ok(infoDto);
    }

    @Override
    public ResponseEntity<Resource> loadFile(String url) {
        try {
            Path path = rootLocation.resolve(url);
            Resource resource = new UrlResource(path.toUri());
            String fileName = url.substring(url.lastIndexOf("/")+1);

            String[] strArr = fileName.split("\\.");
            String fileNamePrefix = strArr[0];
            String fileNameSuffix = strArr[1];
            String finalFileName = null;
            try {
                finalFileName = URLEncoder.encode(fileNamePrefix,"UTF-8")+"."+fileNameSuffix;
            }catch (UnsupportedEncodingException e){
                log.info("文件名字转换异常");
                e.printStackTrace();
            }

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + finalFileName + "\"").body(resource);
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + url);
            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + url, e);
        }
    }

    //将上传的文件保存到服务器中
    private List<FileInfoDto> saveUploadedFiles(String modelCode,List<MultipartFile> files,String orgCode) throws IOException {
        List<FileInfoDto> infoDtos = new ArrayList<>();
        FileInfoDto infoDto;
        Path savedFilePath;
        for (MultipartFile file : files) {
            if (file.isEmpty()) { continue; }
            infoDto = new FileInfoDto();
            byte[] bytes = file.getBytes();
            //current month
            String monthDate = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue();
            Path path = rootLocation.resolve(modelCode+"/"+monthDate+"/");
            //if this directory is not exist,create;
            if(Files.notExists(path)){
                Files.createDirectories(path);
            }
            //获取存储之后的文件路径
            savedFilePath = path.resolve(file.getOriginalFilename());
            //保存文件
            Files.write(savedFilePath, bytes);
            if(file.getOriginalFilename().contains(".txt")){
                List<String> lines = Files.readAllLines(savedFilePath, Charset.forName("GBK"));
                Files.write(savedFilePath, lines,Charset.forName("UTF-8"));
            }

            String fileName = file.getOriginalFilename().substring(0,file.getOriginalFilename().indexOf("."));
            String pdfFilePath = path.resolve(fileName+".pdf").toString();

            if (file.getOriginalFilename().contains(".doc") || file.getOriginalFilename().contains(".docx")){
                PdfUtil.doc2pdf(savedFilePath.toString(),pdfFilePath);
            }else if (file.getOriginalFilename().contains("xls") || file.getOriginalFilename().contains("xlsx")){
                PdfUtil.excel2pdf(savedFilePath.toString(),pdfFilePath);
            }if(file.getOriginalFilename().contains(".txt")){
                txt2pdf(pdfFilePath,savedFilePath);
            }

            infoDto.setId(saveFileInfo(modelCode,monthDate,file.getOriginalFilename(),orgCode));
            infoDto.setName(file.getOriginalFilename());
            infoDtos.add(infoDto);
        }
        return infoDtos;
    }

    //保存文件信息
    private String saveFileInfo(String modelCode,String monthDate,String fileName,String orgCode){
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUrl(modelCode+"/"+monthDate+"/"+fileName);
        fileInfo.setModelCode(modelCode);
        fileInfo.setName(fileName);
        if(!StringUtils.isEmpty(orgCode)){
            fileInfo.setOrgCode(orgCode);
        }
        fileInfoDao.save(fileInfo);
        return fileInfo.getId();
    }

    /**
     * 删除实体文件
     */
    @Override
    public void deleteFile(String id) {
        FileInfo fileInfo = fileInfoDao.getOne(id);
        Path path = rootLocation.resolve(fileInfo.getUrl());
        try{
            Resource resource = new UrlResource(path.toUri());
            File file = resource.getFile();
            if(file.delete()){
                log.info(file.getName() + " is deleted!");
            }else{
                log.info("Delete operation is failed.");
            }
        }catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + fileInfo.getUrl(), e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileInfoDao.deleteById(id);
    }

    private void txt2pdf(String pdfFilePath,Path savedFilePath)throws IOException{
        //转pdf
        Document document = new Document();
        try {

            String prefixFont;BaseFont baseFont1;
            String os = System.getProperties().getProperty("os.name");
            if (os.startsWith("win") || os.startsWith("Win")) {
                prefixFont = "C:\\Windows\\Fonts" + File.separator;
                baseFont1 = BaseFont.createFont(prefixFont + "msyh.ttc,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else {
                prefixFont = "/usr/share/fonts/winFonts" + File.separator;
                baseFont1 = BaseFont.createFont(prefixFont + "MSYH.TTC,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }
            Font yahei12 = new Font(baseFont1, 12f); //微软雅黑 小四

            File txtPdf = new File(pdfFilePath); //新建一个pdf文档
            FileOutputStream txtPdfStream = new FileOutputStream(txtPdf);
            PdfWriter.getInstance(document, txtPdfStream);
            document.open();

            List<String> lines = Files.readAllLines(savedFilePath);
            for(String str:lines){
                document.add(new Paragraph(str,yahei12));
            }
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
