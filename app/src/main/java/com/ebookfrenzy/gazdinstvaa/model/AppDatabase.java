package com.ebookfrenzy.gazdinstvaa.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Company.class,Comment.class},version = 2,exportSchema = false)
@TypeConverters({com.ebookfrenzy.gazdinstvaa.model.TypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static volatile AppDatabase INSTANCE;
    public abstract CompanyDao companyDao();
    public abstract CommentDao commentDao();

    public static AppDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (AppDatabase.class){
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "app_data_base")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
