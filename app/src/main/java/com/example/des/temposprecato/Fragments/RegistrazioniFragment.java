package com.example.des.temposprecato.Fragments;


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.Toast;

import com.example.des.temposprecato.R;
import com.example.des.temposprecato.AltreClassi.RecTempo;
import com.example.des.temposprecato.Database.DatabaseHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistrazioniFragment extends Fragment
{

    DatabaseHelper MyDB;

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_registrazioni, container, false);
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
        AggiornaListView();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void AggiornaListView()
    {
        List<RecTempo> list = MyDB.PrendiTuttiTempi();
        LinkedHashMap<String, String> DateTempi = new LinkedHashMap<>();

        for(int i = 1 ; i < list.size(); i++)  //salta il primo record (quello odierno)
        {
            DateTempi.put(list.get(i).GetData(), (list.get(i).GetSec()));
        }

        if(list.size() - 2 < 0)
        {
            CreazioneToast("Ripassa domani per avere la tua prima registrazione!");
        }

        ListView lstvVistaTempi = getActivity().findViewById(R.id.lstvVistaTempi);

        List<LinkedHashMap<String, String>> listItems = new ArrayList<>();  //per stampare sulla list view
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), listItems, R.layout.list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.txtDataLinea, R.id.txtTempoLinea});


        Iterator it = DateTempi.entrySet().iterator();
        while (it.hasNext())
        {
            LinkedHashMap<String, String> resultsMap = new LinkedHashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        lstvVistaTempi.setAdapter(adapter);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    void CreazioneToast(String text)
    {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

}
