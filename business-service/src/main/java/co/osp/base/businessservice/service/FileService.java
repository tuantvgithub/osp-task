package co.osp.base.businessservice.service;

import co.osp.base.businessservice.entity.File;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FileService {

    Optional<File> findOneById(Long id);

    void deleteById(Long id);

    File getOne(Long id);

    Set<File> findByListIds(List<Long> ids);

    File save(File file);
}
