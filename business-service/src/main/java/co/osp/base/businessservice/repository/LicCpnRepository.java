package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.LicBusinessType;
import co.osp.base.businessservice.entity.LicCpn;
import co.osp.base.businessservice.entity.NetworkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the LicCpn entity.
 */
@Repository
public interface LicCpnRepository extends JpaRepository<LicCpn, Long>,
                                            JpaSpecificationExecutor<LicCpn> {

    // longth
    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE lc.licCpn = :licCpn " +
        "ORDER BY lc.createdDate ASC")
    List<LicCpn> findRelated(@Param("licCpn") LicCpn licCpn);

    //huytq
    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE lc.company = :company " +
        "AND :licNumber = lc.licNumber " +
        "AND NOT ( ((year(lc.expiredTime) * 12 + month(lc.expiredTime)) < (:year * 12 )) OR ((:year * 12 + 12) < (year(lc.licCreatedDate) + month(lc.licCreatedDate))) ) " +
        "ORDER BY lc.licCreatedDate DESC")
    List<LicCpn> findByCompanyAndLicNumberYears(@Param("company") Company company,
                                               @Param("licNumber") String licNumber,
                                               @Param("year") Integer year);

    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE lc.company = :company " +
        "AND :licNumber = lc.licNumber " +
        "AND NOT ( ((:year * 12 + (:quarter) * 3) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) " +
        "OR ((:year * 12 + (:quarter) * 3 - 1) > (year(lc.expiredTime) * 12 + month(lc.expiredTime))) ) " +
        "ORDER BY lc.licCreatedDate DESC")
    List<LicCpn> findByCompanyAndLicNumberQuarters(@Param("company") Company company,
                                                  @Param("licNumber") String licNumber,
                                                  @Param("year") Integer year,
                                                  @Param("quarter") Integer quarter);

    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND (:networkType IS NULL OR :networkType IN elements(lc.networkTypes)) " +
        "AND NOT ( ((:year * 12 + (:quarter) * 3 - 1) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) " +
        "OR ((:year * 12 + (:quarter) * 3 - 2) > (year(lc.expiredTime) * 12 + month(lc.expiredTime))) ) " +
        "AND lc.licensee.feeFree = 0 " +
        "ORDER BY lc.licCreatedDate DESC")
    List<LicCpn> search(@Param("companies") List<Company> companies,
                        @Param("licBusinessType") LicBusinessType licBusinessType,
                        @Param("networkType") NetworkType networkType,
                        @Param("year") Integer year,
                        @Param("quarter") Integer quarter);

    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND (:networkType IS NULL OR :networkType IN elements(lc.networkTypes)) " +
        "AND NOT ( ((year(lc.expiredTime) * 12 + month(lc.expiredTime)) < (:year * 12 + 1)) OR ((:year * 12 + 11) < (year(lc.licCreatedDate) + month(lc.licCreatedDate))) ) " +
        "AND lc.licensee.feeFree = 0 " +
        "ORDER BY lc.licCreatedDate DESC")
    List<LicCpn> search(@Param("companies") List<Company> companies,
                        @Param("licBusinessType") LicBusinessType licBusinessType,
                        @Param("networkType") NetworkType networkType,
                        @Param("year") Integer year);



    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND NOT ( ((:year * 12 + (:quarter) * 3 - 1) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) " +
        "OR ((:year * 12 + (:quarter) * 3 - 2) > (year(lc.expiredTime) * 12 + month(lc.expiredTime))) ) " +
        "AND lc.licensee.feeFree = 0 " +
        "ORDER BY lc.licCreatedDate DESC")
    List<LicCpn> search(@Param("companies") List<Company> companies,
                  @Param("licBusinessType") LicBusinessType licBusinessType,
                  @Param("year") Integer year,
                  @Param("quarter") Integer quarter);

