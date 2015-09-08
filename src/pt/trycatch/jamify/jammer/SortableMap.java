package pt.trycatch.jamify.jammer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SortableMap {
	
	public enum ORDER {
		ASCENDING {
			@Override public <K extends Comparable<? super K>, V extends Comparable<? super V>> void sortByValue(List<Map.Entry<K, V>> list) {
				Collections.sort(list, new Comparator<Map.Entry<K, V>>()
				        {
				            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
				            {
				                if(o1.getValue().equals(o2.getValue()))
				                	return (o1.getKey()).compareTo(o2.getKey());
				                return (o1.getValue()).compareTo(o2.getValue());
				            }
				        });
			}
		},
		DESCENDING {
			@Override public <K extends Comparable<? super K>, V extends Comparable<? super V>> void sortByValue(List<Map.Entry<K, V>> list) {
				Collections.sort(list, new Comparator<Map.Entry<K, V>>()
				        {
				            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
				            {
				            	if(o2.getValue().equals(o1.getValue()))
				                	return (o2.getKey()).compareTo(o1.getKey());
				                return (o2.getValue()).compareTo(o1.getValue());
				            }
				        });
			}
		};
		
		public abstract <K extends Comparable<? super K>, V extends Comparable<? super V>> void sortByValue(List<Map.Entry<K, V>> list);
	}
	
	public static <K extends Comparable<? super K>, V extends Comparable<? super V>> Map<K, V> sort(Map<K, V> map, ORDER order) {
		List<Map.Entry<K, V>> list =
            new LinkedList<Map.Entry<K, V>>(map.entrySet());
		
		order.sortByValue(list);
		
		Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
	}
	
}

