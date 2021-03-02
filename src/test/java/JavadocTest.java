import org.junit.Test;
import spoon.Launcher;
import spoon.javadoc.internal.Javadoc;
import spoon.javadoc.internal.JavadocBlockTag;
import spoon.javadoc.internal.JavadocDescription;
import spoon.javadoc.internal.JavadocDescriptionElement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

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
}
