package gr.pricefox;

import javax.tools.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectUtils {
    public static List<Class> getPackageClasses(String packName)throws Exception{
        List<Class> classes = new ArrayList<>();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null);
        StandardLocation location = StandardLocation.CLASS_PATH;
        Set<JavaFileObject.Kind> kinds = new HashSet<JavaFileObject.Kind>();
        kinds.add(JavaFileObject.Kind.CLASS);
        boolean recurse = true;

        Iterable<JavaFileObject> list = fileManager.list(location, packName,
                kinds, recurse);


        String prefix="file://"+System.getProperty("java.class.path").replace(" ","%20")+"/";

        for (JavaFileObject javaFileObject : list) {
            String fullName = javaFileObject.toUri().toString();
            fullName=fullName.replace(prefix,"");
            fullName = fullName.replaceAll("/", ".");
            fullName = fullName.substring(0, fullName.length()-".class".length());
            Class clzz = Class.forName(fullName);
            classes.add(clzz);
        }
        return classes;
    }


}
