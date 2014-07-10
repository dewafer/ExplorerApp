package wyq.swing.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import wyq.swing.SimpleActionListenerAdaptor;

public class ExplorerWin implements TreeSelectionListener {

	private static final String CLEAR = "Clear";
	private static final String COMMIT = "Commit";
	private static final String FILTER_INPUT = "Filter Input";
	private static final String REMOVE_PADDING = "Remove Padding";
	private static final String SEARCH_BUTTON = "Search";
	private JFrame frame;
	private JTable table;
	private JTree tree;
	private JToolBar toolBar;

	/**
	 * Create the application.
	 */
	public ExplorerWin() {
		initialize();
	}

	private MapTableModel model = new MapTableModel();
	private JPopupMenu popupMenu;
	private JMenuItem mntmRemovePadding;
	private TreeLoader treeLoader;
	private JLabel lblFilter;
	private JTextField filterTextField;
	private JButton btnSearchButton;
	private SimpleActionListenerAdaptor action = new SimpleActionListenerAdaptor(
			this, frame);
	private JMenuItem mntmCommit;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		lblFilter = new JLabel("filter:");
		toolBar.add(lblFilter);

		filterTextField = new JTextField();
		toolBar.add(filterTextField);
		filterTextField.setColumns(10);
		filterTextField.addActionListener(action);
		filterTextField.setActionCommand(FILTER_INPUT);

		btnSearchButton = new JButton(SEARCH_BUTTON);
		btnSearchButton.addActionListener(action);
		btnSearchButton.setActionCommand(FILTER_INPUT);
		toolBar.add(btnSearchButton);

