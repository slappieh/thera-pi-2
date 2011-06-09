package rehaMail;





import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.mysql.jdbc.PreparedStatement;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.ui.XUIElement;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.DocumentZoomType;

import Tools.ButtonTools;
import Tools.DatFunk;
import Tools.JRtaComboBox;
import Tools.JRtaTextField;
import Tools.SqlInfo;
import ag.ion.bion.officelayer.NativeView;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.filter.RTFFilter;
import ag.ion.bion.officelayer.text.IParagraph;
import ag.ion.bion.officelayer.text.ITextCursor;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.bion.officelayer.text.ITextRange;
import ag.ion.bion.officelayer.text.IViewCursor;
import ag.ion.noa.NOAException;
import ag.ion.noa.frame.ILayoutManager;
import ag.ion.noa.internal.frame.LayoutManager;

public class NewMail extends JFrame  implements WindowListener  {
	
	boolean neu;
	
	private IFrame             officeFrame       = null;
	public	ITextDocument      document          = null;
	private JPanel             noaPanel          = null;
	private JXPanel			noaDummy = null;
	NativeView nativeView = null;
	DocumentDescriptor xdescript = null;
	
	ActionListener al = null;
	
	JButton[] buts = {null,null,null,null,null};
	JRadioButton[] rads = {null,null,null};
	ButtonGroup bg = new ButtonGroup();
	JRtaComboBox box = null;
	JRtaTextField betreff = null;
	JRtaTextField empfaenger = null;
	String aktAbsender = "";
	ByteArrayOutputStream out;
	public NewMail(String title,boolean neu,Point pt,ByteArrayOutputStream out,String absender,String sbetreff){
		super();
		this.neu = neu;
		this.out = out;
		/*
		xdescript = new DocumentDescriptor();
		xdescript.setReadOnly(true);
		xdescript.setHidden(true);
		xdescript.setFilterDefinition(RTFFilter.FILTER.toString());
		*/
		try {
			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		addWindowListener(this);
		setSize(800,500);
		setPreferredSize(new Dimension(800,500));
		activateListener();
		getContentPane().add (getContent());
		setLocation(pt);
		setTitle(title);
		final boolean xneu = neu;
		final String xabsender = absender;
		final String xbetreff = sbetreff;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				try{
				if(!xneu){
					rads[0].setSelected(true);
					rads[0].setEnabled(false);rads[1].setEnabled(false);
					doRecipient(true);
					box.setSelectedItem(xabsender);
					box.setEnabled(false);
					betreff.setText("Antwort:["+xbetreff+"]");
				}else{
					rads[0].setSelected(true);
					doRecipient(true);
				}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		pack();
		setVisible(true);

	}
	private void activateListener(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String cmd = arg0.getActionCommand();
				if(cmd.equals("senden")){
					try {
						doSenden();
					} catch (NOAException e) {
						e.printStackTrace();
					}
					return;
				}else if(cmd.equals("einzel")){
					doRecipient(true);
					return;
				}else if(cmd.equals("gruppe")){
					doRecipient(false);
					return;
				}else if(cmd.equals("selektor")){
					return;
				}

			}
			
		};
	}
	private void doSenden() throws NOAException{
		if(betreff.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null,"Es wurde kein -> Betreff <- eingegeben!\nNachricht wird nicht versendet.");
			return;
		}
		int frage = JOptionPane.showConfirmDialog(this, "Wollen Sie die Nachricht jetzt versenden","Bitte beachten",JOptionPane.YES_NO_OPTION);
		if(frage != JOptionPane.YES_OPTION){return;}
		Vector<String> versand = new Vector<String>();
		if(rads[0].isSelected()){
			versand.add(box.getSelectedItem().toString());
		}
		if(empfaenger.getText().trim().length()>0){
			String[] gruppe = empfaenger.getText().trim().split(";");
			for(int i = 0; i < gruppe.length;i++){
				if(gruppe[i].trim().length() >0){
					if(gibtsDenEmpfaenger(gruppe[i].trim())){
						versand.add(String.valueOf(gruppe[i].trim()));
					}else{
						JOptionPane.showMessageDialog(null, "Der Nachrichtenempfänger -> "+gruppe[i].trim()+
								" <- existiert nicht!\n\nFalsch geschrieben???\n");
					}
				}
			}
		}
		if(versand.size()<=0){
			JOptionPane.showMessageDialog(null,"Es wurden keinerlei gültige Empfänger eingegeben!\nNachricht wird nicht versendet.");
			return;
		}
		System.out.println(versand);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		document.getPersistenceService().export(out, RTFFilter.FILTER);
		try {
			out.flush();
			for(int i = 0; i < versand.size();i++){
				
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setVisible(false);
		this.dispose();
		
	}
	private boolean gibtsDenEmpfaenger(String empfaenger){
		boolean ret = false;
		for(int i = 0; i < RehaMail.einzelMail.size();i++){
			if(RehaMail.einzelMail.get(i).get(0).trim().equals(empfaenger.trim())){return true;}
		}
		return ret;
		
	}
	private void doRecipient(boolean einzel){
		if(einzel){box.setDataVectorVector(RehaMail.einzelMail, 0, 2);empfaenger.setText("");return;}
		box.setDataVectorVector(RehaMail.gruppenMail, 0, 1);
		empfaenger.setText(box.getSecValue().toString());
	}
	private JXPanel getContent(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(true);
		CompoundPainter<Object> cp = null;
		MattePainter mp = null;
		LinearGradientPaint p = null;
		/*****************/
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(960,100);
	    float[] dist = {0.0f, 0.75f};
	    Color[] colors = {Color.WHITE,Tools.Colors.PiOrange.alpha(0.25f)};

		start = new Point2D.Float(0, 0);
	    end = new Point2D.Float(0,400);
	    dist = new  float[] {0.0f, 0.75f};
	    colors = new Color[] {Color.WHITE,Tools.Colors.TaskPaneBlau.alpha(0.45f)};
	    p =  new LinearGradientPaint(start, end, dist, colors);
	    mp = new MattePainter(p);
	    cp = new CompoundPainter<Object>(mp);
	    pan.setBackgroundPainter(cp);
	    /******************/
		pan.validate();
		String xwerte = "fill:0:grow(1.0)";
		String ywerte = "p,5dlu,fill:0:grow(1.0)";
		FormLayout lay = new FormLayout(xwerte,ywerte);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		pan.add(getToolbar(),cc.xy(1,1));
		pan.add(getnoaDummy(),cc.xy(1, 3));
		noaDummy.setVisible(true);
		noaDummy.add(getOOorgPanel(),BorderLayout.CENTER);

		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				while(RehaMail.officeapplication==null){
				}

				while(!RehaMail.officeapplication.isActive()){
					Thread.sleep(75);
				}
				fillNOAPanel();
				validate();
				nativeView.requestFocus();
				document.getTextService().getCursorService().getTextCursor().gotoStart(true);
				setVisible(true);
				return null;
			}
			
		}.execute();

