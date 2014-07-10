package wyq.tool.services.impl;

import java.util.Map;

import wyq.tool.services.SearchResult;

public class SimpleSearchResult implements SearchResult {

	protected Object key;
	protected Map<?, ?> values;

	@Override
	public Object getKey() {
		return key;
	}

	@Override
	public Map<?, ?> getValues() {
		return values;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(Object key) {
		this.key = key;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public void setValues(Map<?, ?> values) {
		this.values = values;
	}

	public SimpleSearchResult(Object key, Map<?, ?> values) {
		this.key = key;
		this.values = values;
	}

}