		btnClear = new JButton(CLEAR);
		btnClear.addActionListener(action);
		btnClear.setActionCommand(CLEAR);
		toolBar.add(btnClear);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.2345);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane_left = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_left);

		tree = new JTree();
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		scrollPane_left.setViewportView(tree);
		tree.addTreeSelectionListener(this);

		JScrollPane scrollPane_right = new JScrollPane();
		splitPane.setRightComponent(scrollPane_right);

		popupMenu = new JPopupMenu();

		mntmRemovePadding = new JMenuItem(REMOVE_PADDING);
		mntmRemovePadding.addActionListener(action);
		mntmRemovePadding.setActionCommand(REMOVE_PADDING);
		popupMenu.add(mntmRemovePadding);

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_right.setViewportView(table);
		table.setModel(model);
		addPopup(table, popupMenu);

		mntmCommit = new JMenuItem(COMMIT);
		mntmCommit.addActionListener(action);
		mntmCommit.setActionCommand(COMMIT);
		popupMenu.add(mntmCommit);

	}

	private Map<Object, Map<?, ?>> paddings = new HashMap<Object, Map<?, ?>>();
	private JButton btnClear;

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// save old one's padding values
		TreePath oldLeadSelectionPath = e.getOldLeadSelectionPath();
		if (oldLeadSelectionPath != null) {
			Object oldLast = oldLeadSelectionPath.getLastPathComponent();
			if (oldLast instanceof LabeledDefaultMutableTreeNode) {
				LabeledDefaultMutableTreeNode oldNode = (LabeledDefaultMutableTreeNode) oldLast;
				boolean nodeHasPadding = model.isPadding();
				oldNode.setPadding(nodeHasPadding);
				((DefaultTreeModel) tree.getModel()).nodeChanged(oldNode);
				if (nodeHasPadding) {
					if (!paddings.containsKey(oldNode.getKey())) {
						Map<?, ?> oldPaddingValues = new HashMap<Object, Object>(
								model.getPadding());
						paddings.put(oldNode.getKey(), oldPaddingValues);
					}
					model.setPadding(new HashMap<Object, Object>());
					model.setKey(null);
				} else {
					if (paddings.containsKey(oldNode.getKey())) {
						paddings.remove(oldNode.getKey());
					}
				}
			}
		}

		TreePath newLeadSelectionPath = e.getNewLeadSelectionPath();
		if (newLeadSelectionPath == null) {
			model.setData(Collections.EMPTY_MAP);
			model.setPadding(new HashMap<Object, Object>());
			model.setKey(null);
			return;
		}
		Object lastPathComponent = newLeadSelectionPath.getLastPathComponent();
		if (lastPathComponent instanceof LabeledDefaultMutableTreeNode) {
			LabeledDefaultMutableTreeNode node = (LabeledDefaultMutableTreeNode) lastPathComponent;
			Object userObject = node.getUserObject();
			if (userObject instanceof Map) {
				Map<?, ?> data = (Map<?, ?>) userObject;
				model.setData(data);
				model.setKey(node.getKey());
				// paddings
				if (paddings.containsKey(node.getKey())) {
					@SuppressWarnings("unchecked")
					Map<Object, Object> paddingValues = (Map<Object, Object>) paddings
							.get(node.getKey());
					model.setPadding(paddingValues);
				}

			} else {
				model.setData(Collections.EMPTY_MAP);
				model.setPadding(new HashMap<Object, Object>());
				model.setKey(null);
			}
		}
	}

	private void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				setMenuEnable();
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private void setMenuEnable() {
		int selectedRow = table.getSelectedRow();
		mntmRemovePadding.setEnabled(model.isRowPadding(selectedRow));
		mntmCommit.setEnabled(model.isPadding());
	}

	protected void removePadding(ActionEvent e) {
		int selectedRow = table.getSelectedRow();
		model.removePadding(selectedRow);
	}

	protected void filterInput(ActionEvent e) {
		if (treeLoader != null
				&& treeLoader instanceof ServiceTreeLoaderAdaptor) {
			ServiceTreeLoaderAdaptor adp = (ServiceTreeLoaderAdaptor) treeLoader;
			String filter = filterTextField.getText();
			adp.search(filter);
			model.setData(Collections.EMPTY_MAP);
			reloadTree();
		}
	}

	protected void clear(ActionEvent e) {
		if (treeLoader != null
				&& treeLoader instanceof ServiceTreeLoaderAdaptor) {
			ServiceTreeLoaderAdaptor adp = (ServiceTreeLoaderAdaptor) treeLoader;
			adp.clear();
			model.setData(Collections.EMPTY_MAP);
			reloadTree();
		}
	}

	protected void commit(ActionEvent e) {
		if (treeLoader != null
				&& treeLoader instanceof ServiceTreeLoaderAdaptor) {

			if (!model.isPadding()) {
				return;
			}

			int confirmDialog = JOptionPane.showConfirmDialog(frame,
					"Confirm commit?", "Confirm", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (JOptionPane.YES_OPTION != confirmDialog) {
				return;
			}

			ServiceTreeLoaderAdaptor adp = (ServiceTreeLoaderAdaptor) treeLoader;
			Object key = model.getKey();
			Map<?, ?> originals = model.getData();
			Map<?, ?> paddingValues = model.getPadding();

			boolean b = adp.commit(key, originals, paddingValues);
			if (b) {
				String filter = filterTextField.getText();
				adp.search(filter);
				model.setData(Collections.EMPTY_MAP);
				// remove padding values
				model.setPadding(new HashMap<Object, Object>());
				paddings.remove(model.getKey());
				reloadTree();
			} else {
				JOptionPane.showMessageDialog(frame, "Commit failure.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * @return the treeLoader
	 */
	public TreeLoader getTreeLoader() {
		return treeLoader;
	}

	/**
	 * @param treeLoader
	 *            the treeLoader to set
	 */
	public void setTreeLoader(TreeLoader treeLoader) {
		this.treeLoader = treeLoader;
		reloadTree();
	}

	private void reloadTree() {
		if (treeLoader != null
				&& treeLoader instanceof ServiceTreeLoaderAdaptor) {
			ServiceTreeLoaderAdaptor adp = (ServiceTreeLoaderAdaptor) treeLoader;
			adp.reloadAll();
			// recalculate padding asterisk
			Enumeration<?> breadthFirstEnumeration = adp.getRootNode()
					.breadthFirstEnumeration();
			while (breadthFirstEnumeration.hasMoreElements()) {
				Object object = breadthFirstEnumeration.nextElement();
				if (object instanceof LabeledDefaultMutableTreeNode) {
					LabeledDefaultMutableTreeNode node = (LabeledDefaultMutableTreeNode) object;
					node.setPadding(paddings.containsKey(node.getKey()));
				}
			}
			adp.getTreeModel().reload();
		}
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * @return the tree
	 */
	public JTree getTree() {
		return tree;
	}
}
