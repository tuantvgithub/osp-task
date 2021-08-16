package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.LicBusinessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the LicBusinessType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LicBusinessTypeRepository extends JpaRepository<LicBusinessType, Long> {
    List<LicBusinessType> findAllByParentNull();

    LicBusinessType findAllByName(String name);

    @Query("SELECT lbt FROM LicBusinessType lbt " +
        "WHERE (coalesce(:ids) IS NULL OR lbt.id IN :ids)")
    List<LicBusinessType> findByListIds(@Param("ids") List<Long> ids);
}
