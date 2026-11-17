package com.mycompany.wishwash;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class WishWashGUI extends Application {

    private static final ArrayList<Pengguna> daftarPengguna = new ArrayList<>();
    private static final ArrayList<Layanan> daftarLayanan = new ArrayList<>();
    private static final ArrayList<Pesanan> daftarPesanan = new ArrayList<>();
    private static final AntreanLaundry antrean = new AntreanLaundry();
    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Stage stage;

    @Override
    public void init() {
        daftarPengguna.add(new Admin("Admin WishWash", "0800", "admin123"));
        daftarLayanan.add(new LayananKiloan("Cuci Kering", 6000, 3));
        daftarLayanan.add(new LayananKiloan("Cuci Setrika", 8000, 4));
        daftarLayanan.add(new LayananKiloan("Setrika Saja", 5000, 2));
        daftarLayanan.add(new LayananSatuan("Selimut", 25000, 5));
        daftarLayanan.add(new LayananSatuan("Jaket", 15000, 3));
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("WishWash - Sistem Informasi Laundry");
        tampilLogin();
        stage.show();
    }

    private void ganti(javafx.scene.Parent root, double lebar, double tinggi) {
        stage.setScene(new Scene(root, lebar, tinggi));
    }

    private void alert(Alert.AlertType tipe, String judul, String pesan) {
        Alert a = new Alert(tipe);
        a.setTitle(judul);
        a.setHeaderText(null);
        a.setContentText(pesan);
        a.showAndWait();
    }

    private void peringatan(String pesan) {
        alert(Alert.AlertType.WARNING, "Peringatan", pesan);
    }

    private double parseAngka(String teks) {
        double d = Double.parseDouble(teks.trim().replace(',', '.'));
        if (d <= 0) {
            throw new NumberFormatException("harus > 0");
        }
        return d;
    }

    private static String rupiah(double nilai) {
        return String.format("Rp%,.0f", nilai);
    }

    private Label judul(String teks) {
        Label l = new Label(teks);
        l.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        return l;
    }

    // ====== Tahap 1-3: Login & Registrasi ======
    private void tampilLogin() {
        TextField tfHp = new TextField();
        tfHp.setPromptText("No. HP");
        PasswordField pfSandi = new PasswordField();
        pfSandi.setPromptText("Kata sandi");

        Button btnLogin = new Button("Login");
        Button btnDaftar = new Button("Registrasi Pelanggan");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.add(new Label("No. HP"), 0, 0);
        form.add(tfHp, 1, 0);
        form.add(new Label("Kata sandi"), 0, 1);
        form.add(pfSandi, 1, 1);
        form.add(new HBox(10, btnLogin, btnDaftar), 1, 2);

        btnLogin.setOnAction(e -> {
            String hp = tfHp.getText().trim();
            String sandi = pfSandi.getText();
            if (hp.isEmpty() || sandi.isEmpty()) {
                peringatan("No. HP dan kata sandi wajib diisi.");
                return;
            }
            for (Pengguna p : daftarPengguna) {
                if (p.cekLogin(hp, sandi)) {
                    if (p instanceof Admin) {
                        tampilAdmin((Admin) p);
                    } else {
                        tampilPelanggan((Pelanggan) p);
                    }
                    return;
                }
            }
            alert(Alert.AlertType.ERROR, "Login gagal", "No. HP atau kata sandi salah.");
        });
        btnDaftar.setOnAction(e -> tampilRegistrasi());

        VBox root = new VBox(20, judul("WISHWASH - Sistem Informasi Laundry"), form);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        ganti(root, 520, 320);
    }

    private void tampilRegistrasi() {
        TextField tfNama = new TextField();
        TextField tfHp = new TextField();
        PasswordField pfSandi = new PasswordField();
        Button btnSimpan = new Button("Daftar");
        Button btnKembali = new Button("Kembali");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.add(new Label("Nama"), 0, 0);
        form.add(tfNama, 1, 0);
        form.add(new Label("No. HP"), 0, 1);
        form.add(tfHp, 1, 1);
        form.add(new Label("Kata sandi"), 0, 2);
        form.add(pfSandi, 1, 2);
        form.add(new HBox(10, btnSimpan, btnKembali), 1, 3);

        btnSimpan.setOnAction(e -> {
            String nama = tfNama.getText().trim();
            String hp = tfHp.getText().trim();
            String sandi = pfSandi.getText();
            if (nama.isEmpty() || hp.isEmpty() || sandi.isEmpty()) {
                peringatan("Semua data wajib diisi.");
                return;
            }
            for (Pengguna p : daftarPengguna) {
                if (p.getNoHp().equals(hp)) {
                    peringatan("No. HP sudah terdaftar.");
                    return;
                }
            }
            daftarPengguna.add(new Pelanggan(nama, hp, sandi));
            alert(Alert.AlertType.INFORMATION, "Berhasil", "Registrasi berhasil. Silakan login.");
            tampilLogin();
        });
        btnKembali.setOnAction(e -> tampilLogin());

        VBox root = new VBox(20, judul("Registrasi Pelanggan"), form);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        ganti(root, 520, 360);
    }

    private TableView<Layanan> buatTabelLayanan() {
        TableView<Layanan> t = new TableView<>();
        TableColumn<Layanan, String> cNama = new TableColumn<>("Layanan");
        cNama.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNama()));
        TableColumn<Layanan, String> cHarga = new TableColumn<>("Harga");
        cHarga.setCellValueFactory(c -> new ReadOnlyStringWrapper(
            rupiah(c.getValue().getHarga()) + "/" + c.getValue().getSatuan()));
        TableColumn<Layanan, String> cDurasi = new TableColumn<>("Durasi");
        cDurasi.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDurasi() + " jam"));
        t.getColumns().add(cNama);
        t.getColumns().add(cHarga);
        t.getColumns().add(cDurasi);
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.getItems().setAll(daftarLayanan);
        return t;
    }

    private TableView<Pesanan> buatTabelPesanan() {
        TableView<Pesanan> t = new TableView<>();
        TableColumn<Pesanan, String> cNo = new TableColumn<>("No");
        cNo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNomor()));
        TableColumn<Pesanan, String> cPel = new TableColumn<>("Pelanggan");
        cPel.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getPelanggan().getNama()));
        TableColumn<Pesanan, String> cLay = new TableColumn<>("Layanan");
        cLay.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getLayanan().getNama()));
        TableColumn<Pesanan, String> cJml = new TableColumn<>("Jumlah");
        cJml.setCellValueFactory(c -> new ReadOnlyStringWrapper(
            String.format("%.1f %s", c.getValue().getJumlah(), c.getValue().getLayanan().getSatuan())));
        TableColumn<Pesanan, String> cBiaya = new TableColumn<>("Biaya");
        cBiaya.setCellValueFactory(c -> new ReadOnlyStringWrapper(rupiah(c.getValue().getBiaya())));
        TableColumn<Pesanan, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getStatus()));
        TableColumn<Pesanan, String> cEst = new TableColumn<>("Estimasi");
        cEst.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEstimasiTeks()));
        TableColumn<Pesanan, String> cBayar = new TableColumn<>("Bayar");
        cBayar.setCellValueFactory(c -> new ReadOnlyStringWrapper(
            c.getValue().isLunas() ? "LUNAS" : "BELUM LUNAS"));
        t.getColumns().add(cNo);
        t.getColumns().add(cPel);
        t.getColumns().add(cLay);
        t.getColumns().add(cJml);
        t.getColumns().add(cBiaya);
        t.getColumns().add(cStatus);
        t.getColumns().add(cEst);
        t.getColumns().add(cBayar);
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return t;
    }

    private HBox headerPengguna(Pengguna p) {
        Label sapaan = new Label("Selamat datang, " + p.getNama() + " (" + p.getPeran() + ")");
        sapaan.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> tampilLogin());
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox h = new HBox(10, sapaan, spacer, btnLogout);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(10));
        return h;
    }

    // ====== Menu Pelanggan ======
    private void tampilPelanggan(Pelanggan user) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        TableView<Layanan> tabelLayanan = buatTabelLayanan();
        Tab tabLayanan = new Tab("Daftar Layanan", new VBox(tabelLayanan));
        VBox.setVgrow(tabelLayanan, javafx.scene.layout.Priority.ALWAYS);

        TableView<Pesanan> tabelRiwayat = buatTabelPesanan();
        Runnable muatRiwayat = () -> {
            antrean.perbaruiEstimasi();
            ArrayList<Pesanan> milik = new ArrayList<>();
            for (Pesanan p : daftarPesanan) {
                if (p.getPelanggan() == user) {
                    milik.add(p);
                }
            }
            tabelRiwayat.getItems().setAll(milik);
        };
        Button btnRefresh = new Button("Muat ulang");
        btnRefresh.setOnAction(e -> muatRiwayat.run());
        VBox boxRiwayat = new VBox(10, btnRefresh, tabelRiwayat);
        boxRiwayat.setPadding(new Insets(10));
        VBox.setVgrow(tabelRiwayat, javafx.scene.layout.Priority.ALWAYS);
        Tab tabRiwayat = new Tab("Pesanan & Riwayat", boxRiwayat);
        tabRiwayat.setOnSelectionChanged(e -> muatRiwayat.run());

        ComboBox<Layanan> cbLayanan = new ComboBox<>();
        cbLayanan.getItems().setAll(daftarLayanan);
        cbLayanan.setPromptText("Pilih layanan");
        cbLayanan.setConverter(new javafx.util.StringConverter<Layanan>() {
            @Override
            public String toString(Layanan l) {
                return l == null ? "" : l.getNama() + " - " + rupiah(l.getHarga()) + "/" + l.getSatuan();
            }
            @Override
            public Layanan fromString(String s) {
                return null;
            }
        });
        TextField tfJumlah = new TextField();
        tfJumlah.setPromptText("Berat (kg) / jumlah item");
        Label lbBiaya = new Label("-");
        Label lbEstimasi = new Label("-");
        Button btnHitung = new Button("Hitung Perkiraan");
        Button btnPesan = new Button("Buat Pesanan");

        Tab tabPesan = new Tab("Buat Pesanan");
        tabPesan.setOnSelectionChanged(e -> {
            Layanan dipilih = cbLayanan.getValue();
            cbLayanan.getItems().setAll(daftarLayanan);
            if (dipilih != null && daftarLayanan.contains(dipilih)) {
                cbLayanan.setValue(dipilih);
            }
        });

        btnHitung.setOnAction(e -> {
            Layanan l = cbLayanan.getValue();
            if (l == null || tfJumlah.getText().trim().isEmpty()) {
                peringatan("Pilih layanan dan isi berat/jumlah terlebih dahulu.");
                return;
            }
            try {
                double jumlah = parseAngka(tfJumlah.getText());
                lbBiaya.setText(rupiah(l.hitungBiaya(jumlah)));
                lbEstimasi.setText(antrean.hitungEstimasiBaru(l).format(FORMAT_TANGGAL));
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Input salah", "Berat/jumlah harus berupa angka lebih dari 0.");
            }
        });

        btnPesan.setOnAction(e -> {
            Layanan l = cbLayanan.getValue();
            if (l == null || tfJumlah.getText().trim().isEmpty()) {
                peringatan("Pilih layanan dan isi berat/jumlah terlebih dahulu.");
                return;
            }
            double jumlah;
            try {
                jumlah = parseAngka(tfJumlah.getText());
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Input salah", "Berat/jumlah harus berupa angka lebih dari 0.");
                return;
            }
            String ringkas = String.format("%s %.1f %s%nPerkiraan biaya: %s%nEstimasi selesai: %s%n%nKonfirmasi pesanan?",
                l.getNama(), jumlah, l.getSatuan(), rupiah(l.hitungBiaya(jumlah)),
                antrean.hitungEstimasiBaru(l).format(FORMAT_TANGGAL));
            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION, ringkas, ButtonType.YES, ButtonType.NO);
            konfirmasi.setTitle("Konfirmasi Pesanan");
            konfirmasi.setHeaderText(null);
            if (konfirmasi.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                Pesanan p = new Pesanan(user, l, jumlah);
                daftarPesanan.add(p);
                antrean.tambah(p);
                alert(Alert.AlertType.INFORMATION, "Pesanan dibuat",
                    "Nomor pesanan: " + p.getNomor() + "\nSilakan bawa cucian ke laundry.");
                tfJumlah.clear();
                lbBiaya.setText("-");
                lbEstimasi.setText("-");
                cbLayanan.setValue(null);
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.add(new Label("Layanan"), 0, 0);
        form.add(cbLayanan, 1, 0);
        form.add(new Label("Berat / jumlah"), 0, 1);
        form.add(tfJumlah, 1, 1);
        form.add(new Label("Perkiraan biaya"), 0, 2);
        form.add(lbBiaya, 1, 2);
        form.add(new Label("Estimasi selesai"), 0, 3);
        form.add(lbEstimasi, 1, 3);
        form.add(new HBox(10, btnHitung, btnPesan), 1, 4);
        tabPesan.setContent(form);

        tabs.getTabs().addAll(tabLayanan, tabPesan, tabRiwayat);

        VBox root = new VBox(headerPengguna(user), tabs);
        VBox.setVgrow(tabs, javafx.scene.layout.Priority.ALWAYS);
        ganti(root, 900, 520);
    }

    private void tampilAdmin(Admin admin) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(tabAdminLayanan(), tabAdminJam(), tabAdminPesanan(), tabAdminLaporan());

        VBox root = new VBox(headerPengguna(admin), tabs);
        VBox.setVgrow(tabs, javafx.scene.layout.Priority.ALWAYS);
        ganti(root, 1000, 560);
    }

    private Tab tabAdminLayanan() {
        TableView<Layanan> tabel = buatTabelLayanan();

        TextField tfNama = new TextField();
        tfNama.setPromptText("Nama layanan");
        ComboBox<String> cbJenis = new ComboBox<>();
        cbJenis.getItems().addAll("Kiloan", "Satuan");
        cbJenis.setValue("Kiloan");
        TextField tfHarga = new TextField();
        tfHarga.setPromptText("Harga");
        TextField tfDurasi = new TextField();
        tfDurasi.setPromptText("Durasi (jam)");

        Button btnTambah = new Button("Tambah");
        Button btnUbah = new Button("Ubah Harga/Durasi (terpilih)");
        Button btnHapus = new Button("Hapus (terpilih)");

        tabel.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                tfNama.setText(baru.getNama());
                tfHarga.setText(String.valueOf((long) baru.getHarga()));
                tfDurasi.setText(String.valueOf(baru.getDurasi()));
            }
        });

        btnTambah.setOnAction(e -> {
            if (tfNama.getText().trim().isEmpty() || tfHarga.getText().trim().isEmpty()
                || tfDurasi.getText().trim().isEmpty()) {
                peringatan("Nama, harga, dan durasi wajib diisi.");
                return;
            }
            try {
                double harga = parseAngka(tfHarga.getText());
                int durasi = (int) parseAngka(tfDurasi.getText());
                if (durasi < 1) {
                    throw new NumberFormatException("durasi");
                }
                String nama = tfNama.getText().trim();
                if (cbJenis.getValue().equals("Kiloan")) {
                    daftarLayanan.add(new LayananKiloan(nama, harga, durasi));
                } else {
                    daftarLayanan.add(new LayananSatuan(nama, harga, durasi));
                }
                tabel.getItems().setAll(daftarLayanan);
                tfNama.clear();
                tfHarga.clear();
                tfDurasi.clear();
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Input salah", "Harga dan durasi harus berupa angka lebih dari 0.");
            }
        });

        btnUbah.setOnAction(e -> {
            Layanan l = tabel.getSelectionModel().getSelectedItem();
            if (l == null) {
                peringatan("Pilih layanan di tabel terlebih dahulu.");
                return;
            }
            try {
                l.setHarga(parseAngka(tfHarga.getText()));
                l.setDurasi((int) parseAngka(tfDurasi.getText()));
                antrean.perbaruiEstimasi();
                tabel.refresh();
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Input salah", "Harga dan durasi harus berupa angka lebih dari 0.");
            }
        });

        btnHapus.setOnAction(e -> {
            Layanan l = tabel.getSelectionModel().getSelectedItem();
            if (l == null) {
                peringatan("Pilih layanan di tabel terlebih dahulu.");
                return;
            }
            Alert k = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus layanan \"" + l.getNama() + "\"?", ButtonType.YES, ButtonType.NO);
            k.setHeaderText(null);
            if (k.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                daftarLayanan.remove(l);
                tabel.getItems().setAll(daftarLayanan);
            }
        });

        HBox form = new HBox(10, tfNama, cbJenis, tfHarga, tfDurasi);
        HBox tombol = new HBox(10, btnTambah, btnUbah, btnHapus);
        VBox box = new VBox(10, tabel, form, tombol);
        box.setPadding(new Insets(10));
        VBox.setVgrow(tabel, javafx.scene.layout.Priority.ALWAYS);
        return new Tab("Kelola Layanan", box);
    }

    private Tab tabAdminJam() {
        Label lbSaatIni = new Label();
        Runnable muat = () -> lbSaatIni.setText(
            "Jam operasional saat ini: " + antrean.getJamBuka() + ":00 - " + antrean.getJamTutup() + ":00");
        muat.run();

        TextField tfBuka = new TextField();
        tfBuka.setPromptText("0-23");
        TextField tfTutup = new TextField();
        tfTutup.setPromptText("1-24");
        Button btnSimpan = new Button("Simpan");

        btnSimpan.setOnAction(e -> {
            if (tfBuka.getText().trim().isEmpty() || tfTutup.getText().trim().isEmpty()) {
                peringatan("Jam buka dan jam tutup wajib diisi.");
                return;
            }
            try {
                int buka = Integer.parseInt(tfBuka.getText().trim());
                int tutup = Integer.parseInt(tfTutup.getText().trim());
                if (buka < 0 || tutup > 24 || buka >= tutup) {
                    peringatan("Jam buka harus 0-23, jam tutup 1-24, dan jam buka harus lebih kecil dari jam tutup.");
                    return;
                }
                antrean.setJamOperasional(buka, tutup);
                muat.run();
                alert(Alert.AlertType.INFORMATION, "Berhasil", "Jam operasional diperbarui.");
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Input salah", "Jam harus berupa angka bulat.");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.add(lbSaatIni, 0, 0, 2, 1);
        form.add(new Label("Jam buka"), 0, 1);
        form.add(tfBuka, 1, 1);
        form.add(new Label("Jam tutup"), 0, 2);
        form.add(tfTutup, 1, 2);
        form.add(btnSimpan, 1, 3);
        return new Tab("Jam Operasional", form);
    }

    private Tab tabAdminPesanan() {
        TableView<Pesanan> tabel = buatTabelPesanan();
        Runnable muat = () -> {
            antrean.perbaruiEstimasi();
            tabel.getItems().setAll(daftarPesanan);
        };

        TextField tfJumlah = new TextField();
        tfJumlah.setPromptText("Berat/jumlah hasil timbang");
        Button btnKoreksi = new Button("Koreksi Berat/Jumlah");
        Button btnStatus = new Button("Status Berikutnya");
        Button btnBayar = new Button("Ubah Status Pembayaran");
        Button btnMuat = new Button("Muat Ulang");

        btnKoreksi.setOnAction(e -> {
            Pesanan p = tabel.getSelectionModel().getSelectedItem();
            if (p == null) {
                peringatan("Pilih pesanan di tabel terlebih dahulu.");
                return;
            }
            if (tfJumlah.getText().trim().isEmpty()) {
                peringatan("Isi berat/jumlah hasil timbang.");
                return;
            }
            try {
                p.setJumlah(parseAngka(tfJumlah.getText()));
                tfJumlah.clear();
                tabel.refresh();
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Input salah", "Berat/jumlah harus berupa angka lebih dari 0.");
            }
        });

        btnStatus.setOnAction(e -> {
            Pesanan p = tabel.getSelectionModel().getSelectedItem();
            if (p == null) {
                peringatan("Pilih pesanan di tabel terlebih dahulu.");
                return;
            }
            if (p.lanjutStatus()) {
                if (!p.sedangDikerjakan()) {
                    antrean.keluarkan(p);
                }
                muat.run();
            } else {
                alert(Alert.AlertType.INFORMATION, "Info", "Pesanan sudah selesai.");
            }
        });

        btnBayar.setOnAction(e -> {
            Pesanan p = tabel.getSelectionModel().getSelectedItem();
            if (p == null) {
                peringatan("Pilih pesanan di tabel terlebih dahulu.");
                return;
            }
            p.setLunas(!p.isLunas());
            tabel.refresh();
        });

        btnMuat.setOnAction(e -> muat.run());

        HBox aksi = new HBox(10, tfJumlah, btnKoreksi, btnStatus, btnBayar, btnMuat);
        VBox box = new VBox(10, tabel, aksi);
        box.setPadding(new Insets(10));
        VBox.setVgrow(tabel, javafx.scene.layout.Priority.ALWAYS);
        Tab tab = new Tab("Pesanan & Antrean", box);
        tab.setOnSelectionChanged(e -> muat.run());
        return tab;
    }

    private Tab tabAdminLaporan() {
        Label lbHariIni = new Label();
        Label lbTotal = new Label();
        Label lbJumlah = new Label();
        Runnable muat = () -> {
            double hariIni = 0;
            double total = 0;
            for (Pesanan p : daftarPesanan) {
                if (p.isLunas()) {
                    total += p.getBiaya();
                    if (p.getWaktuDibuat().toLocalDate().equals(LocalDate.now())) {
                        hariIni += p.getBiaya();
                    }
                }
            }
            lbHariIni.setText(rupiah(hariIni));
            lbTotal.setText(rupiah(total));
            lbJumlah.setText(String.valueOf(daftarPesanan.size()));
        };
        muat.run();
        Button btnMuat = new Button("Muat Ulang");
        btnMuat.setOnAction(e -> muat.run());

        GridPane g = new GridPane();
        g.setHgap(15);
        g.setVgap(10);
        g.setPadding(new Insets(20));
        g.add(new Label("Pendapatan hari ini"), 0, 0);
        g.add(lbHariIni, 1, 0);
        g.add(new Label("Total pendapatan"), 0, 1);
        g.add(lbTotal, 1, 1);
        g.add(new Label("Jumlah pesanan"), 0, 2);
        g.add(lbJumlah, 1, 2);
        g.add(btnMuat, 1, 3);
        Tab tab = new Tab("Laporan Pendapatan", g);
        tab.setOnSelectionChanged(e -> muat.run());
        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
