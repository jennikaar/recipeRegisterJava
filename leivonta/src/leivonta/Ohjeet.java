package leivonta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Ohjeet luokka, joka osaa mm. lisätä ohjeen reseptiin, hakea tietyn reseptin ohjeet
 * Tietorakenne tehty listoilla.
 * Iteraattori rajapinta.
 * @author Jenni Kääriäinen
 * @version 22.4.2019
 */
public class Ohjeet implements Iterable<Ohje> {
    
    private boolean muutettu = false;
    private String tiedostonPerusnimi = "";
    
    /*Taulukko ohjeista*/
    private final List<Ohje> alkiot = new ArrayList<Ohje>();
    
    
    /**
     * Ohjeiden alustaminen
     */
    public Ohjeet() {
        //Ei vielä mitään
    }
    
    /**
     * Lisää uuden ohjeen tietorakenteeseen ja ottaa ohjeen omistukseensa.
     * Tietorakenne (tässä lista) muuttuu ohjeen omistajaksi.
     * @param ohje mikä ohje lisätään
     */
    public void lisaa(Ohje ohje) {
        alkiot.add(ohje);
        muutettu = true;
    }
    
    /**
     * Tiedoston lukemiseen. Lukee ohjeet tiedostosta.
     * @param tied tiedoston nimen alkuosa
     * @throws SailoException jos ei onnistuta lukemaan tiedostoa
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * #import java.io.File; 
     * Ohjeet ohjeet = new Ohjeet();
     * Ohje ohje1 = new Ohje(); ohje1.vastaaPerinteinen(1);
     * Ohje ohje2 = new Ohje(); ohje2.vastaaPerinteinen(2);
     * Ohje ohje3 = new Ohje(); ohje3.vastaaPerinteinen(1);
     * Ohje ohje4 = new Ohje(); ohje4.vastaaPerinteinen(2);
     * String tiedNimi = "testileivonta";
     * File ftied = new File(tiedNimi+".dat");
     * ftied.delete();
     * ohjeet.lueTiedostosta(tiedNimi); #THROWS SailoException
     * ohjeet.lisaa(ohje1);
     * ohjeet.lisaa(ohje2);
     * ohjeet.lisaa(ohje3);
     * ohjeet.lisaa(ohje4);
     * ohjeet.tallenna();
     * ohjeet = new Ohjeet();
     * ohjeet.lueTiedostosta(tiedNimi);
     * Iterator<Ohje> i = ohjeet.iterator();
     * i.next().toString() === ohje1.toString();
     * i.next().toString() === ohje2.toString();
     * i.next().toString() === ohje3.toString();
     * i.next().toString() === ohje4.toString();
     * i.hasNext() === false;
     * ohjeet.lisaa(ohje1);
     * ohjeet.tallenna();
     * ftied.delete() === true;
     * File fbak = new File(tiedNimi+".bak");
     * fbak.delete() === true;
     * </pre>
     */
    public void lueTiedostosta(String tied) throws SailoException {
        setTiedostonPerusnimi(tied);
        
        try (BufferedReader fi = new BufferedReader(new FileReader(getTiedostonNimi()))){
            String rivi;
            while ((rivi = fi.readLine())!= null) {
                rivi = rivi.trim();
                if ( "".equals(rivi) || rivi.charAt(0) == ';' ) continue;
                Ohje ohje = new Ohje();
                ohje.parse(rivi);
                lisaa(ohje);
            }
            muutettu = false;
        } catch(FileNotFoundException e){
            throw new SailoException("Tiedosto " + getTiedostonNimi() + " ei aukea"); 
        } catch(IOException e){
            throw new SailoException("Ongelmia tiedoston kanssa: " + e.getMessage());
        }
    }
    
    /**
     * Lukee edellisenä annetun tiedoston
     * @throws SailoException poikkeuksen tapahtuessa
     */
    public void lueTiedostosta() throws SailoException{
        lueTiedostosta(getTiedostonPerusNimi());
    }
    
    /**
     * Asettaa tiedoston perusnimen 
     * @param tied tallennustiedoston nimi
     */
    public void setTiedostonPerusnimi(String tied) {
        tiedostonPerusnimi = tied;
    }
    
    /**
     * Palauttaa tiedoston nimen, jota tallennukseen käytetään
     * @return tiedoston perusnimen
     */
    public String getTiedostonNimi() {
        return tiedostonPerusnimi + ".dat";
    }
    
    /**
     * Palauttaa tiedoston nimen, jota tallennukseen käytetään
     * @return tiedoston perusnimen
     */
    public String getTiedostonPerusNimi() {
        return tiedostonPerusnimi;
    }
    
