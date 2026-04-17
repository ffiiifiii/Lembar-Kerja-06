import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class PerpustakaanSMP {

    // Deklarasi nama file konstan
    private static final String FILE_PEGAWAI = "pegawai.txt";
    private static final String FILE_SISWA = "siswa.txt";
    private static final String FILE_BUKU = "buku.txt";
    private static final String FILE_TRANSAKSI = "transaksi.txt";

    private static Scanner scanner = new Scanner(System.in);
    private static String loggedInPegawai = null;

    public static void main(String[] args) {
        inisialisasiFile();

        System.out.println("=== SELAMAT DATANG DI SISTEM PERPUSTAKAAN SMP ===");
        
        // Menu Autentikasi sebelum masuk ke Menu Utama
        while (loggedInPegawai == null) {
            System.out.println("\n=== MENU AUTENTIKASI ===");
            System.out.println("1. Login Pegawai");
            System.out.println("2. Buat Akun Pegawai (Maks 2 Pegawai)");
            System.out.print("Pilih menu (1/2): ");
            String authPilihan = scanner.nextLine().trim();

            if (authPilihan.equals("1")) {
                // CEK DISINI: Kalau belum ada pegawai sama sekali, langsung tolak!
                if (hitungJumlahPegawai() == 0) {
                    System.out.println("\n[INFO] Belum terdapat data pegawai untuk login. Silakan pilih menu 2 untuk buat akun terlebih dahulu!");
                } else {
                    // Kalau sudah ada data, baru boleh masuk ke proses login
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
        // Membuat file jika belum ada
        try {
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
        
        // Membatasi maksimal 2 pegawai
        if (jumlahPegawai >= 2) {
            System.out.println("GAGAL: Kuota pegawai sudah penuh! Sistem hanya mengizinkan maksimal 2 pegawai.");
            return;
        }

        System.out.print("Masukkan NIP Baru: ");
        String nip = scanner.nextLine().trim();

        // Pengecekan NIP duplikat
        if (cekNipTerdaftar(nip)) {
            System.out.println("GAGAL: NIP tersebut sudah terdaftar! Silakan gunakan NIP lain atau Login.");
            return;
        }

        System.out.print("Masukkan Nama Anda: ");
        String nama = scanner.nextLine().trim();
        System.out.print("Masukkan Password: ");
        String password = scanner.nextLine().trim();

        // Format: NIP,Nama,Password
        String dataBaru = nip + "," + nama + "," + password;
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
        } catch (IOException e) {
            // Abaikan jika error membaca
        }
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
                // Pengecekan NIP dan Password
                if (data.length >= 3 && data[0].equals(inputNip) && data[2].equals(inputPassword)) {
                    loggedInPegawai = data[1]; // Mengambil Nama Pegawai
                    ditemukan = true;
                    System.out.println("Login Berhasil! Selamat bekerja, " + loggedInPegawai);
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
    // 2. KELOLA DATA (BUKU & SISWA)
    // ==========================================

    private static void menuBuku() {
        System.out.println("\n--- KELOLA BUKU ---");
        System.out.println("1. Tambah Buku");
        System.out.println("2. Lihat Daftar Buku");
        System.out.print("Pilih: ");
        String pil = scanner.nextLine().trim();

        if (pil.equals("1")) {
            System.out.print("Masukkan Kode Buku: ");
            String kode = scanner.nextLine().trim();
            System.out.print("Masukkan Judul Buku: ");
            String judul = scanner.nextLine().trim();
            System.out.print("Masukkan Jenis Buku: ");
            String jenis = scanner.nextLine().trim();

            String dataBaru = kode + "," + judul + "," + jenis;
            tulisKeFile(FILE_BUKU, dataBaru);
            System.out.println("Buku berhasil ditambahkan!");
        } else if (pil.equals("2")) {
            bacaDariFile(FILE_BUKU, "Daftar Buku (Kode | Judul | Jenis)");
        }
    }

    private static void menuSiswa() {
        System.out.println("\n--- KELOLA SISWA ---");
        System.out.println("1. Tambah Siswa");
        System.out.println("2. Lihat Daftar Siswa");
        System.out.print("Pilih: ");
        String pil = scanner.nextLine().trim();

        if (pil.equals("1")) {
            System.out.print("Masukkan NIS: ");
            String nis = scanner.nextLine().trim();
            System.out.print("Masukkan Nama: ");
            String nama = scanner.nextLine().trim();
            System.out.print("Masukkan Alamat: ");
            String alamat = scanner.nextLine().trim();

            String dataBaru = nis + "," + nama + "," + alamat;
            tulisKeFile(FILE_SISWA, dataBaru);
            System.out.println("Siswa berhasil ditambahkan!");
        } else if (pil.equals("2")) {
            bacaDariFile(FILE_SISWA, "Daftar Siswa (NIS | Nama | Alamat)");
        }
    }

    // ==========================================
    // 3. TRANSAKSI (PINJAM & KEMBALI)
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
            System.out.println("Gagal mengecek batas pinjam: " + e.getMessage());
        }

        if (jumlahPinjam >= 2) {
            System.out.println("DITOLAK: Siswa ini sedang meminjam 2 buku dan belum dikembalikan. Batas maksimal tercapai!");
            return;
        }

        System.out.print("Masukkan Kode Buku: ");
        String kodeBuku = scanner.nextLine().trim();
        
        System.out.print("Lama Pinjam (hari, misal: 7): ");
        int lamaPinjam = Integer.parseInt(scanner.nextLine().trim());

        String kodeTransaksi = "TRX" + System.currentTimeMillis(); 
        LocalDate tglPinjam = LocalDate.now();
        LocalDate tglKembali = tglPinjam.plusDays(lamaPinjam);
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String dataTrx = kodeTransaksi + "," + nis + "," + kodeBuku + "," + 
                         tglPinjam.format(fmt) + "," + tglKembali.format(fmt) + ",0";
        
        tulisKeFile(FILE_TRANSAKSI, dataTrx);
        System.out.println("Peminjaman berhasil! Kode Transaksi: " + kodeTransaksi);
        System.out.println("Batas kembali: " + tglKembali.format(fmt));
    }

    private static void transaksiKembali() {
        System.out.println("\n--- PENGEMBALIAN BUKU ---");
        System.out.print("Masukkan Kode Transaksi: ");
        String kodeTrx = scanner.nextLine().trim();

        File fileAsli = new File(FILE_TRANSAKSI);
        File fileTemp = new File("temp_transaksi.txt");
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
    // 4. LAPORAN
    // ==========================================

    private static void lihatLaporan() {
        System.out.println("\n--- LAPORAN BUKU BELUM DIKEMBALIKAN & JATUH TEMPO ---");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate hariIni = LocalDate.now();
        boolean adaData = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_TRANSAKSI))) {
            String baris;
            System.out.printf("%-15s | %-10s | %-10s | %-15s | %-15s\n", "Kode Trx", "NIS", "Kode Buku", "Batas Kembali", "Status Denda");
            System.out.println("-------------------------------------------------------------------------------");
            
            while ((baris = br.readLine()) != null) {
                String[] data = baris.split(",");
                if (data.length >= 6 && data[5].equals("0")) { 
                    adaData = true;
                    LocalDate tglKembali = LocalDate.parse(data[4], fmt);
                    String statusDenda = "Aman";
                    
                    if (hariIni.isAfter(tglKembali)) {
                        long telatHari = ChronoUnit.DAYS.between(tglKembali, hariIni);
                        statusDenda = "Telat " + telatHari + " hari";
                    }

                    System.out.printf("%-15s | %-10s | %-10s | %-15s | %-15s\n", 
                                      data[0], data[1], data[2], data[4], statusDenda);
                }
            }
            if (!adaData) {
                System.out.println("Semua buku telah dikembalikan dengan tertib.");
            }
        } catch (IOException | DateTimeParseException e) {
            System.out.println("Error membuat laporan: " + e.getMessage());
        }
    }

    // ==========================================
    // 5. UTILITY FILE I/O (Write & Read Helper)
    // ==========================================

    private static void tulisKeFile(String namaFile, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(namaFile, true))) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Terjadi kesalahan saat menulis ke " + namaFile + ": " + e.getMessage());
        }
    }

    private static void bacaDariFile(String namaFile, String judulLaporan) {
        System.out.println("\n--- " + judulLaporan + " ---");
        try (BufferedReader br = new BufferedReader(new FileReader(namaFile))) {
            String baris;
            boolean kosong = true;
            while ((baris = br.readLine()) != null) {
                System.out.println("- " + baris.replace(",", " | "));
                kosong = false;
            }
            if (kosong) {
                System.out.println("(Data masih kosong)");
            }
        } catch (IOException e) {
            System.out.println("Terjadi kesalahan saat membaca file: " + e.getMessage());
        }
    }
}