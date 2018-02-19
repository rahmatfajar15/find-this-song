package skrip.si.findthissong.algorithm;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * Created by Fajar on 12/13/2017.
 */
public class RaitaAlgorithmTest {
    @Test
    public void search() throws Exception {
        RaitaAlgorithm raita = new RaitaAlgorithm();
        assertTrue(
                raita.search("tanah", "ditelan bencana tanah ini")
        );
    }

}