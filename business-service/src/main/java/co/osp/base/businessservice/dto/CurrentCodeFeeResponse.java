package co.osp.base.businessservice.dto;

import lombok.Data;

@Data
public class CurrentCodeFeeResponse {
    private int tt;
    private String companyName;
    private String companyAddress;
    // LongTH add
    private String taxCode;
    private Long paidMoney;
    private Long ownMoney;
    private String notifyDocNumber;
    private String notifyDocDate;
    private String note;
    private String companyEmail;
    private String companyRepresentationPerson;
    //    uyen add ten dau moi
    private String contactPersion;
    private String companyPhone;
    private String companyAddressContact;
    // end add

    private Long companyId;
    private Long preQuarterFee;
    private Long curQuarterFee;
    private Long quarterGap;
    private String feeText;

}
