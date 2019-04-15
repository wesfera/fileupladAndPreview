package com.zhfile.configuration;

import com.aspose.cells.Workbook;
import com.aspose.words.Document;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @ClassName PdfUtil
 * @Description 需要引入 aspose-words-15.8.0-jdk16.jar / aspose-cells-8.5.2.jar
 **/
public class PdfUtil {
    /**
     * @param wordPath 需要被转换的word全路径带文件名
     * @param pdfPath 转换之后pdf的全路径带文件名
     */
    public static void doc2pdf(String wordPath, String pdfPath) {
        try {
            long old = System.currentTimeMillis();
            File file = new File(pdfPath); //新建一个pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(wordPath); //Address是将要被转化的word文档
            doc.save(os, com.aspose.words.SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            long now = System.currentTimeMillis();
            os.close();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); //转化用时
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param excelPath 需要被转换的excel全路径带文件名
     * @param pdfPath 转换之后pdf的全路径带文件名
     */
    public static void excel2pdf(String excelPath, String pdfPath) {
        try {
            long old = System.currentTimeMillis();
            Workbook wb = new Workbook(excelPath);// 原始excel路径
            FileOutputStream fileOS = new FileOutputStream(new File(pdfPath));
            wb.save(fileOS, com.aspose.cells.SaveFormat.PDF);
            fileOS.close();
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); //转化用时
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
