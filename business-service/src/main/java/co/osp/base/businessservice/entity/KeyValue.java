package co.osp.base.businessservice.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Value.
 */
@Data
@Entity
@Table(name = "key_value")
public class KeyValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "key", length = 128, nullable = false, unique = true)
    private String key;

    @NotNull
    @Size(max = 255)
    @Column(name = "value", length = 255, nullable = false)
    private String value;

    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(name = "fee_free")
    private Boolean feeFree;

    public KeyValue key(String key) {
        this.key = key;
        return this;
    }

    public KeyValue value(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyValue)) {
            return false;
        }
        return id != null && id.equals(((KeyValue) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KeyValue{" +
            "id=" + id +
            ", key='" + key + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
