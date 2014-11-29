package personal.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class which helps us to make some reflection works over java classes.
 * 
 * @author Armen, soso
 * 
 */

public final class ClassUtils {

    private static final String KEY_CONNECTOR = "&";

    private static final String GETTER_PREFIX = "get";

    private static final String SETTER_PREFIX = "set";

    private static final char SETTER_PREFIX_CHAR = 's';

    private static final int SETTER_FIRST_INDEX = 1;

    private final static Map<Class<?>, Method> ID_METHOD_MAP = new HashMap<Class<?>, Method>();

    /**
     * Hidden constructor
     */
    private ClassUtils() {
	throw new AssertionError();
    }

    /**
     * Cascade of maps which stores method pairs for object pairs. We use this
     * map to speed up reflection, in this case map is used like cache.
     * 
     */
    private static final Map<String, Map<Method, Method>> METHODS = new HashMap<String, Map<Method, Method>>();

    /**
     * 
     * This useful method fills destination object's values from source objects
     * filed same-named field's values.
     * 
     * We use this method often when doing database conversions.
     * 
     * Special map (cache) is used to speed up reflection operations.
     * 
     * @param sourceObject
     *            Object filled from.
     * @param destinationObject
     *            Object filled to.
     * @return The filled object.
     */
    public static <T, M> M fillFields(T sourceObject, M destinationObject) {

	Class<?> tClass = sourceObject.getClass();
	Class<?> mClass = destinationObject.getClass();

	/* Retrieve the cache. */
	Map<Method, Method> map = getMethodsGetter2Setter(tClass, mClass);

	/*
	 * Iterates over cached public methods of the class T and invokes
	 * getters for setters.
	 */
	Set<Map.Entry<Method, Method>> methods = map.entrySet();
	Method getter;
	Method setter;
	Object value;
	for (Map.Entry<Method, Method> entry : methods) {
	    try {
		getter = entry.getKey();
		value = getter.invoke(sourceObject);
		setter = entry.getValue();
		setter.invoke(destinationObject, value);
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}

	return destinationObject;
    }

    public static <T, M> M mergeFields(T sourceObject, M destinationObject) {

	Class<?> tClass = sourceObject.getClass();
	Class<?> mClass = destinationObject.getClass();

	/* Retrieve the cache. */
	Map<Method, Method> map = getMethodsGetter2Setter(tClass, mClass);

	/*
	 * Iterates over cached public methods of the class T and invokes
	 * getters for setters.
	 */
	Set<Map.Entry<Method, Method>> methods = map.entrySet();
	Method getter;
	Method setter;
	Object value;
	Object existed;
	for (Map.Entry<Method, Method> entry : methods) {
	    try {
		getter = entry.getKey();
		existed = getter.invoke(destinationObject);
		if (existed == null) {
		    value = getter.invoke(sourceObject);
		    if (ObjectUtils.notNull(value)) {
			setter = entry.getValue();
			setter.invoke(destinationObject, value);
		    }
		}
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}

	return destinationObject;
    }

    public static String getGetterMethodName(String fieldName) {

	String prefix = fieldName.substring(CollectionUtils.FIRST_INDEX,
		CollectionUtils.SECOND_INDEX).toUpperCase();
	String sufix = fieldName.substring(CollectionUtils.SECOND_INDEX);

	String getter = StringUtils.concat(GETTER_PREFIX, prefix, sufix);

	return getter;
    }

    public static String getGetterMethodName(Field field) {

	String fieldName = field.getName();
	String getterName = getGetterMethodName(fieldName);

	return getterName;
    }

    private static String getSetterMethodName(Field field) {

	String name = field.getName();
	String prefix = name.substring(0, 1).toUpperCase();
	String sufix = field.getName().substring(1);

	StringBuilder builder = new StringBuilder().append(SETTER_PREFIX)
		.append(prefix).append(sufix);
	return builder.toString();
    }

    private static String getKey(Class<?> class1, Class<?> class2) {

	String key = class1.getName().concat(KEY_CONNECTOR)
		.concat(class2.getName());

	return key;
    }

    private static boolean isGetter(Method method) {

	String name = method.getName();
	Class<?> returnType = method.getReturnType();
	Class<?>[] parameters = method.getParameterTypes();
	int modifiers = method.getModifiers();
	boolean valid = name.startsWith(GETTER_PREFIX)
		&& !ObjectUtils.valid(parameters)
		&& Modifier.isPublic(modifiers)
		&& !Modifier.isStatic(modifiers)
		&& !Modifier.isAbstract(modifiers)
		&& !returnType.equals(void.class)
		&& !returnType.equals(Void.class);

	return valid;
    }

    private static String getSetterName(Method getter) {

	String getterName = getter.getName();
	int length = getterName.length();
	String root = getterName.substring(SETTER_FIRST_INDEX, length);
	StringBuilder builder = new StringBuilder().append(SETTER_PREFIX_CHAR)
		.append(root);

	return builder.toString();
    }

    private static Method getSetter(Method getter, Class<?> clazz) {

	String setterName = getSetterName(getter);
	Method[] methods = clazz.getDeclaredMethods();
	int length = methods == null ? 0 : methods.length;
	Method setter = null;
	Method method;
	String name;
	for (int i = 0; i < length && setter == null; i++) {
	    method = methods[i];
	    name = method.getName();
	    if (name.equals(setterName)) {
		setter = method;
	    }
	}

	return setter;
    }

    /**
     * Maintains map of two classes' same-named fields' getters and setters.
     * 
     * Used to speed up reflection and override extra processing.
     * 
     * 
     * 
     * @param class1
     * @param class2
     * @return
     */
    protected static Map<Method, Method> getMethodsGetter2Setter(
	    Class<?> class1, Class<?> class2) {

	/* Key for map. */
	String mapKey = getKey(class1, class2);

	/* Try to get from map. */
	Map<Method, Method> map = METHODS.get(mapKey);
	if (map == null) {

	    /* If no such element in the map construct it and.. */
	    map = new HashMap<Method, Method>();
	    boolean isGetter;
	    Method setter;
	    Method[] methods = class1.getDeclaredMethods();
	    for (Method method : methods) {

		/*
		 * Here we take only getter methods, since it is assumed that we
		 * use only POJOs. Find corresponding setter and put into
		 * sub-map.
		 */
		isGetter = isGetter(method);
		if (isGetter) {
		    setter = getSetter(method, class2);
		    if (setter != null) {
			map.put(method, setter);
		    }
		}
	    }

	    // and put into map for further use;
	    METHODS.put(mapKey, map);
	}

	return map;
    }

    private static boolean contains(Annotation[] annotations,
	    String annotationName) {

	boolean valid = Boolean.FALSE;
	int length = annotations == null ? 0 : annotations.length;
	Annotation annotation;
	for (int i = 0; i < length && !valid; i++) {
	    annotation = annotations[i];
	    valid = annotation.toString().contains(annotationName);
	}
	return valid;
    }

    /**
     * Whether the class has filed with annotation.
     * 
     * @param theClass
     *            Class which fields are checked for annotation.
     * @param annotationName
     *            The string which must be contained into annotation's full
     *            name.
     * 
     * @return First found field with such annotation.
     */
    public static Field hasAnnotatedField(Class<?> theClass,
	    String annotationName) {

	Field annotatedField = null;
	Field[] fields = theClass.getDeclaredFields();
	int length = fields == null ? 0 : fields.length;
	boolean valid = Boolean.FALSE;
	Annotation[] annotations;
	Field field;
	for (int i = 0; i < length & !valid; i++) {
	    field = fields[i];
	    annotations = field.getDeclaredAnnotations();
	    valid = contains(annotations, annotationName);
	    if (valid) {
		annotatedField = field;
	    }
	}

	return annotatedField;
    }

    /**
     * Whether the class has filed with annotation.
     * 
     * @param theClass
     *            Class which fields are checked for annotation.
     * @param annotationClass
     *            {@link Annotation} implementation {@link Class}.
     * 
     * @return First found field with such annotation.
     */
    public static Field hasAnnotatedField(Class<?> theClass,
	    Class<? extends Annotation> annotationClass) {

	Field annotatedField = null;
	Field[] fields = theClass.getDeclaredFields();
	int length = fields == null ? 0 : fields.length;
	boolean valid = Boolean.FALSE;
	Field field;
	for (int i = 0; i < length & !valid; i++) {
	    field = fields[i];
	    valid = field.isAnnotationPresent(annotationClass);
	    if (valid) {
		annotatedField = field;
	    }
	}

	return annotatedField;
    }

    /**
     * Whether the class has annotation.
     * 
     * @param theClass
     *            Class which is checked for annotation.
     * @param annotationName
     *            The string which must be contained into annotation's full
     *            name.
     * 
     * @return true if class has annotation with name containing second
     *         parameter.
     */
    public static boolean hasAnnotation(Class<?> theClass, String annotationName) {

	Annotation[] annotations = theClass.getDeclaredAnnotations();
	boolean valid = contains(annotations, annotationName);
	return valid;
    }

    /**
     * Gets the setter {@link Method} for the field
     * 
     * 
     * @param theClass
     *            Class.
     * @param field
     *            Field object to get setter for.
     * 
     * @return Desired method.
     * 
     * @throws NoSuchMethodException
     *             Occurs when reflection problem - can not find field.
     * @throws SecurityException
     *             Occurs when security problem.
     */
    public static Method getSetterMethod(Field field)
	    throws NoSuchMethodException, SecurityException {

	Method setter;
	if (field != null) {
	    Class<?> declaringClass = field.getDeclaringClass();
	    String name = getSetterMethodName(field);
	    setter = declaringClass.getDeclaredMethod(name, field.getType());
	} else {
	    setter = null;
	}

	return setter;
    }

    /**
     * Gets the getter method for the field
     * 
     * 
     * @param theClass
     *            Class.
     * @param field
     *            Field object to get getter for.
     * 
     * @return Desired method.
     * 
     * @throws NoSuchMethodException
     *             Occurs when reflection problem - can not find field.
     * @throws SecurityException
     *             Occurs when security problem.
     */
    public static Method getGetterMethod(Field field)
	    throws NoSuchMethodException, SecurityException {

	Method getter;
	if (field != null) {
	    Class<?> declaringClass = field.getDeclaringClass();
	    String name = getGetterMethodName(field);
	    getter = declaringClass.getDeclaredMethod(name);
	} else {
	    getter = null;
	}

	return getter;
    }

    private static Method getGetterMethodFromMethods(String fieldName,
	    Class<?> theClass) {

	Method cachedMethod = null;
	Method[] methods = theClass.getDeclaredMethods();
	int length = methods == null ? 0 : methods.length;
	Method method;
	boolean isGetter = Boolean.FALSE;
	String getterName = getGetterMethodName(fieldName);
	for (int i = 0; i < length && !isGetter; i++) {
	    method = methods[i];
	    isGetter = isGetter(method) && method.getName().equals(getterName);
	    if (isGetter) {
		ID_METHOD_MAP.put(theClass, method);
		cachedMethod = method;
	    }
	}

	return cachedMethod;
    }

    private static Method getGetterMethodFromFields(String fieldName,
	    Class<?> theClass) throws NoSuchMethodException, SecurityException {

	Method cachedMethod = null;
	Field[] fields = theClass.getDeclaredFields();
	int length = fields == null ? 0 : fields.length;
	Field field;
	Method method;
	boolean isGetter = Boolean.FALSE;
	for (int i = 0; i < length && !isGetter; i++) {
	    field = fields[i];
	    if (field.getName().equals(fieldName)) {
		method = getGetterMethod(field);
		ID_METHOD_MAP.put(theClass, method);
		cachedMethod = method;
	    }
	}

	return cachedMethod;
    }

    /**
     * Gets the getter method for the field
     * 
     * @param fieldName
     *            Name of the field
     * @param theClass
     *            Class which must contain filed
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getGetterMethod(String fieldName, Class<?> theClass)
	    throws NoSuchMethodException {

	Method cachedMethod = ID_METHOD_MAP.get(theClass);

	if (cachedMethod == null) {
	    cachedMethod = getGetterMethodFromMethods(fieldName, theClass);
	}

	if (cachedMethod == null) {
	    cachedMethod = getGetterMethodFromFields(fieldName, theClass);
	}

	if (cachedMethod == null) {
	    throw new NoSuchMethodException(String.format(
		    "No getter method for field %s in class %s", fieldName,
		    theClass.getName()));
	} else {

	    return cachedMethod;
	}
    }

    public static <T> T instatiate(Class<T> clazz) throws IOException {

	T value;

	try {
	    value = clazz.newInstance();
	} catch (InstantiationException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	}

	return value;
    }

    public static Method getDeclaredMethod(String methodName, Class<?> clazz,
	    Class<?>[] parameterTypes) throws IOException {

	Method method;

	try {
	    method = clazz.getDeclaredMethod(methodName, parameterTypes);
	} catch (NoSuchMethodException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return method;
    }

    public static Method getDeclaredMethod(String methodName, Class<?> clazz)
	    throws IOException {

	Method method = getDeclaredMethod(methodName, clazz, null);

	return method;
    }

    public static Object invoke(Object obj, String methodName,
	    Object... parameters) throws IOException {

	Object value;

	Class<?> clazz = obj.getClass();
	Class<?>[] parameterTypes;
	if (ObjectUtils.valid(parameters)) {
	    int length = parameters.length;
	    parameterTypes = new Class<?>[length];
	    Object parameter;
	    for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
		parameter = parameters[i];
		parameterTypes[i] = parameter.getClass();
	    }
	} else {
	    parameterTypes = null;
	}

	try {
	    Method method = getDeclaredMethod(methodName, clazz, parameterTypes);
	    value = method.invoke(obj, parameters);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
	}

	return value;
    }

    public static Object invoke(Object obj, Method method, Object... parameters)
	    throws IOException {

	Object value;

	Class<?>[] parameterTypes;
	if (ObjectUtils.valid(parameters)) {
	    int length = parameters.length;
	    parameterTypes = new Class<?>[length];
	    Object parameter;
	    for (int i = CollectionUtils.FIRST_INDEX; i < length; i++) {
		parameter = parameters[i];
		parameterTypes[i] = parameter.getClass();
	    }
	} else {
	    parameterTypes = null;
	}

	try {
	    value = method.invoke(obj, parameters);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	} catch (IllegalAccessException ex) {
	    throw new IOException(ex);
	} catch (IllegalArgumentException ex) {
	    throw new IOException(ex);
	} catch (InvocationTargetException ex) {
	    throw new IOException(ex);
	}

	return value;
    }

    public static Object getFieldValue(Field field, Object data)
	    throws IOException {

	Object value;

	try {
	    Method getter = getGetterMethod(field);
	    value = invoke(data, getter);
	} catch (NoSuchMethodException ex) {
	    throw new IOException(ex);
	} catch (SecurityException ex) {
	    throw new IOException(ex);
	}

	return value;
    }
}
