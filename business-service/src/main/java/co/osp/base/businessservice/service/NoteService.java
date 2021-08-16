package co.osp.base.businessservice.service;

import co.osp.base.businessservice.dto.NoteDTO;
import co.osp.base.businessservice.entity.Note;

public interface NoteService {

    Note createNote(NoteDTO noteDTO);
    void updateNote(NoteDTO noteDTO);

}
