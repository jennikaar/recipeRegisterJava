package fxLeivonta;

import java.net.URL;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import leivonta.Ohje;

/**
 * Ohjeen muokkausikkunaa varten
 * @author Jenni Kääriäinen
 * @version 26.4.2019
 *
 */
public class OhjeDialogController implements ModalControllerInterface<Ohje>,Initializable  {
    
    @FXML private TextArea editOhje;
    @FXML private TextField editValmistusaika;
    @FXML private TextField editAllergia;
    @FXML private TextArea editAinesosat; 
    @FXML private Label labelVirhe;
    @FXML private ComboBoxChooser<String> comboOhjeTyyppi;
    
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        alusta(); 
    }
    
    @FXML private void handleOK() {
        if(ohjeKohdalla != null && 
                (ohjeKohdalla.getAinesosat() == null|| ohjeKohdalla.getOhje() == null 
                || ohjeKohdalla.getOhjeTyyppi() == null || ohjeKohdalla.getOhjeTyyppi() == "")) {
            Dialogs.showMessageDialog("Ohje tai ainesosat tai ohjetyyppi eivät saa olla tyhijä!",
                    dlg -> dlg.getDialogPane().setPrefWidth(400));
            return; 
        }
        ModalController.closeStage(labelVirhe);
    }
   
    @FXML private void handleCancel() {
        ohjeKohdalla = null;
        ModalController.closeStage(labelVirhe);
    }
    
    @FXML private void handleOhjeValinta() {
        int k = comboOhjeTyyppi.getSelectionModel().getSelectedIndex();
        switch(k) {
        case 0: valitseOhjeTyyppi("");break;
        case 1: valitseOhjeTyyppi("Perinteinen"); break;
        case 2: valitseOhjeTyyppi("Gluteeniton"); break;
        case 3: valitseOhjeTyyppi("Munaton"); break;
        case 4: valitseOhjeTyyppi("Pähkinätön"); break;
        case 5: valitseOhjeTyyppi("Vegaani"); break;
        default:valitseOhjeTyyppi("Perinteinen");
        }
    }

 // ========================================================
    
    private Ohje ohjeKohdalla;
    private TextField edits[];
    
    /**
     * Tekee tarvittavat muut alustukset.
     */
    protected void alusta() {
        edits = new TextField[]{editValmistusaika, editAllergia};
        int i = 0;
        for (TextField edit : edits) {
            final int k = ++i;
            edit.setOnKeyReleased( e -> kasitteleMuutosOhjeeseen(k, (TextField)(e.getSource())));
        }
        editOhje.setOnKeyReleased(e -> kasitteleMuutosOhjeeseen(editOhje));
        editAinesosat.setOnKeyReleased(e -> kasitteleMuutosAinesosiin(editAinesosat));
    }
    
    /**
     * Tyhjennetään kentät
     */
    public void tyhjenna() {
        editOhje.setText("");
        for (TextField edit : edits)
            edit.setText("");
        editAinesosat.clear();
    }
    
    /**
     * Käsitellään ohjeeseen tullut muutos
     * @param edit muuttunut kenttä
     */
    private void kasitteleMuutosOhjeeseen(int k, TextField edit) {
        if (ohjeKohdalla == null) return;
        String s = edit.getText();
        String virhe = null;
        switch (k) {
           case 1 : virhe = ohjeKohdalla.setValmistusaika(s); break;
           case 2 : virhe = ohjeKohdalla.setAllergia(s); break;
           default:
        }
        if (virhe == null) {
            Dialogs.setToolTipText(edit,"");
            edit.getStyleClass().removeAll("virhe");
            naytaVirhe(virhe);
        } else {
            Dialogs.setToolTipText(edit,virhe);
            edit.getStyleClass().add("virhe");
            naytaVirhe(virhe);
        }
    }
    
    /**
     * Käsitellään ohjeeseen tullut muutos
     * @param edit muuttunut kenttä
     */
    private void kasitteleMuutosOhjeeseen(TextArea edit) {
        if (ohjeKohdalla == null) return;
        String s = edit.getText();
        
        String virhe = null;
        if (s.contains("@")) virhe = "Älä lisää ohjeeseen merkkiä @";
        else virhe = ohjeKohdalla.setOhje(s);
        if (virhe == null) {
            Dialogs.setToolTipText(edit,"");
            edit.getStyleClass().removeAll("virhe");
            naytaVirhe(virhe);
        } else {
            Dialogs.setToolTipText(edit,virhe);
            edit.getStyleClass().add("virhe");
            naytaVirhe(virhe);
        }
    }
    
    /**
     * käsitellään muutos ohjeen ainesosiin
     * @param edit taulukko jossa olioita
     */
    private void kasitteleMuutosAinesosiin(TextArea edit) {
        if(ohjeKohdalla == null) return;
        String s = edit.getText();
        String virhe = null;
        virhe = ohjeKohdalla.setAinesosat(s);
        if (virhe == null) {
            Dialogs.setToolTipText(edit,"");
            edit.getStyleClass().removeAll("virhe");
            naytaVirhe(virhe);
        } else {
            Dialogs.setToolTipText(edit,virhe);
            edit.getStyleClass().add("virhe");
            naytaVirhe(virhe);
        }
    }
    
    /**
     * Virheen näyttämistä varten
     * @param virhe joka näytetään
     */
    private void naytaVirhe(String virhe) {
        if ( virhe == null || virhe.isEmpty() ) {
            labelVirhe.setText("");
            labelVirhe.getStyleClass().removeAll("virhe");
            return;
        }
        labelVirhe.setText(virhe);
        labelVirhe.getStyleClass().add("virhe");
    }
    
    /**
     * Napin painalluksella valitaan ohjetyypiksi valittu
     * @param s valittu ohjetyyppi
     * TODO: Ei kriittinen. Vain yhden ohjeen lisääminen yhdelle ohjetyypille per resepti.
     */
    private void valitseOhjeTyyppi(String s) {
        ohjeKohdalla.setOhjeTyyppi(s);
        labelVirhe.setText(s);
    }
    
    /**
     * Näytetään ohjeen tiedot TextField komponentteihin
     * @param edits taulukko tekstikenttiä
     * @param editOhje tekstikenttä
     * @param ohje näytettävä ohje
     */
    public static void naytaOhje(TextField[] edits, TextArea editOhje, Ohje ohje) {
        if (ohje == null) return;
        edits[0].setText(Integer.toString(ohje.getValmistusaika()));
        editOhje.setText(ohje.getOhje());
    }
    
    /**
     * Näytetään ohjeen ainesosat
     * @param editAinesosat ainesosan tekstiboxi
     * @param ohje jonka ainesosat näytetään
     */
    public static void naytaAinesosat(TextArea editAinesosat, Ohje ohje) {
        editAinesosat.setText(ohje.getAinesosat());
    }
    
    /**
     * Luodaan ohjeen kysymisdialogi ja palautetaan sama tietue muutettuna tai null
     * @param modalityStage mille ollaan modaalisia, null = sovellukselle
     * @param oletus mitä dataan näytetään oletuksena
     * @return null jos painetaan Cancel, muuten täytetty tietue
     */
    public static Ohje kysyOhje(Stage modalityStage, Ohje oletus) {
        return ModalController.<Ohje, OhjeDialogController>showModal(
                    OhjeDialogController.class.getResource("LeivontauusiGUIView.fxml"),
                    "Leivonta",
                    modalityStage, oletus, null 
                );
    }

    @Override
    public void setDefault(Ohje oletus) {
        ohjeKohdalla = oletus;
        naytaOhje(edits, editOhje, ohjeKohdalla);
        naytaAinesosat(editAinesosat, ohjeKohdalla);
    }
    
    /**
     * Mitä tehdään, kun dialogi on näytetty
     */
    @Override
    public void handleShown() {
        editOhje.requestFocus();
    }
    
    @Override
    public Ohje getResult() {
        return ohjeKohdalla;
    }

}