    /**
     * Tiedoston tallentamiseen. 
     * @throws SailoException Jos tallentaminen epäonnistuu
     */
    public void tallenna() throws SailoException {
        if ( !muutettu) return;
        
        File fbak = new File(getBakNimi());
        File ftied = new File(getTiedostonNimi());
        fbak.delete();
        ftied.renameTo(fbak);
        
        try (PrintWriter fo = new PrintWriter(new FileWriter(ftied.getCanonicalPath()))){
            for(Ohje ohje: this) {
                fo.println(ohje.toString());
            }
        } catch (FileNotFoundException ex) {
            throw new SailoException("Tiedosto " + ftied.getName() + " ei aukea");
        } catch (IOException ex) {
            throw new SailoException("Tiedoston " + ftied.getName() + " kirjoittamisessa ongelmia");
        }
        
        muutettu = false;
    }
    
    /**
     * Palauttaa varakopiotiedoston nimen
     * @return varakopiotied nimi
     */
    public String getBakNimi() {
        return tiedostonPerusnimi + ".bak";
    }
    /**
     * Palauttaa ohjeiden lukumäärän leivonta-"kirjassa"
     * @return ohjeiden lukumäärä
     */
    public int getLkm() {
        return alkiot.size();
    }
    
    /**
     * Iteraattori ohjeiden läpikäymiseen
     * @return ohjeiteraattori
     * @example
     * <pre name="test">
     * #PACKAGEIMPORT
     * #import java.util.*;
     * 
     * Ohjeet ohjeet = new Ohjeet();
     * Ohje perinteinen1 = new Ohje(1); ohjeet.lisaa(perinteinen1);
     * Ohje perinteinen2 = new Ohje(2); ohjeet.lisaa(perinteinen2);
     * Ohje gluteeniton = new Ohje(1); ohjeet.lisaa(gluteeniton);
     * 
     * Iterator<Ohje> i2 = ohjeet.iterator();
     * i2.next() === perinteinen1;
     * i2.next() === perinteinen2;
     * i2.next() === gluteeniton;
     * i2.next() === gluteeniton; #THROWS NoSuchElementException
     * 
     * int n = 0;
     * int[] rnrot = {1,2,1};
     * 
     * for(Ohje oh:ohjeet){
     *  oh.getReseptiNro() === rnrot[n]; n++;
     * }
     * 
     * n===3;
     * </pre>
     */
    @Override
    public Iterator<Ohje> iterator(){
        return alkiot.iterator();
    }
    
    /**
     * Haetaan kaikki tietyn reseptin ohjeet
     * @param tunnusnro reseptin nro jonka ohjeesta kyse
     * @return tietorakenne(lista) jossa viittaukset ohjeisiin 
     * @example
     * <pre name="test">
     * #import java.util.*;
     * Ohjeet ohjeet = new Ohjeet();
     * Ohje perinteinen1 = new Ohje(1); ohjeet.lisaa(perinteinen1);
     * Ohje perinteinen2 = new Ohje(2); ohjeet.lisaa(perinteinen2);
     * Ohje gluteeniton = new Ohje(1); ohjeet.lisaa(gluteeniton);
     * 
     * List<Ohje> loytyneet;
     * loytyneet = ohjeet.annaOhjeet(3);
     * loytyneet.size() === 0;
     * loytyneet = ohjeet.annaOhjeet(2);
     * loytyneet.size() === 1;
     * loytyneet.get(0) == perinteinen2 === true;
     * loytyneet = ohjeet.annaOhjeet(1);
     * loytyneet.size() === 2;
     * loytyneet.get(0) == perinteinen1 === true;
     * loytyneet.get(1) == gluteeniton === true;
     * </pre>
     */
    public List<Ohje> annaOhjeet(int tunnusnro) {
        List<Ohje> loydetyt = new ArrayList<Ohje>();
        for(Ohje oh : alkiot) {
            if(oh.getReseptiNro() == tunnusnro) loydetyt.add(oh);
        }
        return loydetyt;
    }
    
    /**
     * Korvaa ohjeen tietorakenteessa.  Ottaa omistukseensa.
     * Etsitään samalla tunnusnumerolla oleva ohje.  Jos ei löydy, niin lisätään uutena ohjeena.
     * @param ohje lisättävän reseptin viite
     * @throws SailoException jos epäonnistuu
     * @example
     * <pre name="test">
     * #THROWS SailoException,CloneNotSupportedException
     * #PACKAGEIMPORT
     * Ohjeet ohjeet = new Ohjeet();
     * Ohje ohje1 = new Ohje(), ohje2 = new Ohje();
     * 
     * ohje1.rekisteroi(); ohje2.rekisteroi();
     * ohjeet.getLkm() === 0;
     * ohjeet.korvaaTaiLisaa(ohje1); ohjeet.getLkm() === 1;
     * ohjeet.korvaaTaiLisaa(ohje2); ohjeet.getLkm() === 2;
     * Ohje ohje3 = ohje1.clone();
     * 
     * 
     * Iterator<Ohje> i2=ohjeet.iterator();
     * i2.next() === ohje1;
     * ohjeet.korvaaTaiLisaa(ohje3); ohjeet.getLkm() === 2;
     * i2=ohjeet.iterator();
     * Ohje h = i2.next();
     * h === ohje3;
     * h == ohje3 === true;
     * h == ohje1 === false;
     * </pre>
     */
    public void korvaaTaiLisaa(Ohje ohje) throws SailoException {
        int id = ohje.getTunnusNro();
        for (int i = 0; i < getLkm(); i++) {
            if (alkiot.get(i).getTunnusNro() == id) {
                alkiot.set(i, ohje);
                muutettu = true;
                return;
            }
        }
        lisaa(ohje);
    }
    
