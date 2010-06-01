package patientenFenster;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.therapi.reha.patient.AktuelleRezepte;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dialoge.PinPanel;
import dialoge.RehaSmartDialog;
import events.RehaTPEvent;
import events.RehaTPEventClass;
import events.RehaTPEventListener;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextField;
import ag.ion.bion.officelayer.text.ITextFieldService;
import ag.ion.bion.officelayer.text.TextException;
import ag.ion.noa.NOAException;
import ag.ion.noa.printing.IPrinter;
import hauptFenster.Reha;
import sqlTools.ExUndHop;
import sqlTools.SqlInfo;
import systemEinstellungen.SystemConfig;
import systemTools.Colors;
import systemTools.JRtaTextField;
import terminKalender.DatFunk;

public class RezeptGebuehren extends RehaSmartDialog implements RehaTPEventListener,WindowListener, ActionListener{
	boolean nurkopie;
	boolean aushistorie;
	public JButton okknopf;
	public JRtaTextField gegeben;
	public JLabel rueckgeld;
	public JCheckBox direktdruck;
	private RehaTPEventClass rtp = null;
	private RgebHintergrund rgb;	
	CompoundPainter cp = null;
	MattePainter mp = null;
	LinearGradientPaint p = null;
	private AktuelleRezepte aktuelleRezepte;
	public RezeptGebuehren(AktuelleRezepte aktrez,boolean kopie,boolean historie,Point pt){
		super(null,"RezeptGebuehr");
		if(aktrez!=null){
			this.aktuelleRezepte = aktrez;			
		}
		this.nurkopie = kopie;
		this.aushistorie = historie;
		
		PinPanel pinPanel = new PinPanel();
		pinPanel.setName("RezeptGebuehr");
		pinPanel.getGruen().setVisible(false);
		setPinPanel(pinPanel);
		getSmartTitledPanel().setTitle("Rezept-Gebühr");
		getSmartTitledPanel().setName("RezeptGebuehr");
		setSize(175,250);
		setPreferredSize(new Dimension(175,250));
		getSmartTitledPanel().setPreferredSize(new Dimension (175,250));
		setPinPanel(pinPanel);
		rgb = new RgebHintergrund();
		rgb.setLayout(new BorderLayout());
		new SwingWorker<Void,Void>(){

			@Override
			protected Void doInBackground() throws Exception {
			     rgb.setBackgroundPainter(Reha.thisClass.compoundPainter.get("RezeptGebuehren"));		
				return null;
			}
			
		}.execute();	
		rgb.add(getGebuehren(),BorderLayout.CENTER);
		
		getSmartTitledPanel().setContentContainer(rgb);
		getSmartTitledPanel().getContentContainer().setName("RezeptGebuehr");
	    setName("RezeptGebuehr");
		setModal(true);
	    Point lpt = new Point(pt.x-125,pt.y+30);
	    setLocation(lpt);
	    
		rtp = new RehaTPEventClass();
		rtp.addRehaTPEventListener((RehaTPEventListener) this);

		pack();
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 		   setVisible(true);
		 	   }
		});
		
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 		 setzeFocus();
		 	   }
		});
				
	    


	}
	
	private void setzeFocus(){
		SwingUtilities.invokeLater(new Runnable(){
		 	   public  void run()
		 	   {
		 			gegeben.requestFocus();		 		   
		 	   }
		});
	}
