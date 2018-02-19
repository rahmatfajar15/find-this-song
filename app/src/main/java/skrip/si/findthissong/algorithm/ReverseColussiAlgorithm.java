package skrip.si.findthissong.algorithm;

import android.util.Log;

import java.util.ArrayList;

/**
 * Reverse Colussi String Matching Algorithm
 * Created by Fajar on 8/7/2017.
 */

public class ReverseColussiAlgorithm {

    private final static int ASIZE = 256;
    private int XSIZE;
    
    private char[] x;
    private char[] y;
    private int n;
    private int m;
    private int[][] rcBc;
    private int[] rcGs;
    private int[] h;
    private int[] locc;
    private int[] link;
    private int[] hmin;
    private int[] kmin;
    private int[] rmin;

    private ArrayList<Integer> resultIndex = new ArrayList<>();

    public boolean search(String pattern, String text) {
        x = pattern.toCharArray();
        y = text.toCharArray();
        m = pattern.length();
        n = text.length();

        XSIZE = m+1;

        rcBc = new int[ASIZE][XSIZE];
        rcGs = new int[ASIZE];
        h    = new int[ASIZE];
        locc = new int[ASIZE];
        link = new int[XSIZE];
        hmin = new int[XSIZE];
        kmin = new int[XSIZE];
        rmin = new int[XSIZE];

        try{
            preRC();
        } catch (Exception e) {
            Log.e("preRC", e.getMessage());
        }

        return searchingProcess();
    }

    public ArrayList<Integer> indexes() {
        return this.resultIndex;
    }

    private boolean searchingProcess() {
        boolean result = false;
        /* Searching */
        int i = -1;
        int j = 0;
        int s = m;
        while (j <= n - m) {
            while (j <= n - m && x[m - 1] != y[j + m - 1]) {
                s = rcBc[y[j + m - 1]][s];
                j += s;
            }

            try {
                for (i = 1; i < m && x[h[i]] == y[j + h[i]]; ++i);
            } catch (Exception e) {
                Log.e("RC loop FOR", e.getMessage()+"");
            }

            if (i >= m) {
                this.resultIndex.add(j);
                result = true;
            }
            s = rcGs[i];
            j += s;
        }

//        remove false positive result
        if (!resultIndex.isEmpty() && resultIndex.get(resultIndex.size() - 1 ) > n - m) {
            resultIndex.remove(resultIndex.size() - 1);
            if (resultIndex.isEmpty()){
                result = false;
            }
        }

        return result;
    }

    private void preRC(){
        /* Computation of link and locc */
        for (int a = 0; a < ASIZE; ++a) {
            locc[a] = -1;
        }
        link[0] = -1;
        for (int i = 0; i < m - 1; ++i) {
            link[i + 1] = locc[x[i]];
            locc[x[i]] = i;
        }
        /* Computation of rcBc */
        int i, j;
        for (int a = 0; a < ASIZE; ++a) {
            for (int s = 1; s <= m; ++s) {
                i = locc[a];
                j = link[m - s];
                while (i - j != s && j >= 0) {
                    if (i - j > s) {
                        i = link[i + 1];
                    } else {
                        j = link[j + 1];
                    }
                }
                while (i - j > s) {
                    i = link[i + 1];
                }
                rcBc[a][s] = m - i - 1;
            }
        }

        /* Computation of hmin */
        int k = 1;
        int q;
        i = m - 1;
        while (k <= m) {
            while (i - k >= 0 && x[i - k] == x[i]) {
                --i;
            }
            hmin[k] = i;
            q = k + 1;
            while (hmin[q - k] - (q - k) > i) {
                hmin[q] = hmin[q - k];
                ++q;
            }
            i += (q - k);
            k = q;
            if (i == m) {
                i = m - 1;
            }
        }

        /* Computation of kmin */
        for (int a = 0; a < XSIZE; ++a) {
            kmin[a] = 0;
        }
        for (k = m; k > 0; --k) {
            kmin[hmin[k]] = k;
        }

        /* Computation of rmin */
        int r = 0;
        for (i = m - 1; i >= 0; --i) {
            if (hmin[i + 1] == i) {
                r = i + 1;
            }
            rmin[i] = r;
        }

        /* Computation of rcGs */
        i = 1;
        for (k = 1; k <= m; ++k) {
            if (hmin[k] != m - 1 && kmin[hmin[k]] == k) {
                h[i] = hmin[k];
                rcGs[i++] = k;
            }
        }
        i = m-1;
        for (j = m - 2; j >= 0; --j) {
            if (kmin[j] == 0) {
                h[i] = j;
                rcGs[i--] = rmin[j];
            }
        }
        rcGs[m] = rmin[0];
    }

}
