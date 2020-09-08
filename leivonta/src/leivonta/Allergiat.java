package leivonta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Allergiat luokka, joka osaa mm. lisätä allergian ohjeeseen,
 * hakea tietyn ohjeen allergian.
 * Tietorakenne tehty listoilla.
 * Iteraattori rajapinta.
 * @author Jenni Kääriäinen
 * @version 22.4.2019
 */
public class Allergiat implements Iterable<Allergia> {
    
    private boolean muutettu = false;
    private String tiedostonPerusnimi = "";
    
    /*Taulukko allergioista*/
    private final Collection<Allergia> alkiot = new ArrayList<Allergia>();
    
    /**
     * Allergioiden alustaminen
     */
    public Allergiat() {
        //Ei vielä mitään
    }
    
    /**
     * Lisää uuden allergian tietorakenteeseen ja ottaa allergian omistukseensa
     * @param allergia mikä lisätään
     */
    public void lisaa(Allergia allergia) {
        alkiot.add(allergia);
        muutettu = true;
    }
    
    /**
     * Tiedoston lukemiseen. 
     * @param tied tiedoston nimen alkuosa 
     * @throws SailoException jos ei onnistuta lukemaan tiedostoa
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * #import java.io.File; 
     * Allergiat allergiat = new Allergiat();
     * Allergia al1 = new Allergia(); al1.vastaaPahkinaton();
     * Allergia al2 = new Allergia(); al2.vastaaPahkinaton();
     * Allergia al3 = new Allergia(); al3.vastaaPahkinaton();
     * String tiedNimi = "testileivonta";
     * File ftied = new File(tiedNimi+".dat");
     * ftied.delete();
     * allergiat.lueTiedostosta(tiedNimi); #THROWS SailoException
     * allergiat.lisaa(al1);
     * allergiat.lisaa(al2);
     * allergiat.lisaa(al3);
     * allergiat.tallenna();
     * allergiat = new Allergiat();
     * allergiat.lueTiedostosta(tiedNimi);
     * Iterator<Allergia> i = allergiat.iterator();
     * i.next().toString() === al1.toString();
     * i.next().toString() === al2.toString();
     * i.next().toString() === al3.toString();
     * i.hasNext() === false;
     * allergiat.lisaa(al1);
     * allergiat.tallenna();
     * ftied.delete() === true;
     * File fbak = new File(tiedNimi+".bak");
     * fbak.delete() === true;
     * </pre>
     */
    public void lueTiedostosta(String tied) throws SailoException {
        setTiedostonPerusnimi(tied);
        
        try (BufferedReader fi = new BufferedReader(new FileReader(getTiedostonNimi()))) {
            String rivi;
            while ((rivi = fi.readLine()) != null) {
                rivi = rivi.trim();
                if ( "".equals(rivi) || rivi.charAt(0) == ';') continue;
                Allergia al = new Allergia();
                al.parse(rivi);
                lisaa(al);
            }
            muutettu = false;
        } catch (FileNotFoundException e) {
            throw new SailoException("Tiedosto " + getTiedostonNimi() + " ei aukea");
        } catch (IOException e) {
            throw new SailoException("Ongelmia tiedoston kanssa: " + e.getMessage()); 
        }
    }
    
    /**
     * hakee tiedoston perusnimen
     * @return tiedoston perusnimen
     */
    public String getTiedostonNimi() {
        return tiedostonPerusnimi + ".dat";
    }
    
    /**
     * Asettaa tiedoston perusnimen
     * @param tied tallennustiedoston perusnimi
     */
    public void setTiedostonPerusnimi(String tied) {
        tiedostonPerusnimi = tied;
    }
    
    /**
     * Palauttaa varakopiotiedoston nimen
     * @return varakopion nimi
     */
    public String getBakNimi() {
        return tiedostonPerusnimi + ".bak";
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
            
            try ( PrintWriter fo = new PrintWriter(new FileWriter(ftied.getCanonicalPath()))) {
                for (Allergia al : this) {
                    fo.println(al.toString());
                }
            } catch (FileNotFoundException ex) {
                throw new SailoException("Tiedosto " + ftied.getName() + " ei aukea"); 
            } catch ( IOException ex ) {
                throw new SailoException("Tiedoston " + ftied.getName() + " kirjoittamisessa ongelmia"); 
            }
            muutettu = false;
        }
    
    /**
     * Lukee edellisenä annetun tiedoston
     * @throws SailoException poikkeuksen tapahtuessa
     */
    public void lueTiedostosta() throws SailoException{
        lueTiedostosta(getTiedostonPerusNimi());
    }
    
    
    /**
     * Palauttaa tiedoston nimen, jota tallennukseen käytetään
     * @return tiedoston perusnimen
     */
    public String getTiedostonPerusNimi() {
        return tiedostonPerusnimi;
    }
    
    /**
     * Palauttaa allergioiden lukumäärän leivonta-"kirjassa"
     * @return allergiat lukumäärä
     */
    public int getLkm() {
        return alkiot.size();
    }
    
    /**
     * Iteraattori allergioiden läpikäymiseen
     * @return allergiaiteraattori
     * @example
     * <pre name="test">
     * #PACKAGEIMPORT
     * #import java.util.*;
     * 
     * Allergiat allergiat = new Allergiat();
     * Allergia pahkinaton = new Allergia(); allergiat.lisaa(pahkinaton);
     * Allergia pahkinaton2 = new Allergia();allergiat.lisaa(pahkinaton2);
     * Allergia munaton = new Allergia(); allergiat.lisaa(munaton);
     * 
     * Iterator<Allergia> i2 = allergiat.iterator();
     * i2.next() === pahkinaton;
     * i2.next() === pahkinaton2;
     * i2.next() === munaton;
     * i2.next() === munaton; #THROWS NoSuchElementException
     * </pre>
     */
    @Override
    public Iterator<Allergia> iterator(){
        return alkiot.iterator();
    }
    
    /**
     * Haetaan tietyllä tunnusnumerolla olevaa allergiaa
     * @param tunnusnumero millä numerolla haetaan
     * @return allergia jota etsittiin
     */
     public Allergia annaAllergia(int tunnusnumero) {
        for (Allergia al : alkiot) {
            if (tunnusnumero == al.getTunnusNro()) return al;
        }
        return null;
     }
    
    
    /**
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Allergiat allergiat = new Allergiat();
        Allergia pahkinaton = new Allergia();
        pahkinaton.rekisteroi();
        pahkinaton.vastaaPahkinaton();
        Allergia pahkinaton2 = new Allergia();
        pahkinaton2.rekisteroi();
        pahkinaton2.vastaaPahkinaton();
        Allergia munaton = new Allergia();
        munaton.rekisteroi();
        munaton.vastaaPahkinaton();
        
        allergiat.lisaa(pahkinaton);
        allergiat.lisaa(pahkinaton2);
        allergiat.lisaa(munaton);
        
        /*Kokeillaan osaako tulostaa allergiat*/
        System.out.println("============= Allergiat testi =================");
        
        for (Allergia al : allergiat) {
            System.out.println("Allergia nro: " + al.getTunnusNro());
            al.tulosta(System.out);
        }
        
    }
}
