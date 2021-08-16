package co.osp.base.filemodule.mapping.impl;

import co.osp.base.filemodule.dto.FileDto;
import co.osp.base.filemodule.dto.FileFormDto;
import co.osp.base.filemodule.entity.FileEntity;
import co.osp.base.filemodule.mapping.FileMapping;
import org.springframework.stereotype.Component;

@Component
public class FileMappingImpl implements FileMapping {

    @Override
    public FileDto mappingEntityToDto(FileEntity entity) {
        FileDto dto = new FileDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContent(entity.getContent());
        dto.setFilePath(entity.getFilePath());
        dto.setExtendsion(entity.getExtendsion());
        dto.setUrl(entity.getUrl());
        dto.setContentType(entity.getContentType());

        return dto;
    }

    @Override
    public FileEntity mappingFormDtoToEntity(FileFormDto formDto) {
        FileEntity entity = new FileEntity();

        entity.setName(formDto.getName());
        entity.setContent(formDto.getContent());
        entity.setFilePath(formDto.getFilePath());
        entity.setExtendsion(formDto.getExtendsion());
        entity.setUrl(formDto.getUrl());
        entity.setContentType(formDto.getContentType());

        return entity;
    }
}
