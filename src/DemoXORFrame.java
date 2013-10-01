import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;


public class DemoXORFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public DemoXORFrame(){
		setSize(WIDTH, HEIGHT);

		JMenuBar barra = new JMenuBar();
		barra.add(criarMenuFicheiro());
		barra.add(criarMenuMenu1());
		barra.add(criarMenuOpcoes());
		barra.add(criarMenuAjuda());
		setJMenuBar(barra);
		
		painel = new DemoXORPanel();
		//painel.setBackground(Color.DARK_GRAY);
		painel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		setContentPane(painel);
	}
	
	private JMenu criarMenuOpcoes() {
		JMenu menu = new JMenu("Opçoes");
		menu.add(criarItemMenuOpcoes("Bounding Box"));
		return menu;
	}

	private JMenu criarMenuFicheiro() {
		JMenu menu = new JMenu("File");
		menu.add(criarItemMenuFicheiro("New"));
		menu.add(new JSeparator());
		menu.add(criarItemMenuFicheiro("Exit"));
		return menu;
	}
	
	private JMenuItem criarItemMenuOpcoes(String texto) {
		final JCheckBoxMenuItem item = new JCheckBoxMenuItem(texto);

		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Bounding Box") && item.isSelected())
					item.setSelected(true);
				else if(e.getActionCommand().equals("Bounding Box") && !item.isSelected())
					item.setSelected(false);
				painel.changeOption(1);
			}	
		}
		
		item.addActionListener(new ListenerItemMenu());
		return item;	
	}
	
	private JMenuItem criarItemMenuFicheiro(String texto)
	{
		JMenuItem item = new JMenuItem(texto);
		
		class ListenerItemMenu implements ActionListener
		{

			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("New"))
					painel.limparDesenho();
				else if(e.getActionCommand().equals("Exit"))
					System.exit(0);
			}	
		}
		
		item.addActionListener(new ListenerItemMenu());
		return item;	
	}
	
	
	private JMenu criarMenuMenu1()
	{
		JMenu menu = new JMenu("Pontos");
		menu.add(criarItemMenuMenu1("4"));
		menu.add(criarItemMenuMenu1("7"));
		menu.add(criarItemMenuMenu1("10"));
		return menu;
	}
	
	private JMenuItem criarItemMenuMenu1(String texto)
	{
		JMenuItem item = new JMenuItem(texto);
		
		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				
				if(event.getActionCommand().equals("4")) {
					System.out.println("4 pontos escolhidos."); 
					painel.setPointNumber(4);
				}
				if(event.getActionCommand().equals("7")) {
					System.out.println("7 pontos escolhidos.");
					painel.setPointNumber(7);
				}
				if(event.getActionCommand().equals("10")){
					System.out.println("10 pontos escolhidos.");
					painel.setPointNumber(10);
				}
					
			}
		}
		
		item.addActionListener(new ListenerItemMenu());
		
		return item;
	}
	
	private JMenuItem criarItemMenuAjuda(String texto) 
	{
		JMenuItem item = new JMenuItem(texto);

		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if(e.getActionCommand().equals("About"))
					JOptionPane.showMessageDialog(null, 
							"Demo para programar em Java/Swing\n\n(M. Pr\u00F3spero e F. Birra)\n");
			}
		}
		
		item.addActionListener(new ListenerItemMenu());
		return item;
	}
	private JMenu criarMenuAjuda()
	{
		JMenu menu = new JMenu("Help");		
		
		menu.add(criarItemMenuAjuda("About"));
		return menu;
	}
	
	private DemoXORPanel painel;
	
	private static final int WIDTH = 800;
	private static final int HEIGHT= 600;

}
