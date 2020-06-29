package com.example.des.temposprecato.Activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.des.temposprecato.Fragments.MediaFragment;
import com.example.des.temposprecato.Fragments.RecordFragment;
import com.example.des.temposprecato.Fragments.RegistrazioniFragment;
import com.example.des.temposprecato.Fragments.TempoAttualeFragment;
import com.example.des.temposprecato.R;


public class Main_Activity extends AppCompatActivity
{

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new TempoAttualeFragment()).commit();   //codice per la bottom navigation
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
                {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) //vedo quale tasto della bottom navigation è stato premuto e reagisco di conseguenza
                    {
                        case R.id.nav_tempoNow:
                            selectedFragment = new TempoAttualeFragment();
                            break;

                        case R.id.nav_registrazioni:
                            selectedFragment = new RegistrazioniFragment();
                            break;

                        case R.id.nav_media:
                            selectedFragment = new MediaFragment();
                            break;

                        case R.id.nav_recordTime:
                            selectedFragment = new RecordFragment();
                            break;

                    }

                    if(selectedFragment != null)    //se è stato premuto un tasto che è già stato programmato, allora prosegui
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();

                        return true;
                    }
                    else
                    {
                        return false;
                    }

                }
            };

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------


}

