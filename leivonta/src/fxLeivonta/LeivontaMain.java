package fxLeivonta;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import leivonta.Leivonta;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

/**
 * @author Jenni Kääriäinen
 * Pääohjelma leivontarekisterille
 * @version 22.4.2019 
 */
public class LeivontaMain extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
            final FXMLLoader ldr = new FXMLLoader(getClass().getResource("LeivontaGUIView.fxml"));
            final Pane root = (Pane)ldr.load(); //asetetaan pohja
            final LeivontaGUIController leivontaCtrl = (LeivontaGUIController)ldr.getController();

            final Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("leivonta.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Leivonta");

            primaryStage.setOnCloseRequest((event) -> {
                    if ( !leivontaCtrl.voikoSulkea() ) event.consume();
                });
            
            Leivonta leivonta = new Leivonta(); //luodaan leivonta-olio
            leivontaCtrl.setLeivonta(leivonta); //kutsutaan Controllerin metodia
            
            primaryStage.show();
            Application.Parameters params = getParameters();
            if(params.getRaw().size() >0)
                leivontaCtrl.lueTiedosto(params.getRaw().get(0));
            else
                if ( !leivontaCtrl.avaa()) Platform.exit();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		launch(args); //aloitetaan ohjelma
	}
}
