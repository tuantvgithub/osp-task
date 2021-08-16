package co.osp.base.filemodule.web.exception.handler;

import co.osp.base.filemodule.dto.ErrorDto;
import co.osp.base.filemodule.web.exception.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class FileExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileExceptionHandler.class);

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorDto> handleFileNotFoundException(FileNotFoundException e) {
        LOGGER.warn(e.getMessage());

        ErrorDto errorDto = new ErrorDto(HttpStatus.NOT_FOUND, "Not found", e.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnwantedException(Exception e) {
        LOGGER.warn(e.getMessage());
        e.printStackTrace();

        ErrorDto errorDto = new ErrorDto("Unknow error");
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request
    ) {
        LOGGER.warn(ex.getMessage());

        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}
