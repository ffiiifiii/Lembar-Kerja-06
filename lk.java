import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Siswa {
    private String nis;
    private String nama;
    private String alamat;

    public Siswa(String nis, String nama, String alamat) {
        this.nis = nis;
        this.nama = nama;
        this.alamat = alamat;
    }

    // Getter & Setter
    public String getNis() {
        return nis;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    /** Format simpan ke file: NIS|Nama|Alamat */
    public String toFileString() {
        return nis + "|" + nama + "|" + alamat;
    }

    /** Parsing dari baris file */
    public static Siswa fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3)
            return null;
        return new Siswa(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    @Override
    public String toString() {
        return String.format("%-12s %-25s %s", nis, nama, alamat);
    }
}

class Buku {
    private String kode;
    private String judul;
    private String jenis;

    public Buku(String kode, String judul, String jenis) {
        this.kode = kode;
        this.judul = judul;
        this.jenis = jenis;
    }

    // Getter & Setter
    public String getKode() {
        return kode;
    }

    public String getJudul() {
        return judul;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    /** Format simpan ke file: Kode|Judul|Jenis */
    public String toFileString() {
        return kode + "|" + judul + "|" + jenis;
    }

    /** Parsing dari baris file */
    public static Buku fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3)
            return null;
        return new Buku(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    @Override
    public String toString() {
        return String.format("%-8s %-35s %s", kode, judul, jenis);
    }
}

class Pegawai {
    private String nip;
    private String nama;
    private String tanggalLahir;
    private String password;

    public Pegawai(String nip, String nama, String tanggalLahir, String password) {
        this.nip = nip;
        this.nama = nama;
        this.tanggalLahir = tanggalLahir;
        this.password = password;
    }

    // Getter & Setter
    public String getNip() {
        return nip;
    }

    public String getNama() {
        return nama;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public String getPassword() {
        return password;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setTanggalLahir(String tgl) {
        this.tanggalLahir = tgl;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** Format simpan ke file: NIP|Nama|TanggalLahir|Password */
    public String toFileString() {
        return nip + "|" + nama + "|" + tanggalLahir + "|" + password;
    }

    /** Parsing dari baris file */
    public static Pegawai fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4)
            return null;
        return new Pegawai(parts[0].trim(), parts[1].trim(),
                parts[2].trim(), parts[3].trim());
    }

    @Override
    public String toString() {
        return String.format("%-10s %-25s %s", nip, nama, tanggalLahir);
    }
}

class Transaksi {
    public static final String STATUS_DIPINJAM = "0";
    public static final String STATUS_DIKEMBALIKAN = "1";
    public static final int MAKS_HARI_PINJAM = 7; // batas waktu peminjaman

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String kodeTrans;
    private String nis;
    private String kodeBuku;
    private String tanggalPinjam;
    private String tanggalKembali; // tanggal jatuh tempo
    private String tanggalDikembalikan; // tanggal aktual dikembalikan (bisa kosong)
    private String status;

    public Transaksi(String kodeTrans, String nis, String kodeBuku,
            String tanggalPinjam, String tanggalKembali,
            String tanggalDikembalikan, String status) {
        this.kodeTrans = kodeTrans;
        this.nis = nis;
        this.kodeBuku = kodeBuku;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.tanggalDikembalikan = tanggalDikembalikan;
        this.status = status;
    }

    // Getter & Setter
    public String getKodeTrans() {
        return kodeTrans;
    }

    public String getNis() {
        return nis;
    }

    public String getKodeBuku() {
        return kodeBuku;
    }

    public String getTanggalPinjam() {
        return tanggalPinjam;
    }

    public String getTanggalKembali() {
        return tanggalKembali;
    }

    public String getTanggalDikembalikan() {
        return tanggalDikembalikan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTanggalDikembalikan(String tgl) {
        this.tanggalDikembalikan = tgl;
    }

    /** Cek apakah sudah melewati jatuh tempo */
    public boolean isTerlambat() {
        if (STATUS_DIKEMBALIKAN.equals(status))
            return false;
        try {
            LocalDate jatuhTempo = LocalDate.parse(tanggalKembali, FMT);
            return LocalDate.now().isAfter(jatuhTempo);
        } catch (Exception e) {
            return false;
        }
    }

    /** Hitung jumlah hari keterlambatan */
    public long hariTerlambat() {
        if (!isTerlambat())
            return 0;
        try {
            LocalDate jatuhTempo = LocalDate.parse(tanggalKembali, FMT);
            return ChronoUnit.DAYS.between(jatuhTempo, LocalDate.now());
        } catch (Exception e) {
            return 0;
        }
    }

    /** Format simpan ke file */
    public String toFileString() {
        return kodeTrans + "|" + nis + "|" + kodeBuku + "|"
                + tanggalPinjam + "|" + tanggalKembali + "|"
                + tanggalDikembalikan + "|" + status;
    }

    /** Parsing dari baris file */
    public static Transaksi fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7)
            return null;
        return new Transaksi(
                parts[0].trim(), parts[1].trim(), parts[2].trim(),
                parts[3].trim(), parts[4].trim(), parts[5].trim(), parts[6].trim());
    }

    @Override
    public String toString() {
        String statusStr = STATUS_DIKEMBALIKAN.equals(status) ? "Dikembalikan" : "Dipinjam";
        String terlambat = isTerlambat() ? " [TERLAMBAT " + hariTerlambat() + " hari]" : "";
        return String.format("%-10s %-10s %-8s %-12s %-12s %-14s%s",
                kodeTrans, nis, kodeBuku, tanggalPinjam, tanggalKembali, statusStr, terlambat);
    }
}

class FileManager {
    private static final String DATA_DIR = "data/";

    static {
        // Buat folder data jika belum ada
        File dir = new File(DATA_DIR);
        if (!dir.exists())
            dir.mkdirs();
    }

    /** Baca semua baris dari file, kembalikan list kosong jika file belum ada */
    public static List<String> bacaFile(String namaFile) throws IOException {
        File file = new File(DATA_DIR + namaFile);
        List<String> baris = new ArrayList<>();
        if (!file.exists())
            return baris;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty())
                    baris.add(line);
            }
        }
        return baris;
    }

    /** Tulis semua baris ke file (overwrite) */
    public static void tulisFile(String namaFile, List<String> baris) throws IOException {
        File file = new File(DATA_DIR + namaFile);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String b : baris) {
                bw.write(b);
                bw.newLine();
            }
        }
    }

    /** Tambah satu baris ke file (append) */
    public static void tambahBaris(String namaFile, String baris) throws IOException {
        File file = new File(DATA_DIR + namaFile);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(baris);
            bw.newLine();
        }
    }

    /**
     * Update atau hapus baris berdasarkan kunci (kolom pertama sebelum '|')
     * Jika bariBaru null → hapus baris dengan kunci tersebut
     */
    public static boolean updateBaris(String namaFile, String kunci, String bariBaru)
            throws IOException {
        List<String> semua = bacaFile(namaFile);
        boolean ditemukan = false;
        List<String> hasil = new ArrayList<>();

        for (String b : semua) {
            String id = b.split("\\|")[0].trim();
            if (id.equalsIgnoreCase(kunci)) {
                ditemukan = true;
                if (bariBaru != null)
                    hasil.add(bariBaru); // update
                // jika null → skip (hapus)
            } else {
                hasil.add(b);
            }
        }
        if (ditemukan)
            tulisFile(namaFile, hasil);
        return ditemukan;
    }

    /** Cek apakah kunci (kolom pertama) sudah ada di file */
    public static boolean kunciAda(String namaFile, String kunci) throws IOException {
        for (String b : bacaFile(namaFile)) {
            if (b.split("\\|")[0].trim().equalsIgnoreCase(kunci))
                return true;
        }
        return false;
    }

    /** Generate ID transaksi otomatis: T001, T002, … */
    public static String generateKodeTrans(String namaFile) throws IOException {
        List<String> baris = bacaFile(namaFile);
        int max = 0;
        for (String b : baris) {
            String id = b.split("\\|")[0].trim();
            if (id.startsWith("T")) {
                try {
                    int n = Integer.parseInt(id.substring(1));
                    if (n > max)
                        max = n;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("T%03d", max + 1);
    }
}

class StringHelper {

    /** Ulangi karakter sebanyak panjang (Java 8 compatible) */
    private static String ulang(String karakter, int panjang) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < panjang; i++) {
            sb.append(karakter);
        }
        return sb.toString();
    }

    public static String garis(int panjang) {
        return ulang("=", panjang);
    }

    public static String garisTipis(int panjang) {
        return ulang("-", panjang);
    }

    /** Teks ke tengah dalam lebar tertentu */
    public static String tengah(String teks, int lebar) {
        if (teks == null)
            teks = "";

        if (teks.length() >= lebar)
            return teks;

        int padding = (lebar - teks.length()) / 2;
        return ulang(" ", padding) + teks;
    }

    /** Validasi: string tidak boleh kosong */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Validasi: hanya berisi angka */
    public static boolean isNumeric(String s) {
        if (isEmpty(s))
            return false;

        return s.trim().matches("\\d+");
    }

    /** Validasi format tanggal YYYY-MM-DD */
    public static boolean isValidDate(String tgl) {
        if (isEmpty(tgl))
            return false;

        return tgl.trim().matches("\\d{4}-\\d{2}-\\d{2}");
    }

    /** Kapitalisasi huruf pertama tiap kata */
    public static String titleCase(String s) {
        if (isEmpty(s))
            return "";

        String[] words = s.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1))
                  .append(" ");
            }
        }

        return sb.toString().trim();
    }

    /** Sembunyikan password dengan karakter '*' */
    public static String maskPassword(String pw) {
        if (pw == null)
            return "";

        return ulang("*", pw.length());
    }

    /** Potong string jika melebihi panjang maksimum */
    public static String truncate(String s, int max) {
        if (s == null)
            return "";

        if (s.length() > max)
            return s.substring(0, max - 2) + "..";

        return s;
    }

    /** Header tabel yang rapi */
    public static void printHeader(String judul, int lebar) {
        System.out.println(garis(lebar));
        System.out.println(tengah(judul, lebar));
        System.out.println(garis(lebar));
    }

    /** Cetak baris header kolom */
    public static void printKolomHeader(String... kolom) {
        StringBuilder sb = new StringBuilder();

        for (String k : kolom) {
            sb.append(k);
        }

        System.out.println(sb.toString());
    }
}

