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

    /*
    @Test
    public void test1() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/TestFile.java");

        // Uncomment to attach required classpath, though the bug is reproducible as it is
       //Path classpathFile = Paths.get("files", "classpath.txt");
        //Collection<String> classpath = Files.readAllLines(classpathFile);
        //launcher.getEnvironment().setSourceClasspath(classpath.toArray(new String[0]));

        CtModel model = launcher.buildModel();
        List<CtInvocation> list = model.getRootPackage().filterChildren((CtInvocation i)->i.getExecutable().getSimpleName().equals("option")).list();
        CtExecutableReference<?> executable = list.get(0).getExecutable();

        AtomicReference<TypeFactory> affectedFactory = new AtomicReference<>();

        launcher.getEnvironment().setModelChangeListener(new EmptyModelChangeListener() {
            @Override
            public void onListAdd(CtElement currentElement, CtRole role, List field, CtElement newValue) {
                System.out.println("currentElement: "+currentElement);
                System.out.println("role: "+role);
                System.out.println("field: "+field);
                for(Object o : field)
                    System.out.println("field: " + o);
                System.out.println("newValue: "+newValue);
                TypeFactory factory = currentElement.getFactory().Type();
                if (currentElement != factory.OBJECT || role != CtRole.TYPE_ARGUMENT) {
                    return;
                }

                affectedFactory.set(factory);

                System.out.println("Type parameters before: " + affectedFactory.get().OBJECT.getActualTypeArguments().size());
            }
        });

        // This call corrupts the TypeFactory#OBJECT instance
        executable.getExecutableDeclaration();

        System.out.println("Type parameters after: " + affectedFactory.get().OBJECT.getActualTypeArguments().size());
    }
*/
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
        System.out.println("class of object: "+affectedFactory.get().OBJECT.getClass());
        System.out.println("name of class: "+affectedFactory.get().OBJECT.getSimpleName());


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
/*
    @Test
    public void test3() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/Test.java");
        //launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/TestFile.java");

        CtModel model = launcher.buildModel();
        //List<CtInvocation> list = model.getRootPackage().filterChildren((CtInvocation i)->i.getExecutable().getSimpleName().equals("super")).list();
        List<CtInvocation> list = model.getRootPackage().filterChildren((CtInvocation i)->true).list();

        System.out.println(list.size());
        System.out.println("all invocations");
        for(CtInvocation i : list){
            System.out.println("short rep: " + i.getShortRepresentation());
            System.out.println(i);
        }

        CtInvocation ctInvocation = list.get(1);
        System.out.println(ctInvocation);
        System.out.println(ctInvocation.getTarget());
        System.out.println(((CtInvocation)ctInvocation.getTarget()).getTarget().getClass());
        //System.out.println("no left: "+((CtInvocation)((CtInvocation)ctInvocation.getTarget()).getTarget()).getTarget());

        System.out.println("**************");
        CtInvocation ctInvocation2 = list.get(3);
        System.out.println(ctInvocation2);
        System.out.println(ctInvocation2.getTarget());
        System.out.println(ctInvocation2.getTarget().getClass());
        CtFieldRead f = (CtFieldRead) ctInvocation2.getTarget();


        System.out.println(ctInvocation.getExecutable().isConstructor());
        if(ctInvocation.getExecutable().isConstructor() && !ctInvocation.isImplicit())
            System.out.println("CtInvocation object is an explicit invocation of super");
        CtExecutableReference<?> executable = list.get(0).getExecutable();


        System.out.println("--------------- super");
        List<CtSuperAccess> superList = model.getRootPackage().filterChildren((CtSuperAccess i)->true).list();
        for(CtSuperAccess s : superList){
            System.out.println(s);
            System.out.println(s.isImplicit());
        }

        //List<CtInvocation> list = model.getRootPackage().filterChildren((CtInvocation i)->true).list();

    }*/

    @Test
    public void featurequestion() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/main/java/env/B.java");
        //launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/main/java/spoon/pack/");


        //launcher.getEnvironment().setSourceClasspath(new String[]{"/home/andrewb/Spoon/spoon5/spoon/src/main/java/env/B.java","/home/andrewb/Spoon/spoon5/spoon/src/main/java/spoon/pack/FakeLAuncher.java"});

        //launcher.getEnvironment().setSourceClasspath(new String[]{"/home/andrewb/Spoon/spoon5/spoon/target/classes/env/","/home/andrewb/Spoon/spoon5/spoon/target/classes/spoon/pack/"});

        launcher.getEnvironment().setNoClasspath(false);
        launcher.getEnvironment().setSourceClasspath(new String[]{"/home/andrewb/Spoon/spoon5/spoon/target/classes/"});

        //launcher.getEnvironment().setNoClasspath(false);
        //launcher.getEnvironment().setSourceClasspath(new String[]{"/home/andrewb/Desktop/spoon/class"});
        //launcher.getEnvironment().setInputClassLoader(new URLClassLoader(((StandardEnvironment)launcher.getEnvironment()).urlClasspath(),null));


        // /home/andrewb/Spoon/spoon5/spoon/target/classes/env/B.class
        // /home/andrewb/Spoon/spoon5/spoon/target/classes/spoon/pack/FakeLAuncher.class


        //launcher.getEnvironment().setSourceClasspath(new String[]{"/home/andrewb/Spoon/spoon5/spoon/src/main/java/env/"});


        CtModel model = launcher.buildModel();
        List<CtType> clazz= model.getElements(new TypeFilter(CtType.class));
        CtType t = clazz.get(0);
            System.out.println("****************");
            System.out.println(t);
            CtField f = t.getField("launcher");
            System.out.println(f);
            CtTypeReference r = f.getType();
            System.out.println(r);
            System.out.println(r.getDeclaration());
        System.out.println(r.getTypeDeclaration());


        System.out.println("******************");
        CtField f2 = t.getField("l");
        System.out.println(f2);
        CtTypeReference r2 = f2.getType();
        System.out.println(r2);
        System.out.println(r2.getDeclaration());
        System.out.println(r2.getTypeDeclaration());
        System.out.println(r.getTypeDeclaration().isShadow());


        System.out.println(clazz.size());
        /*
        Collection l = t.getValueByRole(CtRole.METHOD);
        System.out.println(l.iterator().next());

        System.out.println("---------ROLE-------------");
        RoleHandler rh = RoleHandlerHelper.getRoleHandler(t.getClass(),CtRole.METHOD);


        Collection m = rh.asCollection(t);
        System.out.println(m.iterator().next());

        System.out.println("---------ROLEs-------------");
        List<RoleHandler> rhlist = RoleHandlerHelper.getRoleHandlers(t.getClass());
        for(RoleHandler h : rhlist){
            System.out.println(h);
        }



        Map m = rh.asMap(t);
        for(Object o : m.entrySet()){
            System.out.println(o);
        }
        for(Object o : m.keySet()){
            System.out.println(o);
        }*/
    }

    @Test
    public void test4() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/Test.java");
        CtModel model = launcher.buildModel();
        List<CtInvocation> list = model.getRootPackage().filterChildren((CtInvocation i)->true).list();

        for(CtInvocation ctInvocation : list){
            System.out.println(ctInvocation);

            /*
            if(ctInvocation.getExecutable().isConstructor() && !ctInvocation.isImplicit())
                System.out.println("CtInvocation object is an explicit invocation of super");
            else {
                List<CtSuperAccess> superList = ctInvocation.filterChildren((CtSuperAccess i) -> true).list();
                if (!superList.isEmpty())
                    System.out.println("CtInvocation object is an explicit invocation of super");
            }



            if(ctInvocation.getExecutable().isConstructor() && !ctInvocation.isImplicit())
                System.out.println("CtInvocation object is an explicit invocation of super");
            List<CtSuperAccess> superList = ctInvocation.filterChildren((CtSuperAccess i) -> true).list();
            if (!superList.isEmpty())
                System.out.println("CtInvocation object is an explicit invocation of super");

*/
            if((ctInvocation.getExecutable().isConstructor() && !ctInvocation.isImplicit())
                    || !ctInvocation.getElements(new TypeFilter(CtSuperAccess.class)).isEmpty()) {
                System.out.println("CtInvocation object is an explicit invocation of super");
            }
        }




    }

    public CtInvocation getFirstTarget(CtInvocation i) {
        if(i.getTarget() != null && i.getTarget() instanceof CtInvocation)
            return getFirstTarget((CtInvocation) i.getTarget());
        else
            return i;
    }