/****************************************************/	

	private JPanel getGebuehren(){
		FormLayout lay = new FormLayout("10dlu,fill:0:grow(0.50),right:max(40dlu;p),5dlu,40dlu,fill:0:grow(0.50),10dlu",
									//     1   2  3    4  5    6  7   8  9   10   11  12 
										"15dlu,p,10dlu,p,10dlu,p,4dlu,p,4dlu,p,  20dlu,p,15dlu");
		PanelBuilder pb = new PanelBuilder(lay);
		CellConstraints cc = new CellConstraints();

		pb.getPanel().setOpaque(false);
		direktdruck = new JCheckBox("Quittung direkt drucken");
		direktdruck.setOpaque(false);
		direktdruck.setSelected(true);			


		pb.add(direktdruck,cc.xyw(3,2,3));

		pb.addSeparator("Für Rechenkünstler",cc.xyw(2,4,5));

		pb.addLabel("Rezeptgebühren",cc.xy(3,6));
		JLabel lab = new JLabel(SystemConfig.hmAdrRDaten.get("<Rendbetrag>"));
		lab.setFont(new Font("Tahoma",Font.BOLD,14));
		lab.setForeground(Color.BLUE);
		pb.add(lab,cc.xy(5,6));
		
		pb.addLabel("gegeben",cc.xy(3,8));
		gegeben = new JRtaTextField("D",true,"6.2","RECHTS");
		gegeben.setDValueFromS(SystemConfig.hmAdrRDaten.get("<Rendbetrag>"));
		gegeben.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent event) {
				if(event.getKeyCode()==10){
					event.consume();					
				}
			}
			public void keyPressed(KeyEvent event) {
				if(event.getKeyCode()==10){
					event.consume();					
				}
				if(event.getKeyCode()==27){
					dispose();
				}
			}
		    public void keyReleased(KeyEvent event) {
		    	if(event.getKeyCode()==10){
		    		event.consume();
		    	}else{
		    		DecimalFormat df = new DecimalFormat ( "#####0.00" );
		    		Double test = gegeben.getDValueFromS();
		    		Double rg = new Double(SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replaceAll(",","."));
		    		Double rgtest = new Double(test-rg);
		    		if(rgtest <  0.00){
		    			rueckgeld.setForeground(Color.RED);
		    		}else{
		    			rueckgeld.setForeground(Color.BLUE);
		    		}
		    		rueckgeld.setText(df.format(rgtest) );
		    		////System.out.println("Text = "+gegeben.getText()+ " inhalt von Test = "+test.toString());
		    	}
		    }
		});

		pb.add(gegeben,cc.xy(5,8));
		
		pb.addLabel("zurück",cc.xy(3,10));
		rueckgeld = new JLabel("0,00");
		rueckgeld.setFont(new Font("Tahoma",Font.BOLD,14));
		rueckgeld.setForeground(Color.BLUE);
		pb.add(rueckgeld,cc.xy(5,10));
		if(this.nurkopie){
			okknopf = new JButton("Quittung drucken (Kopie)");			
		}else{
			okknopf = new JButton("Quittung drucken & buchen");	
		}
		okknopf.addActionListener(this);
		okknopf.setActionCommand("okknopf");
		okknopf.setName("okknopf");
		okknopf.addKeyListener(this);
		pb.add(okknopf,cc.xyw(3,12,3));
		
		pb.getPanel().validate();
		return pb.getPanel();
	}
