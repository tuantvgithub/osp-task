package co.osp.base.businessservice.service.impl;

import co.osp.base.businessservice.repository.LicBusinessTypeRepository;
import co.osp.base.businessservice.entity.Company;
import co.osp.base.businessservice.entity.File;
import co.osp.base.businessservice.entity.LicBusinessType;
import co.osp.base.businessservice.entity.WarnDoc;
import co.osp.base.businessservice.repository.CompanyRepository;
import co.osp.base.businessservice.repository.FileRepository;
import co.osp.base.businessservice.repository.WarnDocRepository;
import co.osp.base.businessservice.service.WarnDocService;
import co.osp.base.businessservice.dto.RespSearchWarnDoc;
import co.osp.base.businessservice.dto.WarnDocDTO;
import co.osp.base.businessservice.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class WarnDocServiceImpl implements WarnDocService {

    private final WarnDocRepository warnDocRepository;

    private final CompanyRepository companyRepository;

    private final FileRepository fileRepository;

    private final LicBusinessTypeRepository lbtRepository;

    @Autowired
    public WarnDocServiceImpl(
                WarnDocRepository warnDocRepository,
                CompanyRepository companyRepository,
                FileRepository fileRepository,
                LicBusinessTypeRepository lbtRepository
    ) {
        this.warnDocRepository = warnDocRepository;
        this.companyRepository = companyRepository;
        this.fileRepository = fileRepository;
        this.lbtRepository = lbtRepository;
    }

    @Override
    public List<RespSearchWarnDoc> search(WarnDocDTO warnDocDTO)
    {
        List<RespSearchWarnDoc> respSearchWarnDocs = new ArrayList<RespSearchWarnDoc>();
        HashMap<String, RespSearchWarnDoc> hSearchWarnDoc = new HashMap<String, RespSearchWarnDoc>();

        List<Company> companies = companyRepository.findByListIds(warnDocDTO.getCompany_ids());
        List<LicBusinessType> lbts = lbtRepository.findByListIds(warnDocDTO.getBusiness_type_ids());
        String fromDate = null;
        String toDate = null;
        if (warnDocDTO.getWarn_doc_date()!=null){
            fromDate = warnDocDTO.getWarn_doc_date().getFrom_date();
            toDate = warnDocDTO.getWarn_doc_date().getTo_date();
        }
        List<WarnDoc> warnDocs = null;
        if ((warnDocDTO.getBusiness_type_ids()==null)||(warnDocDTO.getBusiness_type_ids().size()==0)){
            warnDocs = warnDocRepository.searchDoc(companies, warnDocDTO.getWarn_doc_number(), lbts,
                fromDate, toDate, warnDocDTO.getYear(), warnDocDTO.getQuarter(),
                warnDocDTO.getType(), warnDocDTO.getDoc_type(), warnDocDTO.getTimes());

            List<WarnDoc> tmps = warnDocRepository.searchKhoSoDoc(companies, warnDocDTO.getWarn_doc_number(),
                fromDate, toDate, warnDocDTO.getYear(), warnDocDTO.getQuarter(),
                warnDocDTO.getType(), warnDocDTO.getDoc_type(), warnDocDTO.getTimes(), null);
            warnDocs.addAll(tmps);
        }else{
            if ((warnDocDTO.getBusiness_type_ids()!=null)&&(warnDocDTO.getBusiness_type_ids().size()==1)&&(warnDocDTO.getBusiness_type_ids().get(0)==100)) {
                warnDocs = warnDocRepository.searchKhoSoDoc(companies, warnDocDTO.getWarn_doc_number(),
                    fromDate, toDate, warnDocDTO.getYear(), warnDocDTO.getQuarter(),
                    warnDocDTO.getType(), warnDocDTO.getDoc_type(), warnDocDTO.getTimes(), null);
            }else{
                warnDocs = warnDocRepository.searchDoc(companies, warnDocDTO.getWarn_doc_number(), lbts,
                    fromDate, toDate, warnDocDTO.getYear(), warnDocDTO.getQuarter(),
                    warnDocDTO.getType(), warnDocDTO.getDoc_type(), warnDocDTO.getTimes());
            }
        }

        for (WarnDoc w : warnDocs)
        {
            RespSearchWarnDoc rswd = new RespSearchWarnDoc();
            rswd.setCompany(w.getCompany());

            if (hSearchWarnDoc.get(w.getCompany().getName())!=null) rswd = hSearchWarnDoc.get(w.getCompany().getName());

            rswd.getWarnDocs().add(w);
            hSearchWarnDoc.put(w.getCompany().getName(), rswd);
        }
        hSearchWarnDoc.forEach((k, v) -> {
            respSearchWarnDocs.add(v);
        });

        return respSearchWarnDocs;
    }

    @Override
    public void del(Long warnDocId)
    {
        warnDocRepository.deleteById(warnDocId);
    }

    @Override
    public void update(WarnDocDTO warnDoc) throws Exception
    {
        WarnDoc oldWarnDoc = warnDocRepository.findById(warnDoc.getWarn_doc_id()).get();
        WarnDoc oriWarnDoc = warnDocRepository.getOne(warnDoc.getWarn_doc_id());
        if (oriWarnDoc==null) return;

        if (warnDoc.getCompany_id()!=null) {
            Company company = companyRepository.getOne(warnDoc.getCompany_id());
            oriWarnDoc.setCompany(company);
        }

        //uyen update cong van
        if(!warnDoc.getDoc_number().equalsIgnoreCase(oldWarnDoc.getDoc_number())){
            WarnDoc docYear = warnDocRepository.findByDocNumberYear(warnDoc.getDoc_number(),
                Integer.valueOf(DateUtils.convertFormat(oriWarnDoc.getDoc_date(), "yyyy-MM-dd", "yyyy")));
            if (docYear!=null) throw new Exception("Thông tin công văn trùng với văn bản đã có trên hệ thống. Vui lòng kiểm tra!");
            oriWarnDoc.setDoc_number(warnDoc.getDoc_number());
        }


        // if (warnDoc.getDoc_number()!=null) {
        //     WarnDoc docYear = warnDocRepository.findByDocNumberYear(warnDoc.getDoc_number(),
        //         Integer.valueOf(DateUtils.convertFormat(oriWarnDoc.getDoc_date(), "yyyy-MM-dd", "yyyy")));
        //     if (docYear!=null) throw new Exception("Thông tin công văn trùng với văn bản đã có trên hệ thống. Vui lòng kiểm tra!");

        //     oriWarnDoc.setDoc_number(warnDoc.getDoc_number());
        // }
        if (warnDoc.getDoc_type()!=null) oriWarnDoc.setDoc_type(warnDoc.getDoc_type());
        if (warnDoc.getFile_id()!=null) {
            File file = fileRepository.getOne(warnDoc.getFile_id());
            oriWarnDoc.setFile(file);
        }
        if ((warnDoc.getBusiness_type_id()!=null)&&(warnDoc.getBusiness_type_id()!=100)) {
            LicBusinessType lbt = lbtRepository.getOne(warnDoc.getBusiness_type_id());
            oriWarnDoc.setLicBusinessType(lbt);
        }
        if (warnDoc.getTimes()!=null) oriWarnDoc.setTimes(warnDoc.getTimes());
        if (warnDoc.getDoc_date()!=null) oriWarnDoc.setDoc_date(warnDoc.getDoc_date());
        oriWarnDoc.setUpdate_time(DateUtils.timeNow());
        if (warnDoc.getYear()!=null) oriWarnDoc.setYear(warnDoc.getYear());
        if (warnDoc.getQuarter()!=null) oriWarnDoc.setQuarter(warnDoc.getQuarter());
        if (warnDoc.getType()!=null) oriWarnDoc.setType(warnDoc.getType());
        if (warnDoc.getNumberOfTime()!=null) oriWarnDoc.setNumberOfTime(warnDoc.getNumberOfTime());
        oriWarnDoc.setPayDeadline(warnDoc.getPay_deadline());
        if (warnDoc.getNote()!=null) oriWarnDoc.setNote(warnDoc.getNote());

        warnDocRepository.save(oriWarnDoc);
    }

    @Override
    public WarnDoc create(WarnDocDTO warnDocDTO) throws Exception
    {
        WarnDoc warnDoc = new WarnDoc();
        if ((warnDocDTO.getDoc_type()!=null)&&(warnDocDTO.getDoc_type().equalsIgnoreCase("out"))){
            WarnDoc dbWarnDoc = warnDocRepository.find(warnDocDTO.getCompany_id(), warnDocDTO.getBusiness_type_id(),
                                    warnDocDTO.getYear(), warnDocDTO.getQuarter(),
                                    warnDocDTO.getType(), warnDocDTO.getTimes(), warnDocDTO.getNumberOfTime());
            if (dbWarnDoc!=null) throw new Exception("Thông tin công văn đã có trên hệ thống. Vui lòng kiểm tra!");
System.out.println("DocNumber: " + warnDocDTO.getDoc_number() + "\tDoc_date: " + Integer.valueOf(DateUtils.convertFormat(warnDocDTO.getDoc_date(), "yyyy-MM-dd", "yyyy")));
            WarnDoc docYear = warnDocRepository.findByDocNumberYear(warnDocDTO.getDoc_number(),
                                Integer.valueOf(DateUtils.convertFormat(warnDocDTO.getDoc_date(), "yyyy-MM-dd", "yyyy")));
            if (docYear!=null) throw new Exception("Thông tin công văn đã có trên hệ thống. Vui lòng kiểm tra!");
        }

        Company company = companyRepository.getOne(warnDocDTO.getCompany_id());
        warnDoc.setCompany(company);
        if (warnDocDTO.getFile_id()!=null) {
            File file = fileRepository.getOne(warnDocDTO.getFile_id());
            warnDoc.setFile(file);
        }

        if ((warnDocDTO.getBusiness_type_id()!=null)&&(warnDocDTO.getBusiness_type_id()!=100)) {
            LicBusinessType lbt = lbtRepository.getOne(warnDocDTO.getBusiness_type_id());
            warnDoc.setLicBusinessType(lbt);
        }

        warnDoc.setDoc_number(warnDocDTO.getDoc_number());
        warnDoc.setDoc_type(warnDocDTO.getDoc_type());
        warnDoc.setDoc_date(warnDocDTO.getDoc_date());
        warnDoc.setCreate_time(DateUtils.timeNow());
        warnDoc.setUpdate_time(DateUtils.timeNow());
        warnDoc.setTimes(warnDocDTO.getTimes());
        if(warnDocDTO.getNumberOfTime()!=null) warnDoc.setNumberOfTime(warnDocDTO.getNumberOfTime());
        warnDoc.setYear(warnDocDTO.getYear());
        warnDoc.setQuarter(warnDocDTO.getQuarter());
        warnDoc.setType(warnDocDTO.getType());
        warnDoc.setPayDeadline(warnDocDTO.getPay_deadline());
        warnDoc.setNote(warnDocDTO.getNote());

        return warnDocRepository.save(warnDoc);
    }
}
