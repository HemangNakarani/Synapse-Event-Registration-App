package com.hemangnh18.synapsepr.Models;

public class Participant {

    String name,number,alt_number,email,institute,city,team,total_members,registred_by,event,code;

    public  Participant()
    {

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAlt_number() {
        return alt_number;
    }

    public void setAlt_number(String alt_number) {
        this.alt_number = alt_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTotal_members() {
        return total_members;
    }

    public void setTotal_members(String total_members) {
        this.total_members = total_members;
    }

    public String getRegistred_by() {
        return registred_by;
    }

    public void setRegistred_by(String registred_by) {
        this.registred_by = registred_by;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Participant(String name, String number, String alt_number, String email, String institute, String city, String team, String total_members, String registred_by, String event, String code) {
        this.name = name;
        this.number = number;
        this.alt_number = alt_number;
        this.email = email;
        this.institute = institute;
        this.city = city;
        this.team = team;
        this.total_members = total_members;
        this.registred_by = registred_by;
        this.event = event;
        this.code = code;
    }
}
