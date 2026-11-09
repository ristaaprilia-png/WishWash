package com.mycompany.wishwash;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;
public class AntreanLaundry {
    private Queue<Pesanan> antrean = new LinkedList<>();
    private int jamBuka = 8;
    private int jamTutup = 20;

    public int getJamBuka() {
        return jamBuka;
    }
    public int getJamTutup() {
        return jamTutup;
    }
    public void setJamOperasional(int buka, int tutup) {
        if (buka >= 0 && tutup <= 24 && buka < tutup) {
            this.jamBuka = buka;
            this.jamTutup = tutup;
            perbaruiEstimasi();
        }
    }

    public Queue<Pesanan> getAntrean() {
        return antrean;
    }
    public LocalDateTime tambahJamKerja(LocalDateTime mulai, int jam) {
        LocalDateTime t = sesuaikanJamKerja(mulai);
        int sisaMenit = jam * 60;
        while (sisaMenit > 0) {
            LocalDateTime tutup = t.toLocalDate().atTime(jamTutup % 24, 0);
            if (jamTutup == 24) {
                tutup = t.toLocalDate().plusDays(1).atStartOfDay();
            }
            int tersediaHariIni = (int) java.time.Duration.between(t, tutup).toMinutes();
            if (sisaMenit <= tersediaHariIni) {
                return t.plusMinutes(sisaMenit);
            }
            sisaMenit -= tersediaHariIni;
            t = t.toLocalDate().plusDays(1).atTime(jamBuka, 0);
        }
        return t;
    }

    private LocalDateTime sesuaikanJamKerja(LocalDateTime t) {
        LocalTime buka = LocalTime.of(jamBuka, 0);
        if (t.toLocalTime().isBefore(buka)) {
            return t.toLocalDate().atTime(buka);
        }
        if (jamTutup < 24 && !t.toLocalTime().isBefore(LocalTime.of(jamTutup, 0))) {
            return t.toLocalDate().plusDays(1).atTime(buka);
        }
        return t;
    }
    public LocalDateTime hitungEstimasiBaru(Layanan layanan) {
        LocalDateTime kursor = LocalDateTime.now();
        for (Pesanan p : antrean) {
            kursor = tambahJamKerja(kursor, p.getLayanan().getDurasi());
        }
        return tambahJamKerja(kursor, layanan.getDurasi());
    }

    public void tambah(Pesanan p) {
        antrean.add(p);
        perbaruiEstimasi();
    }

    public void keluarkan(Pesanan p) {
        antrean.remove(p);
        perbaruiEstimasi();   
    }

    public void perbaruiEstimasi() {
        LocalDateTime kursor = LocalDateTime.now();
        for (Pesanan p : antrean) {
            kursor = tambahJamKerja(kursor, p.getLayanan().getDurasi());
            p.setEstimasiSelesai(kursor);
        }
    }
}
