package co.osp.base.backupfile.service;

import co.osp.base.backupfile.entity.File;
import co.osp.base.backupfile.repository.FileRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Value("${app.uploadFileDir}")
    private String uploadFileDir;

    public void backup() {
        List<Long> ids = this.fileRepository.getAllIds();

//        ids.parallelStream().forEach(id -> {
//            File file = this.fileRepository.getById(id);
//            file.setFilePath(this.uploadFileDir + file.getName());
//            try {
//                java.io.File f = new java.io.File(file.getFilePath());
//                if (file.getContent() == null) return;
//                FileUtils.writeByteArrayToFile(f, file.getContent());
//                this.fileRepository.save(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        for (Long id : ids) {
            File file = this.fileRepository.getById(id);
            file.setFilePath(this.uploadFileDir + file.getName());
            try {
                java.io.File f = new java.io.File(file.getFilePath());
                if (file.getContent() == null) continue;

                FileUtils.writeByteArrayToFile(f, file.getContent());
                this.fileRepository.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
