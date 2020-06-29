package com.example.des.temposprecato.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.des.temposprecato.AltreClassi.RecTempo;
import com.example.des.temposprecato.R;
import com.example.des.temposprecato.Database.DatabaseHelper;

public class RecordFragment extends Fragment
{

    DatabaseHelper MyDB;

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onStart()
    {
        super.onStart();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        MyDB = new DatabaseHelper(getActivity());
        StampaRecord();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void StampaRecord()
    {
        RecTempo recRecord = MyDB.TornaRecord();

        TextView txtGiornoRecord = getActivity().findViewById(R.id.txtGiornoRecord);
        TextView txtTempoRecord = getActivity().findViewById(R.id.txtMedia);

        if(recRecord.GetData().compareTo("") == 0)  //se la data presa è vuota, allora stampa un "-"
        {
            txtGiornoRecord.setTextSize(30);
            txtTempoRecord.setTextSize(80);
            txtGiornoRecord.setText("Record ancora da stabilire!");
            txtTempoRecord.setText("☹");
        }
        else
        {
            txtGiornoRecord.setTextSize(50);
            txtTempoRecord.setTextSize(50);
            txtGiornoRecord.setText(recRecord.GetData());
            txtTempoRecord.setText(recRecord.GetSec());
        }

    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    void CreazioneToast(String text)
    {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

}
