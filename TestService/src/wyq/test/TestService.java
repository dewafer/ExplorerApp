package wyq.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import wyq.tool.services.SearchResult;
import wyq.tool.services.Service;
import wyq.tool.services.impl.SimpleSearchResult;

public class TestService implements Service {

	public TestService() {
		System.out.println("init test :" + this.getClass().getSimpleName());
		// throw new RuntimeException("test");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	Random rand = new Random();

	@Override
	public List<SearchResult> search(String filter) {
		System.out.println("search:" + filter);
		List<SearchResult> test = new ArrayList<SearchResult>();
		int max = 10 + rand.nextInt(50);
		for (int i = 0; i < max; i++) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("cn", "item_" + i);

			int v = 5 + rand.nextInt(25);
			for (int j = 0; j < v; j++) {
				m.put("idx_" + j, rand.nextBoolean());
			}
			SearchResult r = new SimpleSearchResult("item_" + i, m);
			test.add(r);
		}
		return test;
	}

	@Override
	public boolean update(Object key, Map<?, ?> ori, Map<?, ?> padding) {
		System.out.println("update");
		System.out.println("key=" + key);
		System.out.println("ori=" + ori);
		System.out.println("padding=" + padding);
		return rand.nextInt(100) < 80;
	}

	@Override
	public String getName() {
		return "TestService111";
	}

}
