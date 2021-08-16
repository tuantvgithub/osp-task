package co.osp.base.businessservice.service.impl;

import co.osp.base.businessservice.entity.Note;
import co.osp.base.businessservice.repository.NoteRepository;
import co.osp.base.businessservice.service.NoteService;
import co.osp.base.businessservice.dto.NoteDTO;
import co.osp.base.businessservice.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public Note createNote(NoteDTO noteDTO)
    {
        Note note = new Note();
        note.setCompany_id(noteDTO.getCompany_id());
        note.setYear(noteDTO.getYear());
        note.setQuarter(noteDTO.getQuarter());
        note.setType(noteDTO.getType());
        note.setCreate_time(DateUtils.timeNow());
        note.setNote(noteDTO.getNote());
        note.setLicBusinessTypeId(noteDTO.getBusiness_type_id());

        noteRepository.save(note);
        return note;
    }

    @Override
    public void updateNote(NoteDTO noteDTO) {
        Optional<Note> noteOptional = this.noteRepository.findById(noteDTO.getId());
        Note note = null;
        if (noteOptional.isPresent()) {
            note = noteOptional.get();
            note.setNote(noteDTO.getNote());
            note.setUpdate_time(DateUtils.timeNow());
            this.noteRepository.save(note);
        }
    }
}
