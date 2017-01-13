package tech.anri.popadex;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Rayziken on 1/12/2017.
 */

public class Pokemon extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "Pokemon.db";
    private static final int DATABASE_VERSION = 1;

    public Pokemon(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}
