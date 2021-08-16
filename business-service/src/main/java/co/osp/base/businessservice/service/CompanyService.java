package co.osp.base.businessservice.service;

import co.osp.base.businessservice.entity.Company;

import java.util.List;

/**
 * Service Interface for managing {@link Company}.
 */
public interface CompanyService {

    List<Company> findAllByCompanyTypeId(Long companyTypeId);
    List<Company> findByListIds(List<Long> companyIds);
    List<Company> findByIdsAndCode(List<Long> ids, String code);
    Company findById(Long id);
    Company findByName(String name);
}
