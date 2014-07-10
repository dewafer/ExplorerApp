package wyq.swing;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class LongRunDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1985655997129746331L;

	private static final String PROGRESS_BAR_LABEL = "Please wait a moment.";

	private LongRunner longRunner = new LongRunner();

	public LongRunDialog() {
		this(null);
	}

	/**
	 * Create the dialog.
	 */
	public LongRunDialog(Window owner) {

		super(owner);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				longRunner.execute();
			}
		});
//		setType(Type.POPUP);
		setUndecorated(true);
		setModalityType(ModalityType.TOOLKIT_MODAL);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 320, 46);
		setLocationRelativeTo(owner);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressBar.setString(PROGRESS_BAR_LABEL);
		getContentPane().add(progressBar, BorderLayout.CENTER);

	}

	private Callable<?> call;
	private Runnable run;
	private Finish finish;

	class LongRunner extends SwingWorker<Object, Object> {

		@Override
		protected Object doInBackground() throws Exception {
			if (call != null) {
				return call.call();
			}
			if (run != null) {
				run.run();
			}
			return null;
		}

		@Override
		protected void done() {
			try {
				Object result = get();
				if (finish != null) {
					finish.finish(result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			LongRunDialog.this.setVisible(false);
			LongRunDialog.this.dispose();
		}

	}

	/**
	 * @return the work
	 */
	public Callable<?> getCall() {
		return call;
	}

	/**
	 * @param work
	 *            the work to set
	 */
	public void setCall(Callable<?> work) {
		this.call = work;
	}

	public interface Finish {
		public void finish(Object result);
	}

	/**
	 * @return the finish
	 */
	public Finish getFinish() {
		return finish;
	}

	/**
	 * @param finish
	 *            the finish to set
	 */
	public void setFinish(Finish finish) {
		this.finish = finish;
	}

	public static void run(Window owner, Callable<?> work, Finish finish) {
		LongRunDialog dialog = new LongRunDialog(owner);
		dialog.setCall(work);
		dialog.setFinish(finish);
		dialog.setVisible(true);
	}

	public static void run(Window owner, Runnable run) {
		LongRunDialog dialog = new LongRunDialog(owner);
		dialog.setRun(run);
		dialog.setVisible(true);
	}

	/**
	 * @return the run
	 */
	public Runnable getRun() {
		return run;
	}

	/**
	 * @param run
	 *            the run to set
	 */
	public void setRun(Runnable run) {
		this.run = run;
	}
}
