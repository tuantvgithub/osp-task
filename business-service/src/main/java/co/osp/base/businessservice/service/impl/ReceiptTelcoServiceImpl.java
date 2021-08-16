package co.osp.base.businessservice.service.impl;

import co.osp.base.businessservice.dto.*;
import co.osp.base.businessservice.entity.*;
import co.osp.base.businessservice.service.*;
import co.osp.base.businessservice.repository.*;
import co.osp.base.businessservice.utils.DateUtils;
import co.osp.base.businessservice.web.exception.BadRequestAlertException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jhipster.service.filter.LongFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReceiptTelcoServiceImpl implements ReceiptTelcoService {

    @Value("${app.templateDir}")
    private String templateDir;
    @Value("${app.tokhaithuethang}")
    private String tokhaithuethang;
    @Value("${app.tokhaithuenam}")
    private String tokhaithuenam;

    private final ReceiptRepository receiptRepository;
    private final CompanyService companyService;
    private final FileService fileService;
    private final UserService userService;
    private final PaymentTypeService paymentTypeService;
    private final CpnCommonManagerService ccmService;
    private final TlcCpnCodeQueryService tlcCpnCodeQueryService;
    private final ReceiptServicePaymentRepository receiptServicePaymentRepository;
    private final RevenueTelcoRepository revenueTelcoRepository;
    private final LicCpnRepository licCpnRepository;
    private final LicBusinessTypeRepository licBusinessTypeRepository;
    private final NetworkTypeRepository networkTypeRepository;
    private final RevokeLicCpnRepository revokeLicCpnRepository;
    private final NoteRepository noteRepository;
    private final ReceiptServicePaymentHisRepository rspHisRepository;
    private final WarnDocRepository warnDocRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReceiptTelcoServiceImpl(
                ReceiptRepository receiptRepository,
                CompanyService companyService,
                FileService fileService,
                UserService userService,
                PaymentTypeService paymentTypeService,
                CpnCommonManagerService ccmService,
                TlcCpnCodeQueryService tlcCpnCodeQueryService,
                ReceiptServicePaymentRepository receiptServicePaymentRepository,
                RevenueTelcoRepository revenueTelcoRepository,
                LicCpnRepository licCpnRepository,
                LicBusinessTypeRepository licBusinessTypeRepository,
                NetworkTypeRepository networkTypeRepository,
                RevokeLicCpnRepository revokeLicCpnRepository,
                NoteRepository noteRepository,
                ReceiptServicePaymentHisRepository rspHisRepository,
                WarnDocRepository warnDocRepository,
                ObjectMapper objectMapper
    ) {
        this.receiptRepository = receiptRepository;
        this.companyService = companyService;
        this.fileService = fileService;
        this.userService = userService;
        this.paymentTypeService = paymentTypeService;
        this.ccmService = ccmService;
        this.tlcCpnCodeQueryService = tlcCpnCodeQueryService;
        this.receiptServicePaymentRepository = receiptServicePaymentRepository;
        this.revenueTelcoRepository = revenueTelcoRepository;
        this.licCpnRepository = licCpnRepository;
        this.licBusinessTypeRepository = licBusinessTypeRepository;
        this.networkTypeRepository = networkTypeRepository;
        this.revokeLicCpnRepository = revokeLicCpnRepository;
        this.noteRepository = noteRepository;
        this.rspHisRepository = rspHisRepository;
        this.warnDocRepository = warnDocRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Receipt getInfo(Long id) {
        Optional<Receipt> receipt = this.receiptRepository.findById(id);
        return receipt.orElse(null);
    }

    @Override
    public Receipt createReceipt(Principal principal, ReceiptDTO receiptDTO) {
        // check if receipt number existed
        if (receiptRepository.findReceiptByReceipNumber(receiptDTO.getReceipt_number()) != null) {
            return null;
        } else {
            Receipt receipt = new Receipt();
            PaymentType paymentType = this.paymentTypeService
                                            .findById(receiptDTO.getPayment_type_id());
            if (receiptDTO.getFile_id() != null) {
                File file = this.fileService.getOne(receiptDTO.getFile_id());
                receipt.setFile(file);
            }
            if ((receiptDTO.getFile_ids() != null) && (receiptDTO.getFile_ids().size() > 0)) {
                Set<File> files = this.fileService.findByListIds(receiptDTO.getFile_ids());
                receipt.setFiles(files);
            }
            //System.out.println("============ File id: " + file.getId());
            Company company = this.companyService.findById(receiptDTO.getCompany_id());
            System.out.println("============ Company id: " + company.getId());
            receipt.setPaymentType(paymentType);
            receipt.setCompany(company);
            receipt.setReceiptNumber(receiptDTO.getReceipt_number());
            receipt.setReceiptName(receiptDTO.getReceipt_name());
            receipt.setAmountOfMoney(receiptDTO.getAmount_of_money());
            receipt.setReceiptDate(receiptDTO.getReceipt_date());
            receipt.setCreateDate(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss"));
            receipt.setPayer(receiptDTO.getPayer());
            receipt.setReceiver(receiptDTO.getReceiver());
            receipt.setNote(receiptDTO.getNote());
            receipt.setReceiptDate(receiptDTO.getReceipt_date());
            receipt.setReceive_date(receiptDTO.getReceive_date());
            receipt.setIsDelete(0);

            return receiptRepository.save(receipt);
        }
    }

    @Override
    public void deleteReceipt(Principal principal, Long receiptId) {
        ///
        Receipt receipt = this.receiptRepository.findById(receiptId).orElse(null);

        if (receipt != null) {
            String updateTime = DateUtils.timeNow();
            List<ReceiptServicePayment> rsps = receiptServicePaymentRepository.findByReceipt(receipt);
            for (ReceiptServicePayment rsp : rsps) {
                ReceiptServicePaymentHis rspHis = new ReceiptServicePaymentHis();
                rspHis.setCompId(rsp.getCompId());
                rspHis.setCreateTime(rsp.getCreateTime());
                rspHis.setLicBusinessType(rsp.getLicBusinessType());
                rspHis.setLicCpn(rsp.getLicCpn());
                rspHis.setNetworkType(rsp.getNetworkType());
                rspHis.setNote(rsp.getNote());
                rspHis.setPaidMoney(rsp.getPaidMoney());
                rspHis.setQuarter(rsp.getQuarter());
                rspHis.setReceipt(rsp.getReceipt());
                rspHis.setState(rsp.getState());
                rspHis.setType(rsp.getType());
                rspHis.setUpdateTime(updateTime);
                rspHis.setYear(rsp.getYear());
                if (principal instanceof AbstractAuthenticationToken) {
                    UserDTO user = userService.getUserFromAuthentication((AbstractAuthenticationToken) principal);
                    System.out.println("================================ User: " + user.getFirstName() + " " + user.getLastName());
                    //rspHis.setUser(user.getFirstName() + " " + user.getLastName());
                    rspHis.setUser(user.getLastName() + " " + user.getFirstName());
                }

                rspHis.setAction("DELETE");

                rspHisRepository.save(rspHis);
            }
            receiptServicePaymentRepository.deleteByReceipt(receipt);

            receipt.setIsDelete(1);
            //receiptRepository.deleteById(receiptId);
            receiptRepository.save(receipt);
        }

    }

    @Override
    public Receipt updateReceipt(Principal principal, ReceiptDTO receiptDTO) throws Exception {
        Receipt receipt = this.receiptRepository.findById(receiptDTO.getReceipt_id()).orElse(null);
        if (receipt == null) {
            throw new Exception("Không tồn tại: " + receiptDTO.getReceipt_id());
        }

        if (receiptDTO.getPayment_type_id() != null) {
            PaymentType paymentType = this.paymentTypeService
                                            .findById(receiptDTO.getPayment_type_id());
            receipt.setPaymentType(paymentType);
        }

        if (receiptDTO.getFile_id() != null) {
            File file = this.fileService.getOne(receiptDTO.getFile_id());
            receipt.setFile(file);
        }
        Set<File> removedFiles = new HashSet<File>();
        if (receiptDTO.getFile_ids() != null) {
            //// Get remove files
            Set<File> oriFiles = receipt.getFiles();

            Set<File> files = this.fileService.findByListIds(receiptDTO.getFile_ids());
            for (File file : oriFiles) {
                if (!files.contains(file)) {
                    removedFiles.add(file);
                }
            }
            receipt.setFiles(files);
        }
        //System.out.println("============ File id: " + file.getId());
        if (receiptDTO.getCompany_id() != null) {
            Company company = this.companyService.findById(receiptDTO.getCompany_id());
            receipt.setCompany(company);
        }
        //System.out.println("============ Company id: " + company.getId());
        if (receiptDTO.getReceipt_number() != null) {
            receipt.setReceiptNumber(receiptDTO.getReceipt_number());
        }
        if (receiptDTO.getReceipt_name() != null) {
            receipt.setReceiptName(receiptDTO.getReceipt_name());
        }
        if (receiptDTO.getAmount_of_money() != null) {
            receipt.setAmountOfMoney(receiptDTO.getAmount_of_money());
        }
        if (receiptDTO.getReceipt_date() != null) {
            receipt.setReceiptDate(receiptDTO.getReceipt_date());
        }
        if (receiptDTO.getPayer() != null) {
            receipt.setPayer(receiptDTO.getPayer());
        }
        if (receiptDTO.getReceiver() != null) {
            receipt.setReceiver(receiptDTO.getReceiver());
        }
        if (receiptDTO.getNote() != null) {
            receipt.setNote(receiptDTO.getNote());
        }
        receipt.setUpdateDate(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss"));
        if (receiptDTO.getReceipt_date() != null) {
            receipt.setReceive_date(receiptDTO.getReceive_date());
        }
        receipt.setIsDelete(0);

        Receipt newReceipt = receiptRepository.save(receipt);

        /////// Remove Files
        for (File file : removedFiles) {
            System.out.println("Remove files: " + file.getId());
            try {
                this.fileService.deleteById(file.getId());
            } catch (Exception e) {
            }
        }

        return newReceipt;
    }

    @Override
    public List<Receipt> searchReceipt(SearchReceiptDTO searchReceiptDTO) {
        List<Company> companies = this.companyService.findByListIds(searchReceiptDTO.getCompany_ids());
        List<Receipt> receipts = null;
        if ((searchReceiptDTO.getReceipt_number() != null) && (!searchReceiptDTO.getReceipt_number().isEmpty())) {
            receipts = receiptRepository.search(companies,
                searchReceiptDTO.getReceipt_date() == null ? null : searchReceiptDTO.getReceipt_date().getFrom_date(),
                searchReceiptDTO.getReceipt_date() == null ? null : searchReceiptDTO.getReceipt_date().getTo_date(),
                searchReceiptDTO.getAmount_of_money() == null ? null : searchReceiptDTO.getAmount_of_money().getFrom(),
                searchReceiptDTO.getAmount_of_money() == null ? null : searchReceiptDTO.getAmount_of_money().getTo(),
                searchReceiptDTO.getReceipt_number(),
                searchReceiptDTO.getReceipt_name());
        } else {
            receipts = receiptRepository.searchMultipleReceiptNumbers(companies,
                searchReceiptDTO.getReceipt_date() == null ? null : searchReceiptDTO.getReceipt_date().getFrom_date(),
                searchReceiptDTO.getReceipt_date() == null ? null : searchReceiptDTO.getReceipt_date().getTo_date(),
                searchReceiptDTO.getAmount_of_money() == null ? null : searchReceiptDTO.getAmount_of_money().getFrom(),
                searchReceiptDTO.getAmount_of_money() == null ? null : searchReceiptDTO.getAmount_of_money().getTo(),
                searchReceiptDTO.getReceipt_numbers(),
                searchReceiptDTO.getReceipt_name());
        }

        return receipts;
    }

    @Override
    public void paymentReceipt(Principal principal, PaymentReceiptDTO paymentReceipt) throws IOException {
        List<ReceiptServicePayment> receiptPayments = new ArrayList<ReceiptServicePayment>();
        Receipt receipt = receiptRepository.findById(paymentReceipt.getReceipt_id()).get();

        String updateTime = DateUtils.timeNow();
        for (PaymentServiceDTO paymentServiceDTO : paymentReceipt.getPaymentServices()) {
            ReceiptServicePayment receiptServicePayment = new ReceiptServicePayment();
            receiptServicePayment.setReceipt(receipt);
            if (paymentServiceDTO.getBusiness_type_id() == 100) {
                /// Phí kho số
            } else {
                ////// Lic business type
                LicBusinessType licBusinessType = licBusinessTypeRepository.getOne(paymentServiceDTO.getBusiness_type_id());
                receiptServicePayment.setLicBusinessType(licBusinessType);
                NetworkType networkType = null;
                if (licBusinessType.getName().equalsIgnoreCase("Giấy phép cung cấp dịch vụ viễn thông")) {
                    ////// Network type
                    networkType = networkTypeRepository.getOne(paymentServiceDTO.getNetwork_id());
                    receiptServicePayment.setNetworkType(networkType);
                }

                ////// Lic company
                Company company = this.companyService.findById(paymentReceipt.getCompany_id());
                List<LicCpn> licCpns = null;
                if ((paymentServiceDTO.getType() != null) && (paymentServiceDTO.getType().equalsIgnoreCase("quarter"))) {
                    licCpns = licCpnRepository.search(Arrays.asList(company),
                        licBusinessType, networkType, paymentServiceDTO.getYear().intValue(),
                        paymentServiceDTO.getQuarter().intValue());
                } else {
                    licCpns = licCpnRepository.search(Arrays.asList(company),
                        licBusinessType, networkType, paymentServiceDTO.getYear().intValue());
                }
                System.out.println("========= Lic Size: " + licCpns.size());
                System.out.println("Year: " + paymentServiceDTO.getYear() + "\tBusiness: " + licBusinessType.getId() + "\tNetwork: " + (networkType == null ? null : networkType.getId()));
                if ((licCpns != null) && (licCpns.size() >= 1)) {
                    for (LicCpn licCpn : licCpns) {
                        Double fee = tlmFeeEstimate("year", licCpn, paymentServiceDTO.getYear());
                        if (fee > 0) {
                            receiptServicePayment.setLicCpn(licCpn);
                            break;
                        }
                    }
                } else {
                    System.out.println("Can not find right lic_number");
                    throw new IOException("Có lỗi khi thực hiện. Vui lòng kiểm tra lại thông tin. Xin cảm ơn!");
                }
            }

            receiptServicePayment.setYear(paymentServiceDTO.getYear());
            receiptServicePayment.setQuarter(paymentServiceDTO.getQuarter());
            receiptServicePayment.setPaidMoney(paymentServiceDTO.getPaid_money());
            receiptServicePayment.setCreateTime(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss"));
            if (paymentServiceDTO.getType() == null) {
                if ((paymentServiceDTO.getYear() != null) && (paymentServiceDTO.getQuarter() != null)) {
                    receiptServicePayment.setType("quarter");
                } else {
                    receiptServicePayment.setType("year");
                }
            } else {
                receiptServicePayment.setType(paymentServiceDTO.getType());
            }
            if (receipt.getCompany() != null) {
                receiptServicePayment.setCompId(receipt.getCompany().getId());
            }

            receiptPayments.add(receiptServicePayment);
        }

        List<ReceiptServicePayment> rsps = receiptServicePaymentRepository.saveAll(receiptPayments);

        for (ReceiptServicePayment rsp : rsps) {
            ReceiptServicePaymentHis rspHis = new ReceiptServicePaymentHis();
            rspHis.setCompId(rsp.getCompId());
            rspHis.setCreateTime(rsp.getCreateTime());
            rspHis.setLicBusinessType(rsp.getLicBusinessType());
            rspHis.setLicCpn(rsp.getLicCpn());
            rspHis.setNetworkType(rsp.getNetworkType());
            rspHis.setNote(receipt.getNote());
            rspHis.setPaidMoney(rsp.getPaidMoney());
            rspHis.setQuarter(rsp.getQuarter());
            rspHis.setReceipt(rsp.getReceipt());
            rspHis.setState(rsp.getState());
            rspHis.setType(rsp.getType());
            rspHis.setUpdateTime(updateTime);
            rspHis.setYear(rsp.getYear());
            //Huytq
            rspHis.setAmountOfMoney(receipt.getAmountOfMoney());
            rspHis.setReceiptDate(receipt.getReceiptDate());
            rspHis.setReceiptName(receipt.getReceiptName());
            rspHis.setReceiptNumber(receipt.getReceiptNumber());
            rspHis.setPayment_type_ids(receipt.getPaymentType().getId());
            rspHis.setPayer(receipt.getPayer());
            rspHis.setReceiver(receipt.getReceiver());
            rspHis.setReceive_date(receipt.getReceive_date());
            rspHis.setReceiptDate(receipt.getReceiptDate());
            rspHis.setReceiptContent(receipt.getReceiptContent());
            rspHis.setCompId(receipt.getCompany().getId());
            List<Long> fileIds = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            new ArrayList<>(receipt.getFiles()).stream().forEach(a -> {
                fileIds.add(a.getId());
                fileNames.add(a.getName());
            });
            rspHis.setFile_id(objectMapper.writeValueAsString(fileIds));
            rspHis.setFile_name(objectMapper.writeValueAsString(fileNames));
            if (principal instanceof AbstractAuthenticationToken) {
                UserDTO user = userService.getUserFromAuthentication((AbstractAuthenticationToken) principal);
                System.out.println("================================ User: " + user.getFirstName() + " " + user.getLastName());
                rspHis.setUser(user.getLastName() + " " + user.getFirstName());
            }
            rspHisRepository.save(rspHis);
        }
    }

    @Override
    public Page<CompanyPaymentReceipt> searchPayment(SearchReceiptDTO search) {
        Long pageIdx = 0L;
        Long pageSize = 20L;
        if (search.getIndex()!=null) pageIdx = search.getIndex();
        if (search.getSize()!=null) pageSize = search.getSize();
        List<Company> companies = this.companyService.findByListIds(search.getCompany_ids());
        List<LicBusinessType> licBusinessTypes = licBusinessTypeRepository.findByListIds(search.getBusiness_type_ids());
        List<PaymentType> paymentTypes = this.paymentTypeService.findByListIds(search.getPayment_type_ids());
        //uyen
        if (search.getReceipt_date() == null) {
            search.setReceipt_date(new RangeDTO());
        }
        if (search.getAmount_of_money() == null) {
            search.setAmount_of_money(new RangeDTO());
        }
        List<Receipt> receipts = receiptRepository.searchMultipleReceipt(companies,
            search.getReceipt_date().getFrom_date(), search.getReceipt_date().getTo_date(),
            search.getAmount_of_money().getFrom(), search.getAmount_of_money().getTo(),
            search.getReceipt_numbers(), search.getReceipt_name(), paymentTypes);

        HashMap<String, CompanyPaymentReceipt> hashMap = new HashMap<String, CompanyPaymentReceipt>();
        System.out.println("================ size: " + receipts.size());

        receipts.parallelStream().forEach(receipt -> {

//            if (receipts.indexOf(receipt) < (pageIdx * pageSize)) continue;
            List<ReceiptServicePayment> rcpSrvPayments = new ArrayList<ReceiptServicePayment>();
            if (search.getBusiness_type_ids().size() == 1 && search.getBusiness_type_ids().contains(100L)) {
                List<ReceiptServicePayment> tmps = receiptServicePaymentRepository.searchForPhiKhoSo(companies,
                    search.getYear(), search.getQuarter(),
                    search.getType(), receipt);
                rcpSrvPayments.addAll(tmps);
            } else {
                rcpSrvPayments = receiptServicePaymentRepository.search(companies,
                    licBusinessTypes,
                    search.getYear(), search.getQuarter(),
                    search.getType(), receipt);
                if (search.getBusiness_type_ids().contains(100L) || search.getBusiness_type_ids().size() == 0) {
                    List<ReceiptServicePayment> tmps = receiptServicePaymentRepository.searchForPhiKhoSo(companies,
                        search.getYear(), search.getQuarter(),
                        search.getType(), receipt);
                    rcpSrvPayments.addAll(tmps);
                }
            }

            PaymentReceitDetail pmrcpDetail = new PaymentReceitDetail();
            pmrcpDetail.setReceipt(receipt);

            String compName = receipt.getCompany().getName();
            CompanyPaymentReceipt cpnpmReceipt = hashMap.get(compName);
            if (cpnpmReceipt == null) {
                cpnpmReceipt = new CompanyPaymentReceipt();
                cpnpmReceipt.setCompany(receipt.getCompany());
            }

            HashMap<String, PaymentServiceDetail> hServiceDetail = new HashMap<String, PaymentServiceDetail>();

            for (ReceiptServicePayment rcpsrvPayment : rcpSrvPayments) {
                System.out.println(DateUtils.timeNow() + " ========== receipt service payment: " + rcpsrvPayment.getId());
                /////
                PaymentServiceDetail pmServiceDetail = new PaymentServiceDetail();

                if (rcpsrvPayment.getLicBusinessType() != null) {
                    pmServiceDetail.setBusiness_type_id(rcpsrvPayment.getLicBusinessType().getId());
                    pmServiceDetail.setBusiness_type_name(rcpsrvPayment.getLicBusinessType().getName());
                } else {
                    pmServiceDetail.setBusiness_type_id(100L);
                    pmServiceDetail.setBusiness_type_name("Phí Kho số");
                }
                if (rcpsrvPayment.getNetworkType() != null) {
                    pmServiceDetail.setNetwork_id(rcpsrvPayment.getNetworkType().getId());
                    pmServiceDetail.setNetwork_alias(rcpsrvPayment.getNetworkType().getName());
                }
                String serviceDetailName = null;
                if (rcpsrvPayment.getLicBusinessType() != null) {
                    serviceDetailName = receipt.getId() + "." + rcpsrvPayment.getLicBusinessType().getId();
                } else {
                    serviceDetailName = receipt.getId() + ".100";
                }
                if (hServiceDetail.get(serviceDetailName) != null) {
                    pmServiceDetail = hServiceDetail.get(serviceDetailName);
                }
                pmServiceDetail.setPaid_money(pmServiceDetail.getPaid_money() + rcpsrvPayment.getPaidMoney());

                // tuantv
                pmServiceDetail.setQuarter(rcpsrvPayment.getQuarter());
                pmServiceDetail.setYear(rcpsrvPayment.getYear());
                pmServiceDetail.setType(rcpsrvPayment.getType());

                hServiceDetail.put(serviceDetailName, pmServiceDetail);
            }

            hServiceDetail.forEach((k, v) -> {
                pmrcpDetail.getPaymentServices().add(v);
            });

            cpnpmReceipt.getPaymentReceitDetails().add(pmrcpDetail);
            hashMap.put(compName, cpnpmReceipt);
        });

        List<CompanyPaymentReceipt> cpnpmReceipts = new ArrayList<CompanyPaymentReceipt>();
        hashMap.forEach((k, v) -> {
            cpnpmReceipts.add(v);
        });

        Collections.sort(cpnpmReceipts);

        return new PageImpl<CompanyPaymentReceipt>(
            cpnpmReceipts,
            PageRequest.of(pageIdx.intValue(), pageSize.intValue()),
            receipts.size()
        );
    }

    @Override
    public List<PaymentReceitDetail> searchPaymentHis(Long receipt_id) {
        Receipt receipt = this.receiptRepository.getById(receipt_id);

        List<ReceiptServicePaymentHis> rspsHis = rspHisRepository.search(receipt);
        List<PaymentReceitDetail> paymentReceitDetails = new ArrayList<PaymentReceitDetail>();

        HashMap<String, PaymentReceitDetail> hReceiptDetail = new HashMap<String, PaymentReceitDetail>();

        for (ReceiptServicePaymentHis rpsPayment : rspsHis) {
            PaymentReceitDetail paymentReceitDetail = new PaymentReceitDetail();
            paymentReceitDetail.setReceipt(receipt);
            paymentReceitDetail.setCreateDate(rpsPayment.getUpdateTime());
            paymentReceitDetail.setUser(rpsPayment.getUser());

            String key = receipt.getId() + "." + rpsPayment.getUpdateTime();
            if (hReceiptDetail.containsKey(key)) {
                paymentReceitDetail = hReceiptDetail.get(key);
            }

            PaymentServiceDetail pmServiceDetail = new PaymentServiceDetail();

            /////// Get Revenue Telco
            System.out.println("Company_id: " + receipt.getCompany().getId());
            System.out.println("Year: " + rpsPayment.getYear());
            System.out.println("Quarter: " + rpsPayment.getQuarter());
            System.out.println("Type: " + rpsPayment.getType());
            String type = rpsPayment.getType();

            if (rpsPayment.getLicBusinessType() != null) {
                pmServiceDetail.setBusiness_type_id(rpsPayment.getLicBusinessType().getId());
                pmServiceDetail.setBusiness_type_name(rpsPayment.getLicBusinessType().getName());
            }
            if (rpsPayment.getNetworkType() != null) {
                pmServiceDetail.setNetwork_id(rpsPayment.getNetworkType().getId());
                pmServiceDetail.setNetwork_alias(rpsPayment.getNetworkType().getAlias());
            }
            pmServiceDetail.setPaid_money(rpsPayment.getPaidMoney());
            pmServiceDetail.setYear(rpsPayment.getYear());
            pmServiceDetail.setQuarter(rpsPayment.getQuarter());
            pmServiceDetail.setType(rpsPayment.getType());
            pmServiceDetail.setNeed_pay(rpsPayment.getNeed_pay());
            pmServiceDetail.setOwned_money(rpsPayment.getOwned_money());
//            HUyTQ
            pmServiceDetail.setAmount_of_money(rpsPayment.getAmountOfMoney());
            pmServiceDetail.setNote(rpsPayment.getNote());
            pmServiceDetail.setPayment_type_id(rpsPayment.getPayment_type_ids());
            pmServiceDetail.setFile_id(rpsPayment.getFile_id());
            pmServiceDetail.setFile_name(rpsPayment.getFile_name());
            pmServiceDetail.setPayer(rpsPayment.getPayer());
            pmServiceDetail.setReceipt_name(rpsPayment.getReceiptName());
            pmServiceDetail.setReceipt_number(rpsPayment.getReceiptNumber());
            pmServiceDetail.setReceipt_date(rpsPayment.getReceiptDate());
            pmServiceDetail.setReceive_date(rpsPayment.getReceive_date());
            pmServiceDetail.setReceiver(rpsPayment.getReceiver());
            pmServiceDetail.setReceipt_content(rpsPayment.getReceiptContent());
            pmServiceDetail.setCompany_id(rpsPayment.getCompId());
//            pmServiceDetail.setOwned_money(needPaytotal - rpsPayment.getPaidMoney());

            paymentReceitDetail.getPaymentServices().add(pmServiceDetail);

            hReceiptDetail.put(key, paymentReceitDetail);
        }
        TreeMap<String, PaymentReceitDetail> sorted = new TreeMap<>();
        sorted.putAll(hReceiptDetail);
        sorted.forEach((k, v) -> {
            paymentReceitDetails.add(v);
        });

        return paymentReceitDetails;
    }

    @Override
    public CompanyPaymentReceipt infoPayment(Long receipt_id) {
        CompanyPaymentReceipt cpnpmReceipt = new CompanyPaymentReceipt();
        Receipt receipt = this.receiptRepository.findById(receipt_id).orElse(null);

        if (receipt == null) {
            throw new BadRequestAlertException("Receipt by id " + receipt_id + " was not found");
        }

        cpnpmReceipt.setCompany(receipt.getCompany());
        PaymentReceitDetail pmReceiptDetail = new PaymentReceitDetail();
        pmReceiptDetail.setReceipt(receipt);
        cpnpmReceipt.getPaymentReceitDetails().add(pmReceiptDetail);

        List<ReceiptServicePayment> rpsPayments = receiptServicePaymentRepository.findByReceipt(receipt);
        for (ReceiptServicePayment rpsPayment : rpsPayments) {
            PaymentServiceDetail pmServiceDetail = new PaymentServiceDetail();

            /////// Get Revenue Telco
            System.out.println("Company_id: " + receipt.getCompany().getId());
            System.out.println("Year: " + rpsPayment.getYear());
            System.out.println("Quarter: " + rpsPayment.getQuarter());
            System.out.println("Type: " + rpsPayment.getType());
            String type = rpsPayment.getType();
            List<RevenueTelco> revenueTelcos = null;

            Long needPaytotal = null;
            Long revenueTotal = null;
            if (rpsPayment.getLicBusinessType() == null) {
                // Phí kho số
                TlcCpnCodeCriteria criteria = new TlcCpnCodeCriteria();
                LongFilter companyIdFilter = new LongFilter();
                companyIdFilter.setEquals(rpsPayment.getCompId());
                criteria.setCompanyId(companyIdFilter);
                List<CurrentCodeFeeResponse> ccfResps = tlcCpnCodeQueryService.getCurrentCodeFeeReport(criteria, rpsPayment.getYear().intValue(),
                    rpsPayment.getQuarter().intValue(), 0, null, null);
                needPaytotal = 0L;
                for (CurrentCodeFeeResponse ccfResponse : ccfResps) {
                    if ((ccfResponse.getCompanyId() != null) && (ccfResponse.getCompanyId().intValue() == rpsPayment.getCompId().intValue())) {
                        needPaytotal = ccfResponse.getCurQuarterFee();
                        break;
                    }
                }
            } else {
                if ((type != null) && (type.equalsIgnoreCase("quarter"))) {
                    revenueTelcos = revenueTelcoRepository.search(receipt.getCompany(),
                        Arrays.asList(rpsPayment.getLicCpn()), rpsPayment.getYear(),
                        Arrays.asList(rpsPayment.getQuarter()), rpsPayment.getType());
                } else {
                    revenueTelcos = revenueTelcoRepository.search(receipt.getCompany(),
                        Arrays.asList(rpsPayment.getLicCpn()), rpsPayment.getYear(),
                        null, rpsPayment.getType());
                    if ((revenueTelcos == null) || (revenueTelcos.size() < 1)) {
                        revenueTelcos = revenueTelcoRepository.search(receipt.getCompany(),
                            Arrays.asList(rpsPayment.getLicCpn()), rpsPayment.getYear(),
                            Arrays.asList(1L, 2L, 3L, 4L), rpsPayment.getType());
                    }
                }

                for (RevenueTelco rvn : revenueTelcos) {
                    if (rvn.getRevenue() != null) {
                        if (revenueTotal != null) {
                            revenueTotal += rvn.getRevenue();
                        } else {
                            revenueTotal = rvn.getRevenue();
                        }
                    }
                }
                // Phí CCDV - TLM
                Double needPay = serviceFeeEstimate(type, rpsPayment.getLicBusinessType().getName(),
                    rpsPayment.getLicCpn(), rpsPayment.getYear(), revenueTelcos);
                if (needPay != null) {
                    needPaytotal = Math.round(needPay);
                }
            }

            pmServiceDetail.setRevenue(revenueTotal);
            pmServiceDetail.setNeed_pay(needPaytotal);
            if (rpsPayment.getLicBusinessType() != null) {
                pmServiceDetail.setBusiness_type_id(rpsPayment.getLicBusinessType().getId());
                pmServiceDetail.setBusiness_type_name(rpsPayment.getLicBusinessType().getName());
            } else {
                pmServiceDetail.setBusiness_type_id(100L);
                pmServiceDetail.setBusiness_type_name("Phí Kho số");
            }
            if (rpsPayment.getNetworkType() != null) {
                pmServiceDetail.setNetwork_id(rpsPayment.getNetworkType().getId());
                pmServiceDetail.setNetwork_alias(rpsPayment.getNetworkType().getAlias());
            }
            pmServiceDetail.setPaid_money(rpsPayment.getPaidMoney());
            pmServiceDetail.setYear(rpsPayment.getYear());
            pmServiceDetail.setQuarter(rpsPayment.getQuarter());
            pmServiceDetail.setType(rpsPayment.getType());
            if (needPaytotal != null) {
                pmServiceDetail.setOwned_money(needPaytotal - rpsPayment.getPaidMoney());
            } else {
                pmServiceDetail.setOwned_money(-rpsPayment.getPaidMoney());
            }

            pmReceiptDetail.getPaymentServices().add(pmServiceDetail);
        }

        return cpnpmReceipt;
    }

    @Override
    public TelcoServiceDTO serviceNeedPay(TelcoServiceDTO telcoServiceDTO) {
        Company company = this.companyService.findById(telcoServiceDTO.getCompany_id());
        LicBusinessType licBusinessType = licBusinessTypeRepository.getOne(telcoServiceDTO.getBusiness_type_id());
        NetworkType networkType = networkTypeRepository.getOne(telcoServiceDTO.getNetwork_id());
        List<LicCpn> licCpns = null;
        if ((telcoServiceDTO.getType() != null) && (telcoServiceDTO.getType().equalsIgnoreCase("quarter"))) {
            licCpns = licCpnRepository.search(Arrays.asList(company), licBusinessType, networkType,
                telcoServiceDTO.getYear().intValue(), telcoServiceDTO.getQuarter().intValue());
        } else {
            licCpns = licCpnRepository.search(Arrays.asList(company), licBusinessType, networkType,
                telcoServiceDTO.getYear().intValue());
        }

        if ((licCpns == null) || (licCpns.size() <= 0)) {
            return null;
        }

        double needPayTotal = 0L;
        List<RevenueTelco> revenueTelcos = new ArrayList<RevenueTelco>();
        for (LicCpn l : licCpns) {
            List<RevenueTelco> rvns = revenueTelcoRepository.search(company, Arrays.asList(l), telcoServiceDTO.getYear(),
                Arrays.asList(telcoServiceDTO.getQuarter()), telcoServiceDTO.getType());

            needPayTotal += serviceFeeEstimate(telcoServiceDTO.getType(), licBusinessType.getName(),
                l, telcoServiceDTO.getYear(), rvns);
        }

        telcoServiceDTO.setNeed_pay(Double.valueOf(needPayTotal).longValue());
        return telcoServiceDTO;
    }

    // tuantv
    // tính số tháng sử dụng (thời gian có hiệu lực) của giấy phép trong năm bất kỳ
    // @param licCpn giấy phép
    // @param year năm cần tính
    // @return số tháng đc sử dụng
    private int soThangSuDungCuaGiayPhepTrongNam (
        LicCpn licCpn,
        int year
    ) {
        if (licCpn == null) return -1;

        Date createDate = Date.from(licCpn.getLicCreatedDate());
        Date ExpiredDate = Date.from(licCpn.getExpiredDate());
        Date ExpiredTime = Date.from(licCpn.getExpiredTime());

        Date endDate = ExpiredDate.before(ExpiredTime) ? ExpiredDate : ExpiredTime;

        int startMonth = (DateUtils.getYear(createDate) < year) ?
            year * 12 : DateUtils.getYear(createDate) * 12 + DateUtils.getMonth(createDate);

        int endMonth = (DateUtils.getYear(endDate) <= year) ?
            DateUtils.getYear(endDate) * 12 + DateUtils.getMonth(endDate) : year * 12 + 12;

        return Math.max((endMonth - startMonth), 0);
    }

    // tuantv
    // tính phí ccdvvt theo năm
    // @param licCpn giấy phép
    // @param year năm cần tính
    // @param revenueTelcos danh sách BCDT
    // @return số phí ước tính
    private double ccdvvtFeeEstimateByYear (
        LicCpn licCpn,
        Long year,
        List<RevenueTelco> revenueTelcos
    ) {

        if (licCpn == null || year == null) return -1; // error

        // tính phí dựa trên BCDT
        double feeByBCDT = 0;
        if (revenueTelcos != null) {
            for (RevenueTelco revenueTelco : revenueTelcos) {
                if (revenueTelco.getRevenue() != null) {
                    feeByBCDT += revenueTelco.getRevenue() * 0.5 / 100;
                }
            }
        }

        int months = soThangSuDungCuaGiayPhepTrongNam(licCpn, year.intValue());

        // công thức dựa vào hàm ccdvvtFeeEstimate(..., List<>)
        // copy 99%
        String networkNames = "";
        Set<NetworkType> networkTypes = licCpn.getNetworkTypes();

        for (NetworkType networkType : networkTypes) {
            networkNames += networkType.getName().toLowerCase();
        }
        if (networkNames.contains("di động") && networkNames.contains("mặt đất")) {
            //// check băng tần
            if (licCpn.getLicResources() != null) {
                Set<LicResource> licResources = licCpn.getLicResources();
                for (LicResource licResource : licResources) {
                    if (licResource.getName().equalsIgnoreCase("Băng tần vô tuyến điện")) {
                        return Math.max(months * 2000000000D / 12, feeByBCDT);
                    }
                }
            }
            return Math.max(months * 250000000D / 12, feeByBCDT);
        } else if (networkNames.contains("di động") && networkNames.contains("vệ tinh")) {
            return Math.max(months * 100000000D / 12, feeByBCDT);
        } else if (networkNames.contains("di động") && networkNames.contains("hàng hải")) {
            return Math.max(months * 50000000D / 12, feeByBCDT);
        } else if (networkNames.contains("di động") && networkNames.contains("hàng không")) {
            return Math.max(months * 50000000D / 12, feeByBCDT);
        } else if (networkNames.contains("cố định") && networkNames.contains("mặt đất")) {
            //// check băng tần
            if (licCpn.getLicResources() != null) {
                Set<LicResource> licResources = licCpn.getLicResources();
                for (LicResource licResource : licResources) {
                    if (licResource.getName().equalsIgnoreCase("Băng tần vô tuyến điện")) {
                        return Math.max(months * 1000000000D / 12, feeByBCDT);
                    }
                }
            }
            return Math.max(months * 250000000D / 12, feeByBCDT);
        } else if (networkNames.contains("cố định") && networkNames.contains("vệ tinh")) {
            return Math.max(months * 50000000D / 12, feeByBCDT);
        }

        return feeByBCDT;
    }

    // tuantv
    // tính phí thiết lập mạng theo năm
    // @param licCpn giấy phép
    // @param year năm cần tính
    // @return số phí ước tính
    private double tlmFeeEstimateByYear (
        LicCpn licCpn,
        Long year
    ) {

        if (licCpn == null || year == null) return -1; // error

        int months = soThangSuDungCuaGiayPhepTrongNam(licCpn, year.intValue());

        // công thức tính phí theo số tháng sử dụng và loại dịch vụ
        // đã tham khảo từ hàm tlmFeeEstimate(String, LicCpn, Long)
        String networkNames = "";
        Set<NetworkType> networkTypes = licCpn.getNetworkTypes();

        for (NetworkType networkType : networkTypes) {
            networkNames += "." + networkType.getName().toLowerCase();
        }
        if (networkNames.contains("di động") && networkNames.contains("mặt đất")) {
            //// check băng tần
            if ((licCpn.getLicResources() != null) && (licCpn.getLicResources().size() > 0)) {
                Set<LicResource> licResources = licCpn.getLicResources();
                double max = 0.0;
                for (LicResource licResource : licResources) {
                    if (licResource.getName().equalsIgnoreCase("Băng tần vô tuyến điện")
                    ) {
                        double fee = months * 5000000000D / 12;
                        if (max < fee) {
                            max = fee;
                        }
                    } else if (licResource.getName().equalsIgnoreCase("Kênh tần số vô tuyến điện") || licResource.getName().equalsIgnoreCase("Số thuê bao viễn thông")) {
                        double fee = months * 200000000D / 12;
                        if (max < fee) {
                            max = fee;
                        }
                    }
                }
                return max;
                //return months * 2000000000D/12;
            } else {
                return months * 2000000000D / 12;
            }
        } else if (networkNames.contains("di động") && networkNames.contains("vệ tinh")) {
            return months * 100000000D / 12;
        } else if (networkNames.contains("cố định") && networkNames.contains("mặt đất")) {
            if ((licCpn.getLicResources() != null) && (licCpn.getLicResources().size() > 0)) {
                Set<LicResource> licResources = licCpn.getLicResources();
                double max = 0.0;
                for (LicResource licResource : licResources) {
                    if (licResource.getName().equalsIgnoreCase("Băng tần vô tuyến điện")
                        || licResource.getName().equalsIgnoreCase("Số thuê bao viễn thông")) {
                        // Có sử dụng băng tần hoặc thuê bao viễn thông
                        int provinceNumbers = 63;
                        if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                            provinceNumbers = licCpn.getProvinces().size();
                        }
                        if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                            double fee = months * 800000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else if (provinceNumbers <= 1) {
                            max = 0.0;
                        }else {
                            double fee = months * 2000000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        }
                    } else {
                        int provinceNumbers = 63;
                        if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                            provinceNumbers = licCpn.getProvinces().size();
                        }
                        if (provinceNumbers <= 1) {
                            double fee = months * 100000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                            double fee = months * 300000000L / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else {
                            double fee = months * 600000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        }
                    }
                }
                return max;
            } else {
                int provinceNumbers = 63;
                if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                    provinceNumbers = licCpn.getProvinces().size();
                }
                if (provinceNumbers <= 1) {
                    return months * 100000000D / 12;
                } else if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                    return months * 300000000D / 12;
                } else {
                    return months * 600000000D / 12;
                }
            }
        } else if (networkNames.contains("cố định") && networkNames.contains("vệ tinh")) {
            return months * 100000000D / 12;
        }

        return 0L;

    }

    ///// Tính phí thiết lập mạng trước ngày 01/01
    @Override
    public double tlmFee0101Estimate(String type, LicCpn licCpn, Long year) {
        Instant startYear = Instant.parse(year + "-01-01T23:59:59.00Z");
        int startYearset = licCpn.getLicCreatedDate().compareTo(startYear);
        if (startYearset > 0) {
            return 0;
        }
        if (startYearset < -1) {
            return 0;
        }

        /// License from previous year
        Date startDate = Date.from(licCpn.getLicCreatedDate());
//        Date endDate = Date.from(licCpn.getExpiredDate());
        Date endDate = Date.from(licCpn.getExpiredTime());
        //////// Check if license is revoked
        List<RevokeLicCpn> revokeLicCpns = revokeLicCpnRepository.search(licCpn);
        if ((revokeLicCpns != null) && (revokeLicCpns.size() >= 1)) {
            if (licCpn.getExpiredTime().compareTo(revokeLicCpns.get(0).getIssuedDate()) > 0) {
                endDate = Date.from(revokeLicCpns.get(0).getIssuedDate());
            }
        }
        int startMonth = (1900 + startDate.getYear() < year) ? 0 : (startDate.getMonth() + 1);
        int endMonth = (1900 + endDate.getYear() > year) ? 12 : (endDate.getMonth() + 1);
        int months = 0;
//        if(startMonth > 0) {
//            months = endMonth - startMonth - 1;
//        } else {
        months = endMonth - startMonth;
//        }

        String networkNames = "";
        Set<NetworkType> networkTypes = licCpn.getNetworkTypes();

        for (NetworkType networkType : networkTypes) {
            networkNames += "." + networkType.getName().toLowerCase();
        }
        if (networkNames.contains("di động") && networkNames.contains("mặt đất")) {
            //// check băng tần
            if ((licCpn.getLicResources() != null) && (licCpn.getLicResources().size() > 0)) {
                Set<LicResource> licResources = licCpn.getLicResources();
                double max = 0.0;
                Boolean flag = false;
                for (LicResource licResource : licResources) {
                    if (licResource.getName().equalsIgnoreCase("Băng tần vô tuyến điện")) {
                        flag = true;
                        double fee = months * 5000000000D / 12;
                        if (max < fee) {
                            max = fee;
                        }
                    } else if (licResource.getName().equalsIgnoreCase("Kênh tần số vô tuyến điện")) {
                        flag = true;
                        double fee = months * 200000000D / 12;
                        if (max < fee) {
                            max = fee;
                        }
                    }
                }
                if (!flag) max = months * 2000000000D / 12;
                return max;
            } else {
                return months * 2000000000D / 12;
            }
        } else if (networkNames.contains("di động") && networkNames.contains("vệ tinh")) {
            return months * 100000000D / 12;
        } else if (networkNames.contains("cố định") && networkNames.contains("mặt đất")) {
            if ((licCpn.getLicResources() != null) && (licCpn.getLicResources().size() > 0)) {
                Set<LicResource> licResources = licCpn.getLicResources();
                for (LicResource licResource : licResources) {
                    double max = 0.0;
                    if (licResource.getName().equalsIgnoreCase("Băng tần vô tuyến điện")
                        || licResource.getName().equalsIgnoreCase("Số thuê bao viễn thông")) {
                        // Có sử dụng băng tần hoặc thuê bao viễn thông
                        int provinceNumbers = 63;
                        if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                            provinceNumbers = licCpn.getProvinces().size();
                        }
                        if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                            double fee = months * 800000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else if (provinceNumbers <= 1) {
                            double fee = months * 0D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        }else {
                            double fee = months * 2000000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        }
                        return max;
                    } else {
                        int provinceNumbers = 63;
                        if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                            provinceNumbers = licCpn.getProvinces().size();
                        }
                        if (provinceNumbers <= 1) {
                            double fee = months * 100000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                            double fee = months * 300000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else {
                            double fee = months * 600000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        }
                        return max;
                    }
                }
            } else {
                int provinceNumbers = 63;
                if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                    provinceNumbers = licCpn.getProvinces().size();
                }
                if (provinceNumbers <= 1) {
                    return months * 100000000D / 12;
                } else if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                    return months * 300000000D / 12;
                } else {
                    return months * 600000000D / 12;
                }
            }
        } else if (networkNames.contains("cố định") && networkNames.contains("vệ tinh")) {
            return months * 100000000D / 12;
        }

        return 0L;
    }

    @Override
    public double tlmFeeEstimate(String type, LicCpn licCpn, Long year) {
        if (licCpn.getLicCreatedDate().compareTo(Instant.now()) > 0) {
            return 0;
        }
//        Date endDate = ExpiredDate.before(ExpiredTime) ? ExpiredDate : ExpiredTime;
//        if (endDate.before(CurrentStart)) return 0L;
        Date startDate = Date.from(licCpn.getLicCreatedDate());
//        Date endDate = Date.from(licCpn.getExpiredDate());
        Date endDate = Date.from(licCpn.getExpiredTime());
        ///// Check if having related license
        List<LicCpn> relations = licCpnRepository.findRelated(licCpn);
        if ((relations != null) && (relations.size() > 0)) {
            if (relations.get(0).getLicCreatedDate().compareTo(Instant.now()) <= 0) {
                endDate = Date.from(licCpn.getExpiredTime());
            }
        }
        //////// Check if license is revoked
        List<RevokeLicCpn> revokeLicCpns = revokeLicCpnRepository.search(licCpn);
        if ((revokeLicCpns != null) && (revokeLicCpns.size() >= 1)) {
            if (licCpn.getExpiredTime().compareTo(revokeLicCpns.get(0).getIssuedDate()) > 0) {
                endDate = Date.from(revokeLicCpns.get(0).getIssuedDate());
            }
        }
        int startMonth = (1900 + startDate.getYear() < year) ? 0 : (startDate.getMonth() + 1);
        int endMonth = (1900 + endDate.getYear() > year) ? 12 : (endDate.getMonth() + 1);
        int months = 0;
//        if(startMonth > 0) {
//             months = endMonth - startMonth - 1 ;
//        } else {
        months = endMonth - startMonth;
//        }


        System.out.println("LicCpn: " + licCpn.getLicNumber() + "\tMonths: " + months + "\tat: " + year);

        String networkNames = "";
        Set<NetworkType> networkTypes = licCpn.getNetworkTypes();

        for (NetworkType networkType : networkTypes) {
            networkNames += "." + networkType.getName().toLowerCase();
        }
        if (networkNames.contains("di động") && networkNames.contains("mặt đất")) {
            //// check băng tần
            if ((licCpn.getLicResources() != null) && (licCpn.getLicResources().size() > 0)) {
                Set<LicResource> licResources = licCpn.getLicResources();
                double max = 0.0;
                Boolean flag = false;
                for (LicResource licResource : licResources) {
                    if (licResource.getName().equalsIgnoreCase("Băng tần vô tuyến điện")) {
                        flag = true;
                        double fee = months * 5000000000D / 12;
                        if (max < fee) {
                            max = fee;
                        }
                    } else if (licResource.getName().equalsIgnoreCase("Kênh tần số vô tuyến điện") || licResource.getName().equalsIgnoreCase("Số thuê bao viễn thông")) {
                        flag = true;
                        double fee = months * 200000000D / 12;
                        if (max < fee) {
                            max = fee;
                        }
                    }
                }
                if (!flag) max = months * 2000000000D / 12;
                return max;
            } else {
                return months * 2000000000D / 12;
            }
        } else if (networkNames.contains("di động") && networkNames.contains("vệ tinh")) {
            return months * 100000000D / 12;
        } else if (networkNames.contains("cố định") && networkNames.contains("mặt đất")) {
            if ((licCpn.getLicResources() != null) && (licCpn.getLicResources().size() > 0)) {
                Set<LicResource> licResources = licCpn.getLicResources();
                double max = 0.0;
                for (LicResource licResource : licResources) {
                    if (licResource.getName().equalsIgnoreCase("Băng tần vô tuyến điện")
                        || licResource.getName().equalsIgnoreCase("Số thuê bao viễn thông")) {
                        // Có sử dụng băng tần hoặc thuê bao viễn thông
                        int provinceNumbers = 63;
                        if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                            provinceNumbers = licCpn.getProvinces().size();
                        }
                        if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                            double fee = months * 800000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        }else if (provinceNumbers <= 1) {
                            double fee = months * 0D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else {
                            double fee = months * 2000000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        }
                    } else {
                        int provinceNumbers = 63;
                        if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                            provinceNumbers = licCpn.getProvinces().size();
                        }
                        if (provinceNumbers <= 1) {
                            double fee = months * 100000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                            double fee = months * 300000000L / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        } else {
                            double fee = months * 600000000D / 12;
                            if (max < fee) {
                                max = fee;
                            }
                        }
                    }
                }
                return max;
            } else {
                int provinceNumbers = 63;
                if ((licCpn.getProvinces() != null) && (licCpn.getProvinces().size() > 0)) {
                    provinceNumbers = licCpn.getProvinces().size();
                }
                if (provinceNumbers <= 1) {
                    return months * 100000000D / 12;
                } else if ((provinceNumbers >= 2) && (provinceNumbers <= 30)) {
                    return months * 300000000D / 12;
                } else {
                    return months * 600000000D / 12;
                }
            }
        } else if (networkNames.contains("cố định") && networkNames.contains("vệ tinh")) {
            return months * 100000000D / 12;
        }

        return 0L;
    }

    private Double serviceFeeEstimate(String type, String feeName, LicCpn licCpn, Long year, List<RevenueTelco> revenueTelcos) {
        if ((type == null) || (type.isEmpty())) {
            type = "quarter";
        }
        switch (feeName) {
            case "Giấy phép cung cấp dịch vụ viễn thông":
                if (type.equalsIgnoreCase("year")) {
                    // longth
//                    return ccdvvtFeeEstimate(type, licCpn, year, revenueTelcos);
                    // tuantv
                    return ccdvvtFeeEstimateByYear(licCpn, year, revenueTelcos);
                } else {
                    Double needPay = null;
                    for (RevenueTelco rvn : revenueTelcos) {
                        if (rvn.getRevenue() != null) {
                            if (needPay == null) {
                                needPay = new Double(rvn.getRevenue() * 0.5 / 100);
                            } else {
                                needPay += new Double(rvn.getRevenue() * 0.5 / 100);
                            }
                        }
                    }
                    return needPay;
                }

            case "Giấy phép thiết lập mạng viễn thông công cộng":
//                return tlmFeeEstimate(type, licCpn, year);
                // tuantv
                return tlmFeeEstimateByYear(licCpn, year);
        }
        return null;
    }

    RespPaidFeeDTO searchTypeServices(Company company, String name, Long year, Long quarter, String type) {
        LicBusinessType lbt = licBusinessTypeRepository.findAllByName(name);

        List<LicCpn> licCpns = null;
        if ((type != null) && (type.equalsIgnoreCase("year"))) {
            licCpns = licCpnRepository.search(company, lbt, year.intValue());
        } else {
            licCpns = licCpnRepository.search(company, lbt, year.intValue(), quarter.intValue());
        }

        if ((licCpns == null) || (licCpns.size() <= 0)) {
            return null;
        }
        RespPaidFeeDTO respPaidFeeDTO = new RespPaidFeeDTO();
        respPaidFeeDTO.setBusiness_type_id(lbt.getId());
        respPaidFeeDTO.setBusiness_type_name(lbt.getName());

        for (LicCpn licCpn : licCpns) {
            Iterator<LicBusinessType> iterator = licCpn.getLicBusinessTypes().iterator();
            while (iterator.hasNext()) {
                LicBusinessType element = iterator.next();
                if (element.getName().equalsIgnoreCase(name)) {
                    for (NetworkType networkType : licCpn.getNetworkTypes()) {
                        RespPaidServiceDTO paidService = new RespPaidServiceDTO();

                        if (networkType.getAlias() != null) {
                            paidService.setNetwork_id(networkType.getId());
                            paidService.setNetwork_alias(networkType.getAlias());
                            List<RevenueTelco> revenueTelcos = revenueTelcoRepository.search(company, Arrays.asList(licCpn),
                                year, Arrays.asList(quarter), type);
                            Double needPay = serviceFeeEstimate(type, lbt.getName(), licCpn, year, revenueTelcos);
                            List<ReceiptServicePayment> receiptServicePayments = receiptServicePaymentRepository.search(
                                licCpn, year, quarter, type);

                            Long paidMoney = 0L;
                            for (ReceiptServicePayment rsp : receiptServicePayments) {
                                System.out.println("LicCpn: " + licCpn.getId() + "\tPaid: " + rsp.getPaidMoney());
                                paidMoney += rsp.getPaidMoney();
                            }
                            if (needPay != null) {
                                paidService.setNeed_pay(Math.round(needPay - paidMoney));
                            }

                            respPaidFeeDTO.getNetworks().add(paidService);
                        }
                    }
                }
            }
        }

        return respPaidFeeDTO;
    }

    RespPaidFeeDTO searchPaidTypeServices(Company company, LicBusinessType lbt, Long year, Long quarter, String type) {
        List<LicCpn> licCpns = null;
        if ((type != null) && (type.equalsIgnoreCase("quarter"))) {
            licCpns = licCpnRepository.search(company, lbt, year.intValue(), quarter.intValue());
        } else {
            licCpns = licCpnRepository.search(company, lbt, year.intValue());
        }

        if ((licCpns == null) || (licCpns.size() <= 0)) {
            return null;
        }
        RespPaidFeeDTO respPaidFeeDTO = new RespPaidFeeDTO();
        respPaidFeeDTO.setBusiness_type_id(lbt.getId());
        respPaidFeeDTO.setBusiness_type_name(lbt.getName());

        switch (lbt.getName()) {
            case "Giấy phép cung cấp dịch vụ viễn thông":
                if (quarter == null) {
                    type = "year";
                }
                HashMap<String, RespPaidServiceDTO> hServices = new HashMap<String, RespPaidServiceDTO>();
                for (LicCpn licCpn : licCpns) {
                    String networkAlias = "";
                    RespPaidServiceDTO rPaidService = new RespPaidServiceDTO();

                    Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
                    for (NetworkType nwt : networkTypes) {
                        if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                            rPaidService.setNetwork_id(nwt.getId());
                            rPaidService.setNetwork_alias(nwt.getAlias());
//                            rPaidService.setNeed_pay(0L);
                            networkAlias = nwt.getAlias();
                        }
                    }
                    if (networkAlias.isEmpty()) {
                        continue;
                    }
                    if (hServices.containsKey(networkAlias)) {
                        rPaidService = hServices.get(networkAlias);
                    }
                    Double needPay = null;
                    List<RevenueTelco> revenueTelcos = revenueTelcoRepository.search(company, Arrays.asList(licCpn),
                        year, Arrays.asList(quarter), type);
                    System.out.println("Revenue size: " + revenueTelcos.size());
                    Double tmpNeedPaid = serviceFeeEstimate(type, lbt.getName(), licCpn, year, revenueTelcos);
                    if (tmpNeedPaid != null) {
                        needPay = tmpNeedPaid;
                    }
                    System.out.println(type + " ======== CCDVVT LicCpn: " + licCpn.getLicNumber() + " = " + tmpNeedPaid + " at " + year + "\tfor:" + company.getId());

                    List<ReceiptServicePayment> receiptServicePayments = null;
                    if ((type != null) && (type.equalsIgnoreCase("year"))) {
                        receiptServicePayments = receiptServicePaymentRepository.search(licCpn, company.getId(), lbt,
                            year, null, null);
                    } else if (type.equalsIgnoreCase("quarter")) {
                        receiptServicePayments = receiptServicePayments = receiptServicePaymentRepository.search(
                            licCpn, year, quarter, type);
                    }

                    Long paidMoney = 0L;
                    for (ReceiptServicePayment rsp : receiptServicePayments) {
                        System.out.println("LicCpn: " + licCpn.getId() + "\tPaid: " + rsp.getPaidMoney());
                        paidMoney += rsp.getPaidMoney();
                    }
                    if (rPaidService.getPaid_money() != null) {
                        rPaidService.setPaid_money(rPaidService.getPaid_money() + paidMoney);
                    } else {
                        rPaidService.setPaid_money(paidMoney);
                    }

                    if (needPay != null) {
                        if (rPaidService.getNeed_pay() != null) {
                            rPaidService.setNeed_pay(rPaidService.getNeed_pay() + Math.round(needPay));
                        } else {
                            rPaidService.setNeed_pay(Math.round(needPay));
                        }
                        Long tmpOwnedMoney = null;
                        if (rPaidService.getOwned_money() != null) {
                            tmpOwnedMoney = rPaidService.getOwned_money() + Math.round(needPay - paidMoney);
                        } else {
                            tmpOwnedMoney = Math.round(needPay - paidMoney);
                        }
                        rPaidService.setOwned_money(tmpOwnedMoney > 0 ? tmpOwnedMoney : 0L);
                    } else {
                        Long tmpOwnedMoney = null;
                        if (rPaidService.getOwned_money() != null) {
                            tmpOwnedMoney = rPaidService.getOwned_money() + (0 - paidMoney);
                        } else {
                            tmpOwnedMoney = (0L - paidMoney);
                        }
                        rPaidService.setOwned_money(tmpOwnedMoney > 0 ? tmpOwnedMoney : 0L);
                    }

                    hServices.put(networkAlias, rPaidService);
                }
                hServices.forEach((k, v) -> {
                    respPaidFeeDTO.getNetworks().add(v);
                });

                break;
            case "Giấy phép thiết lập mạng viễn thông công cộng":
                RespPaidServiceDTO rPaidService = new RespPaidServiceDTO();
                double needPay = 0L;
                Long paidMoney = 0L;
                for (LicCpn licCpn : licCpns) {
                    double tmpNeedPaid = serviceFeeEstimate(type, lbt.getName(), licCpn, year, null);
                    System.out.println("TLM: LicCpn: " + licCpn.getLicNumber() + " = " + tmpNeedPaid + " at " + year);
                    needPay += tmpNeedPaid;
                    List<ReceiptServicePayment> receiptServicePayments = receiptServicePaymentRepository.search(licCpn, company.getId(), lbt,
                        year, null, "year");

                    for (ReceiptServicePayment rsp : receiptServicePayments) {
                        //if ((rsp.getLicCpn()!=null)&&(rsp.getLicBusinessType().getId()==lbt.getId())) continue;
                        System.out.println("Paid: " + rsp.getPaidMoney());
                        paidMoney += rsp.getPaidMoney();
                    }
                }

                rPaidService.setNeed_pay(Double.valueOf(needPay).longValue());
                rPaidService.setPaid_money(Double.valueOf(paidMoney).longValue());
                Long tmpOwnMoney = Double.valueOf(needPay - paidMoney).longValue();
                rPaidService.setOwned_money(tmpOwnMoney > 0 ? tmpOwnMoney : 0L);
                //rPaidService.setOwned_money(tmpOwnMoney);

                respPaidFeeDTO.getNetworks().add(rPaidService);
                break;
        }

        return respPaidFeeDTO;
    }

    @Override
    public List<RespCompPaidFeeDTO> searchCompPaidServices(PaidFeeDTO paidFeeDTO) {
        List<RespCompPaidFeeDTO> results = new ArrayList<RespCompPaidFeeDTO>();
        List<Company> companies = this.companyService.findByListIds(paidFeeDTO.getCompany_ids());
        for (Company company : companies) {
            RespCompPaidFeeDTO compPaidFee = new RespCompPaidFeeDTO();
            compPaidFee.setCompany(company);

            Long businessTypeId = paidFeeDTO.getBusiness_type_id();
            if (businessTypeId != null) {
                LicBusinessType lbt = licBusinessTypeRepository.getOne(businessTypeId);

                RespPaidFeeDTO tmp = searchPaidTypeServices(company, lbt,
                    paidFeeDTO.getYear(), paidFeeDTO.getQuarter(), paidFeeDTO.getType());
                if (tmp != null) {
                    compPaidFee.getBusiness_types().add(tmp);
                }
            } else {
                RespPaidFeeDTO tmp = searchTypeServices(company, "Giấy phép cung cấp dịch vụ viễn thông",
                    paidFeeDTO.getYear(), paidFeeDTO.getQuarter(), paidFeeDTO.getType());
                if (tmp != null) {
                    compPaidFee.getBusiness_types().add(tmp);
                }

                tmp = searchTypeServices(company, "Giấy phép thiết lập mạng viễn thông công cộng",
                    paidFeeDTO.getYear(), paidFeeDTO.getQuarter(), paidFeeDTO.getType());
                if (tmp != null) {
                    compPaidFee.getBusiness_types().add(tmp);
                }
            }

            results.add(compPaidFee);
        }

        return results;
    }

    @Override
    public List<RespDebtDTO> debtSearch(DebtDTO debtDTO) {
        List<Company> companies = this.companyService.findByListIds(debtDTO.getCompany_ids());
        List<LicBusinessType> licBusinessTypes = licBusinessTypeRepository.findByListIds(debtDTO.getBusiness_type_ids());
        List<NetworkType> networkTypes = networkTypeRepository.findByListIds(debtDTO.getNetwork_type_ids());

        //////// Get License Number
        List<LicCpn> licCpnResults = new ArrayList<LicCpn>();
        for (LicBusinessType licBusinessType : licBusinessTypes) {
            for (NetworkType networkType : networkTypes) {
                List<LicCpn> licCpns = null;
                if ((debtDTO.getType() != null) && (debtDTO.getType().equalsIgnoreCase("quarter"))) {
                    licCpns = licCpnRepository.search(companies, licBusinessType, networkType,
                        debtDTO.getYear().intValue(), debtDTO.getQuarter().intValue());
                } else {
                    licCpns = licCpnRepository.search(companies, licBusinessType, networkType,
                        debtDTO.getYear().intValue());
                }

                licCpnResults.addAll(licCpns);
            }
        }

        HashMap<String, RespDebtDTO> results = new HashMap<String, RespDebtDTO>();
        //////////
        for (LicCpn licCpn : licCpnResults) {
            System.out.println("ID: " + licCpn.getId() + "\tlic_number: " + licCpn.getLicNumber());
            RespDebtDTO respDebtDTO = new RespDebtDTO();
            if (results.containsKey(licCpn.getCompanyName())) {
                respDebtDTO = results.get(licCpn.getCompanyName());
            } else {
                respDebtDTO.setCompany(licCpn.getCompany());
            }
            /////// Receipt
            DebtReceiptDTO debtReceiptDTO = new DebtReceiptDTO();

            DebtServiceDTO debtServiceDTO = new DebtServiceDTO();
            Set<LicBusinessType> businessTypes = licCpn.getLicBusinessTypes();
            Iterator value = businessTypes.iterator();
            boolean isPresent = false;

            while (value.hasNext()) {
                boolean isFullPaid = true;
                LicBusinessType lbt = (LicBusinessType) value.next();

                if (lbt.getName().equalsIgnoreCase("Giấy phép cung cấp dịch vụ viễn thông")) {
                    ///// Get Receipt
                    List<ReceiptServicePayment> receiptServicePayments = receiptServicePaymentRepository.search(licCpn, debtDTO.getYear(),
                        debtDTO.getQuarter(), debtDTO.getType());
                    ///// Get revenue
                    List<RevenueTelco> revenueTelcos = revenueTelcoRepository.search(licCpn.getCompany(),
                        Arrays.asList(licCpn), debtDTO.getYear(),
                        Arrays.asList(debtDTO.getQuarter()), debtDTO.getType());

                    debtServiceDTO.setBusiness_type_id(lbt.getId());
                    debtServiceDTO.setBusiness_type_name(lbt.getName());
                    double needPayMoneyTotal = 0L;
                    Long revenueTotal = 0L;
                    needPayMoneyTotal += serviceFeeEstimate(debtDTO.getType(), lbt.getName(), licCpn,
                        debtDTO.getYear(), revenueTelcos);
                    for (RevenueTelco rvn : revenueTelcos) {
                        revenueTotal += rvn.getRevenue();
                    }
                    debtServiceDTO.setRevenue(revenueTotal);
                    debtServiceDTO.setNeed_pay(Double.valueOf(needPayMoneyTotal).longValue());

                    Long paidMoney = 0L;
                    for (ReceiptServicePayment rsp : receiptServicePayments) {
                        paidMoney += rsp.getPaidMoney();
                    }
                    debtServiceDTO.setPaid_money(paidMoney);
                    Long tmpOwnedMoney = Double.valueOf(needPayMoneyTotal - paidMoney).longValue();
                    debtServiceDTO.setOwned_money(tmpOwnedMoney > 0 ? tmpOwnedMoney : 0L);
                    if (needPayMoneyTotal - paidMoney > 0) {
                        isFullPaid = false;
                    }
                    System.out.println("need pay: " + needPayMoneyTotal + " vs paid: " + paidMoney);
                } else if (lbt.getName().equalsIgnoreCase("Giấy phép thiết lập mạng viễn thông công cộng")) {
                    ///// Get Receipt
                    List<ReceiptServicePayment> receiptServicePayments = receiptServicePaymentRepository.search(licCpn, debtDTO.getYear(),
                        debtDTO.getQuarter(), debtDTO.getType());

                    debtServiceDTO.setBusiness_type_id(lbt.getId());
                    debtServiceDTO.setBusiness_type_name(lbt.getName());
                    double needPayMoney = serviceFeeEstimate(debtDTO.getType(), lbt.getName(), licCpn, debtDTO.getYear(), null);
                    debtServiceDTO.setNeed_pay(Double.valueOf(needPayMoney).longValue());
                    Long paidMoney = 0L;
                    for (ReceiptServicePayment rsp : receiptServicePayments) {
                        paidMoney += rsp.getPaidMoney();
                    }
                    debtServiceDTO.setPaid_money(paidMoney);
                    Long tmpOwnedMoney = Double.valueOf(needPayMoney - paidMoney).longValue();
                    debtServiceDTO.setOwned_money(tmpOwnedMoney > 0 ? tmpOwnedMoney : 0L);

                    System.out.println("need pay: " + needPayMoney + " vs paid: " + paidMoney);
                    if (needPayMoney - paidMoney > 0) {
                        isFullPaid = false;
                    }
                }
                debtServiceDTO.setYear(debtDTO.getYear());
                debtServiceDTO.setQuarter(debtDTO.getQuarter());
                debtServiceDTO.setType(debtDTO.getType());
                if (debtDTO.getFull_paid() != null) {
                    if (debtDTO.getFull_paid() == 1) { // Trả đủ
                        if (isFullPaid == true) {
                            isPresent = true;
                        }
                    } else if (debtDTO.getFull_paid() == 0) {    // Chưa trả đủ
                        if (isFullPaid == false) {
                            isPresent = true;
                        }
                    }
                } else {
                    isPresent = true;
                }
            }

            ///// Network
            Set<NetworkType> nwkTypes = licCpn.getNetworkTypes();
            value = nwkTypes.iterator();
            while (value.hasNext()) {
                NetworkType nwt = (NetworkType) value.next();
                if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                    debtServiceDTO.setNetwork_id(nwt.getId());
                    debtServiceDTO.setNetwork_alias(nwt.getAlias());
                }
            }

            debtReceiptDTO.getPaymentServices().add(debtServiceDTO);
            if (isPresent) {
                respDebtDTO.getPaymentReceiptDetails().add(debtReceiptDTO);
                results.put(licCpn.getCompanyName(), respDebtDTO);
            }
        }

        List<RespDebtDTO> returnList = new ArrayList<RespDebtDTO>();
        results.forEach((k, v) -> {
            returnList.add(v);
        });
        return returnList;
    }

    @Override
    public Page<RespCompPayment> compDebtSearch(DebtDTO debtDTO) {
        List<Long> years = Arrays.asList();
        List<Long> quarters = Arrays.asList();
        String globalType = null;

        // check if is_default
        if ((debtDTO.getIs_default() != null) && (debtDTO.getIs_default() == 1L)) {
            ///// Get max year
            RevenueTelco maxYear = revenueTelcoRepository.findMaxYear();
            ///// Get max year-quarter
            RevenueTelco maxYearQuarter = revenueTelcoRepository.findMaxYearQuarter();
            if ((maxYear != null) && (maxYearQuarter != null)) {
                if ((maxYear.getYear() * 4 + 4) >= (maxYearQuarter.getYear() * 4 + maxYearQuarter.getQuarter())) {
                    years = Arrays.asList(maxYear.getYear());
                    if (debtDTO.getType() == null || !debtDTO.getType().contains("quarter") || !debtDTO.getType().contains("year")) {
                        globalType = "year";
                    }
                } else {
                    years = Arrays.asList(maxYearQuarter.getYear());
                    quarters = Arrays.asList(maxYearQuarter.getQuarter());
                    if (debtDTO.getType() == null || !debtDTO.getType().contains("quarter") || !debtDTO.getType().contains("year")) {
                        globalType = "quarter";
                    }
                }
            } else if ((maxYear == null) && (maxYearQuarter != null)) {
                years = Arrays.asList(maxYearQuarter.getYear());
                quarters = Arrays.asList(maxYearQuarter.getQuarter());
                if (debtDTO.getType() == null || !debtDTO.getType().contains("quarter") || !debtDTO.getType().contains("year")) {
                    globalType = "quarter";
                }
            } else if ((maxYear != null) && (maxYearQuarter == null)) {
                years = Arrays.asList(maxYear.getYear());
                if (debtDTO.getType() == null || !debtDTO.getType().contains("quarter") || !debtDTO.getType().contains("year")) {
                    globalType = "year";
                }
            } else {
//                return new ArrayList<RespCompPayment>();
                return new PageImpl<>(new ArrayList<RespCompPayment>());
            }
            debtDTO.setType(globalType);
            debtDTO.setQuarters(quarters);
            debtDTO.setYears(years);
        } else {
            if ((debtDTO.getYears() != null) && (debtDTO.getYears().size() >= 1)) {
                years = debtDTO.getYears();
                boolean nullsOnly = years.stream().allMatch(x -> x == null);
                System.out.println("Size: " + years.size());
                if (years.size() == 2) {
                    if ((long) years.get(0) == (long) years.get(1)) {
                        years.remove(1);
                    } else if ((long) years.get(1) > (long) years.get(0)) {
                        for (long i = (long) years.get(0) + 1; i < (long) years.get(1); i++) {
                            years.add(i);
                        }
                    } else if ((long) years.get(0) > (long) years.get(1)) {
                        for (long i = (long) years.get(1) + 1; i < (long) years.get(0); i++) {
                            years.add(i);
                        }
                    }
                }
                years.sort(null);
            }

            globalType = debtDTO.getType();
            quarters = debtDTO.getQuarters();
        }

        List<Company> reqCompanies = this.companyService.findByIdsAndCode(debtDTO.getCompany_ids(), debtDTO.getCompany_code());
        List<LicBusinessType> licBusinessTypes = licBusinessTypeRepository.findByListIds(debtDTO.getBusiness_type_ids());
        List<NetworkType> nwkTypes = null;
        if ((debtDTO.getNetwork_type_ids() != null) && (debtDTO.getNetwork_type_ids().size() > 0)) {
            nwkTypes = networkTypeRepository.findByListIds(debtDTO.getNetwork_type_ids());
        }
        Set<Company> companiesSet = new HashSet<Company>();
//        HashMap<String, Company> hCompany = new HashMap<String, Company>();
        ConcurrentHashMap<String, Company> hCompany = new ConcurrentHashMap<String, Company>();

        List<Integer> intYears = new ArrayList<Integer>();
        for (Long year : years) {
            intYears.add(Integer.valueOf(year.intValue()));
        }
        Integer minYear = Integer.valueOf(years.get(0).intValue());
        Integer maxYear = Integer.valueOf(years.get(years.size() - 1).intValue());

        List<LicCpn> tmpLicNumber = null;
        if ((debtDTO.getLic_number() != null) && (!debtDTO.getLic_number().isEmpty())) {
            tmpLicNumber = licCpnRepository.searchByLic(debtDTO.getLic_number());
        }

        for (LicBusinessType lbt : licBusinessTypes) {
            if ((nwkTypes != null) && (nwkTypes.size() > 0)) {
                for (NetworkType nwt : nwkTypes) {
                    if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                        List<Company> tmp = licCpnRepository.search(reqCompanies, lbt, nwt, minYear, maxYear, null);
                        companiesSet.addAll(tmp);
                    }
                }
            } else {
                List<Company> tmp = licCpnRepository.search(reqCompanies, lbt, null, minYear, maxYear, null);
                companiesSet.addAll(tmp);
            }
        }

        int idx = 0;
        final String[] ccmPayDeadline = {null};

        HashMap<String, RespCompPayment> results = new HashMap<String, RespCompPayment>();
        List<RespCompPayment> returnList = new ArrayList<RespCompPayment>();

        // nếu code và name của doanh nghiệp khác nhau
        if(reqCompanies.size()<1){
            return new PageImpl<RespCompPayment>(
                returnList,
                PageRequest.of(
                    debtDTO.getIndex().intValue(),
                    debtDTO.getSize().intValue()
                ),
                reqCompanies.size()
            );
        }
        ArrayList<Company> companies = new ArrayList<>(companiesSet);

        // tuan edit to improve performance
        List<Long> finalYears = years;
        String finalGlobalType = globalType;
        List<Long> finalQuarters = quarters;
        List<NetworkType> finalNwkTypes = nwkTypes;
        List<LicCpn> finalTmpLicNumber = tmpLicNumber;

        System.out.println("=============== SIZE: " + companiesSet.size());
        for (Company company : companiesSet)
        {
            if (company==null) System.out.println("============================== SHITTTTTTTTTTT");
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Callable<RespCompPayment>> listOfCallable = new ArrayList<Callable<RespCompPayment>>();
        for (Company company : companiesSet)
        {
            Callable<RespCompPayment> callable = new Callable<RespCompPayment>() {
                @Override
                public RespCompPayment call() throws Exception {
                    HashMap<String, String> hPaydeadline = new HashMap<String, String>();

                    System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\t" + company.getName());

                    if (hCompany.containsKey(company.getName())) {
                        return null;
                    }
                    hCompany.put(company.getName(), company);

                    RespCompPayment respCompPayment = new RespCompPayment();
                    respCompPayment.setCompany(company);
                    Boolean isPresent = false;

                    for (Long year : finalYears) {
                        YearBusiness yearBusiness = new YearBusiness();
                        yearBusiness.setYear(year);
                        for (LicBusinessType licBusinessType : licBusinessTypes) {

                            if (licBusinessType.getName().equalsIgnoreCase("Giấy phép cung cấp dịch vụ viễn thông")) {
                                ////// CCDVVT
                                CCDVVT ccdvvt = new CCDVVT();
                                ccdvvt.setBusiness_type_id(licBusinessType.getId());
                                ccdvvt.setBusiness_type_name(licBusinessType.getName());

                                YearSummary yearSummary = new YearSummary();
//                        HashMap<String, TelService> hYearServices = new HashMap<String, TelService>();

                                if (finalGlobalType != null && finalGlobalType.contains("quarter")) {
                                    String type = "quarter";
                                    if (finalQuarters.size() <= 0) {
                                        continue;
                                    }
                                    for (Long quarter : finalQuarters) {
                                        Boolean isFullPaid = true;
                                        String hPaydeadlineKey = company.getName() + "." + licBusinessType.getId() + "." + year + "." + type + "." + quarter;
                                        if (hPaydeadline.containsKey(hPaydeadlineKey)) {
                                            ccmPayDeadline[0] = hPaydeadline.get(hPaydeadlineKey);
                                        } else {
                                            ccmPayDeadline[0] = ccmService.getPayDeadline(licBusinessType.getId(), type, year, quarter);
                                            hPaydeadline.put(hPaydeadlineKey, ccmPayDeadline[0]);
                                        }

                                        QuarterBusiness quarterBusiness = new QuarterBusiness();
                                        quarterBusiness.setQuarter(quarter);
                                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\t" + "Search for year: " + year + "\t" + quarter);
                                        List<LicCpn> licCpns = new ArrayList<LicCpn>();
                                        if ((finalNwkTypes == null) || (finalNwkTypes.size() < 1)) {
                                            licCpns = licCpnRepository.search(company, licBusinessType,
                                                year.intValue(), quarter.intValue());
                                        } else {
                                            for (NetworkType nwt : finalNwkTypes) {
                                                if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                                                    List<LicCpn> tmpLics = licCpnRepository.search(Arrays.asList(company), licBusinessType,
                                                        nwt, year.intValue(), quarter.intValue());
                                                    licCpns.addAll(tmpLics);
                                                }
                                            }
                                        }

                                        /////////////////
                                        if ((licCpns == null) || (licCpns.size() == 0)) continue;
                                        List<LicCpn> fLicCpns = licCpns;
                                        if (finalTmpLicNumber != null) {
                                            fLicCpns = licCpns.stream().distinct().filter(finalTmpLicNumber::contains)
                                                .collect(Collectors.toList());
                                        }

                                        if ((fLicCpns == null) || (fLicCpns.size() < 1)) {
                                            continue;
                                        }
                                        isPresent = true;

                                        HashMap<String, TelService> hServices = new HashMap<String, TelService>();
                                        HashSet<String> hsetLics = new HashSet<String>();
                                        String serviceName = "";

                                        for (LicCpn licCpn : fLicCpns) {
                                            System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\t======================== with CCDVVT LicCpn: " + licCpn.getLicNumber());

                                            TelService telService = new TelService();
                                            Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
                                            Iterator value = networkTypes.iterator();
                                            while (value.hasNext()) {
                                                NetworkType nwt = (NetworkType) value.next();
                                                if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                                                    serviceName = nwt.getAlias();
                                                    telService.setNetwork_id(nwt.getId());
                                                    telService.setNetwork_alias(nwt.getAlias());

                                                    if (hServices.get(serviceName) != null) {
                                                        telService = hServices.get(serviceName);
                                                    }
                                                }
                                            }

                                            if (!hsetLics.contains(licCpn.getLicNumber())) {
                                                hsetLics.add(licCpn.getLicNumber());
                                                telService.setLic_id((telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                                    ? ("" + licCpn.getId()) : (telService.getLic_id() + ";" + licCpn.getId()));
                                                telService.setLic_number((telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                                    ? (licCpn.getLicNumber()) : (telService.getLic_number() + ";" + licCpn.getLicNumber()));
                                            }
                                            telService.setLic_start_date(DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd"));
                                            telService.setLic_end_date(DateUtils.timeNowInstant(licCpn.getExpiredDate(), "yyyy-MM-dd"));

                                            List<RevenueTelco> revenueTelcos = null;
                                            revenueTelcos = revenueTelcoRepository.search(company,
                                                Arrays.asList(licCpn), year, Arrays.asList(quarter), type);

                                            Long revenueTotal = null;
                                            Double needPaidMoney = serviceFeeEstimate(debtDTO.getType(),
                                                licBusinessType.getName(), licCpn,
                                                year, revenueTelcos);
                                            for (RevenueTelco rvn : revenueTelcos) {
                                                if (rvn.getRevenue() != null) {
                                                    if (revenueTotal != null) {
                                                        revenueTotal += rvn.getRevenue();
                                                    } else {
                                                        revenueTotal = rvn.getRevenue();
                                                    }
                                                }
                                            }
                                            if (revenueTotal != null) {
                                                if (telService.getRevenue() != null) {
                                                    telService.setRevenue(telService.getRevenue() + revenueTotal);
                                                } else {
                                                    telService.setRevenue(revenueTotal);
                                                }
                                            }
                                            if (needPaidMoney != null) {
                                                if (telService.getNeed_pay() != null) {
                                                    //telService.setNeed_pay(telService.getNeed_pay() + Double.valueOf(needPaidMoney).longValue());
                                                    telService.setNeed_pay(telService.getNeed_pay() + Math.round(needPaidMoney));
                                                } else {
                                                    telService.setNeed_pay(Math.round(needPaidMoney));
                                                }
                                            }

                                            //////// Paid money
                                            Long paidMoney = 0L;
                                            System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tLicCpn: " + licCpn.getId() + "\tyear: " + year + "\tquarter: " + quarter + "\tType: " + type);
                                            List<ReceiptServicePayment> rsps = null;
                                            rsps = receiptServicePaymentRepository.search(licCpn, year, quarter, type);
                                            for (ReceiptServicePayment rsp : rsps) {
                                                System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tPaidMoney: " + rsp.getPaidMoney());
                                                paidMoney += rsp.getPaidMoney();
                                            }
                                            telService.setPaid_money(telService.getPaid_money() + paidMoney);
                                            if (needPaidMoney != null) {
                                                //Long tmpOwnedMoney = Double.valueOf(needPaidMoney - paidMoney).longValue();
                                                Double tmpOwnedMoney = needPaidMoney - paidMoney;
                                                if (telService.getOwned_money() != null) {
                                                    double sum_needpay = telService.getOwned_money() + Math.round(tmpOwnedMoney);
                                                    telService.setOwned_money(Math.round(sum_needpay));
                                                } else {
                                                    telService.setOwned_money(Math.round(tmpOwnedMoney));
                                                }
                                            }

                                            // Year - Quarter - Type
                                            telService.setYear(year);
                                            telService.setQuarter(quarter);
                                            telService.setType(type);

                                            if (ccmPayDeadline[0] == null) {
                                                ccmPayDeadline[0] = DateUtils.nextDay(year, quarter, 45L);
                                                telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                            } else {
                                                telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                            }

                                            ///// Check chậm nộp
                                            if (ccmPayDeadline[0] != null) {
                                                String timeNow = DateUtils.timeNow("yyyy-MM-dd");
                                                if ((timeNow.compareTo(ccmPayDeadline[0]) > 0)
                                                    && ((telService.getOwned_money() == null) || (telService.getOwned_money() > 0))) {
                                                    isFullPaid = false;
                                                }
                                            }
                                            hServices.put(serviceName, telService);
                                        }

                                        /// Warn doc
                                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tChecking warndoc");
                                        List<WarnDoc> warnDocs = warnDocRepository.search(company.getId(), licBusinessType.getId(),
                                            year, quarter, null);
                                        String warnDocNumber = "";
                                        boolean idxDocFirst = true;
                                        for (WarnDoc warnDoc : warnDocs) {
                                            if (idxDocFirst) {
                                                idxDocFirst = false;
                                                warnDocNumber += warnDoc.getDoc_number();
                                            } else {
                                                warnDocNumber += ";" + warnDoc.getDoc_number();
                                            }
                                        }
                                        quarterBusiness.setIn_doc_number(warnDocNumber);

                                        hServices.forEach((k, v) -> {
                                            quarterBusiness.getPaymentServices().add(v);
                                        });

                                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tGetting note");
                                        ///// Get note
                                        Note note = noteRepository.search(company.getId(), licBusinessType.getId(), year, quarter, type);
                                        quarterBusiness.setQuarter_note(note);

                                        if ((debtDTO.getFull_paid() != null) && (debtDTO.getFull_paid() == 0)) {
                                            if (isFullPaid == false) {
                                                ccdvvt.getQuarters().add(quarterBusiness);
                                            } else {
                                                isPresent = false;
                                            }
                                        } else {
                                            ccdvvt.getQuarters().add(quarterBusiness);
                                        }
                                    }
                                }
                                if (finalGlobalType != null && finalGlobalType.contains("year")) {
                                    String type = "year";
                                    boolean isFullPaid = true;
                                    String hPaydeadlineKey = company.getName() + "." + licBusinessType.getId() + year + "." + type;
                                    if (hPaydeadline.containsKey(hPaydeadlineKey)) {
                                        ccmPayDeadline[0] = hPaydeadline.get(hPaydeadlineKey);
                                    } else {
                                        ccmPayDeadline[0] = ccmService.getPayDeadline(licBusinessType.getId(), type, year, null);
                                        hPaydeadline.put(hPaydeadlineKey, ccmPayDeadline[0]);
                                    }
                                    //String ccmPayDeadline = ccmService.getPayDeadline(licBusinessType.getId(), debtDTO.getType(), year, null);

                                    HashMap<String, TelService> hServices = new HashMap<String, TelService>();
                                    String serviceName = "";

                                    QuarterBusiness quarterBusiness = new QuarterBusiness();
                                    quarterBusiness.setQuarter(null);
                                    System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tYear Search for year: " + year);
                                    List<LicCpn> licCpns = new ArrayList<LicCpn>();
                                    if ((finalNwkTypes == null) || (finalNwkTypes.size() < 1)) {
                                        licCpns = licCpnRepository.searchVersion2(company, licBusinessType,
                                            year.intValue());
                                    } else {
                                        for (NetworkType nwt : finalNwkTypes) {
                                            List<LicCpn> tmpLics = licCpnRepository.search(Arrays.asList(company), licBusinessType,
                                                nwt, year.intValue());
                                            licCpns.addAll(tmpLics);
                                        }
                                    }
                                    List<LicCpn> fLicCpns = licCpns;
                                    if (finalTmpLicNumber != null) {
                                        fLicCpns = licCpns.stream().distinct().filter(finalTmpLicNumber::contains)
                                            .collect(Collectors.toList());
                                    }

                                    if ((fLicCpns == null) || (fLicCpns.size() < 1)) {
                                        continue;
                                    }
                                    isPresent = true;

                                    HashSet<String> hsetLics = new HashSet<String>();
                                    for (LicCpn licCpn : fLicCpns) {
                                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\t======================== with CCDVVT LicCpn: " + licCpn.getLicNumber());

                                        TelService telService = new TelService();
                                        Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
                                        Iterator value = networkTypes.iterator();
                                        while (value.hasNext()) {
                                            NetworkType nwt = (NetworkType) value.next();
                                            if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                                                serviceName = nwt.getAlias();
                                                telService.setNetwork_id(nwt.getId());
                                                telService.setNetwork_alias(nwt.getAlias());

                                                if (hServices.get(serviceName) != null) {
                                                    telService = hServices.get(serviceName);
                                                }
                                            }
                                        }

                                        if (!hsetLics.contains(licCpn.getLicNumber())) {
                                            hsetLics.add(licCpn.getLicNumber());
                                            telService.setLic_id((telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                                ? ("" + licCpn.getId()) : (telService.getLic_id() + ";" + licCpn.getId()));
                                            telService.setLic_number((telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                                ? (licCpn.getLicNumber()) : (telService.getLic_number() + ";" + licCpn.getLicNumber()));
                                        }
                                        telService.setLic_start_date(DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd"));
                                        telService.setLic_end_date(DateUtils.timeNowInstant(licCpn.getExpiredDate(), "yyyy-MM-dd"));

                                        List<RevenueTelco> revenueTelcos = revenueTelcoRepository.search(company,
                                            Arrays.asList(licCpn), year, null, type);
                                        if (revenueTelcos != null) {
                                            System.out.println("Size: " + revenueTelcos);
                                        }
                                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tCompany: " + company.getId() + "\tYear: " + year + "\tLicCpn: " + licCpn.getId());
                                        if ((revenueTelcos == null) || (revenueTelcos.size() < 1)) {
                                            revenueTelcos = revenueTelcoRepository.search(company,
                                                Arrays.asList(licCpn), year, Arrays.asList(1L, 2L, 3L, 4L), "quarter");
                                        }
                                        if (revenueTelcos != null) {
                                            System.out.println("Revenue size: " + revenueTelcos.size());
                                        }
                                        Long revenueTotal = null;
                                        Double needPaidMoney = serviceFeeEstimate("year",
                                            licBusinessType.getName(), licCpn,
                                            year, revenueTelcos);
                                        for (RevenueTelco rvn : revenueTelcos) {
                                            if (rvn.getRevenue() != null) {
                                                if (revenueTotal != null) {
                                                    revenueTotal += rvn.getRevenue();
                                                } else {
                                                    revenueTotal = rvn.getRevenue();
                                                }

                                                yearSummary.setRevenue(yearSummary.getRevenue() + rvn.getRevenue());
                                            }
                                        }
                                        if (revenueTotal != null) {
                                            if (telService.getRevenue() != null) {
                                                telService.setRevenue(telService.getRevenue() + revenueTotal);
                                            } else {
                                                telService.setRevenue(revenueTotal);
                                            }

                                            telService.setNeed_pay_revenue(Math.round(Double.valueOf(telService.getRevenue() * 0.5 / 100)));
                                            //// Year summary
                                            yearSummary.setNeed_pay_revenue(yearSummary.getNeed_pay_revenue() + Math.round(Double.valueOf(telService.getRevenue() * 0.5 / 100)));
                                        }
                                        if (needPaidMoney != null) {
                                            if (telService.getNeed_pay() != null) {
                                                telService.setNeed_pay(telService.getNeed_pay() + Math.round(needPaidMoney));
                                            } else {
                                                telService.setNeed_pay(Math.round(needPaidMoney));
                                            }

                                            //// Year summary
                                            Long needPaidMoneys = Math.round(needPaidMoney);
                                            yearSummary.setNeed_pay(yearSummary.getNeed_pay() + Double.valueOf(needPaidMoneys).longValue());
                                        }

                                        //////// Paid money
                                        Long paidMoney = 0L;
                                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tLicCpn: " + licCpn.getId() + "\tyear: " + year + "\tquarter: " + null + "\tType: " + debtDTO.getType());
                                        List<ReceiptServicePayment> rsps = null;
                                        rsps = receiptServicePaymentRepository.search(licCpn, company.getId(), licBusinessType,
                                            year, null, null);
                                        for (ReceiptServicePayment rsp : rsps) {
                                            System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tPaidMoney: " + rsp.getPaidMoney());
                                            if (rsp.getType().equalsIgnoreCase("quarter")) {
                                                yearSummary.setPaid_money_quarters(yearSummary.getPaid_money_quarters() + rsp.getPaidMoney());
                                                telService.setPaid_money_quarters(telService.getPaid_money_quarters() + rsp.getPaidMoney());
                                            } else if (rsp.getType().equalsIgnoreCase("year")) {
                                                yearSummary.setPaid_money_year(yearSummary.getPaid_money_year() + rsp.getPaidMoney());
                                                telService.setPaid_money_year(telService.getPaid_money_year() + rsp.getPaidMoney());
                                            }
                                            paidMoney += rsp.getPaidMoney();
                                        }
                                        //
                                        yearSummary.setOwned_money(yearSummary.getNeed_pay() - yearSummary.getPaid_money_quarters() - yearSummary.getPaid_money_year());

                                        telService.setPaid_money(telService.getPaid_money() + paidMoney);
                                        if (ccmPayDeadline[0] == null) {
                                            ccmPayDeadline[0] = DateUtils.nextDay(year, 4L, 105L);
                                            telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                        } else {
                                            telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                        }
                                        if (needPaidMoney != null) {
                                            //Long tmpOwnedMoney = Double.valueOf(needPaidMoney - paidMoney).longValue();
                                            Double tmpOwnedMoney = needPaidMoney - paidMoney;
                                            if (telService.getOwned_money() != null) {
                                                double sum_needpay = telService.getOwned_money() + Math.round(tmpOwnedMoney);
                                                telService.setOwned_money(Math.round(sum_needpay));
                                            } else {
                                                telService.setOwned_money(Math.round(tmpOwnedMoney));
                                            }
                                        }

                                        // Year - Quarter - Type
                                        telService.setYear(year);
                                        telService.setType(debtDTO.getType());

                                        hServices.put(serviceName, telService);
                                    }

                                    for (Map.Entry h : hServices.entrySet()) {
                                        TelService v = (TelService) h.getValue();
                                        if (v.getRevenue() != null) {
                                            Double tmpValue = v.getRevenue() * 0.5 / 100;
                                            if (tmpValue > v.getNeed_pay()) {
                                                v.setNeed_pay(Math.round(tmpValue));
                                            }
                                            v.setOwned_money(v.getNeed_pay() - v.getPaid_money());
                                        }
                                        ///// Check chậm nộp
                                        if (ccmPayDeadline[0] != null) {
                                            String timeNow = DateUtils.timeNow("yyyy-MM-dd");
                                            if ((timeNow.compareTo(ccmPayDeadline[0]) > 0) && ((v.getOwned_money() == null) || (v.getOwned_money() > 0))) {
                                                isFullPaid = false;
                                            } else {
                                                System.out.println("==================================== Full-paid: " + h.getKey());
                                            }
                                        }
                                        quarterBusiness.getPaymentServices().add(v);
                                    }

                                    /// Warn doc
                                    System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tChecking warndoc");
                                    List<WarnDoc> warnDocs = warnDocRepository.search(company.getId(), licBusinessType.getId(),
                                        year, null, null);
                                    String inDocNumber = "";
                                    String outDocNumber = "";
                                    boolean idxInDocFirst = true;
                                    boolean idxOutDocFirst = true;
                                    for (WarnDoc warnDoc : warnDocs) {
                                        switch (warnDoc.getDoc_type()) {
                                            case "in":
                                                if (idxInDocFirst) {
                                                    idxInDocFirst = false;
                                                    inDocNumber += warnDoc.getDoc_number();
                                                } else {
                                                    inDocNumber += ";" + warnDoc.getDoc_number();
                                                }
                                                break;
                                            case "out":
                                                if (idxOutDocFirst) {
                                                    idxOutDocFirst = false;
                                                    outDocNumber += warnDoc.getDoc_number();
                                                } else {
                                                    outDocNumber += ";" + warnDoc.getDoc_number();
                                                }
                                                break;
                                            default:
                                                continue;
                                        }

                                    }
                                    quarterBusiness.setIn_doc_number(inDocNumber);
                                    quarterBusiness.setOut_doc_number(outDocNumber);

                                    if ((debtDTO.getFull_paid() != null) && (debtDTO.getFull_paid() == 0)) {
                                        if (isFullPaid == false) {
                                            ccdvvt.setYear(quarterBusiness);
                                        } else {
                                            isPresent = false;
                                        }
                                    } else {
                                        ccdvvt.setYear(quarterBusiness);
                                    }

                                    /// Get note
                                    System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tGetting note");
                                    Note note = noteRepository.search(company.getId(), licBusinessType.getId(), year, null, "year");
                                    ccdvvt.setYear_note(note);
                                }

                                if (isPresent) {
                                    yearBusiness.setCcdvvt(ccdvvt);
                                    if ((debtDTO.getType().contains("year") && (debtDTO.getType().contains("quarter")))) {
                                        yearBusiness.setYear_summary(yearSummary);
                                    }
                                }
                            } else if (licBusinessType.getName().equalsIgnoreCase("Giấy phép thiết lập mạng viễn thông công cộng")) {
                                System.out.println("========= Giấy phép thiết lập mạng");
                                boolean isFullPaid = true;
                                String type = "year";
                                String hPaydeadlineKey = company.getName() + "." + licBusinessType.getId() + year;
                                if (hPaydeadline.containsKey(hPaydeadlineKey)) {
                                    ccmPayDeadline[0] = hPaydeadline.get(hPaydeadlineKey);
                                } else {
                                    ccmPayDeadline[0] = ccmService.getPayDeadline(licBusinessType.getId(), type, year, null);
                                    hPaydeadline.put(hPaydeadlineKey, ccmPayDeadline[0]);
                                }
                                String service_type = "4";
                                Long company_id = company.getId();
                                TLM tlm = new TLM();
                                tlm.setBusiness_type_id(licBusinessType.getId());
                                tlm.setBusiness_type_name(licBusinessType.getName());

                                List<LicCpn> tlmLicCpns = licCpnRepository.search(company, licBusinessType, year.intValue());
                                if ((tlmLicCpns == null) || (tlmLicCpns.size() == 0)) continue;

                                List<LicCpn> fLicCpns = tlmLicCpns;
                                if (finalTmpLicNumber != null) {
                                    fLicCpns = tlmLicCpns.stream().distinct().filter(finalTmpLicNumber::contains)
                                        .collect(Collectors.toList());
                                }

                                if ((fLicCpns == null) || (fLicCpns.size() <= 0)) {
                                    continue;
                                }
                                isPresent = true;

                                HashSet<String> hsetLics = new HashSet<String>();
                                for (LicCpn licCpn : fLicCpns) {
                                    TelService telService = new TelService();
                                    if ((finalTmpLicNumber != null) && (finalTmpLicNumber.size() > 0)) {
                                        if (!finalTmpLicNumber.contains(licCpn)) {
                                            continue;
                                        }
                                    }
                                    System.out.println("======================== with TLM LicCpn: " + licCpn.getLicNumber());

                                    Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
                                    Iterator value = networkTypes.iterator();
                                    while (value.hasNext()) {
                                        NetworkType nwt = (NetworkType) value.next();
                                        if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                                            telService.setNetwork_id(nwt.getId());
                                            telService.setNetwork_alias(nwt.getAlias());
                                        }
                                    }

                                    telService.setLic_start_date(DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd"));
                                    telService.setLic_end_date(DateUtils.timeNowInstant(licCpn.getExpiredDate(), "yyyy-MM-dd"));
                                    double needPaidMoney = tlmFeeEstimate(debtDTO.getType(), licCpn, year);
                                    String licCreatedDate = DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd");
                                    double fee0101 = tlmFee0101Estimate(debtDTO.getType(), licCpn, year);

                                    // Check is_paied_0101
                                    if ((debtDTO.getIs_paid_0101() != null) && (debtDTO.getIs_paid_0101() == 1L)) {
                                        if (fee0101 <= 0) {
                                            continue;
                                        }
                                    }

                                    telService.setNeed_pay_0101(telService.getNeed_pay_0101() + Double.valueOf(fee0101).longValue());
                                    if (licCreatedDate.compareTo(DateUtils.timeNow("yyyy-MM-dd")) <= 0) {
//                                if (telService.getNeed_pay()!=null) telService.setNeed_pay(telService.getNeed_pay() + Double.valueOf(needPaidMoney).longValue());
//                                else telService.setNeed_pay(Double.valueOf(needPaidMoney).longValue());
                                        if (telService.getNeed_pay() != null) {
                                            telService.setNeed_pay(telService.getNeed_pay() + Math.round(needPaidMoney));
                                        } else {
                                            telService.setNeed_pay(Math.round(needPaidMoney));
                                        }

                                        if ((needPaidMoney > 0) && (!hsetLics.contains(licCpn.getLicNumber()))) {
                                            hsetLics.add(licCpn.getLicNumber());
                                            String licId = (telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                                ? "" + licCpn.getId() : (telService.getLic_id() + ";" + licCpn.getId());
                                            telService.setLic_id(licId);
                                            String licNumber = (telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                                ? licCpn.getLicNumber() : (telService.getLic_number() + ";" + licCpn.getLicNumber());
                                            telService.setLic_number(licNumber);
                                        }
                                    }

                                    //////// Paid money
                                    Long paidMoney = 0L;
                                    List<ReceiptServicePayment> rsps = receiptServicePaymentRepository.search(licCpn, company.getId(), licBusinessType,
                                        year, null, "year");
                                    for (ReceiptServicePayment rsp : rsps) {
                                        paidMoney += rsp.getPaidMoney();
                                    }
                                    telService.setPaid_money(paidMoney);
                                    Long tmpOwnedMoney = (telService.getOwned_money() != null ? telService.getOwned_money() : 0) + Math.round(needPaidMoney - paidMoney);
//                            Double tmpOwnedMoney = (telService.getOwned_money()!=null?telService.getOwned_money():0) + (needPaidMoney - paidMoney);
                                    //if (tmpOwnedMoney <= 0) continue;       // Ignore lic which has 0 vnd debt
                                    System.out.println("Need pay: " + needPaidMoney + "\tPaid: " + paidMoney + "\tOwned: " + tmpOwnedMoney);
                                    if (needPaidMoney <= 0) {
                                        continue;
                                    }
                                    telService.setOwned_money(tmpOwnedMoney);
                                    if (ccmPayDeadline[0] == null) {
                                        if (telService.getOwned_money() > 0) {
                                            isFullPaid = false;
                                        }
                                    } else {
                                        ///// Check chậm nộp
                                        if (ccmPayDeadline[0] != null) {
                                            String timeNow = DateUtils.timeNow("yyyy-MM-dd");
                                            if ((timeNow.compareTo(ccmPayDeadline[0]) > 0) && (telService.getOwned_money() > 0)) {
                                                isFullPaid = false;
                                            }
                                        }
                                        telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                    }
                                    /// Warn doc
                                    List<WarnDoc> warnDocs = warnDocRepository.search(company.getId(), licBusinessType.getId(),
                                        year, null, null);
                                    String inDocNumber = "";
                                    String outDocNumber = "";
                                    boolean idxInDocFirst = true;
                                    boolean idxOutDocFirst = true;
                                    for (WarnDoc warnDoc : warnDocs) {
                                        switch (warnDoc.getDoc_type()) {
                                            case "in":
                                                if (idxInDocFirst) {
                                                    idxInDocFirst = false;
                                                    inDocNumber += warnDoc.getDoc_number();
                                                } else {
                                                    inDocNumber += ";" + warnDoc.getDoc_number();
                                                }
                                                break;
                                            case "out":
                                                if (idxOutDocFirst) {
                                                    idxOutDocFirst = false;
                                                    outDocNumber += warnDoc.getDoc_number();
                                                } else {
                                                    outDocNumber += ";" + warnDoc.getDoc_number();
                                                }
                                                break;
                                            default:
                                                continue;
                                        }

                                    }
                                    tlm.setIn_doc_number(inDocNumber);
                                    tlm.setOut_doc_number(outDocNumber);

                                    /// Get note
                                    Note note = noteRepository.search(company.getId(), licBusinessType.getId(), year, null, "year");
                                    tlm.setYear_note(note);

                                    if ((debtDTO.getFull_paid() != null) && (debtDTO.getFull_paid() == 0)) {
                                        if (isFullPaid == false) {
                                            tlm.getPaymentServices().add(telService);
                                        } else {
                                            isPresent = false;
                                        }
                                    } else {
                                        tlm.getPaymentServices().add(telService);
                                    }
                                }

                                if (isPresent) {
                                    if ((tlm.getPaymentServices() != null) && (tlm.getPaymentServices().size() > 0)) {
                                        // Nếu doanh nghiệp có dịch vụ phải nộp phí
                                        yearBusiness.setTlm(tlm);
                                    } else {
                                        // Nếu doanh nghiệp ko có dịch vụ phải nộp phí ==> bỏ qua
                                        isPresent = false;
                                    }
                                }
                            }
                        }
                        respCompPayment.getYears().add(yearBusiness);
                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:SS") + "\t" + company.getId() + "\t" + company.getName() + " at Year: " + year);
                    }
                    if (isPresent) {
                        return respCompPayment;
                    }
                    return null;
                }
            };

            listOfCallable.add(callable);
        }

        try {
            List<Future<RespCompPayment>> futures = executor.invokeAll(listOfCallable);

            for(Future<RespCompPayment> future : futures){
                if (future.get()!=null) returnList.add(future.get());
            }

            executor.shutdown();
        }catch (Exception ex){}
        //
        System.out.println("======================== Sorting ===========================");
        Collections.sort(returnList);
        System.out.println("======================== End ===========================");

        return new PageImpl<RespCompPayment>(
            returnList,
            PageRequest.of(
                debtDTO.getIndex().intValue(),
                debtDTO.getSize().intValue()
            ),
            companies.size()
        );
    }

    // tuantv
    @Override
    public List<RespCompPayment> compDebtSearchAll(DebtDTO debtDTO) {
        List<Long> years = Arrays.asList();
        List<Long> quarters = Arrays.asList();
        String globalType = null;

        // check if is_default
        if ((debtDTO.getIs_default() != null) && (debtDTO.getIs_default() == 1L)) {
            ///// Get max year
            RevenueTelco maxYear = revenueTelcoRepository.findMaxYear();
            ///// Get max year-quarter
            RevenueTelco maxYearQuarter = revenueTelcoRepository.findMaxYearQuarter();
            if ((maxYear != null) && (maxYearQuarter != null)) {
                if ((maxYear.getYear() * 4 + 4) >= (maxYearQuarter.getYear() * 4 + maxYearQuarter.getQuarter())) {
                    years = Arrays.asList(maxYear.getYear());
                    if (debtDTO.getType() == null || !debtDTO.getType().contains("quarter") || !debtDTO.getType().contains("year")) {
                        globalType = "year";
                    }
                } else {
                    years = Arrays.asList(maxYearQuarter.getYear());
                    quarters = Arrays.asList(maxYearQuarter.getQuarter());
                    if (debtDTO.getType() == null || !debtDTO.getType().contains("quarter") || !debtDTO.getType().contains("year")) {
                        globalType = "quarter";
                    }
                }
            } else if ((maxYear == null) && (maxYearQuarter != null)) {
                years = Arrays.asList(maxYearQuarter.getYear());
                quarters = Arrays.asList(maxYearQuarter.getQuarter());
                if (debtDTO.getType() == null || !debtDTO.getType().contains("quarter") || !debtDTO.getType().contains("year")) {
                    globalType = "quarter";
                }
            } else if ((maxYear != null) && (maxYearQuarter == null)) {
                years = Arrays.asList(maxYear.getYear());
                if (debtDTO.getType() == null || !debtDTO.getType().contains("quarter") || !debtDTO.getType().contains("year")) {
                    globalType = "year";
                }
            } else {
                return new ArrayList<RespCompPayment>();
//                return new PageImpl<>(new ArrayList<RespCompPayment>());
            }
            debtDTO.setType(globalType);
            debtDTO.setQuarters(quarters);
            debtDTO.setYears(years);
        } else {
            if ((debtDTO.getYears() != null) && (debtDTO.getYears().size() >= 1)) {
                years = debtDTO.getYears();
                boolean nullsOnly = years.stream().allMatch(x -> x == null);
                System.out.println("Size: " + years.size());
                if (years.size() == 2) {
                    if ((long) years.get(0) == (long) years.get(1)) {
                        years.remove(1);
                    } else if ((long) years.get(1) > (long) years.get(0)) {
                        for (long i = (long) years.get(0) + 1; i < (long) years.get(1); i++) {
                            years.add(i);
                        }
                    } else if ((long) years.get(0) > (long) years.get(1)) {
                        for (long i = (long) years.get(1) + 1; i < (long) years.get(0); i++) {
                            years.add(i);
                        }
                    }
                }
                years.sort(null);
            }

            globalType = debtDTO.getType();
            quarters = debtDTO.getQuarters();
        }

        List<Company> reqCompanies = this.companyService.findByIdsAndCode(debtDTO.getCompany_ids(), debtDTO.getCompany_code());
        List<LicBusinessType> licBusinessTypes = licBusinessTypeRepository.findByListIds(debtDTO.getBusiness_type_ids());
        List<NetworkType> nwkTypes = null;
        if ((debtDTO.getNetwork_type_ids() != null) && (debtDTO.getNetwork_type_ids().size() > 0)) {
            nwkTypes = networkTypeRepository.findByListIds(debtDTO.getNetwork_type_ids());
        }
        Set<Company> companiesSet = new HashSet<Company>();
        HashMap<String, Company> hCompany = new HashMap<String, Company>();

        List<Integer> intYears = new ArrayList<Integer>();
        for (Long year : years) {
            intYears.add(Integer.valueOf(year.intValue()));
        }
        Integer minYear = Integer.valueOf(years.get(0).intValue());
        Integer maxYear = Integer.valueOf(years.get(years.size() - 1).intValue());

        List<LicCpn> tmpLicNumber = null;
        if ((debtDTO.getLic_number() != null) && (!debtDTO.getLic_number().isEmpty())) {
            tmpLicNumber = licCpnRepository.searchByLic(debtDTO.getLic_number());
        }

        for (LicBusinessType lbt : licBusinessTypes) {
            if ((nwkTypes != null) && (nwkTypes.size() > 0)) {
                for (NetworkType nwt : nwkTypes) {
                    if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                        List<Company> tmp = licCpnRepository.search(reqCompanies, lbt, nwt, minYear, maxYear, null);
                        companiesSet.addAll(tmp);
                    }
                }
            } else {
                List<Company> tmp = licCpnRepository.search(reqCompanies, lbt, null, minYear, maxYear, null);
                companiesSet.addAll(tmp);
            }
        }

        final String[] ccmPayDeadline = {null};
        HashMap<String, String> hPaydeadline = new HashMap<String, String>();
        HashMap<String, RespCompPayment> results = new HashMap<String, RespCompPayment>();
        List<RespCompPayment> returnList = new ArrayList<RespCompPayment>();

        ArrayList<Company> companies = new ArrayList<>(companiesSet);
        List<Long> finalYears = years;
        String finalGlobalType = globalType;
        List<Long> finalQuarters = quarters;
        List<NetworkType> finalNwkTypes = nwkTypes;
        List<LicCpn> finalTmpLicNumber = tmpLicNumber;
        companies.parallelStream().forEach(company -> {
            // LongTH add for paging
//            if ((debtDTO.getIndex()!=null)&&(debtDTO.getSize()!=null)){
//                if (companies.indexOf(company) < (debtDTO.getIndex() * debtDTO.getSize())) return;
//            }
            System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\t" + company.getName());

            if (hCompany.containsKey(company.getName())) {
                return;
            }
            hCompany.put(company.getName(), company);

            RespCompPayment respCompPayment = new RespCompPayment();
            respCompPayment.setCompany(company);
            Boolean isPresent = false;

            for (Long year : finalYears) {
                YearBusiness yearBusiness = new YearBusiness();
                yearBusiness.setYear(year);
                for (LicBusinessType licBusinessType : licBusinessTypes) {

                    if (licBusinessType.getName().equalsIgnoreCase("Giấy phép cung cấp dịch vụ viễn thông")) {
                        ////// CCDVVT
                        CCDVVT ccdvvt = new CCDVVT();
                        ccdvvt.setBusiness_type_id(licBusinessType.getId());
                        ccdvvt.setBusiness_type_name(licBusinessType.getName());

                        YearSummary yearSummary = new YearSummary();
//                        HashMap<String, TelService> hYearServices = new HashMap<String, TelService>();

                        //List<Long> quarters = null;
                        //if (debtDTO.getType().equalsIgnoreCase("quarter")){
                        if (finalGlobalType != null && finalGlobalType.contains("quarter")) {
                            String type = "quarter";
//                            if ((debtDTO.getQuarters()==null)||(debtDTO.getQuarters().size()<=0)) continue;
//                            quarters = debtDTO.getQuarters();
                            if (finalQuarters.size() <= 0) {
                                continue;
                            }
                            for (Long quarter : finalQuarters) {
                                Boolean isFullPaid = true;
                                String hPaydeadlineKey = company.getName() + "." + licBusinessType.getId() + "." + year + "." + type + "." + quarter;
                                if (hPaydeadline.containsKey(hPaydeadlineKey)) {
                                    ccmPayDeadline[0] = hPaydeadline.get(hPaydeadlineKey);
                                } else {
                                    ccmPayDeadline[0] = ccmService.getPayDeadline(licBusinessType.getId(), type, year, quarter);
                                    hPaydeadline.put(hPaydeadlineKey, ccmPayDeadline[0]);
                                }

                                QuarterBusiness quarterBusiness = new QuarterBusiness();
                                quarterBusiness.setQuarter(quarter);
                                System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\t" + "Search for year: " + year + "\t" + quarter);
                                List<LicCpn> licCpns = new ArrayList<LicCpn>();
                                if ((finalNwkTypes == null) || (finalNwkTypes.size() < 1)) {
                                    licCpns = licCpnRepository.search(company, licBusinessType,
                                        year.intValue(), quarter.intValue());
                                } else {
                                    for (NetworkType nwt : finalNwkTypes) {
                                        if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                                            List<LicCpn> tmpLics = licCpnRepository.search(Arrays.asList(company), licBusinessType,
                                                nwt, year.intValue(), quarter.intValue());
                                            licCpns.addAll(tmpLics);
                                        }
                                    }
                                }

                                /////////////////
                                if ((licCpns==null)||(licCpns.size()==0)) continue;
                                List<LicCpn> fLicCpns = licCpns;
                                if (finalTmpLicNumber != null) {
                                    fLicCpns = licCpns.stream().distinct().filter(finalTmpLicNumber::contains)
                                        .collect(Collectors.toList());
                                }

                                if ((fLicCpns == null) || (fLicCpns.size() < 1)) {
                                    continue;
                                }
                                isPresent = true;

                                HashMap<String, TelService> hServices = new HashMap<String, TelService>();
                                HashSet<String> hsetLics = new HashSet<String>();
                                String serviceName = "";

                                for (LicCpn licCpn : fLicCpns) {
                                    System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\t======================== with CCDVVT LicCpn: " + licCpn.getLicNumber());

                                    TelService telService = new TelService();
                                    Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
                                    Iterator value = networkTypes.iterator();
                                    while (value.hasNext()) {
                                        NetworkType nwt = (NetworkType) value.next();
                                        if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                                            serviceName = nwt.getAlias();
                                            telService.setNetwork_id(nwt.getId());
                                            telService.setNetwork_alias(nwt.getAlias());

                                            if (hServices.get(serviceName) != null) {
                                                telService = hServices.get(serviceName);
                                            }
                                        }
                                    }

                                    if (!hsetLics.contains(licCpn.getLicNumber())) {
                                        hsetLics.add(licCpn.getLicNumber());
                                        telService.setLic_id((telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                            ? ("" + licCpn.getId()) : (telService.getLic_id() + ";" + licCpn.getId()));
                                        telService.setLic_number((telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                            ? (licCpn.getLicNumber()) : (telService.getLic_number() + ";" + licCpn.getLicNumber()));
                                    }
                                    telService.setLic_start_date(DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd"));
                                    telService.setLic_end_date(DateUtils.timeNowInstant(licCpn.getExpiredDate(), "yyyy-MM-dd"));

                                    List<RevenueTelco> revenueTelcos = null;
                                    revenueTelcos = revenueTelcoRepository.search(company,
                                        Arrays.asList(licCpn), year, Arrays.asList(quarter), type);

                                    Long revenueTotal = null;
                                    Double needPaidMoney = serviceFeeEstimate(debtDTO.getType(),
                                        licBusinessType.getName(), licCpn,
                                        year, revenueTelcos);
                                    for (RevenueTelco rvn : revenueTelcos) {
                                        if (rvn.getRevenue() != null) {
                                            if (revenueTotal != null) {
                                                revenueTotal += rvn.getRevenue();
                                            } else {
                                                revenueTotal = rvn.getRevenue();
                                            }
                                        }
                                    }
                                    if (revenueTotal != null) {
                                        if (telService.getRevenue() != null) {
                                            telService.setRevenue(telService.getRevenue() + revenueTotal);
                                        } else {
                                            telService.setRevenue(revenueTotal);
                                        }
                                    }
                                    if (needPaidMoney != null) {
                                        if (telService.getNeed_pay() != null) {
                                            //telService.setNeed_pay(telService.getNeed_pay() + Double.valueOf(needPaidMoney).longValue());
                                            telService.setNeed_pay(telService.getNeed_pay() + Math.round(needPaidMoney));
                                        } else {
                                            telService.setNeed_pay(Math.round(needPaidMoney));
                                        }
                                    }

                                    //////// Paid money
                                    Long paidMoney = 0L;
                                    System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tLicCpn: " + licCpn.getId() + "\tyear: " + year + "\tquarter: " + quarter + "\tType: " + type);
                                    List<ReceiptServicePayment> rsps = null;
                                    rsps = receiptServicePaymentRepository.search(licCpn, year, quarter, type);
                                    for (ReceiptServicePayment rsp : rsps) {
                                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tPaidMoney: " + rsp.getPaidMoney());
                                        paidMoney += rsp.getPaidMoney();
                                    }
                                    telService.setPaid_money(telService.getPaid_money() + paidMoney);
                                    if (needPaidMoney != null) {
                                        //Long tmpOwnedMoney = Double.valueOf(needPaidMoney - paidMoney).longValue();
                                        Double tmpOwnedMoney = needPaidMoney - paidMoney;
                                        if (telService.getOwned_money() != null) {
                                            double sum_needpay = telService.getOwned_money() + Math.round(tmpOwnedMoney);
                                            telService.setOwned_money(Math.round(sum_needpay));
                                        } else {
                                            telService.setOwned_money(Math.round(tmpOwnedMoney));
                                        }
                                    }

                                    // Year - Quarter - Type
                                    telService.setYear(year);
                                    telService.setQuarter(quarter);
                                    telService.setType(type);

                                    if (ccmPayDeadline[0] == null) {
                                        ccmPayDeadline[0] = DateUtils.nextDay(year, quarter, 45L);
                                        telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                    } else {
                                        telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                    }

                                    ///// Check chậm nộp
                                    if (ccmPayDeadline[0] != null) {
                                        String timeNow = DateUtils.timeNow("yyyy-MM-dd");
                                        if ((timeNow.compareTo(ccmPayDeadline[0]) > 0)
                                            && ((telService.getOwned_money() == null) || (telService.getOwned_money() > 0))) {
                                            isFullPaid = false;
                                        }
                                    }
                                    hServices.put(serviceName, telService);
                                }

                                /// Warn doc
                                System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tChecking warndoc");
                                List<WarnDoc> warnDocs = warnDocRepository.search(company.getId(), licBusinessType.getId(),
                                    year, quarter, null);
                                String warnDocNumber = "";
                                boolean idxDocFirst = true;
                                for (WarnDoc warnDoc : warnDocs) {
                                    if (idxDocFirst) {
                                        idxDocFirst = false;
                                        warnDocNumber += warnDoc.getDoc_number();
                                    } else {
                                        warnDocNumber += ";" + warnDoc.getDoc_number();
                                    }
                                }
                                quarterBusiness.setIn_doc_number(warnDocNumber);

                                hServices.forEach((k, v) -> {
                                    quarterBusiness.getPaymentServices().add(v);
                                });

                                System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tGetting note");
                                ///// Get note
                                Note note = noteRepository.search(company.getId(), licBusinessType.getId(), year, quarter, type);
                                quarterBusiness.setQuarter_note(note);

                                if ((debtDTO.getFull_paid() != null) && (debtDTO.getFull_paid() == 0)) {
                                    if (isFullPaid == false) {
                                        ccdvvt.getQuarters().add(quarterBusiness);
                                    } else {
                                        isPresent = false;
                                    }
                                } else {
                                    ccdvvt.getQuarters().add(quarterBusiness);
                                }
                            }
                            //}else if (debtDTO.getType().equalsIgnoreCase("year")){
                        }
                        if (finalGlobalType != null && finalGlobalType.contains("year")) {
                            String type = "year";
                            boolean isFullPaid = true;
                            String hPaydeadlineKey = company.getName() + "." + licBusinessType.getId() + year + "." + type;
                            if (hPaydeadline.containsKey(hPaydeadlineKey)) {
                                ccmPayDeadline[0] = hPaydeadline.get(hPaydeadlineKey);
                            } else {
                                ccmPayDeadline[0] = ccmService.getPayDeadline(licBusinessType.getId(), type, year, null);
                                hPaydeadline.put(hPaydeadlineKey, ccmPayDeadline[0]);
                            }
                            //String ccmPayDeadline = ccmService.getPayDeadline(licBusinessType.getId(), debtDTO.getType(), year, null);

                            HashMap<String, TelService> hServices = new HashMap<String, TelService>();
                            String serviceName = "";

                            QuarterBusiness quarterBusiness = new QuarterBusiness();
                            quarterBusiness.setQuarter(null);
                            System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tYear Search for year: " + year);
                            List<LicCpn> licCpns = new ArrayList<LicCpn>();
                            if ((finalNwkTypes == null) || (finalNwkTypes.size() < 1)) {
                                licCpns = licCpnRepository.searchVersion2(company, licBusinessType,
                                    year.intValue());
                            } else {
                                for (NetworkType nwt : finalNwkTypes) {
                                    List<LicCpn> tmpLics = licCpnRepository.search(Arrays.asList(company), licBusinessType,
                                        nwt, year.intValue());
                                    licCpns.addAll(tmpLics);
                                }
                            }
                            List<LicCpn> fLicCpns = licCpns;
                            if (finalTmpLicNumber != null) {
                                fLicCpns = licCpns.stream().distinct().filter(finalTmpLicNumber::contains)
                                    .collect(Collectors.toList());
                            }

                            if ((fLicCpns == null) || (fLicCpns.size() < 1)) {
                                continue;
                            }
                            isPresent = true;

                            HashSet<String> hsetLics = new HashSet<String>();
                            for (LicCpn licCpn : fLicCpns) {
                                System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\t======================== with CCDVVT LicCpn: " + licCpn.getLicNumber());

                                TelService telService = new TelService();
                                Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
                                Iterator value = networkTypes.iterator();
                                while (value.hasNext()) {
                                    NetworkType nwt = (NetworkType) value.next();
                                    if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                                        serviceName = nwt.getAlias();
                                        telService.setNetwork_id(nwt.getId());
                                        telService.setNetwork_alias(nwt.getAlias());

                                        if (hServices.get(serviceName) != null) {
                                            telService = hServices.get(serviceName);
                                        }
                                    }
                                }

                                if (!hsetLics.contains(licCpn.getLicNumber())) {
                                    hsetLics.add(licCpn.getLicNumber());
                                    telService.setLic_id((telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                        ? ("" + licCpn.getId()) : (telService.getLic_id() + ";" + licCpn.getId()));
                                    telService.setLic_number((telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                        ? (licCpn.getLicNumber()) : (telService.getLic_number() + ";" + licCpn.getLicNumber()));
                                }
                                telService.setLic_start_date(DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd"));
                                telService.setLic_end_date(DateUtils.timeNowInstant(licCpn.getExpiredDate(), "yyyy-MM-dd"));

                                List<RevenueTelco> revenueTelcos = revenueTelcoRepository.search(company,
                                    Arrays.asList(licCpn), year, null, type);
                                if (revenueTelcos != null) {
                                    System.out.println("Size: " + revenueTelcos);
                                }
                                System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tCompany: " + company.getId() + "\tYear: " + year + "\tLicCpn: " + licCpn.getId());
                                if ((revenueTelcos == null) || (revenueTelcos.size() < 1)) {
                                    revenueTelcos = revenueTelcoRepository.search(company,
                                        Arrays.asList(licCpn), year, Arrays.asList(1L, 2L, 3L, 4L), "quarter");
                                }
                                if (revenueTelcos != null) {
                                    System.out.println("Revenue size: " + revenueTelcos.size());
                                }
                                Long revenueTotal = null;
                                Double needPaidMoney = serviceFeeEstimate("year",
                                    licBusinessType.getName(), licCpn,
                                    year, revenueTelcos);
                                for (RevenueTelco rvn : revenueTelcos) {
                                    if (rvn.getRevenue() != null) {
                                        if (revenueTotal != null) {
                                            revenueTotal += rvn.getRevenue();
                                        } else {
                                            revenueTotal = rvn.getRevenue();
                                        }

                                        yearSummary.setRevenue(yearSummary.getRevenue() + rvn.getRevenue());
                                    }
                                }
                                if (revenueTotal != null) {
                                    if (telService.getRevenue() != null) {
                                        telService.setRevenue(telService.getRevenue() + revenueTotal);
                                    } else {
                                        telService.setRevenue(revenueTotal);
                                    }

                                    telService.setNeed_pay_revenue(Double.valueOf(revenueTotal * 0.5 / 100).longValue());
                                    //// Year summary
                                    yearSummary.setNeed_pay_revenue(yearSummary.getNeed_pay_revenue() + Double.valueOf(revenueTotal * 0.5 / 100).longValue());
                                }
                                if (needPaidMoney != null) {
                                    if (telService.getNeed_pay() != null) {
                                        telService.setNeed_pay(telService.getNeed_pay() + Math.round(needPaidMoney));
                                    } else {
                                        telService.setNeed_pay(Math.round(needPaidMoney));
                                    }

                                    //// Year summary
                                    yearSummary.setNeed_pay(yearSummary.getNeed_pay() + Double.valueOf(needPaidMoney).longValue());
                                }


                                //////// Paid money
                                Long paidMoney = 0L;
                                System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tLicCpn: " + licCpn.getId() + "\tyear: " + year + "\tquarter: " + null + "\tType: " + debtDTO.getType());
                                List<ReceiptServicePayment> rsps = null;
                                rsps = receiptServicePaymentRepository.search(licCpn, company.getId(), licBusinessType,
                                    year, null, null);
                                for (ReceiptServicePayment rsp : rsps) {
                                    System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tPaidMoney: " + rsp.getPaidMoney());
                                    if (rsp.getType().equalsIgnoreCase("quarter")) {
                                        yearSummary.setPaid_money_quarters(yearSummary.getPaid_money_quarters() + rsp.getPaidMoney());
                                        telService.setPaid_money_quarters(telService.getPaid_money_quarters() + rsp.getPaidMoney());
                                    } else if (rsp.getType().equalsIgnoreCase("year")) {
                                        yearSummary.setPaid_money_year(yearSummary.getPaid_money_year() + rsp.getPaidMoney());
                                        telService.setPaid_money_year(telService.getPaid_money_year() + rsp.getPaidMoney());
                                    }
                                    paidMoney += rsp.getPaidMoney();
                                }
                                //
                                yearSummary.setOwned_money(yearSummary.getNeed_pay() - yearSummary.getPaid_money_quarters() - yearSummary.getPaid_money_year());

                                telService.setPaid_money(telService.getPaid_money() + paidMoney);
                                if (ccmPayDeadline[0] == null) {
                                    ccmPayDeadline[0] = DateUtils.nextDay(year, 4L, 105L);
                                    telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                } else {
                                    telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                                }
                                if (needPaidMoney != null) {
                                    //Long tmpOwnedMoney = Double.valueOf(needPaidMoney - paidMoney).longValue();
                                    Double tmpOwnedMoney = needPaidMoney - paidMoney;
                                    if (telService.getOwned_money() != null) {
                                        double sum_needpay = telService.getOwned_money() + Math.round(tmpOwnedMoney);
                                        telService.setOwned_money(Math.round(sum_needpay));
                                    } else {
                                        telService.setOwned_money(Math.round(tmpOwnedMoney));
                                    }
                                }

                                // Year - Quarter - Type
                                telService.setYear(year);
                                telService.setType(debtDTO.getType());

                                hServices.put(serviceName, telService);
                            }

                            for (Map.Entry h : hServices.entrySet()) {
                                TelService v = (TelService) h.getValue();
                                if (v.getRevenue() != null) {
//                                    Long tmpValue = Double.valueOf(v.getRevenue() * 0.5 / 100).longValue();
//                                    if (tmpValue > v.getNeed_pay()) v.setNeed_pay(tmpValue);
//                                    v.setOwned_money(v.getNeed_pay() - v.getPaid_money());
                                    Double tmpValue = v.getRevenue() * 0.5 / 100;
                                    if (tmpValue > v.getNeed_pay()) {
                                        v.setNeed_pay(Math.round(tmpValue));
                                    }
                                    v.setOwned_money(v.getNeed_pay() - v.getPaid_money());
                                }
                                ///// Check chậm nộp
                                if (ccmPayDeadline[0] != null) {
                                    String timeNow = DateUtils.timeNow("yyyy-MM-dd");
                                    if ((timeNow.compareTo(ccmPayDeadline[0]) > 0) && ((v.getOwned_money() == null) || (v.getOwned_money() > 0))) {
                                        isFullPaid = false;
                                    } else {
                                        System.out.println("==================================== Full-paid: " + h.getKey());
                                    }
                                }
                                quarterBusiness.getPaymentServices().add(v);
//                                yearSummary.getPaymentServices().add(v);
                            }

                            /// Warn doc
                            System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tChecking warndoc");
                            List<WarnDoc> warnDocs = warnDocRepository.search(company.getId(), licBusinessType.getId(),
                                year, null, null);
                            String inDocNumber = "";
                            String outDocNumber = "";
                            boolean idxInDocFirst = true;
                            boolean idxOutDocFirst = true;
                            for (WarnDoc warnDoc : warnDocs) {
                                switch (warnDoc.getDoc_type()) {
                                    case "in":
                                        if (idxInDocFirst) {
                                            idxInDocFirst = false;
                                            inDocNumber += warnDoc.getDoc_number();
                                        } else {
                                            inDocNumber += ";" + warnDoc.getDoc_number();
                                        }
                                        break;
                                    case "out":
                                        if (idxOutDocFirst) {
                                            idxOutDocFirst = false;
                                            outDocNumber += warnDoc.getDoc_number();
                                        } else {
                                            outDocNumber += ";" + warnDoc.getDoc_number();
                                        }
                                        break;
                                    default:
                                        continue;
                                }

                            }
                            quarterBusiness.setIn_doc_number(inDocNumber);
                            quarterBusiness.setOut_doc_number(outDocNumber);

                            if ((debtDTO.getFull_paid() != null) && (debtDTO.getFull_paid() == 0)) {
                                if (isFullPaid == false) {
                                    ccdvvt.setYear(quarterBusiness);
                                } else {
                                    isPresent = false;
                                }
                            } else {
                                ccdvvt.setYear(quarterBusiness);
                            }

                            /// Get note
                            System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:ss.SS") + "\tGetting note");
                            Note note = noteRepository.search(company.getId(), licBusinessType.getId(), year, null, "year");
                            ccdvvt.setYear_note(note);
                        }

                        if (isPresent) {
                            yearBusiness.setCcdvvt(ccdvvt);
                            if ((debtDTO.getType().contains("year") && (debtDTO.getType().contains("quarter")))) {
                                yearBusiness.setYear_summary(yearSummary);
                            }
                        }
                    } else if (licBusinessType.getName().equalsIgnoreCase("Giấy phép thiết lập mạng viễn thông công cộng")) {
                        System.out.println("========= Giấy phép thiết lập mạng");
                        boolean isFullPaid = true;
                        String type = "year";
                        String hPaydeadlineKey = company.getName() + "." + licBusinessType.getId() + year;
                        if (hPaydeadline.containsKey(hPaydeadlineKey)) {
                            ccmPayDeadline[0] = hPaydeadline.get(hPaydeadlineKey);
                        } else {
                            ccmPayDeadline[0] = ccmService.getPayDeadline(licBusinessType.getId(), type, year, null);
                            hPaydeadline.put(hPaydeadlineKey, ccmPayDeadline[0]);
                        }
//                        String ccmPayDeadline = ccmService.getPayDeadline(licBusinessType.getId(), debtDTO.getType(), year, null);
                        String service_type = "4";
                        Long company_id = company.getId();
//                        List<CpnDebtOld> licCpns = cpnDebtOldRepository.search(company_id,service_type,year);
                        TLM tlm = new TLM();
                        tlm.setBusiness_type_id(licBusinessType.getId());
                        tlm.setBusiness_type_name(licBusinessType.getName());

                        List<LicCpn> tlmLicCpns = licCpnRepository.search(company, licBusinessType, year.intValue());
                        if ((tlmLicCpns==null)||(tlmLicCpns.size()==0)) continue;

                        List<LicCpn> fLicCpns = tlmLicCpns;
                        if (finalTmpLicNumber != null) {
                            fLicCpns = tlmLicCpns.stream().distinct().filter(finalTmpLicNumber::contains)
                                .collect(Collectors.toList());
                        }

                        if ((fLicCpns == null) || (fLicCpns.size() <= 0)) {
                            continue;
                        }
                        isPresent = true;

                        HashSet<String> hsetLics = new HashSet<String>();
                        for (LicCpn licCpn : fLicCpns) {
                            TelService telService = new TelService();
                            if ((finalTmpLicNumber != null) && (finalTmpLicNumber.size() > 0)) {
                                if (!finalTmpLicNumber.contains(licCpn)) {
                                    continue;
                                }
                            }
                            System.out.println("======================== with TLM LicCpn: " + licCpn.getLicNumber());

                            Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
                            Iterator value = networkTypes.iterator();
                            while (value.hasNext()) {
                                NetworkType nwt = (NetworkType) value.next();
                                if ((nwt.getAlias() != null) && (!nwt.getAlias().isEmpty())) {
                                    telService.setNetwork_id(nwt.getId());
                                    telService.setNetwork_alias(nwt.getAlias());
                                }
                            }

                            telService.setLic_start_date(DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd"));
                            telService.setLic_end_date(DateUtils.timeNowInstant(licCpn.getExpiredDate(), "yyyy-MM-dd"));
                            double needPaidMoney = tlmFeeEstimate(debtDTO.getType(), licCpn, year);
                            String licCreatedDate = DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd");
                            double fee0101 = tlmFee0101Estimate(debtDTO.getType(), licCpn, year);

                            // Check is_paied_0101
                            if ((debtDTO.getIs_paid_0101() != null) && (debtDTO.getIs_paid_0101() == 1L)) {
                                if (fee0101 <= 0) {
                                    continue;
                                }
                            }

                            telService.setNeed_pay_0101(telService.getNeed_pay_0101() + Double.valueOf(fee0101).longValue());
                            if (licCreatedDate.compareTo(DateUtils.timeNow("yyyy-MM-dd")) <= 0) {
                                if (telService.getNeed_pay() != null) {
                                    telService.setNeed_pay(telService.getNeed_pay() + Math.round(needPaidMoney));
                                } else {
                                    telService.setNeed_pay(Math.round(needPaidMoney));
                                }

                                if ((needPaidMoney>0)&&(!hsetLics.contains(licCpn.getLicNumber()))) {
                                    hsetLics.add(licCpn.getLicNumber());
                                    String licId = (telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                        ? "" + licCpn.getId() : (telService.getLic_id() + ";" + licCpn.getId());
                                    telService.setLic_id(licId);
                                    String licNumber = (telService.getLic_number() == null || telService.getLic_number().isEmpty())
                                        ? licCpn.getLicNumber() : (telService.getLic_number() + ";" + licCpn.getLicNumber());
                                    telService.setLic_number(licNumber);
                                }
                            }

                            //////// Paid money
                            Long paidMoney = 0L;
                            List<ReceiptServicePayment> rsps = receiptServicePaymentRepository.search(licCpn, company.getId(), licBusinessType,
                                year, null, "year");
                            for (ReceiptServicePayment rsp : rsps) {
                                paidMoney += rsp.getPaidMoney();
                            }
                            telService.setPaid_money(paidMoney);
                            Long tmpOwnedMoney = (telService.getOwned_money() != null ? telService.getOwned_money() : 0) + Math.round(needPaidMoney - paidMoney);
                            System.out.println("Need pay: " + needPaidMoney + "\tPaid: " + paidMoney + "\tOwned: " + tmpOwnedMoney);
                            if (needPaidMoney <= 0) {
                                continue;
                            }
                            telService.setOwned_money(tmpOwnedMoney);
                            if (ccmPayDeadline[0] == null) {
                                if (telService.getOwned_money() > 0) {
                                    isFullPaid = false;
                                }
                            } else {
                                ///// Check chậm nộp
                                if (ccmPayDeadline[0] != null) {
                                    String timeNow = DateUtils.timeNow("yyyy-MM-dd");
                                    if ((timeNow.compareTo(ccmPayDeadline[0]) > 0) && (telService.getOwned_money() > 0)) {
                                        isFullPaid = false;
                                    }
                                }
                                telService.setPay_deadline(DateUtils.convertFormat(ccmPayDeadline[0], "yyyy-MM-dd", "dd/MM/yyyy"));
                            }
                            /// Warn doc
                            List<WarnDoc> warnDocs = warnDocRepository.search(company.getId(), licBusinessType.getId(),
                                year, null, null);
                            String inDocNumber = "";
                            String outDocNumber = "";
                            boolean idxInDocFirst = true;
                            boolean idxOutDocFirst = true;
                            for (WarnDoc warnDoc : warnDocs) {
                                switch (warnDoc.getDoc_type()) {
                                    case "in":
                                        if (idxInDocFirst) {
                                            idxInDocFirst = false;
                                            inDocNumber += warnDoc.getDoc_number();
                                        } else {
                                            inDocNumber += ";" + warnDoc.getDoc_number();
                                        }
                                        break;
                                    case "out":
                                        if (idxOutDocFirst) {
                                            idxOutDocFirst = false;
                                            outDocNumber += warnDoc.getDoc_number();
                                        } else {
                                            outDocNumber += ";" + warnDoc.getDoc_number();
                                        }
                                        break;
                                    default:
                                        continue;
                                }

                            }
                            tlm.setIn_doc_number(inDocNumber);
                            tlm.setOut_doc_number(outDocNumber);

                            /// Get note
                            Note note = noteRepository.search(company.getId(), licBusinessType.getId(), year, null, "year");
                            tlm.setYear_note(note);

                            if ((debtDTO.getFull_paid() != null) && (debtDTO.getFull_paid() == 0)) {
                                if (isFullPaid == false) {
                                    tlm.getPaymentServices().add(telService);
                                } else {
                                    isPresent = false;
                                }
                            } else {
                                tlm.getPaymentServices().add(telService);
                            }
                        }

                        if (isPresent) {
                            if ((tlm.getPaymentServices() != null) && (tlm.getPaymentServices().size() > 0)) {
                                // Nếu doanh nghiệp có dịch vụ phải nộp phí
                                yearBusiness.setTlm(tlm);
                            } else {
                                // Nếu doanh nghiệp ko có dịch vụ phải nộp phí ==> bỏ qua
                                isPresent = false;
                            }
                        }
                    }
                }
                respCompPayment.getYears().add(yearBusiness);
                System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:SS") + "\t" + company.getId() + "\t" + company.getName() + " at Year: " + year);

            }
            if (isPresent) {
                returnList.add(respCompPayment);
//                if ((debtDTO.getIndex()!=null)&&(debtDTO.getSize()!=null)) {
//                    if (returnList.size() >= debtDTO.getSize()) {
////                        System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:SS") + "\tEnough!");
////                        return returnList;
//                        return new PageImpl<RespCompPayment>(
//                            returnList,
//                            PageRequest.of(debtDTO.getIndex().intValue(), debtDTO.getSize().intValue()),
//                            companies.size()
//                        );
//                    }
//                }
            }
            System.out.println(DateUtils.timeNow("yyyy-MM-dd HH:mm:SS") + "\tfinish for " + company.getName());
        });

        Collections.sort(returnList);
        return returnList;
    }

    @Override
    public XSSFWorkbook generateReceiptSummary(SearchReceiptDTO search) {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Tờ khai thu phí,lệ phí");
        //////// Font
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setFontName("Times New Roman");
        headerFont.setBold(true);
        headerFont.setItalic(false);

        XSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 12);
        titleFont.setFontName("Times New Roman");
        titleFont.setBold(true);
        titleFont.setItalic(false);

        XSSFFont boldFont = workbook.createFont();
        boldFont.setFontHeightInPoints((short) 12);
        boldFont.setFontName("Times New Roman");
        boldFont.setBold(true);

        XSSFFont boldItalicFont = workbook.createFont();
        boldItalicFont.setFontHeightInPoints((short) 12);
        boldItalicFont.setFontName("Times New Roman");
        boldItalicFont.setBold(true);
        boldItalicFont.setItalic(true);

        XSSFFont italicFont = workbook.createFont();
        italicFont.setFontHeightInPoints((short) 12);
        italicFont.setFontName("Times New Roman");
        italicFont.setItalic(true);

        //// header font
        XSSFFont normalFont = workbook.createFont();
        normalFont.setFontHeightInPoints((short) 12);
        normalFont.setFontName("Times New Roman");

        ///////// Set Column size
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4500);
        sheet.setColumnWidth(4, 15000);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 2000);
        //////// Style
        CellStyle h1Style = workbook.createCellStyle();;
        h1Style.setAlignment(HorizontalAlignment.CENTER);
        h1Style.setFont(boldFont);

        CellStyle h2Style = workbook.createCellStyle();;
        h2Style.setAlignment(HorizontalAlignment.CENTER);
        h2Style.setFont(boldItalicFont);

        CellStyle h3Style = workbook.createCellStyle();;
        h3Style.setAlignment(HorizontalAlignment.CENTER);
        h3Style.setFont(italicFont);

        CellStyle menuStyle = workbook.createCellStyle();;
        menuStyle.setAlignment(HorizontalAlignment.CENTER);
        menuStyle.setFont(boldFont);
        menuStyle.setBorderBottom(BorderStyle.THIN);
        menuStyle.setBorderLeft(BorderStyle.THIN);
        menuStyle.setBorderRight(BorderStyle.THIN);
        menuStyle.setBorderTop(BorderStyle.THIN);

        CellStyle subMenuStyle1 = workbook.createCellStyle();;
        subMenuStyle1.setAlignment(HorizontalAlignment.LEFT);
        subMenuStyle1.setFont(boldFont);

        CellStyle subMenuStyle2 = workbook.createCellStyle();;
        subMenuStyle2.setAlignment(HorizontalAlignment.RIGHT);
        DataFormat format = workbook.createDataFormat();
        subMenuStyle2.setDataFormat(format.getFormat("#,##0"));
        subMenuStyle2.setFont(boldFont);
        subMenuStyle2.setBorderBottom(BorderStyle.THIN);
        subMenuStyle2.setBorderLeft(BorderStyle.THIN);
        subMenuStyle2.setBorderRight(BorderStyle.THIN);
        subMenuStyle2.setBorderTop(BorderStyle.THIN);

        CellStyle valueCenterStyle = workbook.createCellStyle();;
        valueCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        valueCenterStyle.setFont(normalFont);
        valueCenterStyle.setBorderBottom(BorderStyle.THIN);
        valueCenterStyle.setBorderLeft(BorderStyle.THIN);
        valueCenterStyle.setBorderRight(BorderStyle.THIN);
        valueCenterStyle.setBorderTop(BorderStyle.THIN);

        CellStyle valueLeftStyle = workbook.createCellStyle();;
        valueLeftStyle.setAlignment(HorizontalAlignment.LEFT);
        valueLeftStyle.setFont(normalFont);
        valueLeftStyle.setBorderBottom(BorderStyle.THIN);
        valueLeftStyle.setBorderLeft(BorderStyle.THIN);
        valueLeftStyle.setBorderRight(BorderStyle.THIN);
        valueLeftStyle.setBorderTop(BorderStyle.THIN);

        CellStyle valueRightStyle = workbook.createCellStyle();;
        valueRightStyle.setAlignment(HorizontalAlignment.RIGHT);
        valueRightStyle.setFont(normalFont);
        valueRightStyle.setBorderBottom(BorderStyle.THIN);
        valueRightStyle.setBorderLeft(BorderStyle.THIN);
        valueRightStyle.setBorderRight(BorderStyle.THIN);
        valueRightStyle.setBorderTop(BorderStyle.THIN);

        CellStyle normalStyle = workbook.createCellStyle();
        normalStyle.setFont(normalFont);

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(format.getFormat("#,##0"));
        currencyStyle.setFont(normalFont);
        currencyStyle.setBorderBottom(BorderStyle.THIN);
        currencyStyle.setBorderLeft(BorderStyle.THIN);
        currencyStyle.setBorderRight(BorderStyle.THIN);
        currencyStyle.setBorderTop(BorderStyle.THIN);

        ////// Header
        final Row[] fRow = {sheet.createRow(0)};
        Cell cell = fRow[0].createCell(0);
        cell.setCellValue("TỜ KHAI CHI TIẾT THU PHÍ, LỆ PHÍ");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        cell.setCellStyle(h1Style);

        fRow[0] = sheet.createRow(1);
        cell = fRow[0].createCell(0);
        if ((search.getType() != null) && (search.getType().equalsIgnoreCase("year"))) {
            cell.setCellValue("Năm " + search.getYear());
        } else {
            cell.setCellValue("Tháng " + (search.getMonth() >= 10 ? search.getMonth() : "0" + search.getMonth())
                + " năm " + search.getYear());
        }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
        cell.setCellStyle(h2Style);

        fRow[0] = sheet.createRow(2);
        cell = fRow[0].createCell(0);
        if ((search.getType() != null) && (search.getType().equalsIgnoreCase("year"))) {
            cell.setCellValue("(Kèm theo tờ khai phí,lệ phí Năm " + search.getYear() + ")");
        } else {
            cell.setCellValue("(Kèm theo tờ khai phí,lệ phí Tháng " + (search.getMonth() >= 10 ? search.getMonth() : "0" + search.getMonth())
                + " năm " + search.getYear() + ")");
        }
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
        cell.setCellStyle(h3Style);

        fRow[0] = sheet.createRow(3);
        cell = fRow[0].createCell(5);
        cell.setCellValue("Đơn vị tính: đồng");
        cell.setCellStyle(normalStyle);

        //// Data header
        Row hRow = sheet.createRow(4);
        Cell hSTTCell = hRow.createCell(0);
        Cell hSign = hRow.createCell(1);
        Cell hReceiptNumber = hRow.createCell(2);
        Cell hReceiptDate = hRow.createCell(3);
        Cell hReceiptMenu = hRow.createCell(4);
        Cell hReceiptFee = hRow.createCell(5);
        Cell hNote = hRow.createCell(6);
        hSTTCell.setCellValue("STT");
        hSign.setCellValue("Ký hiệu");
        hReceiptNumber.setCellValue("Biên lai");
        hReceiptDate.setCellValue("Ngày tháng");
        hReceiptMenu.setCellValue("Danh mục phí, lệ phí/Tên đối tượng nộp tiền");
        hReceiptFee.setCellValue("Tổng số tiền thu");
        hNote.setCellValue("Ghi chú");
        hSTTCell.setCellStyle(menuStyle);
        hSign.setCellStyle(menuStyle);
        hReceiptNumber.setCellStyle(menuStyle);
        hReceiptDate.setCellStyle(menuStyle);
        hReceiptMenu.setCellStyle(menuStyle);
        hReceiptFee.setCellStyle(menuStyle);
        hNote.setCellStyle(menuStyle);
        ///// Value
        String totalSum = "SUM(";
        int rowIdx = 5;

        ///// 1	Phí thẩm định điều kiện hoạt động viễn thông đối với đài vô tuyến điện thuộc công trình viễn thông
        fRow[0] = sheet.createRow(rowIdx);
        Cell cell01 = fRow[0].createCell(0);
        cell01.setCellValue(1);
        cell01.setCellStyle(menuStyle);
        Cell cellMenu = fRow[0].createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
        cellMenu.setCellValue("Phí thẩm định điều kiện hoạt động viễn thông đối với đài vô tuyến điện thuộc công trình viễn thông");
        cellMenu.setCellStyle(subMenuStyle1);
        Cell cellTotal = fRow[0].createCell(5);
        cellTotal.setCellStyle(subMenuStyle2);
        Cell cell06 = fRow[0].createCell(6);
        cell06.setCellStyle(menuStyle);
        rowIdx++;

        for (int idx = 1; idx < 3; idx++) {
            fRow[0] = sheet.createRow(rowIdx);
            Cell cell0 = fRow[0].createCell(0);
            cell0.setCellValue(idx);
            cell0.setCellStyle(valueCenterStyle);
            for (int i = 1; i <= 6; i++) {
                Cell celli = fRow[0].createCell(i);
                if (i == 4) {
                    celli.setCellStyle(valueLeftStyle);
                } else if (i == 5) {
                    celli.setCellStyle(valueRightStyle);
                } else {
                    celli.setCellStyle(valueCenterStyle);
                }
            }
            rowIdx++;
        }
        cellTotal.setCellFormula("SUM(F" + (rowIdx - 1) + ":F" + (rowIdx) + ")");
        totalSum += cellTotal.getAddress() + ",";

        ///// 2	Lệ phí cấp giấy đăng ký công bố hợp quy
        fRow[0] = sheet.createRow(rowIdx);
        cell01 = fRow[0].createCell(0);
        cell01.setCellValue(2);
        cell01.setCellStyle(menuStyle);
        cellMenu = fRow[0].createCell(1);
        cellMenu.setCellValue("Lệ phí cấp giấy đăng ký công bố hợp quy");
        cellMenu.setCellStyle(subMenuStyle1);
        CellRangeAddress cellMenuMerge = new CellRangeAddress(rowIdx, rowIdx, 1, 4);
        sheet.addMergedRegion(cellMenuMerge);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellMenuMerge, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellMenuMerge, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellMenuMerge, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellMenuMerge, sheet);

        cellTotal = fRow[0].createCell(5);
        cellTotal.setCellStyle(subMenuStyle2);
        cell06 = fRow[0].createCell(6);
        cell06.setCellStyle(menuStyle);
        rowIdx++;

        for (int idx = 1; idx < 3; idx++) {
            fRow[0] = sheet.createRow(rowIdx);
            Cell cell0 = fRow[0].createCell(0);
            cell0.setCellValue(idx);
            cell0.setCellStyle(valueCenterStyle);
            for (int i = 1; i <= 6; i++) {
                Cell celli = fRow[0].createCell(i);
                if (i == 4) {
                    celli.setCellStyle(valueLeftStyle);
                } else if (i == 5) {
                    celli.setCellStyle(valueRightStyle);
                } else {
                    celli.setCellStyle(valueCenterStyle);
                }
            }
            rowIdx++;
        }
        cellTotal.setCellFormula("SUM(F" + (rowIdx - 1) + ":F" + (rowIdx) + ")");
        totalSum += cellTotal.getAddress() + ",";

        ///// 3	Phí sử dụng mã, số viễn thông
        fRow[0] = sheet.createRow(rowIdx);
        cell01 = fRow[0].createCell(0);
        cell01.setCellValue(3);
        cell01.setCellStyle(menuStyle);
        cellMenu = fRow[0].createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
        cellMenu.setCellValue("Phí sử dụng mã, số viễn thông");
        cellMenu.setCellStyle(subMenuStyle1);
        cellTotal = fRow[0].createCell(5);
        cellTotal.setCellStyle(subMenuStyle2);
        cell06 = fRow[0].createCell(6);
        cell06.setCellStyle(menuStyle);
        rowIdx++;

        for (int idx = 1; idx < 3; idx++) {
            fRow[0] = sheet.createRow(rowIdx);
            Cell cell0 = fRow[0].createCell(0);
            cell0.setCellValue(idx);
            cell0.setCellStyle(valueCenterStyle);
            for (int i = 1; i <= 6; i++) {
                Cell celli = fRow[0].createCell(i);
                if (i == 4) {
                    celli.setCellStyle(valueLeftStyle);
                } else if (i == 5) {
                    celli.setCellStyle(valueRightStyle);
                } else {
                    celli.setCellStyle(valueCenterStyle);
                }
            }
            rowIdx++;
        }
        cellTotal.setCellFormula("SUM(F" + (rowIdx - 1) + ":F" + (rowIdx) + ")");
        totalSum += cellTotal.getAddress() + ",";

        ///// 4	Lệ phí phân bổ mã, số viễn thông
        fRow[0] = sheet.createRow(rowIdx);
        cell01 = fRow[0].createCell(0);
        cell01.setCellValue(4);
        cell01.setCellStyle(menuStyle);
        cellMenu = fRow[0].createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
        cellMenu.setCellValue("Lệ phí phân bổ mã, số viễn thông");
        cellMenu.setCellStyle(subMenuStyle1);
        cellTotal = fRow[0].createCell(5);
        cellTotal.setCellStyle(subMenuStyle2);
        cell06 = fRow[0].createCell(6);
        cell06.setCellStyle(menuStyle);
        rowIdx++;

        for (int idx = 1; idx < 3; idx++) {
            fRow[0] = sheet.createRow(rowIdx);
            Cell cell0 = fRow[0].createCell(0);
            cell0.setCellValue(idx);
            cell0.setCellStyle(valueCenterStyle);
            for (int i = 1; i <= 6; i++) {
                Cell celli = fRow[0].createCell(i);
                if (i == 4) {
                    celli.setCellStyle(valueLeftStyle);
                } else if (i == 5) {
                    celli.setCellStyle(valueRightStyle);
                } else {
                    celli.setCellStyle(valueCenterStyle);
                }
            }
            rowIdx++;
        }
        cellTotal.setCellFormula("SUM(F" + (rowIdx - 1) + ":F" + (rowIdx) + ")");
        totalSum += cellTotal.getAddress() + ",";

        ///// 5	Phí Quyền hoạt động viễn thông
        fRow[0] = sheet.createRow(rowIdx);
        cell01 = fRow[0].createCell(0);
        cell01.setCellValue(5);
        cell01.setCellStyle(menuStyle);
        cellMenu = fRow[0].createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
        cellMenu.setCellValue("Phí Quyền hoạt động viễn thông");
        cellMenu.setCellStyle(subMenuStyle1);
        cellTotal = fRow[0].createCell(5);
        cellTotal.setCellStyle(subMenuStyle2);
        cell06 = fRow[0].createCell(6);
        cell06.setCellStyle(menuStyle);
        rowIdx++;

        List<Receipt> receipts = receiptRepository.searchOrderByReceiptDate(null,
            (search.getYear() != null) ? Integer.valueOf(search.getYear().intValue()) : null,
            (search.getMonth() != null) ? Integer.valueOf(search.getMonth().intValue()) : null);
        int receiptNumber = 0;
        int k = 1;

        ExecutorService service = Executors.newCachedThreadPool();
        for (Receipt receipt : receipts) {
            int finalK = k;
            int finalRowIdx = rowIdx;
            Future<Boolean> res = service.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    List<ReceiptServicePayment> rsps = receiptServicePaymentRepository.findByReceiptAndLicBusinessType3Or4(receipt);
                    if ((rsps == null) || (rsps.size() < 1)) {
                        return false;
                    }

                    fRow[0] = sheet.createRow(finalRowIdx);
                    Cell cell0 = fRow[0].createCell(0);
                    cell0.setCellValue(finalK);
                    cell0.setCellStyle(valueCenterStyle);
                    System.out.println("Số thứ tự là: " + finalK);
                    //// Ký hiệu
                    Cell cell1 = fRow[0].createCell(1);
                    cell1.setCellValue(receipt.getReceiptName());
                    cell1.setCellStyle(valueCenterStyle);
                    //// Biên lai
                    Cell cell2 = fRow[0].createCell(2);
                    cell2.setCellValue(receipt.getReceiptNumber());
                    cell2.setCellStyle(valueCenterStyle);
                    //// Ngày tháng
                    Cell cell3 = fRow[0].createCell(3);
                    cell3.setCellValue(DateUtils.convertFormat(
                        receipt.getReceiptDate(), "yyyy-MM-dd", "dd/MM/yyyy"));
                    cell3.setCellStyle(valueCenterStyle);
                    //// Danh mục
                    Cell cell4 = fRow[0].createCell(4);
                    cell4.setCellValue(receipt.getCompany().getName());
                    cell4.setCellStyle(valueLeftStyle);
                    //// Tổng tiền
                    Cell cell5 = fRow[0].createCell(5);
                    cell5.setCellValue(receipt.getAmountOfMoney());
                    cell5.setCellStyle(currencyStyle);
                    //// Ghi chú
                    Cell cell6 = fRow[0].createCell(6);
                    cell6.setCellStyle(valueCenterStyle);

                    return true;
                }
            });

            try {
                if (res.get()) {
                    k++;
                    rowIdx++;
                    receiptNumber++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        service.shutdown();

//        for (Receipt receipt : receipts) {
//            List<ReceiptServicePayment> rsps = receiptServicePaymentRepository.findByReceiptAndLicBusinessType3Or4(receipt);
//            if ((rsps == null) || (rsps.size() < 1)) {
//                continue;
//            }
//            else{
//                k++;
//            }
//            fRow[0] = sheet.createRow(rowIdx);
//            Cell cell0 = fRow[0].createCell(0);
//            cell0.setCellValue(k);
//            cell0.setCellStyle(valueCenterStyle);
//            System.out.println("Số thứ tự là: " + k);
//            //// Ký hiệu
//            Cell cell1 = fRow[0].createCell(1);
//            cell1.setCellValue(receipt.getReceiptName());
//            cell1.setCellStyle(valueCenterStyle);
//            //// Biên lai
//            Cell cell2 = fRow[0].createCell(2);
//            cell2.setCellValue(receipt.getReceiptNumber());
//            cell2.setCellStyle(valueCenterStyle);
//            //// Ngày tháng
//            Cell cell3 = fRow[0].createCell(3);
//            cell3.setCellValue(DateUtils.convertFormat(
//                           receipt.getReceiptDate(), "yyyy-MM-dd", "dd/MM/yyyy"));
//            cell3.setCellStyle(valueCenterStyle);
//            //// Danh mục
//            Cell cell4 = fRow[0].createCell(4);
//            cell4.setCellValue(receipt.getCompany().getName());
//            cell4.setCellStyle(valueLeftStyle);
//            //// Tổng tiền
//            Cell cell5 = fRow[0].createCell(5);
//            cell5.setCellValue(receipt.getAmountOfMoney());
//            cell5.setCellStyle(currencyStyle);
//            //// Ghi chú
//            Cell cell6 = fRow[0].createCell(6);
//            cell6.setCellStyle(valueCenterStyle);
//
//            rowIdx++;
//            receiptNumber++;
//        }

        cellTotal.setCellFormula("SUM(F" + (rowIdx - receiptNumber + 1) + ":F" + (rowIdx) + ")");
        totalSum += cellTotal.getAddress() + ",";

        ///// 6	Lệ phí cấp giấy phép kinh doanh dịch vụ viễn thông và giấy phép nghiệp vụ viễn thông
        fRow[0] = sheet.createRow(rowIdx);
        cell01 = fRow[0].createCell(0);
        cell01.setCellValue(6);
        cell01.setCellStyle(menuStyle);
        cellMenu = fRow[0].createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
        cellMenu.setCellValue("Lệ phí cấp giấy phép kinh doanh dịch vụ viễn thông và giấy phép nghiệp vụ viễn thông");
        cellMenu.setCellStyle(subMenuStyle1);
        cellTotal = fRow[0].createCell(5);
        cellTotal.setCellStyle(subMenuStyle2);
        cell06 = fRow[0].createCell(6);
        cell06.setCellStyle(menuStyle);
        rowIdx++;

        for (int idx = 1; idx < 3; idx++) {
            fRow[0] = sheet.createRow(rowIdx);
            Cell cell0 = fRow[0].createCell(0);
            cell0.setCellValue(idx);
            cell0.setCellStyle(valueCenterStyle);
            for (int i = 1; i <= 6; i++) {
                Cell celli = fRow[0].createCell(i);
                if (i == 4) {
                    celli.setCellStyle(valueLeftStyle);
                } else if (i == 5) {
                    celli.setCellStyle(valueRightStyle);
                } else {
                    celli.setCellStyle(valueCenterStyle);
                }
            }
            rowIdx++;
        }
        cellTotal.setCellFormula("SUM(F" + (rowIdx - 1) + ":F" + (rowIdx) + ")");
        totalSum += cellTotal.getAddress() + ",";

        ///// 7	Phí Quyền hoạt động viễn thông (Phí sửa chữa bảo dưỡng các công trình cáp)
        fRow[0] = sheet.createRow(rowIdx);
        cell01 = fRow[0].createCell(0);
        cell01.setCellValue(7);
        cell01.setCellStyle(menuStyle);
        cellMenu = fRow[0].createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
        cellMenu.setCellValue("Phí Quyền hoạt động viễn thông (Phí sửa chữa bảo dưỡng các công trình cáp)");
        cellMenu.setCellStyle(subMenuStyle1);
        cellTotal = fRow[0].createCell(5);
        cellTotal.setCellStyle(subMenuStyle2);
        cell06 = fRow[0].createCell(6);
        cell06.setCellStyle(menuStyle);
        rowIdx++;

        for (int idx = 1; idx < 3; idx++) {
            fRow[0] = sheet.createRow(rowIdx);
            Cell cell0 = fRow[0].createCell(0);
            cell0.setCellValue(idx);
            cell0.setCellStyle(valueCenterStyle);
            for (int i = 1; i <= 6; i++) {
                Cell celli = fRow[0].createCell(i);
                if (i == 4) {
                    celli.setCellStyle(valueLeftStyle);
                } else if (i == 5) {
                    celli.setCellStyle(valueRightStyle);
                } else {
                    celli.setCellStyle(valueCenterStyle);
                }
            }
            rowIdx++;
        }
        cellTotal.setCellFormula("SUM(F" + (rowIdx - 1) + ":F" + (rowIdx) + ")");
        totalSum += cellTotal.getAddress() + ")";

        //////
        fRow[0] = sheet.createRow(rowIdx);
        cell = fRow[0].createCell(4);
        if ((search.getType() != null) && (search.getType().equalsIgnoreCase("year"))) {
            cell.setCellValue("Tổng cộng thu  Năm " + search.getYear());
        } else {
            cell.setCellValue("Tổng cộng thu Tháng " + (search.getMonth() >= 10 ? search.getMonth() : "0" + search.getMonth())
                + "/" + search.getYear());
        }
        cell.setCellStyle(menuStyle);

        cellTotal = fRow[0].createCell(5);
        cellTotal.setCellStyle(currencyStyle);
        cellTotal.setCellFormula(totalSum);

        rowIdx++;

        ////////// Signature area
        fRow[0] = sheet.createRow(++rowIdx);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 4, 5));
        cell = fRow[0].createCell(4);
        cell.setCellValue("Hà nội, ngày " + DateUtils.timeNow("dd")
            + " tháng " + DateUtils.timeNow("MM")
            + " năm " + DateUtils.timeNow("yyyy"));
        cell.setCellStyle(h1Style);
        rowIdx++;

        fRow[0] = sheet.createRow(++rowIdx);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 4, 5));
        cell = fRow[0].createCell(4);
        cell.setCellValue("Đại diện đơn vị");
        cell.setCellStyle(h1Style);
        rowIdx++;
        fRow[0] = sheet.createRow(rowIdx);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 4, 5));
        cell = fRow[0].createCell(4);
        cell.setCellValue("(Ký tên, đóng dấu)");
        cell.setCellStyle(h3Style);
        rowIdx++;
        rowIdx += 4;
        fRow[0] = sheet.createRow(rowIdx);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 4, 5));
        cell = fRow[0].createCell(4);
        cell.setCellValue("Hoàng Minh Cường");
        cell.setCellStyle(h1Style);
        rowIdx++;

        return workbook;
    }

    @Override
    public XSSFWorkbook generateTaxSummary(SearchReceiptDTO search) throws IOException {
        List<Receipt> receipts = receiptRepository.search(null,
            (search.getYear() != null) ? Integer.valueOf(search.getYear().intValue()) : null,
            (search.getMonth() != null) ? Integer.valueOf(search.getMonth().intValue()) : null);
        final Long[] totalFee = {0L};

        ExecutorService service = Executors.newCachedThreadPool();
        for (Receipt receipt : receipts) {
            Future<Long> totalFuture = service.submit(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    List<ReceiptServicePayment> rsps = receiptServicePaymentRepository.findByReceiptAndLicBusinessType3Or4(receipt);
                    return ((rsps == null) || (rsps.size() < 1)) ? 0L : receipt.getAmountOfMoney();
                }
            });
            try {
                totalFee[0] += totalFuture.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        service.shutdown();

//        for (Receipt receipt : receipts) {
//            List<ReceiptServicePayment> rsps = receiptServicePaymentRepository.findByReceiptAndLicBusinessType3Or4(receipt);
//            if ((rsps == null) || (rsps.size() < 1)) {
//                continue;
//            }
//
//            totalFee[0] += receipt.getAmountOfMoney();
//        }

        // Create a blank sheet
        FileInputStream fileTemplate = null;
        Map<String, String> maps = new HashMap<String, String>();
        if ((search.getType() != null) && (search.getType().equalsIgnoreCase("year"))) {
            fileTemplate = new FileInputStream(templateDir + "/" + tokhaithuenam);
            maps.put("#year#", "Năm " + search.getYear());
        } else {
            fileTemplate = new FileInputStream(templateDir + "/" + tokhaithuethang);
            maps.put("#month#", "Tháng " + (search.getMonth() < 10 ? "0" + search.getMonth() : search.getMonth())
                + " năm " + search.getYear());
            String currentDate = "Ngày " + DateUtils.timeNow("dd")
                + " tháng " + DateUtils.timeNow("MM")
                + " năm " + DateUtils.timeNow("yyyy");
            maps.put("#current_date#", currentDate);
        }
        maps.put("#total_fee#", "" + totalFee[0]);

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook(fileTemplate);
        XSSFSheet toKhaiSheet = workbook.getSheet("Tờ khai");
        if (toKhaiSheet == null) {
            return null;
        }
        for (Row row : toKhaiSheet) {
            for (Cell cell : row) {
                if ((cell.getCellType() == CellType.STRING) && (cell.getStringCellValue() != null)) {
                    String value = cell.getStringCellValue().trim();
                    if (maps.containsKey(value)) {
                        if (value.equalsIgnoreCase("#total_fee#")) {
                            cell.setCellType(CellType.NUMERIC);
                            CellStyle style = cell.getCellStyle();
                            DataFormat format = workbook.createDataFormat();
                            style.setDataFormat(format.getFormat("#,##0"));
                            cell.setCellValue(Long.valueOf(maps.get(value)));
                        } else {
                            cell.setCellValue(maps.get(value));
                        }
                    }
                }
            }
        }
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);

        return workbook;
    }

}
