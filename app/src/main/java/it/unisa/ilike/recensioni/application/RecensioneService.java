package it.unisa.ilike.recensioni.application;

import java.util.List;

import it.unisa.ilike.account.storage.IscrittoBean;
import it.unisa.ilike.contenuti.storage.ContenutoBean;
import it.unisa.ilike.recensioni.application.exceptions.InvalidMotivazioneException;
import it.unisa.ilike.recensioni.application.exceptions.InvalidTestoException;
import it.unisa.ilike.recensioni.application.exceptions.InvalidTipoException;
import it.unisa.ilike.recensioni.application.exceptions.MotivazioneVuotaException;
import it.unisa.ilike.recensioni.application.exceptions.TestoTroppoBreveException;
import it.unisa.ilike.recensioni.application.exceptions.ValutazioneException;
import it.unisa.ilike.recensioni.storage.RecensioneBean;
import it.unisa.ilike.utils.exceptions.NotIscrittoException;

/**
 * Interfaccia che esplicita i metodi di servizio relativi alle recensioni
 * @version 0.1
 * @author LuiginaCostante
 */

public interface RecensioneService {

    public boolean creaRecensione(String testo, int valutazione, IscrittoBean i, ContenutoBean c)
            throws NotIscrittoException, TestoTroppoBreveException, InvalidTestoException, ValutazioneException;

    public boolean aggiungiSegnalazione (int tipo, String motivazione, RecensioneBean r, IscrittoBean i)
            throws NotIscrittoException, InvalidTipoException, MotivazioneVuotaException, InvalidMotivazioneException;

    public List<RecensioneBean> getRecensioniContenuto (ContenutoBean c);

}