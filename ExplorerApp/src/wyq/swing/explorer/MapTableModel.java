package wyq.swing.explorer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class MapTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868744681079494632L;
	private Map<?, ?> data = Collections.EMPTY_MAP;
	private Map<Object, Object> padding = new HashMap<Object, Object>();
	private Object key;
	private static final String[] COL_NAMES = { "Key", "Value", "Padding",
			"Check" };
	private static final Class<?>[] COL_TYPES = { String.class, String.class,
			String.class, Boolean.class };
	private static final boolean[] COL_EDITABLE = { false, true, true, true };

	private static final String EMPTY_STRING_LABEL = "{EMPTY STRING}";
	private static final String NULL_LABEL = "{null}";

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		// Key, Value, Padding and Check
		return COL_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			Object key = findKey(rowIndex);
			if (padding.containsKey(key)) {
				return key + "*";
			} else {
				return key;
			}
		}

		if (columnIndex == 1) {
			Object key = findKey(rowIndex);
			return data.get(key);
		}

		if (columnIndex == 2) {
			Object key = findKey(rowIndex);
			Object value = padding.get(key);
			if (padding.containsKey(key) && value == null) {
				return NULL_LABEL;
			}
			if (value instanceof String) {
				String tmp = (String) value;
				if (tmp.length() == 0) {
					return EMPTY_STRING_LABEL;
				} else {
					if (EMPTY_STRING_LABEL.equals(tmp)
							|| NULL_LABEL.equals(tmp)) {
						return tmp + "(String)";
					}
				}
			}
			return value;

		}

		if (columnIndex == 3) {
			return isRowPadding(rowIndex);
		}

		return null;
	}

	@Override
	public String getColumnName(int column) {
		if (column < COL_NAMES.length) {
			return COL_NAMES[column];
		} else {
			return super.getColumnName(column);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COL_TYPES[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return COL_EDITABLE[columnIndex];
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Object key = findKey(rowIndex);
		if (aValue instanceof Boolean) {
			Boolean check = (Boolean) aValue;
			if (!check) {
				removePadding(rowIndex);
			} else {
				padding.put(key, null);
			}
		} else {
			padding.put(key, aValue);
		}
		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	/**
	 * @return the data
	 */
	public Map<?, ?> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<?, ?> data) {
		this.data = data;
		fireTableDataChanged();
	}

	/**
	 * @return the padding
	 */
	public Map<Object, Object> getPadding() {
		return padding;
	}

	/**
	 * @param padding
	 *            the padding to set
	 */
	public void setPadding(Map<Object, Object> padding) {
		this.padding = padding;
		fireTableDataChanged();
	}

	public boolean isRowPadding(int rowIndex) {
		Object key = findKey(rowIndex);
		return key != null && padding.containsKey(key);
	}

	public boolean isPadding() {
		return !padding.isEmpty();
	}

	public void removePadding(int rowIndex) {
		Object key = findKey(rowIndex);
		if (key != null)
			padding.remove(key);
		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	protected Object findKey(int rowIndex) {
		Object[] keyArray = new String[0];
		keyArray = data.keySet().toArray(keyArray);
		if (rowIndex >= 0 && rowIndex < keyArray.length) {
			return keyArray[rowIndex];
		} else {
			return null;
		}

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
