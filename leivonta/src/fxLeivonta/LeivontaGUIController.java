package fxLeivonta;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.awt.Desktop;

import javafx.scene.control.TextArea;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ListChooser;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.StringGrid;
import fi.jyu.mit.fxgui.TextAreaOutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import leivonta.Allergia;
import leivonta.Leivonta;
import leivonta.Ohje;
import leivonta.Resepti;
import leivonta.SailoException;

/**
 * @author Jenni Kääriäinen
 * Käyttöliittymän tapahtumien hoitamiseksi leivontarekisterille
 * 
 * TODO: Allergia toimii kuten aiemmin, enteriä painamalla ohjeen kohdalla tulee pähkinätön.
 * Ei osata laittaa muita allergioita.
 * TODO: Osaa etsiä nyt nimen jne mukaan, valmistusajan mukaan ei osata (liittyy ohjeisiin) eikä allergian.
 * Ei varmuutta miten haluttaisiin edes toteuttaa (Missä sopivat ohjeet näkyisivät).
 * 
 * Suunnitelmasta poikkeavaa: Ohjetyyppiä ei voi kirjoittaa, ohjeen ainesosat täytyy kirjoittaa 
 * tekstialueeseen ja siten oikeellisuustarkistuskin hieman erilainen, kategoria ja teema muokataan 
 * reseptin eikä ohjeen muokkausikkunassa (järkevämpi), allergiaan voi lisätä vain pähkinättömän, 
 * muokkaaminen ei tapahdu suoraan pääikkunassa vaan avaa uuden ikkunan
 * 
 * @version 26.4.2019 
 */
public class LeivontaGUIController implements Initializable {
    
    @FXML private Label labelVirhe; 
    @FXML private ComboBoxChooser<String> cbKentat; //kenttien attribuutti
    @FXML private ComboBoxChooser<String> comboOhjeTyyppi;
    @FXML private TextField hakuehto; 
    @FXML private ListChooser<Resepti> chooserReseptit; 
    @FXML private TextArea areaResepti;
    @FXML private StringGrid<Ohje> tableAinesosat; 
    @FXML private TextField editNimi;
    @FXML private TextField editKategoria;
    @FXML private TextField editTeema;
    @FXML private TextField editValmistusaika;
    @FXML private TextField editAllergia;
    
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        alusta();
    }
    
    @FXML private void handleHakuEhto() {
        hae(0);
    }
    
    @FXML private void handleTallenna() {
        tallenna();
    }
    
    @FXML private void handleAvaa() {
        avaa();
    }
    
    @FXML private void handleTulosta() throws SailoException {
        TulostusController tulostusCtrl = TulostusController.tulosta(null); 
        tulostaValitut(tulostusCtrl.getTextArea()); 
    } 
    
    @FXML private void handleLopeta() {
        tallenna();
        Platform.exit();
    }
    
    @FXML private void handleUusiResepti() {
        uusiResepti();
    }
    
    @FXML private void handleMuokkaaResepti() {
        muokkaaResepti();
    }
	
    @FXML private void handlePoistaResepti() {
        poistaResepti(); 
    }
    
    @FXML private void handleUusiOhje() throws SailoException {
        uusiOhje();
    }
    
    @FXML private void handleUusiAllergia() {
        uusiAllergia();
    }
    
    @FXML private void handlePoistaOhje() {
        poistaOhje();
    }
    
    @FXML private void handleApua() {
        avustus();
    }
    
    @FXML private void handleTietoja() {
        ModalController.showModal(LeivontaGUIController.class.getResource("Leivontatiedot.fxml"), "Resepti", null, "");
    }
    
    @FXML private void handleOhjeValinta() {
        int k = comboOhjeTyyppi.getSelectionModel().getSelectedIndex();
        switch(k) {
        case 0: ohjeenValinta("Kaikki"); break;
        case 1: ohjeenValinta("Perinteinen"); break;
        case 2: ohjeenValinta("Gluteeniton"); break;
        case 3: ohjeenValinta("Munaton"); break;
        case 4: ohjeenValinta("Pähkinätön"); break;
        case 5: ohjeenValinta("Vegaani"); break;
        default:ohjeenValinta("Perinteinen");
        }
    }
    
    @FXML private void handleAvaaRekisteri() {
        ModalController.showModal(LeivontaGUIController.class.getResource("LeivontaGUIView.fxml"), "Resepti", null, "");
    }
    