class SiswaService {
    private static final String FILE = "siswa.txt";

    /** Tambah siswa baru */
    public boolean tambah(Siswa s) throws IOException {
        if (FileManager.kunciAda(FILE, s.getNis())) {
            System.out.println("  [!] NIS " + s.getNis() + " sudah terdaftar.");
            return false;
        }
        FileManager.tambahBaris(FILE, s.toFileString());
        System.out.println("  [OK] Siswa berhasil ditambahkan.");
        return true;
    }

    /** Edit data siswa berdasarkan NIS */
    public boolean edit(String nis, String namaBaru, String alamatBaru) throws IOException {
        List<String> baris = FileManager.bacaFile(FILE);
        for (String b : baris) {
            String[] parts = b.split("\\|", -1);
            if (parts[0].trim().equalsIgnoreCase(nis)) {
                Siswa s = Siswa.fromFileString(b);
                if (s == null)
                    continue;
                if (!StringHelper.isEmpty(namaBaru))
                    s.setNama(namaBaru);
                if (!StringHelper.isEmpty(alamatBaru))
                    s.setAlamat(alamatBaru);
                boolean ok = FileManager.updateBaris(FILE, nis, s.toFileString());
                if (ok)
                    System.out.println("  [OK] Data siswa diperbarui.");
                return ok;
            }
        }
        System.out.println("  [!] NIS tidak ditemukan.");
        return false;
    }

