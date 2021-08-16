package co.osp.base.parsingpltool.web.controller;

import co.osp.base.parsingpltool.service.ParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/parsing-pl/")
public class ParsingController {

    private final ParsingService parsingService;

    @Autowired
    public ParsingController(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> parsingPL(
                @PathVariable Long id,
                @RequestBody MultipartFile file
    ) throws IOException {

        if (id == 1) return ResponseEntity.ok(this.parsingService.parsingPL1(file));
        else if (id == 2) return ResponseEntity.ok(this.parsingService.parsingPL2(file));
        else if (id == 3) return ResponseEntity.ok(this.parsingService.parsingPL3(file));
        else if (id == 4) return ResponseEntity.ok(this.parsingService.parsingPL4(file));
        else if (id == 5) return ResponseEntity.ok(this.parsingService.parsingPL5(file));

        return new ResponseEntity<>("Pl's id is not valid. " +
                "System only support id 1->5", HttpStatus.BAD_REQUEST);
    }

}
