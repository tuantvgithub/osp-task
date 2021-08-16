package co.osp.base.reportmodule;

import co.osp.base.reportmodule.dto.TestDto;
import co.osp.base.reportmodule.service.ReportService;
import co.osp.base.reportmodule.web.controller.ReportController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class ReportModuleApplication implements CommandLineRunner {

	@Autowired
	private ReportService reportService;

	@Autowired
	ConfigurableApplicationContext context;

	private final Logger logger = LoggerFactory.getLogger(ReportModuleApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReportModuleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		logger.info("============== creating report ==============");
//		List<TestDto> testDtoList = new ArrayList<TestDto>(Arrays.asList(
//				new TestDto(2L, 10D, 2019L, 20L),
//				new TestDto(3L, 20D, 2020L, 20L),
//				new TestDto(4L, 30D, 2021L, 20L),
//				new TestDto(5L, 100D, 2022L, 20L)
//		));
//		this.reportService.exportTest("testReport.jrxml", "test.html",
//											"html", testDtoList);
//		logger.info("============== finished ==============");
//		context.close();
	}
}
