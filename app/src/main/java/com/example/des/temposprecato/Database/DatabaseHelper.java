package com.example.des.temposprecato.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.des.temposprecato.AltreClassi.RecTempo;
import com.example.des.temposprecato.Services.ServizioTemporale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper
{
    //Meglio usare dei tag per non rischiare errori inutili causati da nomi sbagliati
    private static final String TBL_TEMPI = "tblTempi";
    private static final String TAG_DATA = "Data";
    private static final String TAG_SECONDI = "Secondi";

    private static final String DATABASE_NAME = "dbTempoSprecato.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABELLA_TEMPI = "CREATE TABLE " + TBL_TEMPI + " (IDtempo integer primary key autoincrement, Data text not null, " + TAG_SECONDI + " integer not null);";   //ID, Data, Secondi totali spesi in quella data
   // private static final String CREATE_TABELLA_ACHIEVEMENT = "CREATE TABLE tblAchievement (IDachievement integer primary key autoincrement, Ore integer not null, Testo text not null, Sbloccato integer default 0, DataSblocco text);";   //ID, Ore per sbloccarlo, Testo che viene visto quando sbloccato, Sbloccato o meno, Data del primo sblocco dell'achievement

    DateFormat giorniFormat = new SimpleDateFormat("dd/MM/yyyy");
    //----------------------------------------------------------------------------------------------------------------------------------------

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABELLA_TEMPI); //Creo la tabella tempi
       // database.execSQL(CREATE_TABELLA_ACHIEVEMENT);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion )
    {

    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public boolean AggiungiTempo(String d, int s)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TAG_DATA, d);
        values.put(TAG_SECONDI,s);

        long result = db.insert(TBL_TEMPI, null, values);

        if(result == -1)    //inserimento fallito
        {
            //db.close();
            return false;
        }
        else    //inserito correttamente
        {
            //db.close();
            return true;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public List<RecTempo> PrendiTuttiTempi()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT IDtempo, " + TAG_DATA + ", " + TAG_SECONDI + " FROM " + TBL_TEMPI + " ORDER BY IDtempo DESC";
        Cursor cursor = db.rawQuery(query, null);

        List<RecTempo> lstTempi = new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do {
                int ID = cursor.getInt(0);
                String data = cursor.getString(1);
                int sec = cursor.getInt(2);

                RecTempo r = new RecTempo(ID, data, sec);

                lstTempi.add(r);

            } while (cursor.moveToNext());
        }

        //db.close();

        return lstTempi;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public void ResettaDB ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TBL_TEMPI);
        onCreate(db);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public void AggiornaTempo (int tempoDaAggiungere)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String dataGiaInserita = "SELECT " + TAG_DATA + " FROM " + TBL_TEMPI + " WHERE Data = '" + giorniFormat.format(Calendar.getInstance().getTime()) + "' ";  //torna la data odierna se è già stata aggiunta
        Cursor cursor = db.rawQuery(dataGiaInserita, null);

        if (cursor.moveToFirst())   //se la query ha prodotto qualcosa, aggiorna quel tempo
        {
            String aggiornaRecordOdierno = "UPDATE " + TBL_TEMPI + " SET " + TAG_SECONDI + " = " + TAG_SECONDI + " + " + tempoDaAggiungere + " WHERE " + TAG_DATA + " = '" + giorniFormat.format(Calendar.getInstance().getTime()) + "';";  //modifica il valore dei secondi nella data odierna
            db.execSQL(aggiornaRecordOdierno);
        }
        else    //se no, crea il primo record odierno
        {
            ServizioTemporale.TOTseconds[0] = 0;
            AggiungiTempo(giorniFormat.format(Calendar.getInstance().getTime()), 0);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public int IniziaServizioConDatiGiusti()    //quando il servizio viene avviato ma c'è già una registrazione per la giornata odierna, allora il servizio dovrebbe partire a contare dal punto salvato
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String dataGiaInserita = "SELECT " + TAG_DATA + " FROM " + TBL_TEMPI + " WHERE " + TAG_DATA + " = '" + giorniFormat.format(Calendar.getInstance().getTime()) + "' ";  //torna la data odierna se è già stata aggiunta
        Cursor cursor = db.rawQuery(dataGiaInserita,  null);

        if (cursor.moveToFirst())   //se la query ha prodotto qualcosa, allora vuol dire che c'era già un salvataggio odierno
        {
            String prendiISecondi = "SELECT " + TAG_SECONDI + " FROM " + TBL_TEMPI + " WHERE " + TAG_DATA + " = '" + giorniFormat.format(Calendar.getInstance().getTime()) + "'";
            Cursor c = db.rawQuery(prendiISecondi,  null);
            c.moveToFirst();
            int sec =  c.getInt(0);
            return sec;
        }
        else
        {
            //db.close();
            return 0;
        }

    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public RecTempo TornaRecord()   //Ritorna la registrazione con il tempo sprecato maggiore
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String recordTempo = "SELECT "+TAG_DATA+", MAX("+TAG_SECONDI+") FROM "+TBL_TEMPI+" WHERE " + TAG_DATA + " <> '" + giorniFormat.format(Calendar.getInstance().getTime()) + "' ";  //torna la data odierna se è già stata aggiunta
        Cursor cursor = db.rawQuery(recordTempo,  null);

        RecTempo rec;

        cursor.moveToFirst();

        String dataRecord = cursor.getString(0);

        if(dataRecord == null)    //se la query non torna nulla
        {
            rec = new RecTempo("", 0);
        }
        else
        {
            int sec =  cursor.getInt(1);
            rec = new RecTempo(dataRecord, sec);
        }

        return rec;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public int TornaMedia()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String media = "SELECT AVG("+TAG_SECONDI+") FROM "+TBL_TEMPI+"; ";  //torna la data odierna se è già stata aggiunta
        Cursor cursor = db.rawQuery(media,  null);

        if (cursor.moveToFirst())   //se la query ha prodotto qualcosa, allora vuol dire che c'era già un salvataggio odierno
        {
            return cursor.getInt(0);
        }
        else
        {
            //db.close();
            return 0;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------
/*
    public String AchievementUnlocked(int nOre) //dovrei salvarmi gli achievement sbloccati
    {
        SQLiteDatabase db = this.getWritableDatabase();

    }*/

    //----------------------------------------------------------------------------------------------------------------------------------------

}