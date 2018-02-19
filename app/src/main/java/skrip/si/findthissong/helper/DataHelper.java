package skrip.si.findthissong.helper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Helper for ArrayList<ArrayList<Integer>>
 * Created by Fajar on 8/9/2017.
 */

public class DataHelper implements Serializable{

    private ArrayList<ArrayList<Integer>> indexList;

    public DataHelper(ArrayList<ArrayList<Integer>> indexes) {
        this.indexList = indexes;
    }

    public ArrayList<ArrayList<Integer>> getList(){
        return this.indexList;
    }
}
