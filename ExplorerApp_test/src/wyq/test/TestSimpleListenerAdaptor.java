/**
 * 
 */
package wyq.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.junit.Test;

import wyq.swing.SimpleListenerAdaptor;

/**
 * @author wangyq
 * 
 */
public class TestSimpleListenerAdaptor {

	/**
	 * Test method for
	 * {@link wyq.swing.SimpleListenerAdaptor#getListener(java.lang.Class)}.
	 */
	@Test
	public final void testGetListener() {
		SimpleListenerAdaptor adp = new SimpleListenerAdaptor(this);
		ActionListener a = adp.getListener(ActionListener.class);
		assertNotNull(a);
	}

	@Test
	public final void testGetListener_twice() {
		SimpleListenerAdaptor adp = new SimpleListenerAdaptor(this);

		ActionListener a = adp.getListener(ActionListener.class);
		assertNotNull(a);

		Object o = adp.getListener(Object.class);
		assertNull(o);

		MenuListener m = adp.getListener(MenuListener.class);
		assertNotNull(m);

		ActionListener b = adp.getListener(ActionListener.class);
		assertNotNull(b);

		a.actionPerformed(new ActionEvent(this, 0, "cmd"));
		m.menuSelected(new MenuEvent(this));
		b.actionPerformed(new ActionEvent(this, 0, "cmd"));

	}

	/**
	 * Test method for
	 * {@link wyq.swing.SimpleListenerAdaptor#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])}
	 * .
	 */
	@Test
	public final void testInvoke() {
		SimpleListenerAdaptor adp = new SimpleListenerAdaptor(this);
		ActionListener a = adp.getListener(ActionListener.class);
		a.actionPerformed(new ActionEvent(this, 0, "Test"));
	}

	public void actionPerformed(ActionEvent e) {
		assertNotNull(e);
	}

	private void menuSelected(MenuEvent e) {
		assertNotNull(e);
	}

	protected void testMethod(ActionEvent e) {
		assertNotNull(e);
	}

	/**
	 * Test method for
	 * {@link wyq.swing.SimpleListenerAdaptor#getListener(java.lang.Class, java.lang.Map)}
	 * .
	 */
	@Test
	public void testGetListener2() throws Exception {
		SimpleListenerAdaptor adp = new SimpleListenerAdaptor(this);

		Map methodMap = new HashMap();
		methodMap.put("actionPerformed", "testMethod");
		ActionListener a = adp.getListener(ActionListener.class, methodMap);
		assertNotNull(a);

		a.actionPerformed(new ActionEvent(this, 0, "cmd"));
	}
}
