package co.osp.base.backupfile.repository;

import co.osp.base.backupfile.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Query("select f.id from File f")
    List<Long> getAllIds();
}
