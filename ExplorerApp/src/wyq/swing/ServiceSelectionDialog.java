package wyq.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import wyq.swing.LongRunDialog.Finish;
import wyq.tool.services.Service;
import wyq.tool.services.ServiceProvider;

public class ServiceSelectionDialog extends JDialog implements ActionListener,
		Callable<Object>, Finish {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8328486610828243316L;
	private final JPanel contentPanel = new JPanel();
	private JComboBox<String> comboBox;
	private Service[] services;

	/**
	 * Create the dialog.
	 */
	public ServiceSelectionDialog(JFrame parent) {
		// setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		super(parent, true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		FlowLayout fl_contentPanel = new FlowLayout();
		fl_contentPanel.setAlignment(FlowLayout.LEFT);
		contentPanel.setLayout(fl_contentPanel);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblService = new JLabel("Service");
			contentPanel.add(lblService);
		}
		{
			comboBox = new JComboBox<String>();
			contentPanel.add(comboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(this);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("OK".equals(cmd)) {
			this.setVisible(false);
			return;
		}
		if ("Cancel".equals(cmd)) {
			this.setVisible(false);
			comboBox.setSelectedIndex(-1);
			return;
		}
	}

	public int loadServices() {
		LongRunDialog.run(this, this, this);
		comboBox.removeAllItems();
		for (Service s : services) {
			comboBox.addItem(s.getName());
		}
		return services.length;
	}

	@Override
	public void finish(Object result) {
		if (result instanceof Service[]) {
			services = (Service[]) result;
		}
	}

	@Override
	public Object call() throws Exception {
		return ServiceProvider.availableServices();
	}

	public Service getSelectedService() {
		int selectedIndex = comboBox.getSelectedIndex();
		if (selectedIndex >= 0 && selectedIndex < services.length) {
			return services[selectedIndex];
		} else {
			return null;
		}
	}

	public static Service selectService(JFrame parent) {
		ServiceSelectionDialog dialog = new ServiceSelectionDialog(parent);
		int count = dialog.loadServices();
		Service selectedService = null;
		if (count > 1) {
			dialog.pack();
			dialog.setVisible(true);
			selectedService = dialog.getSelectedService();
			dialog.dispose();
		} else {
			selectedService = dialog.services[0];
		}
		return selectedService;
	}
}
