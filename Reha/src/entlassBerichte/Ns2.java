package entlassBerichte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXPanel;

import systemTools.JCompTools;
import systemTools.JRtaCheckBox;
import systemTools.JRtaComboBox;
import systemTools.JRtaTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class Ns2 implements ActionListener {
	EBerichtPanel eltern = null;
	JXPanel pan = null;
	Font fontgross = null;
	Font fontklein = null;
	Font fontnormal = null;
	Font fontcourier = null;
	Font fontarialfett = null;
	Font fontarialnormal = null;
	Font fontarialfettgross = null;
	JScrollPane jscr = null;
	Vector ktl = new Vector();
	String[] sktl = null;
	

	public Ns2(EBerichtPanel xeltern){
		pan = new JXPanel(new BorderLayout());
		pan.setOpaque(false);
		//pan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		fontgross =new Font("Arial",Font.BOLD,14);
		fontklein =new Font("Arial",Font.PLAIN,9);
		fontnormal =new Font("Arial",Font.PLAIN,11);
		fontarialfett =new Font("Arial",Font.BOLD,11);
		fontarialnormal =new Font("Arial",Font.PLAIN,11);		
		fontcourier =new Font("Courier New",Font.PLAIN,12);
		fontarialfettgross = new Font("Arial",Font.BOLD,12);		
		eltern = xeltern;
		new SwingWorker<Void,Void>(){
			
			@Override
			protected Void doInBackground() throws Exception {
				try{
					pan.add(constructSeite());
					for(int i = 7; i < 8;i++){
						eltern.bta[i].setFont(fontcourier);
						eltern.bta[i].setForeground(Color.BLUE);
						eltern.bta[i].setWrapStyleWord(true);
						eltern.bta[i].setLineWrap(true);
						eltern.bta[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
					}
					new Thread(){
						public void run(){
							//laden(eltern.neu);
							//inGuiInit = false;
						}
					}.start();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			return null;
			}
		}.execute();	
	}	
	
	public JXPanel getSeite(){
		return pan;
	}
	public JScrollPane constructSeite(){// 1          2          3         4           5
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.25),550dlu,fill:0:grow(0.25),10dlu",
				
				//   2=titel   4=block1 6=block2 8=block3
				// 1    2   3    4   5    6   7  8   9   10  11   12  13  14   15  16  17  18 19
				"20dlu, p ,2dlu, p, 10dlu,p,2dlu,p ,5dlu,p,  2dlu, p, 5dlu,p, 2dlu,p,10dlu,p,10dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		pb.setOpaque(false);
		CellConstraints cc = new CellConstraints();
		
		pb.add(getTitelLeistungen(),cc.xy(3,2));
		//Block 1 Therapeutische Leistungen, die Legende zu KTL und Besonderheiten im Verlauf
		pb.add(getBlock1(),cc.xy(3,4));
		
		pb.add(getTitelVorschlaege(),cc.xy(3,6));
		pb.add(getBlock2(),cc.xy(3,8));
		
		
		pb.getPanel().validate();
		jscr = JCompTools.getTransparentScrollPane(pb.getPanel());
		jscr.getVerticalScrollBar().setUnitIncrement(15);
		jscr.validate();
		return jscr;		
	}	
	private JPanel getBlock2(){
		FormLayout laytherap = new FormLayout("1px,p:g,1px",
//				 1   2  3  4   5  6   7  8	 9  10  11	12	13 14
				"1px,p,1px");
				PanelBuilder therap = new PanelBuilder(laytherap);
				therap.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
				therap.getPanel().setOpaque(false);               
				CellConstraints cctherap = new CellConstraints();
				therap.add(getBlock6Komplett(),cctherap.xy(2, 2));

				therap.getPanel().validate();
				return therap.getPanel();
	}
	private JPanel getBlock6Komplett(){   //        1         2           3    4    5           6             7  
		FormLayout layvorschlag = new FormLayout("2dlu,fill:0:grow(0.55),10dlu,1px,10dlu, fill:0:grow(0.45) ,2dlu",
			// 1  2   3   4   5    6   7   8     9   10   11 12 13   14    15
			"2dlu,p,4dlu, p, 5dlu, p,2dlu,30dlu,5dlu,1px,5dlu,p,2dlu,30dlu,4dlu");
		PanelBuilder vorschlag = new PanelBuilder(layvorschlag);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		vorschlag.setOpaque(false);               
		CellConstraints ccvorschlag = new CellConstraints();

		// linker Block �berschrift		
		FormLayout links = new FormLayout(  "p","p");
		PanelBuilder titl = new PanelBuilder(links);
		titl.getPanel().setOpaque(false);
		CellConstraints cctitl = new CellConstraints();
		// hier dazwischen den ganzen linken Schei�
		JLabel lab = new JLabel("Weiterbehandelnde �rzte / Psychologen");
		lab.setFont(fontarialfett);
		titl.add(lab,cctitl.xy(1,1,CellConstraints.FILL,CellConstraints.TOP));
		titl.getPanel().validate();
		vorschlag.add(titl.getPanel(),ccvorschlag.xy(2,2));
		// linker Block mit K�stchen
							//    1   2  3     4            5  6   7   1 2  3   4 5  6     7 8
		links = new FormLayout(  "p,2dlu,p,fill:0:grow(1.0),p,2dlu,p","p,p,10dlu,p,p,10dlu,p,p,10dlu");
		titl = new PanelBuilder(links);
		titl.getPanel().setOpaque(false);
		cctitl = new CellConstraints();
		eltern.bchb[7] = new JRtaCheckBox("");
		eltern.bchb[7].setName("F_125");
		titl.add(eltern.bchb[7],cctitl.xywh(1,1,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titl.add(this.getLabelArialFettRot("Diagnostische"),cctitl.xywh(3,1,1,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		titl.add(this.getLabelArialFettRot("Kl�rung"),cctitl.xywh(3,2,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));
		
		eltern.bchb[8] = new JRtaCheckBox("");
		eltern.bchb[8].setName("F_127");
		titl.add(eltern.bchb[8],cctitl.xywh(1,4,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titl.add(this.getLabelArialFettRot("Station�re Behandlung"),cctitl.xywh(3,4,1,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		titl.add(this.getLabelArialFettRot("OP"),cctitl.xywh(3,5,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));

		eltern.bchb[9] = new JRtaCheckBox("");
		eltern.bchb[9].setName("F_128");
		titl.add(eltern.bchb[9],cctitl.xywh(1,7,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titl.add(this.getLabelArialFettRot("Psych. Beratung /"),cctitl.xywh(3,7,1,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		titl.add(this.getLabelArialFettRot("Psychotherapie"),cctitl.xywh(3,8,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));

		eltern.bchb[10] = new JRtaCheckBox("");
		eltern.bchb[10].setName("F_126");
		titl.add(eltern.bchb[10],cctitl.xywh(5,1,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titl.add(this.getLabelArialFettRot("Kontrolle Labor /"),cctitl.xywh(7,1,1,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		titl.add(this.getLabelArialFettRot("Medikamente"),cctitl.xywh(7,2,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));
		
		eltern.bchb[11] = new JRtaCheckBox("");
		eltern.bchb[11].setName("F_141");
		titl.add(eltern.bchb[11],cctitl.xywh(5,4,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titl.add(this.getLabelArialFettRot("Suchberatung"),cctitl.xywh(7,4,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));	

		eltern.bchb[12] = new JRtaCheckBox("");
		eltern.bchb[12].setName("F_140");
		titl.add(eltern.bchb[12],cctitl.xywh(5,7,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titl.add(this.getLabelArialFettRot("Heil- und Hilfsmittel inclusive"),cctitl.xywh(7,7,1,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		titl.add(this.getLabelArialFettRot("Physiotherapie und Ergotherapie"),cctitl.xywh(7,8,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));
		
		titl.getPanel().validate();
		vorschlag.add(titl.getPanel(),ccvorschlag.xy(2,4));


		// rechter Block �berschrift		
		FormLayout rechts = new FormLayout(  "p","p");
		PanelBuilder titr = new PanelBuilder(rechts);
		titr.getPanel().setOpaque(false);
		CellConstraints cctitr = new CellConstraints();
		lab = new JLabel("Patienten");
		lab.setFont(fontarialfett);
		titr.add(lab,cctitr.xy(1,1,CellConstraints.FILL,CellConstraints.TOP));
		titr.getPanel().validate();
		vorschlag.add(titr.getPanel(),ccvorschlag.xy(6,2));

		// rechter Block mit K�stchen
		//                         1   2  3     4            5  6   7   1 2  3    4 5  6    7 8
		rechts = new FormLayout(  "p,2dlu,p,fill:0:grow(1.0),p,2dlu,p","p,p,10dlu,p,p,10dlu,p,p,10dlu");		
		titr = new PanelBuilder(rechts);
		titr.getPanel().setOpaque(false);
		cctitr = new CellConstraints();
		eltern.bchb[13] = new JRtaCheckBox("");
		eltern.bchb[13].setName("F_156");
		titr.add(eltern.bchb[13],cctitr.xywh(1,1,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titr.add(this.getLabelArialFettRot("�bungen selbst�nd."),cctitr.xywh(3,1,1,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		titr.add(this.getLabelArialFettRot("fortsetzen"),cctitr.xywh(3,2,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));

		eltern.bchb[14] = new JRtaCheckBox("");
		eltern.bchb[14].setName("F_137");
		titr.add(eltern.bchb[14],cctitr.xywh(1,4,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titr.add(this.getLabelArialFettRot(" "),cctitr.xywh(3,4,1,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		titr.add(this.getLabelArialFettRot(" "),cctitr.xywh(3,5,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));
		titr.add(this.getLabelArialFettRot("Gewichtsreduktion"),cctitr.xywh(3,4,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));

		eltern.bchb[15] = new JRtaCheckBox("");
		eltern.bchb[15].setName("F_138");
		titr.add(eltern.bchb[15],cctitr.xywh(1,7,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titr.add(this.getLabelArialFettRot(" "),cctitr.xywh(3,7,1,1,CellConstraints.DEFAULT,CellConstraints.BOTTOM));	
		titr.add(this.getLabelArialFettRot(" "),cctitr.xywh(3,8,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));
		titr.add(this.getLabelArialFettRot("Alkoholkarenz"),cctitr.xywh(3,7,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
	/********/	
		eltern.bchb[16] = new JRtaCheckBox("");
		eltern.bchb[16].setName("F_157");
		titr.add(eltern.bchb[16],cctitr.xywh(5,1,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titr.add(this.getLabelArialFettRot("Sport und Bewegung"),cctitr.xywh(7,1,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));	
		//titr.add(this.getLabelArialFettRot("Sport und Bewegung"),cctitr.xywh(7,2,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));

		eltern.bchb[17] = new JRtaCheckBox("");
		eltern.bchb[17].setName("F_139");
		titr.add(eltern.bchb[17],cctitr.xywh(5,4,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titr.add(this.getLabelArialFettRot("Nikotinkarenz"),cctitr.xywh(7,4,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));	
		//titr.add(this.getLabelArialFettRot("Nikotinkarenz"),cctitr.xywh(7,5,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));

		eltern.bchb[18] = new JRtaCheckBox("");
		eltern.bchb[18].setName("F_131");
		titr.add(eltern.bchb[18],cctitr.xywh(5,7,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));
		titr.add(this.getLabelArialFettRot("Selbsthilfegruppe"),cctitr.xywh(7,7,1,2,CellConstraints.DEFAULT,CellConstraints.CENTER));	
		//titr.add(this.getLabelArialFettRot("Selbsthilfegruppe "),cctitr.xywh(7,8,1,1,CellConstraints.DEFAULT,CellConstraints.TOP));

		titr.getPanel().validate();
		vorschlag.add(titr.getPanel(),ccvorschlag.xy(6,4,CellConstraints.DEFAULT,CellConstraints.TOP));

		vorschlag.add(getRand(Color.GRAY),ccvorschlag.xywh(4,1,1,4,CellConstraints.DEFAULT,CellConstraints.FILL));
		
		// Jetzt die Erl�uterungen
		// linker Block �berschrift		
		links = new FormLayout(  "p","p");
		titl = new PanelBuilder(links);
		titl.getPanel().setOpaque(false);
		cctitl = new CellConstraints();
		lab = new JLabel("Erl�uterungen");
		lab.setFont(fontarialfett);
		titl.add(lab,cctitl.xy(1,1,CellConstraints.FILL,CellConstraints.TOP));
		titl.getPanel().validate();
		vorschlag.add(titl.getPanel(),ccvorschlag.xy(2,6));	
		eltern.bta[7] = new JTextArea();
		eltern.bta[7].setName("UNBEKANNT3");
		vorschlag.add(eltern.bta[7],ccvorschlag.xyw(2,8,5,CellConstraints.FILL,CellConstraints.FILL));

		vorschlag.add(getRand(Color.GRAY),ccvorschlag.xyw(1,10,7));

		links = new FormLayout(  "p","p");
		titl = new PanelBuilder(links);
		titl.getPanel().setOpaque(false);
		cctitl = new CellConstraints();
		lab = new JLabel("Letzte Medikation:");
		lab.setFont(fontarialfett);
		titl.add(lab,cctitl.xy(1,1,CellConstraints.FILL,CellConstraints.TOP));
		titl.getPanel().validate();
		vorschlag.add(titl.getPanel(),ccvorschlag.xy(2,12));

		eltern.bta[8] = new JTextArea();
		eltern.bta[8].setName("UNBEKANNT3");
		vorschlag.add(eltern.bta[8],ccvorschlag.xyw(2,14,5,CellConstraints.FILL,CellConstraints.FILL));
		
		vorschlag.getPanel().validate();
		return vorschlag.getPanel();
	}	
	/************************
	 * 
	 * Die Therapeutischen Leistungen, KTL-Legende und Besonderheiten im Verlauf
	 * 
	 */
	
	
	private JPanel getBlock1(){
		FormLayout laytherap = new FormLayout("1px,p:g,1px",
//				 1   2  3  4   5  6   7  8	 9  10  11	12	13 14
				"1px,p,1px");
				PanelBuilder therap = new PanelBuilder(laytherap);
				therap.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
				therap.getPanel().setOpaque(false);               
				CellConstraints cctherap = new CellConstraints();
				
				therap.add(getTherap(),cctherap.xy(2, 2));
				therap.getPanel().validate();
				return therap.getPanel();
	}
	private JPanel getTherap(){
		FormLayout laytherap = new FormLayout("0px,2dlu,p:g,2dlu,0px",
//				 1   2    3  4   5  6   7  8   9  10  11	12	13 14
				"0px,2dlu,p,2dlu,p,2dlu,p,2dlu,p,0px");
				PanelBuilder therap = new PanelBuilder(laytherap);
				//therap.getPanel().setBorder(BorderFactory.createLineBorder(Color.BLACK));
				therap.getPanel().setOpaque(false);               
				CellConstraints cctherap = new CellConstraints();
				therap.add(getLegende1(),cctherap.xy(3, 3));
				therap.add(getLeistung(1),cctherap.xy(3, 5));
				therap.add(getKodierung(),cctherap.xy(3, 7));
				therap.add(getBesonderheiten(),cctherap.xy(3, 9));
				therap.getPanel().validate();
				return therap.getPanel();
	}
	private JPanel getBesonderheiten(){
		FormLayout laybesonder = new FormLayout("2dlu,p:g,2dlu",
//				 1    2    3  4   5  6   7  8   9  10  11	12	13 14
				"2dlu,p,2dlu,100dlu,5dlu");
				PanelBuilder besonder = new PanelBuilder(laybesonder);
				besonder.getPanel().setOpaque(false);               
				CellConstraints ccbesonder = new CellConstraints();

				JLabel lab = new JLabel("Besonderheiten im Verlauf");
				lab.setFont(this.fontarialfett);
				besonder.add(lab,ccbesonder.xy(2,2,CellConstraints.LEFT,CellConstraints.DEFAULT));
				
				eltern.bta[7] = new JTextArea();
				eltern.bta[7].setName("UNBEKANNT2");
				besonder.add(eltern.bta[7],ccbesonder.xy(2, 4,CellConstraints.FILL,CellConstraints.FILL));
				
				besonder.getPanel().validate();
				return besonder.getPanel();
	}
	private JPanel getKodierung(){
		FormLayout layKodierung = new FormLayout(
				"fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,"+
				"fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10),2dlu,fill:0:grow(0.10)"
				,"p,2dlu,1px");
		PanelBuilder kodierung = new PanelBuilder(layKodierung); 
		kodierung.setOpaque(false);
		CellConstraints cckodierung = new CellConstraints();
		String [] code = {"A = ","B = ","C = ","D = ","E = ","F = ","G = ","H = ","I = ",
				"K = ","L = ","M = ","N = ","P = ","Q = ","R = ","S = ","T = ","U = ","Z = "};
		String [] zeit = {"5 Min.","10 Min.","15 Min.","20 Min.","25 Min.","30 Min.","35 Min.","40 Min.","45 Min.",
				"50 Min.","60 Min.","75 Min.","90 Min.","100 Min.","120 Min.","150 Min.","180 Min.","240 Min.","300 Min.","individuell"};
		int durchlauf = 1;
		int codes = 1;
		int xpos = 1;
		int item = 0;
		FormLayout layCelle = new FormLayout("p,p","p,p");
		PanelBuilder celle;
		CellConstraints cccelle;
		for(int i = 0; i < code.length;i+=2){
			celle = new PanelBuilder(layCelle);
			celle.getPanel().setOpaque(false);
			cccelle = new CellConstraints();
			celle.add(getLabel(code[item]),cckodierung.xy(1,1,CellConstraints.LEFT,CellConstraints.DEFAULT));
			celle.add(getLabelKleinRot(zeit[item]),cckodierung.xy(2,1,CellConstraints.RIGHT,CellConstraints.DEFAULT));
			celle.add(getLabel(code[item+1]),cckodierung.xy(1,2,CellConstraints.LEFT,CellConstraints.DEFAULT));
			celle.add(getLabelKleinRot(zeit[item+1]),cckodierung.xy(2,2,CellConstraints.RIGHT,CellConstraints.DEFAULT));
			celle.getPanel().validate();
			kodierung.add(celle.getPanel(),cckodierung.xy(xpos, 1));
			durchlauf ++;
			codes +=2;
			xpos += 2;
			item += 2;
		}
		kodierung.add(getRand(Color.GRAY),cckodierung.xywh(1, 3, 19, 1,CellConstraints.DEFAULT,CellConstraints.FILL));
		kodierung.getPanel().validate();
		return kodierung.getPanel();
	}

	private JPanel getLegende1(){		//     1    2    3    4    5     6               7      8     9    10     11      12
		FormLayout laydummy = new FormLayout("2dlu,15dlu,2dlu,1px,10dlu,fill:0:grow(1.0),10dlu,50dlu,10dlu,30dlu,10dlu,right:40dlu,10dlu",
		"1px,15dlu");
		PanelBuilder dummy = new PanelBuilder(laydummy);
		dummy.getPanel().setOpaque(false);

		CellConstraints ccdum = new CellConstraints();
		dummy.add(getLabelKleinRot("Pos."),ccdum.xy(2,2,CellConstraints.RIGHT,CellConstraints.CENTER));
		dummy.add(getLabelKleinRot("Leistung im Klartext"),ccdum.xy(6,2,CellConstraints.LEFT,CellConstraints.CENTER));
		dummy.add(getLabelKleinRot("KTL-Code"),ccdum.xy(8,2,CellConstraints.LEFT,CellConstraints.CENTER));
		dummy.add(getLabelKleinRot("Dauer"),ccdum.xy(10,2,CellConstraints.LEFT,CellConstraints.CENTER));
		dummy.add(getLabelKleinRot("Anzahl"),ccdum.xy(12,2,CellConstraints.LEFT,CellConstraints.CENTER));
		dummy.getPanel().validate();
		return dummy.getPanel();
		
	}
	
	private JPanel getLeistung(int seite){
		FormLayout layKopf = new FormLayout("fill:0:grow(1.0)",
			    //           5=F174  7=F175  9=F176   11=F177
				// 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6  
				  "p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p,p");		
		PanelBuilder kopf = new PanelBuilder(layKopf);
		kopf.setOpaque(false); 
		CellConstraints cckopf = new CellConstraints();
								//           1    2    3   4     5          6         7    8      9    10     11        12       13
		FormLayout dummy = new FormLayout("2dlu,15dlu,2dlu,1px,10dlu,fill:0:grow(1.0),10dlu,50dlu,10dlu,30dlu,10dlu,right:40dlu,10dlu",
		"1px,20dlu");
		PanelBuilder dum;
		CellConstraints ccdum;
		JLabel lab;
		for(int i = 0; i < 10; i++){
			dum = new PanelBuilder(dummy);
			dum.getPanel().setOpaque(false);
			ccdum = new CellConstraints();
			lab = new JLabel(new Integer(i+1+((seite-1)*25)).toString()+".");
			dum.add(lab,ccdum.xy(2,2,CellConstraints.RIGHT,CellConstraints.CENTER));
			dum.add(getRand(Color.GRAY),cckopf.xywh(4, 1, 1, 2,CellConstraints.DEFAULT,CellConstraints.FILL));
			dum.add(getRand(Color.GRAY),cckopf.xywh(1, 1, 13, 1,CellConstraints.FILL,CellConstraints.DEFAULT));				
			eltern.ktlcmb[i+((seite-1)*25)] = new JRtaComboBox();
			eltern.ktlcmb[i+((seite-1)*25)].setActionCommand(new Integer(i+((seite-1)*25)).toString());
			eltern.ktlcmb[i+((seite-1)*25)].addActionListener(this);
			eltern.ktlcmb[i+((seite-1)*25)].setMaximumRowCount( 35 );
			// 
			dum.add(eltern.ktlcmb[i+((seite-1)*25)],ccdum.xy(6, 2,CellConstraints.FILL,CellConstraints.CENTER));
			eltern.ktltfc[i+((seite-1)*25)] = new JRtaTextField("nix",false);
			eltern.ktltfc[i+((seite-1)*25)].setFont(fontcourier);
			eltern.ktltfc[i+((seite-1)*25)].setForeground(Color.BLUE);

			// 
			dum.add(eltern.ktltfc[i+((seite-1)*25)],ccdum.xy(8, 2,CellConstraints.FILL,CellConstraints.CENTER));
			eltern.ktltfd[i+((seite-1)*25)] = new JRtaTextField("nix",false);
			eltern.ktltfd[i+((seite-1)*25)].setFont(fontcourier);
			eltern.ktltfd[i+((seite-1)*25)].setForeground(Color.BLUE);

			// 
			dum.add(eltern.ktltfd[i+((seite-1)*25)],ccdum.xy(10, 2,CellConstraints.FILL,CellConstraints.CENTER));
			eltern.ktltfa[i+((seite-1)*25)] = new JRtaTextField("ZAHLEN",false);
			eltern.ktltfa[i+((seite-1)*25)].setFont(fontcourier);
			eltern.ktltfa[i+((seite-1)*25)].setForeground(Color.BLUE);
			// 
			dum.add(eltern.ktltfa[i+((seite-1)*25)],ccdum.xy(12, 2,CellConstraints.FILL,CellConstraints.CENTER));			
			dum.getPanel().validate();
			kopf.add(dum.getPanel(),cckopf.xy(1,(i+1)) );
			/*
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		}
		dummy = new FormLayout("2dlu,15dlu,2dlu,1px,10dlu,fill:0:grow(1.0),10dlu,50dlu,10dlu,30dlu,10dlu,right:40dlu,10dlu",
		"1px,0dlu");
		dum = new PanelBuilder(dummy);
		dum.getPanel().setOpaque(false);
		ccdum = new CellConstraints();
		dum.add(getRand(Color.GRAY),cckopf.xywh(1, 1, 13, 1,CellConstraints.FILL,CellConstraints.DEFAULT));
		kopf.add(dum.getPanel(),cckopf.xy(1,26) );
		kopf.getPanel().validate();
		return kopf.getPanel();
	}	
	/************************
	 * 
	 * Der Titel Therap. Leist
	 * 
	 */
	private JPanel getTitelLeistungen(){
		FormLayout laytit = new FormLayout("0dlu,p",
		"2dlu,p,0dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		JLabel lab = new JLabel("Therapeutische Leistungen");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(2, 2));
		tit.getPanel().validate();
		return tit.getPanel();	
	}	
	/************************
	 * 
	 * Der Titel Vorschl�ge...
	 * 
	 */
	private JPanel getTitelVorschlaege(){
		FormLayout laytit = new FormLayout("0dlu,p,4dlu,p",
				"0dlu,p,0dlu");
		PanelBuilder tit = new PanelBuilder(laytit);
		//tit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		tit.setOpaque(false);               
		CellConstraints cctit = new CellConstraints();
		JLabel lab = new JLabel("Vorschl�ge f�r nachfolgende Ma�nahmen");
		lab.setFont(fontarialfett);
		tit.add(lab,cctit.xy(2,2));
		lab = new JLabel("Zutreffendes bitte ankreuzen (X), Mehrfachnennungen sind m�glich");
		lab.setFont(fontarialnormal);
		tit.add(lab,cctit.xy(4,2));
		tit.getPanel().validate();
		return tit.getPanel();
	}

	private JLabel getLabel(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontklein);
		return lab;
	}
	private JLabel getLabelArialNormal(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontarialnormal);
		return lab;
	}
	private JLabel getLabelArialNormalRot(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontarialnormal);
		lab.setForeground(Color.RED);
		return lab;
	}
	private JLabel getLabelArialFettRot(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontarialfett);
		lab.setForeground(Color.RED);
		return lab;
	}
	private JLabel getLabelArialFettNormal(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontarialfett);
		return lab;
	}
	private JLabel getLabelKleinRot(String text){
		JLabel lab = new JLabel(text);
		lab.setFont(fontklein);
		lab.setForeground(Color.RED);
		return lab;
	}
	private JPanel getRand(Color col){
		JPanel pan = new JPanel();
		pan.setOpaque(false);
		pan.setBorder(BorderFactory.createLineBorder(col));
		return pan;
	}	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