    /** Hapus siswa berdasarkan NIS */
    public boolean hapus(String nis) throws IOException {
        boolean ok = FileManager.updateBaris(FILE, nis, null);
        if (ok)
            System.out.println("  [OK] Siswa dihapus.");
        else
            System.out.println("  [!] NIS tidak ditemukan.");
        return ok;
    }

    /** Cari siswa berdasarkan NIS */
    public Siswa cariByNis(String nis) throws IOException {
        for (String b : FileManager.bacaFile(FILE)) {
            Siswa s = Siswa.fromFileString(b);
            if (s != null && s.getNis().equalsIgnoreCase(nis))
                return s;
        }
        return null;
    }

    /** Ambil semua siswa */
    public List<Siswa> semuaSiswa() throws IOException {
        List<Siswa> list = new ArrayList<>();
        for (String b : FileManager.bacaFile(FILE)) {
            Siswa s = Siswa.fromFileString(b);
            if (s != null)
                list.add(s);
        }
        return list;
    }

    /** Tampilkan daftar semua siswa */
    public void tampilkan() throws IOException {
        List<Siswa> list = semuaSiswa();
        StringHelper.printHeader("DAFTAR SISWA", 60);
        System.out.printf("%-12s %-25s %s%n", "NIS", "NAMA", "ALAMAT");
        System.out.println(StringHelper.garisTipis(60));
        if (list.isEmpty()) {
            System.out.println("  (Belum ada data siswa)");
        } else {
            list.forEach(System.out::println);
        }
        System.out.println(StringHelper.garis(60));
        System.out.println("  Total: " + list.size() + " siswa");
    }
}

class BukuService {
    private static final String FILE = "buku.txt";

    /** Tambah buku baru */
    public boolean tambah(Buku b) throws IOException {
        if (FileManager.kunciAda(FILE, b.getKode())) {
            System.out.println("  [!] Kode buku " + b.getKode() + " sudah ada.");
            return false;
        }
        FileManager.tambahBaris(FILE, b.toFileString());
        System.out.println("  [OK] Buku berhasil ditambahkan.");
        return true;
    }

    /** Edit data buku */
    public boolean edit(String kode, String judulBaru, String jenisBaru) throws IOException {
        Buku buku = cariByKode(kode);
        if (buku == null) {
            System.out.println("  [!] Kode buku tidak ditemukan.");
            return false;
        }
        if (!StringHelper.isEmpty(judulBaru))
            buku.setJudul(judulBaru);
        if (!StringHelper.isEmpty(jenisBaru))
            buku.setJenis(jenisBaru);
        boolean ok = FileManager.updateBaris(FILE, kode, buku.toFileString());
        if (ok)
            System.out.println("  [OK] Data buku diperbarui.");
        return ok;
    }

    /** Hapus buku */
    public boolean hapus(String kode) throws IOException {
        boolean ok = FileManager.updateBaris(FILE, kode, null);
        if (ok)
            System.out.println("  [OK] Buku dihapus.");
        else
            System.out.println("  [!] Kode buku tidak ditemukan.");
        return ok;
    }

    /** Cari buku berdasarkan kode */
    public Buku cariByKode(String kode) throws IOException {
        for (String b : FileManager.bacaFile(FILE)) {
            Buku buku = Buku.fromFileString(b);
            if (buku != null && buku.getKode().equalsIgnoreCase(kode))
                return buku;
        }
        return null;
    }

    /** Cari buku berdasarkan judul (pencarian parsial) */
    public List<Buku> cariByJudul(String keyword) throws IOException {
        List<Buku> hasil = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (String b : FileManager.bacaFile(FILE)) {
            Buku buku = Buku.fromFileString(b);
            if (buku != null && buku.getJudul().toLowerCase().contains(kw)) {
                hasil.add(buku);
            }
        }
        return hasil;
    }

    /** Ambil semua buku */
    public List<Buku> semuaBuku() throws IOException {
        List<Buku> list = new ArrayList<>();
        for (String b : FileManager.bacaFile(FILE)) {
            Buku buku = Buku.fromFileString(b);
            if (buku != null)
                list.add(buku);
        }
        return list;
    }

    /** Tampilkan daftar semua buku */
    public void tampilkan() throws IOException {
        List<Buku> list = semuaBuku();
        StringHelper.printHeader("DAFTAR BUKU PERPUSTAKAAN", 65);
        System.out.printf("%-8s %-35s %s%n", "KODE", "JUDUL", "JENIS");
        System.out.println(StringHelper.garisTipis(65));
        if (list.isEmpty()) {
            System.out.println("  (Belum ada data buku)");
        } else {
            list.forEach(System.out::println);
        }
        System.out.println(StringHelper.garis(65));
        System.out.println("  Total: " + list.size() + " buku");
    }
}

class PegawaiService {
    private static final String FILE = "pegawai.txt";

    /**
     * Login pegawai: verifikasi NIP & password
     * 
     * @return objek Pegawai jika berhasil, null jika gagal
     */
    public Pegawai login(String nip, String password) throws IOException {
        for (String b : FileManager.bacaFile(FILE)) {
            Pegawai p = Pegawai.fromFileString(b);
            if (p != null
                    && p.getNip().equals(nip)
                    && p.getPassword().equals(password)) {
                return p;
            }
        }
        return null;
    }

    /** Tambah pegawai baru */
    public boolean tambah(Pegawai p) throws IOException {
        if (FileManager.kunciAda(FILE, p.getNip())) {
            System.out.println("  [!] NIP " + p.getNip() + " sudah terdaftar.");
            return false;
        }
        FileManager.tambahBaris(FILE, p.toFileString());
        System.out.println("  [OK] Pegawai berhasil ditambahkan.");
        return true;
    }

