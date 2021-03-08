import org.junit.Test;
import spoon.Launcher;
import spoon.javadoc.internal.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JavadocTest {
    @Test
    public void test1() {
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/CommentClass.java");
        spoon.buildModel();
        CtClass ctClass = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtClass.class)).get(0);
        System.out.println(ctClass.getPackage());

        CtMethod ctMethod = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class)).get(0);
        String comment = ctMethod.getDocComment();
        Javadoc javadoc = Javadoc.parse(comment);
        List<JavadocBlockTag> blockTagList = javadoc.getBlockTags();
        System.out.println("************ JavadocBlockTag ************");
        for(JavadocBlockTag j : blockTagList){
            System.out.println("--------");
            System.out.println(j);
        }

        JavadocDescription description = javadoc.getDescription();
        List<JavadocDescriptionElement> descriptionList = javadoc.getDescription().getElements();
        System.out.println("************ JavadocDescription ************");
        for(JavadocDescriptionElement j : description.getElements()){
            System.out.println("--------");
            System.out.println(j);
        }

        CtField field = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtField.class)).get(0);
        String comment2 = field.getDocComment();
        Javadoc jd2 = Javadoc.parse(comment2);
        JavadocDescription jdd2 = jd2.getDescription();
        System.out.println("************ JavadocDescription (Field) ************");
        for(JavadocDescriptionElement j : jdd2.getElements()){
            System.out.println("--------");
            System.out.println(j);
        }

    }

    @Test
    public void test2() {
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/CommentClass2.java");
        spoon.buildModel();
        CtClass ctClass = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtClass.class)).get(0);
        CtMethod ctMethod = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class)).get(0);
        String comment = ctMethod.getDocComment();
        Javadoc javadoc = Javadoc.parse(comment);

        List<JavadocBlockTag> blockTagList = javadoc.getBlockTags();
        System.out.println("************ JavadocBlockTag ************");
        for(JavadocBlockTag j : blockTagList){
            System.out.println("--------");
            System.out.println(j);
            //System.out.println(j.getContent().getElements());
            for(JavadocDescriptionElement i : j.getContent().getElements()){
                System.out.println("______________");
                System.out.println(i);
            }
        }

        JavadocDescription description = javadoc.getDescription();
        System.out.println("************ JavadocDescription ************");
        for(JavadocDescriptionElement j : description.getElements()){
            System.out.println("--------");
            System.out.println(j);
        }

        /*
        CtField field = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtField.class)).get(0);
        String comment2 = field.getDocComment();
        Javadoc jd2 = Javadoc.parse(comment2);
        JavadocDescription jdd2 = jd2.getDescription();
        System.out.println("************ JavadocDescription (Field) ************");
        for(JavadocDescriptionElement j : jdd2.getElements()){
            System.out.println("--------");
            System.out.println(j);
        }
         */

    }

    //blockTagList.stream().forEach(r -> result.addAll(r.getContent().getElements().stream().filter(e -> e instanceof JavadocInlineTag).collect(Collectors.toCollection())));

    @Test
    public void test3() {
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/CommentClass2.java");
        spoon.buildModel();
        CtMethod ctMethod = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class)).get(0);
        List<JavadocInlineTag> list = new ArrayList<>();


        String comment = ctMethod.getDocComment();
        Javadoc javadoc = Javadoc.parse(comment);
        for(JavadocBlockTag blockTag : javadoc.getBlockTags()){
            for(JavadocDescriptionElement element : blockTag.getContent().getElements()){
                if(element instanceof JavadocInlineTag) {
                    list.add((JavadocInlineTag) element);
                }
            }
        }


        for(JavadocInlineTag t : list){
            System.out.println(t);
        }

    }
}
