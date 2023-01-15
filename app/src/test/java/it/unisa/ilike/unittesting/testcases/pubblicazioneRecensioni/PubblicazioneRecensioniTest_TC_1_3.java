package it.unisa.ilike.unittesting.testcases.pubblicazioneRecensioni;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.*;
import it.unisa.ilike.account.storage.IscrittoBean;
import it.unisa.ilike.account.storage.IscrittoProxyBean;
import it.unisa.ilike.contenuti.storage.ContenutoBean;
import it.unisa.ilike.contenuti.storage.FilmBean;
import it.unisa.ilike.contenuti.storage.SerieTVBean;
import it.unisa.ilike.recensioni.application.RecensioneImpl;
import it.unisa.ilike.recensioni.application.RecensioneService;
import it.unisa.ilike.recensioni.application.exceptions.InvalidMotivazioneException;
import it.unisa.ilike.recensioni.application.exceptions.InvalidTestoException;
import it.unisa.ilike.recensioni.application.exceptions.TestoTroppoBreveException;
import it.unisa.ilike.recensioni.application.exceptions.ValutazioneException;
import it.unisa.ilike.recensioni.storage.RecensioneBean;
import it.unisa.ilike.recensioni.storage.RecensioneDAO;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;


/**
 * Implementa il testing di unità per TC_1_3 (Pubblicazione recensioni)
 * @author Francesco Giorgione
 */
@RunWith(Parameterized.class)
public class PubblicazioneRecensioniTest_TC_1_3 {
    private IscrittoBean iscritto;
    private ContenutoBean contenuto;
    private String testo;
    private int valutazione;
    private RecensioneDAO mockRecensioneDAO;
    private RecensioneService recensioneService;


    private static IscrittoBean getIscritto() {
        return new IscrittoProxyBean("testiscritto1@ilike.it", null, null, null, null, null);
    }

    private static ContenutoBean getContenuto() {
        ContenutoBean contenuto = new SerieTVBean();
        contenuto.setId(1);
        return contenuto;
    }

    private static RecensioneBean getRecensione() {
        RecensioneBean recensione = new RecensioneBean();
        recensione.setId(-1);
        recensione.setTesto("testo di prova");
        recensione.setData(new Date());
        recensione.setCancellata(false);

        IscrittoBean iscritto = new IscrittoProxyBean("email@test.it", null, null, null, null, null);
        recensione.setIscritto(iscritto);

        ContenutoBean contenuto = new FilmBean();
        contenuto.setId(-1);
        recensione.setContenuto(contenuto);

        return recensione;
    }


    private static String getTesto() {
        return "Uno dei film più riusciti e forse uno dei più belli della storia de cinema. " +
                "Undici Oscar per dire tutto questo, scene spettacolari, musiche bellissime del " +
                "compositore James Horner e battute azzeccate fino all'ultima. Come ha detto " +
                "Mymovies, nel film lavorarono più persone di quante se ne imbarcarono nel 1912 e" +
                " solo questo fa pensare a qualcosa di Straordinario.";
    }


    private static int getValutazione() {
        return 0;
    }


    /**
     * Costruttore utilizzato per l'esecuzione dei test parametrizzati
     * @param iscritto oggetto IscrittoBean che contiene le informazioni dell'autore della recensione
     * @param contenuto oggetto ContenutoBean che contiene le informazioni del contenuto cui è riferita la recensione
     * @param testo oggetto String contenente il testo della recensione da salvare
     * @param valutazione è la valutazione assegnata al contenuto cui è riferita la recensione
     */
    public PubblicazioneRecensioniTest_TC_1_3(IscrittoBean iscritto, ContenutoBean contenuto, String testo, int valutazione) {
        this.iscritto = iscritto;
        this.contenuto = contenuto;
        this.testo = testo;
        this.valutazione = valutazione;
    }


    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {getIscritto(), getContenuto(), getTesto(), getValutazione()}
        });
    }


    @Before
    public void init() {
        mockRecensioneDAO = Mockito.mock(RecensioneDAO.class);
        when(mockRecensioneDAO.doSaveRecensione(getRecensione())).thenReturn(true);
        recensioneService = new RecensioneImpl(mockRecensioneDAO);
    }


    @Test
    public void testCreaRecensione() {
        String testoInfo = testo.length() > 50 ? testo.substring(0, 10) + "... (" + testo.length()
                + " caratteri)" : testo;

        System.out.println("Parameterized input is: " + testoInfo + ", " + valutazione);

        assertThrows(ValutazioneException.class, () -> {
            recensioneService.creaRecensione(testo, valutazione, iscritto, contenuto);
        });
    }
}
