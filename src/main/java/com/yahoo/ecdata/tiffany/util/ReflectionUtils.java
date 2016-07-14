package com.yahoo.ecdata.tiffany.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

@SuppressWarnings("unused")
public final class ReflectionUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);
	private static final String CLASS_PATTERN = "classpath*:%s/**/*.class";
	private static final String CLASS_SUFFIX = ".class";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Class[] getClassesWithParent(String basePackageName,
			Class requiredType) throws ClassNotFoundException, IOException {
		basePackageName = basePackageName.replace(".", "/");
		List classes = new ArrayList();
		String pattern = String.format(CLASS_PATTERN, basePackageName);
		String[] classNames = getResourceNames(pattern, CLASS_SUFFIX, true);
		for (String className : classNames) {
			Class<?> theClass = Class.forName(className);
			if (requiredType.isAssignableFrom(theClass)
					&& !theClass.isInterface()) {
				classes.add(theClass);
			}
		}
		return (Class[]) classes.toArray(new Class[classes.size()]);
	}

	
	public static String findContainingJar(Class<?> requiredType)
			throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return findContainingJar(loader, requiredType);
	}

	public static String findContainingJar(ClassLoader loader,
			Class<?> requiredType) throws IOException {
		String classFile = ClassUtils
				.convertClassNameToResourcePath(requiredType.getName())
				+ ClassUtils.CLASS_FILE_SUFFIX;

		for (Enumeration<URL> itr = loader.getResources(classFile); itr
				.hasMoreElements();) {
			URL url = itr.nextElement();
			if (ResourceUtils.isJarURL(url)) {
				String toReturn = url.getPath();
				if (toReturn.startsWith("file:")) {
					toReturn = toReturn.substring("file:".length());
				}

				toReturn = URLDecoder.decode(toReturn, "UTF-8");
				return toReturn.substring(0,
						toReturn.indexOf(ResourceUtils.JAR_URL_SEPARATOR));
			}
		}

		return null;
	}

	/**
	 * Get all classes in package basePackageName that implement the interface
	 * specified by given interfaceName.
	 * 
	 * @param basePackageName
	 *            base package name
	 * @param requiredType
	 *            interface name to find implementations for
	 * @return the class list which matches required class type
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @see <a href="http://snippets.dzone.com/posts/show/4831">Get all classes
	 *      within a package</a>
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T>[] getClassesWithInterface(
			String basePackageName, Class<T> requiredType) throws IOException,
			ClassNotFoundException {
		basePackageName = basePackageName.replace(".", "/");
		List<T> classes = new ArrayList<T>();
		String pattern = String.format(CLASS_PATTERN, basePackageName);
		String[] classNames = getResourceNames(pattern, CLASS_SUFFIX, true);

		for (String className : classNames) {
			
			Class<?> theClass = Class.forName(className);
			boolean same=requiredType.equals(theClass);
			boolean assignable=requiredType.isAssignableFrom(theClass);
			if (!same && assignable) {
				classes.add((T) theClass);
			}
		}

		return classes.toArray(new Class[classes.size()]);
	}

	
	@SuppressWarnings("unchecked")
	public static <T> Class<T>[] getClassesWithAnnotation(
			String basePackageName,
			Class<?> annotationClass) throws IOException,
			ClassNotFoundException {
		basePackageName = basePackageName.replace(".", "/");
		List<Class<?>> classes = new ArrayList<Class<?>>();
			String pattern = String.format(CLASS_PATTERN, basePackageName);
			String[] classNames = getResourceNames(pattern, CLASS_SUFFIX, true);
			for (String className : classNames) {
				Class<?> theClass = Class.forName(className);
				Annotation[] classAnnotations=theClass.getAnnotations();
				for(int i=0;i<classAnnotations.length;i++){
					if (classAnnotations[i].annotationType().getName().equals(annotationClass.getName())) {
						classes.add(theClass);
					}
				}
			}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Get all public methods of the same name
	 * 
	 * @param clazz
	 *            target class
	 * @param name
	 *            of method
	 * @return Method[] if no method, return an empty[]
	 */
	public static Method[] getMethodsByName(Class<?> clazz, String name) {
		// Class<?>clazz=object.getClass();
		Method[] methods = clazz.getMethods();// NPE
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.getName().equals(name)) {
				list.add(m);
			}
		}
		return list.toArray(new Method[list.size()]);
	}

	public static Field[] getFields(Class<?> clazz) {
		// Class<?>clazz=object.getClass();
		Field[] fields = clazz.getFields();
		return fields;
	}

	/**
	 * 
	 * @param pattern
	 *            the resource location
	 * @param suffix
	 * @param convert
	 *            if true, convert a "/" based resource path to a "." based
	 *            fully qualified class name.
	 * @return resource name list
	 * @throws IOException
	 */
	public static String[] getResourceNames(String pattern, String suffix,
			boolean convert) throws IOException {
		String classPaths = System.getProperty("java.class.path");
		String[] classPathAr = classPaths.split(File.pathSeparator);
		List<String> resourceNames = new ArrayList<String>();

		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resourcePatternResolver.getResources(pattern);
		for (Resource resource : resources) {
			URL url = resource.getURL();

			String resourceName;			
			if (ResourceUtils.isJarURL(url)) {
				resourceName = resolveClassFromjar(url.toString());
			} else if(url.toString().contains(".jar")){
				resourceName = resolveClassFromPath(url.toString());
			}else{
				resourceName = resolveClassFromPath(url.toString(), classPathAr);
			}
			if (resourceName != null) {
				int index = resourceName.lastIndexOf(suffix);
				if (index >= 0) {
					resourceName = resourceName.substring(0, index);
				}

				if (convert) {
					resourceNames.add(ClassUtils
							.convertResourcePathToClassName(resourceName));
				} else {
					resourceNames.add(resourceName);
				}
			}
		}		
		return resourceNames.toArray(new String[resourceNames.size()]);
	}
	

	public static String[] getResourceNamesWithPrefix(String pattern,
			String suffix, boolean convert) throws IOException {
		String[] resources = getResourceNames(pattern, suffix, convert);
		for (int i = 0; i < resources.length; i++) {
			resources[i] = "classpath*:" + resources[i];
		}

		return resources;
	}

	public static Class<?>[] getExportedInterface(String beanName, Object bean,
			Class<? extends Annotation> annotationClass) {
		Class<?>[] interfaces = null;
		if (AopUtils.isAopProxy(bean)) {
			interfaces = AopProxyUtils.proxiedUserInterfaces(bean);
		} else {
			interfaces = ClassUtils.getAllInterfaces(bean);
		}

		interfaces = getInterfacesRecursively(interfaces);

		List<Class<?>> interfaceList = new ArrayList<Class<?>>();
		for (Class<?> interfaceClass : interfaces) {
			if (AnnotationUtils.findAnnotation(interfaceClass, annotationClass) != null) {
				interfaceList.add(interfaceClass);
			}
		}

		return interfaceList.toArray(new Class<?>[interfaceList.size()]);
	}

	public static Class<?>[] getInterfacesRecursively(Class<?>... interfaces) {
		Set<Class<?>> interfaceSet = new HashSet<Class<?>>();

		for (Class<?> interfaceClass : interfaces) {
			interfaceSet.add(interfaceClass);
			interfaceSet.addAll(Arrays
					.asList(getInterfacesRecursively(interfaceClass
							.getInterfaces())));
		}

		return interfaceSet.toArray(new Class<?>[interfaceSet.size()]);
	}

	private static String resolveClassFromPath(String url, String[] classPaths) {
		for (String classPath : classPaths) {
			int index = url.replace("\\", "/").lastIndexOf(
					classPath.replace("\\", "/"));
			if (index >= 0) {
				String classUrl = url.substring(index + classPath.length() + 1);
				return classUrl;
			}
		}

		return null;
	}
	
	private static String resolveClassFromPath(String url) {
		int index = url.indexOf(".jar");
		if(index >= 0){
			url = url.substring(index+5);
			return url;
		}
		return null;
	}

	private static String resolveClassFromjar(String url) {
		return url.substring(url.indexOf(ResourceUtils.JAR_URL_SEPARATOR) + 2);
	}
}

