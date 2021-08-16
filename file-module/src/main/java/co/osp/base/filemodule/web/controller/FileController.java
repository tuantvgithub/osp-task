package co.osp.base.filemodule.web.controller;

import co.osp.base.filemodule.dto.FileDto;
import co.osp.base.filemodule.dto.FileFormDto;
import co.osp.base.filemodule.service.FileService;
import co.osp.base.filemodule.utils.MediaTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.jhipster.web.util.HeaderUtil;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String ENTITY_NAME = "File";

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    private ServletContext servletContext;

    @PostMapping
    public ResponseEntity<FileDto> uploadFile(
            @Valid @RequestBody FileFormDto uploadFormDto
    ) throws URISyntaxException, IOException {
        FileDto dto = this.fileService.uploadFile(uploadFormDto);

        if (dto.getId() == null) dto.setId(0L);

        return ResponseEntity
                .created(new URI("/api/v1/files/" + dto.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(
                        this.applicationName, true,
                        ENTITY_NAME, dto.getId().toString()))
                .body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDto> getFileById(@PathVariable Long id) throws IOException {
        FileDto fileDto = this.fileService.getFileById(id);

        return ResponseEntity.ok(fileDto);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFileById(@PathVariable Long id) throws IOException {
        FileDto fileDto = this.fileService.getFileById(id);
        File file = new File(fileDto.getFilePath());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(fileDto.getName(), this.servletContext);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileDto.getName())
                .contentType(mediaType)
                .contentLength(file.length())
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        this.fileService.deleteFileById(id);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityDeletionAlert(
                        this.applicationName, true,
                        ENTITY_NAME, id.toString()))
                .build();
    }

}
