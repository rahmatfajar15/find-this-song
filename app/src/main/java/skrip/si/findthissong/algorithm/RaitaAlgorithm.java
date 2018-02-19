package skrip.si.findthissong.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * RaitaAlgorithm Algorithm String Matching
 * Created by Fajar on 8/5/2017.
 */

public class RaitaAlgorithm {

    private final static int ASIZE = 256;

    private ArrayList<Integer> resultIndex = new ArrayList<>();

    public boolean search(String pattern, String text) {
        final int m = pattern.length(),
                n = text.length();
        final char first = pattern.charAt(0),
                middle = pattern.charAt(floorDivAlt(m, 2)),
                last = pattern.charAt(m-1);
        final int[] bmbc = preprocessing(pattern);


        /*Pengecekan*/
        boolean result = false;
        int j = 0;
        while (j <= (n-m)) {
            if (m == 1){
                if (pattern.toCharArray()[0] == text.toCharArray()[j]){
                    this.resultIndex.add(j);
                    result = true;
                }
            } else {
                if ( last == text.charAt(j+(m-1)) &&
                        first == text.charAt(j) &&
                        middle == text.charAt(j+floorDivAlt(m, 2)) &&
                        pattern.substring(1, m-1).equals(text.substring(j+1, j+(m-1)))
                        ){
                    this.resultIndex.add(j);
                    result = true;
                }
            }
            j += bmbc[ (int) text.toCharArray()[j+m-1] ];
        }

        return result;
    }

    /*-- https://stackoverflow.com/questions/27643616/ceil-conterpart-for-math-floordiv-in-java/27643634 --*/
    private int floorDivAlt(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    private int[] preprocessing(String pattern) {
        final int m = pattern.length();
        int[] bmBc = new int[ASIZE];

        for (int i=0 ; i < bmBc.length ; i++)
            bmBc[i] = m;

        for (int i=0 ; i < m-1 ; i++ )
            bmBc[ (int)pattern.toCharArray()[i] ] = m-i-1;

        return bmBc;
    }

    public ArrayList<Integer> indexes() {
        return this.resultIndex;
    }
}
