package co.osp.base.parsingpltool.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ParsingService {

    String parsingPL1(MultipartFile multipartFile) throws IOException;
    String parsingPL2(MultipartFile multipartFile) throws IOException;
    String parsingPL3(MultipartFile multipartFile) throws IOException;
    String parsingPL4(MultipartFile multipartFile) throws IOException;
    String parsingPL5(MultipartFile multipartFile) throws IOException;
}
