package it.unisa.ilike.liste.application.exceptions;

/**
 * Classe che estende Exception
 *  @version 0.1
 *  @author FrancescoGiorgione
 *  @see java.lang.Exception
 */
public class NomeVuotoException extends Exception {
    /**
     * Il metodo restituisce un'eccezione controllata che comunica che l'iscritto ha inserito una
     * stringa vuota come nome della sua nuova lista.
     */
    public NomeVuotoException() {
        super("Il nome della lista non può essere vuoto");
    }
}