    /**
     * Poistaa kaikki tietyn tietyn reseptin ohjeet
     * @param tunnusNro viite siihen, mihin liittyvät tietueet poistetaan
     * @return montako poistettiin
     * @example
     * <pre name="test">
     *  Ohjeet ohjeet = new Ohjeet();
     *  Ohje ohje1 = new Ohje (); ohje1.vastaaPerinteinen(1);
     *  Ohje ohje2 = new Ohje (); ohje2.vastaaPerinteinen(1);
     *  Ohje ohje3 = new Ohje (); ohje3.vastaaPerinteinen(2);
     *  Ohje ohje4 = new Ohje (); ohje4.vastaaPerinteinen(3);
     *  
     *  ohjeet.lisaa(ohje1);
     *  ohjeet.lisaa(ohje2);
     *  ohjeet.lisaa(ohje3);
     *  ohjeet.lisaa(ohje4);
     *  ohjeet.poistaReseptinOhjeet(1) === 2; ohjeet.getLkm() === 2;
     *  ohjeet.poistaReseptinOhjeet(4) === 0; ohjeet.getLkm() === 2;
     *  List<Ohje> h = ohjeet.annaOhjeet(1);
     *  h.size() === 0; 
     *  h = ohjeet.annaOhjeet(2);
     *  h.get(0) === ohje3;
     * </pre>
     */
    public int poistaReseptinOhjeet(int tunnusNro) {
        int n = 0;
        for (Iterator<Ohje> it = alkiot.iterator(); it.hasNext();) {
            Ohje oh = it.next();
            if ( oh.getReseptiNro() == tunnusNro ) {
                it.remove();
                n++;
            }
        }
        if (n > 0) muutettu = true;
        return n;
    }
    
    /**
     * Poistaa ohjeen jolla on valittu tunnusnumero 
     * @param ohje joka poistetaan
     * @return tosi jos löytyi poistettava ohje
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * #import java.io.File;
     *  Ohjeet ohjeet = new Ohjeet();
     *  Ohje ohje1 = new Ohje (); ohje1.vastaaPerinteinen(1);
     *  Ohje ohje2 = new Ohje (); ohje2.vastaaPerinteinen(1);
     *  Ohje ohje3 = new Ohje (); ohje3.vastaaPerinteinen(2);
     *  Ohje ohje4 = new Ohje (); ohje4.vastaaPerinteinen(3);
     *  Ohje ohje5 = new Ohje (); ohje5.vastaaPerinteinen(4);
     *  
     *  ohjeet.lisaa(ohje1);
     *  ohjeet.lisaa(ohje2);
     *  ohjeet.lisaa(ohje3);
     *  ohjeet.lisaa(ohje4);
     *  ohjeet.poista(ohje5) === false; ohjeet.getLkm() === 4;
     *  ohjeet.poista(ohje1) === true;   ohjeet.getLkm() === 3;
     *  List<Ohje> h = ohjeet.annaOhjeet(1);
     *  h.size() === 1; 
     *  h.get(0) === ohje2;
     * </pre>
     * 
     */ 
    public boolean poista(Ohje ohje) { 
        boolean ret = alkiot.remove(ohje);
        if (ret) muutettu = true;
        return ret;
    } 
    
    /**
     * Testataan ohjeet-luokkaa
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Ohjeet ohjeet = new Ohjeet();
        Ohje ohje1 = new Ohje();
        ohje1.vastaaPerinteinen(1,1);
        Ohje ohje2 = new Ohje();
        ohje2.vastaaPerinteinen(2,1);
        Ohje ohje3 = new Ohje();
        ohje3.vastaaPerinteinen(1,2);
        
        ohjeet.lisaa(ohje1);
        ohjeet.lisaa(ohje2);
        ohjeet.lisaa(ohje3);
        
        /*Kokeillaan osaako lisätä reseptin 1 ohjeet listaan ja tulostaa ne*/
        System.out.println("============= Ohjeet testi =================");
        List<Ohje> ohjeet2 = ohjeet.annaOhjeet(1);
        
        for (Ohje oh : ohjeet2) {
            System.out.println(oh.getReseptiNro() + " " + oh.getAllergiaNro() + " ");
            oh.tulosta(System.out);
        }
        
    }
}
