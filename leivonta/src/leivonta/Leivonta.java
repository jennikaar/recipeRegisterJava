package leivonta;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Leivonta-luokka joka huolehtii resepteistä.
 * @author Jenni Kääriäinen
 * @version 22.4.2019
 * 
 * Testien alustus
 * @example
 * <pre name="testJAVA">
 * #import leivonta.SailoException;
 *  private Leivonta leivonta;
 *  private Resepti juustokakku;
 *  private Resepti juustokakku2;
 *  private int rid1;
 *  private int rid2;
 *  private Ohje ohje1;
 *  private Ohje ohje2;
 *  private Ohje ohje3; 
 *  private Ohje ohje4; 
 *  private Ohje ohje5;
 *  
 *  public void alustaLeivonta() {
 *    leivonta = new Leivonta();
 *    juustokakku = new Resepti(); juustokakku.vastaaJuustokakku(); juustokakku.rekisteroi();
 *    juustokakku2 = new Resepti(); juustokakku2.vastaaJuustokakku(); juustokakku2.rekisteroi();
 *    rid1 = juustokakku.getTunnusNro();
 *    rid2 = juustokakku2.getTunnusNro();
 *    ohje1 = new Ohje(rid2); ohje1.vastaaPerinteinen(rid2);
 *    ohje2 = new Ohje(rid1); ohje2.vastaaPerinteinen(rid1);
 *    ohje3 = new Ohje(rid2); ohje3.vastaaPerinteinen(rid2); 
 *    ohje4 = new Ohje(rid1); ohje4.vastaaPerinteinen(rid1); 
 *    ohje5 = new Ohje(rid2); ohje5.vastaaPerinteinen(rid2);
 *    try {
 *    leivonta.lisaa(juustokakku);
 *    leivonta.lisaa(juustokakku2);
 *    leivonta.lisaa(ohje1);
 *    leivonta.lisaa(ohje2);
 *    leivonta.lisaa(ohje3);
 *    leivonta.lisaa(ohje4);
 *    leivonta.lisaa(ohje5);
 *    } catch ( Exception e) {
 *       System.err.println(e.getMessage());
 *    }
 *  }
 * </pre>
 */
public class Leivonta {
    
    private Reseptit reseptit = new Reseptit();
    private Ohjeet ohjeet = new Ohjeet();
    private Allergiat allergiat = new Allergiat();
    
    /**
     * Lisää leivonta-rekisteriin uuden reseptin
     * @param resepti lisättävä resepti
     * @throws SailoException poikkeus, jos lisäys ei onnistu
     * @example
     * <pre name="test">
     * #THROWS SailoException
     *  alustaLeivonta();
     *  leivonta.etsi("*",0).size() === 2;
     *  leivonta.lisaa(juustokakku);
     *  leivonta.etsi("*",0).size() === 3;
     * </pre>
     */
    public void lisaa(Resepti resepti) throws SailoException{
        reseptit.lisaa(resepti);
    }
    
    /**
     * Palauttaa hakuehtoon vastaavien reseptien viitteet
     * @param hakuehto hakuehto
     * @param k etsittävän kentän indeksi
     * @return tietorakenne löytyneistä resepteistä
     * @throws SailoException jos epäonnistuu
     * @example
     * <pre name="test">
     *  #THROWS CloneNotSupportedException, SailoException
     *   alustaLeivonta();
     *   Resepti resepti3 = new Resepti(); resepti3.rekisteroi();
     *   resepti3.vastaaJuustokakku();
     *   leivonta.lisaa(resepti3);
     *   Collection<Resepti> loytyneet = leivonta.etsi("*Juustokakku*",1);
     *   loytyneet.size() === 3;
     *   Iterator<Resepti> it = loytyneet.iterator();
     *   it.next();
     *   it.next();
     *   it.next() == resepti3 === true;
     * </pre>
     */
    public Collection<Resepti> etsi(String hakuehto, int k) throws SailoException {
        return reseptit.etsi(hakuehto, k);
    }
    
    /**
     * Lisätään uusi ohje reseptiin
     * @param ohje mikä ohje lisätään
     * @throws SailoException epäonnistuessa
     */
    public void lisaa(Ohje ohje) throws SailoException{
        ohjeet.lisaa(ohje);
    }
    
