package com.mycompany.wishwash;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pesanan {
    public static final String[] TAHAP = {
        "Diterima", "Dicuci", "Disetrika", "Siap Diambil", "Selesai"
    };
    private static int counter = 0;

    private String nomor;
    private Pelanggan pelanggan;
    private Layanan layanan;
    private double jumlah;
    private int indeksStatus = 0;
    private boolean lunas = false;
    private LocalDateTime estimasiSelesai;
    private LocalDateTime waktuDibuat;

    public Pesanan(Pelanggan pelanggan, Layanan layanan, double jumlah) {
        counter++;
        this.nomor = String.format("WW-%03d", counter);
        this.pelanggan = pelanggan;
        this.layanan = layanan;
        this.jumlah = jumlah;
        this.waktuDibuat = LocalDateTime.now();
    }

    public String getNomor() {
        return nomor;
    }
    public Pelanggan getPelanggan() {
        return pelanggan;
    }
    public Layanan getLayanan() {
        return layanan;
    }
    public double getJumlah() {
        return jumlah;
    }
    public void setJumlah(double jumlah) {         
        if (jumlah > 0) {
            this.jumlah = jumlah;
        }
    }

    public double getBiaya() {
        return layanan.hitungBiaya(jumlah);
    }
    public String getStatus() {
        return TAHAP[indeksStatus];
    }
    public boolean lanjutStatus() {
        if (indeksStatus < TAHAP.length - 1) {
            indeksStatus++;
            return true;
        }
        return false;
    }
    public boolean sedangDikerjakan() { return indeksStatus < 3; } 

    public boolean isLunas() {
        return lunas;
    }
    public void setLunas(boolean lunas) {
        this.lunas = lunas;
    }
    public LocalDateTime getEstimasiSelesai() {
        return estimasiSelesai;
    }
    public void setEstimasiSelesai(LocalDateTime e) {
        this.estimasiSelesai = e;
    }
    public LocalDateTime getWaktuDibuat() {
        return waktuDibuat;
    }
    public String getEstimasiTeks() {
        if (!sedangDikerjakan()) {
            return "-";
        }
        return estimasiSelesai.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public void tampilkan() {
        System.out.printf("%s | %s | %s %.1f %s | Rp%,.0f | %s | Estimasi: %s | %s%n",
            nomor, pelanggan.getNama(), layanan.getNama(), jumlah, layanan.getSatuan(),
            getBiaya(), getStatus(), getEstimasiTeks(), lunas ? "LUNAS" : "BELUM LUNAS");
    }
}
