package com.mycompany.wishwash;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    static Scanner input = new Scanner(System.in);
    static ArrayList<Pengguna> daftarPengguna = new ArrayList<>();
    static ArrayList<Layanan> daftarLayanan = new ArrayList<>();
    static ArrayList<Pesanan> daftarPesanan = new ArrayList<>();
    static AntreanLaundry antrean = new AntreanLaundry();

    public static void main(String[] args) {
        daftarPengguna.add(new Admin("Admin WishWash", "0800", "admin123"));
        daftarLayanan.add(new LayananKiloan("Cuci Kering", 6000, 3));
        daftarLayanan.add(new LayananKiloan("Cuci Setrika", 8000, 4));
        daftarLayanan.add(new LayananKiloan("Setrika Saja", 5000, 2));
        daftarLayanan.add(new LayananSatuan("Selimut", 25000, 5));
        daftarLayanan.add(new LayananSatuan("Jaket", 15000, 3));

        boolean jalan = true;
        while (jalan) {
            System.out.println("\n=== WISHWASH - Sistem Informasi Laundry ===");
            System.out.println("1. Registrasi Pelanggan");
            System.out.println("2. Login");
            System.out.println("0. Keluar");
            int pilih = bacaInt("Pilih: ");
            switch (pilih) {
                case 1:
                    registrasi();
                    break;
                case 2:
                    login();
                    break;
                case 0:
                    jalan = false;
                    System.out.println("Terima kasih!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    static int bacaInt(String label) {
        while (true) {
            System.out.print(label);
            try {
                return Integer.parseInt(input.nextLine().trim());
            }
            catch (NumberFormatException e) {
                System.out.println("Masukkan angka bulat.");
            }
        }
    }

    static double bacaDouble(String label) {
        while (true) {
            System.out.print(label);
            try {
                double d = Double.parseDouble(input.nextLine().trim().replace(',', '.'));
                if (d > 0) {
                    return d;
                }
                System.out.println("Nilai harus lebih dari 0.");
            } catch (NumberFormatException e) { System.out.println("Masukkan angka."); }
        }
    }

    static String bacaString(String label) {
        System.out.print(label);
        return input.nextLine().trim();
    }

    static void registrasi() {
        String nama = bacaString("Nama      : ");
        String hp = bacaString("No. HP    : ");
        String sandi = bacaString("Kata sandi: ");
        if (nama.isEmpty() || hp.isEmpty() || sandi.isEmpty()) {
            System.out.println("Semua data wajib diisi.");
            return;
        }
        for (Pengguna p : daftarPengguna) {
            if (p.getNoHp().equals(hp)) {
                System.out.println("No. HP sudah terdaftar.");
                return;
            }
        }
        daftarPengguna.add(new Pelanggan(nama, hp, sandi));
        System.out.println("Registrasi berhasil. Silakan login.");
    }

    static void login() {
        String hp = bacaString("No. HP    : ");
        String sandi = bacaString("Kata sandi: ");
        for (Pengguna p : daftarPengguna) {
            if (p.cekLogin(hp, sandi)) {
                System.out.println("Login berhasil. Selamat datang, " + p.getNama() + " (" + p.getPeran() + ")");
                if (p instanceof Admin) {
                    menuAdmin((Admin) p);
                } else {
                    menuPelanggan((Pelanggan) p);
                }
                return;
            }
        }
        System.out.println("No. HP atau kata sandi salah.");
    }

    static void menuPelanggan(Pelanggan user) {
        boolean aktif = true;
        while (aktif) {
            System.out.println("\n--- Menu Pelanggan ---");
            System.out.println("1. Lihat daftar layanan");
            System.out.println("2. Buat pesanan baru");
            System.out.println("3. Pantau pesanan & riwayat transaksi");
            System.out.println("0. Logout");
            switch (bacaInt("Pilih: ")) {
                case 1:
                    tampilLayanan();
                    break;
                case 2:
                    buatPesanan(user);
                    break;
                case 3:
                    lihatPesananSaya(user);
                    break;
                case 0:
                    aktif = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    static void tampilLayanan() {
        System.out.println("\nNo | Layanan | Harga | Durasi");
        for (int i = 0; i < daftarLayanan.size(); i++) {
            Layanan l = daftarLayanan.get(i);
            System.out.printf("%d. %s - Rp%,.0f/%s - %d jam%n",
                i + 1, l.getNama(), l.getHarga(), l.getSatuan(), l.getDurasi());
        }
    }

    static void buatPesanan(Pelanggan user) {
        if (daftarLayanan.isEmpty()) {
            System.out.println("Belum ada layanan.");
            return;
        }
        tampilLayanan();
        int no = bacaInt("Pilih nomor layanan: ");
        if (no < 1 || no > daftarLayanan.size()) {
            System.out.println("Layanan tidak ada.");
            return;
        }
        Layanan l = daftarLayanan.get(no - 1);

        double jumlah = bacaDouble("Perkiraan " + (l instanceof LayananKiloan ? "berat (kg)" : "jumlah item") + ": ");
        System.out.printf("Perkiraan biaya  : Rp%,.0f%n", l.hitungBiaya(jumlah));
        System.out.println("Estimasi selesai : " + antrean.hitungEstimasiBaru(l)
            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        String ok = bacaString("Konfirmasi pesanan? (y/n): ");
        if (ok.equalsIgnoreCase("y")) {
            Pesanan p = new Pesanan(user, l, jumlah);
            daftarPesanan.add(p);
            antrean.tambah(p);
            System.out.println("Pesanan dibuat. Nomor pesanan: " + p.getNomor() + ". Silakan bawa cucian ke laundry.");
        } else {
            System.out.println("Pesanan dibatalkan.");
        }
    }

    static void lihatPesananSaya(Pelanggan user) {
        antrean.perbaruiEstimasi();
        boolean ada = false;
        for (Pesanan p : daftarPesanan) {
            if (p.getPelanggan() == user) {
                p.tampilkan();
                ada = true;
            }
        }
        if (!ada) {
            System.out.println("Belum ada pesanan.");
        }
    }

    static void menuAdmin(Admin admin) {
        boolean aktif = true;
        while (aktif) {
            System.out.println("\n--- Menu Admin ---");
            System.out.println("1. Kelola layanan");
            System.out.println("2. Atur jam operasional");
            System.out.println("3. Lihat antrean pesanan");
            System.out.println("4. Koreksi berat/jumlah (setelah ditimbang)");
            System.out.println("5. Perbarui status cucian");
            System.out.println("6. Catat pembayaran");
            System.out.println("7. Laporan pendapatan");
            System.out.println("0. Logout");
            switch (bacaInt("Pilih: ")) {
                case 1:
                    kelolaLayanan();
                    break;
                case 2:
                    aturJam();
                    break;
                case 3:
                    lihatAntrean();
                    break;
                case 4:
                    koreksiPesanan();
                    break;
                case 5:
                    perbaruiStatus();
                    break;
                case 6:
                    catatPembayaran();
                    break;
                case 7:
                    laporan();
                    break;
                case 0:
                    aktif = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    static void kelolaLayanan() {
        System.out.println("1. Tambah layanan");
        System.out.println("2. Ubah harga/durasi");
        System.out.println("3. Hapus layanan");
        System.out.println("0. Kembali");
        int pilih = bacaInt("Pilih: ");
        if (pilih == 1) {
            String nama = bacaString("Nama layanan: ");
            String jenis = bacaString("Jenis (1=Kiloan, 2=Satuan): ");
            double harga = bacaDouble("Harga: ");
            int durasi = (int) bacaDouble("Durasi (jam): ");
            if (jenis.equals("1")) {
                daftarLayanan.add(new LayananKiloan(nama, harga, durasi));
            } else {
                daftarLayanan.add(new LayananSatuan(nama, harga, durasi));
            }
            System.out.println("Layanan ditambahkan.");
        } else if (pilih == 2 || pilih == 3) {
            tampilLayanan();
            int no = bacaInt("Nomor layanan: ");
            if (no < 1 || no > daftarLayanan.size()) {
                System.out.println("Layanan tidak ada.");
                return;
            }
            Layanan l = daftarLayanan.get(no - 1);
            if (pilih == 2) {
                l.setHarga(bacaDouble("Harga baru: "));
                l.setDurasi((int) bacaDouble("Durasi baru (jam): "));
                antrean.perbaruiEstimasi();
                System.out.println("Layanan diperbarui.");
            } else {
                daftarLayanan.remove(l);
                System.out.println("Layanan dihapus.");
            }
        }
    }

    static void aturJam() {
        System.out.println("Jam operasional saat ini: " + antrean.getJamBuka() + ":00 - " + antrean.getJamTutup() + ":00");
        int buka = bacaInt("Jam buka (0-23): ");
        int tutup = bacaInt("Jam tutup (1-24): ");
        antrean.setJamOperasional(buka, tutup);
        System.out.println("Jam operasional: " + antrean.getJamBuka() + ":00 - " + antrean.getJamTutup() + ":00");
    }

    static void lihatAntrean() {
        antrean.perbaruiEstimasi();
        if (antrean.getAntrean().isEmpty()) {
            System.out.println("Antrean kosong.");
            return;
        }
        int urut = 1;
        for (Pesanan p : antrean.getAntrean()) {
            System.out.print(urut++ + ". ");
            p.tampilkan();
        }
    }

    static Pesanan cariPesanan(String nomor) {
        for (Pesanan p : daftarPesanan) {
            if (p.getNomor().equalsIgnoreCase(nomor)) {
                return p;
            }
        }
        System.out.println("Pesanan tidak ditemukan.");
        return null;
    }

    static void koreksiPesanan() {
        Pesanan p = cariPesanan(bacaString("Nomor pesanan: "));
        if (p == null) {
            return;
        }
        System.out.printf("Data lama: %.1f %s, biaya Rp%,.0f%n", p.getJumlah(), p.getLayanan().getSatuan(), p.getBiaya());
        p.setJumlah(bacaDouble("Berat/jumlah hasil timbang: "));
        System.out.printf("Biaya dihitung ulang: Rp%,.0f%n", p.getBiaya());
    }

    static void perbaruiStatus() {
        Pesanan p = cariPesanan(bacaString("Nomor pesanan: "));
        if (p == null) {
            return;
        }
        if (p.lanjutStatus()) {
            if (!p.sedangDikerjakan()) {
                antrean.keluarkan(p);
            }
            System.out.println("Status sekarang: " + p.getStatus());
        } else {
            System.out.println("Pesanan sudah selesai.");
        }
    }

    static void catatPembayaran() {
        Pesanan p = cariPesanan(bacaString("Nomor pesanan: "));
        if (p == null) {
            return;
        }
        String s = bacaString("Sudah dibayar? (y/n): ");
        p.setLunas(s.equalsIgnoreCase("y"));
        System.out.println("Pembayaran dicatat: " + (p.isLunas() ? "LUNAS" : "BELUM LUNAS"));
    }

    static void laporan() {
        double hariIni = 0, total = 0;
        for (Pesanan p : daftarPesanan) {
            if (p.isLunas()) {
                total += p.getBiaya();
                if (p.getWaktuDibuat().toLocalDate().equals(LocalDate.now())) {
                    hariIni += p.getBiaya();
                }
            }
        }
        System.out.printf("Pendapatan hari ini : Rp%,.0f%n", hariIni);
        System.out.printf("Total pendapatan    : Rp%,.0f%n", total);
        System.out.println("Jumlah pesanan      : " + daftarPesanan.size());
    }
}