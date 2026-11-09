package com.mycompany.wishwash;

public class LayananSatuan extends Layanan {
    public LayananSatuan(String nama, double harga, int durasiJam) {
        super(nama, harga, durasiJam);
    }
    @Override
    public double hitungBiaya(double jumlahItem) {
        return getHarga() * Math.ceil(jumlahItem); 
    }
    @Override
    public String getSatuan() {
        return "item";
    }
}
