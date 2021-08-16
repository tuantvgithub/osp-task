package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>,
                                            JpaSpecificationExecutor<Company> {

    Company findByName(String name);

    @Query("SELECT CPN FROM Company CPN WHERE CPN.companyType.id = ?1")
    List<Company> findAllByCompanyTypeId(Long companyTypeId);

    @Query("select company from Company company where (coalesce(:ids) IS NULL OR company.id in :ids)")
    List<Company> findByListIds(@Param("ids")List<Long> ids);

    // LongTH
    @Query("SELECT company FROM Company company " +
        "WHERE (coalesce(:ids) IS NULL OR company.id IN :ids) " +
        "AND (:code IS NULL OR company.code = :code)")
    List<Company> findByIdsAndCode(@Param("ids")List<Long> ids, @Param("code") String code);

}
