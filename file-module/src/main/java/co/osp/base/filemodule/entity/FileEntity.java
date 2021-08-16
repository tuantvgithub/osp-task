package co.osp.base.filemodule.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name="file")
public class FileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Size(max=128)
    @Column(name="name", length=128)
    private String name;

    @Size(max=45)
    @Column(name="extension", length=45)
    private String extendsion;

    @Size(max=128)
    @Column(name="url", length=128, nullable=false)
    private String url;

    @Lob
    @Column(name="content", length=16777216)
    private byte[] content;

    @Column(name="content_type", nullable=false)
    private String contentType;

    @Column(name="file_path")
    private String filePath;
}