    /**
     * Lisätään uusi allergia ohjeeseen
     * @param allergia mikä allergia lisätään
     */
    public void lisaa(Allergia allergia) {
        allergiat.lisaa(allergia);
    }
    
    
    /**
     * Poistaa reseptin tiedot
     * @param resepti mikä resepti poistetaan
     * @return montako poistetaan
     * @example
     * <pre name="test">
     * #THROWS Exception
     * alustaLeivonta();
     * leivonta.etsi("*",0).size() === 2;
     * leivonta.annaOhjeet(juustokakku).size() === 2;
     * leivonta.poista(juustokakku) === 1;
     * leivonta.etsi("*",0).size() === 1;
     * leivonta.annaOhjeet(juustokakku).size() === 0;
     * leivonta.annaOhjeet(juustokakku2).size() === 3;
     * </pre>
     */
    public int poista(Resepti resepti) {
        if(resepti == null) return 0;
        int ret = reseptit.poista(resepti.getTunnusNro());  
        ohjeet.poistaReseptinOhjeet(resepti.getTunnusNro());  
        return ret;  
    }
    
    /**
     * Poistaa ohjeen tiedot
     * @param ohje poistettava ohje
     * @example
     * <pre name="test">
     * #THROWS Exception
     *  alustaLeivonta();
     *  leivonta.annaOhjeet(juustokakku).size() === 2;
     *  leivonta.poista(ohje2);
     *  leivonta.annaOhjeet(juustokakku).size() === 1;
     * </pre>
     */
    public void poista(Ohje ohje) {
        ohjeet.poista(ohje);
    }
    
    /**
     * Lukee leivonta-rekisterin tiedot tiedostosta
     * @param nimi jota käytetään lukemisessa
     * @throws SailoException jos ei onnistu
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * #import java.io.*;
     * #import java.util.*;
     * 
     * Leivonta leivonta = new Leivonta();
     * Resepti juustokakku1 = new Resepti(); 
     * juustokakku1.vastaaJuustokakku(); 
     * juustokakku1.rekisteroi();
     * 
     * Resepti juustokakku2 = new Resepti();
     * juustokakku2.vastaaJuustokakku(); 
     * juustokakku2.rekisteroi();
     * 
     * Ohje perinteinen1 = new Ohje(); 
     * perinteinen1.vastaaPerinteinen(juustokakku2.getTunnusNro());
     * Ohje perinteinen2 = new Ohje(); 
     * perinteinen2.vastaaPerinteinen(juustokakku1.getTunnusNro());
     * Ohje perinteinen3 = new Ohje(); 
     * perinteinen3.vastaaPerinteinen(juustokakku2.getTunnusNro());
     * 
     * Allergia al1 = new Allergia(); al1.vastaaPahkinaton(); 
     * Allergia al2 = new Allergia(); al2.vastaaPahkinaton();
     * Allergia al3 = new Allergia(); al3.vastaaPahkinaton();
     * 
     * String hakemisto = "testileivontakirja";
     * File dir = new File(hakemisto);
     * File frtied = new File(hakemisto+"/reseptit.dat");
     * File fltied = new File(hakemisto+ "/allergiat.dat");
     * File fotied = new File(hakemisto+ "/ohjeet.dat");
     * 
     * dir.mkdir();
     * frtied.delete(); 
     * fotied.delete();
     * fltied.delete();
     * 
     * leivonta.lueTiedostosta(hakemisto); #THROWS SailoException
     * 
     * leivonta.lisaa(juustokakku1);
     * leivonta.lisaa(juustokakku2);
     * leivonta.lisaa(perinteinen1); 
     * leivonta.lisaa(perinteinen2); 
     * leivonta.lisaa(perinteinen3);
     * leivonta.lisaa(al1);
     * leivonta.lisaa(al2);
     * leivonta.lisaa(al3); 
     * leivonta.tallenna(); 
     * 
     * leivonta = new Leivonta();
     * leivonta.lueTiedostosta(hakemisto);
     * 
     * Collection<Resepti> kaikki = leivonta.etsi("",-1);
     * Iterator<Resepti> it = kaikki.iterator();
     * it.next() === juustokakku2;
     * it.hasNext() === false;
     * 
     * List<Ohje> loytyneet = leivonta.annaOhjeet(juustokakku1);
     * Iterator<Ohje> io = loytyneet.iterator();
     * io.next() === perinteinen2;
     * io.hasNext() === false;
     * 
     * loytyneet = leivonta.annaOhjeet(juustokakku2);
     * io = loytyneet.iterator();
     * io.next() === perinteinen1;
     * io.next() === perinteinen3;
     * io.hasNext() === false;
     * 
     * leivonta.lisaa(juustokakku2);
     * leivonta.lisaa(perinteinen3);
     * leivonta.lisaa(al3);
     * leivonta.tallenna();
     * 
     * frtied.delete() === true;
     * fotied.delete() === true;
     * fltied.delete() === true;
     * 
     * File frbak = new File(hakemisto+"/reseptit.bak");
     * File fobak = new File(hakemisto+"/ohjeet.bak");
     * File flbak = new File(hakemisto+"/allergiat.bak");
     * 
     * frbak.delete() === true;
     * fobak.delete() === true;
     * flbak.delete() === true;
     * dir.delete() === true;
     * </pre>
     */
    public void lueTiedostosta(String nimi) throws SailoException {
        reseptit = new Reseptit();
        ohjeet = new Ohjeet();
        allergiat = new Allergiat();
        
        setTiedosto(nimi);
        reseptit.lueTiedostosta();
        ohjeet.lueTiedostosta();
        allergiat.lueTiedostosta();
    }
    
