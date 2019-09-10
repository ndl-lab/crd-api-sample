package jp.go.ndl.lab.crd.domain;

import lombok.Data;

@Data
/**
 * 参加館データ
 */
public class Library {

    public Library() {
    }

    public Library(String id, String type, int registerYear, String name, String nameYomi, String address, String phone, String isils) {
        this.id = id;
        this.type = type;
        this.registerYear = registerYear;
        this.name = name;
        this.nameYomi = nameYomi;
        this.address = address;
        this.phone = phone;
        this.isils = isils;
    }

    public String id;
    public String type;
    public int registerYear;
    public String name;
    public String nameYomi;

    public String address;
    public String phone;

    public String isils;

}
