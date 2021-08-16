package co.osp.base.filemodule.service.impl;

import co.osp.base.filemodule.dto.FileDto;
import co.osp.base.filemodule.dto.FileFormDto;
import co.osp.base.filemodule.entity.FileEntity;
import co.osp.base.filemodule.mapping.FileMapping;
import co.osp.base.filemodule.repository.FileRepository;
import co.osp.base.filemodule.service.FileService;
import co.osp.base.filemodule.utils.DateUtils;
import co.osp.base.filemodule.web.exception.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class FileServiceImpl implements FileService {

    @Value("${app.uploadFileDir}")
    private String uploadFileDir;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileRepository fileRepository;

    private final FileMapping fileMapping;

    @Autowired
    public FileServiceImpl(
            FileRepository fileRepository,
            FileMapping fileMapping
    ) {
        this.fileRepository = fileRepository;
        this.fileMapping = fileMapping;
    }

    @Override
    public void deleteFileById(Long id) {
        LOGGER.debug("Rest request to delete File by {} " ,id);
        Optional<FileEntity> fileOptional = this.fileRepository.findById(id);

        if (!fileOptional.isPresent()) {
            throw new FileNotFoundException("File by id " + id + " was not found.");
        } else {
            FileEntity file = fileOptional.get();
            File tmp = new File(file.getFilePath());

            if (!tmp.delete()) LOGGER.debug("Failed to delete file {}", file.getFilePath());
            this.fileRepository.deleteById(id);
        }
    }

    @Override
    public FileDto uploadFile(FileFormDto uploadFormDto) throws IOException {
        LOGGER.debug("Rest request to upload File : {}", uploadFormDto);

        String dirPath = this.uploadFileDir + File.separator
                            + DateUtils.now("yyyyMMdd") + File.separator
                            + DateUtils.now("HHmmss");
        File dir = new File(dirPath);
        if(!dir.mkdirs()) LOGGER.debug("Failed to create folder {}", dirPath);

        String savedFilePath = dirPath + File.separator + uploadFormDto.getName();
        File file = new File(savedFilePath);
        file.createNewFile();
        if (uploadFormDto.getContent() != null) {
            FileUtils.writeByteArrayToFile(file, uploadFormDto.getContent());
        }
        uploadFormDto.setContent(null);
        uploadFormDto.setFilePath(savedFilePath);
        FileEntity entity = this.fileMapping.mappingFormDtoToEntity(uploadFormDto);

        return this.fileMapping.mappingEntityToDto(this.fileRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public FileDto getFileById(Long id) throws IOException {
        LOGGER.debug("Rest request to get File by id {}", id);

        Optional<FileEntity> entityOptional = this.fileRepository.findById(id);

        if (!entityOptional.isPresent()) {
            throw new FileNotFoundException("File by id " + id + " was not found");
        }

        FileDto fileDto = this.fileMapping.mappingEntityToDto(entityOptional.get());
        File file = new File(fileDto.getFilePath());
        if (!file.exists()) {
            throw new FileNotFoundException("File by id " + id +
                    " was not found in path " + fileDto.getFilePath());
        }

        byte[] content = FileUtils.readFileToByteArray(file);
        fileDto.setContent(content);

        return fileDto;
    }

}
