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
import com.example.des.temposprecato.Database.DatabaseHelper;
import com.example.des.temposprecato.R;

public class MediaFragment extends Fragment
{

    DatabaseHelper MyDB;

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_media, container, false);
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
        StampaMedia();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void StampaMedia()
    {
        int media = MyDB.TornaMedia();

        TextView txtMedia = getActivity().findViewById(R.id.txtMedia);

        String mediaDaStampare = RecTempo.TrasformaInTempo(media);
        txtMedia.setText(mediaDaStampare);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    void CreazioneToast(String text)
    {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

}
