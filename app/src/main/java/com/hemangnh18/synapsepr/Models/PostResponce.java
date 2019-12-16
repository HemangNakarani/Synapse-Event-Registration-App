package com.hemangnh18.synapsepr.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostResponce {

    @SerializedName("responce")
    @Expose
    private Responce responce;

    public Responce getResponce() {
        return responce;
    }

    public void setResponce(Responce responce) {
        this.responce = responce;
    }


    public class Responce {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("code")
        @Expose
        private Integer code;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

    }
}