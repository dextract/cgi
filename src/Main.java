import javax.swing.JFrame;
/**
 * @author Professores de CGI
 */
public class Main {

	public static void main(String[] args) {
		DemoXORFrame frame = new DemoXORFrame();				//Criar o frame da aplicacao
		frame.setTitle("Editor Demo CGI"); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);									// Tornar o frame visivel
	}
}
