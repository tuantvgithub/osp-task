package co.osp.base.filemodule.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FileFormDto {

    @NotNull(message="The name has to be present")
    @Size(max=128, message="The name must be less than {max} characters")
    private String name;

    @Size(max=45, message="The extendsion must be less than {max} characters")
    private String extendsion;

    @NotNull(message="The url has to be present")
    @Size(max=128, message="The url must be less than {max} characters")
    private String url;

    private byte[] content;

    @NotNull
    private String contentType;

    private String filePath;
}