/*
    public CtInvocation getFirstTarget(CtTargetedExpression i) {
        if(i.getTarget() != null && i.getTarget() instanceof CtInvocation)
            return getFirstTarget(i.getTarget());
        else
            return i;
    }
*/

    @Test
    public void ctclasscompilequestion() {
        /*File file = new File(destinationPath);
        file.getParentFile().mkdirs();
        FileUtils.writeByteArrayToFile(file, (imports + type.toString()).getBytes());*/


        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int success = compiler.run(null, null, null, "/home/andrewb/Desktop/source.java");
        if(success == 0)
            System.out.println("CtClass compiles");
        else
            System.out.println("CtClass does not compile");
    }

    @Test
    public void dependencyquestion() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/extra/A.java");
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/extra/B.java");
        launcher.addInputResource("/home/andrewb/Spoon/spoon5/spoon/src/test/java/extra/C.java");


        CtModel model = launcher.buildModel();
        //List<CtInvocation> list = model.getRootPackage().filterChildren((CtInvocation i)->i.getExecutable().getSimpleName().equals("super")).list();

        //List<CtClass> classList = model.getRootPackage().filterChildren((CtClass t)->t.getSimpleName().equals("A")).list();

        List<CtClass> classList = model.getRootPackage().filterChildren((CtClass t)->true).list();
        CtClass ctClass = classList.get(1);
        System.out.println(ctClass);

        List<CtTypeReference> list = ctClass.filterChildren((CtTypeReference t)->t.getSimpleName().equals("A")).list();

        for(CtTypeReference t : list){
            //System.out.println("*******");
            //System.out.println(t);
            //System.out.println(t.getParent());
        }

        if(!ctClass.filterChildren((CtTypeReference t)->t.getSimpleName().equals("C")).list().isEmpty())
            System.out.println("ctClass B depends on A");
        else
            System.out.println("no dependence");

        System.out.println("-------- NEXT ----------");

