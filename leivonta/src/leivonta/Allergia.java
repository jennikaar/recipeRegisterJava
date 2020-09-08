package leivonta;

import java.io.OutputStream;
import java.io.PrintStream;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Allergian luokka, 
 * - Tietää Allergian/Ruokavalion kentät 
 * - Osaa antaa merkkijonona i:n kentän tiedot
 * - Osaa laittaa merkkijonon i:neksi kentäksi 
 * @author Jenni Kääriäinen
 * @version 7.3.2019
 * @version 2.4.2019 tiedostojen käsittely
 */
public class Allergia {
    
    private int tunnusNro;
    private String allergia;
    
    private static int seuraavaNro = 1;
    
    /**
     * Alustetaan allergian tiedot
     */
    public Allergia() {
        //Ei tarvetta 
    }
    
    /**
     * Alustetaan tiedoiksi pähkinättömän allergian testiarvoiksi. 
     */
    public void vastaaPahkinaton() {
        allergia = "Pähkinätön";
    }
    
    /**
     * @param os tietovirta johon tulostetaan
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
    }
    
    /**
     * Tulostetaan ohjeen tiedot
     * @param out tietovirta johon tulostetaan
     */
    public void tulosta(PrintStream out) {
        out.println(allergia);
    }
    
    /**
     * Selvittää allergian tiedot | erotellusta merkkijonosta
     * Pitää huolen että seuraavaNro on isompi kuin tuleva tunnusNro
     * @param rivi josta ohjeen tiedot otetaan
     * @example
     * <pre name="test">
     * Allergia allergia = new Allergia();
     * allergia.parse("1|pähkinätön, munaton|");
     * allergia.getTunnusNro() === 1;
     * allergia.toString() === "1|pähkinätön, munaton";
     * 
     * allergia.rekisteroi();
     * int n = allergia.getTunnusNro();
     * allergia.parse(""+(n+20));
     * allergia.rekisteroi();
     * allergia.getTunnusNro() === n+20+1;
     * allergia.toString() === "" + (n+20+1) + "|pähkinätön, munaton"
     * </pre>
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer(rivi);
        setTunnusNro(Mjonot.erota(sb, '|', getTunnusNro()));
        allergia = Mjonot.erota(sb, '|', allergia);  
    }
    
    
    /**
     * Asettaa tunnusnumeron ja varmistaa, että seuraava numero on aina
     * isompi
     * @param nr annettava tunnusnumero
     */
    private void setTunnusNro(int nr) {
        tunnusNro = nr;
        if ( tunnusNro >= seuraavaNro ) seuraavaNro = tunnusNro + 1;
    }
    
    /**
     * Palauttaa ohjeen merkkijonona, jonka voi tallentaa tiedostoon
     * @return merkkijonon jonka voi tallentaa
     * @example
     * <pre name="test">
     * Allergia allergia = new Allergia();
     * allergia.parse ("1|pähkinätön, munaton|");
     * allergia.toString() === "1|pähkinätön, munaton";
     * </pre>
     */
    @Override
    public String toString() {
        return "" + getTunnusNro() + "|" + allergia;
    }
    
    /**
     * Tarkistetaan onko samat, hash codet samat
     */
    @Override
    public boolean equals(Object resepti) {
        if (resepti == null) return false;
        return this.toString().equals(resepti.toString());
    }
    
    /**
     * Natiivi metodi, palauttaa objektin int 
     * hash coden
     */
    @Override
    public int hashCode() {
        return tunnusNro;
    }
    
    /**
     * Antaa ohjeelle rekisterinumeron
     * @return ohjeen tunnusmureon, jolla se identifioidaan
     * @example
     * <pre name="test">
     * Allergia pahkinaton = new Allergia();
     * pahkinaton.getTunnusNro() === 0;
     * pahkinaton.rekisteroi();
     * Allergia munaton = new Allergia();
     * munaton.rekisteroi();
     * int n1 = pahkinaton.getTunnusNro();
     * int n2 = munaton.getTunnusNro();
     * n1 === n2-1;
     * </pre>
     */
    public int rekisteroi() {
        tunnusNro = seuraavaNro;
        seuraavaNro++;
        return tunnusNro;
    }
    
    /**
     * Palautetaan allergian tunnusnro
     * @return allergian tunnusnro
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    /**
     * Palautetaan allergia
     * @return allergia
     */
    public String getAllergia() {
        return allergia;
    }
    
    /**
     * Asetetaan allergiaksi
     * @param s mikä allergia on
     */
    public void setAllergia(String s) {
        allergia = s;
    }
    
    /**
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Allergia allergia = new Allergia();
        allergia.vastaaPahkinaton();
        allergia.tulosta(System.out);
    }

}