    /**
     * Asettaa tiedostojen perusnimet
     * @param nimi uusi nimi
     */
    public void setTiedosto(String nimi) {
        File dir = new File(nimi);
        dir.mkdirs();
        String hakemistonNimi = "";
        if (!nimi.isEmpty()) hakemistonNimi = nimi + "/";
        reseptit.setTiedostonPerusNimi(hakemistonNimi + "reseptit");
        ohjeet.setTiedostonPerusnimi(hakemistonNimi + "ohjeet");
        allergiat.setTiedostonPerusnimi(hakemistonNimi + "allergiat");
    }
    
    /**
     * Tallentaa leivonta-rekisterin tiedot tiedostoon
     * @throws SailoException jos ei onnistu
     */
    public void tallenna() throws SailoException{
        String virhe = "";
        try {
            reseptit.tallenna();
        } catch (SailoException ex) {
            virhe = ex.getMessage();
        }
        try {
            ohjeet.tallenna();
        } catch (SailoException ex) {
            virhe += ex.getMessage();
        }
        try {
            allergiat.tallenna();
        } catch (SailoException ex) {
            virhe += ex.getMessage();
        }
        if( !"".contentEquals(virhe)) throw new SailoException(virhe);
    }
    
    /**
     * @param resepti jonka ohjeet halutaan
     * @return tietyn reseptin ohjeet
     * @throws SailoException jos epäonnistutaan
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * #import java.util.*;
     * 
     *  Leivonta leivonta = new Leivonta();
     *  Resepti korvapuusti = new Resepti(), korvapuusti2 = new Resepti(), juustokakku= new Resepti();
     *  korvapuusti.rekisteroi(); korvapuusti2.rekisteroi(); juustokakku.rekisteroi();
     *  int id1 = korvapuusti.getTunnusNro();
     *  int id2 = juustokakku.getTunnusNro();
     *  
     *  Ohje perinteinen1 = new Ohje(id1); leivonta.lisaa(perinteinen1);
     *  Ohje perinteinen2 = new Ohje(id2); leivonta.lisaa(perinteinen2);
     *  Ohje gluteeniton = new Ohje(id1); leivonta.lisaa(gluteeniton);
     * 
     * List<Ohje> loytyneet;
     * loytyneet = leivonta.annaOhjeet(korvapuusti2);
     * loytyneet.size() === 0;
     * loytyneet = leivonta.annaOhjeet(juustokakku);
     * loytyneet.size() === 1;
     * loytyneet.get(0) == perinteinen2 === true;
     * loytyneet = leivonta.annaOhjeet(korvapuusti);
     * loytyneet.size() === 2;
     * loytyneet.get(0) == perinteinen1 === true;
     * loytyneet.get(1) == gluteeniton === true;
     * 
     * </pre>
     */
    public List<Ohje> annaOhjeet(Resepti resepti) throws SailoException {
        return ohjeet.annaOhjeet(resepti.getTunnusNro());
    }
    