/*
        List<CtTypeReference> list2 = ctClass.filterChildren(
                (CtTypeReference t)->t.getDeclaration() != null && t.getDeclaration() instanceof CtClass).list();

        for(CtTypeReference r : list2){
                CtClass clazz = (CtClass) r.getDeclaration();
        }

*/
        ctClass = classList.get(2);

        /*
        System.out.println(ctClass);
        List<CtTypeReference> list2 = ctClass.filterChildren((CtTypeReference t)->t.getSimpleName().equals("A")).list();

        for(CtTypeReference t : list2){
            System.out.println("*******");
            System.out.println(t);
            System.out.println(t.getParent());
        }*/

        //System.out.println(rec(ctClass));

    }

    public boolean rec(CtClass ctClass) {
        //System.out.println("--------------"+ctClass.getSimpleName());
        boolean b;

        // get all type references in ctClass
        List<CtTypeReference> list = ctClass.filterChildren(
                (CtTypeReference t)->t.getDeclaration() != null && t.getDeclaration() instanceof CtClass).list();

        //System.out.println("list is empty: " + list.isEmpty());

        for(CtTypeReference r : list){
            // check if ctClass depends on A
            //System.out.println(r.getSimpleName());
            if(r.getSimpleName().equals("D"))
                return true;

            // check next class
            CtClass clazz = (CtClass) r.getDeclaration();
            if(rec(clazz))
                return true;
        }
        return false;
    }

}