    /** Edit data pegawai */
    public boolean edit(String nip, String namaBaru, String tglBaru, String pwBaru)
            throws IOException {
        Pegawai p = cariByNip(nip);
        if (p == null) {
            System.out.println("  [!] NIP tidak ditemukan.");
            return false;
        }
        if (!StringHelper.isEmpty(namaBaru))
            p.setNama(namaBaru);
        if (!StringHelper.isEmpty(tglBaru))
            p.setTanggalLahir(tglBaru);
        if (!StringHelper.isEmpty(pwBaru))
            p.setPassword(pwBaru);
        boolean ok = FileManager.updateBaris(FILE, nip, p.toFileString());
        if (ok)
            System.out.println("  [OK] Data pegawai diperbarui.");
        return ok;
    }

    /** Hapus pegawai */
    public boolean hapus(String nip) throws IOException {
        boolean ok = FileManager.updateBaris(FILE, nip, null);
        if (ok)
            System.out.println("  [OK] Pegawai dihapus.");
        else
            System.out.println("  [!] NIP tidak ditemukan.");
        return ok;
    }

    /** Cari pegawai berdasarkan NIP */
    public Pegawai cariByNip(String nip) throws IOException {
        for (String b : FileManager.bacaFile(FILE)) {
            Pegawai p = Pegawai.fromFileString(b);
            if (p != null && p.getNip().equalsIgnoreCase(nip))
                return p;
        }
        return null;
    }

    /** Ambil semua pegawai */
    public List<Pegawai> semuaPegawai() throws IOException {
        List<Pegawai> list = new ArrayList<>();
        for (String b : FileManager.bacaFile(FILE)) {
            Pegawai p = Pegawai.fromFileString(b);
            if (p != null)
                list.add(p);
        }
        return list;
    }

    /** Tampilkan daftar semua pegawai */
    public void tampilkan() throws IOException {
        List<Pegawai> list = semuaPegawai();
        StringHelper.printHeader("DAFTAR PEGAWAI PERPUSTAKAAN", 60);
        System.out.printf("%-10s %-25s %s%n", "NIP", "NAMA", "TANGGAL LAHIR");
        System.out.println(StringHelper.garisTipis(60));
        if (list.isEmpty()) {
            System.out.println("  (Belum ada data pegawai)");
        } else {
            list.forEach(System.out::println);
        }
        System.out.println(StringHelper.garis(60));
        System.out.println("  Total: " + list.size() + " pegawai");
    }

    /** Inisialisasi akun admin default jika file pegawai kosong */
    public void initDefault() throws IOException {
        if (FileManager.bacaFile(FILE).isEmpty()) {
            Pegawai admin = new Pegawai("P001", "Admin Perpustakaan",
                    "1990-01-01", "    ");
            FileManager.tambahBaris(FILE, admin.toFileString());
            System.out.println("  [INFO] Akun default dibuat: NIP=P001, Password=admin123");
        }
    }
}

class TransaksiService {
    private static final String FILE = "transaksi.txt";
    private static final int MAKS_PINJAM = 2;
    private static final int MAKS_HARI = 7;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SiswaService siswaService;
    private final BukuService bukuService;

    public TransaksiService(SiswaService siswaService, BukuService bukuService) {
        this.siswaService = siswaService;
        this.bukuService = bukuService;
    }

    /**
     * Proses peminjaman buku
     */
    public boolean pinjam(String nis, String kodeBuku) throws IOException {
        // 1. Validasi siswa
        Siswa siswa = siswaService.cariByNis(nis);
        if (siswa == null) {
            System.out.println("  [!] NIS tidak ditemukan.");
            return false;
        }

        // 2. Validasi buku
        Buku buku = bukuService.cariByKode(kodeBuku);
        if (buku == null) {
            System.out.println("  [!] Kode buku tidak ditemukan.");
            return false;
        }

        // 3. Cek buku sudah dipinjam siswa ini
        List<Transaksi> aktif = getAktifByNis(nis);
        for (Transaksi t : aktif) {
            if (t.getKodeBuku().equalsIgnoreCase(kodeBuku)) {
                System.out.println("  [!] Siswa sudah meminjam buku ini.");
                return false;
            }
        }

        // 4. Cek kuota pinjam (maks 2)
        if (aktif.size() >= MAKS_PINJAM) {
            System.out.println("  [!] Siswa sudah meminjam " + MAKS_PINJAM
                    + " buku (maksimum). Kembalikan dulu sebelum meminjam lagi.");
            return false;
        }

        // 5. Cek buku sedang dipinjam orang lain
        if (bukuSedangDipinjam(kodeBuku)) {
            System.out.println("  [!] Buku sedang dipinjam oleh siswa lain.");
            return false;
        }

        // 6. Buat transaksi
        String kodeTrans = FileManager.generateKodeTrans(FILE);
        String tglPinjam = LocalDate.now().format(FMT);
        String tglKembali = LocalDate.now().plusDays(MAKS_HARI).format(FMT);

        Transaksi tr = new Transaksi(kodeTrans, nis, kodeBuku,
                tglPinjam, tglKembali, "-", Transaksi.STATUS_DIPINJAM);

        FileManager.tambahBaris(FILE, tr.toFileString());

        System.out.println("  [OK] Peminjaman berhasil!");
        System.out.println("       Kode Transaksi : " + kodeTrans);
        System.out.println("       Siswa          : " + siswa.getNama());
        System.out.println("       Buku           : " + buku.getJudul());
        System.out.println("       Tanggal Pinjam : " + tglPinjam);
        System.out.println("       Jatuh Tempo    : " + tglKembali);
        return true;
    }