    /**
     * @param tunnusnro millä tunnusnumerolla haetaan
     * @return allergia ohjeelle
     */
    public Allergia annaAllergia(int tunnusnro) {
        return allergiat.annaAllergia(tunnusnro);
    }
    
    /**
     * Korvaa reseptin tietorakenteessa.  Ottaa omistukseensa.
     * Etsitään samalla tunnusnumerolla olevaresepti.  Jos ei löydy,
     * niin lisätään uutena reseptinä.
     * @param resepti lisättävän reseptin viite
     * @throws SailoException jos epäonnistuu
     * @example
     * <pre name="test">
     *  #THROWS SailoException  
     *  alustaLeivonta();
     *  leivonta.etsi("*",0).size() === 2;
     *  leivonta.korvaaTaiLisaa(juustokakku);
     *  leivonta.etsi("*",0).size() === 2;
     * </pre>
     */
    public void korvaaTaiLisaa(Resepti resepti) throws SailoException {
        reseptit.korvaaTaiLisaa(resepti);  
    }
    
    /**
     * Korvaa ohjeen tietorakenteessa. Ottaa omistukseensa.
     * Etsitään samalla tunnusnumerolla oleva ohje. Jos ei löyty, niin lisätään uutena ohjeena
     * @param ohje lisättävän ohjeen viite
     * @throws SailoException jos epäonnistutaan 
     */
    public void korvaaTaiLisaa(Ohje ohje) throws SailoException {
        ohjeet.korvaaTaiLisaa(ohje);
    }
    
    
    /**
     * Testiohjelma leivonnasta
     * @param args ei käytössä
     */
    public static void main(String args[]) {
        Leivonta leivonta = new Leivonta();

        try {

            Resepti juustokakku = new Resepti();
            Resepti juustokakku2 = new Resepti();
            juustokakku.rekisteroi();
            juustokakku.vastaaJuustokakku();
            juustokakku2.rekisteroi();
            juustokakku2.vastaaJuustokakku();

            leivonta.lisaa(juustokakku);
            leivonta.lisaa(juustokakku2);
            
            int id1 = juustokakku.getTunnusNro();
            int id2 = juustokakku2.getTunnusNro();
            Allergia pahkinaton = new Allergia(); pahkinaton.rekisteroi();
            Allergia pahkinaton2 = new Allergia(); pahkinaton2.rekisteroi();
            pahkinaton.vastaaPahkinaton(); leivonta.lisaa(pahkinaton); 
            pahkinaton2.vastaaPahkinaton(); leivonta.lisaa(pahkinaton2);
            int id3 = pahkinaton.getTunnusNro();
            int id4 = pahkinaton2.getTunnusNro();
            Ohje perinteinen = new Ohje(id1); perinteinen.rekisteroi();
            perinteinen.vastaaPerinteinen(id1,id3); leivonta.lisaa(perinteinen);
            Ohje perinteinen2 = new Ohje(id2); perinteinen2.rekisteroi();
            perinteinen2.vastaaPerinteinen(id2,id3);leivonta.lisaa(perinteinen2);
            Ohje gluteeniton = new Ohje(id1); gluteeniton.rekisteroi(); 
            gluteeniton.vastaaPerinteinen(id1,id4); leivonta.lisaa(gluteeniton);

            System.out.println("============= Leivonnan testi =================");
                Collection<Resepti> reseptit = leivonta.etsi("",-1);
                int i = 0;
                for (Resepti resepti : reseptit) {
                    System.out.println("Resepti paikassa: " + i);
                    resepti.tulosta(System.out);
                    List<Ohje> loytyneet = leivonta.annaOhjeet(resepti);
                    for (Ohje ohje : loytyneet) {
                        ohje.tulosta(System.out);
                        Allergia allergia = leivonta.annaAllergia(ohje.getAllergiaNro());
                        if ( allergia != null) {
                            allergia.tulosta(System.out);
                        }
                    }
                    i++;
                }

        } catch (SailoException ex) {
            System.out.println(ex.getMessage());
        }
    }


}
