package com.mycompany.wishwash;

public class Pelanggan extends Pengguna {
    public Pelanggan(String nama, String noHp, String kataSandi) {
        super(nama, noHp, kataSandi);
    }
    @Override
    public String getPeran() {
        return "Pelanggan";
    }
}
