package co.osp.base.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "receipt")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Receipt {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "receipt_name")
    private String receiptName;

    @Column(name = "amount_of_money")
    private Long amountOfMoney;

    @Column(name = "receipt_date")
    private String receiptDate;

    @Column(name = "receive_date")
    private String receive_date;

    @Column(name = "create_date")
    private String createDate;

    @Column(name = "update_date")
    private String updateDate;

    @Column(name = "receipt_content")
    private String receiptContent;

    @Column(name = "payer")
    private String payer;

    @Column(name = "receiver")
    private String receiver;

    @Column(name = "create_user")
    private String createUser;

    @ManyToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "payment_type_id", referencedColumnName = "id")
    private PaymentType paymentType;

    @OneToOne
    @JsonIgnoreProperties
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;

    /**
     * File
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "receipt_file",
        joinColumns = @JoinColumn(name = "receipt_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private Set<File> files = new HashSet<>();

    @Column(name = "note")
    private String note;

    @Column(name = "is_delete")
    private Integer isDelete;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(id, receipt.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
