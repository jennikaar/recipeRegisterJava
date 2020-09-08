package fxLeivonta;

import java.net.URL;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import leivonta.Resepti;

/**
 * Reseptin lisäämistä ja muokkaamista varten dialogi
 * @author Jenni Kääriäinen
 * @version 22.4.2019
 */
public class ReseptiDialogController implements ModalControllerInterface<Resepti>,Initializable  {
    
    @FXML private TextField editNimi;
    @FXML private TextField editKategoria;
    @FXML private TextField editTeema;
    @FXML private Label labelVirhe;
    
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        alusta(); 
    }
   
    @FXML private void handleOK() {
        if ( reseptiKohdalla != null && reseptiKohdalla.getNimi().trim().equals("") ) {
            naytaVirhe("Nimi ei saa olla tyhjä");
            return;
        }
        ModalController.closeStage(labelVirhe);
    }
   
    @FXML private void handleCancel() {
        reseptiKohdalla = null;
        ModalController.closeStage(labelVirhe);
    }
    
// ========================================================
    private Resepti reseptiKohdalla;
    private TextField edits[];
    
    /**
     * Tekee tarvittavat muut alustukset.
     */
    protected void alusta() {
        edits = new TextField[]{editNimi, editKategoria, editTeema};
        int i = 0;
        for (TextField edit : edits) {
            final int k = ++i;
            edit.setOnKeyReleased( e -> kasitteleMuutosReseptiin(k, (TextField)(e.getSource())));
        }
    }
    
    /**
     * Tyhjentään tekstikentät
     * @param edits taulukko joka tyhjennetään
     */
    public static void tyhjenna(TextField edits[]) {
        for (TextField edit : edits)
            edit.setText("");
    }
    
    @Override
    public Resepti getResult() {
        return reseptiKohdalla;
    }
    
    @Override
    public void setDefault(Resepti oletus) {
        reseptiKohdalla = oletus;
        naytaResepti(edits, reseptiKohdalla);
    }
    
    /**
     * Mitä tehdään kun dialogi on näytetty
     */
    @Override
    public void handleShown() {
        editNimi.requestFocus();
    }
    
    /**
     * Virheen näyttämistä vartem
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
     * Käsitellään reseptiin tullut muutos
     * @param edit muuttunut kenttä
     */
    private void kasitteleMuutosReseptiin(int k, TextField edit) {
        if (reseptiKohdalla == null) return;
        String s = edit.getText();
        String virhe = null;
        switch (k) {
           case 1 : virhe = reseptiKohdalla.setRnimi(s); break;
           case 2 : virhe = reseptiKohdalla.setKategoria(s); break;
           case 3 : virhe = reseptiKohdalla.setTeema(s); break;
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
     * Näytetään reseptin tiedot TextField komponentteihin
     * @param edits taulukko tekstikenttiä
     * @param resepti näytettävä resepti
     */
    public static void naytaResepti(TextField[] edits, Resepti resepti) {
        if (resepti == null) return;
        edits[1].setText(resepti.getKategoria());
        edits[2].setText(resepti.getTeema());
    }
    
    /**
     * Luodaan reseptin kysymisdialogi ja palautetaan sama tietue muutettuna tai null
     * @param modalityStage mille ollaan modaalisia, null = sovellukselle
     * @param oletus mitä dataan näytetään oletuksena
     * @return null jos painetaan Cancel, muuten täytetty tietue
     */
    public static Resepti kysyResepti(Stage modalityStage, Resepti oletus) {
        return ModalController.<Resepti, ReseptiDialogController>showModal(
                    ReseptiDialogController.class.getResource("LeivontauusiresGUIView.fxml"),
                    "Leivonta",
                    modalityStage, oletus, null 
                );
    }
    
}
