package br.com.academiadev.thunderpets.model;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class EntidadeAuditavel<String> {

    @CreatedBy
    @Column(updatable = false)
    private String created_by;

    @LastModifiedBy
    private String updated_by;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate created_at;

    @LastModifiedDate
    private LocalDate updated_at;
}
