package co.osp.base.filemodule.dto;

import lombok.Data;

@Data
public class FileDto {

    private Long id;

    private String name;

    private String extendsion;

    private String url;

    private byte[] content;

    private String contentType;

    private String filePath;
}