/****************************************************/	
	public void rezGebDrucken(){
		String url = "";
		if( ((String)Reha.thisClass.patpanel.vecaktrez.get(43)).equals("T") ){
			url = SystemConfig.rezGebVorlageHB;
		}else{
			url = SystemConfig.rezGebVorlageNeu;			
		}
	
		// Wenn Hausbesuch andere Vorlage.....
		IDocumentService documentService = null;;
		
		try {
			documentService = Reha.officeapplication.getDocumentService();
		} catch (OfficeApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        IDocumentDescriptor docdescript = new DocumentDescriptor();
        //docdescript.setHidden(true);
		if(!direktdruck.isSelected()){
	        docdescript.setHidden(false);			
		}else{
	        docdescript.setHidden(true);			
		}

        docdescript.setAsTemplate(true);
		IDocument document = null;
		try {
			document = documentService.loadDocument(url,docdescript);

		} catch (NOAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**********************/
		ITextDocument textDocument = (ITextDocument)document;
		String druckerName = null;
		try {
			druckerName = textDocument.getPrintService().getActivePrinter().getName();
		} catch (NOAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Wenn nicht gleich wie in der INI angegeben -> Drucker wechseln
		IPrinter iprint = null;
		if(! druckerName.equals(SystemConfig.rezGebDrucker)){
			try {
				iprint = (IPrinter) textDocument.getPrintService().createPrinter(SystemConfig.rezGebDrucker);
			} catch (NOAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				textDocument.getPrintService().setActivePrinter(iprint);
			} catch (NOAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**********************/
		ITextFieldService textFieldService = textDocument.getTextFieldService();
		ITextField[] placeholders = null;
		try {
			placeholders = textFieldService.getPlaceholderFields();
		} catch (TextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < placeholders.length; i++) {
			String placeholderDisplayText = placeholders[i].getDisplayText();
			////System.out.println("Platzhalter-Name = "+placeholderDisplayText);
			placeholders[i].getTextRange().setText(SystemConfig.hmAdrRDaten.get(placeholderDisplayText));
		}
		if(direktdruck.isSelected()){
			try {
				textDocument.print();
				textDocument.close();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			final ITextDocument xtextDocument = textDocument;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					//Reha.officeapplication.getDocumentService().
					xtextDocument.getFrame().getXFrame().activate();
				}
			});
		}

		//document.getFrame().getXFrame().getContainerWindow().setVisible(true);
	}
	
	public void rehaTPEventOccurred(RehaTPEvent evt) {
		// TODO Auto-generated method stub
		try{
			if(evt.getDetails()[0] != null){
				if(evt.getDetails()[0].equals(this.getName())){
					this.setVisible(false);
					rtp.removeRehaTPEventListener((RehaTPEventListener) this);
					rtp = null;
					super.dispose();
					this.dispose();
					//System.out.println("****************Rezeptgebühren -> Listener entfernt**************");				
				}
			}
		}catch(NullPointerException ne){
			//System.out.println("In PatNeuanlage" +evt);
		}
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		if(rtp != null){
			this.setVisible(false);			
			rtp.removeRehaTPEventListener((RehaTPEventListener) this);		
			rtp = null;
			super.dispose();
			dispose();
			//System.out.println("****************Rezeptgebühren -> Listener entfernt (Closed)**********");
		}
		
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getActionCommand().equals("okknopf")){
			new Thread(){
				public void run(){
					if(!nurkopie){
						doBuchen();
					}
					rezGebDrucken();
				}
			}.start();
			this.dispose();
			super.dispose();
		}
	}
	
	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode()==10){
			event.consume();
			if( ((JComponent)event.getSource()).getName().equals("okknopf")){
				new Thread(){
					public void run(){
						if(!nurkopie){
							doBuchen();
						}
						rezGebDrucken();
					}
				}.start();
				this.dispose();
				super.dispose();
			}
			//System.out.println("Return Gedrückt");
		}
	}
	public void doBuchen(){
		String cmd = null;
		try{
		cmd = "insert into kasse set einnahme='"+
		SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replaceAll(",",".")+"', datum='"+
		DatFunk.sDatInSQL(DatFunk.sHeute())+"', ktext='"+
		Reha.thisClass.patpanel.patDaten.get(2)+","+
		SystemConfig.hmAdrRDaten.get("<Rnummer>")+"', "+
		"pat_intern='"+SystemConfig.hmAdrRDaten.get("<Rpatid>")+"', "+
		"rez_nr='"+SystemConfig.hmAdrRDaten.get("<Rnummer>")+"'";
		SqlInfo.sqlAusfuehren(cmd);
		////System.out.println("Kassenbuch -> "+cmd);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Die bezahlten Rezeptgebühren konnten nicht verbucht werden.\n+" +
					"Bitte notieren Sie den Namen des Patienten und die Rezeptnummer und verständigen\n"+
					"Sie den Administrator");
		}
		try{
		cmd = "update verordn set rez_geb='"+
		SystemConfig.hmAdrRDaten.get("<Rendbetrag>").replaceAll(",",".")+"', "+
		"rez_bez='T', zzstatus='1' where rez_nr='"+SystemConfig.hmAdrRDaten.get("<Rnummer>")/*SystemConfig.hmAdrRDaten.get("<Rnummer>")*/+"' LIMT 1";
		SqlInfo.sqlAusfuehren(cmd);
		try{
			aktuelleRezepte.setZuzahlImage(1);
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Der Zuzahlungsstatus im Rezeptstamm konnte nicht korrekt gesetzt werden.\n+" +
					"Bitte notieren Sie den Namen des Patienten und die Rezeptnummer und verständigen\n"+
					"Sie den Administrator");
		}
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Der Zuzahlungsstatus im Rezeptstamm konnte nicht korrekt gesetzt werden.\n+" +
					"Bitte notieren Sie den Namen des Patienten und die Rezeptnummer und verständigen\n"+
					"Sie den Administrator");
			
		}
	}
	
}
class RgebHintergrund extends JXPanel{
	ImageIcon hgicon;
	int icx,icy;
	AlphaComposite xac1 = null;
	AlphaComposite xac2 = null;		
	public RgebHintergrund(){
		super();
		/*
		hgicon = new ImageIcon(Reha.proghome+"icons/geld.png");
		icx = hgicon.getIconWidth()/2;
		icy = hgicon.getIconHeight()/2;
		xac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f); 
		xac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f);
		*/			
		
	}
	@Override
	public void paintComponent( Graphics g ) { 
		super.paintComponent( g );
		Graphics2D g2d = (Graphics2D)g;
		
		if(hgicon != null){
			g2d.setComposite(this.xac1);
			g2d.drawImage(hgicon.getImage(), (getWidth()/2)-icx , (getHeight()/2)-icy,null);
			g2d.setComposite(this.xac2);
		}
	}
}