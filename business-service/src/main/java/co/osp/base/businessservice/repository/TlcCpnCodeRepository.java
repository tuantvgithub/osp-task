package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.TlcCpnCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the TlcCpnCode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TlcCpnCodeRepository extends JpaRepository<TlcCpnCode, Long>,
                                                JpaSpecificationExecutor<TlcCpnCode> {

    @Query("select distinct t.company from TlcCpnCode t join t.company company " +
        "where t.type = :type and (company.code like %:codeOrNameSearch% or company.name like %:codeOrNameSearch%) ")
    List<Company> getCompaniesFromTlcCpnCode(@Param("type") String type, @Param("codeOrNameSearch") String codeOrNameSearch);

    @Query("select t from TlcCpnCode t where t.province.id is not null")
    List<TlcCpnCode> getByProvince();

    List<TlcCpnCode> findAllByCompanyNot(Company company);

    List<TlcCpnCode> findAllByCompanyEqualsAndTypeEquals(Company company, String type);

    List<TlcCpnCode> findAllByTypeEquals(String type);

    @Query("select tlcCpnCode from TlcCpnCode tlcCpnCode left join fetch tlcCpnCode.tlcCodeHis  where tlcCpnCode.id =:id")
    Optional<TlcCpnCode> findOneWithEagerRelationships(@Param("id") Long id);
}
