package verkauf;

import hauptFenster.Reha;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Date;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.thera_pi.nebraska.gui.utils.ButtonTools;
import org.thera_pi.nebraska.gui.utils.JCompTools;
import org.thera_pi.nebraska.gui.utils.StringTools;

import sqlTools.SqlInfo;
import systemEinstellungen.INIFile;
import systemEinstellungen.SystemConfig;
import systemTools.JRtaTextField;
import verkauf.model.Artikel;
import verkauf.model.ArtikelVerkauf;
import verkauf.model.Verkauf;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.internal.text.TextFieldService;
import ag.ion.bion.officelayer.internal.text.TextTableService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextTable;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.internal.printing.PrintProperties;
import ag.ion.noa.printing.IPrinter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.star.awt.Size;

public class VerkaufGUI extends JXPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6537113748627245247L;
	ActionListener al = null;
	KeyListener kl = null;
	FocusListener fl = null;
	MouseListener ml = null;
	JRtaTextField[] edits = {null,null,null,null,null,null,null,null,null};
	JButton[] buts = {null,null,null,null};
	public JXTable vktab = null;
	public DefaultTableModel vkmod = new DefaultTableModel();
	JScrollPane jscr = null;
	int lastcol = 6;
	JLabel einheitAnzahlLabel = null;
	String[] column = {"Artikel-ID", "Beschreibung", "Einzel-Preis", "Anzahl", "Gesamt-Preis", "MwSt.", ""};
	
	ArtikelVerkauf aktuellerArtikel = null;
	verkauf.model.Verkauf verkauf = null;
	DecimalFormat df = null;
	INIFile settings = null;
	
	public VerkaufGUI(){
		super();
		this.activateListener();
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		this.add(this.getContent1(), BorderLayout.CENTER);
		verkauf = new Verkauf();
		df = new DecimalFormat("0.00");
		settings = new INIFile(Reha.proghome +"ini/"+ Reha.aktIK +"/verkauf.ini");

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setzeFocus();
			}
		});


	}
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				edits[0].requestFocus();
			}
		});
	}
	private JXPanel getContent1(){
		JXPanel pan = new JXPanel();
		JLabel lab = null;
		/**************/
		//				  1       2     3       4     5      6     7     8     9     10   11    12      13     14       15   16     17
		String xwerte = "5dlu,60dlu, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu, 60dlu:g, 5dlu, 60dlu, 5dlu, 60dlu, 5dlu, 28dlu,  5dlu, 27dlu, 5dlu";
		//				  1   2    3    4   5      6        7     8   9   10  11   12  13   14  15   16  17
		String ywerte = "5dlu, p, 1dlu, p, 10dlu, 150dlu:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu ";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		
		/******Legende********/
		lab = new JLabel("Artikel-ID");
		lab.setIcon(SystemConfig.hmSysIcons.get("kleinehilfe"));
		lab.addMouseListener(ml);
		pan.add(lab,cc.xy(2,2));
		lab = new JLabel("Beschreibung");
		pan.add(lab,cc.xyw(8,2,3));
		einheitAnzahlLabel = new JLabel("Anzahl / Einheit");
		pan.add(einheitAnzahlLabel,cc.xy(4, 2));
		lab = new JLabel("Gesamtpreis");
		pan.add(lab,cc.xy(12, 2));
		lab = new JLabel("Rabatt");
		pan.add(lab,cc.xy(6, 2));
		
		/******Edits und 2 Button********/
		pan.add( (edits[0] = new JRtaTextField("ZAHLEN",true)),cc.xy(2,4));
		edits[0].setName("artikelid");
		edits[0].addFocusListener(fl);
		edits[0].addKeyListener(kl);
		
		pan.add( (edits[1] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xy(4,4));
		edits[1].setName("anzahl");
		edits[1].setText("1,00");
		edits[1].addFocusListener(fl);
		edits[1].addKeyListener(kl);
		
		pan.add( (edits[2] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xy(6,4));
		edits[2].setText("0,00");
		edits[2].setName("artikelRabatt");
		edits[2].addFocusListener(fl);
		edits[2].addKeyListener(kl);
		
		pan.add( (edits[3] = new JRtaTextField("nix",true)),cc.xyw(8,4,3));
		edits[3].setName("beschreibung");
		edits[3].addFocusListener(fl);
		edits[3].addKeyListener(kl);
		
		pan.add( (edits[4] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xy(12, 4));
		edits[4].setName("preis");
		edits[4].addFocusListener(fl);
		edits[4].addKeyListener(kl);
		
		pan.add( (buts[0] = ButtonTools.macheBut("+", "uebernahme", al)),cc.xy(14,4));
		buts[0].setMnemonic(KeyEvent.VK_PLUS);
		pan.add( (buts[3] = ButtonTools.macheBut("-", "loesche", al)),cc.xy(16,4));
		buts[3].setMnemonic(KeyEvent.VK_MINUS);
		
		/******Tabelle********/
		vkmod.setColumnIdentifiers(column);
		vktab = new JXTable(vkmod);
		vktab.setEditable(false);
		vktab.getColumn(lastcol).setMinWidth(0);
		vktab.getColumn(lastcol).setMaxWidth(0);
		jscr = JCompTools.getTransparentScrollPane(vktab);
		jscr.validate();
		pan.add(jscr,cc.xyw(2, 6, 15));
		
		/******Summe / Steuer / Rabatt ********/
		lab = new JLabel("Rabatt:");
		pan.add(lab, cc.xy(12, 8));
		lab = new JLabel("Summe:");
		pan.add(lab,cc.xy(12,10));
		lab = new JLabel("MwSt. 7%:");
		pan.add(lab,cc.xy(12,12));
		lab = new JLabel("MwSt. 19%");
		pan.add(lab,cc.xy(12,14));
		
		pan.add((edits[5] = new JRtaTextField("ZAHLEN", true)), cc.xyw(14, 8, 3));
		edits[5].setName("gesamtRabatt");
		edits[5].addFocusListener(fl);
		
		edits[5].setText("0");
		pan.add( (edits[6] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xyw(14, 10, 3));
		edits[6].setEditable(false);
		edits[6].setText("0,00");
		pan.add( (edits[7] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xyw(14, 12,3));
		edits[7].setEditable(false);
		edits[7].setText("0,00");
		pan.add( (edits[8] = new JRtaTextField("FL",true,"6.2","RECHTS")),cc.xyw(14, 14, 3));
		edits[8].setEditable(false);
		edits[8].setText("0,00");
		
		
		/******Steuerbuttons********/
		pan.add( (buts[1] = ButtonTools.macheBut("Barzahlung", "barzahlung", al)),cc.xy(12, 16));
		buts[1].setMnemonic(KeyEvent.VK_B);
		pan.add( (buts[2] = ButtonTools.macheBut("Rechnung", "rechnung", al)),cc.xyw(14, 16, 3));
		buts[2].setMnemonic(KeyEvent.VK_N);
		/*************/
		pan.validate();
		edits[0].requestFocus();
		return pan;
	}
	
	private void activateListener(){
		ml = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				UebergabeTool ean = new UebergabeTool("");
				Point position = getLocation();
				Dimension dim = getSize();
				position.setLocation((position.getX() + (dim.getWidth() / 2)), position.getY());
				
				new ArtikelSuchenDialog(null, ean, position);
				edits[0].setText(ean.getString());
				edits[0].requestFocus();
				if(!ean.getString().equals("")) {
					edits[1].requestFocus();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
			
		};
		
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("uebernahme") && aktuellerArtikel != null){
					aktuellerArtikel.setAnzahl(Double.parseDouble(edits[1].getText().replace(",", ".")));
					aktuellerArtikel.gewaehreRabatt(Double.parseDouble(edits[2].getText().replace(",", ".")));
					aktuellerArtikel.setBeschreibung(edits[3].getText());
					aktuellerArtikel.setPreis(Double.parseDouble(edits[4].getText().replace(",", ".")) / Double.parseDouble(edits[1].getText().replace(",", ".")));
					
					verkauf.fügeArtikelHinzu(aktuellerArtikel);
					aktuellerArtikel = null;
					
					edits[6].setText(df.format(verkauf.getBetragBrutto()));
					edits[7].setText(df.format(verkauf.getBetrag7()));
					edits[8].setText(df.format(verkauf.getBetrag19()));
					
					setzeFelderzurueck();
					
					vkmod.setDataVector(verkauf.liefereTabDaten(), column);
					vktab.getColumn(lastcol).setMinWidth(0);
					vktab.getColumn(lastcol).setMaxWidth(0);
					edits[0].requestFocus();
					return;
				}
				else if(cmd.equals("loesche")){
					if(vkmod.getRowCount() > 0) {
						verkauf.loescheArtikel((Integer.parseInt((String)vkmod.getValueAt(vktab.getSelectedRow(), 6))));
						vkmod.setDataVector(verkauf.liefereTabDaten(), column);
						vktab.getColumn(lastcol).setMinWidth(0);
						vktab.getColumn(lastcol).setMaxWidth(0);
						edits[6].setText(df.format(verkauf.getBetragBrutto()));
						edits[7].setText(df.format(verkauf.getBetrag7()));
						edits[8].setText(df.format(verkauf.getBetrag19()));
					}
					return;
				}else if(cmd.equals("barzahlung")){
					bonEnde();
					return;
				}else if(cmd.equals("rechnung")){
					rechnungEnde();
					return;
				}
			}
		};
		
		kl = new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_F9) {
					edits[0].requestFocus();
					verkauf.fügeArtikelHinzu(aktuellerArtikel);
					aktuellerArtikel = null;
					setzeFelderzurueck();
					edits[6].setText(df.format(verkauf.getBetragBrutto()));
					edits[7].setText(df.format(verkauf.getBetragBrutto()));
					edits[8].setText(df.format(verkauf.getBetragBrutto()));
					vkmod.setDataVector(verkauf.liefereTabDaten(), column);
					vktab.getColumn(lastcol).setMinWidth(0);
					vktab.getColumn(lastcol).setMaxWidth(0);
					
				}
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
				if(arg0.getKeyChar() == '?') {
					UebergabeTool ean = new UebergabeTool("");
					Point position = getLocation();
					Dimension dim = getSize();
					position.setLocation((position.getX() + (dim.getWidth() / 2)), position.getY());
					
					new ArtikelSuchenDialog(null, ean, position);
					edits[0].setText(ean.getString());
					edits[0].requestFocus();
					if(!ean.getString().equals("")) {
						edits[1].requestFocus();
					}
				}
			}
		};

		fl = new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
			}
			
			@Override
			public void focusLost(FocusEvent arg0) {
				try {
				if( ((JComponent)arg0.getSource()).getName().equals("artikelid")){
					Long ean = Long.parseLong(edits[0].getText());
					if(Artikel.artikelExistiert(ean)) {
						aktuellerArtikel = new ArtikelVerkauf(ean);	
						edits[3].setText(aktuellerArtikel.getBeschreibung());
						edits[4].setText(df.format(aktuellerArtikel.getPreis()));
						einheitAnzahlLabel.setText("Anzahl / " + aktuellerArtikel.getEinheit());
					} else {
						JOptionPane.showMessageDialog(null, "Und morgen verkaufst du deinen Chef? - den Artikel gibt es nicht vorhanden!");
						edits[0].requestFocus();
					}
				} else if(((JComponent)arg0.getSource()).getName().equals("anzahl")) {
					if(aktuellerArtikel != null) {
						aktuellerArtikel.setAnzahl(Double.parseDouble(edits[1].getText().replace(",", ".")));
						edits[4].setText(df.format(aktuellerArtikel.getPreis() * aktuellerArtikel.getAnzahl()));
					}
				} else if(((JComponent)arg0.getSource()).getName().equals("artikelRabatt")) {
					if(aktuellerArtikel != null) {
						aktuellerArtikel.gewaehreRabatt(Double.parseDouble(edits[2].getText().replace(",", ".")));
						edits[4].setText(df.format(aktuellerArtikel.getPreis() * aktuellerArtikel.getAnzahl()));
					}
				} else if(((JComponent)arg0.getSource()).getName().equals("beschreibung")) {
					if(aktuellerArtikel != null) {
						aktuellerArtikel.setBeschreibung(edits[3].getText());
					}
				} else if(((JComponent)arg0.getSource()).getName().equals("preis")) {
					if(aktuellerArtikel != null) {
						aktuellerArtikel.setPreis(Double.parseDouble(edits[4].getText().replace(",", ".")));
					}
				}else if(((JComponent)arg0.getSource()).getName().equals("gesamtRabatt")) {
					verkauf.gewaehreRabatt(Double.parseDouble(edits[5].getText()));
					edits[6].setText(df.format(verkauf.getBetragBrutto()));
					edits[7].setText(df.format(verkauf.getBetrag7()));
					edits[8].setText(df.format(verkauf.getBetrag19()));
				}
			}
				catch(Exception e) {}
			}
			
		};
	}
	
	private void bonEnde() {
		if(verkauf.getAnzahlPositionen() != 0) {
		Point position = getLocation();
		Dimension dim = getSize();
		position.setLocation((position.getX() + (dim.getWidth() / 2)), position.getY());
		new WechselgeldDialog(null, position, verkauf.getBetragBrutto());
		
		String propSection = "Bon";
		String nummernkreis = "VB-"+ SqlInfo.erzeugeNummer("vbon");
		
		
		IOfficeApplication application = Reha.officeapplication;
		try {
			IDocumentService service = application.getDocumentService();
			IDocumentDescriptor descriptor = new DocumentDescriptor();
			descriptor.setHidden(settings.getBooleanProperty(propSection, "SofortDrucken"));
			descriptor.setAsTemplate(true);
			
			String url = Reha.proghome + "vorlagen/"+ Reha.aktIK + "/" + settings.getStringProperty(propSection, "Vorlage");
			ITextDocument doc = (ITextDocument) service.loadDocument(url, descriptor);
			if(settings.getBooleanProperty(propSection, "SeitenLaengeAendern")) {
				Size page = (Size) doc.getPageService().getPage(0).getPageStyle().getProperties().getXPropertySet().getPropertyValue("Size");
				page.Height = page.Height + verkauf.getAnzahlPositionen() * settings.getIntegerProperty(propSection, "ProArtikelSeitenLaenge");
				doc.getPageService().getPage(0).getPageStyle().getProperties().getXPropertySet().setPropertyValue("Size", page);
			}
						
			TextFieldService feldservice = (TextFieldService) doc.getTextFieldService();
			ITextField[] felder = feldservice.getPlaceholderFields();
			for(int i = 0; i < felder.length; i++) {
				if(felder[i].getDisplayText().equals("<Rrabatt>")) {
					felder[i].getTextRange().setText(df.format(verkauf.getRabatt()));
				} else if(felder[i].getDisplayText().equals("<Rnummer>")) {
					felder[i].getTextRange().setText(nummernkreis);
				} else if(felder[i].getDisplayText().equals("<Rbrutto>")) {
					felder[i].getTextRange().setText(df.format(verkauf.getBetragBrutto()));
				} else if(felder[i].getDisplayText().equals("<Rmwst7>")) {
					felder[i].getTextRange().setText(df.format(verkauf.getBetrag7()));
				} else if(felder[i].getDisplayText().equals("<Rmwst19>")) {
					felder[i].getTextRange().setText(df.format(verkauf.getBetrag19()));
				} else if(felder[i].getDisplayText().equals("<Rnetto>")) {
					felder[i].getTextRange().setText(df.format(verkauf.getBetragBrutto() - verkauf.getBetrag19() - verkauf.getBetrag7()));
				}
			}
			TextTableService tservice = (TextTableService) doc.getTextTableService();
			ITextTable[] tables = tservice.getTextTables();
			fuelleTabelle(tables[0], propSection);
			
			if(settings.getBooleanProperty(propSection, "SofortDrucken")) {
				String druckername = settings.getStringProperty(propSection, "Drucker");
				IPrinter drucker = null;
				if(druckername == null) {
					drucker = doc.getPrintService().getActivePrinter();
				} else {
					drucker = doc.getPrintService().createPrinter(druckername);
				}
				doc.getPrintService().setActivePrinter(drucker);
				PrintProperties printprop = new PrintProperties((short) 1, null);
				doc.getPrintService().print(printprop);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fehler: "+ e.getMessage() +"!");
			e.printStackTrace();
		}
		schreibeUmsatzDaten(nummernkreis, 0.00, -100);
		verkauf.fuehreVerkaufdurch(-100, nummernkreis);
		verkauf = new Verkauf();
		vkmod.setDataVector(verkauf.liefereTabDaten(), column);
		edits[0].requestFocus();
		edits[5].setText("0");
		edits[6].setText("0,00");
		edits[7].setText("0,00");
		edits[8].setText("0");
		}
	}
	
	private void rechnungEnde() {
		if(verkauf.getAnzahlPositionen() != 0) {
		if(Reha.thisClass.patpanel != null) {
			String name = Reha.thisClass.patpanel.patDaten.get(2) , vorname = Reha.thisClass.patpanel.patDaten.get(3),
					adresse = Reha.thisClass.patpanel.patDaten.get(21), plz = Reha.thisClass.patpanel.patDaten.get(23), 
					ort = Reha.thisClass.patpanel.patDaten.get(24), anrede = Reha.thisClass.patpanel.patDaten.get(0);
			String propSection = "Rechnung";
			String nummernkreis = "VR-"+ SqlInfo.erzeugeNummer("vrechnung");
			int patid = Integer.parseInt(Reha.thisClass.patpanel.patDaten.get(29));
			
			
			IOfficeApplication application = Reha.officeapplication;
			try {
				IDocumentService service = application.getDocumentService();
				IDocumentDescriptor descriptor = new DocumentDescriptor();
				descriptor.setHidden(settings.getBooleanProperty(propSection, "SofortDrucken"));
				descriptor.setAsTemplate(true);
				
				String url = Reha.proghome + "vorlagen/"+ Reha.aktIK + "/" + settings.getStringProperty(propSection, "Vorlage");
				ITextDocument doc = (ITextDocument) service.loadDocument(url, descriptor);
				TextFieldService feldservice = (TextFieldService) doc.getTextFieldService();
				ITextField[] felder = feldservice.getPlaceholderFields();
				for(int i = 0; i < felder.length; i++) {
					if(felder[i].getDisplayText().equals("<Pname>")) {
						
					} else if(felder[i].getDisplayText().equals("<Pnname>")) {
						felder[i].getTextRange().setText(StringTools.EGross(name));
					} else if(felder[i].getDisplayText().equals("<Pvname>")) {
						felder[i].getTextRange().setText(StringTools.EGross(vorname));
					} else if(felder[i].getDisplayText().equals("<Panrede>")) {
						if(anrede.equals("HERR")) {
							felder[i].getTextRange().setText("Sehr geehrter Herr " + StringTools.EGross(name));
						} else {
							felder[i].getTextRange().setText("Sehr geehrter Frau " + StringTools.EGross(name));
						}
					} else if(felder[i].getDisplayText().equals("<Padr>")) {
						felder[i].getTextRange().setText(StringTools.EGross(adresse));
					} else if(felder[i].getDisplayText().equals("<Pplz>")) {
						felder[i].getTextRange().setText(StringTools.EGross(plz));
					} else if(felder[i].getDisplayText().equals("<Port>")) {
						felder[i].getTextRange().setText(StringTools.EGross(ort));
					} else if(felder[i].getDisplayText().equals("<Rnetto>")) {
						felder[i].getTextRange().setText(df.format(verkauf.getBetragBrutto() - verkauf.getBetrag19() - verkauf.getBetrag7()));
					} else if(felder[i].getDisplayText().equals("<Rmwst7>")) {
						felder[i].getTextRange().setText(df.format(verkauf.getBetrag7()));
					} else if(felder[i].getDisplayText().equals("<Rmwst19>")) {
						felder[i].getTextRange().setText(df.format(verkauf.getBetrag19()));
					} else if(felder[i].getDisplayText().equals("<Rbrutto>")) {
						felder[i].getTextRange().setText(df.format(verkauf.getBetragBrutto()));
					} else if(felder[i].getDisplayText().equals("<Rrabatt>")) {
						felder[i].getTextRange().setText(df.format(verkauf.getRabatt()));
					} else if(felder[i].getDisplayText().equals("<Rnummer>")) {
						felder[i].getTextRange().setText(nummernkreis);
					}	
				}
				TextTableService tservice = (TextTableService) doc.getTextTableService();
				ITextTable[] tables = tservice.getTextTables();
				fuelleTabelle(tables[0], propSection);
				
				if(settings.getBooleanProperty(propSection, "SofortDrucken")) {
					String druckername = settings.getStringProperty(propSection, "Drucker");
					IPrinter drucker = null;
					if(druckername == null) {
						drucker = doc.getPrintService().getActivePrinter();
					} else {
						drucker = doc.getPrintService().createPrinter(druckername);
					}
					doc.getPrintService().setActivePrinter(drucker);
					PrintProperties printprop = new PrintProperties(settings.getIntegerProperty(propSection, "Exemplare").shortValue(), null);
					doc.getPrintService().print(printprop);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Fehler: "+ e.getMessage() +"!");
				e.printStackTrace();
			}
			verkauf.fuehreVerkaufdurch(patid, nummernkreis);
			schreibeUmsatzDaten(nummernkreis, verkauf.getBetragBrutto(), patid);
			verkauf = new Verkauf();
			vkmod.setDataVector(verkauf.liefereTabDaten(), column);
			edits[0].requestFocus();
			edits[5].setText("0");
			edits[6].setText("0,00");
			edits[7].setText("0,00");
			edits[8].setText("0");
			
		} else {
			JOptionPane.showMessageDialog(null, "Bitte erst Patientenfenster öffnen und Patienten auswählen!");
			// vlt. von Selbst Patientenfenster öffnen?
		}
		}
	}
	
	private void fuelleTabelle(ITextTable tabelle, String propSection) throws TextException {
		ArtikelVerkauf[] positionen = verkauf.liefereArtikel();
		for(int n = 0; n < positionen.length; n++) {
			tabelle.addRow(n+1, 1);
			for(int m = 0; m < settings.getIntegerProperty(propSection, "Spaltenanzahl"); m++) {
				String spaltenname = settings.getStringProperty(propSection, "Spalte" +(m+1));
				if(spaltenname.equals("ArtikelID")) {
					tabelle.getCell(m, n+1).getTextService().getText().setText(String.valueOf(positionen[n].getEan()));
				} else if(spaltenname.equals("MwSt")) {
					tabelle.getCell(m, n+1).getTextService().getText().setText(String.valueOf(positionen[n].getMwst()));
				} else if(spaltenname.equals("Anzahl")) {
					tabelle.getCell(m, n+1).getTextService().getText().setText(String.valueOf(positionen[n].getAnzahl()));
				} else if(spaltenname.equals("Beschreibung")) {
					tabelle.getCell(m, n+1).getTextService().getText().setText(positionen[n].getBeschreibung());
				} else if(spaltenname.equals("EinzelPreis")) {
					tabelle.getCell(m, n+1).getTextService().getText().setText(df.format(positionen[n].getPreis()));
				} else if(spaltenname.equals("GesamtPreis")) {
					tabelle.getCell(m, n+1).getTextService().getText().setText(df.format(positionen[n].getPreis() * positionen[n].getAnzahl()));
				} else if(spaltenname.equals("Rabatt")) {
					tabelle.getCell(m, n+1).getTextService().getText().setText(df.format(positionen[n].getRabatt()));
				} else if(spaltenname.equals("Bemerkung")) {
					String inhalt = "";
					if(positionen[n].getMwst() == 7) {
						inhalt += "+ ";
					}
					if(positionen[n].getMwst() == 19) {
						inhalt += "* ";
					}
					if(positionen[n].getRabatt() != 0) {
						inhalt += "%";
					}
					
					tabelle.getCell(m, n+1).getTextService().getText().setText(inhalt);
				} else if(spaltenname.equals("NettoPreis")) {
					tabelle.getCell(m, n+1).getTextService().getText().setText(df.format(positionen[n].getPreis() / (1 + (positionen[n].getMwst()))));
				}
			}
		}
	}

	private void setzeFelderzurueck() {
		edits[0].setText("");
		edits[1].setText("1,00");
		einheitAnzahlLabel.setText("Anzahl");
		edits[2].setText("0,00");
		edits[3].setText("");
		edits[4].setText("");
	}
	
	private void schreibeUmsatzDaten(String vnummer, double offen, int patid) {
		Date date = new Date(System.currentTimeMillis());
		String sql ="INSERT INTO `therapi_entwicklung`.`verkliste` (`verklisteID`, `v_nummer`, `v_datum`, `v_betrag`, `r_mwst7`, `r_mwst19`, `v_offen`, `v_bezahldatum`, `mahndat1`, `mahndat2`, `mahndat3`, `mahnsperre`, `pat_id`) " +
				"VALUES (NULL, '"+ vnummer +"', '"+ date.toString() +"', '"+ verkauf.getBetragBrutto() +"', '"+ verkauf.getBetrag7() +"', '"+ verkauf.getBetrag19() +"', '"+ offen +"', NULL, NULL, NULL, NULL, '0', '"+ patid +"');";
		SqlInfo.sqlAusfuehren(sql);
	}
	
}
