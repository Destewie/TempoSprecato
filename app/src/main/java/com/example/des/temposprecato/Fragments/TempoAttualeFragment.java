package com.example.des.temposprecato.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;


import com.example.des.temposprecato.R;
import com.example.des.temposprecato.AltreClassi.RecTempo;

import com.example.des.temposprecato.Database.DatabaseHelper;
import com.example.des.temposprecato.Services.ServizioTemporale;

import java.util.Timer;
import java.util.TimerTask;

public class TempoAttualeFragment extends Fragment
{
    ServizioTemporale ST;
    boolean boundAttivo = false;

    String stringaTempo;

    Boolean continua = true;

    /*
    DateFormat date = new SimpleDateFormat("HH:mm:ss");
    DateFormat giorniFormat = new SimpleDateFormat("dd/MM/yyyy");
    */

    DatabaseHelper MyDB = new DatabaseHelper(getContext());

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tempo_attuale, container, false);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onStart()
    {
        super.onStart();

        getActivity().startService(new Intent(getActivity(), ServizioTemporale.class));    //del

        // Bind to LocalService
        Intent intent = new Intent(getActivity(), ServizioTemporale.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        MyDB = new DatabaseHelper(getActivity());


        //--------------------------------------------------------   BOTTONI DA DEBUG PER DB (inizio codice)

/*
        Button btnDEL = getActivity().findViewById(R.id.btnDEL);
        btnDEL.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                MyDB.ResettaDB();
            }
        });


        Button btnIN = getActivity().findViewById(R.id.btnIN);
        btnIN.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                MyDB.ResettaDB();

                MyDB.AggiungiTempo("26/1/2019", 4210);
                MyDB.AggiungiTempo("27/01/2019", 6245);
                MyDB.AggiungiTempo("28/01/2019", 1743);
                MyDB.AggiungiTempo("29/01/2019", 5400);
                MyDB.AggiungiTempo("30/01/2019", 5345);
                MyDB.AggiungiTempo("31/01/2019", 2001);
            }
        });
*/

        //--------------------------------------------------------   BOTTONI DA DEBUG PER DB (fine codice)

    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onStop()
    {
        continua = false;
        getActivity().unbindService(mConnection);
        boundAttivo = false;
        super.onStop();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServizioTemporale.LocalBinder binder = (ServizioTemporale.LocalBinder) service;
            ST = binder.getService();
            boundAttivo = true;

            int[] tempo = ServizioTemporale.TornaTempo();
            continua = true;
            AggiornaTimer(tempo);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            boundAttivo = false;
        }
    };

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void AggiornaTimer(final int[] s)       //Non fa altro che stampare ogni secondo il tempo sull'activity
    {
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                if(continua)
                {
                    stringaTempo = RecTempo.TrasformaInTempo(s[0]);
                    mHandler.obtainMessage(1).sendToTarget();
                }
                else
                {
                    timer.cancel();
                    timer.purge();
                }

            }
        }, 0, 1000);

    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private Handler mHandler = new Handler() //Stampa su una TextView il tempo (serve da tramite tra un thread a caso e il UIthread che governa l'activity che ti interessa (che in questo caso è l'activity principale))
    {
        public void handleMessage(Message msg)
        {
            final TextView txtTempo = getActivity().findViewById(R.id.txtTempo);
            txtTempo.setText(stringaTempo);
        }
    };

    //----------------------------------------------------------------------------------------------------------------------------------------

}
