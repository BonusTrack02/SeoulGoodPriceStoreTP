package com.bonustrack02.tp08goodprice;

public class Item {
    public Item(String imgShop, String name, String address, String phone, String pride) {
        this.imgShop = imgShop;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.pride = pride;
    }

    public Item() {
    }

    String imgShop, name, address, phone, pride;
}
