package personal.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

    public static final int FIRST_INDEX = 0;

    public static final int SECOND_INDEX = 1;

    public static final int SINGLETON_SIZE = 1;

    public static final int DOUBLE_SIZE = 2;

    public static final int NON_EXISTING_INDEX = -1;

    private static final String GETTER_PREFIX = "get";

    public static final int EMPTY_ARRAY_LENGTH = 0;

    public static <K, T> Map<K, T> getMapFromList(List<T> list, String idField) {

	// getter method name for key field
	String methodName = idField;
	methodName = StringUtils.concat(GETTER_PREFIX,
		methodName.substring(0, 1).toUpperCase(),
		methodName.substring(1));

	Map<K, T> map = mapFromList(list, methodName);

	return map;
    }

    @SuppressWarnings("unchecked")
    public static <K, T> Map<K, T> mapFromList(List<T> list, String methodName) {

	Map<K, T> map = new HashMap<K, T>();

	try {
	    // put keys and values into map
	    Method method = null;
	    K key;
	    for (T item : list) {
		if (method == null) {
		    method = ClassUtils.getDeclaredMethod(methodName,
			    item.getClass());
		}
		key = (K) ClassUtils.invoke(item, method);
		map.put(key, item);
	    }
	} catch (IOException ex) {
	    map = null;
	}

	return map;
    }

    public static <T> T getFirst(T[] array) {

	T value;
	if (ObjectUtils.valid(array)) {
	    value = array[FIRST_INDEX];
	} else {
	    value = null;
	}

	return value;
    }

    public static <T> T getSecond(T[] array) {

	T value;
	if (ObjectUtils.valid(array)) {
	    value = array[SECOND_INDEX];
	} else {
	    value = null;
	}

	return value;
    }

    public static <T> T getFirst(Collection<T> collection) {

	T value;
	if (collection == null || collection.isEmpty()) {
	    value = null;
	} else if (collection instanceof List) {
	    value = ((List<T>) collection).get(FIRST_INDEX);
	} else {
	    Iterator<T> iterator = collection.iterator();
	    value = iterator.next();
	}

	return value;
    }

    public static <T> T getSecond(Collection<T> collection) {

	T value;
	if (collection == null || collection.size() < DOUBLE_SIZE) {
	    value = null;
	} else if (collection instanceof List) {
	    value = ((List<T>) collection).get(SECOND_INDEX);
	} else {
	    Iterator<T> iterator = collection.iterator();
	    iterator.next();
	    value = iterator.next();
	}

	return value;
    }

    public static <T> T getLast(T[] array) {

	T value;

	if (array == null || array.length == EMPTY_ARRAY_LENGTH) {
	    value = null;
	} else {
	    value = array[array.length - SECOND_INDEX];
	}

	return value;
    }

    public static <T> T getLast(Collection<T> collection) {

	T value;

	if (collection == null || collection.size() < SINGLETON_SIZE) {
	    value = null;
	} else if (collection instanceof List) {
	    value = ((List<T>) collection)
		    .get((collection.size() - SECOND_INDEX));
	} else {
	    Iterator<T> iterator = collection.iterator();
	    iterator.next();
	    value = null;
	    while (iterator.hasNext()) {
		value = iterator.next();
	    }
	}

	return value;
    }

    public static <T> List<List<T>> partition(List<T> list, Integer size) {

	List<List<T>> parts;
	List<T> part;
	if (list.size() <= size) {
	    part = list;
	    parts = new ArrayList<List<T>>();
	    parts.add(part);
	} else {
	    int collSize = list.size();
	    int count = collSize / size;
	    parts = new ArrayList<List<T>>();
	    int subCount = FIRST_INDEX;
	    for (int i = FIRST_INDEX; i < count + CollectionUtils.SECOND_INDEX; i++) {
		part = new ArrayList<T>();
		for (int j = FIRST_INDEX; j < size
			&& (j + subCount) < list.size(); j++) {
		    part.add(list.get(j + subCount));
		}
		parts.add(part);
		subCount += size;
	    }
	}

	return parts;
    }

    public static <K, V1, V2> void andMerge(Map<K, V1> map1, Map<K, V2> map2,
	    K key, V2 value) {

	if (ObjectUtils.notTrue(map1.containsKey(key))) {
	    map2.put(key, value);
	}
    }

    public static <E> void andMerge(Collection<E> collection1,
	    Collection<E> collection2, E data) {

	if (ObjectUtils.notTrue(collection1.contains(data))) {
	    collection2.add(data);
	}
    }
}
