package wyq.swing.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import wyq.tool.services.SearchResult;
import wyq.tool.services.Service;

public class ServiceTreeLoaderAdaptor extends AbstractTreeLoader {

	Service service = null;
	private List<SearchResult> search = new ArrayList<SearchResult>();

	public ServiceTreeLoaderAdaptor(Service service, JFrame parent, JTree tree,
			Object invokeObject) {
		super(parent, tree, invokeObject);
		this.service = service;
	}

	public ServiceTreeLoaderAdaptor(Service selectService, JFrame frame,
			JTree tree) {
		this(selectService, frame, tree, null);
	}

	@Override
	public void load(DefaultMutableTreeNode root) {
		if (root.isRoot()) {
			for (SearchResult result : search) {
				Map<?, ?> map = result.getValues();
				String label = (String) result.getKey();
				LabeledDefaultMutableTreeNode node = createNode(label, map,
						label);
				node.setAllowsChildren(false);
				root.add(node);
			}
		}
	}

	public void search(String filter) {
		// clear before search
		search.clear();
		search.addAll(service.search(filter));
	}

	public void clear() {
		search.clear();
	}

	public boolean commit(Object key, Map<?, ?> originalValues,
			Map<?, ?> paddingValues) {
		return service.update(key, originalValues, paddingValues);
	}

}
