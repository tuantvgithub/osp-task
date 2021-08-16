package co.osp.base.backupfile;

import co.osp.base.backupfile.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackupFileApplication implements ApplicationRunner {

	@Autowired
	private FileService fileService;

	private final Logger logger = LoggerFactory.getLogger(BackupFileApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BackupFileApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		logger.info("================ Backing up ================");
//		this.fileService.backup();
//		logger.info("================ Finished - press Ctrl + C to shutdown");
//	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("================ Backing up ================");
		this.fileService.backup();
		logger.info("================ Finished - press Ctrl + C to shutdown");
	}
}
