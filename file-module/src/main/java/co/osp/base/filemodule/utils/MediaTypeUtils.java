package co.osp.base.filemodule.utils;

import org.springframework.http.MediaType;

import javax.servlet.ServletContext;

public class MediaTypeUtils {

    public static MediaType getMediaTypeForFileName(String fileName, ServletContext context) {
        String mimeType = context.getMimeType(fileName);
        try {
            MediaType mediaType = MediaType.parseMediaType(mimeType);
            return mediaType;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}
