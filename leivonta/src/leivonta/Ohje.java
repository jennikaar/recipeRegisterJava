package leivonta;

import java.io.OutputStream;
import java.io.PrintStream;

import fi.jyu.mit.ohj2.Mjonot;
import kanta.AineTarkistus;

/**
 * Ohjeen luokka, tietää ohjeen kentät ja osaa laittaa merkkijonon i:nneksi kentäksi
 * sekä antaa merkkijonona i:nnen kentän tiedot
 * @author Jenni Kääriäinen
 * @version 22.4.2019
 */
public class Ohje implements Cloneable{
    
    private String ainesosat;
    private String ohje;
    private int valmistusaika;
    private int reseptiNro;
    private int tunnusNro;
    private int allergiaNro; // tunnus allergialle
    private String ohjeTyyppi; 
    
    private static int seuraavaNro = 1;
    
    private AineTarkistus aineet = new AineTarkistus();
    
    /**
     * Alustetaan ohje
     */
    public Ohje() {
        //Ei tarvetta vielä
    }
    
    /**
     * Alustetaan tietyn reseptin ohje 
     * @param reseptiNro reseptin viitenro
     */
    public Ohje(int reseptiNro) {
        this.reseptiNro = reseptiNro;
    }
    
    /**
     * Alustetaan tietyn reseptin ohje jolla tietyt allergiat
     * @param reseptiNro reseptin viitenro
     * @param allergiaNro allergian viitenro
     */
    public Ohje(int reseptiNro, int allergiaNro) {
        this.reseptiNro = reseptiNro;
        this.allergiaNro = allergiaNro;
    }
    
    /**
     * Tehdään ohjeesta kopio
     * @example
     * <pre name="test">
     *   Ohje oh = new Ohje();
     *   oh.parse("1|1|1|Perinteinen|200g keksejä, 75g voita...|1. Sulata voi ja murskaa keksit...|120");
     *   oh.anna(0) === "1";   
     *   oh.anna(1) === "1";   
     *   oh.anna(2) === "1";   
     *   oh.anna(3) === "Perinteinen";   
     *   oh.anna(4) === "200g keksejä, 75g voita...";   
     *   oh.anna(5) === "1. Sulata voi ja murskaa keksit...";
     *   oh.anna(6) === "120"; 
     * </pre>
     */
     @Override
     public Ohje clone() throws CloneNotSupportedException {
         Ohje uusi;
         uusi = (Ohje) super.clone();
         return uusi;
     }
    
    /**
     * Alustetaan tiedoiksi perinteisen ohjeen tiedot testiarvoiksi. 
     * Allergialla.
     * @param nro viite reseptiin, jonka ohjeesta kyse
     * @param anro viite allergiaan, jonka ohjeesta kyse
     */
    public void vastaaPerinteinen(int nro, int anro) {
        reseptiNro = nro;
        ainesosat = "200g keksejä, 75g voita..." ;
        ohje = "1. Sulata voi ja murskaa keksit...";
        valmistusaika = 120;
        ohjeTyyppi = "Perinteinen";
        allergiaNro = anro;
    }
    
