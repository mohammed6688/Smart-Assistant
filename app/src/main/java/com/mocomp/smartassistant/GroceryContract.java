package com.mocomp.smartassistant;


import android.provider.BaseColumns;

public class GroceryContract {

    private GroceryContract() {
    }

    public static final class GroceryEntry implements BaseColumns {
        public static final String TABLE_NAME = "groceryList";
        public static final String COLUMN_ENGLISH = "english";
        public static final String COLUMN_ARABIC = "arabic";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
