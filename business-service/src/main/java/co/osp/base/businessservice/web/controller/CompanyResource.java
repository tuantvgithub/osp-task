package co.osp.base.businessservice.web.controller;

import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * REST controller for managing {@link Company}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CompanyResource {

    private final Logger log = LoggerFactory.getLogger(CompanyResource.class);

    private final CompanyService companyService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public CompanyResource(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * {@code GET  /companies} : get all the companies.
     *
     * @param companyTypeId
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of companies in body.
     */
    @GetMapping("/companies-by-company-type")
    public ResponseEntity<List<Company>> getAllCompaniesByCompanyType(Long companyTypeId) {
        log.debug("REST request to get all companies by company type id");
        List<Company> result = this.companyService.findAllByCompanyTypeId(companyTypeId);
        return ResponseEntity.ok().body(result);
    }

}
