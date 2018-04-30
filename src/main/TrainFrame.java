package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import trainstation.Gare;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.ScrollPane;
import java.awt.TextArea;
import javax.swing.border.TitledBorder;


public class TrainFrame extends JFrame{

	private static final long serialVersionUID = 1L;    
	final static String trainInStat1 = "/resources/silverTrain.png";
	final static String trainInStat2 = "/resources/TrainInStat2.png";
	final static String rails = "/resources/Rails.png";  
	public int compteur_rotation =5;
	public int compteur_train_quai =5;
	public JPanel panel; 
	
	private static TrainFrame instance=null ; 
	public static TrainFrame getInstance()
	{
		if (instance == null) 
			instance = new TrainFrame();
		return instance;
	}

	private TrainFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(null);
		setSize(new Dimension(1804, 1147));

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

		revalidate();
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0,0,1804, 1047);        


		getContentPane().add(panel);
		panel.setLayout(null);       

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Boutons de control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(1465, 820, 192, 152);
		panel.add(panel_1);
		panel_1.setLayout(null);


		JButton btnEntreeEnGare = new JButton(" + Quais: " +  Integer.toString(compteur_train_quai));
		btnEntreeEnGare.setBounds(6, 18, 180, 23);
		panel_1.add(btnEntreeEnGare);

		JButton btnDepart = new JButton("+ ROTATION");
		btnDepart.setBounds(6, 50, 180, 23);
		panel_1.add(btnDepart);

		JButton btnSortirTrainGare = new JButton("Quitter QUAIS");
		btnSortirTrainGare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {}
		});
		btnSortirTrainGare.setBounds(6, 84, 180, 23);
		panel_1.add(btnSortirTrainGare);

		JButton btnEntreeTrainGare = new JButton("Rotation -> Quais: " +  Integer.toString(compteur_rotation) );
		btnEntreeTrainGare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnEntreeTrainGare.setBounds(6, 122, 180, 23);
		panel_1.add(btnEntreeTrainGare);

		
	
		btnEntreeTrainGare.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					compteur_train_quai ++;
					System.out.println(compteur_train_quai);

					if(compteur_train_quai !=0) {
						btnSortirTrainGare.setEnabled(true);
						
					}
					if(compteur_rotation>=1) {
						//TODO ajouter le enalb edans la +train rotation
						btnEntreeTrainGare.setEnabled(true);
						Gare.getInstance().entreeGare();
//						compteur_rotation --;
						
						btnEntreeTrainGare.setText("Rotation -> Quais: " +  Integer.toString(compteur_rotation));

						//System.out.println(compteur_rotation);
					}else {
						compteur_rotation=0;
						btnEntreeTrainGare.setText("Rotation -> Quais: " +  Integer.toString(compteur_rotation));
						btnEntreeTrainGare.setEnabled(false);
					}
					compteur_rotation --;
					btnEntreeEnGare.setText("Quais: " +  Integer.toString(compteur_train_quai));

				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		if(compteur_train_quai>=0) {

		btnSortirTrainGare.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					
					if(compteur_train_quai ==0) {
						btnSortirTrainGare.setEnabled(false);
					}
					else {
						
					compteur_rotation ++;
					Gare.getInstance().quitterGare();

					if(compteur_rotation!=0) {

						btnEntreeTrainGare.setEnabled(true);
						System.out.println(compteur_rotation);
					}
					btnEntreeTrainGare.setText("Rotation -> Quais: " +  Integer.toString(compteur_rotation));

					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				compteur_train_quai --;
				if(compteur_train_quai>=0)
					btnEntreeEnGare.setText(" Quais: " +  Integer.toString(compteur_train_quai));
				if(compteur_train_quai ==0) {
					btnSortirTrainGare.setEnabled(false);
				}

			}
		});}
		btnDepart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				compteur_rotation++;
				//TODO rotation quai + + 
				btnEntreeTrainGare.setText("Rotation -> Quais: " +  Integer.toString(compteur_rotation));

				try {
					Gare.getInstance().nouveauTrainEnRotation();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(compteur_rotation !=0) {
					btnEntreeTrainGare.setEnabled(true);
				}
			}
		});
		btnEntreeEnGare.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Gare.getInstance().nouveauTrainEnGare();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				compteur_train_quai ++;
				btnEntreeEnGare.setText("Quais: " +  Integer.toString(compteur_train_quai));

				if(compteur_train_quai !=0) {
					btnSortirTrainGare.setEnabled(true);
				}
			}
		});
		JLabel lblNewLabel_1 = new JLabel("Compteur rotation");
		lblNewLabel_1.setBounds(1552, 632, 192, 162);
		panel.add(lblNewLabel_1);
		lblNewLabel_1.setText( Integer.toString(compteur_rotation));
	}       



	public JLabel trains(String name)
	{       
		JLabel lblNewLabel = new JLabel(name);
		lblNewLabel.setIcon(new ImageIcon(TrainFrame.class.getResource(trainInStat1)));
		lblNewLabel.setBounds(33, 308, 133, 128);
		lblNewLabel.setVisible(true);
		panel.add(lblNewLabel);        
		panel.revalidate();
		panel.repaint();
		panel.setComponentZOrder(lblNewLabel, 0);

		return lblNewLabel;     
	}


	public void delete(JLabel label)
	{       
		panel.remove(label);
		panel.revalidate();    
		panel.repaint();
	}


	public JLabel trainRails(String name, Point p)
	{       
		JLabel lblNewLabel = new JLabel(name);

		lblNewLabel.setIcon(new ImageIcon(TrainFrame.class.getResource(rails)));
		lblNewLabel.setBounds( p.x, p.y, 100,1100);
		lblNewLabel.setVisible(true);
		panel.add(lblNewLabel);        
		panel.revalidate();
		panel.repaint();

		return lblNewLabel;     
	}    

	public JLabel createStationnementLabel(String name, Point p)
	{       
		JLabel lblNewLabel = new JLabel(name);
		lblNewLabel.setIcon(new ImageIcon(TrainFrame.class.getResource("/resources/Station.png")));
		lblNewLabel.setOpaque(true);

		lblNewLabel.setBounds(p.x, p.y, 130  , 130);
		lblNewLabel.setVisible(true);
		panel.add(lblNewLabel);        
		panel.revalidate();
		panel.repaint();

		return lblNewLabel;     
	}      
}
