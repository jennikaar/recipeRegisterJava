package leivonta;

import java.io.*;
import java.util.Comparator;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Resepti-luokka joka osaa huolehtia tunnusnumerosta, 
 * tietää reseptin kentät(nimi,kategoria,teema..)
 * 
 * @author Jenni Kääriäinen
 * @version 22.4.2019
 */
public class Resepti implements Cloneable{
    
    private int tunnusNro;
    private static int seuraavaNro;
    private String rnimi = "";
    private String kategoria = "";
    private String teema = "";
    
    /**
     * Palautetaan reseptin nimi
     * @return reseptin nimi
     * @example
     * <pre name="test">
     * Resepti juustokakku = new Resepti();
     * juustokakku.vastaaJuustokakku();
     * juustokakku.getNimi() =R= "Juustokakku";
     * </pre>
     */
    public String getNimi() {
        return rnimi;
    }
    
    /**
     * Palautaan reseptin kategoria
     * @return kategoria
     * Resepti juustokakku = new Resepti();
     * juustokakku.vastaaJuustokakku();
     * juustokakku.getKategoria() =R= "Juustokakut";
     */
    public String getKategoria() {
        return kategoria;
    }
    
    /**
     * Palautetaan reseptin teema
     * @return teema
     */
    public String getTeema() {
        return teema;
    }
    
    /**
     * Eka kenttä joka mielekäs kysyttäväksi
     * @return ekan kentän indeksi
     */
    public int ekaKentta() {
        return 1;
    }
    
    /**
     * Asetetaan nimi
     * @param s uusi nimi
     * @return null jos ok
     */
    public String setRnimi(String s) {
        rnimi = s;
        return null;
    }
    
    /**
     * Asetetaan kategoria
     * @param s uusi kategoria
     * @return null jos ok
     */
    public String setKategoria(String s) {
        kategoria = s;
        return null;
    }
    
    /**
     * Asetetaan teema
     * @param s uusi teema
     * @return null jos ok
     */
    public String setTeema(String s) {
        teema = s;
        return null;
    }
    /**
     * Apumetodi, jonka avulla täytetään testiarvot jäsenelle
     */
    public void vastaaJuustokakku() {
        rnimi = "Juustokakku";
        kategoria = "Juustokakut";
        teema = "";
    }
    
   /**
    * Tehdään reseptistä kopio
    * @example
    * <pre name="test">
    *  #THROWS CloneNotSupportedException 
    *  Resepti resepti = new Resepti();
    *  resepti.parse("   1  |  Juustokakku   | juustokakut");
    *  Resepti kopio = resepti.clone();
    *  kopio.toString() === resepti.toString();
    *  resepti.parse("  2  |  Juustokakku   | juustokakut");
    *  kopio.toString().equals(resepti.toString()) === false;
    * </pre>
    */
    @Override
    public Resepti clone() throws CloneNotSupportedException {
        Resepti uusi;
        uusi = (Resepti) super.clone();
        return uusi;
    }
    
    /**
     * Antaa k:n kentän sisällön merkkijonona
     * @param k monenneko kentän sisältö palautetaan
     * @return kentän sisältö merkkijonona
     */
    public String anna(int k) {
        switch (k) {
        case 0: return "" + tunnusNro;
        case 1: return "" + rnimi;
        case 2: return "" + kategoria;
        case 3: return "" + teema;
        default: return "Äääliö";
        }
    }
    
    /**
     * Reseptin vertailija
     */ 
    public static class Vertailija implements Comparator<Resepti> { 
        private int k; 
         
        @SuppressWarnings("javadoc") 
        public Vertailija(int k) { 
            this.k = k; 
        } 
         
        @Override 
        public int compare(Resepti resepti1, Resepti resepti2) { 
            return resepti1.anna(k).compareToIgnoreCase(resepti2.anna(k)); 
        } 
    } 
    
