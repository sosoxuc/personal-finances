package personal.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Check if object not null and if {@link Collection} is not empty
 * 
 * @author levan
 * 
 */
public class ObjectUtils {

    public static boolean notEquals(Object thisObject, Object thatObject) {
	return notTrue(thisObject.equals(thatObject));
    }

    public static boolean notTrue(boolean expression) {

	return !expression;
    }

    public static boolean notNull(Object data) {

	boolean check = data != null;

	return check;
    }

    public static boolean notNullAll(Object... datas) {

	boolean check = datas != null;
	if (check) {
	    for (int i = CollectionUtils.FIRST_INDEX; i < datas.length && check; i++) {
		check = datas[i] != null;
	    }
	}

	return check;
    }

    public static boolean isNullOne(Object... datas) {

	boolean check = datas == null;
	if (!check) {
	    for (int i = CollectionUtils.FIRST_INDEX; i < datas.length
		    && !check; i++) {
		check = datas[i] == null;
	    }
	}

	return check;
    }

    public static boolean valid(Collection<?> collection) {

	return collection != null && !collection.isEmpty();
    }

    public static boolean valid(Map<?, ?> map) {

	return map != null && !map.isEmpty();
    }

    public static boolean valid(CharSequence text) {

	return text != null
		&& text.length() > CollectionUtils.EMPTY_ARRAY_LENGTH;
    }

    public static boolean valid(Object[] array) {

	return array != null
		&& array.length > CollectionUtils.EMPTY_ARRAY_LENGTH;
    }

    public static void close(Closeable closeable) throws IOException {

	if (notNull(closeable)) {
	    closeable.close();
	}
    }

    public static void closeAll(Closeable... closeables) throws IOException {

	if (valid(closeables)) {
	    for (Closeable closeable : closeables) {
		close(closeable);
	    }
	}
    }

}
