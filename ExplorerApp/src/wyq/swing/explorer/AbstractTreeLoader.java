package wyq.swing.explorer;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import wyq.swing.LongRunDialog;
import wyq.swing.SimpleListenerAdaptor;

public abstract class AbstractTreeLoader implements TreeLoader,
		TreeWillExpandListener {

	private LabeledDefaultMutableTreeNode rootNode = new LabeledDefaultMutableTreeNode(
			"root");

	private DefaultTreeModel treeModel = null;
	private JFrame parent;
	private JTree tree;
	private Object target;

	private SimpleListenerAdaptor listenerAdaptor;

	public AbstractTreeLoader(JFrame parent, JTree tree, Object invokeTarget) {
		this.parent = parent;
		this.tree = tree;
		this.target = invokeTarget;
		initialize();
	}

	private void initialize() {
		if (target != null) {
			listenerAdaptor = new SimpleListenerAdaptor(target);
		}

		treeModel = new DefaultTreeModel(rootNode, true);
		// Retrieve tree model listeners from original model
		TreeModel oriModel = tree.getModel();
		if (oriModel instanceof DefaultTreeModel) {
			DefaultTreeModel defOriModel = (DefaultTreeModel) oriModel;
			for (TreeModelListener listener : defOriModel
					.getTreeModelListeners()) {
				treeModel.addTreeModelListener(listener);
			}
		}
		// redirect treeModelListener
		if (listenerAdaptor != null) {
			treeModel.addTreeModelListener(listenerAdaptor
					.getListener(TreeModelListener.class));
		}
		tree.setModel(treeModel);
		tree.addTreeWillExpandListener(this);

	}

	public final LabeledDefaultMutableTreeNode createNode(String label,
			Object userObject, Object key) {
		return new LabeledDefaultMutableTreeNode(label, userObject, key);
	}

	public final DefaultMutableTreeNode createNode(String label,
			Object userObject) {
		return createNode(label, userObject, null);
	}

	public final DefaultMutableTreeNode createNode(String label) {
		return createNode(label, null);
	}

	/**
	 * @return the rootNode
	 */
	public final LabeledDefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	/**
	 * @return the treeModel
	 */
	public final DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	@Override
	public final void reloadAll() {
		LabeledDefaultMutableTreeNode root = getRootNode();
		root.removeAllChildren();
		// load
		LongRunDialog.run(parent, new TreeLoadRunner(root));
	}

	@Override
	public final void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		TreePath path = event.getPath();
		Object lastPathComponent = path.getLastPathComponent();
		if (lastPathComponent instanceof LabeledDefaultMutableTreeNode) {
			LabeledDefaultMutableTreeNode node = (LabeledDefaultMutableTreeNode) lastPathComponent;
			LongRunDialog.run(parent, new TreeLoadRunner(false, node));
		}
	}

	@Override
	public final void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		TreePath path = event.getPath();
		Object lastPathComponent = path.getLastPathComponent();
		if (lastPathComponent instanceof LabeledDefaultMutableTreeNode) {
			LabeledDefaultMutableTreeNode node = (LabeledDefaultMutableTreeNode) lastPathComponent;
			LongRunDialog.run(parent, new TreeLoadRunner(true, node));
		}
	}

	class TreeLoadRunner implements Runnable {

		boolean isUnload = false;
		LabeledDefaultMutableTreeNode root = null;

		public TreeLoadRunner(boolean isUnload,
				LabeledDefaultMutableTreeNode root) {
			this.isUnload = isUnload;
			this.root = root;
		}

		public TreeLoadRunner(LabeledDefaultMutableTreeNode node) {
			this(false, node);
		}

		@Override
		public void run() {
			// load tree
			if (isUnload) {
				unload(root);
			} else {
				load(root);
			}
			getTreeModel().reload(root);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wyq.swing.explorer.TreeLoader#unload(javax.swing.tree.DefaultMutableTreeNode
	 * )
	 */
	@Override
	public void unload(DefaultMutableTreeNode root) {
		root.removeAllChildren();
	}

}