    /**
     * Selvittää reseptin tiedot | erotellusta merkkijonosta
     * Pitää huolen että seuraavanro on suurempi kuin tunnusnro.
     * @param rivi josta reseptin tiedot otetaan 
     * @example
     * <pre name="test">
     * Resepti resepti = new Resepti();
     * resepti.parse("   1  |  Juustokakku   | juustokakut"); 
     * resepti.getTunnusNro() === 1;
     * resepti.toString().startsWith("1|Juustokakku|juustokakut|") === true;
     * 
     * resepti.rekisteroi();
     * int n = resepti.getTunnusNro();
     * resepti.parse(""+(n+20));
     * resepti.rekisteroi();
     * resepti.getTunnusNro() === 20+n+1;
     * </pre>
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer(rivi);
        setTunnusNro(Mjonot.erota(sb, '|', getTunnusNro()));
        rnimi = Mjonot.erota(sb, '|', rnimi);
        kategoria = Mjonot.erota(sb,'|', kategoria);
        teema = Mjonot.erota(sb, '|', teema);
    }
    
    /**
     * Asettaa tunnusnumeron ja varmistaa, että seuraava numero on aina suurempi
     * @param nr asetettava tunnusnumero
     */
    private void setTunnusNro(int nr) {
        tunnusNro = nr;
        if (tunnusNro >= seuraavaNro) seuraavaNro = tunnusNro +1;   
    }
    
    /**
     * Tutkii onko reseptin tiedot samat kuin parametrina tuodut tiedot
     * @example
     * <pre name="test">  
     * Resepti resepti1 = new Resepti();
     * resepti1.parse("   1  |  Juustokakku   | juustokakut");
     * Resepti resepti2 = new Resepti();
     * resepti2.parse("   1  |  Juustokakku   | juustokakut");
     * Resepti resepti3 = new Resepti();
     * resepti3.parse("   2  |  Korvapuustit   | pullat");
     * resepti1.equals(resepti2) === true;
     * resepti2.equals(resepti1) === true;
     * resepti1.equals(resepti3) === false;
     * resepti3.equals(resepti2) === false;   
     * </pre>
     */
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
     * Palauttaa reseptin tiedot merkkijonona, jonka voi tallentaa tiedostoon
     * @return resepti tolppaeroteltuna merkkijonona
     * @example
     * <pre name="test">
     * Resepti resepti = new Resepti();
     * resepti.parse("   1  |  Juustokakku   | juustokakut"); 
     * resepti.toString().startsWith("1|Juustokakku|juustokakut|") === true;
     * </pre>
     */
    @Override
    public String toString() {
        return "" +
                getTunnusNro() + "|" +
                rnimi + "|" +
                kategoria + "|" +
                teema;
    }

    /**
     * Tulostetaan reseptin tiedot
     * @param out tietovirta johon tulostetaan
     */
    public void tulosta(PrintStream out) {
        out.println(String.format("%03d", tunnusNro, 3) + "  " + rnimi + "  ");
        out.println("Kategoria: "+ kategoria + " sekä teema: " + teema );
    }
    
    /**
     * Tulostetaan reseptin tiedot
     * @param os tietovirta johon tulostetaan
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
    }
    
    /**
     * Reseptin rekisteröinti eli antaa reseptille rekisterinumeron
     * @return tunnusnumero jonka resepti saa
     * @example
     * <pre name="test">
     * Resepti juustokakku = new Resepti();
     * juustokakku.getTunnusNro();
     * juustokakku.rekisteroi();
     * Resepti juustokakku2 = new Resepti();
     * juustokakku2.rekisteroi();
     * int j1 = juustokakku.getTunnusNro();
     * int j2 = juustokakku2.getTunnusNro();
     * j1 === j2-1;
     * </pre>
     */
    public int rekisteroi() {
        tunnusNro = seuraavaNro;
        seuraavaNro++;
        return tunnusNro;
    }
    
    /**
     * Palauttaa tunnusnumeron
     * @return tunnusnumeron
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    /**
     * Testiohjelma reseptille
     * @param args ei käytössä
     */
    public static void main(String args[]) {
        Resepti juustokakku = new Resepti();
        Resepti juustokakku2 = new Resepti();
        juustokakku.rekisteroi();
        juustokakku2.rekisteroi();
        juustokakku.tulosta(System.out);
        juustokakku.vastaaJuustokakku();
        juustokakku.tulosta(System.out);

        juustokakku2.vastaaJuustokakku();
        juustokakku2.tulosta(System.out);

        juustokakku2.vastaaJuustokakku();
        juustokakku2.tulosta(System.out);
    }

}
