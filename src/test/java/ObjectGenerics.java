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
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ObjectGenerics {

    @Test
    public void test2() {
        // todo how is the factory created?
        Launcher launcher = new Launcher();
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/TestFile.java");
        //launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/Test.java");


        // Uncomment to attach required classpath, though the bug is reproducible as it is
       /* Path classpathFile = Paths.get("files", "classpath.txt");
        Collection<String> classpath = Files.readAllLines(classpathFile);
        launcher.getEnvironment().setSourceClasspath(classpath.toArray(new String[0]));*/

        launcher.getEnvironment().setNoClasspath(false);
        CtModel model = launcher.buildModel();
        List<CtInvocation> list = model.getRootPackage().filterChildren((CtInvocation i)->i.getExecutable().getSimpleName().equals("doSomething2")).list();
        CtExecutableReference<?> executable = list.get(0).getExecutable();


        AtomicReference<TypeFactory> affectedFactory = new AtomicReference<>();

        // todo who is currentElement?
        // todo who is factory?
        launcher.getEnvironment().setModelChangeListener(new EmptyModelChangeListener() {
            @Override
            public void onListAdd(CtElement currentElement, CtRole role, List field, CtElement newValue) {
                //System.out.println("********************");
                TypeFactory factory = currentElement.getFactory().Type();
                if (currentElement != factory.OBJECT || role != CtRole.TYPE_ARGUMENT) {
                    return;
                }

                //System.out.println(currentElement);
                //System.out.println(currentElement.getClass());

                affectedFactory.set(factory);

                //System.out.println("object identity: " + (currentElement == affectedFactory.get().OBJECT));
                System.out.println("Type parameters before: " + affectedFactory.get().OBJECT.getActualTypeArguments().size());
            }
        });


        // This call corrupts the TypeFactory#OBJECT instance
        CtExecutable e = executable.getExecutableDeclaration();
        /*CtClass cl = (CtClass) e.getParent(new TypeFilter(CtClass.class));
        System.out.println(cl);
        System.out.println("**************************************************************");
        for(Object elem : cl.getElements(new TypeFilter(CtElement.class))){
            System.out.println("----" + ((CtElement)elem).getClass() + "----");
            System.out.println(((CtElement)elem).toString());
        }*/

//        System.out.println("class of object: "+affectedFactory.get().OBJECT.getClass());
//        System.out.println("name of class: "+affectedFactory.get().OBJECT.getSimpleName());


        List<CtTypeReference> list2 = model.getRootPackage().filterChildren((CtTypeReference i)->i.getSimpleName().equals("Test")).list();
        System.out.println("ooooooooooooooooooooooooooooooooo");
        System.out.println(list2.size());
        System.out.println(list2.get(0));
        CtTypeReference test = list2.get(0);
        System.out.println(test.getTypeDeclaration());
        System.out.println(executable);
        System.out.println(executable.getDeclaration());
        System.out.println(executable.getDeclaringType().getTypeDeclaration());
        System.out.println(executable.getDeclaration());
        System.out.println(e);


        //System.out.println("custom: "+executable.getFactory().Type().OBJECT.getActualTypeArguments().size());

        //System.out.println("shadow type: " + ((CtClass)e.getParent()));
        //System.out.println("factory identity: " + (((CtClass)e.getParent()).getFactory().Type() == e.getFactory().Type()));
        //System.out.println("object identity: " + (e.getFactory().Type().OBJECT == affectedFactory.get().OBJECT));

        System.out.println("Type parameters after: " + affectedFactory.get().OBJECT.getActualTypeArguments().size());

        Deque<RuntimeBuilderContext> contexts = new ArrayDeque<>();
        final CtTypeParameterReference typeParameterReference = launcher.getFactory().Core().createTypeParameterReference();
        RuntimeBuilderContext runtimeBuilderContext = new TypeReferenceRuntimeBuilderContext(new Type() {
            @Override
            public String getTypeName() {
                return null;
            }
        }, typeParameterReference);
        contexts.push(runtimeBuilderContext);

        final CtTypeParameterReference typeParameterReference2 = launcher.getFactory().Core().createTypeParameterReference();
        RuntimeBuilderContext runtimeBuilderContext2 = new TypeReferenceRuntimeBuilderContext(new Type() {
            @Override
            public String getTypeName() {
                return null;
            }
        }, typeParameterReference2);

        System.out.println("IS TRUE?: " + contexts.contains(runtimeBuilderContext2));

    }

}