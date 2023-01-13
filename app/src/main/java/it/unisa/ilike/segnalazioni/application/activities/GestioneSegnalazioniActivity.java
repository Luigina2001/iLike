package it.unisa.ilike.segnalazioni.application.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;

import it.unisa.ilike.R;
import it.unisa.ilike.account.application.AccountImpl;
import it.unisa.ilike.account.application.AccountService;
import it.unisa.ilike.account.application.activities.LoginActivity;
import it.unisa.ilike.account.storage.Account;
import it.unisa.ilike.contenuti.application.activities.VisualizzazioneHomepageActivity;
import it.unisa.ilike.recensioni.storage.RecensioneBean;
import it.unisa.ilike.segnalazioni.application.SegnalazioneImpl;
import it.unisa.ilike.segnalazioni.application.SegnalazioneService;
import it.unisa.ilike.segnalazioni.storage.SegnalazioneBean;
import it.unisa.ilike.segnalazioni.application.exceptions.InvalidMotivazioneException;
import it.unisa.ilike.segnalazioni.application.exceptions.MotivazioneVuotaException;
import it.unisa.ilike.segnalazioni.storage.SegnalazioneDAO;

public class GestioneSegnalazioniActivity extends AppCompatActivity {

    /**
     * Classe interna che consente di creare un nuovo thread per la chiamata al metodo di servizio cancellaRecensione
     * contenuto in SegnalazioneService. Questo è necessario in quanto il metodo in questione richiama metodi
     * della classe RecensioneDAO. In Android non è consentito fare operazioni di accesso
     * alla rete nel main thread; dato che questa activity si trova nel main thread occorre creare
     * questa classe che estende <code>AsyncTask</code> per usufruire dei metodi di cui sopra.
     */
    private class GsonResultCancellaRecensione extends AsyncTask<String, Void, Boolean> {

        Boolean isValidate = true;
        String messaggio = null;

