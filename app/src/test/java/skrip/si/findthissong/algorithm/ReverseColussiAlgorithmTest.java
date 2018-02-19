package skrip.si.findthissong.algorithm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * Created by Fajar on 12/10/2017.
 */
public class ReverseColussiAlgorithmTest {
    @Test
    public void search() throws Exception {
        String text = "SALAM TERAKHIR Ikang Fawzi Di malam ini kuberanikan diri Menemuimu 'tuk kesekian kalinya Janganlah kau takut Tiada guna pula kau bicara Kuingin menatapmu dewiku _ Dan kini kusadari Betapa s'lama ini kau mencurahkan Kasih sayang padaku Semata hanyalah 'tuk membangkitkan s'mangat juangku tanpa setitik rasa cinta Kau buka mataku kau tuntun diriku Memulai hidup baru Menjauhkan diri dari godaan Barang yang nista Kulakukan semua untukmu Perlahan aku mencoba bangkit lagi menatap tegap masa depan berseri Setulus hatiku kuhaturkan t'rima kasih sayang maafkan aku salah duga Kau buka mataku kau tuntun diriku Memulai hidup baru Menjauhkan diri dari godaan Barang yang nista Kulakukan semua untukmu T'rimalah salamku yang terakhir kali Ku akan pergi jauh Kucoba untuk mengerti dirimu dan kuberjanji Melangkah di jalan Nya _ Sayangku";
        ReverseColussiAlgorithm reverseColussi = new ReverseColussiAlgorithm();
        assertTrue(reverseColussi.search("/", text));
        System.out.print(reverseColussi.indexes());;

    }

    @Test
    public void indexes() throws Exception {
    }

}