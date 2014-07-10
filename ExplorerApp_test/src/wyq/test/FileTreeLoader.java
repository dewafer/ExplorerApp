package wyq.test;

import java.awt.EventQueue;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

import wyq.swing.explorer.AbstractTreeLoader;
import wyq.swing.explorer.ExplorerWin;
import wyq.swing.explorer.TreeLoader;

public class FileTreeLoader extends AbstractTreeLoader {

	public static void main(String[] args) throws Exception {

		// UIManager
		// .setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ExplorerWin window = null;
				try {
					window = new ExplorerWin();
					TreeLoader adaptor = new FileTreeLoader(window.getFrame(),
							window.getTree(), null);
					window.setTreeLoader(adaptor);
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog((window == null ? null
							: window.getFrame()), e);
					System.exit(-1);
				}
			}
		});

	}

	public FileTreeLoader(JFrame parent, JTree tree, Object invokeTarget) {
		super(parent, tree, invokeTarget);
	}

	@Override
	public void load(DefaultMutableTreeNode root) {
		File[] subs;
		File f = null;
		if (root.isRoot()) {
			// root
			subs = File.listRoots();
		} else {
			Object userObject = root.getUserObject();
			f = (File) ((Map) userObject).get("File");
			subs = f.listFiles();
		}
		if (subs != null && subs.length > 0) {
			for (File r : subs) {
				String label = root.isRoot() ? r.getPath() : r.getName();
				Object userObject = createFileInfoMap(r);
				DefaultMutableTreeNode node = createNode(label, userObject);
				node.setAllowsChildren(r.isDirectory());
				root.add(node);
			}
		}
		sleep();
	}

	@Override
	public void unload(DefaultMutableTreeNode root) {
		super.unload(root);
		sleep();
	}

	Random rand = new Random();

	private void sleep() {
		try {
			Thread.sleep(100 + rand.nextInt(300));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Map<String, Object> createFileInfoMap(File file) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("File Name", file.getName());
		data.put("Size", formatSize(file.length()));
		data.put("Last Modified", formatTime(file.lastModified()));
		data.put("Is Dir", file.isDirectory());
		data.put("Is File", file.isFile());
		data.put("Is Hidden", file.isHidden());
		data.put("File", file);
		return data;
	}

	private String formatTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(new Date(time));
	}

	private String formatSize(long size) {
		StringBuffer sb = new StringBuffer();
		long[] calc = divide(size);
		int d = (int) calc[0];
		long calcSize = calc[1];
		String surffix = SIZE_SURFFIX[d];

		sb.append(calcSize);
		sb.append(surffix);
		if (d > 0)
			sb.append("(" + size + ")");

		return sb.toString();

	}

	private static final String[] SIZE_SURFFIX = { "B", "K", "M", "G", "T" };

	private long[] divide(long size) {
		int i = 0;
		while (size >= 1024 && i < SIZE_SURFFIX.length) {
			size /= 1024;
			i++;
		}
		return new long[] { i, size };
	}

}