		pan.validate();
		return pan;
	}
	private JXPanel getToolbar(){
		JXPanel pan = new JXPanel();
		pan.setOpaque(false);
		String xwert = "p,5dlu,80dlu,5dlu,100dlu:g,5dlu";
		String ywert = "p,2dlu,p";
		FormLayout lay = new FormLayout(xwert,ywert);
		CellConstraints cc = new CellConstraints();
		pan.setLayout(lay);
		
		JToolBar bar = new JToolBar();
		bar.setOpaque(false);
		bar.setRollover(true);
		bar.setBorder(null);
		bar.setOpaque(false);

		bar.addSeparator(new Dimension(30,30));
		bar.add(buts[0]=ButtonTools.macheButton("", "senden", al));
		Image ico = new ImageIcon(RehaMail.progHome+"icons/evolution.png").getImage().getScaledInstance(34,34, Image.SCALE_SMOOTH);
		buts[0].setIcon(new ImageIcon(ico));
		buts[0].setToolTipText("Nachricht versenden");
		bar.addSeparator(new Dimension(40,30));
		bar.add(rads[0]=new JRadioButton("an Einzelperson"));
		bg.add(rads[0]);
		rads[0].setActionCommand("einzel");
		rads[0].addActionListener(al);
		rads[0].setOpaque(false);
		bar.add(rads[1]=new JRadioButton("an Gruppe"));
		bg.add(rads[1]);
		rads[1].setActionCommand("gruppe");
		rads[1].addActionListener(al);
		rads[1].setOpaque(false);
		pan.add(bar,cc.xy(1,1));
		
		pan.add(box=new JRtaComboBox(),cc.xy(3,1));
		box.setActionCommand("selektor");
		box.addActionListener(al);
		
		
		pan.add(empfaenger=new JRtaTextField("nix",true),cc.xy(5, 1));
		empfaenger.setFont(new Font("Courier New",12,12));
		
		JLabel lab = new JXLabel("Betreff der Nachricht");
		lab.setForeground(Color.RED);
		pan.add(lab,cc.xy(1, 3,CellConstraints.RIGHT,CellConstraints.DEFAULT));
		
		betreff = new JRtaTextField("nix",true);
		betreff.setFont(new Font("Courier New",12,12));
		pan.add(betreff,cc.xyw(3,3,3));
		pan.validate();
		
		return pan;
	}
	private JPanel getOOorgPanel(){
		noaPanel = new JPanel(new GridLayout());
		noaPanel.setPreferredSize(new Dimension(1024,800));
		noaPanel.validate();
		return noaPanel;
	}
	private JXPanel getnoaDummy(){
		noaDummy = new JXPanel(new GridLayout(1,1));
		return noaDummy;
	}
 

	private void fillNOAPanel() {
	    if (noaPanel != null) {
		      try {
		        officeFrame = constructOOOFrame(RehaMail.officeapplication, noaPanel);
		        DocumentDescriptor desc = new DocumentDescriptor();
		        desc.setFilterDefinition(RTFFilter.FILTER.toString());

		        
		        document = (ITextDocument) RehaMail.officeapplication.getDocumentService().constructNewDocument(officeFrame,
		            IDocument.WRITER,
		            desc);
		        Tools.OOTools.setzePapierFormat(document, new Integer(25199), new Integer(19299));
	        	Tools.OOTools.setzeRaender(document, new Integer(10), new Integer(10),new Integer(10),new Integer(10));
		        //hideElements(LayoutManager.URL_MENUBAR);
		        //hideElements(LayoutManager.URL_STATUSBAR);
	        	
		        try {
					document.zoom(DocumentZoomType.BY_VALUE, (short)90);
				} catch (DocumentException e) {
					e.printStackTrace();
				}
		        if(out != null){
					ByteArrayInputStream ins = (ByteArrayInputStream)getInputStream(out);
					document.getTextService().getText().setText("\n\n********************bisherige Nachricht************************\n\n");
					ITextCursor textCursor = document.getTextService().getCursorService().getTextCursor();
					textCursor.gotoEnd(false);
					textCursor.insertDocument(ins,RTFFilter.FILTER);
					textCursor.gotoStart(false);
					IViewCursor viewCursor = document.getViewCursorService().getViewCursor();
					viewCursor.getPageCursor().jumpToFirstPage();
					viewCursor.getPageCursor().jumpToStartOfPage();
					ins.close();

					/*
					IDocument xdocument = RehaMail.officeapplication.getDocumentService().loadDocument(
							getInputStream(out)
							, xdescript);
				    ITextDocument xtextDocument = (ITextDocument) xdocument;
				    IViewCursor templateViewCursor = xtextDocument.getViewCursorService().getViewCursor();
				    templateViewCursor.getPageCursor().jumpToEndOfPage();
				    ITextRange end = templateViewCursor.getTextCursorFromEnd().getEnd();
				    templateViewCursor.getPageCursor().jumpToStartOfPage();
				    templateViewCursor.goToRange(end, true);
				    MailPanel.copy( xtextDocument,document);
					xtextDocument.close();
					IViewCursor viewCursor = document.getViewCursorService().getViewCursor();
					viewCursor.getPageCursor().jumpToFirstPage();
					
					ITextCursor textCursor = document.getTextService().getText().getTextCursorService().getTextCursor();
			        textCursor.getStart();//.gotoEnd(false);
			        IParagraph paragraph = document.getTextService().getTextContentService().constructNewParagraph();
			        textCursor.getStart();//textCursor.gotoEnd(false);
			        document.getTextService().getTextContentService().insertTextContent(textCursor.getEnd(),
			            paragraph);
			        */    
			        /*
			        StringBuffer bufferedString = new StringBuffer();
			        for (int j = 0; j < allTexts2BePlaced[i].length; j++) {
			          bufferedString.append(allTexts2BePlaced[i][j] + "\n");
			        }
			        */

			        //paragraph.setParagraphText("\n\n********************bisherige Nachricht, beantwortet am: "+DatFunk.sHeute()+"************************\n");
					
					//ITextContent tcontent =
					//document.getTextService().getTextContentService().insertTextContent(arg0)
		        	//document.getTextService().getText().setText("\n\n********************bisherige Nachricht, beantwortet am: "+DatFunk.sHeute()+"************************\n");
		        	
		        }
		        nativeView.validate();
		        //noaPanel.setVisible(true);
		      }catch (Throwable throwable) {
		        noaPanel.add(new JLabel("Ein Fehler ist aufgetreten: " + throwable.getMessage()));
		      }
		    }
		  }

	private IFrame constructOOOFrame(IOfficeApplication officeApplication, final Container parent) throws Throwable {
	    nativeView = new NativeView(RehaMail.officeNativePfad);
	    parent.add(nativeView);
	    parent.addComponentListener(new ComponentAdapter() {
	      public void componentResized(ComponentEvent e) {
	    	refreshSize();
	        nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
	        parent.getLayout().layoutContainer(parent);
	      }
	    });

	    nativeView.setPreferredSize(new Dimension(parent.getWidth() - 5, parent.getHeight() - 5));
	    parent.getLayout().layoutContainer(parent);
	    officeFrame = officeApplication.getDesktopService().constructNewOfficeFrame(nativeView);
	    return officeFrame;
	}
	private void hideElements(String url ) throws UnknownPropertyException, PropertyVetoException, IllegalArgumentException, WrappedTargetException, NOAException{
	    ILayoutManager layoutManager = officeFrame.getLayoutManager();
	    XLayoutManager xLayoutManager = layoutManager.getXLayoutManager();
	    XUIElement element = xLayoutManager.getElement(url);
	    if (element != null) {
	        XPropertySet xps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, element);
	        xps.setPropertyValue("Persistent", new Boolean(false));
	        xLayoutManager.hideElement(url);
	    }
	}

	public final void refreshSize() {
		noaPanel.setPreferredSize(new Dimension(noaPanel.getWidth() , noaPanel.getHeight()- 5));
		final Container parent = noaPanel.getParent();
		if (parent instanceof JComponent) {
			((JComponent) parent).revalidate();
		}
		final Window window1 = SwingUtilities.getWindowAncestor(nativeView.getParent().getParent());
		if (window1 != null) {
			window1.validate();
		}
		noaPanel.getLayout().layoutContainer(noaPanel);

	}




	
	@Override
	public void windowActivated(WindowEvent arg0) {
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		if(document != null){
			document.close();
			System.out.println("Dokument wurde geschlossen");
			document = null;
		}
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
	}
	private InputStream getInputStream(ByteArrayOutputStream out){
		InputStream is=new ByteArrayInputStream(out.toByteArray());
		return is;
	}
	public static void doSpeichernMail(int dokuid,int pat_intern, String dateiname,int format,String[] str,boolean neu) throws Exception{
		Statement stmt = null;;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			stmt = (Statement) RehaMail.thisClass.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE );
			
			String select = "insert into pimail set dokuid = ? , datum = ?, dokutitel = ?,"+
			"benutzer = ?, pat_intern = ?, format = ?,"+
			"dokutext = ?, dokublob = ? , groesse = ? ";
			ps = (PreparedStatement) RehaMail.thisClass.conn.prepareStatement(select);
			ps.setInt(1, dokuid);
			ps.setString(2, str[0]);			  
			ps.setString(3, str[1]);
			ps.setString(4, str[2]);			  
			ps.setInt(5, pat_intern);
			ps.setInt(6, format);			  
			ps.setString(7, str[3]);
			File f = new File(dateiname);
			byte[] b = "Scheiss".getBytes();
			ps.setBytes(8,b);
			ps.setInt(9, (int)b.length);
			ps.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
			if(ps != null){
				ps.close();
			}
		}
		
	}
	

}