    /**
     * Proses pengembalian buku
     */
    public boolean kembalikan(String kodeTrans) throws IOException {
        List<String> semua = FileManager.bacaFile(FILE);
        boolean ditemukan = false;

        List<String> hasil = new ArrayList<>();
        for (String b : semua) {
            Transaksi tr = Transaksi.fromFileString(b);
            if (tr != null && tr.getKodeTrans().equalsIgnoreCase(kodeTrans)) {
                if (Transaksi.STATUS_DIKEMBALIKAN.equals(tr.getStatus())) {
                    System.out.println("  [!] Buku sudah dikembalikan sebelumnya.");
                    return false;
                }
                // Update status
                tr.setStatus(Transaksi.STATUS_DIKEMBALIKAN);
                tr.setTanggalDikembalikan(LocalDate.now().format(FMT));
                hasil.add(tr.toFileString());
                ditemukan = true;

                // Tampilkan info
                Siswa siswa = siswaService.cariByNis(tr.getNis());
                Buku buku = bukuService.cariByKode(tr.getKodeBuku());
                System.out.println("  [OK] Pengembalian berhasil!");
                System.out.println("       Kode Transaksi  : " + kodeTrans);
                if (siswa != null)
                    System.out.println("       Siswa           : " + siswa.getNama());
                if (buku != null)
                    System.out.println("       Buku            : " + buku.getJudul());
                System.out.println("       Dikembalikan Tgl: " + LocalDate.now().format(FMT));
                if (tr.isTerlambat()) {
                    System.out.println("  [!] TERLAMBAT " + tr.hariTerlambat() + " hari dari jatuh tempo!");
                }
            } else {
                hasil.add(b);
            }
        }

        if (!ditemukan) {
            System.out.println("  [!] Kode transaksi tidak ditemukan.");
            return false;
        }

        FileManager.tulisFile(FILE, hasil);
        return true;
    }

    /** Ambil semua transaksi */
    public List<Transaksi> semuaTransaksi() throws IOException {
        List<Transaksi> list = new ArrayList<>();
        for (String b : FileManager.bacaFile(FILE)) {
            Transaksi t = Transaksi.fromFileString(b);
            if (t != null)
                list.add(t);
        }
        return list;
    }

    /** Ambil transaksi yang masih aktif (belum dikembalikan) */
    public List<Transaksi> transaksiAktif() throws IOException {
        return semuaTransaksi().stream()
                .filter(t -> Transaksi.STATUS_DIPINJAM.equals(t.getStatus()))
                .collect(Collectors.toList());
    }

    /** Ambil transaksi aktif milik siswa tertentu */
    public List<Transaksi> getAktifByNis(String nis) throws IOException {
        return transaksiAktif().stream()
                .filter(t -> t.getNis().equalsIgnoreCase(nis))
                .collect(Collectors.toList());
    }

    /** Cek apakah buku sedang dipinjam */
    private boolean bukuSedangDipinjam(String kodeBuku) throws IOException {
        return transaksiAktif().stream()
                .anyMatch(t -> t.getKodeBuku().equalsIgnoreCase(kodeBuku));
    }

    // ===================== LAPORAN =====================

    /** Laporan buku yang belum dikembalikan */
    public void laporanBelumDikembalikan() throws IOException {
        List<Transaksi> aktif = transaksiAktif();
        StringHelper.printHeader("LAPORAN BUKU BELUM DIKEMBALIKAN", 80);
        System.out.printf("%-10s %-10s %-8s %-12s %-12s %-15s%n",
                "KODE TR", "NIS", "BUKU", "TGL PINJAM", "JATUH TEMPO", "STATUS");
        System.out.println(StringHelper.garisTipis(80));

        if (aktif.isEmpty()) {
            System.out.println("  Semua buku sudah dikembalikan.");
        } else {
            for (Transaksi t : aktif) {
                String terlambat = t.isTerlambat()
                        ? "TERLAMBAT " + t.hariTerlambat() + "hr"
                        : "Tepat waktu";
                System.out.printf("%-10s %-10s %-8s %-12s %-12s %-15s%n",
                        t.getKodeTrans(), t.getNis(), t.getKodeBuku(),
                        t.getTanggalPinjam(), t.getTanggalKembali(), terlambat);
            }
        }
        System.out.println(StringHelper.garis(80));
        System.out.println("  Total buku dipinjam: " + aktif.size());
    }

    /** Laporan siswa yang terlambat mengembalikan */
    public void laporanTerlambat() throws IOException {
        List<Transaksi> terlambat = transaksiAktif().stream()
                .filter(Transaksi::isTerlambat)
                .collect(Collectors.toList());

        StringHelper.printHeader("LAPORAN KETERLAMBATAN PENGEMBALIAN", 80);
        System.out.printf("%-10s %-10s %-8s %-12s %-12s %-10s%n",
                "KODE TR", "NIS", "BUKU", "TGL PINJAM", "JATUH TEMPO", "TERLAMBAT");
        System.out.println(StringHelper.garisTipis(80));

        if (terlambat.isEmpty()) {
            System.out.println("  Tidak ada siswa yang terlambat.");
        } else {
            for (Transaksi t : terlambat) {
                Siswa s = siswaService.cariByNis(t.getNis());
                String nama = s != null ? " (" + s.getNama() + ")" : "";
                System.out.printf("%-10s %-10s %-8s %-12s %-12s %d hari%n",
                        t.getKodeTrans(), t.getNis() + nama, t.getKodeBuku(),
                        t.getTanggalPinjam(), t.getTanggalKembali(), t.hariTerlambat());
            }
        }
        System.out.println(StringHelper.garis(80));
        System.out.println("  Total siswa terlambat: " + terlambat.size());
    }

