package de.ypsilon.st;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Executor for handling the compiling and evaluation from the source files
 * May produce various exceptions. Use with care :)
 *
 * @version 1.0
 * @author yJulian, yNiklas
 */
public class Executor {

    private JavaCompiler compiler;
    private List<JavaFileObject> sourceFiles;
    private Map<String, ByteArrayOutputStream> classCache;
    private ForwardingJavaFileManager<StandardJavaFileManager> forwardingJavaFileManager;

    /**
     * Constructor to create a new usable instance from the Executor with everything set up.
     */
    public Executor() {
        init();
    }

    /**
     * Initialize the Executor to prepare it to load the classes inserted with the
     * {@link #loadClass(String, CharSequence)} method.
     *
     * @throws RuntimeException when not able to get a compiler
     * @throws IllegalArgumentException when the location or className is not valid
     */
    private void init() {
        classCache = new HashMap<>();
        compiler = ToolProvider.getSystemJavaCompiler();

        // Throw exception when compiler is not found. OS not supported?!
        if (null == compiler)
            throw new RuntimeException("Could not get a compiler.");

        // Initialize a standard file manager instance from the compiler with default values
        StandardJavaFileManager standardFileManager  = compiler
                .getStandardFileManager(null, Locale.GERMAN, Charset.defaultCharset());

        // Initialize the forwarding java file manager used to compile the source files
        forwardingJavaFileManager = new ForwardingJavaFileManager<>(standardFileManager) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
                if (StandardLocation.CLASS_OUTPUT == location && JavaFileObject.Kind.CLASS == kind)
                    return new SimpleJavaFileObject(URI.create("mem:///" + className + ".class"), JavaFileObject.Kind.CLASS) {
                        @Override
                        public OutputStream openOutputStream() {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            classCache.put(className, outputStream);
                            return outputStream;
                        }
                    };
                else
                    throw new IllegalArgumentException("Unexpected output file requested: " + location + ", " + className + ", " + kind);
            }
        };

        sourceFiles = new LinkedList<>();
    }

    /**
     * Load a class to the classpath used to execute linked class methods
     *
     * @param className the name from the Class
     * @param classCode the source code
     */
    public void loadClass(String className, CharSequence classCode) {
        SimpleJavaFileObject simpleJavaFileObject = new SimpleJavaFileObject(
                URI.create("string:///" + className + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return classCode;
            }
        };

        sourceFiles.add(simpleJavaFileObject);
    }

    /**
     * Load a sourcecode file
     *
     * @param file the source file as a {@link File}
     * @throws RuntimeException when the file is not found
     */
    public void loadFile(File file)  {
        try {
            String className = file.getName().split("\\.")[0];
            StringBuilder builder = new StringBuilder();

            Scanner scanner = new Scanner(new FileReader(file));
            scanner.useDelimiter("\n"); // Set the delimiter to a line break
            scanner.forEachRemaining(s -> builder.append(s).append("\n"));

            loadClass(className, builder.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
    }

    /**
     * Evaluate a method from the source files
     *
     * @param className the classname from the classes method
     * @param methodName the name from the method
     * @param instance a instance from the class or null. In that case the default constructor
     *                 is used to instantiate a new instance from the class. When the String main
     *                 is provided the method is executed as a class method.
     * @param args the arguments from the method
     * @return the result from the method
     * @throws RuntimeException when the execution failed
     */
    public Object evaluate(String className, String methodName, Object instance, Object... args) {
        // Now we can compile!
        compiler.getTask(null, forwardingJavaFileManager, null, null, null, sourceFiles).call();

        try{
            Class<?> loadedClass = new ClassLoader(){
                @Override
                public Class<?> findClass(String name){
                    byte[] bytes = classCache.get(name).toByteArray();
                    return defineClass(name, bytes, 0, bytes.length);
                }
            }.loadClass(className);

            List<Class<?>> methodArguments = new ArrayList<>();
            for (Object arg : args) {
                methodArguments.add(arg.getClass());
            }
            Method method = loadedClass.getMethod(methodName, methodArguments.toArray(new Class[0]));
            return method.invoke(
                    (instance == null ? loadedClass.getConstructor().newInstance()
                            : instance == "main" ? null : instance)
                    , args);
        }catch(ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException x){
            throw new RuntimeException("Run failed: " + x, x);
        }
    }

}
