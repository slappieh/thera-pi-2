package hauptFenster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXFrame;



import events.PatStammEvent;
import events.PatStammEventClass;

class RehaIOServer extends SwingWorker<Void,Void>{
	public ServerSocket serv = null;
	StringBuffer sb = new StringBuffer();
	InputStream input = null;
	OutputStream output = null;
	//public int port = 6000;
	public static boolean reha301IsActive = false;
	public static boolean offenePostenIsActive = false;
	
	public RehaIOServer(int x){
		Reha.xport = x;
		execute();
	}	
		
	public String getPort(){
		return Integer.toString(Reha.xport);
	}
	private void doOffenePosten(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			offenePostenIsActive = true;
			System.out.println("301-er  Modul gestartet");
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			offenePostenIsActive = false;
			Reha.thisFrame.toFront();
			System.out.println("301-er  Modul beendet");
			return;
		}
	}
	/*****
	 * 
	 * 
	 * 301-er
	 */
	private void doReha301(String op){
		if(op.split("#")[1].equals(RehaIOMessages.IS_STARTET)){
			Reha.thisFrame.setCursor(Reha.thisClass.cdefault);
			reha301IsActive = true;
			System.out.println("301-er  Modul gestartet");
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.IS_FINISHED)){
			reha301IsActive = false;
			/*
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
					Reha.thisFrame.setVisible(true);
                	Reha.thisFrame.toFront();
                	Reha.thisFrame.repaint();
                	Reha.thisFrame.requestFocus();
                }
            });
			*/
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					//Reha.thisFrame.setVisible(false);
					Reha.thisFrame.setVisible(true);
					Reha.thisFrame.setExtendedState(JXFrame.MAXIMIZED_BOTH);
					Reha.thisFrame.toFront();
					Reha.thisFrame.repaint();
					//Reha.thisFrame.requestFocus();
				}
			});

			System.out.println("301-er  Modul beendet");
			return;
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATANDREZFIND)){
			if(Reha.thisClass.patpanel != null){
				System.out.println("Suche PatientenIK="+op.split("#")[2]+" mit Rezeptnummer="+op.split("#")[3]);
				this.posteAktualisiePatUndRez(op.split("#")[2], op.split("#")[3]);
				System.out.println("erledigt");
			}
		}else if(op.split("#")[1].equals(RehaIOMessages.MUST_PATFIND)){
			if(Reha.thisClass.patpanel != null){
				this.posteAktualisiePat(op.split("#")[2]);
			}
		}
		//JOptionPane.showMessageDialog(null, "Hallo Reha hier spricht das 301-er Modul");
	}
	@Override
	protected Void doInBackground() throws Exception {
			
			while(Reha.xport < 6020){
				try {
					serv = new ServerSocket(Reha.xport);
					break;
				} catch (Exception e) {
					System.out.println("In Exception währen der Portsuche - 1");
					if(serv != null){
						try {
							serv.close();
						} catch (IOException e1) {
							System.out.println("In Exception währen der Portsuche - 2");
							e1.printStackTrace();
						}
						serv = null;
					}
					//e.printStackTrace();
					System.out.println("Port: "+Reha.xport+" bereits belegt");
					Reha.xport++;
				}
			}
			if(Reha.xport==6020){
				JOptionPane.showMessageDialog(null, "Fehler bei der Initialisierung des IO-Servers");
				Reha.xport = -1;
				serv = null;
				return null;
			}
			System.out.println("IO-SocketServer installiert auf Port: "+Reha.xport);
			Socket client = null;
			while(true){
				try {
					client = serv.accept();
				} catch (SocketException se) {
					//se.printStackTrace();
					return null;
				}
				sb.setLength(0);
				sb.trimToSize();
				input = client.getInputStream();
				//output = client.getOutputStream();
				int byteStream;
				//String test = "";
				try {
					while( (byteStream =  input.read()) > -1){
						char b = (char)byteStream;
						sb.append(b);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("In Exception währen der while input.read()-Schleife");
				}
				/***************************/
				if(sb.toString().startsWith("Reha301#")){
					doReha301(String.valueOf(sb.toString()) );
					
				}else if(sb.toString().startsWith("OffenePosten#")){
					doOffenePosten(String.valueOf(sb.toString()));
				}
			}

//			return null;

		}
	private RehaIOServer getInstance(){
		return this;
	}
	
	private void posteAktualisiePatUndRez(String patid,String reznum){
		final String xpatid = patid;
		final String xreznum = reznum;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				//System.out.println("Suche Patient:"+xpatid+" und Rezept:"+xreznum);
				try{
				String s1 = String.valueOf("#PATSUCHEN");
				String s2 = xpatid;
				PatStammEvent pEvt = new PatStammEvent(getInstance());
				pEvt.setPatStammEvent("PatSuchen");
				pEvt.setDetails(s1,s2,xreznum) ;
				PatStammEventClass.firePatStammEvent(pEvt);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}
	private void posteAktualisiePat(String patid){
		final String xpatid = patid;
		new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				String s1 = String.valueOf("#PATSUCHEN");
				String s2 = xpatid;
				PatStammEvent pEvt = new PatStammEvent(getInstance());
				pEvt.setPatStammEvent("PatSuchen");
				pEvt.setDetails(s1,s2,"") ;
				PatStammEventClass.firePatStammEvent(pEvt);		
				return null;
			}
			
		}.execute();
	}
	
}