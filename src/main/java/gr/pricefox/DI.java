package gr.pricefox;

import gr.pricefox.annotations.Autowired;
import gr.pricefox.annotations.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

public class DI {
    private Map<Class<?>, Class<?>> diMap;
    private Map<Class<?>, Object> scopeMap;

    private static DI DI;

    private DI() {
        diMap = new HashMap<>();
        scopeMap = new HashMap<>();
    }


    public static void startApp(Class<?> mainClass) {
        try {
            if (DI == null) {
                DI = new DI();
                DI.init(mainClass);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T getBean(Class<T> classz) {
        try {
            return (T) DI.getComponentInstance(classz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void init(Class<?> mainClass){
        try
        {
            List<Class> classes = ReflectUtils.getPackageClasses(mainClass.getPackage().getName());
            //we add on diMap all ComponentAnnotated classes and their interfaces
            //for now we don't support multiple implementations of one interface
            Set<Class> types=getComponentAnnotatedClasses(mainClass.getPackage().getName());
            for (Class<?> componentClass : types) {
                Class<?>[] interfaces = componentClass.getInterfaces();
                if (interfaces.length == 0) {
                    diMap.put(componentClass, componentClass);
                } else {
                    for (Class<?> iface : interfaces) {
                        diMap.put(componentClass, iface);
                    }
                }
            }
            //we add on scopeMap all ComponentAnnotated classes and instances
            for (Class<?> classz : classes) {
                if (classz.isAnnotationPresent(Component.class)) {
                    Object classInstance = classz.newInstance();
                    scopeMap.put(classz, classInstance);
                    //inject classInstance (dependency) to classz (dependant)
                    autoWire( classz, classInstance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Set<Class> getComponentAnnotatedClasses(String packageName) throws Exception {
        Set<Class> componentClasses=new HashSet<>();
        ReflectUtils.getPackageClasses(packageName).forEach(c->{
            if(c.isAnnotationPresent(Component.class))componentClasses.add(c);
        });
        return componentClasses;
    }


    public <T> Object getComponentInstance(Class<T> interfaceClass)
            throws InstantiationException, IllegalAccessException {
        Class<?> implementationClass = getImplementationClass(interfaceClass);

        if (scopeMap.containsKey(implementationClass)) {
            return scopeMap.get(implementationClass);
        }
        Object service = implementationClass.newInstance();
        scopeMap.put(implementationClass, service);
        return service;
    }


    private Class<?> getImplementationClass(Class<?> interfaceClass) {
        Set<Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream()
                .filter(entry -> entry.getValue() == interfaceClass).collect(Collectors.toSet());
        String errorMessage = "";
        if (implementationClasses == null || implementationClasses.size() == 0) {
            errorMessage = "no implementation found for interface " + interfaceClass.getName();
        } else if (implementationClasses.size() == 1) {
            Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();
            if (optional.isPresent()) {
                return optional.get().getKey();
            }
        } else if (implementationClasses.size() > 1) {
            //toDo: Consider case of multiple implementations of one Interface
            errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName()
                    + " Expected single implementation";
        }
        throw new RuntimeErrorException(new Error(errorMessage));
    }

    private void autoWire( Class<?> classz, Object classInstance)
            throws InstantiationException, IllegalAccessException {

        List<Field> annotatedFields=new ArrayList<>();
        //Get Components (fields)
        for (Field field : classz.getDeclaredFields()) {
            if( field.isAnnotationPresent(Autowired.class)){
                //because is private field
                field.trySetAccessible();
                annotatedFields.add(field);
            }
        }
        for (Field field : annotatedFields) {
            //Inject fieldInstance on classInstance
            Object fieldInstance = getComponentInstance(field.getType());
            field.set(classInstance,fieldInstance);
            autoWire( fieldInstance.getClass(), fieldInstance);
        }
    }
}
