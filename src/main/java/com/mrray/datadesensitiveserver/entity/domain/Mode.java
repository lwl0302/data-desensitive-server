package com.mrray.datadesensitiveserver.entity.domain;

import javax.persistence.*;

@Entity
@Table(name = "t_mode")
public class Mode extends SuperEntity {
    private String description;
    @ManyToOne(targetEntity = Algorithm.class, fetch = FetchType.LAZY)
    private Algorithm algorithm;
    private String methodName;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}