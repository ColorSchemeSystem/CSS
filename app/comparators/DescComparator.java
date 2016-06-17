package comparators;

import java.util.Comparator;
import java.util.Map;

public class DescComparator implements Comparator {
	@Override
	public int compare(Object o1, Object o2) {
		Map.Entry e1 = (Map.Entry) o1;
		Map.Entry e2 = (Map.Entry) o2;
		return ((Long) e2.getValue()).compareTo((Long) e1.getValue());
	}
}