    /**
     * Selvittää ohjeen tiedot | erotellusta merkkijonosta
     * Pitää huolen että seuraavaNro on isompi kuin tuleva tunnusNro
     * @param rivi josta ohjeen tiedot otetaan
     * @example
     * <pre name="test">
     * Ohje ohje = new Ohje();
     * ohje.parse("1|1|1|Perinteinen|200g keksejä, 75g voita...|1. Sulata voi ja murskaa keksit...|120");
     * ohje.getReseptiNro() === 1;
     * ohje.toString() === "1|1|1|Perinteinen|200g keksejä, 75g voita...|1. Sulata voi ja murskaa keksit...|120";
     * 
     * ohje.rekisteroi();
     * int n = ohje.getTunnusNro();
     * ohje.parse(""+(n+20));
     * ohje.rekisteroi();
     * ohje.getTunnusNro() === n+20+1;
     * ohje.toString() === "" + (n+20+1) + "|1|1|Perinteinen|200g keksejä, 75g voita...|1. Sulata voi ja murskaa keksit...|120"
     * </pre>
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer(rivi);
        setTunnusNro(Mjonot.erota(sb, '|', getTunnusNro()));
        reseptiNro = Mjonot.erota(sb, '|', reseptiNro); 
        allergiaNro = Mjonot.erota(sb, '|', allergiaNro); 
        ohjeTyyppi = Mjonot.erota(sb, '|', ohjeTyyppi);
        ainesosat = Mjonot.erota(sb, '|', ainesosat);
        ohje = Mjonot.erota(sb, '|', ohje);
        valmistusaika = Mjonot.erota(sb, '|', valmistusaika); 
    }
    


    /**
     * @param k Minkä kentän sisältö halutaan
     * @return valitun kentän sisältö
     * @example
     * <pre name="test">
     *   Ohje oh = new Ohje();
     *   oh.parse("1|1|1|Perinteinen|200g keksejä, 75g voita...|1. Sulata voi ja murskaa keksit...|120");
     *   oh.anna(0) === "1";
     *   oh.anna(1) === "1";
     *   oh.anna(2) === "1";
     *   oh.anna(3) === "Perinteinen";
     *   oh.anna(4) === "200g keksejä, 75g voita...";   
     *   oh.anna(5) === "1. Sulata voi ja murskaa keksit...";  
     *   oh.anna(6) === "120";
     * </pre>
     */
    public String anna(int k) {
        switch (k) {
            case 0:
                return "" + tunnusNro;
            case 1:
                return "" + reseptiNro;
            case 2:
                return "" + allergiaNro;
            case 3:
                return "" + ohjeTyyppi;
            case 4:
                return "" + ainesosat;
            case 5:
                return "" + ohje;
            case 6:
                return ""  + valmistusaika;
            default:
                return "???";
        }
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
     * Asetetaan ohjetyyppi ohjeelle
     * @param s tyyppi
     */
    public void setOhjeTyyppi(String s) {
        ohjeTyyppi = s;
    }
    
    /**
     * Asettaa valmistusajan
     * @param s kirjoitettu aika
     * @return valmistusaika
     */
    public String setValmistusaika(String s) {
        valmistusaika = Mjonot.erotaInt(s, 0);
        return null;
    }
    
    /**
     * Asettaa ohjeen
     * @param s kirjoitettu ohje
     * @return ohje
     */
    public String setOhje(String s) {
        String riviton = s.replaceAll("\n","@");
        ohje = riviton;
        return null;
    }
    
    /**
     * Aseta ohjeen ainesosat
     * @param s merkkijono ainesosia
     * @return Ainesosat merkkijonona
     */
    public String setAinesosat(String s) {
        ainesosat = s;
        String virhe = aineet.tarkista(s); 
        if ( s != null ) return virhe; 
        return null;
    }
    
    /**
     * Palauttaa ohjeen merkkijonona, jonka voi tallentaa tiedostoon
     * @return merkkijonon jonka voi tallentaa
     * @example
     * <pre name="test">
     * Ohje ohje = new Ohje();
     * ohje.parse("1|1|1|Perinteinen|200g keksejä, 75g voita...|1. Sulata voi ja murskaa keksit...|120");
     * ohje.toString() === "1|1|1|Perinteinen|200g keksejä, 75g voita...|1. Sulata voi ja murskaa keksit...|120";
     * </pre>
     */
    @Override
    public String toString() {
        return "" + getTunnusNro() + "|" + reseptiNro + "|" + allergiaNro + "|" + ohjeTyyppi + "|" + ainesosat + "|" + ohje + "|" + valmistusaika;
    }
    
    @Override
    public boolean equals(Object resepti) {
        if (resepti == null) return false;
        return this.toString().equals(resepti.toString());
    }
    
    @Override
    public int hashCode() {
        return tunnusNro;
    }
    
    /**
     * Alustetaan tiedoiksi perinteisen ohjeen tiedot testiarvoiksi. 
     * @param nro viite reseptiin, jonka ohjeesta kyse
     */
    public void vastaaPerinteinen(int nro) {
        reseptiNro = nro;
        ohjeTyyppi = "Perinteinen";
        ainesosat = "200g keksejä, 75g voita..." ;
        ohje = "1. Sulata voi ja murskaa keksit...";
        valmistusaika = 120;
    }
    
    /**
     * Tulostetaan tiedot
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
        String muokattuohje = ohje.replaceAll("@", "\n");
        out.println("Ohje nro " + tunnusNro + " " + "Resepti " + reseptiNro + " Allergia " + allergiaNro + " \n" + ohjeTyyppi + " \n" + ainesosat + " \n" + muokattuohje + " \n" + valmistusaika + " min" );
    }
    
    /**
     * Antaa ohjeelle rekisterinumeron
     * @return ohjeen tunnusnumeron, jolla se identifioidaan
     * @example
     * <pre name="test">
     * Ohje perinteinen = new Ohje();
     * perinteinen.getTunnusNro() === 0;
     * perinteinen.rekisteroi();
     * Ohje gluteeniton = new Ohje();
     * gluteeniton.rekisteroi();
     * int n1 = perinteinen.getTunnusNro();
     * int n2 = gluteeniton.getTunnusNro();
     * n1 === n2-1;
     * </pre>
     */
    public int rekisteroi() {
        tunnusNro = seuraavaNro;
        seuraavaNro++;
        return tunnusNro;
    }
    
    /**
     * Palautetaan ohjeen tunnusnro
     * @return ohjeen tunnusnro
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    /**
     * Palauttaa ohjeen reseptin numeron
     * @return palauttaa kyseisen reseptin numeron
     */
    public int getReseptiNro() {
        return reseptiNro;
    }
    
    /**
     * Palauttaa valmistusajan
     * @return valmistusaika
     */
    public int getValmistusaika() {
        return valmistusaika;
    }
    
    /**
     * Palauttaa ohjetyypin
     * @return ohjeen ohjetyyppi
     */
    public String getOhjeTyyppi() {
        return ohjeTyyppi;
    }
    
    /**
     * Palauttaa ohjeen ainesosat
     * @return ainesosat
     */
    public String getAinesosat() {
        return ainesosat;
    }
    
    /**
     * Hakee ohjeen allergian numeron
     * @return palauttaa kyseisen allergian numeron
     */
    public int getAllergiaNro() {
        return allergiaNro;
    }
    
    /**
     * Hakee ohjeen 
     * @return ohjeen ohje
     */
    public String getOhje() {
        String uusiohje = ohje;
        if (ohje !=null) uusiohje = ohje.replaceAll("@", "\n");
        return uusiohje;
    }
    
    /**
     * Asetetaan ohjeelle allergia
     * @param s Mitä ollaan asettamassa
     * @return null jos ok
     */
    public String setAllergia(String s) {
        Allergia allergia = new Allergia();
        allergia.setAllergia(s);
        return null;
    }
    
    /**
     * Asetetaan ohjeen allergialle tunnusnumero
     * @param tunnusnro allergian tunnusnumero
     */
    public void setAllergiaNro(int tunnusnro) {
        allergiaNro = tunnusnro;
    }
    
    /**
     * Testataan ohje-luokkaa
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Ohje ohje = new Ohje();
        ohje.vastaaPerinteinen(1,1);
        ohje.tulosta(System.out);
    }

}
