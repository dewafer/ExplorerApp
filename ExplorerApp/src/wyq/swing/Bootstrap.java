package wyq.swing;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import wyq.swing.explorer.ExplorerWin;
import wyq.swing.explorer.ServiceTreeLoaderAdaptor;
import wyq.tool.services.Service;

public class Bootstrap {

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		// UIManager
		// .setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ExplorerWin window = null;
				try {
					window = new ExplorerWin();
					Service selectService = ServiceSelectionDialog
							.selectService(window.getFrame());
					if (selectService != null) {
						// specify loader before set visible
						ServiceTreeLoaderAdaptor adaptor = new ServiceTreeLoaderAdaptor(
								selectService, window.getFrame(), window
										.getTree());
						window.setTreeLoader(adaptor);
						window.getFrame().setVisible(true);
					} else {
						JOptionPane.showMessageDialog(window.getFrame(),
								"No service selected.");
						System.exit(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog((window == null ? null
							: window.getFrame()), e);
					System.exit(-1);
				}
			}
		});
	}

}
