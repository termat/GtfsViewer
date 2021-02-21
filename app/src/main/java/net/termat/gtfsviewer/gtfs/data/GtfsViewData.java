package net.termat.gtfsviewer.gtfs.data;

import android.content.Context;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class GtfsViewData {
    private static GtfsViewData db=null;
    private GtfsViewDB gtfsdb;

    public static GtfsViewData getInstance(Context context){
        if(db==null)db=new GtfsViewData(context);
        return db;
    }

    private GtfsViewData(Context context){
        gtfsdb = Room.databaseBuilder(context, GtfsViewDB.class, "gtfsrt")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .addMigrations(new Migration(1, 2){
                    @Override
                    public void migrate(SupportSQLiteDatabase database){}
                })
                .build();
    }

    public void add(GtfsView h){
        gtfsdb.getDao().insert(h);
    }

    public void delete(GtfsView h){
        gtfsdb.getDao().delete(h);
    }

    public void update(GtfsView h){gtfsdb.getDao().update(h);}

    public GtfsViewDao getDao(){
        return gtfsdb.getDao();
    }

    public String getDataId(){
        long m=0;
        for(GtfsView g : gtfsdb.getDao().findAll()){
          m=Math.max(m,g.id);
        }
        return Long.toString(m+1);
    }
}
