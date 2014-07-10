package wyq.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

public class SimpleActionListenerAdaptor implements ActionListener {

	private Object target;
	private Component parent;

	public SimpleActionListenerAdaptor(Object target, Component parent) {
		this.target = target;
		this.parent = parent;
	}

	public SimpleActionListenerAdaptor(Object target) {
		this.target = target;
	}

	public SimpleActionListenerAdaptor() {
		// do nothing
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (target == null) {
			JOptionPane.showMessageDialog(parent, "target is null", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (target instanceof Component && parent == null) {
			parent = (Component) target;
		}
		String methodName = e.getActionCommand();
		if (methodName.matches("^[A-Z].*")) {
			methodName = methodName.substring(0, 1).toLowerCase()
					+ methodName.substring(1);
		}
		if (methodName.contains(" ")) {
			// methodName = methodName.replaceAll(" ", "");
			methodName = methodName.trim();
			StringBuffer sb = new StringBuffer(methodName);
			while (sb.toString().contains(" ")) {
				int idx = sb.indexOf(" ");
				String nxtChar = sb.substring(idx + 1, idx + 2);
				sb.replace(idx, idx + 2, nxtChar.toUpperCase());
			}
			methodName = sb.toString();
		}
		try {
			Method method = target.getClass().getDeclaredMethod(methodName,
					ActionEvent.class);
			if (method != null) {
				method.setAccessible(true);
				method.invoke(target, e);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(parent, e1.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(Object target) {
		this.target = target;
	}

}
