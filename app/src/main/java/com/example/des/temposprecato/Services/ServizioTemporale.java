package com.example.des.temposprecato.Services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.example.des.temposprecato.R;
import com.example.des.temposprecato.Activities.Main_Activity;
import com.example.des.temposprecato.AltreClassi.RecTempo;
import com.example.des.temposprecato.Database.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Notification.GROUP_ALERT_SUMMARY;

public class ServizioTemporale extends Service //in teoria questo servizio dovrebbe sempre girare in background...
{
    public static int[] TOTseconds = {0};

    public final boolean [] primoAvvio = {true};    //fa partire il servizio solo quando l'app si apre la prima volta

    Date currentTime;

    private static final int NOTIF_ID_TEMPO = 666;
    private static final String NOTIF_CHANNEL_ID_TEMPO = "777";
    private static final int NOTIF_ID_ACHIEVEMENT = 111;
    private static final String NOTIF_CHANNEL_ID_ACHIEVEMENT = "222";


    DateFormat tempoFormat = new SimpleDateFormat("HH:mm:ss");
    DateFormat secondiFormat = new SimpleDateFormat("ss");
    DateFormat giorniFormat = new SimpleDateFormat("dd/MM/yyyy");

    DatabaseHelper MyDB = new DatabaseHelper(this);

    //----------------------------------------------------------------------------------------------------------------------------------------

    public class LocalBinder extends Binder
    {
        public ServizioTemporale getService()
        {
            return ServizioTemporale.this;
        }
    }

