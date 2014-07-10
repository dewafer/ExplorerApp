package wyq.tool.services;

import java.util.List;
import java.util.Map;

public interface Service {

	public abstract List<SearchResult> search(String filter);

	public abstract boolean update(Object key, Map<?, ?> originalAttris,
			Map<?, ?> changedAttributes);

	public abstract String getName();
}