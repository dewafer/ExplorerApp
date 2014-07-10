package wyq.swing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class SimpleListenerAdaptor implements InvocationHandler {

	Object target;
	Map<Class<?>, Object> proxies = new HashMap<Class<?>, Object>();
	Map<Class<?>, Map<String, String>> methodRedirectMap = new HashMap<Class<?>, Map<String, String>>();

	public SimpleListenerAdaptor(Object target) {
		this.target = target;
	}

	public <T> T getListener(Class<T> type) {
		return getListener(type, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getListener(Class<T> type, Map<String, String> methodNameMap) {
		if (type == null || !type.isInterface()) {
			return null;
		}
		Object proxy = null;
		if (!proxies.containsKey(type)) {
			proxy = newProxy(type);
			if (proxy != null) {
				proxies.put(type, proxy);
				if (methodNameMap != null) {
					methodRedirectMap.put(type, methodNameMap);
				}
			}
		} else {
			proxy = proxies.get(type);
		}

		return (T) proxy;
	}

	protected Object newProxy(Class<?> newType) {
		if (!newType.isInterface()) {
			return null;
		}

		Class<?>[] interfaces = { newType };
		Object newProxy = Proxy.newProxyInstance(getClassLoader(), interfaces,
				this);

		return newProxy;
	}

	protected ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		if (target == null) {
			return null;
		}

		Class<?> proxyClass = method.getDeclaringClass();
		String methodName = method.getName();
		if (methodRedirectMap.containsKey(proxyClass)) {
			Map<String, String> methodNameMap = methodRedirectMap.get(proxyClass);
			if (methodNameMap.containsKey(methodName)) {
				methodName = methodNameMap.get(methodName);
			}
		}

		Method targetMethod = null;
		Class<?> targetClass = target.getClass();

		Class<?>[] parameterTypes = method.getParameterTypes();
		targetMethod = targetClass
				.getDeclaredMethod(methodName, parameterTypes);

		targetMethod.setAccessible(true);
		Object o = targetMethod.invoke(target, args);

		return o;
	}

	/**
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * @return the methodRedirectMap
	 */
	public Map<Class<?>, Map<String, String>> getMethodRedirectMap() {
		return methodRedirectMap;
	}

}
