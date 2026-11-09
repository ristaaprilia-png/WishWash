package com.mycompany.wishwash;

public class LayananKiloan extends Layanan {
    public LayananKiloan(String nama, double harga, int durasiJam) {
        super(nama, harga, durasiJam);
    }
    @Override
    public double hitungBiaya(double berat) {
        return getHarga() * berat;
    }
    @Override
    public String getSatuan() {
        return "kg";
    }
}
