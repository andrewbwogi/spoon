import org.apache.commons.io.FileUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.declaration.*;
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
    public void featurequestion() throws ClassNotFoundException {
        Launcher launcher = new Launcher();
        launcher.addInputResource("/home/andrewb/Desktop/spoon/class/B.java");
        launcher.getEnvironment().setNoClasspath(false);
        launcher.getEnvironment().setSourceClasspath(new String[]{"/home/andrewb/Desktop/spoon/class/classes5/"});
        //launcher.getEnvironment().setInputClassLoader(new URLClassLoader(((StandardEnvironment)launcher.getEnvironment()).urlClasspath(),null));



        CtModel model = launcher.buildModel();
        List<CtType> clazz= model.getElements(new TypeFilter(CtType.class));
        CtType t = clazz.get(0);
        CtField f2 = t.getField("l");
        CtTypeReference r2 = f2.getType();
        System.out.println(r2.getTypeDeclaration());
    }


}