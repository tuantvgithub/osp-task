package co.osp.base.businessservice.service.impl;

import co.osp.base.businessservice.dto.*;
import co.osp.base.businessservice.entity.*;
import co.osp.base.businessservice.service.*;
import co.osp.base.businessservice.utils.DateUtil;
import co.osp.base.businessservice.repository.*;
import co.osp.base.businessservice.utils.CurrencyUtils;
import co.osp.base.businessservice.utils.DateUtils;
import co.osp.base.businessservice.utils.WordUtils;
import io.github.jhipster.service.filter.LongFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;


@Service
@Transactional
public class RevenueTelcoServiceImpl implements RevenueTelcoService {

    @Value("${app.savedDir}")
    private String savedDir;
    @Value("${app.templateDir}")
    private String templateDir;
    @Value("${app.ccdvvtQuarterWarn1}")
    private String ccdvvtQuarterWarn1;
    @Value("${app.ccdvvtQuarterWarn21}")
    private String ccdvvtQuarterWarn21;
    @Value("${app.ccdvvtQuarterWarn22}")
    private String ccdvvtQuarterWarn22;
    @Value("${app.ccdvvtYearWarn1}")
    private String ccdvvtYearWarn1;
    @Value("${app.ccdvvtYearWarn21}")
    private String ccdvvtYearWarn21;
    @Value("${app.ccdvvtYearWarn22}")
    private String ccdvvtYearWarn22;
    @Value("${app.tlmWarn1}")
    private String tlmWarn1;
    @Value("${app.tlmWarn21}")
    private String tlmWarn21;
    @Value("${app.tlmWarn22}")
    private String tlmWarn22;
    @Value("${app.phiKhoSo21}")
    private String phiKhoSo21;
    @Value("${app.phiKhoSo22}")
    private String phiKhoSo22;
    @Value("${app.doichieucongno}")
    private String doichieucongno;

    private final RevenueTelcoRepository revenueTelcoRepository;
    private final LicCpnRepository licCpnRepository;
    private final FileService fileService;
    private final LicBusinessTypeRepository licBusinessTypeRepository;
    private final WarnDocRepository warnDocRepository;
    private final CpnCommonManagerRepository ccmRepository;
    private final CompanyService companyService;
    private final ReceiptTelcoService receiptTelcoService;
    private final TlcCpnCodeQueryService tlcCpnCodeQueryService;

    @Autowired
    public RevenueTelcoServiceImpl(
                RevenueTelcoRepository revenueTelcoRepository,
                LicCpnRepository licCpnRepository,
                FileService fileService,
                LicBusinessTypeRepository licBusinessTypeRepository,
                WarnDocRepository warnDocRepository,
                CpnCommonManagerRepository ccmRepository,
                CompanyService companyService,
                ReceiptTelcoService receiptTelcoService,
                TlcCpnCodeQueryService tlcCpnCodeQueryService
    ) {
        this.revenueTelcoRepository = revenueTelcoRepository;
        this.licCpnRepository = licCpnRepository;
        this.fileService = fileService;
        this.licBusinessTypeRepository = licBusinessTypeRepository;
        this.warnDocRepository = warnDocRepository;
        this.ccmRepository = ccmRepository;
        this.companyService = companyService;
        this.receiptTelcoService = receiptTelcoService;
        this.tlcCpnCodeQueryService = tlcCpnCodeQueryService;
    }

