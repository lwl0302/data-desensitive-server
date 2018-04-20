package com.mrray.datadesensitiveserver.entity.domain;

import org.apache.commons.lang.RandomStringUtils;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class SuperEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String uuid = RandomStringUtils.random(8, true, true).toLowerCase();

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}