    /** Laporan riwayat transaksi lengkap */
    public void laporanRiwayat() throws IOException {
        List<Transaksi> semua = semuaTransaksi();
        StringHelper.printHeader("RIWAYAT TRANSAKSI LENGKAP", 90);
        System.out.printf("%-10s %-10s %-8s %-12s %-12s %-14s %-12s%n",
                "KODE TR", "NIS", "BUKU", "TGL PINJAM", "JATUH TEMPO", "TGL KEMBALI", "STATUS");
        System.out.println(StringHelper.garisTipis(90));

        if (semua.isEmpty()) {
            System.out.println("  Belum ada transaksi.");
        } else {
            for (Transaksi t : semua) {
                String status = Transaksi.STATUS_DIKEMBALIKAN.equals(t.getStatus())
                        ? "Dikembalikan"
                        : "Dipinjam";
                String tglKembaliAktual = "-".equals(t.getTanggalDikembalikan())
                        ? "-"
                        : t.getTanggalDikembalikan();
                System.out.printf("%-10s %-10s %-8s %-12s %-12s %-14s %-12s%n",
                        t.getKodeTrans(), t.getNis(), t.getKodeBuku(),
                        t.getTanggalPinjam(), t.getTanggalKembali(),
                        tglKembaliAktual, status);
            }
        }
        System.out.println(StringHelper.garis(90));
        long dipinjam = semua.stream().filter(t -> Transaksi.STATUS_DIPINJAM.equals(t.getStatus())).count();
        long kembali = semua.stream().filter(t -> Transaksi.STATUS_DIKEMBALIKAN.equals(t.getStatus())).count();
        System.out.println("  Total transaksi: " + semua.size()
                + " | Dipinjam: " + dipinjam + " | Dikembalikan: " + kembali);
    }

    /** Laporan statistik buku paling banyak dipinjam */
    public void laporanPopulerBuku() throws IOException {
        List<Transaksi> semua = semuaTransaksi();
        Map<String, Long> hitungBuku = new LinkedHashMap<>();

        for (Transaksi t : semua) {
            hitungBuku.merge(t.getKodeBuku(), 1L, Long::sum);
        }

        // Urutkan dari terbanyak
        List<Map.Entry<String, Long>> sorted = hitungBuku.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        StringHelper.printHeader("LAPORAN BUKU PALING POPULER", 55);
        System.out.printf("%-5s %-8s %-30s %-10s%n", "NO", "KODE", "JUDUL", "TOTAL PINJAM");
        System.out.println(StringHelper.garisTipis(55));

        if (sorted.isEmpty()) {
            System.out.println("  Belum ada transaksi.");
        } else {
            int no = 1;
            for (Map.Entry<String, Long> e : sorted) {
                Buku buku = bukuService.cariByKode(e.getKey());
                String judul = buku != null ? StringHelper.truncate(buku.getJudul(), 28) : "(tidak diketahui)";
                System.out.printf("%-5d %-8s %-30s %-10d%n",
                        no++, e.getKey(), judul, e.getValue());
            }
        }
        System.out.println(StringHelper.garis(55));
    }
}

public class lk {
    private static final Scanner sc = new Scanner(System.in);

    // Services
    private static final PegawaiService pegawaiService = new PegawaiService();
    private static final SiswaService siswaService = new SiswaService();
    private static final BukuService bukuService = new BukuService();
    private static final TransaksiService transaksiService = new TransaksiService(siswaService, bukuService);

    private static Pegawai pegawaiLogin = null; // pegawai yang sedang login

    public static void main(String[] args) {
        try {
            // Inisialisasi akun default jika belum ada
            pegawaiService.initDefault();

            tampilBanner();
            login();

            boolean lanjut = true;
            while (lanjut) {
                tampilMenuUtama();
                String pilihan = bacaInput("Pilih menu");
                switch (pilihan) {
                    case "1" : menuSiswa();
                    case "2" : menuBuku();
                    case "3" : menuPegawai();
                    case "4" : menuTransaksi();
                    case "5" : menuLaporan();
                    case "0" : {
                        System.out.println("\n  Terima kasih. Sampai jumpa!");
                        lanjut = false;
                    }
                    default : System.out.println("  [!] Pilihan tidak valid.\n");
                }
            }
        } catch (IOException e) {
            System.out.println("\n  [ERROR] Terjadi kesalahan file: " + e.getMessage());
        }
    }

    // ===================== LOGIN =====================

    private static void login() throws IOException {
        System.out.println(StringHelper.garis(50));
        System.out.println(StringHelper.tengah("HALAMAN LOGIN PEGAWAI", 50));
        System.out.println(StringHelper.garis(50));

        int percobaan = 0;
        while (pegawaiLogin == null) {
            if (percobaan >= 3) {
                System.out.println("  [!] Terlalu banyak percobaan. Program ditutup.");
                System.exit(0);
            }
            String nip = bacaInput("  NIP");
            String pw = bacaInput("  Password");

            try {
                pegawaiLogin = pegawaiService.login(nip, pw);
                if (pegawaiLogin == null) {
                    percobaan++;
                    System.out.println("  [!] NIP atau password salah. Sisa percobaan: " + (3 - percobaan));
                } else {
                    System.out.println("\n  Selamat datang, " + pegawaiLogin.getNama() + "!");
                }
            } catch (IOException e) {
                throw new IOException("Gagal membaca data pegawai: " + e.getMessage());
            }
        }
    }

    // ===================== MENU UTAMA =====================

    private static void tampilBanner() {
        System.out.println(StringHelper.garis(55));
        System.out.println(StringHelper.tengah("SISTEM PERPUSTAKAAN SMP NUSANTARA", 55));
        System.out.println(StringHelper.tengah("Versi 1.0", 55));
        System.out.println(StringHelper.garis(55));
        System.out.println();
    }

