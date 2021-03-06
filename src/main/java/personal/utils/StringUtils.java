package personal.utils;

import java.util.Map;
import java.util.Random;

/**
 * Utility class for {@link String} and {@link CharSequence} manipulations
 * 
 * @author soso, levan
 * 
 */
public final class StringUtils {

    public static final char SPACE = ' ';

    public static final char EMPTY = ' ';
    
    public static final String EMPTY_STRING = "";

    public static final char SLASH = '/';

    public static final char COLON = ':';

    public static final String NEW_LINE = "\n";

    private StringUtils() {
    throw new AssertionError();
    }

    public static String translateChars(final String text,
        Map<String, String> map) {
    StringBuilder result = new StringBuilder();

    char[] chars = text.toCharArray();

    for (int i = CollectionUtils.FIRST_INDEX; i < chars.length; i++) {
        String trans = map.get(String.valueOf(chars[i]));
        if (trans == null) {
        trans = String.valueOf(chars[i]);
        }
        result.append(trans);
    }

    return result.toString();
    }

    public static String generateString(final String characters, int length) {

    if (characters.length() == CollectionUtils.EMPTY_ARRAY_LENGTH
        || length <= CollectionUtils.EMPTY_ARRAY_LENGTH) {
        throw new IllegalArgumentException();
    }

    Random rnd = new Random();
    char[] chars = characters.toCharArray();
    char[] result = new char[length];
    for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
        result[i] = chars[rnd.nextInt(chars.length)];
    }

    return String.valueOf(result);
    }

    private static void append(Object[] tockens, StringBuilder builder) {

    if (ObjectUtils.valid(tockens)) {
        for (Object tocken : tockens) {
        if (tocken instanceof Object[]) {
            append((Object[]) tocken, builder);
        } else {
            builder.append(tocken);
        }
        }
    }
    }

    /**
     * Creates concatenates passed objects in one text and if one of them is
     * array then concatenates contents of this array recursively
     * 
     * @param tockens
     * @return {@link String}
     */
    public static String concatRecursively(Object... tockens) {

    String concat;

    if (ObjectUtils.valid(tockens)) {
        StringBuilder builder = new StringBuilder();
        append(tockens, builder);
        concat = builder.toString();
    } else {
        concat = null;
    }

    return concat;
    }

    /**
     * Creates concatenates passed objects in one text
     * 
     * @param tockens
     * @return {@link String}
     */
    public static String concat(Object... tockens) {

    String concat;

    if (ObjectUtils.valid(tockens)) {
        StringBuilder builder = new StringBuilder();
        for (Object tocken : tockens) {
        builder.append(tocken);
        }
        concat = builder.toString();
    } else {
        concat = null;
    }

    return concat;
    }
}
