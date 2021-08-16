package co.osp.base.businessservice.web.controller;

import co.osp.base.businessservice.dto.RespSearchWarnDoc;
import co.osp.base.businessservice.dto.WarnDocDTO;
import co.osp.base.businessservice.entity.Note;
import co.osp.base.businessservice.entity.WarnDoc;
import co.osp.base.businessservice.service.NoteService;
import co.osp.base.businessservice.dto.NoteDTO;
import co.osp.base.businessservice.service.WarnDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WarnDocResource {

    private final NoteService noteService;
    private final WarnDocService warnDocService;

    @Autowired
    public WarnDocResource(NoteService noteService, WarnDocService warnDocService) {
        this.noteService = noteService;
        this.warnDocService = warnDocService;
    }

    @PostMapping("/telcoServices/searchWarnDoc")
    public ResponseEntity<List<RespSearchWarnDoc>> search(@RequestBody WarnDocDTO warnDocDTO)
    {
        List<RespSearchWarnDoc> result = warnDocService.search(warnDocDTO);

        result.forEach((w) -> {
            if (w.getCompany()!=null){
                w.getCompany().setCompanyType(null);
                w.getCompany().setProvince(null);
            }
            for (WarnDoc warnDoc : w.getWarnDocs())
            {
                if (warnDoc.getFile()!=null) {
                    warnDoc.getFile().setContent(null);
                    warnDoc.getFile().setContentType(null);
                }
                warnDoc.setCompany(null);
            }
        });
        return ResponseEntity.ok(result);
    }

    @PostMapping("/telcoServices/createWarnDoc")
    public ResponseEntity<WarnDoc> create(@RequestBody WarnDocDTO warnDoc) throws Exception
    {
        WarnDoc result = null;
        try {
            result = warnDocService.create(warnDoc);
        } catch(Exception e){
            System.out.println(e.getMessage());
            throw new Exception(e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/telcoServices/deleteWarnDoc")
    public void del(@RequestParam(name = "warn_doc_id", required = true) Long warnDocId)
    {
        warnDocService.del(warnDocId);
        return;
    }

    @PutMapping("/telcoServices/updateWarnDoc")
    public void update(@RequestBody WarnDocDTO warnDoc) throws Exception
    {
        warnDocService.update(warnDoc);
        return;
    }

    @PostMapping("/telcoServices/createNote")
    public ResponseEntity<Note> create(@RequestBody NoteDTO noteDTO)
    {
        Note note = noteService.createNote(noteDTO);
        return ResponseEntity.ok(note);
    }

    @PostMapping("/telcoServices/updateNote")
    public void update(@RequestBody NoteDTO noteDTO) {
        this.noteService.updateNote(noteDTO);
    }

    @PostMapping("/telcoServices/searchNote")
    public void searchNote(@RequestBody NoteDTO noteDto) {

    }
}
