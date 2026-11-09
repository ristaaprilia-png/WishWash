package com.mycompany.wishwash;

public abstract class Pengguna {
    private String nama;
    private String noHp;
    private String kataSandi;

    public Pengguna(String nama, String noHp, String kataSandi) {
        this.nama = nama;
        this.noHp = noHp;
        this.kataSandi = kataSandi;
    }

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getNoHp() {
        return noHp;
    }
    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
    public void setKataSandi(String kataSandi) {

        this.kataSandi = kataSandi;

    }
    public boolean cekLogin(String noHp, String kataSandi) {
        return this.noHp.equals(noHp) && this.kataSandi.equals(kataSandi);
    }
    public abstract String getPeran();
}
