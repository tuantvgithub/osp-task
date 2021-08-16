package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.LicBusinessType;
import co.osp.base.businessservice.entity.NetworkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data  repository for the NetworkType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NetworkTypeRepository extends JpaRepository<NetworkType, Long> {
    List<NetworkType> findAllByParentNull();

    // longth
    @Query("SELECT nwt FROM NetworkType nwt " +
        "WHERE :licBusinessTypes IN elements(nwt.licBusinessTypes)")
    List<NetworkType> findByLicBusinessType(@Param("licBusinessTypes") Set<LicBusinessType> licBusinessTypes);

    @Query("SELECT nwt FROM NetworkType nwt " +
        "WHERE (coalesce(:ids) IS NULL OR nwt.id IN :ids) " +
        "AND nwt.alias IS NOT NULL")
    List<NetworkType> findByListIds(@Param("ids") List<Long> ids);
}
