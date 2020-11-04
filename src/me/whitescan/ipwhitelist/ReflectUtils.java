package me.whitescan.ipwhitelist;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Whitescan
 *
 */
public class ReflectUtils {

	// Hide construktor
	private ReflectUtils() {
	}

	public static Map<String, Method> methods = new HashMap<String, Method>();
	public static Map<String, Field> fields = new HashMap<String, Field>();

	public static boolean fieldExists(String fieldname, Object obj) {
		String fieldid = obj.getClass().getName() + " " + fieldname;
		if (fields.containsKey(fieldid)) {
			return true;
		}
		for (Field f : obj.getClass().getDeclaredFields()) {
			if (!f.getName().equals(fieldname))
				continue;
			fields.put(fieldid, f);
			return true;
		}
		for (Field f : obj.getClass().getFields()) {
			if (!f.getName().equals(fieldname))
				continue;
			fields.put(fieldid, f);
			return true;
		}
		return false;
	}

	public static Object getField(String fieldname, Object obj)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		String fieldid = obj.getClass().getName() + " " + fieldname;
		for (Field f : obj.getClass().getDeclaredFields()) {
			if (!f.getName().equals(fieldname))
				continue;
			f.setAccessible(true);
			fields.put(fieldid, f);
			return f.get(obj);
		}
		for (Field f : obj.getClass().getFields()) {
			if (!f.getName().equals(fieldname))
				continue;
			f.setAccessible(true);
			fields.put(fieldid, f);
			return f.get(obj);
		}
		return null;
	}

	public static boolean methodExists(String methodname, Object obj, Object... args) {
		Object[] paramTypes = new Class[args.length];
		for (int i = 0; i < args.length; ++i) {
			paramTypes[i] = args.getClass();
		}
		String methodid = getMethodId(methodname, obj, (Class[]) paramTypes);
		if (methods.containsKey(methodid)) {
			return true;
		}
		for (Method m : obj.getClass().getDeclaredMethods()) {
			if (!m.getName().equals(methodname) || !Arrays.equals(m.getParameterTypes(), paramTypes))
				continue;
			methods.put(methodid, m);
			return true;
		}
		for (Method m : obj.getClass().getMethods()) {
			if (!m.getName().equals(methodname) || !Arrays.equals(m.getParameterTypes(), paramTypes))
				continue;
			methods.put(methodid, m);
			return true;
		}
		return false;
	}

	public static Object invokeMethod(String methodname, Object obj, Object... args)
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] paramTypes = new Class[args.length];
		for (int i = 0; i < args.length; ++i) {
			paramTypes[i] = args.getClass();
		}
		String methodid = getMethodId(methodname, obj, (Class[]) paramTypes);
		if (methods.containsKey(methodid)) {
			return methods.get(methodid).invoke(obj, args);
		}
		for (Method m : obj.getClass().getDeclaredMethods()) {
			if (!m.getName().equals(methodname) || !Arrays.equals(m.getParameterTypes(), paramTypes))
				continue;
			m.setAccessible(true);
			methods.put(methodid, m);
			return m.invoke(obj, args);
		}
		for (Method m : obj.getClass().getMethods()) {
			if (!m.getName().equals(methodname) || !Arrays.equals(m.getParameterTypes(), paramTypes))
				continue;
			m.setAccessible(true);
			methods.put(methodid, m);
			return m.invoke(obj, args);
		}
		return null;
	}

	public static String getMethodId(String methodname, Object obj, Class<?>[] paramTypes) {
		StringBuilder b = new StringBuilder(obj.getClass().getName() + "." + methodname + "(");
		if (paramTypes.length > 0) {
			b.append(paramTypes[0].getName());
			for (int i = 1; i < paramTypes.length; ++i) {
				b.append(", ");
				b.append(paramTypes[i].getName());
			}
		}
		b.append(");");
		return b.toString();
	}
}