    private static void tampilMenuUtama() {
        System.out.println("\n" + StringHelper.garis(40));
        System.out.println(StringHelper.tengah("MENU UTAMA", 40));
        System.out.println(StringHelper.tengah("Login: " + pegawaiLogin.getNama(), 40));
        System.out.println(StringHelper.garis(40));
        System.out.println("  1. Manajemen Data Siswa");
        System.out.println("  2. Manajemen Data Buku");
        System.out.println("  3. Manajemen Data Pegawai");
        System.out.println("  4. Transaksi Peminjaman & Pengembalian");
        System.out.println("  5. Laporan");
        System.out.println("  0. Keluar");
        System.out.println(StringHelper.garis(40));
    }

    // ===================== MENU SISWA =====================

    private static void menuSiswa() throws IOException {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n" + StringHelper.garis(35));
            System.out.println(StringHelper.tengah("MENU DATA SISWA", 35));
            System.out.println(StringHelper.garis(35));
            System.out.println("  1. Lihat Semua Siswa");
            System.out.println("  2. Tambah Siswa");
            System.out.println("  3. Edit Siswa");
            System.out.println("  4. Hapus Siswa");
            System.out.println("  5. Cari Siswa (by NIS)");
            System.out.println("  0. Kembali");
            System.out.println(StringHelper.garis(35));
            String p = bacaInput("Pilih");
            switch (p) {
                case "1" : {
                    try {
                        siswaService.tampilkan();
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "2" : {
                    String nis = bacaInput("  NIS");
                    String nama = bacaInput("  Nama");
                    String alamat = bacaInput("  Alamat");
                    if (StringHelper.isEmpty(nis) || StringHelper.isEmpty(nama)) {
                        System.out.println("  [!] NIS dan Nama tidak boleh kosong.");
                        break;
                    }
                    try {
                        siswaService.tambah(new Siswa(nis.toUpperCase(), nama, alamat));
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "3" : {
                    String nis = bacaInput("  NIS yang diedit");
                    String nama = bacaInput("  Nama baru (Enter=skip)");
                    String alamat = bacaInput("  Alamat baru (Enter=skip)");
                    try {
                        siswaService.edit(nis, nama, alamat);
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "4" : {
                    String nis = bacaInput("  NIS yang dihapus");
                    String conf = bacaInput("  Konfirmasi hapus? (y/n)");
                    if ("y".equalsIgnoreCase(conf)) {
                        try {
                            siswaService.hapus(nis);
                        } catch (IOException e) {
                            System.out.println("  [ERROR] " + e.getMessage());
                        }
                    }
                }
                case "5" : {
                    String nis = bacaInput("  Masukkan NIS");
                    try {
                        Siswa s = siswaService.cariByNis(nis);
                        if (s != null) {
                            System.out.println(StringHelper.garisTipis(50));
                            System.out.println("  NIS    : " + s.getNis());
                            System.out.println("  Nama   : " + s.getNama());
                            System.out.println("  Alamat : " + s.getAlamat());
                            System.out.println(StringHelper.garisTipis(50));
                        } else {
                            System.out.println("  [!] Siswa tidak ditemukan.");
                        }
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "0" : kembali = true;
                default : System.out.println("  [!] Pilihan tidak valid.");
            }
        }
    }

    // ===================== MENU BUKU =====================

    private static void menuBuku() throws IOException {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n" + StringHelper.garis(35));
            System.out.println(StringHelper.tengah("MENU DATA BUKU", 35));
            System.out.println(StringHelper.garis(35));
            System.out.println("  1. Lihat Semua Buku");
            System.out.println("  2. Tambah Buku");
            System.out.println("  3. Edit Buku");
            System.out.println("  4. Hapus Buku");
            System.out.println("  5. Cari Buku (by Judul)");
            System.out.println("  0. Kembali");
            System.out.println(StringHelper.garis(35));
            String p = bacaInput("Pilih");
            switch (p) {
                case "1" : {
                    try {
                        bukuService.tampilkan();
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "2" : {
                    String kode = bacaInput("  Kode Buku");
                    String judul = bacaInput("  Judul");
                    String jenis = bacaInput("  Jenis Buku");
                    if (StringHelper.isEmpty(kode) || StringHelper.isEmpty(judul)) {
                        System.out.println("  [!] Kode dan Judul tidak boleh kosong.");
                        break;
                    }
                    try {
                        bukuService.tambah(new Buku(kode.toUpperCase(), judul, jenis));
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "3" : {
                    String kode = bacaInput("  Kode yang diedit");
                    String judul = bacaInput("  Judul baru (Enter=skip)");
                    String jenis = bacaInput("  Jenis baru (Enter=skip)");
                    try {
                        bukuService.edit(kode, judul, jenis);
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "4" : {
                    String kode = bacaInput("  Kode yang dihapus");
                    String conf = bacaInput("  Konfirmasi hapus? (y/n)");
                    if ("y".equalsIgnoreCase(conf)) {
                        try {
                            bukuService.hapus(kode);
                        } catch (IOException e) {
                            System.out.println("  [ERROR] " + e.getMessage());
                        }
                    }
                }
                case "5" : {
                    String kw = bacaInput("  Kata kunci judul");
                    try {
                        List<Buku> hasil = bukuService.cariByJudul(kw);
                        if (hasil.isEmpty()) {
                            System.out.println("  Tidak ada buku dengan kata kunci tersebut.");
                        } else {
                            System.out.printf("%-8s %-35s %s%n", "KODE", "JUDUL", "JENIS");
                            System.out.println(StringHelper.garisTipis(55));
                            hasil.forEach(System.out::println);
                        }
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "0" : kembali = true;
                default : System.out.println("  [!] Pilihan tidak valid.");
            }
        }
    }

    // ===================== MENU PEGAWAI =====================

    private static void menuPegawai() throws IOException {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n" + StringHelper.garis(35));
            System.out.println(StringHelper.tengah("MENU DATA PEGAWAI", 35));
            System.out.println(StringHelper.garis(35));
            System.out.println("  1. Lihat Semua Pegawai");
            System.out.println("  2. Tambah Pegawai");
            System.out.println("  3. Edit Pegawai");
            System.out.println("  4. Hapus Pegawai");
            System.out.println("  0. Kembali");
            System.out.println(StringHelper.garis(35));
            String p = bacaInput("Pilih");
            switch (p) {
                case "1" : {
                    try {
                        pegawaiService.tampilkan();
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "2" : {
                    String nip = bacaInput("  NIP");
                    String nama = bacaInput("  Nama");
                    String tgl = bacaInput("  Tanggal Lahir (YYYY-MM-DD)");
                    String pw = bacaInput("  Password");
                    if (StringHelper.isEmpty(nip) || StringHelper.isEmpty(nama)) {
                        System.out.println("  [!] NIP dan Nama tidak boleh kosong.");
                        break;
                    }
                    if (!StringHelper.isValidDate(tgl)) {
                        System.out.println("  [!] Format tanggal tidak valid (YYYY-MM-DD).");
                        break;
                    }
                    try {
                        pegawaiService.tambah(new Pegawai(nip, nama, tgl, pw));
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "3" : {
                    String nip = bacaInput("  NIP yang diedit");
                    String nama = bacaInput("  Nama baru (Enter=skip)");
                    String tgl = bacaInput("  Tanggal Lahir baru (Enter=skip)");
                    String pw = bacaInput("  Password baru (Enter=skip)");
                    try {
                        pegawaiService.edit(nip, nama, tgl, pw);
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "4" : {
                    String nip = bacaInput("  NIP yang dihapus");
                    if (nip.equals(pegawaiLogin.getNip())) {
                        System.out.println("  [!] Tidak bisa menghapus akun yang sedang login.");
                        break;
                    }
                    String conf = bacaInput("  Konfirmasi hapus? (y/n)");
                    if ("y".equalsIgnoreCase(conf)) {
                        try {
                            pegawaiService.hapus(nip);
                        } catch (IOException e) {
                            System.out.println("  [ERROR] " + e.getMessage());
                        }
                    }
                }
                case "0" : kembali = true;
                default : System.out.println("  [!] Pilihan tidak valid.");
            }
        }
    }

    // ===================== MENU TRANSAKSI =====================

    private static void menuTransaksi() throws IOException {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n" + StringHelper.garis(40));
            System.out.println(StringHelper.tengah("MENU TRANSAKSI", 40));
            System.out.println(StringHelper.garis(40));
            System.out.println("  1. Pinjam Buku");
            System.out.println("  2. Kembalikan Buku");
            System.out.println("  3. Lihat Peminjaman Aktif");
            System.out.println("  4. Cek Buku Dipinjam oleh Siswa");
            System.out.println("  0. Kembali");
            System.out.println(StringHelper.garis(40));
            String p = bacaInput("Pilih");
            switch (p) {
                case "1" : {
                    String nis = bacaInput("  NIS Siswa");
                    String kode = bacaInput("  Kode Buku");
                    try {
                        transaksiService.pinjam(nis, kode);
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "2" : {
                    String kodeTr = bacaInput("  Kode Transaksi");
                    try {
                        transaksiService.kembalikan(kodeTr);
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "3" : {
                    try {
                        transaksiService.laporanBelumDikembalikan();
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "4" : {
                    String nis = bacaInput("  NIS Siswa");
                    try {
                        List<Transaksi> aktif = transaksiService.getAktifByNis(nis);
                        Siswa siswa = siswaService.cariByNis(nis);
                        String namaSiswa = siswa != null ? siswa.getNama() : "(tidak diketahui)";
                        System.out.println(StringHelper.garisTipis(60));
                        System.out.println("  Siswa: " + namaSiswa + " (" + nis + ")");
                        System.out.println(StringHelper.garisTipis(60));
                        if (aktif.isEmpty()) {
                            System.out.println("  Tidak ada buku yang sedang dipinjam.");
                        } else {
                            for (Transaksi t : aktif) {
                                Buku b = bukuService.cariByKode(t.getKodeBuku());
                                String judul = b != null ? b.getJudul() : "(tidak diketahui)";
                                System.out.println("  - " + t.getKodeBuku() + " | " + judul
                                        + " | Jatuh tempo: " + t.getTanggalKembali()
                                        + (t.isTerlambat() ? " [TERLAMBAT!]" : ""));
                            }
                        }
                        System.out.println(StringHelper.garisTipis(60));
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "0" : kembali = true;
                default : System.out.println("  [!] Pilihan tidak valid.");
            }
        }
    }

    // ===================== MENU LAPORAN =====================

    private static void menuLaporan() throws IOException {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n" + StringHelper.garis(40));
            System.out.println(StringHelper.tengah("MENU LAPORAN", 40));
            System.out.println(StringHelper.garis(40));
            System.out.println("  1. Buku yang Belum Dikembalikan");
            System.out.println("  2. Siswa yang Terlambat Mengembalikan");
            System.out.println("  3. Riwayat Semua Transaksi");
            System.out.println("  4. Buku Paling Populer");
            System.out.println("  0. Kembali");
            System.out.println(StringHelper.garis(40));
            String p = bacaInput("Pilih");
            switch (p) {
                case "1" : {
                    try {
                        transaksiService.laporanBelumDikembalikan();
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "2" : {
                    try {
                        transaksiService.laporanTerlambat();
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "3" : {
                    try {
                        transaksiService.laporanRiwayat();
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "4" : {
                    try {
                        transaksiService.laporanPopulerBuku();
                    } catch (IOException e) {
                        System.out.println("  [ERROR] " + e.getMessage());
                    }
                }
                case "0" : kembali = true;
                default : System.out.println("  [!] Pilihan tidak valid.");
            }
        }
    }

    // ===================== HELPER =====================

    /** Baca input dari pengguna */
    private static String bacaInput(String prompt) {
        System.out.print(prompt + " > ");
        return sc.nextLine().trim();
    }
}