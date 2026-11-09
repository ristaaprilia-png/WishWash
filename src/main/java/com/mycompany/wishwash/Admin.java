package com.mycompany.wishwash;

public class Admin extends Pengguna {
    public Admin(String nama, String noHp, String kataSandi) {
        super(nama, noHp, kataSandi);
    }
    @Override
    public String getPeran() {
        return "Admin";
    }
}
