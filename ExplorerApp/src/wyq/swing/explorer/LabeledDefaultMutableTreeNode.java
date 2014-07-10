package wyq.swing.explorer;

import javax.swing.tree.DefaultMutableTreeNode;

class LabeledDefaultMutableTreeNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8262422504647406479L;

	private String label;

	private boolean isPadding = false;

	private Object key;

	public LabeledDefaultMutableTreeNode(String label) {
		this.label = label;
	}

	public LabeledDefaultMutableTreeNode(String label, Object userObject) {
		super(userObject);
		this.label = label;
	}

	public LabeledDefaultMutableTreeNode(String label, Object userObject,
			Object key) {
		super(userObject);
		this.label = label;
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label == null ? super.toString() : (isPadding ? label + "*"
				: label);
	}

	/**
	 * @return the isPadding
	 */
	public boolean isPadding() {
		return isPadding;
	}

	/**
	 * @param isPadding
	 *            the isPadding to set
	 */
	public void setPadding(boolean isPadding) {
		this.isPadding = isPadding;
	}

	/**
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(Object key) {
		this.key = key;
	}

}