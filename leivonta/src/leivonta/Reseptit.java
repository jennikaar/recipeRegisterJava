package leivonta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import fi.jyu.mit.ohj2.WildChars;

/**
 * Leivonta-rekisterin reseptit 
 * osaa mm. lisätä reseptin
 * @author Jenni Kääriäinen
 * @version 26.4.2019
 */
public class Reseptit implements Iterable<Resepti> {
    
    private int lkm = 0;
    private static final int MAX_RESEPTEJA = 8;
    private String tiedostonPerusNimi = "reseptit";
    private Resepti alkiot[] = new Resepti[MAX_RESEPTEJA];
    private boolean muutettu = false;
    
    /**
     * Oletusmuodostaja
     */
    public Reseptit() {
        // Attribuuttien oma alustus riittää
    }
    
    /**
     * Luokka jäsenten iteroimiseksi
     *@example
     * <pre name="test">
     * #THROWS SailoException  
     * #PACKAGEIMPORT 
     * #import java.util.*;
     * 
     * Reseptit reseptit = new Reseptit();
     * Resepti juustokakku = new Resepti(), juustokakku2 = new Resepti();
     * juustokakku.rekisteroi(); juustokakku2.rekisteroi();
     * 
     * reseptit.lisaa(juustokakku);
     * reseptit.lisaa(juustokakku2);
     * 
     * StringBuffer ids = new StringBuffer(30);
     * for (Resepti resepti: reseptit)
     *      ids.append(" "+ resepti.getTunnusNro()); 
     *      
     * String tulos = " " + juustokakku.getTunnusNro() + " " + juustokakku2.getTunnusNro(); 
     * ids.toString() === tulos;
     * 
     * ids = new StringBuffer(30);
     * for (Iterator<Resepti>  i=reseptit.iterator(); i.hasNext(); ) {
     *  Resepti resepti = i.next();
     *  ids.append(" "+ resepti.getTunnusNro());
     * }
     * 
     * ids.toString() === tulos;
     * 
     * Iterator<Resepti>  i=reseptit.iterator(); 
     * i.next() == juustokakku  === true; 
     * i.next() == juustokakku2  === true; 
     * 
     * i.next();  #THROWS NoSuchElementException 
     * </pre>
     */
    public class ReseptitIterator implements Iterator<Resepti>{
        private int kohdalla = 0;
        
        /**
         * Onko olemassa vielä seuraavaa reseptiä
         * @see java.util.Iterator#hasNext() 
         * @return true jos on olemassa vielä reseptejä
         */
        @Override
        public boolean hasNext() {
            return kohdalla < getLkm();
        }
        
        /**
         * Annetaan seuraava resepti
         * @return seuraava resepti
         * @throws NoSuchElementException jos seuraavaa alkiota ei ole
         * @see java.util.Iterator#next() 
         */
        @Override
        public Resepti next() throws NoSuchElementException{
            if ( !hasNext() ) throw new NoSuchElementException("Ei oo");
            return anna(kohdalla++);
        }
        
