package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A DecisionHis.
 */
@Data
@Entity
@Table(name = "decision_his")
public class DecisionHis extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "decisionHis", allowSetters = true)
    private KeyValue decisionType;

    @Size(max = 256)
    @Column(name = "cpn_name", length = 256)
    private String cpnName;

    @Size(max = 256)
    @Column(name = "cpn_address", length = 256)
    private String cpnAddress;

    @Size(max = 40)
    @Column(name = "contact_phone", length = 40)
    private String contactPhone;

    @Size(max = 256)
    @Column(name = "contact_email", length = 256)
    private String contactEmail;

    @Size(max = 128)
    @Column(name = "proposal_number", length = 128)
    private String proposalNumber;

    @NotNull
    @Column(name = "decision_date", nullable = false)
    private Instant decisionDate;

    @Column(name = "recommend_date")
    private Instant recommendDate;

    @NotNull
    @Column(name = "effective_date", nullable = false)
    private Instant effectiveDate;

    @Size(max = 2048)
    @Column(name = "note", length = 2048)
    private String note;

    @Size(max = 20)
    @Column(name = "approved", length = 20)
    private String approved;

    @Column(name = "payment_status")
    private Boolean paymentStatus;

    @Column(name = "decision_fee")
    private Long decisionFee;

    @Column(name = "decision_number")
    private String decisionNumber;

    @Column(name = "return_date")
    private Instant returnDate;

    @Column(name = "notification_number")
    private String notificationNumber;

    @Column(name = "notification_date")
    private Instant notificationDate;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_person_position")
    private String contactPersonPosition;

    @Column(name = "approved_return_date")
    private Instant approvedReturnDate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "decisionHis", allowSetters = true)
    private Company cpn;

    @OneToMany(mappedBy = "decisionHis", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "decisionHis", allowSetters = true)
    private Set<TlcCode> tlcCodes = new HashSet<>();

    /**
     * File
     */
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "decision_his_file",
        joinColumns = @JoinColumn(name = "decision_his_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private Set<File> files = new HashSet<>();

    @Column(name = "locked")
    private Boolean locked;

    @Column(name = "updated_to_current")
    private Boolean updatedToCurrent;

    @Column(name = "display_code",columnDefinition = "text")
    private String displayCode;

    @Column(name = "total")
    private Long total;

    @Column(name = "is_history")
    private Boolean history = false;

    public DecisionHis cpnAddress(String cpnAddress) {
        this.cpnAddress = cpnAddress;
        return this;
    }

    public DecisionHis contactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        return this;
    }
    public DecisionHis contactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        return this;
    }

    public DecisionHis proposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
        return this;
    }

    public DecisionHis decisionDate(Instant decisionDate) {
        this.decisionDate = decisionDate;
        return this;
    }

    public DecisionHis recommendDate(Instant recommendDate) {
        this.recommendDate = recommendDate;
        return this;
    }

    public DecisionHis effectiveDate(Instant effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }

    public DecisionHis note(String note) {
        this.note = note;
        return this;
    }

    public DecisionHis approved(String approved) {
        this.approved = approved;
        return this;
    }

    public DecisionHis decisionFee(Long decisionFee) {
        this.decisionFee = decisionFee;
        return this;
    }

    public DecisionHis decisionNumber(String decisionNumber) {
        this.decisionNumber = decisionNumber;
        return this;
    }

    public DecisionHis returnDate(Instant returnDate) {
        this.returnDate = returnDate;
        return this;
    }

    public DecisionHis notificationNumber(String notificationNumber) {
        this.notificationNumber = notificationNumber;
        return this;
    }

    public DecisionHis notificationDate(Instant notificationDate) {
        this.notificationDate = notificationDate;
        return this;
    }

    public DecisionHis contactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
        return this;
    }

    public DecisionHis approvedReturnDate(Instant approvedReturnDate) {
        this.approvedReturnDate = approvedReturnDate;
        return this;
    }

    public DecisionHis cpn(Company company) {
        this.cpn = company;
        return this;
    }

    public DecisionHis addTlcCode(TlcCode tlcCode) {
        this.tlcCodes.add(tlcCode);
        return this;
    }

    public DecisionHis removeTlcCode(TlcCode tlcCode) {
        this.tlcCodes.remove(tlcCode);
        return this;
    }

    public DecisionHis addFile(File file) {
        this.files.add(file);
        return this;
    }

    public DecisionHis removeFile(File file) {
        this.files.remove(file);
        return this;
    }

    public String getDisplayCode() {
        StringBuilder s = new StringBuilder();
        for(TlcCode e: this.getTlcCodes())
        {
            if(e.getNdcCode() != null)
            {
                s.append(e.getCode() + " (NDC="+e.getNdcCode()+"), ");
            } else if(e.getProvince() != null)
            {
                s.append(e.getCode() + " ("+e.getProvince().getName()+"), ");
            } else{
                s.append(e.getCode()+", ");
            }
        }
        if (s.toString().length() > 0)
        {
            displayCode = s.toString().trim().substring(0, s.toString().trim().length()-1);
        }
        return displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DecisionHis)) {
            return false;
        }
        return id != null && id.equals(((DecisionHis) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
