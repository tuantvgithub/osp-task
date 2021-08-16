package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {

    @Query(value = "SELECT * FROM cpn_note n " +
        "WHERE (n.company_id = :company_id) " +
        "AND n.lic_business_type_id = :lic_business_type_id " +
        "AND (:year IS NULL OR n.year = :year) " +
        "AND (:quarter IS NULL OR n.quarter = :quarter) " +
        "AND (:type IS NULL OR n.type = :type) " +
        "ORDER BY n.create_time DESC LIMIT 1", nativeQuery=true)
    public Note search(@Param("company_id") Long company_id,
                       @Param("lic_business_type_id") Long lic_business_type_id,
                       @Param("year") Long year,
                       @Param("quarter") Long quarter,
                       @Param("type") String type);
}
