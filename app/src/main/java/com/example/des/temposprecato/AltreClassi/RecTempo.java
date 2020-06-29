package com.example.des.temposprecato.AltreClassi;

public class RecTempo
{
    int IDnelDB;            // ID della registrazione nel DB
    String data;            // è una stringa perchè non ho trovato modo di inserire in mysqlite un valore che rappresenti una data
    int secondiPassati;     // Quanti secondi sono stati contati

    //----------------------------------------------------------------------------------------------------------------------------------------

    public String GetData() //Get per la data
    {
        return data;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public String GetSec() //Get per i secondi
    {
        return RecTempo.TrasformaInTempo(secondiPassati);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public RecTempo(int id, String d, int s) //costruttore principale
    {
        IDnelDB = id;
        data = d;
        secondiPassati = s;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public RecTempo(String d, int s) //costruttore parziale
    {
        data = d;
        secondiPassati = s;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public static String TrasformaInTempo(int secondi)  //Trasforma un determinato tempo espresso in secondi in un formato "ore : minuti : secondi"
    {
        int ore = 0;
        int minuti = 0;

        while(secondi - 3600 >= 0)
        {
            ore++;
            secondi -= 3600;
        }

        while(secondi - 60 >= 0)
        {
            minuti ++;
            secondi -= 60;
        }

        return ore + " : " + minuti + " : " + secondi;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

}
