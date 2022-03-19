package com.bonustrack02.tp08goodprice;

public class Item {
    public Item(String imgShop, String name, String address, String phone) {
        this.imgShop = imgShop;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public Item() {
    }

    String imgShop, name, address, phone;
}
