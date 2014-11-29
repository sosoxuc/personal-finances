package personal.utils;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class SqlUtils {

    /**
     * Adds SQL string parameter to the SQL text and parameter map increasing
     * parameter counter for further use.
     * 
     * @param data
     *            actual parameter object.
     * @param params
     *            parameter map to add new parameter.
     * @param sql
     *            SQL builder object.
     * @param expression
     *            SQL text to add to final SQL.
     * @param contains
     *            whether text is searched from the start, end or anywhere.
     * @return increased counter.
     */
    public static void sqlParam(String data, Map<String, Object> params,
	    StringBuilder sql, String expression, SqlStringContaining contains) {

	if (StringUtils.isBlank(data)) {
	    return;
	}
	switch (contains) {
	case END:
	    data = "%".concat(data);
	    break;
	case START:
	    data = data.concat("%");
	    break;
	case FULL:
	    data = data.replace(" ", "%");
	    data = "%".concat(data).concat("%");
	    break;
	default:
	    break;
	}

	data = data.trim();

	sqlParam(data, params, sql, expression);
    }

    /**
     * Adds SQL string parameter to the SQL text and parameter map increasing
     * parameter counter for further use.
     * 
     * @param data
     *            actual parameter object.
     * @param params
     *            parameter map to add new parameter.
     * @param sql
     *            SQL builder object.
     * @param expression
     *            SQL text to add to final SQL.
     * @return increased counter.
     */
    public static void sqlParam(Object data, Map<String, Object> params,
	    StringBuilder sql, String expression) {

	if (data != null) {
	    sql.append(expression);
	    String param = extractParameter(expression);
	    params.put(param, data);
	}
    }

    public static String extractParameter(String expression) {

	if (StringUtils.isBlank(expression)) {
	    throw new IllegalArgumentException();
	}

	int count = StringUtils.countMatches(expression, ":");

	if (count != 1) {
	    throw new IllegalArgumentException();
	}

	int pos = StringUtils.indexOf(expression, ":");

	String sub = expression.substring(pos + 1);

	if (StringUtils.isBlank(sub)) {
	    throw new IllegalArgumentException();
	}

	String paramName = sub.split("\\s+")[0];

	return paramName;
    }

    /**
     * Enumerator of database text search options, whether text is searched from
     * the start, end or anywhere.
     * 
     * @author soso
     * 
     */
    public enum SqlStringContaining {
	START, END, FULL, NONE;
    }
}
