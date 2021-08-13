package com.example.zipper.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipperService {

    public void updateDoc(String filePath, Map<String, String> params)
                                throws IOException, InvalidFormatException {
        XWPFDocument document = new XWPFDocument(OPCPackage.open(new FileInputStream(filePath)));
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        System.out.println("---------------- before update ----------------");
        for (XWPFParagraph paragraph : paragraphs) {
            System.out.println(paragraph.getText());
        }

        Pattern pattern = Pattern.compile("#(.*)#");
        for (XWPFParagraph paragraph : paragraphs) {
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                params.forEach((k, v) -> {
                    if (text.contains(k)) {
                        run.setText(text.replace("#" + k + "#", v), 0);
                    }
                });
            }
        }

        System.out.println("---------------- after update ----------------");
        for (XWPFParagraph paragraph : paragraphs) {
            System.out.println(paragraph.getText());
        }

        System.out.println("---------------- write result to out.docx and compress ----------------");
        FileOutputStream outputStream = new FileOutputStream("/home/tuan/Desktop/out.docx");
        document.write(outputStream);
        this.zipCompress("/home/tuan/Desktop/out.docx", "/home/tuan/Desktop/out.zip");
    }

    public void zipCompress(String src, String dest) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(dest);
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        File f = new File(src);
        ZipEntry zipEntry = new ZipEntry(f.getName());
        zipOutputStream.putNextEntry(zipEntry);

        FileInputStream inputStream = new FileInputStream(f);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes)) >= 0) {
            zipOutputStream.write(bytes, 0, length);
        }

        zipOutputStream.close();
        inputStream.close();
        outputStream.close();
    }

}
