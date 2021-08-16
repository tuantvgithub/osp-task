package co.osp.base.reportmodule.web.controller;

import co.osp.base.reportmodule.dto.TestDto;
import co.osp.base.reportmodule.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports/")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

//    @PostMapping("/export/test")
//    public String exportTest(
//            @RequestParam String reportTemplateFileName,
//            @RequestParam String destFileName,
//            @RequestParam String format,
//            @RequestBody List<TestDto> testDtoList
//    ) throws JRException, FileNotFoundException {
//        return this.reportService.exportTest(reportTemplateFileName, destFileName, format, testDtoList);
//    }

    @GetMapping("/export/test")
    public byte[] exportTest() throws JRException, IOException {
        		List<TestDto> testDtoList = new ArrayList<TestDto>(Arrays.asList(
				new TestDto(1L, 10D, 2018L, 1L),
				new TestDto(2L, 20D, 2019L, 1L),
				new TestDto(3L, 20D, 2020L, 1L),
				new TestDto(4L, 30D, 2021L, 1L),
				new TestDto(5L, 100D, 2022L, 1L)
		));
        File file = new File(this.reportService.exportTest(
                "testReport.jrxml", "test.html",
                "html", testDtoList
        ));

        return FileUtils.readFileToByteArray(file);
    }

    @GetMapping("/export/test.html_files/{img}")
    public @ResponseBody byte[] getImage(@PathVariable String img) throws IOException {
        File imgFile = new File("./test.html_files/" + img);
        InputStream in = new FileInputStream(imgFile);
        return IOUtils.toByteArray(in);
    }

}
