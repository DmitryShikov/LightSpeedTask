package com.lightspeed;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public final class CopyUtility {

    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T source) {
        return (T) deepCopy(source, new IdentityHashMap<>());
    }

    private static Object deepCopy(
            Object sourceObject,
            IdentityHashMap<Object, Object> visitedObjects
    ) {
        if (sourceObject == null) {
            return null;
        }

        if (visitedObjects.containsKey(sourceObject)) {
            return visitedObjects.get(sourceObject);
        }

        Class<?> sourceClass = sourceObject.getClass();

        if (isImmutable(sourceClass) || isAcceptableToCopy(sourceClass)) {
            return sourceObject;
        }

        if (sourceClass.isArray()) {
            int length = Array.getLength(sourceObject);
            Object copiedArray = Array.newInstance(sourceClass.componentType(), length);
            for (int i = 0; i < length; i++) {
                Object copiedValue = deepCopy(Array.get(sourceObject, i), visitedObjects);
                Array.set(copiedArray, i, copiedValue);
            }
            visitedObjects.put(sourceObject, copiedArray);
            return copiedArray;
        }

        if (sourceObject instanceof Collection<?> collection) {
            Collection<Object> copiedCollection = getEmptyCollection(sourceClass);
            visitedObjects.put(sourceObject, copiedCollection);
            for (Object item : collection) {
                copiedCollection.add(deepCopy(item, visitedObjects));
            }
            visitedObjects.put(collection, copiedCollection);
            return copiedCollection;
        }

        if (sourceObject instanceof Map<?, ?> map) {
            Map<Object, Object> copiedMap = getEmptyMap(sourceClass);
            visitedObjects.put(sourceObject, copiedMap);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                copiedMap.put(
                        deepCopy(entry.getKey(), visitedObjects),
                        deepCopy(entry.getValue(), visitedObjects)
                );
            }
            return copiedMap;
        }

        try {
            Constructor<Object> constructor = (Constructor<Object>) ReflectionFactory
                    .getReflectionFactory()
                    .newConstructorForSerialization(sourceClass, Object.class.getDeclaredConstructor());
            Object copiedObject = constructor.newInstance();
            visitedObjects.put(sourceObject, copiedObject);
            while (sourceClass != null) {
                Field[] sourceFields = sourceClass.getDeclaredFields();
                for (Field field : sourceFields) {
                    if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object copiedValue = deepCopy(field.get(sourceObject), visitedObjects);
                    field.set(copiedObject, copiedValue);
                }
                sourceClass = sourceClass.getSuperclass();
            }
            return copiedObject;
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Exception during copy plain object" + sourceClass, e);
        }
    }

    private static boolean isAcceptableToCopy(Class<?> sourceClass) {
        return sourceClass.isSynthetic()
                || sourceClass.isAnonymousClass()
                || sourceClass.getName().contains("$$Lambda$")
                || sourceClass == Thread.class
                || sourceClass == java.net.Socket.class
                || sourceClass == java.io.FileInputStream.class;
    }

    private static Map<Object, Object> getEmptyMap(Class<?> sourceClass) {
        if (HashMap.class.isAssignableFrom(sourceClass)) {
            return new HashMap<>();
        }
        if (TreeMap.class.isAssignableFrom(sourceClass)) {
            return new TreeMap<>();
        }
        if (LinkedHashMap.class.isAssignableFrom(sourceClass)) {
            return new LinkedHashMap<>();
        }
        if (IdentityHashMap.class.isAssignableFrom(sourceClass)) {
            return new IdentityHashMap<>();
        }
        if (ConcurrentHashMap.class.isAssignableFrom(sourceClass)) {
            return new ConcurrentHashMap<>();
        }
        return new HashMap<>();
    }

    private static Collection<Object> getEmptyCollection(Class<?> sourceClass) {
        if (ArrayList.class.isAssignableFrom(sourceClass)) {
            return new ArrayList<>();
        }
        if (LinkedList.class.isAssignableFrom(sourceClass)) {
            return new LinkedList<>();
        }
        if (HashSet.class.isAssignableFrom(sourceClass)) {
            return new HashSet<>();
        }
        return new ArrayList<>();
    }

    private static boolean isImmutable(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz.isEnum()
                || Number.class.isAssignableFrom(clazz)
                || clazz == Boolean.class
                || clazz == Character.class;
    }
}
