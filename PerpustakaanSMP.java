import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class PerpustakaanSMP {
    private static final String FILE_PEGAWAI = "D:\\pemlan\\LK06\\pegawai.txt";
    private static final String FILE_SISWA = "D:\\pemlan\\LK06\\siswa.txt";
    private static final String FILE_BUKU = "D:\\pemlan\\LK06\\buku.txt";
    private static final String FILE_TRANSAKSI = "D:\\pemlan\\LK06\\transaksi.txt";

    private static Scanner scanner = new Scanner(System.in);
    private static String loggedInPegawai = null;

    public static void main(String[] args) {
        inisialisasiFile();

        System.out.println("=== SELAMAT DATANG DI SISTEM PERPUSTAKAAN SMP ===");
        while (loggedInPegawai == null) {
            System.out.println("\n=== MENU AUTENTIKASI ===");
            System.out.println("1. Login Pegawai");
            System.out.println("2. Buat Akun Pegawai (Maks 2 Pegawai)");
            System.out.print("Pilih menu (1/2): ");
            String authPilihan = scanner.nextLine().trim();

            if (authPilihan.equals("1")) {
                if (hitungJumlahPegawai() == 0) {
                    System.out.println("\n[INFO] Belum terdapat data pegawai untuk login. Silakan pilih menu 2 untuk buat akun terlebih dahulu!");
                } else {
                    loginPegawai();
                }
            } else if (authPilihan.equals("2")) {
                buatAkunPegawai();
            } else {
                System.out.println("Pilihan tidak valid!");
            }
        }

        boolean jalan = true;
        while (jalan) {
            System.out.println("\n=== MENU UTAMA PERPUSTAKAAN ===");
            System.out.println("Petugas Aktif: " + loggedInPegawai);
            System.out.println("1. Kelola Data Buku");
            System.out.println("2. Kelola Data Siswa");
            System.out.println("3. Transaksi Peminjaman");
            System.out.println("4. Transaksi Pengembalian");
            System.out.println("5. Laporan (Buku Belum Kembali & Jatuh Tempo)");
            System.out.println("0. Keluar / Logout");
            System.out.print("Pilih menu (0-5): ");
            
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1": menuBuku(); break;
                case "2": menuSiswa(); break;
                case "3": transaksiPinjam(); break;
                case "4": transaksiKembali(); break;
                case "5": lihatLaporan(); break;
                case "0":
                    jalan = false;
                    System.out.println("Terima kasih telah menggunakan sistem ini, " + loggedInPegawai + "!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }

    // ==========================================
    // 1. SISTEM LOGIN, REGISTRASI & INISIALISASI
    // ==========================================

    private static void inisialisasiFile() {
        try {
            File folder = new File("D:\\pemlan\\LK06");
            if (!folder.exists()) {
                folder.mkdirs(); 
            }

            File fPegawai = new File(FILE_PEGAWAI);
            if (fPegawai.createNewFile()) {
                System.out.println("[INFO] Sistem baru dijalankan. Silakan buat akun pegawai terlebih dahulu.");
            }
            new File(FILE_SISWA).createNewFile();
            new File(FILE_BUKU).createNewFile();
            new File(FILE_TRANSAKSI).createNewFile();
        } catch (IOException e) {
            System.out.println("Terjadi kesalahan saat inisialisasi file: " + e.getMessage());
        }
    }

    private static int hitungJumlahPegawai() {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PEGAWAI))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            System.out.println("Error menghitung pegawai: " + e.getMessage());
        }
        return count;
    }

    private static void buatAkunPegawai() {
        int jumlahPegawai = hitungJumlahPegawai();
        
        if (jumlahPegawai >= 2) {
            System.out.println("GAGAL: Kuota pegawai sudah penuh! Sistem hanya mengizinkan maksimal 2 pegawai.");
            return;
        }

        System.out.print("Masukkan NIP Baru: ");
        String nip = scanner.nextLine().trim();

        if (cekNipTerdaftar(nip)) {
            System.out.println("GAGAL: NIP tersebut sudah terdaftar! Silakan gunakan NIP lain atau Login.");
            return;
        }

        System.out.print("Masukkan Nama Anda: ");
        String nama = scanner.nextLine().trim();
        System.out.print("Masukkan Tanggal Lahir (dd-MM-yyyy): ");
        String tglLahir = scanner.nextLine().trim();
        System.out.print("Masukkan Password: ");
        String password = scanner.nextLine().trim();

        // Format data: NIP, Nama, Password, Tanggal Lahir
        String dataBaru = nip + "," + nama + "," + password + "," + tglLahir;
        tulisKeFile(FILE_PEGAWAI, dataBaru);
        System.out.println("BERHASIL: Akun pegawai atas nama " + nama + " berhasil dibuat! Silakan Login.");
    }

    private static boolean cekNipTerdaftar(String nip) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PEGAWAI))) {
            String baris;
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(",");
                if (data.length >= 1 && data[0].equals(nip)) {
                    return true;
                }
            }
        } catch (IOException e) {}
        return false;
    }

    private static void loginPegawai() {
        System.out.print("\nMasukkan NIP Anda: ");
        String inputNip = scanner.nextLine().trim();
        System.out.print("Masukkan Password: ");
        String inputPassword = scanner.nextLine().trim();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PEGAWAI))) {
            String baris;
            boolean ditemukan = false;
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(","); 
                // data[0]=NIP, data[1]=Nama, data[2]=Password, data[3]=Tanggal Lahir
                if (data.length >= 3 && data[0].equals(inputNip) && data[2].equals(inputPassword)) {
                    loggedInPegawai = data[1]; 
                    ditemukan = true;
                    System.out.println("Login Berhasil! Selamat bekerja " + loggedInPegawai + " !");
                    break;
                }
            }
            if (!ditemukan) {
                System.out.println("LOGIN GAGAL: NIP atau Password salah.");
            }
        } catch (IOException e) {
            System.out.println("Error membaca file pegawai: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. KELOLA DATA BUKU
    // ==========================================

    private static void menuBuku() {
        System.out.println("\n--- KELOLA BUKU ---");
        System.out.println("1. Tambah Buku");
        System.out.println("2. Lihat Daftar Buku");
        System.out.println("3. Update Data Buku");
        System.out.println("4. Hapus Data Buku");
        System.out.println("0. Kembali ke Menu Utama");
        System.out.print("Pilih (0-4): ");
        String pil = scanner.nextLine().trim();

        switch (pil) {
            case "1":
                System.out.print("Masukkan Kode Buku: ");
                String kode = scanner.nextLine().trim();
                System.out.print("Masukkan Judul Buku: ");
                String judul = scanner.nextLine().trim();
                System.out.print("Masukkan Jenis Buku: ");
                String jenis = scanner.nextLine().trim();
                tulisKeFile(FILE_BUKU, kode + "," + judul + "," + jenis);
                System.out.println("Buku berhasil ditambahkan!");
                break;
            case "2":
                cetakTabel(FILE_BUKU, "DAFTAR BUKU PERPUSTAKAAN", 
                           new String[]{"Kode Buku", "Judul Buku", "Jenis Buku"}, 
                           new int[]{12, 30, 20});
                break;
            case "3":
                updateBuku();
                break;
            case "4":
                hapusBuku();
                break;
            case "0":
                return;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }

    private static void updateBuku() {
        System.out.print("Masukkan Kode Buku yang akan diubah: ");
        String targetKode = scanner.nextLine().trim();
        
        File fileAsli = new File(FILE_BUKU);
        File fileTemp = new File(fileAsli.getParent(), "temp_buku.txt");
        boolean ditemukan = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileAsli));
             BufferedWriter bw = new BufferedWriter(new FileWriter(fileTemp))) {
            
            String baris;
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(",");
                if (data.length >= 3 && data[0].equalsIgnoreCase(targetKode)) {
                    ditemukan = true;
                    String kodeFinal = data[0];
                    String judulFinal = data[1];
                    String jenisFinal = data[2];

                    boolean lanjutUpdate = true;
                    while (lanjutUpdate) {
                        System.out.println("\n--- DATA BUKU SAAT INI ---");
                        String border = "-------------------------------------------------------------------------";
                        System.out.println(border);
                        System.out.printf("| %-12s | %-30s | %-20s |\n", "Kode Buku", "Judul Buku", "Jenis Buku");
                        System.out.println(border);
                        System.out.printf("| %-12s | %-30s | %-20s |\n", kodeFinal, judulFinal, jenisFinal);
                        System.out.println(border);

                        System.out.println("\nPilih bagian yang ingin diubah:");
                        System.out.println("1. Kode Buku");
                        System.out.println("2. Judul Buku");
                        System.out.println("3. Jenis Buku");
                        System.out.println("0. Selesai & Simpan");
                        System.out.print("Pilih (0-3): ");
                        String pilUbah = scanner.nextLine().trim();

                        switch (pilUbah) {
                            case "1":
                                System.out.print("Masukkan Kode Buku Baru: ");
                                kodeFinal = scanner.nextLine().trim();
                                break;
                            case "2":
                                System.out.print("Masukkan Judul Buku Baru: ");
                                judulFinal = scanner.nextLine().trim();
                                break;
                            case "3":
                                System.out.print("Masukkan Jenis Buku Baru: ");
                                jenisFinal = scanner.nextLine().trim();
                                break;
                            case "0":
                                lanjutUpdate = false;
                                break;
                            default:
                                System.out.println("Pilihan tidak valid!");
                        }
                    }
                    baris = kodeFinal + "," + judulFinal + "," + jenisFinal;
                }
                bw.write(baris);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saat update: " + e.getMessage());
        }

        if (ditemukan) {
            fileAsli.delete();
            fileTemp.renameTo(fileAsli);
            System.out.println("Data buku berhasil diperbarui dan disimpan!");
        } else {
            fileTemp.delete();
            System.out.println("Buku dengan Kode " + targetKode + " tidak ditemukan.");
        }
    }

    private static void hapusBuku() {
        System.out.print("Masukkan Kode Buku yang akan dihapus: ");
        String targetKode = scanner.nextLine().trim();
        
        File fileAsli = new File(FILE_BUKU);
        File fileTemp = new File(fileAsli.getParent(), "temp_buku.txt");
        boolean ditemukan = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileAsli));
             BufferedWriter bw = new BufferedWriter(new FileWriter(fileTemp))) {
            
            String baris;
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(",");
                if (data.length >= 1 && data[0].equalsIgnoreCase(targetKode)) {
                    ditemukan = true;
                    continue; 
                }
                bw.write(baris);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saat menghapus: " + e.getMessage());
        }

        if (ditemukan) {
            fileAsli.delete();
            fileTemp.renameTo(fileAsli);
            System.out.println("Buku berhasil dihapus dari sistem!");
        } else {
            fileTemp.delete();
            System.out.println("Buku dengan Kode " + targetKode + " tidak ditemukan.");
        }
    }

    // ==========================================
    // 3. KELOLA DATA SISWA
    // ==========================================

    private static void menuSiswa() {
        System.out.println("\n--- KELOLA SISWA ---");
        System.out.println("1. Tambah Siswa");
        System.out.println("2. Lihat Daftar Siswa");
        System.out.println("3. Update Data Siswa");
        System.out.println("4. Hapus Data Siswa");
        System.out.println("0. Kembali ke Menu Utama");
        System.out.print("Pilih (0-4): ");
        String pil = scanner.nextLine().trim();

        switch (pil) {
            case "1":
                System.out.print("Masukkan NIS: ");
                String nis = scanner.nextLine().trim();
                System.out.print("Masukkan Nama: ");
                String nama = scanner.nextLine().trim();
                System.out.print("Masukkan Alamat: ");
                String alamat = scanner.nextLine().trim();
                tulisKeFile(FILE_SISWA, nis + "," + nama + "," + alamat);
                System.out.println("Siswa berhasil ditambahkan!");
                break;
            case "2":
                cetakTabel(FILE_SISWA, "DAFTAR SISWA TERDAFTAR", 
                           new String[]{"NIS", "Nama Siswa", "Alamat"}, 
                           new int[]{12, 25, 30});
                break;
            case "3":
                updateSiswa();
                break;
            case "4":
                hapusSiswa();
                break;
            case "0":
                return;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }

    private static void updateSiswa() {
        System.out.print("Masukkan NIS Siswa yang akan diubah: ");
        String targetNis = scanner.nextLine().trim();
        
        File fileAsli = new File(FILE_SISWA);
        File fileTemp = new File(fileAsli.getParent(), "temp_siswa.txt");
        boolean ditemukan = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileAsli));
             BufferedWriter bw = new BufferedWriter(new FileWriter(fileTemp))) {
            
            String baris;
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(",");
                if (data.length >= 3 && data[0].equalsIgnoreCase(targetNis)) {
                    ditemukan = true;
                    String nisFinal = data[0];
                    String namaFinal = data[1];
                    String alamatFinal = data[2];

                    boolean lanjutUpdate = true;
                    while (lanjutUpdate) {
                        System.out.println("\n--- DATA SISWA SAAT INI ---");
                        String border = "------------------------------------------------------------------------------";
                        System.out.println(border);
                        System.out.printf("| %-12s | %-25s | %-30s |\n", "NIS", "Nama Siswa", "Alamat");
                        System.out.println(border);
                        System.out.printf("| %-12s | %-25s | %-30s |\n", nisFinal, namaFinal, alamatFinal);
                        System.out.println(border);

                        System.out.println("\nPilih bagian yang ingin diubah:");
                        System.out.println("1. NIS");
                        System.out.println("2. Nama Siswa");
                        System.out.println("3. Alamat");
                        System.out.println("0. Selesai & Simpan");
                        System.out.print("Pilih (0-3): ");
                        String pilUbah = scanner.nextLine().trim();

                        switch (pilUbah) {
                            case "1":
                                System.out.print("Masukkan NIS Baru: ");
                                nisFinal = scanner.nextLine().trim();
                                break;
                            case "2":
                                System.out.print("Masukkan Nama Baru: ");
                                namaFinal = scanner.nextLine().trim();
                                break;
                            case "3":
                                System.out.print("Masukkan Alamat Baru: ");
                                alamatFinal = scanner.nextLine().trim();
                                break;
                            case "0":
                                lanjutUpdate = false;
                                break;
                            default:
                                System.out.println("Pilihan tidak valid!");
                        }
                    }
                    baris = nisFinal + "," + namaFinal + "," + alamatFinal;
                }
                bw.write(baris);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saat update: " + e.getMessage());
        }

        if (ditemukan) {
            fileAsli.delete();
            fileTemp.renameTo(fileAsli);
            System.out.println("Data Siswa berhasil diperbarui dan disimpan!");
        } else {
            fileTemp.delete();
            System.out.println("Siswa dengan NIS " + targetNis + " tidak ditemukan.");
        }
    }

    private static void hapusSiswa() {
        System.out.print("Masukkan NIS Siswa yang akan dihapus: ");
        String targetNis = scanner.nextLine().trim();
        
        File fileAsli = new File(FILE_SISWA);
        File fileTemp = new File(fileAsli.getParent(), "temp_siswa.txt");
        boolean ditemukan = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileAsli));
             BufferedWriter bw = new BufferedWriter(new FileWriter(fileTemp))) {
            
            String baris;
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(",");
                if (data.length >= 1 && data[0].equalsIgnoreCase(targetNis)) {
                    ditemukan = true;
                    continue; 
                }
                bw.write(baris);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saat menghapus: " + e.getMessage());
        }

        if (ditemukan) {
            fileAsli.delete();
            fileTemp.renameTo(fileAsli);
            System.out.println("Siswa berhasil dihapus dari sistem!");
        } else {
            fileTemp.delete();
            System.out.println("Siswa dengan NIS " + targetNis + " tidak ditemukan.");
        }
    }

    // ==========================================
    // 4. TRANSAKSI (PINJAM & KEMBALI)
    // ==========================================

    private static void transaksiPinjam() {
    System.out.println("\n--- PEMINJAMAN BUKU ---");
    System.out.print("Masukkan NIS Siswa: ");
    String nis = scanner.nextLine().trim();

    int jumlahPinjam = 0;

    try (BufferedReader br = new BufferedReader(new FileReader(FILE_TRANSAKSI))) {
        String baris;

        while ((baris = br.readLine()) != null) {
            String[] data = baris.split(",");

            if (data.length >= 6) {
                if (data[1].equals(nis) && data[5].equals("0")) {
                    jumlahPinjam++;
                }
            }
        }

    } catch (IOException e) {
        System.out.println("Gagal mengecek data transaksi: " + e.getMessage());
    }

    if (jumlahPinjam >= 2) {
        System.out.println("DITOLAK: Siswa sudah meminjam 2 buku!");
        return;
    }

    System.out.print("Masukkan Kode Buku: ");
    String kodeBuku = scanner.nextLine().trim();

    System.out.print("Lama Pinjam (hari): ");
    int lamaPinjam = Integer.parseInt(scanner.nextLine().trim());

    // KODE TRANSAKSI BARU
    String kodeTransaksi = generateKodeTransaksi();

    LocalDate tglPinjam = LocalDate.now();
    LocalDate tglKembali = tglPinjam.plusDays(lamaPinjam);

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    String dataBaru =
            kodeTransaksi + "," +
            nis + "," +
            kodeBuku + "," +
            tglPinjam.format(fmt) + "," +
            tglKembali.format(fmt) + ",0";

    tulisKeFile(FILE_TRANSAKSI, dataBaru);

    System.out.println("Peminjaman berhasil!");
    System.out.println("Kode Transaksi : " + kodeTransaksi);
    System.out.println("Tanggal Pinjam : " + tglPinjam.format(fmt));
    System.out.println("Batas Kembali  : " + tglKembali.format(fmt));
}


