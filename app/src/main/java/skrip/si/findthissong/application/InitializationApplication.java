package skrip.si.findthissong.application;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import skrip.si.findthissong.model.LyricModel;

/**
 * All initialization
 * Created by Fajar on 8/20/2017.
 */

public class InitializationApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialization
        // firebase
        // https://stackoverflow.com/a/40524235/4917020
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
