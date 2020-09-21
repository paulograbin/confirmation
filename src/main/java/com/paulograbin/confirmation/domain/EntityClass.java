package com.paulograbin.confirmation.domain;

public class EntityClass {

    protected Long id;

    public EntityClass(Long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
