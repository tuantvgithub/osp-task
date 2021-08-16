package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.TlcCodeFee;
import co.osp.base.businessservice.entity.TlcCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the TlcCodeFee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TlcCodeFeeRepository extends JpaRepository<TlcCodeFee, Long>, JpaSpecificationExecutor<TlcCodeFee>{

    List<TlcCodeFee> findAllByTlcCodeTypeEquals(TlcCodeType tlcCodeType);
    @Query(value = "select t from TlcCodeFee t where t.name=:name and t.tlcCodeType.name=:typeName")
    TlcCodeFee findByName(@Param("name") String name, @Param("typeName") String typeName);
    Optional<TlcCodeFee> findTopByTtEquals(String tt);
}