    private XSSFWorkbook genExcel(List<RevenueTelcoReport> revenueTelcoReports, String title)
    {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Báo cáo doanh thu");
        //////// Font
        XSSFFont titleFont= workbook.createFont();
        titleFont.setFontHeightInPoints((short)16);
        titleFont.setFontName("Times New Roman");
        titleFont.setBold(true);
        titleFont.setItalic(false);
        //// header font
        XSSFFont headerFont= workbook.createFont();
        headerFont.setFontHeightInPoints((short)13);
        headerFont.setFontName("Times New Roman");
        headerFont.setBold(true);
        headerFont.setItalic(false);
        ///////// Set Column size
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 15000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 10000);
        sheet.setColumnWidth(4, 5000);
        //////// Style
        CellStyle style=workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        CellStyle headerStyle=workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);

        Row fRow = sheet.createRow(0);
        Cell cell = fRow.createCell(0);
        //cell.setCellValue("SỐ LIỆU BÁO CÁO DOANH THU DỊCH VỤ VIỄN THÔNG QUÝ " + quarter + "/" + year);
        cell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,4));
        style.setFont(titleFont);
        cell.setCellStyle(style);
        ////// Header
        Row hRow = sheet.createRow(1);
        Cell hSTTCell = hRow.createCell(0);
        Cell hCompanyNameCell = hRow.createCell(1);
        Cell hLicNumberCell = hRow.createCell(2);
        Cell hServiceNameCell = hRow.createCell(3);
        Cell hRevenueCell = hRow.createCell(4);
        hSTTCell.setCellValue("STT");
        hCompanyNameCell.setCellValue("Tên doanh nghiệp");
        hLicNumberCell.setCellValue("Số GP");
        hServiceNameCell.setCellValue("Nội dung");
        hRevenueCell.setCellValue("Doanh thu");
        headerStyle.setFont(headerFont);
        hSTTCell.setCellStyle(headerStyle);
        hCompanyNameCell.setCellStyle(headerStyle);
        hLicNumberCell.setCellStyle(headerStyle);
        hServiceNameCell.setCellStyle(headerStyle);
        hRevenueCell.setCellStyle(headerStyle);

        ///// Value
        int idx = 1;
        int rowIdx = 2;

        DataFormat format = workbook.createDataFormat();
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(format.getFormat("#,##0"));
        currencyStyle.setBorderBottom(BorderStyle.THIN);
        currencyStyle.setBorderLeft(BorderStyle.THIN);
        currencyStyle.setBorderRight(BorderStyle.THIN);
        currencyStyle.setBorderTop(BorderStyle.THIN);
        currencyStyle.setWrapText(true);

        XSSFFont valueFont= workbook.createFont();
        valueFont.setFontHeightInPoints((short)13);
        valueFont.setFontName("Times New Roman");
        valueFont.setBold(false);
        valueFont.setItalic(false);

        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setFont(valueFont);
        centerStyle.setBorderBottom(BorderStyle.THIN);
        centerStyle.setBorderLeft(BorderStyle.THIN);
        centerStyle.setBorderRight(BorderStyle.THIN);
        centerStyle.setBorderTop(BorderStyle.THIN);
        centerStyle.setWrapText(true);

        CellStyle leftStyle = workbook.createCellStyle();
        leftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        leftStyle.setAlignment(HorizontalAlignment.LEFT);
        leftStyle.setFont(valueFont);
        leftStyle.setBorderBottom(BorderStyle.THIN);
        leftStyle.setBorderLeft(BorderStyle.THIN);
        leftStyle.setBorderRight(BorderStyle.THIN);
        leftStyle.setBorderTop(BorderStyle.THIN);
        leftStyle.setWrapText(true);

        for (RevenueTelcoReport revenueTelcoReport : revenueTelcoReports)
        {
            System.out.println("============== " + revenueTelcoReport.getCompany().getName());
            boolean first = true;
            for (RevenueTelcoServiceReport revenueTelcoServiceReport : revenueTelcoReport.getServiceRevenueReports())
            {
                //// Create row
                Row row = sheet.createRow(rowIdx);
                if (first)
                {
                    Cell noCell = row.createCell(0, CellType.NUMERIC);
                    noCell.setCellValue(idx);
                    noCell.setCellStyle(centerStyle);

                    // Company name Cell
                    Cell companyCell = row.createCell(1, CellType.STRING);
                    companyCell.setCellValue(revenueTelcoReport.getCompany().getName());
                    companyCell.setCellStyle(leftStyle);

                    first = false;
                    idx++;
                }
                // Lic number (Số giấy phép)
                Cell licNumberCell = row.createCell(2, CellType.STRING);
                licNumberCell.setCellValue(revenueTelcoServiceReport.getLicNumber());
                licNumberCell.setCellStyle(centerStyle);
                // Service name (nội dung)
                Cell serviceNameCell = row.createCell(3, CellType.STRING);
                serviceNameCell.setCellValue(revenueTelcoServiceReport.getServiceType());
                serviceNameCell.setCellStyle(centerStyle);
                // Revenue
                Cell revenueCell = row.createCell(4);
                revenueCell.setCellStyle(currencyStyle);
                if( revenueTelcoServiceReport.getRevenue() == null){
                    revenueCell.setCellValue("-");
                } else {
                    revenueCell.setCellValue(revenueTelcoServiceReport.getRevenue());
                }


                // Company name
                if (revenueTelcoReport.getServiceRevenueReports().indexOf(revenueTelcoServiceReport) == (revenueTelcoReport.getServiceRevenueReports().size() - 1))
                {
                    if (revenueTelcoReport.getServiceRevenueReports().size()>1) {
                        CellRangeAddress noMerge = new CellRangeAddress(rowIdx - revenueTelcoReport.getServiceRevenueReports().size() + 1, rowIdx,
                                                                        1, 1);
                        sheet.addMergedRegion(noMerge);
                        RegionUtil.setBorderTop(BorderStyle.THIN, noMerge, sheet);
                        RegionUtil.setBorderRight(BorderStyle.THIN, noMerge, sheet);
                        RegionUtil.setBorderBottom(BorderStyle.THIN, noMerge, sheet);
                        RegionUtil.setBorderLeft(BorderStyle.THIN, noMerge, sheet);

                        CellRangeAddress compRangeAddrMerge = new CellRangeAddress(rowIdx - revenueTelcoReport.getServiceRevenueReports().size() + 1, rowIdx,
                                                                            0, 0);
                        sheet.addMergedRegion(compRangeAddrMerge);
                        RegionUtil.setBorderTop(BorderStyle.THIN, compRangeAddrMerge, sheet);
                        RegionUtil.setBorderRight(BorderStyle.THIN, compRangeAddrMerge, sheet);
                        RegionUtil.setBorderBottom(BorderStyle.THIN, compRangeAddrMerge, sheet);
                        RegionUtil.setBorderLeft(BorderStyle.THIN, compRangeAddrMerge, sheet);
                    }
                }
                // Next
                rowIdx++;
            }
        }
        sheet.createFreezePane(0,2);

        return workbook;
    }

    @Override
    public XSSFWorkbook generateReport(List<Long> companyIds,
                                       Long year, Long quarter, String type)
    {
        List<RevenueTelcoReport> revenueTelcoReports = search(companyIds, year, quarter, type);

        if (quarter==null){
            // Year report
            return genExcel(revenueTelcoReports, "SỐ LIỆU BÁO CÁO DOANH THU DỊCH VỤ VIỄN THÔNG NĂM " + year);
        }else{
            // Quarter report
            return genExcel(revenueTelcoReports, "SỐ LIỆU BÁO CÁO DOANH THU DỊCH VỤ VIỄN THÔNG QUÝ " + quarter + "/" + year);
        }
    }

    @Override
    public List<RevenueTelcoReport> search(List<Long> companyIds,
                                           Long year, Long quarter, String type)
    {
        List<RevenueTelcoReport> revenueTelcoReports = new ArrayList<RevenueTelcoReport>();
        List<Company> tmpCompanies = this.companyService.findByListIds(companyIds);
        LicBusinessType lbt = licBusinessTypeRepository.findAllByName("Giấy phép cung cấp dịch vụ viễn thông");
        List<Company> companies = licCpnRepository.searchs(tmpCompanies, lbt, year.intValue());

        HashMap<String, RevenueTelcoReport> hCompany = new HashMap<String, RevenueTelcoReport>();
        String fileName = null;
        Long fileId = null;

        List<RevenueTelco> revenueTelcos = revenueTelcoRepository.search(companies, year, quarter, type);
        if(revenueTelcos!=null) System.out.println("======================= size: " + revenueTelcos.size());
        for (RevenueTelco revenueTelco : revenueTelcos)
        {
            RevenueTelcoReport revenueTelcoReport = null;
            if (hCompany.get(revenueTelco.getCompany().getName())!=null){
                revenueTelcoReport = hCompany.get(revenueTelco.getCompany().getName());
            }else{
                revenueTelcoReport = new RevenueTelcoReport();
                revenueTelcoReport.setCompany(revenueTelco.getCompany());
                if (revenueTelcoReport.getFile()!=null) {
                    revenueTelcoReport.getFile().setId(revenueTelco.getFile().getId());
                    revenueTelcoReport.getFile().setName(revenueTelco.getFile().getName());
                }
            }

            RevenueTelcoServiceReport revenueTelcoServiceReport = new RevenueTelcoServiceReport();
            revenueTelcoServiceReport.setYear(year);
            revenueTelcoServiceReport.setQuarter(quarter);
            revenueTelcoServiceReport.setRevenue(revenueTelco.getRevenue());
            //revenueTelcoServiceReport.setLicNumber(revenueTelco.getLicNumber());
            revenueTelcoServiceReport.setLicNumber(revenueTelco.getNote());
            LicCpn licCpn = revenueTelco.getLicCpn();
            if (licCpn!=null){
                Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
                networkTypes.forEach((n) -> {
                    if ((n.getAlias()!=null)&&(!n.getAlias().isEmpty())) revenueTelcoServiceReport.setServiceType(n.getAlias());
                });
            }

            revenueTelcoReport.getServiceRevenueReports().add(revenueTelcoServiceReport);

            hCompany.put(revenueTelco.getCompany().getName(), revenueTelcoReport);
        }

        hCompany.forEach((k,v) -> {
            revenueTelcoReports.add(v);
        });

        return revenueTelcoReports;
    }

    @Override
    public void del(List<Company> companyIds, Long year, Long quarter, String type)
    {
        revenueTelcoRepository.del(companyIds, year, quarter, type);
    }

    private void delFileFrom(Long year, Long quarter, String type)
    {
        List<File> files = revenueTelcoRepository.searchFile(year, quarter, type);
        for(File file : files){
            delFile(file.getId());
        }
    }

    @Override
    public void upload(Long year, Long quarter, File file, String type) throws Exception
    {
        ///// Save file to
        File savedFile = this.fileService.save(file);
        if(savedFile==null){
            ///// Return error
            throw new Exception("Không thể lưu file");
        }

        InputStream is = new ByteArrayInputStream(file.getContent());
        Workbook workbook = new XSSFWorkbook(is);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();

        int startDataRow = 2;
        int rowNumb = 0;
        Company currentCompany = null;
        List<RevenueTelco> revenueTelcos = new ArrayList<RevenueTelco>();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();

            rowNumb++;
            if(rowNumb <= startDataRow) continue;
            Cell companyNameCell = currentRow.getCell(1);
            Cell licCell = currentRow.getCell(2);
            Cell serviceCell = currentRow.getCell(3);
            Cell revenueCell = currentRow.getCell(4);

            Company company = null;
            LicCpn licCpn = null;

            if ((companyNameCell.getCellType()==CellType.STRING)&&(!companyNameCell.getStringCellValue().isEmpty())){
                company = this.companyService.findByName(companyNameCell.getStringCellValue().trim());
                if (company == null) {
                    System.out.println("================== Company not found: " + companyNameCell.getStringCellValue());
                    throw new Exception(companyNameCell.getStringCellValue() + " tại " + companyNameCell.getAddress().toString() + " không đúng!");
                }
                if((currentCompany==null)||(!currentCompany.getName().equalsIgnoreCase(company.getName()))) currentCompany = company;
            }
            if((serviceCell.getCellType()==CellType.STRING)&&(!serviceCell.getStringCellValue().isEmpty())){
//                serviceType = serviceTypeRepository.findByName(serviceCell.getStringCellValue().trim());
//                if (serviceType == null){
//                    System.out.println("================== Service not found: " + serviceCell.getStringCellValue());
//                    //throw new Exception(serviceCell.getStringCellValue() + " tại " + serviceCell.getAddress().toString() + " không đúng!");
//                    throw new Exception(" Tên doanh nghiệp không đúng!");
//                }
            }
            LicCpn maxLicCpn = null;
            String maxLicNumber = "";
            if((licCell.getCellType()==CellType.STRING)&&(!licCell.getStringCellValue().isEmpty())){

                String parts[] = licCell.getStringCellValue().split(";");
                Instant maxExpiredTime = null;
                for (String part : parts)
                {
                    List<LicCpn> licCpns = null;
                    if ((type!=null)&&(type.equalsIgnoreCase("year"))){
                        licCpns = licCpnRepository.findByCompanyAndLicNumberYears(currentCompany, part, year.intValue());
                    }else{
                        licCpns = licCpnRepository.findByCompanyAndLicNumberQuarters(currentCompany, part,
                                        year.intValue(), quarter.intValue());
                    }
                    if ((licCpns!=null)&&(licCpns.size()>=1)) licCpn = licCpns.get(0);
                    if (licCpn!=null){
                        if (maxExpiredTime==null) {
                            maxExpiredTime = licCpn.getExpiredTime();
                            maxLicCpn = licCpn;
                            maxLicNumber = part;
                        } else {
                            if (maxExpiredTime.compareTo(licCpn.getExpiredTime()) > 0){
                                maxExpiredTime = licCpn.getExpiredTime();
                                maxLicCpn = licCpn;
                                maxLicNumber = part;
                            }
                        }
                    }
                }
                if (maxLicCpn == null){
                    //System.out.println("================== LicNumber not found: " + licCell.getStringCellValue() + "\t" + currentCompany.getId());
                    throw new Exception(" Số giấy phép không tồn tại hoặc đã hết hạn!");
                }

                System.out.println("licCpn: " + maxLicCpn.getLicNumber());
            }
            Long revenueValue = null;
            if((revenueCell==null)||(revenueCell.getCellType() == CellType.BLANK)||(revenueCell.getCellType()!=CellType.NUMERIC)){
//                System.out.println("================== Revenue not valid: " + revenueCell.getStringCellValue());
//                throw new Exception(" BCDT chỉ được phép nhập số và không được để trống!");
            }else{
                revenueValue = Double.valueOf(revenueCell.getNumericCellValue()).longValue();
            }

            RevenueTelco revenueTelco = new RevenueTelco();
            revenueTelco.setCompany(currentCompany);
            revenueTelco.setLicCpn(maxLicCpn);
            revenueTelco.setLicNumber(maxLicNumber);
            revenueTelco.setQuarter(quarter);
            revenueTelco.setYear(year);
            revenueTelco.setType(type);
            //revenueTelco.setRevenue(Double.valueOf(revenueCell.getNumericCellValue()).longValue());
            revenueTelco.setRevenue(revenueValue);
            revenueTelco.setFile(file);
            if ((type!=null)&&(type.equalsIgnoreCase("year"))){
                revenueTelco.setPayDeadline(DateUtils.nextDay(year, 4L, 105L));
            }else{
                revenueTelco.setPayDeadline(DateUtils.nextDay(year, quarter, 45L));
            }
            revenueTelco.setNote(licCell.getStringCellValue());

            revenueTelcos.add(revenueTelco);
        }

        delFileFrom(year, quarter, type);
        revenueTelcoRepository.saveAll(revenueTelcos);
    }

    @Override
    public void delFile(Long fileId)
    {
        File file = this.fileService.getOne(fileId);
        revenueTelcoRepository.delFile(file);
        this.fileService.deleteById(fileId);
    }

    private String extractName(String compName)
    {
        String comp = compName.toLowerCase();
        if (comp.startsWith("tổng công ty")) return "Tổng công ty";
        if (comp.startsWith("công ty")) return "Công ty";
        if (comp.startsWith("tập đoàn")) return "Tập đoàn";
        return "Doanh nghiệp";
    }

    private int khoSoWarnDoc(int idx, String savedFolder,
                             Company company, WarnFeeDocDTO wfdDTO,
                             CurrentCodeFeeResponse ccfResponse) throws IOException
    {
        Long year = Long.valueOf(DateUtils.currentYear());
        Long quarter = 1L;
        String type = "quarter";
        Long feeType = 100L;
        int times = 1;
        String fileName = "";
        //String docDate = DateUtils.timeNow("yyyy-MM-dd");
        String docDate = null;
        Map<String, String> maps = new HashMap<String, String>();


        if (wfdDTO.getYear()!=null) year = wfdDTO.getYear();
        if (wfdDTO.getQuarter()!=null) quarter = wfdDTO.getQuarter();
        if (wfdDTO.getType()!=null) type = wfdDTO.getType();
        if (wfdDTO.getBusiness_type_id()!=null) feeType = wfdDTO.getBusiness_type_id();
        if (wfdDTO.getTimes()!=null) times = wfdDTO.getTimes().intValue();

        Calendar calendar = Calendar.getInstance();
        String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
        String templatePath = null;
        if(times == 2){
            fileName = idx + ". Vv đôn đốc nộp phí sử dung kho số viễn thông Quý " + quarter + " năm " + year
                + " (lần " + wfdDTO.getNumberOfTime() + ") của " + company.getName();

            if ((wfdDTO.getAddition()!=null)&&(wfdDTO.getAddition().getDoc_date()!=null)){
                docDate = wfdDTO.getAddition().getDoc_date();
            }
            //////// Check if công văn thông báo is uploaded
            List<WarnDoc> warnDocs = null;
            String notify_doc = "";
            String warn_doc = "";
            String notify_doc_date = "";
            String notify_date_fee = "";
            Long numberOfTime = wfdDTO.getNumberOfTime();
            if ((numberOfTime!=null)&&(numberOfTime>1)){
                ////// Đôn đốc lần n
                templatePath = templateDir + "/" + phiKhoSo22;

                warnDocs = warnDocRepository.searchKhoSoDoc(Arrays.asList(company),
                    null, null, null, wfdDTO.getYear(), wfdDTO.getQuarter(), wfdDTO.getType(), "out", wfdDTO.getTimes(), wfdDTO.getNumberOfTime());
                if ((warnDocs==null)||(warnDocs.size()<(wfdDTO.getNumberOfTime()-1))) {
                    return -1;
//                    missComps.add(company.getName());
//                    throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                        "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                }

                // Công văn thông báo
                List<WarnDoc> warnInfoDocs = warnDocRepository.searchKhoSoDoc(Arrays.asList(company),
                    null, null, null, wfdDTO.getYear(), wfdDTO.getQuarter(), wfdDTO.getType(), "out",
                    1L, null);
                if ((warnInfoDocs==null)||(warnInfoDocs.size()==0)) {
                    return -1;
//                    missComps.add(company.getName());
//                    throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                        "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                }
                WarnDoc warnInfoDoc = warnInfoDocs.get(0);
                notify_doc = warnInfoDoc.getDoc_number();
                notify_doc_date = warnInfoDoc.getDoc_date();
                notify_date_fee = warnInfoDoc.getPayDeadline();

                boolean first = true;
                for (WarnDoc warnDoc : warnDocs)
                {
                    if (first){
                        first = false;
                        warn_doc += warnDoc.getDoc_number();
                    } else if (warnDocs.indexOf(warnDoc) == warnDocs.size()-1){
                        warn_doc += " và " + warnDoc.getDoc_number();
                    } else {
                        warn_doc += ", " + warnDoc.getDoc_number();
                    }
                }
                maps.put("warn_doc", warn_doc);
            }else{
                ////// Đôn đốc lần 1
                templatePath = templateDir + "/" + phiKhoSo21;

                warnDocs = warnDocRepository.searchKhoSoDoc(Arrays.asList(company),
                    null, null, null, wfdDTO.getYear(), wfdDTO.getQuarter(), wfdDTO.getType(), "out",
                    1L, null);
                if ((warnDocs==null)||(warnDocs.size()==0)) {
                    return -1;
//                    missComps.add(company.getName());
//                    throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                        "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                }

                WarnDoc warnDoc = warnDocs.get(0);
                notify_doc = warnDoc.getDoc_number();
                notify_doc_date = warnDoc.getDoc_date();
                notify_date_fee = warnDoc.getPayDeadline();
                if (notify_date_fee==null) notify_date_fee = DateUtil.getLastDayOfFirstMonthNextQuarter(""+quarter, Long.valueOf(year).intValue());
            }

//            if (missComps.size()>0){
//                int first = 0;
//                String missCompNames = "\"companies\":[";
//                for (String name : missComps)
//                {
//                    if (first==0) {
//                        missCompNames += "\"" + name + "\"";
//                    }else{
//                        missCompNames += ",\"" + name + "\"";
//                    }
//                    first++;
//                }
//                missCompNames += "]";
//                throw new IOException("{" + missCompNames + ",\"message\":\"Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                        "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập\"}");
//            }

            maps.put("times", ""+numberOfTime);
            maps.put("notify_doc", notify_doc);
            maps.put("notify_doc_date", DateUtils.convertFormat(notify_doc_date,
                "yyyy-MM-dd", "dd/MM/yyyy"));
            maps.put("notify_date_fee", DateUtils.convertFormat(notify_date_fee,
                "yyyy-MM-dd", "dd/MM/yyyy"));
        }

        if (docDate!=null){
            maps.put("doc_date", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "dd"));
            maps.put("doc_month", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "MM"));
            maps.put("doc_year", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "yyyy"));
        }else{
            maps.put("doc_date", "  ");
            maps.put("doc_month", "  ");
            maps.put("doc_year", currentYear);
        }
        maps.put("quarter", ""+quarter);
        maps.put("year", "" + year);
        maps.put("company_name", company.getName());
        if ((wfdDTO.getAddition()!=null)&&(wfdDTO.getAddition().getDate_fee()!=null)) {
            String payDeadline = DateUtils.convertFormat(wfdDTO.getAddition().getDate_fee(),
                "yyyy-MM-dd", "dd/MM/yyyy");
            maps.put("date_fee", payDeadline);
        }
        maps.put("company_address", company.getAddress());
        maps.put("pre_name", extractName(company.getName()));
        //////// Get lic_info
        String dataFee = "";
        double totalFee = 0;

        // Phí kho số
        TlcCpnCodeCriteria criteria = new TlcCpnCodeCriteria();
        LongFilter companyIdFilter = new LongFilter();
        companyIdFilter.setEquals(company.getId());
        criteria.setCompanyId(companyIdFilter);
        List<CurrentCodeFeeResponse> ccfResps  = tlcCpnCodeQueryService.getCurrentCodeFeeReport(criteria, wfdDTO.getYear().intValue(),
            wfdDTO.getQuarter().intValue(), 0, null, null);

        for (CurrentCodeFeeResponse ccfRsp : ccfResps)
        {
            if ((ccfRsp.getCompanyId()!=null)&&(ccfRsp.getCompanyId()==company.getId())){
                totalFee = ccfRsp.getCurQuarterFee();
                String dataFeeWord = CurrencyUtils.numberToString(totalFee);
                maps.put("data_fee_number", "" + String.format(Locale.GERMAN, "%,d", Double.valueOf(totalFee).longValue()));
                maps.put("data_fee_word", dataFeeWord);
            }
        }

        maps.put("data_fee", dataFee);

        WordUtils wordUtils = new WordUtils();
        wordUtils.fromTemplate(templatePath, savedFolder + "/" + fileName + ".docx", maps);

        return 1;
    }

    private int ccdvvtWarnDoc(int idx, String savedFolder, Company company,
                              WarnFeeDocDTO wfdDTO) throws IOException
    {
        Long year = 2021L;
        Long quarter = 1L;
        String type = "quarter";
        Long feeType = 3L;
        Long times = 1L;
        String fileName = "";
        Long numberOfTime = 1L;
        //String docDate = DateUtils.timeNow("yyyy-MM-dd");
        String docDate = null;
        if (wfdDTO.getYear()!=null) year = wfdDTO.getYear();
        if (wfdDTO.getQuarter()!=null) quarter = wfdDTO.getQuarter();
        if (wfdDTO.getType()!=null) type = wfdDTO.getType();
        if (wfdDTO.getBusiness_type_id()!=null) feeType = wfdDTO.getBusiness_type_id();
        if (wfdDTO.getTimes()!=null) times = wfdDTO.getTimes();
        if ((wfdDTO.getNumberOfTime()!=null)&&(wfdDTO.getNumberOfTime()>1)) numberOfTime = wfdDTO.getNumberOfTime();

        Calendar calendar = Calendar.getInstance();
        String currentYear = String.valueOf(calendar.get(Calendar.YEAR));

        String templatePath = null;
        String timesType = "" + times + "." + type.toLowerCase();
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("quarter", "" + quarter);
        maps.put("year", "" + year);
        //maps.put("current_year", currentYear);
        maps.put("company_name", company.getName());
        if (wfdDTO.getAddition()!=null) {
            if (wfdDTO.getAddition().getDate_fee()!=null) {
                String payDeadline = DateUtils.convertFormat(wfdDTO.getAddition().getDate_fee(),
                    "yyyy-MM-dd", "dd/MM/yyyy");
                maps.put("date_fee", payDeadline);
            }

            if (wfdDTO.getAddition().getDoc_date()!=null) {
                docDate = wfdDTO.getAddition().getDoc_date();
            }
        }else{
            String payDeadline = DateUtils.lastDateOfFirstMonthOfNextQuarter(year, quarter);
            maps.put("date_fee", DateUtils.convertFormat(payDeadline, "yyyy-MM-dd", "dd/MM/yyyy"));
        }
        maps.put("company_address", company.getAddress());
        maps.put("pre_name", extractName(company.getName()));
        if (docDate!=null){
            maps.put("doc_date", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "dd"));
            maps.put("doc_month", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "MM"));
            maps.put("doc_year", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "yyyy"));
        }else{
            maps.put("doc_date","  ");
            maps.put("doc_month", "  ");
            maps.put("doc_year", currentYear);
        }

        String payDeadline = null;
        List<WarnDoc> warnDocs = null;
        String notify_doc = "";
        String warn_doc = "";
        String notify_doc_date = "";
        String notify_date_fee = "";
        switch (timesType)
        {
            case "1.year":
                templatePath = templateDir + "/" + ccdvvtYearWarn1;
                fileName = idx + "_Thông báo_CCDV_Năm " + year + "_" + company.getName();
                break;
            case "2.year":
                fileName = idx + "_Đôn đốc_CCDV_Năm " + year + "_" + company.getName();

                //////// Check if công văn thông báo is uploaded
                if (wfdDTO.getNumberOfTime()!=null) numberOfTime = wfdDTO.getNumberOfTime();
                if (numberOfTime>1){
                    templatePath = templateDir + "/" + ccdvvtYearWarn22;

                    ////// Đôn đốc lần n
                    warnDocs = warnDocRepository.search(company.getId(),
                        wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                        wfdDTO.getTimes(), wfdDTO.getNumberOfTime());
                    if ((warnDocs==null)||(warnDocs.size()<(wfdDTO.getNumberOfTime()-1))) {
                        return -1;
//                        throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                            "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                    }

                    WarnDoc warnInfoDoc = warnDocRepository.search(company.getId(),
                        wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                        1L);
                    if (warnInfoDoc==null) {
                        return -1;
//                        throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                            "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                    }
                    notify_doc = warnInfoDoc.getDoc_number();
                    notify_doc_date = warnInfoDoc.getDoc_date();
                    notify_date_fee = warnInfoDoc.getPayDeadline();

                    boolean first = true;
                    for (WarnDoc warnDoc : warnDocs)
                    {
                        if (first){
                            first = false;
                            warn_doc += warnDoc.getDoc_number();
                        } else if (warnDocs.indexOf(warnDoc) == warnDocs.size()-1){
                            warn_doc += " và " + warnDoc.getDoc_number();
                        } else {
                            warn_doc += ", " + warnDoc.getDoc_number();
                        }
                    }
                }else{
                    ////// Đôn đốc lần 1
                    templatePath = templateDir + "/" + ccdvvtYearWarn21;

                    WarnDoc warnDoc = warnDocRepository.search(company.getId(),
                        wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                        1L);
                    if (warnDoc==null) {
                        return -1;
//                        throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                            "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                    }

                    notify_doc = warnDoc.getDoc_number();
                    notify_doc_date = warnDoc.getDoc_date();
                    notify_date_fee = warnDoc.getPayDeadline();
                }

                maps.put("times", ""+numberOfTime);
                maps.put("notify_doc", notify_doc);
                maps.put("warn_doc", warn_doc);
                maps.put("notify_doc_date", DateUtils.convertFormat(notify_doc_date,
                    "yyyy-MM-dd", "dd/MM/yyyy"));
                maps.put("notify_date_fee", DateUtils.convertFormat(notify_date_fee,
                    "yyyy-MM-dd", "dd/MM/yyyy"));

                break;
            case "1.quarter":
                templatePath = templateDir + "/" + ccdvvtQuarterWarn1;
                fileName = idx + "_Thông báo_CCDV_Quý " + quarter + " " + year + "_" + company.getName();
                break;
            case "2.quarter":
                fileName = idx + "_Đôn đốc_CCDV_Quý " + quarter + " " + year + "_" + company.getName();
                //////// Check if công văn thông báo is uploaded
                if (wfdDTO.getNumberOfTime()!=null) numberOfTime = wfdDTO.getNumberOfTime();
                if (numberOfTime>1){
                    ////// Đôn đốc lần n
                    templatePath = templateDir + "/" + ccdvvtQuarterWarn22;

                    warnDocs = warnDocRepository.search(company.getId(),
                        wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                        wfdDTO.getTimes(), wfdDTO.getNumberOfTime());
                    if ((warnDocs==null)||(warnDocs.size()<(wfdDTO.getNumberOfTime()-1))) {
                        return -1;
//                        throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                            "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                    }

                    WarnDoc warnInfoDoc = warnDocRepository.search(company.getId(),
                        wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                        1L);
                    if (warnInfoDoc==null) {
                        return -1;
//                        throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                            "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                    }
                    notify_doc = warnInfoDoc.getDoc_number();
                    notify_doc_date = warnInfoDoc.getDoc_date();
                    notify_date_fee = warnInfoDoc.getPayDeadline();

                    boolean first = true;
                    for (WarnDoc warnDoc : warnDocs)
                    {
                        if (first){
                            first = false;
                            warn_doc += warnDoc.getDoc_number();
                        } else if (warnDocs.indexOf(warnDoc) == warnDocs.size()-1){
                            warn_doc += " và " + warnDoc.getDoc_number();
                        } else {
                            warn_doc += ", " + warnDoc.getDoc_number();
                        }
                    }
                }else{
                    ////// Đôn đốc lần 1
                    templatePath = templateDir + "/" + ccdvvtQuarterWarn21;

                    WarnDoc warnDoc = warnDocRepository.search(company.getId(),
                        wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                        1L);
                    if (warnDoc==null) {
                        return -1;
//                        throw new IOException("Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
//                            "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập");
                    }

                    notify_doc = warnDoc.getDoc_number();
                    notify_doc_date = warnDoc.getDoc_date();
                    notify_date_fee = warnDoc.getPayDeadline();
                }

                maps.put("times", ""+numberOfTime);
                maps.put("notify_doc", notify_doc);
                maps.put("warn_doc", warn_doc);
                maps.put("notify_doc_date", DateUtils.convertFormat(notify_doc_date,
                    "yyyy-MM-dd", "dd/MM/yyyy"));
                maps.put("notify_date_fee", DateUtils.convertFormat(notify_date_fee,
                    "yyyy-MM-dd", "dd/MM/yyyy"));

                break;
        }

        WordUtils wordUtils = new WordUtils();
        wordUtils.fromTemplate(templatePath, savedFolder + "/" + fileName + ".docx", maps);
        return 1;
    }

    private int tlmWarnDoc(int idx, String savedFolder, Company company,
                           WarnFeeDocDTO wfdDTO, Long dataFeeTotal) throws IOException
    {
        Long year = Long.valueOf(DateUtils.currentYear());
        Long quarter = 1L;
        String type = "quarter";
        Long feeType = 3L;
        int times = 1;
        String fileName = "";
        //String docDate = DateUtils.timeNow("yyyy-MM-dd");
        String docDate = null;
        Map<String, String> maps = new HashMap<String, String>();

        if (wfdDTO.getYear()!=null) year = wfdDTO.getYear();
        if (wfdDTO.getQuarter()!=null) quarter = wfdDTO.getQuarter();
        if (wfdDTO.getType()!=null) type = wfdDTO.getType();
        if (wfdDTO.getBusiness_type_id()!=null) feeType = wfdDTO.getBusiness_type_id();
        if (wfdDTO.getTimes()!=null) times = wfdDTO.getTimes().intValue();

        Calendar calendar = Calendar.getInstance();
        String currentYear = String.valueOf(calendar.get(Calendar.YEAR));
        String templatePath = null;
        if (times==1){
            templatePath = templateDir + "/" + tlmWarn1;
            fileName = idx + "_Thông báo_TLM_Năm " + year + "_" + company.getName();

            if (wfdDTO.getAddition()!=null) {
                if (wfdDTO.getAddition().getDate_fee()!=null) {
                    String payDeadline = DateUtils.convertFormat(wfdDTO.getAddition().getDate_fee(),
                        "yyyy-MM-dd", "dd/MM/yyyy");
                    maps.put("date_fee", payDeadline);
                }

                if (wfdDTO.getAddition().getDoc_date()!=null) {
                    docDate = wfdDTO.getAddition().getDoc_date();
                }
            }else{
                maps.put("date_fee", "31/03/" + year);
            }
        }else if(times == 2){
            fileName = idx + "_Đôn đốc_TLM_Năm " + year + "_" + company.getName();

            if ((wfdDTO.getAddition()!=null)&&(wfdDTO.getAddition().getDoc_date()!=null)) {
                docDate = wfdDTO.getAddition().getDoc_date();
            }
            //////// Check if công văn thông báo is uploaded
            List<WarnDoc> warnDocs = null;
            String notify_doc = "";
            String warn_doc = "";
            String notify_doc_date = "";
            String notify_date_fee = "";
            Long numberOfTime = wfdDTO.getNumberOfTime();
            if ((numberOfTime!=null)&&(numberOfTime>1)){
                ////// Đôn đốc lần n
                templatePath = templateDir + "/" + tlmWarn22;

                warnDocs = warnDocRepository.search(company.getId(),
                    wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                    wfdDTO.getTimes(), wfdDTO.getNumberOfTime());
                if ((warnDocs==null)||(warnDocs.size()<(wfdDTO.getNumberOfTime()-1))) {
                    return -1;
                }

                // Công văn thông báo
                WarnDoc warnInfoDoc = warnDocRepository.search(company.getId(),
                    wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                    1L);
                if (warnInfoDoc==null) {
                    return -1;
                }
                notify_doc = warnInfoDoc.getDoc_number();
                notify_doc_date = warnInfoDoc.getDoc_date();
                notify_date_fee = warnInfoDoc.getPayDeadline();

                boolean first = true;
                for (WarnDoc warnDoc : warnDocs)
                {
                    if (first){
                        first = false;
                        warn_doc += warnDoc.getDoc_number();
                    } else if (warnDocs.indexOf(warnDoc) == warnDocs.size()-1){
                        warn_doc += " và " + warnDoc.getDoc_number();
                    } else {
                        warn_doc += ", " + warnDoc.getDoc_number();
                    }
                }
            }else{
                ////// Đôn đốc lần 1
                templatePath = templateDir + "/" + tlmWarn21;

                WarnDoc warnDoc = warnDocRepository.search(company.getId(),
                    wfdDTO.getBusiness_type_id(), wfdDTO.getYear(), null, wfdDTO.getType(),
                    1L);
                if (warnDoc==null) {
                    return -1;
                }

                notify_doc = warnDoc.getDoc_number();
                notify_doc_date = warnDoc.getDoc_date();
                notify_date_fee = warnDoc.getPayDeadline();
            }

            maps.put("times", ""+numberOfTime);
            maps.put("notify_doc", notify_doc);
            maps.put("warn_doc", warn_doc);
            maps.put("notify_doc_date", DateUtils.convertFormat(notify_doc_date,
                "yyyy-MM-dd", "dd/MM/yyyy"));
            maps.put("notify_date_fee", DateUtils.convertFormat(notify_date_fee,
                "yyyy-MM-dd", "dd/MM/yyyy"));
        }

        if (docDate!=null) {
            maps.put("doc_date", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "dd"));
            maps.put("doc_month", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "MM"));
            maps.put("doc_year", DateUtils.convertFormat(docDate, "yyyy-MM-dd", "yyyy"));
        }else{
            maps.put("doc_date","  ");
            maps.put("doc_month", "  ");
            maps.put("doc_year", currentYear);
        }
        maps.put("year", "" + year);
        maps.put("company_name", company.getName());
        if ((wfdDTO.getAddition()!=null)&&(wfdDTO.getAddition().getDate_fee()!=null)) {
            String payDeadline = DateUtils.convertFormat(wfdDTO.getAddition().getDate_fee(),
                "yyyy-MM-dd", "dd/MM/yyyy");
            maps.put("date_fee", payDeadline);
        }else{
            maps.put("date_fee", "30/03/" + currentYear);
        }
        maps.put("company_address", company.getAddress());
        maps.put("pre_name", extractName(company.getName()));
        //////// Get lic_info
        LicBusinessType lbt = licBusinessTypeRepository.getOne(wfdDTO.getBusiness_type_id());
        List<LicCpn> licCpns = licCpnRepository.searchLicPerYear(Arrays.asList(company), lbt, wfdDTO.getYear().intValue());
        String licInfo = "";
        String dataFee = "";
        boolean first = true;
        double fee = 0;
        double totalFee = 0;

        for (LicCpn licCpn : licCpns)
        {
            String licCreatedTime = DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "yyyy-MM-dd");
            if (licCreatedTime.compareTo(year + "-01-01")>0) continue;
            String networkName = "";
            Set<NetworkType> networkTypes = licCpn.getNetworkTypes();
            for (NetworkType nwt : networkTypes){
                if (nwt.getAlias()!=null) {
                    networkName = nwt.getAlias();
                    break;
                }
            }
            if (first) {
                licInfo += "Giấy phép thiết lập mạng viễn thông " + networkName + " số " + licCpn.getLicNumber()
                    + " ngày " + DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "dd/MM/yyyy");
                first = false;
            }else{
                licInfo += ", Giấy phép thiết lập mạng viễn thông " + networkName + " số " + licCpn.getLicNumber()
                    + " ngày " + DateUtils.timeNowInstant(licCpn.getLicCreatedDate(), "dd/MM/yyyy");
            }
            /////// Estimate fee
            fee = receiptTelcoService.tlmFee0101Estimate("year", licCpn, year); // Phí 01/01
            totalFee += fee;
            if (fee > 0) {
                dataFee += "+ Phí " + networkName + ": " + String.format(Locale.GERMAN, "%,d", Double.valueOf(fee).longValue()) + " đồng;\n";
            }
        }
        if (fee<=0) return 0;

        String dataFeeWord = CurrencyUtils.numberToString(totalFee);
        maps.put("data_fee_number", "" + String.format(Locale.GERMAN, "%,d", Double.valueOf(totalFee).longValue()));
        maps.put("data_fee_word", dataFeeWord);

        maps.put("lic_info", licInfo);
        maps.put("data_fee", dataFee);

        WordUtils wordUtils = new WordUtils();
        wordUtils.fromTemplate(templatePath, savedFolder + "/" + fileName + ".docx", maps);

        return 1;
    }

    private int debtComparationDoc(int idx, String savedFolder,
                                   Company company, WarnFeeDocDTO wfdDTO) throws IOException
    {
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("year_range", wfdDTO.getYears().get(0) + "-" + wfdDTO.getYears().get(1));
        maps.put("company_name", company.getName());
        maps.put("today", DateUtils.timeNow("dd/MM/yyyy"));

        String templatePath = templateDir + "/" + doichieucongno;
        String fileName = "Công văn_Đối chiếu công nợ_" + company.getName();
        WordUtils wordUtils = new WordUtils();
        wordUtils.fromTemplate(templatePath, savedFolder + "/" + fileName + ".docx", maps);

        return 1;
    }

    private String generateDebtComparisionDoc(List<RespCompPayment> companyDebts,
                                              WarnFeeDocDTO wfdDTO) throws IOException
    {
        String savedFolder = null;
        String timeNow = DateUtils.timeNow("yyyyMMddHHmmss");
        int idx = 1;
        for (RespCompPayment companyDebt : companyDebts) {
            Company company = companyDebt.getCompany();
            try {
                savedFolder = savedDir + "cv_doichieucongno_" + wfdDTO.getYears().get(0) + "-" + wfdDTO.getYears().get(1) +
                    "." + timeNow;
                Path path = Paths.get(savedFolder);
                if (!Files.exists(path)) Files.createDirectory(path);

                idx += debtComparationDoc(idx, savedFolder, company, wfdDTO);
            }catch(IOException ioe){
                throw new IOException(ioe.getMessage());
            }
        }
        return savedFolder;
    }

    private String generateWarnDoc(List<RespCompPayment> companyDebts,
                                   WarnFeeDocDTO wfdDTO) throws IOException
    {
        Long feeType = 3L;
        if (wfdDTO.getBusiness_type_id()!=null) feeType = wfdDTO.getBusiness_type_id();

        LicBusinessType lbt = licBusinessTypeRepository.getOne(feeType);

        String savedFolder = null;
        String timeNow = DateUtils.timeNow("yyyyMMddHHmmss");
        int idx = 1;
        Set<String> missComps = new HashSet<String>();
        for (RespCompPayment companyDebt : companyDebts)
        {
            Company company = companyDebt.getCompany();
            try {
                if ((lbt==null)||(lbt.getName().equalsIgnoreCase("Giấy phép cung cấp dịch vụ viễn thông"))){
                    /////// Create folder
                    if ((wfdDTO.getType()!=null)&&(wfdDTO.getType().equalsIgnoreCase("year")))
                    {
                        if ((wfdDTO.getTimes()!=null)&&(wfdDTO.getTimes()==2L))
                        {
                            savedFolder = savedDir + "Đôn đốc_CCDV_Năm " + wfdDTO.getYear() +
                                "." + timeNow;
                        }else{
                            savedFolder = savedDir + "Thông báo_CCDV_Năm " + wfdDTO.getYear() +
                                "." + timeNow;
                        }
                    }else{
                        if ((wfdDTO.getTimes()!=null)&&(wfdDTO.getTimes()==2L))
                        {
                            savedFolder = savedDir + "Đôn đốc_CCDV_Quý " + wfdDTO.getQuarter() +
                                " " + wfdDTO.getYear() + "." + timeNow;
                        }else{
                            savedFolder = savedDir + "Thông báo_CCDV_Quý " + wfdDTO.getQuarter() +
                                " " + wfdDTO.getYear() + "." + timeNow;
                        }
                    }
                    Path path = Paths.get(savedFolder);
                    if (!Files.exists(path)) Files.createDirectory(path);

                    int resultCode = ccdvvtWarnDoc(idx, savedFolder, company, wfdDTO);
                    if (resultCode==-1){
                        missComps.add(company.getName());
                    }else{
                        idx += resultCode;
                    }
                }else if (lbt.getName().equalsIgnoreCase("Giấy phép thiết lập mạng viễn thông công cộng")){
                    /////// Create folder
                    if ((wfdDTO.getTimes()!=null)&&(wfdDTO.getTimes()==2L))
                    {
                        savedFolder = savedDir + "Đôn đốc_TLM_Năm " + wfdDTO.getYear() +
                            "." + timeNow;
                    }else{
                        savedFolder = savedDir + "Thông báo_TLM_Năm " + wfdDTO.getYear() +
                            "." + timeNow;
                    }
                    Path path = Paths.get(savedFolder);
                    if (!Files.exists(path)) Files.createDirectory(path);

                    Long dataFee = 0L;
                    int resultCode = tlmWarnDoc(idx, savedFolder, company, wfdDTO, dataFee);
                    if (resultCode==-1){
                        missComps.add(company.getName());
                    }else{
                        idx += resultCode;
                    }
                }else{
                    return null;
                }
            }catch(IOException ioe){
                throw new IOException(ioe.getMessage());
            }
        }
        if (missComps.size()>0){
            int first = 0;
            String missCompNames = "\"companies\":[";
            for (String name : missComps)
            {
                if (first==0) {
                    missCompNames += "\"" + name + "\"";
                }else{
                    missCompNames += ",\"" + name + "\"";
                }
                first++;
            }
            missCompNames += "]";
            throw new IOException("{" + missCompNames + ",\"message\":\"Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
                    "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập\"}");
        }
        return savedFolder;
    }

    @Override
    public String generateKhoSoWarnFeeDocs(WarnFeeDocDTO wfdDTO) throws IOException
    {
        ////// Check debt of such companies
        // Phí kho số
        TlcCpnCodeCriteria criteria = new TlcCpnCodeCriteria();
        if ((wfdDTO.getCompany_ids()!=null)&&(wfdDTO.getCompany_ids().size()>0)) {
            LongFilter companyIdFilter = new LongFilter();
            companyIdFilter.setIn(wfdDTO.getCompany_ids());
            criteria.setCompanyId(companyIdFilter);
        }
        List<CurrentCodeFeeResponse> ccfResps  = tlcCpnCodeQueryService.getCurrentCodeFeeReport(criteria, wfdDTO.getYear().intValue(),
                 wfdDTO.getQuarter().intValue(), 1, 0, null);

        try {
            String folderDoc = null;
            int idx = 1;
            String timeNow = DateUtils.timeNow("yyyyMMddHHmmss");
            ArrayList<String> missComps = new ArrayList<String>();

            for (CurrentCodeFeeResponse ccfResponse : ccfResps)
            {
                if (ccfResponse.getCompanyId()!=null){
                    Company company = this.companyService.findById(ccfResponse.getCompanyId());
                    /////// Create folder
                    folderDoc = savedDir + "Đôn đốc_PKS_Quý " + wfdDTO.getQuarter() + " Năm " + wfdDTO.getYear() +
                        "." + timeNow;
                    Path path = Paths.get(folderDoc);
                    if (!Files.exists(path)) Files.createDirectory(path);

                    int resultCode = khoSoWarnDoc(idx, folderDoc, company, wfdDTO, ccfResponse);
                    if (resultCode==-1){
                        missComps.add(company.getName());
                    }else{
                        idx += resultCode;
                    }
                }
            }

            if (missComps.size()>0){
                int first = 0;
                String missCompNames = "\"companies\":[";
                for (String name : missComps)
                {
                    if (first==0) {
                        missCompNames += "\"" + name + "\"";
                    }else{
                        missCompNames += ",\"" + name + "\"";
                    }
                    first++;
                }
                missCompNames += "]";
                throw new IOException("{" + missCompNames + ",\"message\":\"Để xuất file công văn đôn đốc vui lòng nhập thông tin công văn " +
                        "thông báo và các công văn đôn đốc trước đó (nếu có) của các DN chưa nhập\"}");
            }

            return folderDoc;
        }catch(IOException ioe){
            throw new IOException(ioe.getMessage());
        }
    }

    @Override
    public String generateWarnFeeDocs(WarnFeeDocDTO wfdDTO) throws IOException
    {
        ////// Check debt of such companies
        DebtDTO debtDTO = new DebtDTO();
        if (wfdDTO.getBusiness_type_id()!=null) debtDTO.setBusiness_type_ids(Arrays.asList(wfdDTO.getBusiness_type_id()));
        if (wfdDTO.getType()!=null) debtDTO.setType(wfdDTO.getType());
        if (wfdDTO.getTimes()==3) {
            debtDTO.setYears(wfdDTO.getYears());
        }else{
            debtDTO.setYears(Arrays.asList(wfdDTO.getYear()));
        }
        if (wfdDTO.getQuarter()!=null) debtDTO.setQuarters(Arrays.asList(wfdDTO.getQuarter()));
        if (wfdDTO.getCompany_ids()!=null) debtDTO.setCompany_ids(wfdDTO.getCompany_ids());
        if ((wfdDTO.getTimes()!=null)&&(wfdDTO.getTimes()==2)) debtDTO.setFull_paid(0L);

        List<RespCompPayment> companyDebts = receiptTelcoService.compDebtSearchAll(debtDTO);
        if ((companyDebts==null)||(companyDebts.size()<=0)) {
            System.out.println("===================== NO DEBT COMPANY");
            return null;
        }

        try {
            String folderDoc = null;
            if (wfdDTO.getTimes()==3){
                folderDoc = generateDebtComparisionDoc(companyDebts, wfdDTO);
            }else{
                folderDoc = generateWarnDoc(companyDebts, wfdDTO);
            }

            if (folderDoc!=null){
                if ((wfdDTO.getTimes()==1)&&(wfdDTO.getAddition()!=null)&&(wfdDTO.getAddition().getDate_fee()!=null))
                {
                    /////// Update paydeadline in cpn_common_manager
                    CpnCommonManager ccm = ccmRepository.search(wfdDTO.getBusiness_type_id(),
                                                wfdDTO.getType(), wfdDTO.getYear(), wfdDTO.getQuarter());
                    if (ccm!=null){
                        ccm.setPayDeadline(wfdDTO.getAddition().getDate_fee());
                        ccm.setUpdateTime(DateUtils.timeNow());
                    }else{
                        ccm = new CpnCommonManager();
                        ccm.setBusinessTypeId(wfdDTO.getBusiness_type_id());
                        ccm.setCreateTime(DateUtils.timeNow());
                        ccm.setType(wfdDTO.getType());
                        ccm.setYear(wfdDTO.getYear());
                        ccm.setQuarter(wfdDTO.getQuarter());
                        ccm.setPayDeadline(wfdDTO.getAddition().getDate_fee());
                    }
                    ccmRepository.save(ccm);

                    /////// Update paydeadline into warndoc
                    warnDocRepository.update(wfdDTO.getAddition().getDate_fee(), wfdDTO.getBusiness_type_id(),
                                        wfdDTO.getYear(), wfdDTO.getQuarter(), wfdDTO.getType());
                }
            }
            return folderDoc;
        }catch(IOException ioe){
            throw new IOException(ioe.getMessage());
        }
    }
}
