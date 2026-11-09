package com.mycompany.wishwash;

public abstract class Layanan {
    private String nama;
    private double harga;     
    private int durasiJam;    

    public Layanan(String nama, double harga, int durasiJam) {
        this.nama = nama;
        this.harga = harga;
        this.durasiJam = durasiJam;
    }

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public double getHarga() {
        return harga;
    }
    public void setHarga(double harga) {
        if (harga > 0) {
            this.harga = harga;
        }
    }
    public int getDurasi() {
        return durasiJam;
    }
    public void setDurasi(int durasiJam) {
        if (durasiJam > 0) {
            this.durasiJam = durasiJam;
        }
    }
    public abstract double hitungBiaya(double jumlah);
    public abstract String getSatuan();
}
