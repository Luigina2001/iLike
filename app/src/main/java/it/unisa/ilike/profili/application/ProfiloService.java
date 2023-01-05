package it.unisa.ilike.profili.application;

import it.unisa.ilike.account.storage.IscrittoBean;

/**
 * Questa interfaccia consente di gestire le informazioni relative al profilo di un iscritto.
 * @author Marta
 * @version 0.1
 */
public interface ProfiloService {
    public IscrittoBean getIscrittoReal(IscrittoBean i);
}