// =====================================
// METHOD BARU UNTUK AUTO KODE TRX
// =====================================
private static String generateKodeTransaksi() {
    int nomor = 1;

    try (BufferedReader br = new BufferedReader(new FileReader(FILE_TRANSAKSI))) {
        while (br.readLine() != null) {
            nomor++;
        }
    } catch (IOException e) {
        // abaikan jika file kosong
    }

    return String.format("TRX-%02d", nomor);
}

    private static void transaksiKembali() {
        System.out.println("\n--- PENGEMBALIAN BUKU ---");
        System.out.print("Masukkan Kode Transaksi: ");
        String kodeTrx = scanner.nextLine().trim();

        File fileAsli = new File(FILE_TRANSAKSI);
        File fileTemp = new File(fileAsli.getParent(), "temp_transaksi.txt");
        boolean ditemukan = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileAsli));
             BufferedWriter bw = new BufferedWriter(new FileWriter(fileTemp))) {
            
            String baris;
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(",");
                if (data.length >= 6 && data[0].equalsIgnoreCase(kodeTrx) && data[5].equals("0")) {
                    baris = data[0]+","+data[1]+","+data[2]+","+data[3]+","+data[4]+",1";
                    ditemukan = true;
                }
                bw.write(baris);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saat proses pengembalian: " + e.getMessage());
        }

        if (ditemukan) {
            fileAsli.delete();
            fileTemp.renameTo(fileAsli);
            System.out.println("Buku berhasil dikembalikan!");
        } else {
            fileTemp.delete();
            System.out.println("Kode transaksi tidak ditemukan atau buku sudah dikembalikan sebelumnya.");
        }
    }

    // ==========================================
    // 5. LAPORAN & FORMAT TABEL
    // ==========================================

    private static void lihatLaporan() {
    System.out.println("\n--- LAPORAN BUKU BELUM DIKEMBALIKAN & JATUH TEMPO ---");

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDate hariIni = LocalDate.now();
    boolean adaData = false;

    String border =
    "------------------------------------------------------------------------------------------------------------";

    System.out.println(border);
    System.out.printf("| %-10s | %-12s | %-20s | %-12s | %-15s | %-15s |\n",
            "Kode Trx", "NIS", "Nama Siswa", "Kode Buku", "Batas Kembali", "Status");

    System.out.println(border);

    try (BufferedReader br = new BufferedReader(new FileReader(FILE_TRANSAKSI))) {

        String baris;

        while ((baris = br.readLine()) != null) {
            String[] data = baris.split(",");

            if (data.length >= 6 && data[5].equals("0")) {

                adaData = true;

                String nis = data[1];
                String nama = cariNamaSiswa(nis);
                String kodeBuku = data[2];
                String batasKembali = data[4];

                LocalDate tglKembali = LocalDate.parse(batasKembali, fmt);

                String status = "Tidak";

                if (hariIni.isAfter(tglKembali)) {
                    status = "Ya";
                }

                System.out.printf("| %-10s | %-12s | %-20s | %-12s | %-15s | %-15s |\n",
                        data[0], nis, nama, kodeBuku, batasKembali, status);
            }
        }

        if (!adaData) {
            System.out.printf("| %-106s |\n",
                    "Semua buku telah dikembalikan.");
        }

        System.out.println(border);

    } catch (Exception e) {
        System.out.println("Error membuat laporan: " + e.getMessage());
    }
}
private static String cariNamaSiswa(String nisCari) {

    try (BufferedReader br = new BufferedReader(new FileReader(FILE_SISWA))) {

        String baris;

        while ((baris = br.readLine()) != null) {
            String[] data = baris.split(",");

            if (data.length >= 2 && data[0].equals(nisCari)) {
                return data[1];
            }
        }

    } catch (IOException e) {
    }

    return "Tidak Ditemukan";
}

    // ==========================================
    // 6. UTILITY FILE I/O & FORMATTER
    // ==========================================

    private static void tulisKeFile(String namaFile, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(namaFile, true))) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Terjadi kesalahan saat menulis ke " + namaFile + ": " + e.getMessage());
        }
    }

    private static void cetakTabel(String namaFile, String judulLaporan, String[] headers, int[] widths) {
        System.out.println("\n--- " + judulLaporan + " ---");

        int totalWidth = 1; 
        for (int w : widths) {
            totalWidth += w + 3; 
        }

        String border = "";
        for(int i = 0; i < totalWidth; i++) border += "-";

        System.out.println(border);
        System.out.print("|");
        for (int i = 0; i < headers.length; i++) {
            System.out.printf(" %-" + widths[i] + "s |", headers[i]);
        }
        System.out.println("\n" + border);

        try (BufferedReader br = new BufferedReader(new FileReader(namaFile))) {
            String baris;
            boolean kosong = true;
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(",");
                System.out.print("|");
                for (int i = 0; i < widths.length; i++) {
                    String value = (i < data.length) ? data[i] : "-";
                    System.out.printf(" %-" + widths[i] + "s |", value);
                }
                System.out.println();
                kosong = false;
            }
            if (kosong) {
                System.out.printf("| %-" + (totalWidth - 4) + "s |\n", "(Data Kosong) Silakan tambah data terlebih dahulu.");
            }
        } catch (IOException e) {
            System.out.printf("| %-" + (totalWidth - 4) + "s |\n", "Terjadi kesalahan saat membaca file.");
        }
        System.out.println(border);
    }
}