        /**
         * Tuhoamista ei ole toteutettu
         * @throws UnsupportedOperationException aina
         * @see java.util.Iterator#remove() 
         */
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Me ei poisteta");
        }
    }
    
    /**
     * Palautetaan iteraattori resepteistään
     * @return reseptien iteraattori
     */
    @Override
    public Iterator<Resepti> iterator(){
        return new ReseptitIterator();
    }
    
    /**
     * Lisää uuden reseptin tietorakenteeseen. Ottaa reseptin omistukseensa.
     * @param resepti lisättävän reseptin viite. Tietorakenne muuttuu omistajaksi.
     * @throws SailoException jos tietorakenne täynnä
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Reseptit reseptit = new Reseptit();
     * Resepti juustokakku = new Resepti();
     * Resepti juustokakku2 = new Resepti();
     * reseptit.getLkm() === 0;
     * reseptit.lisaa(juustokakku); reseptit.getLkm() === 1;
     * reseptit.lisaa(juustokakku2); reseptit.getLkm() === 2;
     * reseptit.lisaa(juustokakku); reseptit.getLkm() === 3;
     * reseptit.anna(0) === juustokakku;
     * reseptit.anna(1) === juustokakku2;
     * reseptit.anna(2) === juustokakku;
     * reseptit.anna(1) == juustokakku === false;
     * reseptit.anna(1) == juustokakku2 === true;
     * reseptit.anna(3) === juustokakku; #THROWS IndexOutOfBoundsException 
     * reseptit.lisaa(juustokakku); reseptit.getLkm() === 4;
     * reseptit.lisaa(juustokakku); reseptit.getLkm() === 5;
     * reseptit.lisaa(juustokakku); reseptit.getLkm() === 6;
     * reseptit.lisaa(juustokakku); reseptit.getLkm() === 7;
     * reseptit.lisaa(juustokakku); reseptit.getLkm() === 8;
     * reseptit.lisaa(juustokakku); 
     * </pre>
     */
    public void lisaa(Resepti resepti) throws SailoException {
        if (lkm >= alkiot.length) alkiot = Arrays.copyOf(alkiot, lkm+20);
        alkiot[lkm] = resepti;
        lkm++;
        muutettu = true;
    }
    
    /**
     * Palauttaa viitteen i:teen reseptiin
     * @param i monennen reseptin viite halutaan
     * @return viite reseptiin jonka indeksi on i
     * @throws IndexOutOfBoundsException jos indeksi on taulukon ulkopuolella
     */
    public Resepti anna(int i) throws IndexOutOfBoundsException {
        if (i<0 || lkm <= i)
            throw new IndexOutOfBoundsException("Laiton indeksi: " + i);
        return alkiot[i];
    }
    
    /**
     * Lukee reseptin tiedostosta
     * @param tied tiedoston perusnimi
     * @throws SailoException jos lukeminen ei onnistu
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * #import java.io.File; 
     * 
     * Reseptit reseptit = new Reseptit();
     * 
     * Resepti juustokakku = new Resepti(); 
     * Resepti juustokakku2 = new Resepti();
     * juustokakku.vastaaJuustokakku(); juustokakku2.vastaaJuustokakku();
     * 
     * String hakemisto = "testileivonta";
     * String tiedNimi = hakemisto+"/reseptit"; 
     * File ftied = new File(tiedNimi+".dat");
     * File dir = new File(hakemisto); 
     * dir.mkdir(); 
     * ftied.delete(); 
     * reseptit.lueTiedostosta(tiedNimi); #THROWS SailoException
     * reseptit.lisaa(juustokakku);
     * reseptit.lisaa(juustokakku2);
     * reseptit.tallenna();
     * reseptit = new Reseptit();
     * reseptit.lueTiedostosta(tiedNimi);  
     *  Iterator<Resepti> i = reseptit.iterator(); 
     *  i.next() === juustokakku;  
     *  i.next() === juustokakku2;
     *  i.hasNext() === false; 
     *  reseptit.lisaa(juustokakku2); 
     *  reseptit.tallenna(); 
     *  ftied.delete() === true; 
     *  File fbak = new File(tiedNimi+".bak"); 
     *  fbak.delete() === true; 
     *  dir.delete() === true; 
     * </pre>
     */
    public void lueTiedostosta(String tied) throws SailoException {
        setTiedostonPerusNimi(tied); 
        try ( BufferedReader fi = new BufferedReader(new FileReader(getTiedostonNimi())) ) {
            String rivi;
            while((rivi = fi.readLine()) != null) {
                rivi = rivi.trim();
                if ( "".equals(rivi) || rivi.charAt(0) == ';' ) continue;
                Resepti resepti = new Resepti();
                resepti.parse(rivi);
                lisaa(resepti);
            }
            muutettu = false;
        } catch ( FileNotFoundException e ) { 
            throw new SailoException("Tiedosto " + getTiedostonNimi() + " ei aukea");
        } catch( IOException e ) { 
            throw new SailoException("Ongelmia tiedoston kanssa: " + e.getMessage());
        }
    }
    
    /**
     * Tallentaa reseptit tiedostoon.
     * @throws SailoException jos tallennus epäonnistuu
     * Tiedoston muoto: 
     * <pre> 
     * Leivonta
     * 20 
     * ; kommenttirivi 
     * 1|Juustokakku|juustokakut||
     * 1|Juustokakku|juustokakut||
     * </pre>
     */
    public void tallenna() throws SailoException{
        if (!muutettu) return;
        File fbak = new File(getBakNimi());
        File ftied = new File(getTiedostonNimi()); 
        fbak.delete(); 
        ftied.renameTo(fbak); 
        
        try ( PrintWriter fo = new PrintWriter(new FileWriter(ftied.getCanonicalPath())) ) {
            for ( Resepti resepti : this)
                fo.println(resepti.toString()); 
        } catch ( FileNotFoundException ex ) { 
            throw new SailoException("Tiedosto " + ftied.getName() + " ei aukea"); 
        } catch ( IOException ex ) { 
            throw new SailoException("Tiedoston " + ftied.getName() + " kirjoittamisessa ongelmia"); 
        } 
        muutettu = false;
    }
    
    /**
     * Palauttaa hakuehtoon vastaavien reseptien viitteet
     * @param hakuehto hakuehto
     * @param k etsittävän kentän indexi
     * @return tietorakenne löytyneistä resepteistä
     * @example
     * <pre name="test">
     * #THROWS SailoException   
     * Reseptit reseptit = new Reseptit();
     * Resepti resepti1 = new Resepti(); resepti1.parse("1|Juustokakku|juustokakut||");
     * Resepti resepti2 = new Resepti(); resepti2.parse("2|Korvapuustit|pullat||");
     * Resepti resepti3 = new Resepti(); resepti3.parse("4|Prinsessakakku|täytekakut|Syntymäpäivät|");
     * reseptit.lisaa(resepti1);reseptit.lisaa(resepti2);reseptit.lisaa(resepti3);
     * </pre>
     */
    public Collection<Resepti> etsi(String hakuehto, int k) { 
        String ehto = "*";
        if(hakuehto != null && hakuehto.length() > 0) ehto = hakuehto;
        int hk = k;
        List<Resepti> loytyneet = new ArrayList<Resepti>();
        for(Resepti resepti: this) {
            if (WildChars.onkoSamat(resepti.anna(hk), ehto)) loytyneet.add(resepti);
        }
        Collections.sort(loytyneet, new Resepti.Vertailija(hk));  
        return loytyneet;
    }

    /**
     * Luetaan aikaisemmin annetusta tiedostosta
     * @throws SailoException poikkeuksen sattuessa
     */
    public void lueTiedostosta() throws SailoException {
        lueTiedostosta(getTiedostonPerusNimi()); 
    }
    
    /**
     * Asettaa tiedoston perusnimen ilman tarkenninta
     * @param nimi tallennustiedoston perusnimi
     */
    public void setTiedostonPerusNimi(String nimi) {
        tiedostonPerusNimi = nimi;
    }
    
    /**
     * Palauttaa tiedoston nimen, jota käytetty tallennukseen
     * @return tallennustiedoston nimi
     */
    public String getTiedostonPerusNimi() {
        return tiedostonPerusNimi;
    }
    
    /**
     * Palauttaa varakopiotiedoston nimen 
     * @return varakopiotiedoston nimi
     */
    private String getBakNimi() {
        return tiedostonPerusNimi + ".bak"; 
    }
    
    /**
     * Palauttaa tiedoston nimen, jota käytetään tallennukseen 
     * @return tallennustiedoston nimi
     */
    public String getTiedostonNimi() {
        return getTiedostonPerusNimi() + ".dat";
    }
    
    /**
     * Palauttaa reseptien lukumäärän
     * @return reseptien lukumäärä
     */
    public int getLkm() {
        return lkm;
    }
    
    /**
     * Korvaa reseptin tietorakenteessa.  Ottaa omistukseensa.
     * Etsitään samalla tunnusnumerolla olevaresepti.  Jos ei löydy,
     * niin lisätään uutena reseptinä.
     * @param resepti lisättävän reseptin viite
     * @throws SailoException jos epäonnistuu
     * @example
     * <pre name="test">
     * #THROWS SailoException,CloneNotSupportedException
     * #PACKAGEIMPORT
     * Reseptit reseptit = new Reseptit();
     * Resepti juustokakku = new Resepti(), juustokakku2 = new Resepti();
     * juustokakku.rekisteroi(); juustokakku2.rekisteroi();
     * reseptit.getLkm() === 0;
     * reseptit.korvaaTaiLisaa(juustokakku); reseptit.getLkm() === 1;
     * reseptit.korvaaTaiLisaa(juustokakku2); reseptit.getLkm() === 2;
     * Resepti juustokakku3 = juustokakku.clone();
     * Iterator<Resepti> it = reseptit.iterator();
     * it.next() == juustokakku === true;
     * reseptit.korvaaTaiLisaa(juustokakku3); reseptit.getLkm() === 2;
     * it = reseptit.iterator();
     * Resepti r0 = it.next();
     * r0 === juustokakku3;
     * r0 == juustokakku3 === true;
     * r0 == juustokakku === false;
     * </pre>
     */
    public void korvaaTaiLisaa(Resepti resepti) throws SailoException {
        int id = resepti.getTunnusNro();
        for (int i = 0; i < lkm; i++) {
            if ( alkiot[i].getTunnusNro() == id ) {
                alkiot[i] = resepti;
                muutettu = true;
                return;
            }
        }
        lisaa(resepti);
    }
    
    /**
     * Poistaa jäsenen jolla on valittu tunnusnumero 
     * @param id poistettavan jäsenen tunnusnumero
     * @return 1 jos poistettiin, 0 jos ei löydy
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * Reseptit reseptit = new Reseptit();
     * Resepti juustokakku = new Resepti(), juustokakku2 = new Resepti(), juustokakku3 = new Resepti();
     * juustokakku.rekisteroi(); juustokakku2.rekisteroi(); juustokakku3.rekisteroi();
     * int id1 = juustokakku.getTunnusNro();
     * reseptit.lisaa(juustokakku); reseptit.lisaa(juustokakku2); reseptit.lisaa(juustokakku3);
     * reseptit.poista(id1+1) === 1;
     * reseptit.annaId(id1+1) === null; reseptit.getLkm() === 2;
     * reseptit.poista(id1) === 1; reseptit.getLkm() === 1;
     * reseptit.poista(id1+3) === 0; reseptit.getLkm() === 1;
     * </pre> 
     */ 
    public int poista(int id) { 
        int ind = etsiId(id); 
        if (ind < 0) return 0; 
        lkm--; 
        for (int i = ind; i < lkm; i++) 
            alkiot[i] = alkiot[i + 1]; 
        alkiot[lkm] = null; 
        muutettu = true; 
        return 1; 
    } 
    
    /**
     * Etsii reseptin id:n perusteella
     * @param id tunnusnumero, jonka mukaan etsitään
     * @return resepti jolla etsittävä id tai null
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     *  Reseptit reseptit = new Reseptit();
     *  Resepti juustokakku = new Resepti(), juustokakku2 = new Resepti(), juustokakku3 = new Resepti();
     *  juustokakku.rekisteroi(); juustokakku2.rekisteroi(); juustokakku3.rekisteroi();
     *  int id1 = juustokakku.getTunnusNro();
     *  reseptit.lisaa(juustokakku); reseptit.lisaa(juustokakku2); reseptit.lisaa(juustokakku3);
     *  reseptit.annaId(id1) == juustokakku === true;
     *  reseptit.annaId(id1+1) == juustokakku2 === true;
     *  reseptit.annaId(id1+2) == juustokakku3 === true;
     * </pre>
     */ 
    public Resepti annaId(int id) { 
        for (Resepti resepti : this) { 
            if (id == resepti.getTunnusNro()) return resepti; 
        } 
        return null; 
    } 
    /**
     * Etsii reseptin id:n perusteella
     * @param id tunnusnumero, jonka mukaan etsitään
     * @return löytyneen reseptin indeksi tai -1 jos ei löydy
     * @example
     * <pre name="test">
     *  #THROWS SailoException 
     *  Reseptit reseptit = new Reseptit();
     *  Resepti juustokakku = new Resepti(), juustokakku2 = new Resepti(), juustokakku3 = new Resepti();
     *  juustokakku.rekisteroi(); juustokakku2.rekisteroi(); juustokakku3.rekisteroi();
     *  int id1 = juustokakku.getTunnusNro();
     *  reseptit.lisaa(juustokakku); reseptit.lisaa(juustokakku2); reseptit.lisaa(juustokakku3);
     *  reseptit.etsiId(id1+1) === 1;
     *  reseptit.etsiId(id1+2) === 2;
     * </pre>
     */ 
    public int etsiId(int id) { 
        for (int i = 0; i < lkm; i++) 
            if (id == alkiot[i].getTunnusNro()) return i; 
        return -1; 
    } 
    
    /**
     * Testataan luokkaa
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Reseptit reseptit = new Reseptit();
        Resepti juustokakku = new Resepti();
        Resepti juustokakku2 = new Resepti();
        juustokakku.rekisteroi();
        juustokakku.vastaaJuustokakku();
        juustokakku2.rekisteroi();
        juustokakku2.vastaaJuustokakku();
        
        try {
            reseptit.lisaa(juustokakku);
            reseptit.lisaa(juustokakku2);
            System.out.println("============= Reseptit testi =================");
            
            int i = 0;
            for(Resepti resepti: reseptit) {
                System.out.println("Resepti nro: " + i++);
                resepti.tulosta(System.out);
            }
        } catch (SailoException ex) {
            System.out.println(ex.getMessage());
        }
        
    }

}