    private IBinder binder = new LocalBinder();

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;  //Questa è la creazione di un collegamento tra l'activity e il servizio ogni volta che l'applicazione si accende da zero
    }

    //----------------------------------------------------------------------------------------------------------------------------------------


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(primoAvvio[0] == true)
        {
            startForeground();

            primoAvvio[0] = false;

            creazioneCanaleDiNotifica(NOTIF_CHANNEL_ID_TEMPO);
            creazioneCanaleDiNotifica(NOTIF_CHANNEL_ID_ACHIEVEMENT);

            Timer timer = new Timer();

            TOTseconds[0] = MyDB.IniziaServizioConDatiGiusti();
            MyDB.AggiornaTempo(0);

            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {

                    if(tempoFormat.format(Calendar.getInstance().getTime()).compareTo("00:00:00") == 0)   //se è mezzanotte azzero il tutto
                    {
                        TOTseconds[0] = 0;
                    }

                    if(SchermoAcceso())
                    {

                        if(IlMomentoDiSalvare()) //Salvataggio dati
                        {
                            MyDB.AggiornaTempo(5);
                        }

                        ControllaAchievement();     //Notifiche per farti sentire in colpa

                        IlTempoProsegue();
                    }

                }

            }, 0, 1000); // Viene richiamato ogni secondo


            return ServizioTemporale.START_STICKY;  //Quando il servizio viene interrotto per necessità di risorse, il sistema lo resuscita appena possibile
        }
        else
        {
            return 0;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        startService(new Intent(this, ServizioTemporale.class));    //Quando viene ucciso si richiama
    }


    //----------------------------------------------------------------------------------------------------------------------------------------

    boolean SchermoAcceso ()
    {
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);

        if(pm.isInteractive())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void startForeground()
    {
        Intent notificationIntent = new Intent(this, Main_Activity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID_TEMPO, new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID_TEMPO)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Tempo sprecato")
                .setContentText("Service is running foreground")
                .setContentIntent(pendingIntent)
                .build());
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void creazioneCanaleDiNotifica(String numCanale)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Canale";
            String description = "Canale per i perditempo";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(numCanale, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void creazioneNotifica(String channelID, int notificationID, String titolo, String testo,  boolean siCancellaSulClick, boolean nonEliminabile, int priorita, boolean suoniVibraz, boolean testoGrande)
    {
        Intent intent = new Intent(this, Main_Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.temposprecatoicon)
                .setContentTitle(titolo)
                .setPriority(priorita) //MAX = 2 //MIN = -2
                .setContentIntent(pendingIntent)
                .setAutoCancel(siCancellaSulClick)
                .setOngoing(nonEliminabile);

        if(testoGrande == true)
        {
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(testo));
        }
        else
        {
            mBuilder.setContentText(testo);
        }

        if(suoniVibraz == false)
        {
            mBuilder
                .setVibrate(new long[]{0L}) //no vibrazione
                .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                .setGroup("My group")
                .setGroupSummary(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        }
        else
        {
            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI); //prende le impostazioni di default per quanto riguarda le notifiche

        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, mBuilder.build());
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    void CreazioneToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    public static int[] TornaTempo()
    {
        return TOTseconds;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private boolean IlMomentoDiSalvare()
    {
        if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("00") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("05") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("10") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("15") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("20") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("25") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("30") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("35") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("40") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("45") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("50") == 0)
            return true;

        else if(secondiFormat.format(Calendar.getInstance().getTime()).compareTo("55") == 0)
            return true;

        return false;

    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void ControllaAchievement()
    {
        if(TOTseconds[0] == 3600)   //1 ora
        {
            creazioneNotifica(NOTIF_CHANNEL_ID_ACHIEVEMENT, NOTIF_ID_ACHIEVEMENT,"Achievement unlocked ☹", "È passata solo 1 ora, sei ancora in tempo per spegnere", true, false, 1, true, true);
        }
        else if(TOTseconds[0] == 3600*2) //2 ore
        {
            creazioneNotifica(NOTIF_CHANNEL_ID_ACHIEVEMENT, NOTIF_ID_ACHIEVEMENT,"Achievement unlocked ☹", "Magari ti sei perso un paio di ore sui social, ora però vai a fare qualcos'altro", true, false, 1, true, true);
        }
        else if(TOTseconds[0] == 3600*3) //3 ore
        {
            creazioneNotifica(NOTIF_CHANNEL_ID_ACHIEVEMENT, NOTIF_ID_ACHIEVEMENT,"Achievement unlocked ☹", "3 ore con il tuo telefono... ricorda che non è lui il tuo vero migliore amico", true, false, 1, true, true);
        }
        else if(TOTseconds[0] == 3600*4) //4 ore
        {
            creazioneNotifica(NOTIF_CHANNEL_ID_ACHIEVEMENT, NOTIF_ID_ACHIEVEMENT,"Achievement unlocked ☹", "4 ore? Ti invidio, non saprei cosa fare per 4 ore sul mio telefono...", true, false, 1, true, true);
        }
        else if(TOTseconds[0] == 3600*5) //5 ore
        {
            creazioneNotifica(NOTIF_CHANNEL_ID_ACHIEVEMENT, NOTIF_ID_ACHIEVEMENT,"Achievement unlocked ☹", "Ok, dalle 5 ore diventa una cosa seria...", true, false, 1, true, true);
        }
        else if(TOTseconds[0] == 3600*8) //8 ore
        {
            creazioneNotifica(NOTIF_CHANNEL_ID_ACHIEVEMENT, NOTIF_ID_ACHIEVEMENT,"Achievement unlocked ☹", "Se si togliessero le 16 ore giornalmente investite a lavorare e a dormire, ti rimarrebbero solo 8 ore per te stesso e tu le hai appena sprecate sul telefono", true, false, 1, true, true);
        }
        else if(TOTseconds[0] == (3600*24) - 5) //24 ore, ACHIEVEMENT SUPREMO
        {
            creazioneNotifica(NOTIF_CHANNEL_ID_ACHIEVEMENT, NOTIF_ID_ACHIEVEMENT,"Achievement unlocked XDDD", "Vorrei essere lì per stringerti la mano, HAI APPENA PLATINATO QUESTO MINIGIOCO!! (Non) Dovresti essere fiero di te.", true, false, 1, true, true);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void IlTempoProsegue()
    {
        TOTseconds[0]++;    //Aggiorna il tempo sprecato

        creazioneNotifica(NOTIF_CHANNEL_ID_TEMPO, NOTIF_ID_TEMPO,"Guarda quanto tempo hai sprecato fino ad ora...", RecTempo.TrasformaInTempo(TOTseconds[0]), false, true, 2, false, false);    //Crea una nuova notifica con i secondi aggiornati
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

}
