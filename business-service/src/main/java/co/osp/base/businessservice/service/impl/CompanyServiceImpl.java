package co.osp.base.businessservice.service.impl;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.repository.CompanyRepository;
import co.osp.base.businessservice.service.CompanyService;
import io.github.jhipster.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Company}.
 */
@Service
@Transactional
public class CompanyServiceImpl extends QueryService<Company> implements CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Company> findAllByCompanyTypeId(Long companyTypeId) {
        return this.companyRepository.findAllByCompanyTypeId(companyTypeId);
    }

    @Override
    public List<Company> findByListIds(List<Long> companyIds) {
        return this.companyRepository.findByListIds(companyIds);
    }

    @Override
    public List<Company> findByIdsAndCode(List<Long> ids, String code) {
        return this.companyRepository.findByIdsAndCode(ids, code);
    }

    @Override
    public Company findById(Long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        return company.orElse(null);
    }

    @Override
    public Company findByName(String name) {
        return this.companyRepository.findByName(name);
    }
}
