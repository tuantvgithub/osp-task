package co.osp.base.filemodule.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorDto {

    private HttpStatus status;

    private LocalDateTime timestamp;

    private String message;

    private String details;

    public ErrorDto(HttpStatus httpStatus, String message, String details) {
        this.status = httpStatus;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorDto(String message) {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }
}
