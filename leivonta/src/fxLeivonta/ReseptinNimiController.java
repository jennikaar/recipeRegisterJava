package fxLeivonta;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Avausikkunan käyttäytyminen.
 * Kysytään reseptin nimi ja luodaan tätä varten dialogi.
 * 
 * @author Jenni Kääriäinen
 * @version 22.2.2019
 */
public class ReseptinNimiController implements ModalControllerInterface<String> {
    
    @FXML private TextField textVastaus;
    @FXML private Label labelOtsikko;
    private String vastaus = null;

    
    @FXML private void handleOK() {
        vastaus = textVastaus.getText();
        ModalController.closeStage(textVastaus);
    }
    
    @FXML private void handleUusiResepti() {
        vastaus = "uusi";
        ModalController.closeStage(labelOtsikko);
    }
    
    @FXML private void handleAvaaRekisteri() {
        vastaus = labelOtsikko.getText();
        ModalController.closeStage(labelOtsikko);
    }

    @Override
    public String getResult() {
        return vastaus;
    }

    
    @Override
    public void setDefault(String oletus) {
        textVastaus.setText(oletus);
    }

    
    /**
     * Mitä tehdään kun dialogi on näytetty
     */
    @Override
    public void handleShown() {
        textVastaus.requestFocus();
    }
    
    /**
     * Luodaan reseptin kysymiseen ja palautetaan siihen kirjoitettu resepti tai null
     * @param modalityStage mille ollaan modaalisia, null = sovellukselle
     * @param oletus mitä reseptiä ä näytetään oletuksena
     * @return null jos painetaan Cancel, muuten kirjoitettu resepti
     */
    public static String kysyNimi(Stage modalityStage, String oletus) {
        return ModalController.showModal(
                ReseptinNimiController.class.getResource("LeivontaavausGUIView.fxml"),
                "Leivonta",
                modalityStage, oletus);
    }
}
