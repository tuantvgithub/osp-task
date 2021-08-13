package com.example.zipper;

import com.example.zipper.service.ZipperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class ZipperApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ZipperApplication.class, args);
	}

	@Autowired
	private ZipperService service;

	@Override
	public void run(String... args) throws Exception {

		Map<String, String> params = new HashMap<String, String>();
		params.put("HOVATEN", "Tran Van Tuan");
		params.put("MSSV", "20184223");
		params.put("WEEK", "2");

		String filePath = "/home/tuan/Desktop/20184223_TranVanTuan_Tuan2.docx";

		this.service.updateDoc(filePath, params);
	}
}