// Tästä eteenpäin ei suoraan käyttöliittymään liittyvää
    
    private Leivonta leivonta;
    private Resepti reseptiKohdalla;
    private Allergia allergiaKohdalla;
    private Ohje ohjeKohdalla;
    private String reseptinnimi = "juustokakku";
    private TextField[] edits;
    private TextField[] edits2;
    private static Resepti apuResepti = new Resepti();
    
    /**
     * Asetetaan käytettävä leivontarekisteri
     * @param leivonta leivontarekisteri jota käytetään tässä käyttöliittymässä
     * @throws SailoException jos epäonnistutaan
     */
    public void setLeivonta(Leivonta leivonta) throws SailoException {
        this.leivonta = leivonta;
        naytaResepti();
    }
    
    /**
     * Alustetaan reseptin tiedot isolle tekstikentälle, fontti, tekstin sopivuus
     * Alustetaan reseptilistan kuuntelija
     */
    protected void alusta() {
        areaResepti.setFont(new Font("Courier New", 12));
        
        chooserReseptit.clear();
        chooserReseptit.addSelectionListener(e -> naytaResepti());
        
        edits = new TextField[]{editNimi, editKategoria, editTeema}; 
        edits2 = new TextField[] {editValmistusaika, editAllergia};
        
        tableAinesosat.setOnMouseClicked( e -> { if ( e.getClickCount() > 1 ) muokkaaOhjetta(); } );
        for(TextField edit:edits2) {
            edit.setOnMouseClicked( e -> { if ( e.getClickCount() > 1 ) muokkaaOhjetta(); } );
        }
        areaResepti.setOnMouseClicked( e -> { if ( e.getClickCount() > 1 ) muokkaaOhjetta(); } );
    }
    
    /**
     * Näyttää listasta valitun reseptin tiedot, tulostaa isoon edit-ikkunaan
     */
    protected void naytaResepti() {
        reseptiKohdalla = chooserReseptit.getSelectedObject(); //Palauttaa valitun olion tai null.
        
        if (reseptiKohdalla == null) {
            areaResepti.clear();
            return;
        }
        
        ReseptiDialogController.naytaResepti(edits, reseptiKohdalla); 
        //Näytetään valittuna oleva ohje
        int k = comboOhjeTyyppi.getSelectionModel().getSelectedIndex();
        switch(k) {
        case 0: ohjeenValinta("Kaikki"); break;
        case 1: ohjeenValinta("Perinteinen"); break;
        case 2: ohjeenValinta("Gluteeniton"); break;
        case 3: ohjeenValinta("Munaton"); break;
        case 4: ohjeenValinta("Pähkinätön"); break;
        case 5: ohjeenValinta("Vegaani"); break;
        default:ohjeenValinta("Perinteinen");
        }
    }
    
    /**
     * Näyttää valitun ohjeen tiedot
     */
    protected void naytaOhje() {
        if (ohjeKohdalla == null) areaResepti.setText("Lisää ohje!");
        else {
            OhjeDialogController.naytaOhje(edits2, areaResepti, ohjeKohdalla);
            if(ohjeKohdalla.getAllergiaNro() > 0) edits2[1].setText("Pähkinätön");
            naytaAinesosat(ohjeKohdalla);
        }
    }
    
    /**
     * Näytetään ainesosat
     * @param ohje jonka ainesosat näytetään
     */
    private void naytaAinesosat(Ohje ohje) {
        tableAinesosat.clear();
        if (ohje == null ) return;
       
        naytaAinesosa(ohje);
    }
 
    /**
     * Ainesosien näyttämiseen. Pilkotaan ohjeen tiedot sopiviin osiin ja asetetaan taulukkoon
     * @param oh ohje jonka ainesosia näytetään
     */
    private void naytaAinesosa(Ohje oh) {
        String[] rivi = oh.toString().split("\\|");
        String[] ainesosat = rivi[4].toString().split("\\,");
        for(int i=0;i<ainesosat.length;i++) {
            String[] ainejamaara = ainesosat[i].toString().split(" ");
            if (ainejamaara.length>1) tableAinesosat.add(oh, ainejamaara[0], ainejamaara[1]);
        }
    }
    
    /**
     * Näytetään virhe
     * @param virhe onko mitään haussa vai ei
     */
    protected void naytaVirhe(String virhe) {
        if ( virhe == null || virhe.isEmpty() ) {
            labelVirhe.setText("");
            labelVirhe.getStyleClass().removeAll("virhe");
            return;
        }
        labelVirhe.setText(virhe);
        labelVirhe.getStyleClass().add("virhe");
        }
    
    private void setTitle(String title) {
        ModalController.getStage(hakuehto).setTitle(title);
    }
    
    /**
     * Hakee reseptin tiedot listaan
     * @param rnro reseptin numero, joka haetaan
     */
    protected void hae(int rnro) {
        int rnr = rnro; //reseptin numero, joka aktivoidaan haun jälk.
        if(rnr<=0) {
            Resepti kohdalla = reseptiKohdalla;
            if(kohdalla != null) rnr = kohdalla.getTunnusNro();
        }
        
        int k = cbKentat.getSelectionModel().getSelectedIndex() + apuResepti.ekaKentta();
        String ehto = hakuehto.getText();
        if (ehto.indexOf('*') <0) ehto = "*" + ehto + "*";
        
        chooserReseptit.clear();
        
        //TODO: Valmistusajan ja Allergian haku
        int index = 0;
        Collection<Resepti> reseptit;
        try {
            reseptit = leivonta.etsi(ehto, k);
            int i = 0;
            for (Resepti resepti: reseptit) {
                if(resepti.getTunnusNro()== rnro) index = i;
                chooserReseptit.add(resepti.getNimi(), resepti);
                i++;
            }
        } catch (SailoException ex) {
            Dialogs.showMessageDialog("reseptin hakemisessa ongelmia!" + ex.getMessage());
        }
        chooserReseptit.setSelectedIndex(index); //laittaa valitun reseptin näkyväksi
    }
    
    /**
     * Tulostaa valitun reseptin tiedot
     * @param os tietovirta johon tulostetaan
     * @param resepti tulostettava
     */
    public void tulosta(PrintStream os, final Resepti resepti) {
        os.println("----------------------------------------------");
        resepti.tulosta(os);
        os.println("----------------------------------------------");
        List<Ohje> ohjeet;
        try {
            ohjeet = leivonta.annaOhjeet(resepti);
            for (Ohje ohj:ohjeet) {
                ohj.tulosta(os);
                Allergia allergia = leivonta.annaAllergia(ohj.getAllergiaNro());
                if ( allergia != null) {
                    allergia.tulosta(os);
                }
            }
        } catch (SailoException ex) {
           Dialogs.showMessageDialog("Ohjeiden hakemisessa on ongelma" + ex.getMessage());
        } 
    }
    
    
    /**
     * Tulostaa listassa olevat reseptit tekstialueeseen
     * @param text alue johon tulostetaan
     * @throws SailoException jos epäonnistutaan
     */
    public void tulostaValitut(TextArea text) throws SailoException {
        try (PrintStream os = TextAreaOutputStream.getTextPrintStream(text)) {
            os.println("Tulostetaan kaikki reseptit");
            Collection<Resepti> reseptit = leivonta.etsi("", -1);
            for(Resepti resepti : reseptit) {
                tulosta(os, resepti);
                os.println("\n\n");
            }
        } catch (SailoException ex) {
            Dialogs.showMessageDialog("Reseptin hakemisessa ongelmia!" + ex.getMessage());
        }
        
    }
    
    /**
     * Lisää uuden reseptin editointia varten
     */
    protected void uusiResepti() {
        try {
            Resepti uusi = new Resepti();
            uusi = ReseptiDialogController.kysyResepti(null, uusi); 
            if ( uusi == null) return;
            uusi.rekisteroi();
           leivonta.lisaa(uusi);
           hae(uusi.getTunnusNro());
        } catch (SailoException e) {
            Dialogs.showMessageDialog("Ongelma uuden reseptin luomisessa" + e.getMessage());
            return;
        }
    }
    
    /**
     * Poistetaan valittu resepti ja kysytään ensin halutaanko poistaa
     */
    private void poistaResepti() {
        Resepti resepti = reseptiKohdalla;
        if (resepti == null ) return;
        if ( !Dialogs.showQuestionDialog("Poisto", "Poistetaanko resepti: " + resepti.getNimi(), "Kyllä", "Ei") )
            return;
        leivonta.poista(reseptiKohdalla);
        int index = chooserReseptit.getSelectedIndex(); 
        hae(0);
        chooserReseptit.setSelectedIndex(index); 
    }
    
    private void poistaOhje() {
        Ohje ohje = ohjeKohdalla;
        if(ohje == null) return;
        if (!Dialogs.showQuestionDialog("Poisto", "Poistetaanko ohje: " + ohje.getOhjeTyyppi(), "Kyllä", "Ei") )
            return;
        leivonta.poista(ohjeKohdalla);
        int index = chooserReseptit.getSelectedIndex(); 
        hae(0);
        chooserReseptit.setSelectedIndex(index); 
    }
    
    /**
     * Tekee uuden ohjeen editointia varten
     * @throws SailoException jos epäonnistutaan
     */
    public void uusiOhje() throws SailoException { 
        if (reseptiKohdalla == null ) {
            virheviesti("Ei voida lisätä ohjetta ilman reseptiä!");
            return;
        }
        
        try {
            Ohje ohje = new Ohje(reseptiKohdalla.getTunnusNro());
            ohje = OhjeDialogController.kysyOhje(null, ohje);
            if (ohje == null ) return;
            ohje.rekisteroi();
            leivonta.lisaa(ohje);
            ohjeKohdalla = ohje;
            areaResepti.setText(ohje.getOhje());
        } catch (SailoException e) {
            Dialogs.showMessageDialog("Ongelmia lisäämisessä!" + e.getMessage());
        }
        hae(reseptiKohdalla.getTunnusNro());
    }
    
    /**
     * Ohjeiden näyttämistä varten
     * @param resepti jonka ohjeet näytetään
     */
    private void naytaOhjeet(Resepti resepti) {
        areaResepti.clear();
        tableAinesosat.clear();
        for (TextField edit :edits2) {
            edit.clear();
        }
        if ( resepti == null ) return;
        
        try {
            List<Ohje> ohjeet = leivonta.annaOhjeet(resepti);
            if ( ohjeet.size() == 0 ) return;
            for (Ohje ohje: ohjeet) {
                try(PrintStream os = TextAreaOutputStream.getTextPrintStream(areaResepti)){
                    tulostaOhje(os, ohje);
                }
            }
        } catch (SailoException e) {
            naytaVirhe(e.getMessage());
        } 
    }
    
     /**
     * Ohjeen näyttämistä varten
     * @param s valittu ohje
     */
    public void ohjeenValinta(String s) {
        ohjeKohdalla = null;
        if (s.equalsIgnoreCase("Kaikki")) {
            naytaOhjeet(reseptiKohdalla);
            return;
        }
        try {
            List<Ohje> ohjeet = leivonta.annaOhjeet(reseptiKohdalla);
            for (Ohje ohje: ohjeet) {
                if(ohje.getOhjeTyyppi().equalsIgnoreCase(s)) {
                    ohjeKohdalla = ohje;
                }
            }
        } catch (SailoException e) {
            e.getMessage();
        }
        naytaOhje();
    } 
    
    /**
     * Jotta tulostaisi ohjeen isolle tekstiosalle
     * @param os tietovirta
     * @param ohje jota halutaan tulostaa
     */
    public void tulostaOhje(PrintStream os, final Ohje ohje) {
        if(ohje != null) {
            ohje.tulosta(os);
            Allergia allergia = leivonta.annaAllergia(ohje.getAllergiaNro());
            if ( allergia != null) {
                allergia.tulosta(os);
            }
        }
        else areaResepti.setText("Lisää ohje");
    }
    
    /**
     * Muokataan ohjetta
     */
    private void muokkaaOhjetta() {
        if ( ohjeKohdalla == null ) return; 
        try { 
            Ohje ohje; 
            ohje = OhjeDialogController.kysyOhje(null, ohjeKohdalla.clone());
            if ( ohje == null ) return; 
            leivonta.korvaaTaiLisaa(ohje);
            hae(reseptiKohdalla.getTunnusNro()); 
        } catch (CloneNotSupportedException e) { 
            Dialogs.showMessageDialog(e.getMessage()); 
        } catch (SailoException e) { 
            Dialogs.showMessageDialog(e.getMessage()); 
        } 
    }
    
    /**
     * Tekee uuden allergian editointia varten
     */
    public void uusiAllergia(){
        if (reseptiKohdalla == null || ohjeKohdalla == null ) {
            virheviesti("Ei pystytä lisäämään allergiaa ilman ohjetta!"); 
            return;
        }
        if (ohjeKohdalla.getAllergiaNro() > 0 ) {
            virheviesti("Ohjeella on jo allergia!"); 
            return;
        }
        Allergia allergia = new Allergia();
        allergia.rekisteroi();
        allergia.setAllergia("Pähkinätön"); //"Rakennuspalikka"
        leivonta.lisaa(allergia);
        lisaaAllergia(allergia);
        hae(reseptiKohdalla.getTunnusNro());
    }
    
    /**
     * Virheviesti, jos ei pystytä lisäämään allergiaa
     * @param viesti viesti joka halutaan tulostaa
     */
    public void virheviesti(String viesti) {
        Dialogs.showMessageDialog(viesti,
        dlg -> dlg.getDialogPane().setPrefWidth(400));
    }
    
    /**
     * Lisätään ohjeeseen allergia
     * @param allergia allergia joka lisätään ohjeeseen
     */
    public void lisaaAllergia(Allergia allergia) {
        allergiaKohdalla = allergia;
        if(ohjeKohdalla != null && allergiaKohdalla != null) {
            ohjeKohdalla.setAllergiaNro(allergia.getTunnusNro());
            ohjeKohdalla.setAllergia(editAllergia.getText());
            }
    }
    
    /**
     * Muokataan reseptiä
     */
    private void muokkaaResepti() {
        if ( reseptiKohdalla == null ) return; 
        try { 
            Resepti resepti; 
            resepti = ReseptiDialogController.kysyResepti(null, reseptiKohdalla.clone()); 
            if ( resepti == null ) return; 
            leivonta.korvaaTaiLisaa(resepti); 
            hae(resepti.getTunnusNro()); 
        } catch (CloneNotSupportedException e) { 
            Dialogs.showMessageDialog(e.getMessage()); 
        } catch (SailoException e) { 
            Dialogs.showMessageDialog(e.getMessage()); 
        } 
    }
    
    /**
     * Alustaa reseptin lukemalla sen tiedostosta
     * @param nimi tiedosto josta resepti luetaan
     * @return null jos onnistuu, muuten virhe
     */
    protected String lueTiedosto(String nimi) {
        reseptinnimi = nimi;
        setTitle("Resepti - " + reseptinnimi);
        try {
            leivonta.lueTiedostosta(nimi);
            hae(0);
            return null;
        } catch (SailoException e) {
            hae(0);
            String virhe = e.getMessage();
            if ( virhe != null) Dialogs.showMessageDialog(virhe);
            return virhe;
        }
    }
    
    /**
     * Kysytään reseptin nimi ja luetaan se
     * Jos painettu uutta reseptiä, avataan oletusreseptirekisteri ja siihen uuden reseptin lisääminen
     * @return true jos onnistui, false jos ei
     */
    public boolean avaa() {
        String uusinimi = ReseptinNimiController.kysyNimi(null, reseptinnimi);
        if (uusinimi == null) return false;
        if (uusinimi == "uusi") {
            lueTiedosto("Leivontarekisteri");
            uusiResepti();
            return true;
        }
        lueTiedosto(uusinimi);
        return true;
    }
    
    /**
     * Tallentaa muutokset
     * @return null jos onnistuu, muuten virhe
     */
    private String tallenna() {
        try {
            leivonta.tallenna();
            return null;
        } catch (SailoException ex) {
            Dialogs.showMessageDialog("Tallennuksessa ongelmia!" + ex.getMessage());
            return ex.getMessage();
        }
    }
    
    /**
     * Tarkistetaan onko tallennus tehty
     * @return true jos saa sulkea sovelluksen, false jos ei
     */
     public boolean voikoSulkea() {
        tallenna();
        return true;
     }
     
     /**
      * Näytetään ohjelman suunnitelma erillisessä selaimessa.
      */
     private void avustus() {
         Desktop desktop = Desktop.getDesktop();
         try {
             URI uri = new URI("https://tim.jyu.fi/view/kurssit/tie/ohj2/2019k/ht/jeadjuka");
             desktop.browse(uri);
         } catch (URISyntaxException e) {
             return;
         } catch (IOException e) {
             return;
         }
     }        
}
