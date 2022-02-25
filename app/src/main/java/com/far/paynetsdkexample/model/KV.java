package com.far.paynetsdkexample.model;

import com.agilisa.devices.global.Global;

public class KV {
    Object object;
    String description;

    public KV(Object object, String description) {
        this.object = object;
        this.description = description;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