//    huytq
    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND NOT ( ((:year * 12 + (:quarter) * 3) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) " +
        "OR ((:year * 12 + (:quarter) * 3 - 1) > (year(lc.expiredTime) * 12 + month(lc.expiredTime))) ) " +
        "ORDER BY lc.licCreatedDate DESC")
    List<LicCpn> search2(@Param("companies") List<Company> companies,
                  @Param("licBusinessType") LicBusinessType licBusinessType,
                  @Param("year") Integer year,
                  @Param("quarter") Integer quarter);

    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (:company IS NULL OR lc.company IN :company) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND ( (:year IS NULL) OR " +
        "NOT ( ((year(lc.expiredTime) * 12 + month(lc.expiredTime)) < (:year * 12 + 1)) OR ((:year * 12 + 11) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) ) ) " +
        "AND lc.licensee.feeFree = 0"
    )
    List<LicCpn> search(@Param("company") Company company,
                        @Param("licBusinessType") LicBusinessType licBusinessType,
                        @Param("year") Integer year);

    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (:company IS NULL OR lc.company IN :company) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND ( (:year IS NULL) OR " +
        "NOT ( ((year(lc.expiredTime) * 12 + month(lc.expiredTime)) < (:year * 12)) OR ((:year * 12 + 12) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) ) ) "
    )
    List<LicCpn> search3(@Param("company") Company company,
                        @Param("licBusinessType") LicBusinessType licBusinessType,
                        @Param("year") Integer year);

    //tuantv
    @Query(
        "SELECT lc FROM LicCpn lc " +
            "WHERE " +
            "   (:company IS NULL OR lc.company IN :company) " +
            "   AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
            "   AND ( " +
            "       :year IS NULL " +
            "       OR NOT ( " +
            "           ( " +
            "               lc.expiredTime < lc.expiredDate " +
            "               AND ( " +
            "                   YEAR(lc.expiredTime)*12 + MONTH(lc.expiredTime) < :year*12 + 1 " +
            "                   OR :year*12 + 11 < YEAR(lc.licCreatedDate)*12 + MONTH(lc.licCreatedDate) " +
            "               ) " +
            "           ) OR ( " +
            "               YEAR(lc.expiredDate)*12 + MONTH(lc.expiredDate) < :year*12 + 1 " +
            "               OR :year*12 + 11 < YEAR(lc.licCreatedDate)*12 + MONTH(lc.licCreatedDate) " +
            "           ) " +
            "       ) " +
            "   ) " +
            "AND lc.licensee.feeFree = 0"
    )
    List<LicCpn> searchVersion2(@Param("company") Company company,
                                @Param("licBusinessType") LicBusinessType licBusinessType,
                                @Param("year") Integer year);

    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND NOT ( ((year(lc.expiredTime) * 12 + month(lc.expiredTime)) < (:year * 12 + 1)) OR ((:year * 12 + 11) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) ) " +
        "AND lc.licensee.feeFree = 0"
    )
    List<LicCpn> searchLicPerYear(@Param("companies") List<Company> companies,
                        @Param("licBusinessType") LicBusinessType licBusinessType,
                        @Param("year") Integer year);

    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (:company IS NULL OR lc.company IN :company) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND NOT ( ((:year * 12 + (:quarter) * 3 - 1) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) " +
        "OR ((:year * 12 + (:quarter) * 3 - 2) > (year(lc.expiredTime) * 12 + month(lc.expiredTime))) ) " +
        "AND lc.licensee.feeFree = 0"
    )
    List<LicCpn> search(@Param("company") Company company,
                        @Param("licBusinessType") LicBusinessType licBusinessType,
                        @Param("year") Integer year,
                        @Param("quarter") Integer quarter);

    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE (:company IS NULL OR lc.company IN :company) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND (:network IS NULL OR :network IN elements(lc.networkTypes)) " +
        "AND NOT ( ((:year * 12 + (:quarter) * 3 - 1) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) " +
        "OR ((:year * 12 + (:quarter) * 3 - 2) > (year(lc.expiredTime) * 12 + month(lc.expiredTime))) ) " +
        "AND lc.licensee.feeFree = 0"
    )
    List<LicCpn> search(@Param("company") Company company,
                        @Param("licBusinessType") LicBusinessType licBusinessType,
                        @Param("network") NetworkType network,
                        //@Param("licNumber") String licNumber,
                        @Param("year") Integer year,
                        @Param("quarter") Integer quarter);

    @Query("SELECT DISTINCT lc.company FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND NOT ( ((year(lc.expiredTime) * 12 + month(lc.expiredTime)) < (:year * 12 + 1)) OR ((:year * 12 + 11) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) ) " +
        "AND lc.licensee.feeFree = 0"
    )
    List<Company> search(@Param("companies") List<Company> companies,
                        @Param("licBusinessType") LicBusinessType licBusinessType,
                        @Param("year") Integer year);

    //huytq
    @Query("SELECT DISTINCT lc.company FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND NOT ( ((year(lc.expiredTime) * 12 + month(lc.expiredTime)) < (:year * 12 + 1)) OR ((:year * 12 + 11) < (year(lc.licCreatedDate) * 12 + month(lc.licCreatedDate))) ) "
    )
    List<Company> searchs(@Param("companies") List<Company> companies,
                         @Param("licBusinessType") LicBusinessType licBusinessType,
                         @Param("year") Integer year);

    @Query("SELECT DISTINCT lc.company FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND ((year(lc.licCreatedDate) IN :years) OR (year(lc.expiredTime) IN :years)) " +
        "AND lc.licensee.feeFree = 0"
    )
    List<Company> search(@Param("companies") List<Company> companies,
                         @Param("licBusinessType") LicBusinessType licBusinessType,
                         @Param("years") List<Integer> years);

    @Query("SELECT DISTINCT lc.company FROM LicCpn lc " +
        "WHERE (coalesce(:companies) IS NULL OR lc.company IN :companies) " +
        "AND (:licBusinessType IS NULL OR :licBusinessType IN elements(lc.licBusinessTypes)) " +
        "AND ((year(lc.licCreatedDate) <= :maxYear) AND (year(lc.expiredTime) >= :minYear)) " +
        "AND (:networkType IS NULL OR :networkType IN elements(lc.networkTypes)) " +
        "AND (:option IS NULL) " +
        "AND lc.licensee.feeFree = 0")
    List<Company> search(@Param("companies") List<Company> companies,
                         @Param("licBusinessType") LicBusinessType licBusinessType,
                         @Param("networkType") NetworkType networkType,
                         @Param("minYear") Integer minYear,
                         @Param("maxYear") Integer maxYear,
                         @Param("option") Integer option);


    @Query("SELECT lc FROM LicCpn lc " +
        "WHERE lc.licNumber LIKE %:licNumber%")
    List<LicCpn> searchByLic(@Param("licNumber") String licNumber);

}
