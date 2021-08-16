package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.CpnCommonManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CpnCommonManagerRepository extends JpaRepository<CpnCommonManager, Long>,
                                                    JpaSpecificationExecutor<CpnCommonManager> {

    @Query(value = "SELECT * FROM cpn_common_manager ccm " +
        "WHERE ccm.business_type_id = :business_type_id " +
        "AND ccm.type = :type " +
        "AND ccm.year = :year " +
        "AND (:quarter IS NULL OR ccm.quarter = :quarter) " +
        "ORDER BY ccm.update_time DESC LIMIT 1"
        , nativeQuery = true)
    CpnCommonManager search(@Param("business_type_id") Long businessTypeId,
                            @Param("type") String type,
                            @Param("year") Long year,
                            @Param("quarter") Long quarter);

}
