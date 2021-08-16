package co.osp.base.businessservice.service.impl;


import co.osp.base.businessservice.entity.File;
import co.osp.base.businessservice.repository.FileRepository;
import co.osp.base.businessservice.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service Implementation for managing {@link File}.
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileRepository fileRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<File> findOneById(Long id) {
        log.debug("Request to get File : {}", id);
        return fileRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.deleteById(id);
    }

    @Override
    public File getOne(Long id) {
        Optional<File> file = this.fileRepository.findById(id);

        return file.orElse(null);
    }

    @Override
    public Set<File> findByListIds(List<Long> ids) {
        return this.fileRepository.findByListIds(ids);
    }

    @Override
    public File save(File file) {
        return this.fileRepository.save(file);
    }
}