        /**
         * Consente di utilizzare il metodo cancellaRecensione di SegnalazioneService e di memorizzarne
         * l'esito nella variabile di istanza isValidate;
         * @param strings array di stringhe contenente la motivazione di cancellazione
         * @return true se l'operazione è andata a buon fine, false altrimenti
         */
        @Override
        protected Boolean doInBackground(String...  strings) {
            SegnalazioneService segnalazioneService = new SegnalazioneImpl();

            try {
                segnalazioneService.cancellaRecensione(segnalazione, strings[0], account.getGestoreBean());
            } catch (MotivazioneVuotaException e) {
                messaggio = "Inserire una motivazione";
                isValidate=false;
            } catch (InvalidMotivazioneException e) {
                messaggio = "Non puoi scrivere più di 300 caretteri";
                isValidate=false;
            }

            return isValidate;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (isValidate) {
                Intent i = new Intent();
                i.setClass(getApplicationContext(), VisualizzazioneSegnalazioniActivity.class);
                i.putExtra("account", (Serializable) account);
                startActivity(i);
            }else {
                Toast.makeText(GestioneSegnalazioniActivity.this, messaggio, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Restituisce il valore della variabile di istanza isValidate dopo che il metodo doInBackground
         * ha terminato la sua esecuzione
         * @return il valore della variabile d'istanza isValidate
         */
        public Boolean isValidate(){
            while (this.isValidate==null);
            return this.isValidate;
        }

    }


    /**
     * Classe interna che consente di creare un nuovo thread per la chiamata al metodo di servizio rifiutaSegnalazione
     * contenuto in SegnalazioneService. Questo è necessario in quanto il metodo in questione richiama metodi
     * della classe RecensioneDAO. In Android non è consentito fare operazioni di accesso
     * alla rete nel main thread; dato che questa activity si trova nel main thread occorre creare
     * questa classe che estende <code>AsyncTask</code> per usufruire dei metodi di cui sopra.
     */
    private class GsonResultRifiutaSegnalazione extends AsyncTask<Void, Void, Boolean> {

        Boolean isValidate = true;

        /**
         * Consente di utilizzare il metodo rifiutaSegnalazione di SegnalazioneService e di memorizzarne
         * l'esito nella variabile di istanza isValidate;
         * @param v non occorrono argomenti a questo metodo ma AsyncTask richiede di specificare sempre
         *          3 paramtri. In questo caso v rappresenta un segnaposto per gli argomenti mancanti.
         * @return true se l'operazione è andata a buon fine, false altrimenti
         */
        @Override
        protected Boolean doInBackground(Void...  v) {

            SegnalazioneService segnalazioneService = new SegnalazioneImpl();
            isValidate = segnalazioneService.rifiutaSegnalazione(segnalazione, account.getGestoreBean());

            return isValidate;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (isValidate){
                Intent i = new Intent();
                i.setClass(getApplicationContext(), VisualizzazioneSegnalazioniActivity.class);
                i.putExtra("account", (Serializable) account);
                startActivity(i);
            }else{
                Toast.makeText(GestioneSegnalazioniActivity.this, "Rifiuto segnalazione non effettuato", Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Restituisce il valore della variabile di istanza isValidate dopo che il metodo doInBackground
         * ha terminato la sua esecuzione
         * @return il valore della variabile d'istanza isValidate
         */
        public Boolean isValidate(){
            while (this.isValidate==null);
            return this.isValidate;
        }

    }

    private class GsonResultGetSegnalazione extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void...  v) {

            SegnalazioneDAO segnalazioneDAO = new SegnalazioneDAO();
            segnalazione = segnalazioneDAO.doRetrieveByIdSegnalazione(idSegnalazione);
            recensione = segnalazione.getRecensione();

            return null;
        }

        protected void onPostExecute(Boolean a) {
            super.onPostExecute(a);
            TextView recensioneText = findViewById(R.id.textViewTestoRecensione);
            recensioneText.setText(recensione.getTesto());

            TextView segnalazioneText = findViewById(R.id.textViewSegnalazione);
            segnalazioneText.setText(segnalazione.getMotivazione());
        }
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione_segnalazione);

        motivazioneCancellazione= findViewById(R.id.motivazioneCancellazione);
        cancellaRecensioneButton= findViewById(R.id.cancellaRecensioneButton);

        Intent i = getIntent();
        setReturnIntent();

        account = (Account) getIntent().getExtras().getSerializable("account");
        idSegnalazione = Integer.parseInt(getIntent().getExtras().getString("idSegnalazione"));

        GsonResultGetSegnalazione g= (GsonResultGetSegnalazione) new GsonResultGetSegnalazione().execute(new Void[0]);
    }


    private void setReturnIntent() {
        Intent data = new Intent();
        setResult(RESULT_OK,data);
    }

    public void onClickRifiutaSegnalazione(View view) {

        boolean isValidate;

        GsonResultRifiutaSegnalazione g= (GsonResultRifiutaSegnalazione)
                new GsonResultRifiutaSegnalazione().execute(new Void[0]);
    }

    public void onClickAccettaSegnalazione(View view) {

        motivazioneCancellazione.setVisibility(View.VISIBLE);
        cancellaRecensioneButton.setVisibility(View.VISIBLE);

        String motivazione = motivazioneCancellazione.toString();
        String[] s= {motivazione};

        GsonResultCancellaRecensione g= (GsonResultCancellaRecensione) new GsonResultCancellaRecensione().execute(s);
        boolean isValidate= g.isValidate();

        if (isValidate) {
            Intent i = new Intent();
            i.setClass(getApplicationContext(), VisualizzazioneHomepageActivity.class);
            i.putExtra("account", (Serializable) account);
            startActivity(i);
        }
    }


    public void onClickCancellaRecensione (View v){

        String motivazione = motivazioneCancellazione.toString();
        String[] s= {motivazione};

        GsonResultCancellaRecensione g= (GsonResultCancellaRecensione) new GsonResultCancellaRecensione().execute(s);
        boolean isValidate= g.isValidate();


    }

    public void onClickVisualizzaSegnalazioni(View view) {
        Intent i = new Intent();
        i.setClass(getApplicationContext(), VisualizzazioneSegnalazioniActivity.class);
        i.putExtra("account", (Serializable) account);
        startActivity(i);
    }


    public void onClickHomepage(View view) {
        Intent i = new Intent();
        i.setClass(getApplicationContext(), VisualizzazioneHomepageActivity.class);
        i.putExtra("account", (Serializable) account);
        startActivity(i);
    }

    public void onClickLogout(View view) {
        AccountService accountService = new AccountImpl();
        account = accountService.logout(account.getGestoreBean());
        Intent i = new Intent();
        i.setClass(getApplicationContext(), VisualizzazioneHomepageActivity.class);
        i.putExtra("account", (Serializable) account);
        startActivity(i);
    }

    private RecensioneBean recensione;
    private SegnalazioneBean segnalazione;
    private Account account;
    private EditText motivazioneCancellazione;
    private Button cancellaRecensioneButton;
    private int idSegnalazione;
}
