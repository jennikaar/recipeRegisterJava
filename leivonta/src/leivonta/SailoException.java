package leivonta;

/**
 * Poikkeusluokka tietorakenteiden aiheuttamille poikkeuksille
 * @author Jenni Kääriäinen
 * @version 25.2.2019
 *
 */
public class SailoException extends Exception {
    
    private static final long serialVersionUID = 1L; //versionumero
    
    /**
     * Muodostaja, jolle tuodaan poikkeuksen viesti
     * @param viesti Poikkeuksen viesti
     */
    public SailoException (String viesti) {
        super(viesti);
    }

}
