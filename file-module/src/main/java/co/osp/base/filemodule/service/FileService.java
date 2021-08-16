package co.osp.base.filemodule.service;

import co.osp.base.filemodule.dto.FileDto;
import co.osp.base.filemodule.dto.FileFormDto;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileService {

    void deleteFileById(Long id);
    FileDto uploadFile(FileFormDto uploadFormDto) throws IOException;
    FileDto getFileById(Long id) throws IOException;

}
