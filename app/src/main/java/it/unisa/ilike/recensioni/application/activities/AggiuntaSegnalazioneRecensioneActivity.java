package it.unisa.ilike.recensioni.application.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unisa.ilike.R;
import it.unisa.ilike.account.application.activities.LoginActivity;
import it.unisa.ilike.account.application.activities.VisualizzazioneProfiloPersonaleActivity;
import it.unisa.ilike.account.storage.Account;
import it.unisa.ilike.contenuti.application.activities.VisualizzazioneHomepageActivity;
import it.unisa.ilike.contenuti.storage.ContenutoBean;
import it.unisa.ilike.liste.storage.ListaBean;
import it.unisa.ilike.moduloFIA.ActivityChatbot;
import it.unisa.ilike.recensioni.application.RecensioneImpl;
import it.unisa.ilike.recensioni.application.RecensioneService;
import it.unisa.ilike.recensioni.application.exceptions.InvalidMotivazioneException;
import it.unisa.ilike.recensioni.application.exceptions.InvalidTipoException;
import it.unisa.ilike.recensioni.application.exceptions.MotivazioneVuotaException;
import it.unisa.ilike.segnalazioni.storage.SegnalazioneBean;
import it.unisa.ilike.utils.InternetConnection;

/**
 * Questa classe gestisce il flusso di interazioni tra l'utente e il sistema. Essa permette di effettuare
 * l'aggiunta di una segnalazione ad una recensione.
 * @author Simona Lo Conte
 * @version 0.1
 */
public class AggiuntaSegnalazioneRecensioneActivity extends AppCompatActivity {


    private class GsonResultValidate extends AsyncTask<String, Void, Boolean> {

        Boolean isValidate = true;
        String messaggio = null;


        @Override
        protected Boolean doInBackground(String... string) {
            RecensioneService recensioneService = new RecensioneImpl();
            int tipo= Integer.parseInt(string[0]);

            try {

                if(recensioneService.aggiungiSegnalazione(tipo, string[1], segnalazione.getRecensione(), account.getIscrittoBean())) {
                    this.messaggio = "Segnalazione effettuata";
                }
                else {
                    this.isValidate = false;
                    this.messaggio = "Si è verificato un errore, riprovare";
                }

            } catch (InvalidTipoException e) {
                messaggio = "Tipo segnalazione non corretto";
                isValidate = false;
                e.printStackTrace();
            } catch (MotivazioneVuotaException e) {
                messaggio = "Inserire almeno 1 carattere per la motivazione";
                isValidate = false;
                e.printStackTrace();
            } catch (InvalidMotivazioneException e) {
                messaggio = "Puoi inserire al massimo 500 caratteri";
                isValidate = false;
                e.printStackTrace();
            }
            return isValidate;
        }

        protected void onPostExecute(Boolean b) {
            if(this.isValidate){
                onBackPressed();
                /*Intent i = new Intent();
                i.setClass(getApplicationContext(), VisualizzazioneDettagliataContenutoActivity.class);
                i.putExtra("account", account);
                i.putExtra("idContenuto", contenuto.getId());
                startActivity(i);*/
            }else {
                Toast toast = Toast.makeText(getApplicationContext(), this.messaggio, Toast.LENGTH_LONG);
                toast.show();
            }
        }

        public Boolean isValidate(){
            while (this.isValidate==null);
            return this.isValidate;
        }
    }


    private class GsonResultGetListe extends AsyncTask<Void, Void, ArrayList<ListaBean>> {

        private Boolean isOk= true;

        @Override
        protected ArrayList<ListaBean> doInBackground(Void... voids) {
            if (account.getIscrittoBean()!=null){
                ArrayList<ListaBean> listeIscritto = (ArrayList<ListaBean>) account.getIscrittoBean().getListe();
                if (listeIscritto==null){
                    isOk= false;
                }
                else
                    return listeIscritto;
            }
            else
                isOk=false;

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ListaBean> listeIscritto) {
            Log.d("chatBot", "sono in onPostExecute");

            if (isOk){
                ArrayList<ContenutoBean> list= new ArrayList<>();
                for (ListaBean l: listeIscritto){
                    List<ContenutoBean> contenutiLista= l.getContenuti();
                    for (ContenutoBean c: contenutiLista){
                        list.add(c);
                    }
                }
                ContenutoBean[] array = list.toArray(new ContenutoBean[list.size()]);
                Intent i = new Intent();
                i.setClass(getApplicationContext(), ActivityChatbot.class);
                i.putExtra("contenuti", array);
                i.putExtra("account", account);
                startActivity(i);
            }
            else{
                Toast.makeText(AggiuntaSegnalazioneRecensioneActivity.this, "Devi essere loggato ed avere almeno una lista per effettuare questa " +
                        "operazione!", Toast.LENGTH_LONG).show();
            }

        }
    }


