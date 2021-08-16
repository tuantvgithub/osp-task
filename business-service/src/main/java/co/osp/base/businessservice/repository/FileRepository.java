package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data  repository for the File entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findAllByNameOrderByIdDesc(String name);

    // longth
    @Query("SELECT DISTINCT f FROM File f WHERE (f.id IN :file_ids)")
    Set<File> findByListIds(@Param("file_ids") List<Long> file_ids);

    @Query("select f.id from File f")
    List<Long> getAllIds();
}
