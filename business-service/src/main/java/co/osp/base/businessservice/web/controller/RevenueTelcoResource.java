package co.osp.base.businessservice.web.controller;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.File;
import co.osp.base.businessservice.service.CompanyService;
import co.osp.base.businessservice.service.FileService;
import co.osp.base.businessservice.service.RevenueTelcoService;
import co.osp.base.businessservice.dto.WarnFeeDocDTO;
import co.osp.base.businessservice.dto.RevenueTelcoReport;
import io.github.jhipster.web.util.ResponseUtil;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api")
public class RevenueTelcoResource {

    private final RevenueTelcoService revenueTelcoService;

    private final CompanyService companyService;

    private final FileService fileService;

    @Autowired
    public RevenueTelcoResource(
            RevenueTelcoService revenueTelcoService,
            CompanyService companyService,
            FileService fileService
    ) {
        this.revenueTelcoService = revenueTelcoService;
        this.companyService = companyService;
        this.fileService = fileService;
    }

    @GetMapping("/revenue/telcoServices/search")
    public ResponseEntity<List<RevenueTelcoReport>> search(
                    Principal principal,
                    @RequestParam(name = "company_id", required = false) List<Long> companyIds,
                    @RequestParam(name = "year") Long year,
                    @RequestParam(name = "quarter", required = false) Long quater,
                    @RequestParam(name = "type") String type)
    {
        List<RevenueTelcoReport> result = this.revenueTelcoService
                                                .search(companyIds, year, quater, type);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/revenue/telcoServices/summary/upload")
    public void upload(@RequestParam(name = "year") Long year,
                        @RequestParam(name = "quarter", required = false) Long quarter,
                        @RequestParam(name = "type") String type,
                        @Valid @RequestBody File file) throws Exception
    {
        try {
            this.revenueTelcoService.upload(year, quarter, file, type);
        } catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @DeleteMapping("/revenue/telcoServices/del")
    public void del(@RequestParam(name = "Companyid", required = false) List<Long> companyIds,
                    @RequestParam(name = "year") Long year,
                    @RequestParam(name = "quarter", required = false) Long quater,
                    @RequestParam(name = "type") String type
    ) {
        List<Company> companies = this.companyService.findByListIds(companyIds);
        this.revenueTelcoService.del(companies, year, quater, type);
    }

    @DeleteMapping("/revenue/telcoServices/delFile")
    public void delFile(@RequestParam(name = "file_id", required = false) Long file_id
    ) {
        this.revenueTelcoService.delFile(file_id);
    }

    @GetMapping("/revenue/telcoServices/file")
    public ResponseEntity<File> getFile(
            @RequestParam(name = "file_id", required = false) Long file_id,
            HttpServletResponse response
    ) throws IOException {
        Optional<File> file = this.fileService.findOneById(file_id);
        return ResponseUtil.wrapOrNotFound(file);
    }

    @GetMapping("/revenue/telcoServices/generate-excel")
    public void generateRevenue(
            @RequestParam(name = "company_id", required = false) List<Long> companyIds,
            @RequestParam(name = "quarter", required = false) Long quarter,
            @RequestParam(name = "year") Long year,
            @RequestParam(name = "type") String type,
            HttpServletResponse response
    ) throws IOException {
        XSSFWorkbook workbook =revenueTelcoService.generateReport(companyIds, year, quarter, type);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode("Báo cáo doanh thu.xlsx", "UTF-8"));

        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
        response.getOutputStream().flush();
    }

    @PostMapping("/revenue/telcoServices/feeKhoSoWarning")
    public byte[] generateKhoSoWarningFee(
            @RequestBody WarnFeeDocDTO warnFeeDocDTO,
            HttpServletResponse response
    ) throws Exception {
        String folder = revenueTelcoService.generateKhoSoWarnFeeDocs(warnFeeDocDTO);
        if (folder == null) throw new Exception("Hiện tại Kỳ báo cáo bạn chọn không có Doanh nghiệp nào chậm nộp phí!");
        java.io.File folderFile = new java.io.File(folder);

        String zipFileName = folderFile.getName().substring(0, folderFile.getName().indexOf("."));
        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition",
            "attachment;filename*=UTF-8''" + URLEncoder.encode(zipFileName + ".zip", "UTF-8"));

        // creating byteArray stream, make it bufforable and passing this buffor to ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        //packing files
        java.io.File[] files = folderFile.listFiles();
        for (java.io.File file : files) {
            if(!file.isFile()) continue;
            //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @PostMapping("/revenue/telcoServices/feeWarning")
    public byte[] generateWarningFee(@RequestBody WarnFeeDocDTO warnFeeDocDTO,
                                     HttpServletResponse response) throws Exception {
        String folder = revenueTelcoService.generateWarnFeeDocs(warnFeeDocDTO);
        if (folder == null) throw new Exception("Hiện tại Kỳ báo cáo bạn chọn không có Doanh nghiệp nào chậm nộp phí!");
        java.io.File folderFile = new java.io.File(folder);

        //System.out.println("=============================== " + folderFile.getName());
        String zipFileName = folderFile.getName().substring(0, folderFile.getName().indexOf("."));
        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition",
            "attachment;filename*=UTF-8''" + URLEncoder.encode(zipFileName + ".zip", "UTF-8"));

        // creating byteArray stream, make it bufforable and passing this buffor to ZipOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        //packing files
        java.io.File[] files = folderFile.listFiles();

        for (java.io.File file : files) {
            if(!file.isFile()) continue;
            //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