    /**
     * Primo metodo chiamato alla creazione dell'activity, per le inizializzazioni di avvio necessarie.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiunta_segnalazione_recensione);

        segnalazione = (SegnalazioneBean) getIntent().getExtras().getSerializable("segnalazione");
        account = (Account) getIntent().getExtras().getSerializable("account");
        contenuto = (ContenutoBean) getIntent().getExtras().getSerializable("contenuto");

        boolean checkconnessione;
        if (InternetConnection.haveInternetConnection(AggiuntaSegnalazioneRecensioneActivity.this)) {
            checkconnessione = true;
            Log.d("connessione", "Connessione presente!");
        } else {
            checkconnessione = false;
            Log.d("connessione", "Connessione assente!");
        }

        if (checkconnessione) {
            try {

                TextView tipoSegnalazioneTextView = findViewById(R.id.tipoSegnalazione);
                String spoiler = "spoiler allert";
                String altre = "altre segnalazioni";
                if (segnalazione.getTipo() == 1)
                    tipoSegnalazioneTextView.setText(spoiler);
                else
                    tipoSegnalazioneTextView.setText(altre);
            }catch(NetworkOnMainThreadException n){
                Toast.makeText(getApplicationContext(), "Verifica la tua connessione ad internet", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Connessione Internet assente!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Questo metodo permette di aggiungere una segnalazione alla recensione.
     * @param view
     */
    public void onClickInviaSegnalazione(View view) {

        boolean checkconnessione;
        if (InternetConnection.haveInternetConnection(AggiuntaSegnalazioneRecensioneActivity.this)) {
            checkconnessione = true;
            Log.d("connessione", "Connessione presente!");
        } else {
            checkconnessione = false;
            Log.d("connessione", "Connessione assente!");
        }

        if (checkconnessione) {
            try {
                TextView motivazioneSegnalazioneTextView = findViewById(R.id.motivazioneSegnalazione);
                String motivazioneSegnalazione = String.valueOf(motivazioneSegnalazioneTextView.getText());
                String[] s = new String[2];

                s[0] = String.valueOf(segnalazione.getTipo());
                s[1] = motivazioneSegnalazione;

                GsonResultValidate g = (GsonResultValidate) new GsonResultValidate().execute(s);
            }catch(NetworkOnMainThreadException n){
                Toast.makeText(getApplicationContext(), "Verifica la tua connessione ad internet", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Connessione Internet assente!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Questo metodo permette all'utente che non ha effettuato l'accesso di andare alla pagina di login di iLike
     * (da cui poter eventualmente andare alla pagina di registrazione se non si è registrati alla piattaforma).
     * All'iscritto che ha effettuato l'accesso, essa permette di andare alla pagina di visualizzazione del proprio
     * profilo personale.
     * @param v
     */
    public void onClickProfilo(View v){
        if(account.isIscritto()) {
            Intent i = new Intent();
            i.setClass(getApplicationContext(), VisualizzazioneProfiloPersonaleActivity.class);
            i.putExtra("account", (Serializable) account);
            startActivity(i);
        }else {
            Intent i = new Intent();
            i.setClass(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }
    }

    /**
     * Questo metodo permette di passare alla homepage di iLike.
     * @param v
     */
    public void onClickHomepage(View v){
        Intent i = new Intent();
        i.setClass(getApplicationContext(), VisualizzazioneHomepageActivity.class);
        i.putExtra("account", (Serializable) account);
        startActivity(i);
    }

    public void onClickChatBot (View v){
        GsonResultGetListe g= (GsonResultGetListe) new GsonResultGetListe().execute(new Void[0]);
    }

    private Account account;
    private SegnalazioneBean segnalazione;
    private ContenutoBean contenuto;
}