package wyq.swing.explorer;

import javax.swing.tree.DefaultMutableTreeNode;

public interface TreeLoader {

	public abstract void load(DefaultMutableTreeNode root);

	public abstract void unload(DefaultMutableTreeNode root);

	public abstract void reloadAll();

}