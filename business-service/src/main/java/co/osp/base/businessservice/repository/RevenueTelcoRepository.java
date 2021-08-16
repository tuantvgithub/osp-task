package co.osp.base.businessservice.repository;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.File;
import co.osp.base.businessservice.entity.LicCpn;
import co.osp.base.businessservice.entity.RevenueTelco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data  repository for the VT02 entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RevenueTelcoRepository extends JpaRepository<RevenueTelco, Long> {

    List<RevenueTelco> findAll();

    @Query("SELECT r FROM RevenueTelco r " +
        "JOIN FETCH r.company compnay " +
        "WHERE (coalesce(:companyIds) IS NULL OR r.company IN :companyIds) " +
        "AND (:year IS NULL OR r.year = :year) " +
        "AND (:quarter IS NULL OR r.quarter = :quarter) AND (:type IS NULL OR r.type = :type)")
    List<RevenueTelco> search(@Param("companyIds") List<Company> companyIds,
                              @Param("year") Long year, @Param("quarter") Long quarter,
                              @Param("type") String type);

    @Query("SELECT r FROM RevenueTelco r " +
        "JOIN FETCH r.company compnay " +
        "JOIN FETCH r.file file " +
        "WHERE r.company = :company AND (:year IS NULL OR r.year = :year) " +
        "AND (:quarter IS NULL OR r.quarter = :quarter) AND (:type IS NULL OR r.type = :type)")
    List<RevenueTelco> search(@Param("company") Company company, @Param("year") Long year,
                              @Param("quarter") Long quarter, @Param("type") String type);

    @Query("SELECT r FROM RevenueTelco r " +
        "JOIN FETCH r.company compnay " +
        "WHERE (r.year IS NULL OR r.year = :year) AND " +
        "(r.quarter IS NULL OR r.quarter = :quarter) AND (:type IS NULL OR r.type = :type)")
    List<RevenueTelco> search(@Param("year") Long year, @Param("quarter") Long quarter,
                              @Param("type") String type);

    @Query("SELECT distinct r.company FROM RevenueTelco r " +
        "JOIN r.company company " +
        "WHERE (:year IS NULL OR r.year = :year) AND (:quarter IS NULL OR r.quarter = :quarter) " +
        "AND (:type IS NULL OR r.type = :type)")
    List<Company> findCompanyByYearAndQuarterAndType(@Param("year") Long year,
                                                     @Param("quarter") Long quarter,
                                                     @Param("type") String type);

    @Transactional
    @Modifying
    @Query("DELETE FROM RevenueTelco r " +
        "WHERE (:companyIds is NULL OR r.company IN :companyIds) " +
        "AND (:year IS NULL OR r.year = :year) AND (:quarter IS NULL OR r.quarter = :quarter) " +
        "AND (:type IS NULL OR r.type = :type)")
    void del(@Param("companyIds") List<Company> companyIds, @Param("year") Long year,
             @Param("quarter") Long quarter, @Param("type") String type);

    @Transactional
    @Modifying
    @Query("DELETE FROM RevenueTelco r WHERE file = :file")
    void delFile(@Param("file") File file);

    @Transactional
    @Modifying
    @Query("UPDATE RevenueTelco r SET r.payDeadline = :payDeadline " +
        "WHERE r.company = :company " +
        "AND r.licCpn IN :licCpns " +
        "AND (:year IS NULL OR r.year = :year) " +
        "AND (:quarter IS NULL OR r.quarter = :quarter) " +
        "AND (:type IS NULL OR r.type = :type)")
    void updatePayDeadline(@Param("payDeadline") String payDeadline,
                           @Param("company") Company company,
                           @Param("licCpns") List<LicCpn> licCpns,
                           @Param("year") Long year,
                           @Param("quarter") Long quarter,
                           @Param("type") String type);

    @Query("SELECT distinct r.file FROM RevenueTelco r " +
        "JOIN r.file file " +
        "WHERE (r.year IS NULL OR r.year = :year) AND " +
        "(r.quarter IS NULL OR r.quarter = :quarter) AND (:type IS NULL OR r.type = :type)")
    List<File> searchFile(@Param("year") Long year, @Param("quarter") Long quarter,
                          @Param("type") String type);

    @Query("SELECT r FROM RevenueTelco r " +
        "WHERE (:company IS NULL OR r.company = :company) " +
        "AND (coalesce(:licCpns) IS NULL OR r.licCpn IN :licCpns) " +
        "AND (:year IS NULL OR r.year = :year) " +
        "AND (coalesce(:quarters) IS NULL OR r.quarter IN :quarters) " +
        "AND (:type IS NULL OR r.type = :type) " +
        "ORDER BY r.quarter ASC")
    List<RevenueTelco> search(@Param("company")Company company,
                              @Param("licCpns") List<LicCpn> licCpns,
                              @Param("year") Long year,
                              @Param("quarters") List<Long> quarters,
                              @Param("type") String type);

    @Query(value = "SELECT * FROM telco_revenue tr " +
        "WHERE tr.revenue > 0 " +
        "AND tr.type = 'year' " +
        "ORDER BY tr.year LIMIT 1", nativeQuery = true)
    RevenueTelco findMaxYear();

    @Query(value = "SELECT * FROM telco_revenue tr " +
        "WHERE tr.revenue > 0 " +
        "AND tr.type = 'quarter' " +
        "ORDER BY (tr.year*4 + tr.quarter) DESC LIMIT 1", nativeQuery = true)
    RevenueTelco findMaxYearQuarter();
}
