package co.osp.base.filemodule.mapping;

import co.osp.base.filemodule.dto.FileDto;
import co.osp.base.filemodule.dto.FileFormDto;
import co.osp.base.filemodule.entity.FileEntity;

public interface FileMapping {

    FileDto mappingEntityToDto(FileEntity entity);
    FileEntity mappingFormDtoToEntity(FileFormDto formDto);
}
