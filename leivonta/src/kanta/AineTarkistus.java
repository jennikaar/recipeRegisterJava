package kanta;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Luokka ainesosien tarkistamiseksi
 * @author Jenni Kääriäinen
 * @version 17.4.2019
 *
 */
public class AineTarkistus {
    
    /**
     * Tarkistetaan ainesosat.  Sallitaan myös muoto jossa vain syntymäaika.
     * @param ainesosat joka tutkitaan.
     * @return null jos oikein, muuten virhettä kuvaava teksti
     * @example
     * <pre name="test">
     * #PACKAGEIMPORT
     * AineTarkistus aineet = new AineTarkistus();
     * aineet.tarkista("") === "lisää ainesosa!";
     * aineet.tarkista("2dl maitoa,jauhoja") === "Täytyy lisätä määrä";
     * aineet.tarkista("50g voita,200g keksejä") === null;
     * </pre>
     */ 
    public String tarkista(String ainesosat) {
        String[] ainesosa = ainesosat.toString().split("\\,");
        if (ainesosa.length < 2) return "lisää ainesosa!";
        for(int i=0;i<ainesosa.length;i++) {
            String[] ainejamaara = ainesosa[i].toString().split(" ");
            if(ainejamaara.length > 0 && Mjonot.erotaInt(ainejamaara[0], 0) == 0) return "Täytyy lisätä määrä";
        }
        return null;
    }

}
