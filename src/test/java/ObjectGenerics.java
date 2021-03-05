import org.apache.commons.io.FileUtils;
import org.apache.directory.fortress.realm.util.ChildFirstUrlClassLoader;
import org.junit.Test;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.StandardEnvironment;
import spoon.support.modelobs.EmptyModelChangeListener;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.visitor.java.internal.RuntimeBuilderContext;
import spoon.support.visitor.java.internal.TypeReferenceRuntimeBuilderContext;

import javax.net.ssl.KeyManager;
import javax.swing.Icon;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ObjectGenerics {



    public URL[] urlClasspath(String[] cl) {
        //CollationData_sq sq;
        Icon ic;
        KeyManager k;
        String[] classpath = cl;
        int length = (classpath == null) ? 0 : classpath.length;
        URL[] urls = new URL[length];
        for (int i = 0; i < length; i += 1) {
            try {
                urls[i] = new File(classpath[i]).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Invalid classpath: " + Arrays.toString(classpath), e);
            }
        }
        return urls;
    }

    @Test
    public void featurequestion() throws ClassNotFoundException, MalformedURLException {
        //String classpath = "/home/andrewb/Desktop/spoon/class/classes5/";
        //String classpath = "/home/andrewb/Spoon/spoon5/spoon/src/test/resources/";
        String classpath = "/home/andrewb/Spoon/spoon5/spoon/src/test/java/";


        Launcher launcher = new Launcher();
        //launcher.addInputResource("/home/andrewb/Desktop/spoon/class/B.java");
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/resources/LauncherUser.java");
        //launcher.addInputResource("/home/andrewb/Desktop/spoon/class/classes5/File.java");
        //launcher.addInputResource("/home/andrewb/Desktop/spoon/class/classes5/java/io/File.java");
        //launcher.addInputResource("/home/andrewb/Desktop/spoon/class/classes5/javax/swing/Icon.java");
        //launcher.addInputResource("/home/andrewb/Desktop/spoon/class/classes5/java/io/File.java");
        //launcher.addInputResource("/home/andrewb/Desktop/spoon/class/classes5/java/io/news/New.java");

        //launcher.addInputResource("/home/andrewb/Desktop/spoon/class/classes5/java/io/Test.java");



        launcher.getEnvironment().setNoClasspath(false);
        launcher.getEnvironment().setSourceClasspath(new String[]{classpath});


        //launcher.getEnvironment().setInputClassLoader(new URLClassLoader(((StandardEnvironment)launcher.getEnvironment()).urlClasspath(),null));
        //launcher.getEnvironment().setInputClassLoader(new URLClassLoader(((StandardEnvironment)launcher.getEnvironment()).urlClasspath(),null));
        //launcher.getEnvironment().setInputClassLoader(new URLClassLoader(((StandardEnvironment)launcher.getEnvironment()).urlClasspath(),"".getClass().getClassLoader()));
        //launcher.getEnvironment().setInputClassLoader(new Child(((StandardEnvironment)launcher.getEnvironment()).urlClasspath(),null));
        //launcher.getEnvironment().setInputClassLoader(new Child(((StandardEnvironment)launcher.getEnvironment()).urlClasspath(),"".getClass().getClassLoader()));
        //launcher.getEnvironment().setInputClassLoader(new ParentLastURLClassLoader.ChildURLClassLoader(((StandardEnvironment)launcher.getEnvironment()).urlClasspath(),new ParentLastURLClassLoader.FindClassClassLoader("".getClass().getClassLoader())));



        //URLClassLoader urlcl = new ParentLastURLClassLoader.ChildURLClassLoader(urls,null);
        //Child child = new Child(urls, Thread.currentThread().getContextClassLoader());

        //launcher.getEnvironment().setInputClassLoader(urlcl);
        //launcher.getEnvironment().setInputClassLoader(new URLClassLoader(urls,null));
        //launcher.getEnvironment().setInputClassLoader(child);

        File addOnFolder = new File(classpath);
        URL url = addOnFolder.toURL();
        URL[] urls = new URL[]{url};
        List<URL> urlList = new ArrayList<>();
        urlList.add(url);

        ChildFirstUrlClassLoader childFirstUrlClassLoader = new ChildFirstUrlClassLoader(urls,Thread.currentThread().getContextClassLoader());
        URLClassLoader standardClassloader = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
        URLClassLoader standardClassloaderNullParent = new URLClassLoader(urls,null);

        ParentNonURLClassLoader parentNonURLClassLoader = new ParentNonURLClassLoader(urlList);

        ClassLoader parentLastURLClassLoader = new ParentLastURLClassLoader(urlList);
        launcher.getEnvironment().setInputClassLoader(standardClassloaderNullParent);




        CtModel model = launcher.buildModel();
        List<CtType> clazz= model.getElements(new TypeFilter(CtType.class));
        CtType t = clazz.get(0);
        System.out.println("printing t: " + t);
        CtField f2 = t.getField("l");
        CtTypeReference r2 = f2.getType();
        System.out.println(r2.getTypeDeclaration());

        System.out.println("-------------CUSTOM CLASSLOADER------------");
        //Class utils = childUrlcl.loadClass("Utils",true);
        //Class utils = urlcl.loadClass("Utils");
        //Class utils = child.loadClass("Utils");
        //Class utils = parentLastURLClassLoader.loadClass("Utils");
        Class utils = childFirstUrlClassLoader.loadClass("Icon");
        Launcher newLauncher = new Launcher();
        ClassFactory cf = newLauncher.getFactory().Class();
        CtClass classFromObject = cf.get(utils);
        System.out.println(classFromObject);
    }

// Thread.currentThread().getContextClassLoader()

    private static class ParentLastURLClassLoader extends ClassLoader
    {
        private ChildURLClassLoader childClassLoader;

        /**
         * This class allows me to call findClass on a classloader
         */
        private static class FindClassClassLoader extends ClassLoader
        {
            public FindClassClassLoader(ClassLoader parent)
            {
                super(parent);
            }

            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException
            {
                return super.findClass(name);
            }
        }

        /**
         * This class delegates (child then parent) for the findClass method for a URLClassLoader.
         * We need this because findClass is protected in URLClassLoader
         */
        private static class ChildURLClassLoader extends URLClassLoader
        {
            private FindClassClassLoader realParent;

            public ChildURLClassLoader( URL[] urls, FindClassClassLoader realParent )
            {
                super(urls, null);

                this.realParent = realParent;
            }

            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException
            {
                Class<?> loaded = super.findLoadedClass(name);
                if( loaded != null )
                    return loaded;
                try
                {
                    // first try to use the URLClassLoader findClass
                    return super.findClass(name);
                }
                catch( ClassNotFoundException e )
                {
                    // if that fails, we ask our real parent classloader to load the class (we give up)
                    return realParent.loadClass(name);
                }
            }
        }

        public ParentLastURLClassLoader(List<URL> classpath)
        {
            super(Thread.currentThread().getContextClassLoader());
            //super(null);

            URL[] urls = classpath.toArray(new URL[classpath.size()]);

            childClassLoader = new ChildURLClassLoader( urls, new FindClassClassLoader(this.getParent()) );
        }

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
        {
            try
            {
                // first we try to find a class inside the child classloader
                return childClassLoader.findClass(name);
            }
            catch( ClassNotFoundException e )
            {
                // didn't find it, try the parent
                return super.loadClass(name, resolve);
            }
        }
    }


    private static class ParentNonURLClassLoader extends ClassLoader
    {
        private ChildURLClassLoader childClassLoader;

        /**
         * This class allows me to call findClass on a classloader
         */
        private static class FindClassClassLoader extends ClassLoader
        {
            public FindClassClassLoader(ClassLoader parent)
            {
                super(parent);
            }

            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException
            {
                return super.findClass(name);
            }
        }

        /**
         * This class delegates (child then parent) for the findClass method for a URLClassLoader.
         * We need this because findClass is protected in URLClassLoader
         */
        private static class ChildURLClassLoader extends URLClassLoader
        {
            private FindClassClassLoader realParent;

            public ChildURLClassLoader( URL[] urls, FindClassClassLoader realParent )
            {
                super(urls, null);

                this.realParent = realParent;
            }

            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException
            {
                Class<?> loaded = super.findLoadedClass(name);
                if( loaded != null )
                    return loaded;

                    // first try to use the URLClassLoader findClass
                    return super.findClass(name);

            }
        }

        public ParentNonURLClassLoader(List<URL> classpath)
        {
            //super(Thread.currentThread().getContextClassLoader());
            super(null);

            URL[] urls = classpath.toArray(new URL[classpath.size()]);

            childClassLoader = new ChildURLClassLoader( urls, new FindClassClassLoader(this.getParent()) );
        }

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
        {

                // first we try to find a class inside the child classloader
                return childClassLoader.findClass(name);

        }
    }

}