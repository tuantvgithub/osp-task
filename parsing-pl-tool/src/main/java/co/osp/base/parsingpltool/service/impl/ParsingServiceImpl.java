package co.osp.base.parsingpltool.service.impl;

import co.osp.base.parsingpltool.service.ParsingService;
import co.osp.base.parsingpltool.utils.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ParsingServiceImpl implements ParsingService {

    @Value("${app.plFilePath}")
    private String plFilePath;

    @Override
    public String parsingPL1(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet sheet = workbook.getSheet("PL 1");

        XSSFCell quarterAndYear = sheet.getRow(7).getCell(1);
        Pattern pattern1 = Pattern.compile("Quý(.*)/Năm(.*)");
        Matcher matcher1 = pattern1.matcher(quarterAndYear.getStringCellValue());
        if (matcher1.find()) {
            System.out.println("quarter: " + matcher1.group(1).trim());
            System.out.println("year: " + matcher1.group(2).trim());
        }

        XSSFCell companyName = sheet.getRow(8).getCell(1);
        Pattern pattern2 = Pattern.compile(":(.*)");
        Matcher matcher2 = pattern2.matcher(companyName.getStringCellValue());
        if (matcher2.find()) {
            System.out.println("company name: " + matcher2.group(1).trim());
        }

        long flag = 0;
        int i = 11;
        for (i = 11; i < 44; i++) {
            XSSFRow row = sheet.getRow(i);
            Cell stt = row.getCell(0);
            Cell telecoServiceName = row.getCell(1);

            switch (stt.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    double d = stt.getNumericCellValue();
                    DecimalFormat format = new DecimalFormat("##.#");
                    flag = StringUtils.countCharacter(format.format(d), '.');
                    this.tab(flag);
                    break;
                case Cell.CELL_TYPE_STRING:
                    String s = stt.getStringCellValue();
                    long tmp = StringUtils.countCharacter(s, '.');
                    if (tmp != 0) flag = tmp;
                    this.tab(flag+1);
                    break;
            }
            System.out.print(telecoServiceName.getStringCellValue() + ": ");
            for (Cell cell : row) {
                this.printCellValue(cell);
                this.tab(1);
            }
            System.out.println();
        }

        XSSFRow lastRow = sheet.getRow(i);
        Cell sum = lastRow.getCell(0);
        System.out.print(sum.getStringCellValue() + ":");
        for (int j = 2; j < 7; j++) {
            this.tab(1);
            this.printCellValue(lastRow.getCell(j));
        }
        System.out.println();

        return "PL1 parsing completed";
    }

    @Override
    public String parsingPL2(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet sheet = workbook.getSheet("PL 2");

        XSSFCell quarterAndYear = sheet.getRow(7).getCell(1);
        Pattern pattern1 = Pattern.compile("Quý(.*)/Năm(.*)");
        Matcher matcher1 = pattern1.matcher(quarterAndYear.getStringCellValue());
        if (matcher1.find()) {
            System.out.println("quarter: " + matcher1.group(1).trim());
            System.out.println("year: " + matcher1.group(2).trim());
        }

        XSSFCell companyName = sheet.getRow(8).getCell(1);
        Pattern pattern2 = Pattern.compile(":(.*)");
        Matcher matcher2 = pattern2.matcher(companyName.getStringCellValue());
        if (matcher2.find()) {
            System.out.println("company name: " + matcher2.group(1).trim());
        }

        long flag = 0;
        int i = 11;
        for (i = 11; i < 29; i++) {
            XSSFRow row = sheet.getRow(i);
            Cell stt = row.getCell(0);
            Cell telecoServiceName = row.getCell(1);

            switch (stt.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    double d = stt.getNumericCellValue();
                    DecimalFormat format = new DecimalFormat("##.#");
                    flag = StringUtils.countCharacter(format.format(d), '.');
                    this.tab(flag);
                    break;
                case Cell.CELL_TYPE_STRING:
                    String s = stt.getStringCellValue();
                    long tmp = StringUtils.countCharacter(s, '.');
                    if (tmp != 0) flag = tmp;
                    this.tab(flag+1);
                    break;
            }
            System.out.print(telecoServiceName.getStringCellValue() + ": ");
            for (Cell cell : row) {
                this.printCellValue(cell);
                this.tab(1);
            }
            System.out.println();
        }

        XSSFRow lastRow = sheet.getRow(i);
        Cell sum = lastRow.getCell(0);
        System.out.print(sum.getStringCellValue() + ":");
        for (int j = 2; j < 6; j++) {
            this.tab(1);
            this.printCellValue(lastRow.getCell(j));
        }
        System.out.println();

        return "PL2 parsing completed";
    }

    @Override
    public String parsingPL3(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet sheet = workbook.getSheet("PL 3");

        XSSFCell quarterAndYear = sheet.getRow(7).getCell(1);
        Pattern pattern1 = Pattern.compile("Quý(.*)/Năm(.*)");
        Matcher matcher1 = pattern1.matcher(quarterAndYear.getStringCellValue());
        if (matcher1.find()) {
            System.out.println("quarter: " + matcher1.group(1).trim());
            System.out.println("year: " + matcher1.group(2).trim());
        }

        XSSFCell companyName = sheet.getRow(8).getCell(1);
        Pattern pattern2 = Pattern.compile(":(.*)");
        Matcher matcher2 = pattern2.matcher(companyName.getStringCellValue());
        if (matcher2.find()) {
            System.out.println("company name: " + matcher2.group(1).trim());
        }

        long flag = 0;
        int i = 11;
        for (i = 11; i < 29; i++) {
            XSSFRow row = sheet.getRow(i);
            Cell stt = row.getCell(0);
            Cell telecoServiceName = row.getCell(1);

            switch (stt.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    double d = stt.getNumericCellValue();
                    DecimalFormat format = new DecimalFormat("##.#");
                    flag = StringUtils.countCharacter(format.format(d), '.');
                    this.tab(flag);
                    break;
                case Cell.CELL_TYPE_STRING:
                    String s = stt.getStringCellValue();
                    long tmp = StringUtils.countCharacter(s, '.');
                    if (tmp != 0) flag = tmp;
                    this.tab(flag+1);
                    break;
            }
            System.out.print(telecoServiceName.getStringCellValue() + ": ");
            for (Cell cell : row) {
                this.printCellValue(cell);
                this.tab(1);
            }
            System.out.println();
        }

        XSSFRow lastRow = sheet.getRow(i);
        Cell sum = lastRow.getCell(0);
        System.out.print(sum.getStringCellValue() + ":");
        for (Cell cell : lastRow) {
            this.tab(1);
            this.printCellValue(cell);
        }
        System.out.println();

        return "PL3 parsing completed";
    }

    @Override
    public String parsingPL4(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet sheet = workbook.getSheet("PL 4");

        XSSFCell quarterAndYear = sheet.getRow(7).getCell(1);
        Pattern pattern1 = Pattern.compile("Quý(.*)/Năm(.*)");
        Matcher matcher1 = pattern1.matcher(quarterAndYear.getStringCellValue());
        if (matcher1.find()) {
            System.out.println("quarter: " + matcher1.group(1).trim());
            System.out.println("year: " + matcher1.group(2).trim());
        }

        XSSFCell companyName = sheet.getRow(8).getCell(1);
        Pattern pattern2 = Pattern.compile(":(.*)");
        Matcher matcher2 = pattern2.matcher(companyName.getStringCellValue());
        if (matcher2.find()) {
            System.out.println("company name: " + matcher2.group(1).trim());
        }

        XSSFRow row = sheet.getRow(13);
        for (Cell cell : row) {
            this.printCellValue(cell);
            this.tab(1);
        }
        System.out.println();

        return "PL4 parsing completed";
    }

    @Override
    public String parsingPL5(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet sheet = workbook.getSheet("PL 5");

        XSSFCell quarterAndYear = sheet.getRow(7).getCell(1);
        Pattern pattern1 = Pattern.compile("Quý(.*)/Năm(.*)");
        Matcher matcher1 = pattern1.matcher(quarterAndYear.getStringCellValue());
        if (matcher1.find()) {
            System.out.println("quarter: " + matcher1.group(1).trim());
            System.out.println("year: " + matcher1.group(2).trim());
        }

        XSSFCell companyName = sheet.getRow(8).getCell(1);
        Pattern pattern2 = Pattern.compile(":(.*)");
        Matcher matcher2 = pattern2.matcher(companyName.getStringCellValue());
        if (matcher2.find()) {
            System.out.println("company name: " + matcher2.group(1).trim());
        }

        XSSFRow row = sheet.getRow(13);
        for (Cell cell : row) {
            this.printCellValue(cell);
            this.tab(1);
        }
        System.out.println();

        return "PL5 parsing completed";
    }

    private void tab(long i) {
        if (i == 0) return;
        System.out.print("\t");
        tab(i-1);
    }

    private void printCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                System.out.print(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                System.out.print(cell.getStringCellValue());
                break;
            default:
                System.out.print("NK");
        }
    }
}
