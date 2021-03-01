import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InliningTest {

    @Test
    public void done() {
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/ModifiedClass.java");
        spoon.buildModel();
        CtClass ctClass = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtClass.class)).get(0);

        InlineOpportunity opportunity1 = new InlineOpportunity();
        CtInvocation invocation = (CtInvocation) ctClass.filterChildren((CtInvocation i)->i.getExecutable().getSimpleName().equals("extractedMethodDeclaration2")).list().get(0);
        CtMethod method = (CtMethod) ctClass.filterChildren((CtMethod m)->m.getSimpleName().equals("extractedMethodDeclaration2")).list().get(0);
        opportunity1.setInvocation(invocation);
        opportunity1.setExtractedMethodDeclaration(method);

        InlineOpportunity opportunity2 = new InlineOpportunity();
        CtInvocation invocation2 = (CtInvocation) ctClass.filterChildren((CtInvocation i)->i.getExecutable().getSimpleName().equals("extractedMethodDeclaration3")).list().get(0);
        CtMethod method2 = (CtMethod) ctClass.filterChildren((CtMethod m)->m.getSimpleName().equals("extractedMethodDeclaration3")).list().get(0);
        opportunity2.setInvocation(invocation2);
        opportunity2.setExtractedMethodDeclaration(method2);

        List<InlineOpportunity> opportunities = new ArrayList<>();
        opportunities.add(opportunity1);
        opportunities.add(opportunity2);


        // write method properly
        // show pretty printing
        // explain multiple print outs
        // add code for assignment
        for (InlineOpportunity opp: opportunities) {
            if(opp.getInvocation().getParent() instanceof CtRHSReceiver) {

                // stash original element
                CtInvocation clonedInvocation = opp.getInvocation().clone();

                // clone replacing elements
                List<CtStatement> statements = opp.getExtractedMethodDeclaration().getBody().getStatements();
                List<CtStatement> clonedStatements = new ArrayList<>();
                for (CtStatement s : statements) {
                    clonedStatements.add(s.clone());
                }

                // make the inlining
                int size = clonedStatements.size();
                for(int i = 0; i<size-1;i++){
                    ((CtStatement)opp.getInvocation().getParent()).insertBefore(clonedStatements.get(i));
                }
                CtExpression returnExpression = ((CtReturn)clonedStatements.get(size-1)).getReturnedExpression();
                opp.getInvocation().replace(returnExpression);

                // print the resulting class
                System.out.println(ctClass);

                // reset model
                returnExpression.replace(clonedInvocation);
                for (CtStatement s : clonedStatements) {
                    s.delete();
                }
            }
        }

        // test multiple elements in opportunities
        // mention that ctClass can be cloned for each unique java file
    }


    @Test
    public void testGetParentAfterGetParameterReference() {
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/ModifiedClass.java");
        spoon.buildModel();
        CtClass ctClass = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtClass.class)).get(0);

        InlineOpportunity opportunity1 = new InlineOpportunity();
        CtInvocation invocation = (CtInvocation) ctClass.filterChildren((CtInvocation i)->i.getExecutable().getSimpleName().equals("extractedMethodDeclaration2")).list().get(0);
        CtMethod method = (CtMethod) ctClass.filterChildren((CtMethod m)->m.getSimpleName().equals("extractedMethodDeclaration2")).list().get(0);
        opportunity1.setInvocation(invocation);
        opportunity1.setExtractedMethodDeclaration(method);

        List<InlineOpportunity> opportunities = new ArrayList<>();
        opportunities.add(opportunity1);

        // write method properly (void)
        // show pretty printing
        /*for (InlineOpportunity opp: opportunities) {
            opp.getInvocation().replace(opp.getExtractedMethodDeclaration().getBody().getStatements());
            System.out.println(ctClass);
        }*/

        // explain multiple print outs
        /*for (InlineOpportunity opp: opportunities) {
            CtInvocation invocationClone = opp.getInvocation().clone();
            List<CtStatement> statements = opp.getExtractedMethodDeclaration().getBody().getStatements();
            List<CtStatement> statementsClone = new ArrayList<>();
            for(CtStatement s : statements){
                statementsClone.add(s.clone());
            }
            opp.getInvocation().replace(statementsClone);
            System.out.println(ctClass);

            // reset model
            statementsClone.get(0).insertBefore(invocationClone);
            for(CtStatement s : statementsClone){
                s.delete();
            }
        }*/

        // add code for assignment (non void)
        for (InlineOpportunity opp: opportunities) {
            //System.out.println(opp.getInvocation().getParent().getClass());
            if(opp.getInvocation().getParent() instanceof CtRHSReceiver) {
                CtInvocation invocationClone = opp.getInvocation().clone();
                List<CtStatement> statements = opp.getExtractedMethodDeclaration().getBody().getStatements();
                List<CtStatement> statementsClone = new ArrayList<>();
                for (CtStatement s : statements) {
                    statementsClone.add(s.clone());
                }
                int size = statementsClone.size();
                for(int i = 0; i<size-1;i++){
                    ((CtStatement)opp.getInvocation().getParent()).insertBefore(statementsClone.get(i));
                }
                CtExpression returnExpression = ((CtReturn)statementsClone.get(size-1)).getReturnedExpression();
                opp.getInvocation().replace(returnExpression);
                System.out.println(ctClass);

                // reset model
                returnExpression.replace(invocationClone);
                for (CtStatement s : statementsClone) {
                    s.delete();
                }
                System.out.println(ctClass);
            }
        }

        // DONE print file?
        // inline with void/non-void method?
            // combine?
        // clone files?
            // clone source?


        // test multiple elements in opportunities
        // mention that ctClass can be cloned for each unique java file
    }

    @Test
    public void combination() {
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/ModifiedClass.java");
        spoon.buildModel();
        CtClass ctClass = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtClass.class)).get(0);

        InlineOpportunity opportunity1 = new InlineOpportunity();
        CtInvocation invocation = (CtInvocation) ctClass.filterChildren((CtInvocation i)->i.getExecutable().getSimpleName().equals("extractedMethodDeclaration2")).list().get(0);
        CtMethod method = (CtMethod) ctClass.filterChildren((CtMethod m)->m.getSimpleName().equals("extractedMethodDeclaration2")).list().get(0);
        opportunity1.setInvocation(invocation);
        opportunity1.setExtractedMethodDeclaration(method);

        List<InlineOpportunity> opportunities = new ArrayList<>();
        opportunities.add(opportunity1);

        // write method properly (void)
        // show pretty printing
        /*for (InlineOpportunity opp: opportunities) {
            opp.getInvocation().replace(opp.getExtractedMethodDeclaration().getBody().getStatements());
            System.out.println(ctClass);
        }*/

        // explain multiple print outs
        for (InlineOpportunity opp: opportunities) {
            CtInvocation invocationClone = opp.getInvocation().clone();
            List<CtStatement> statements = opp.getExtractedMethodDeclaration().getBody().getStatements();
            List<CtStatement> statementsClone = new ArrayList<>();
            for(CtStatement s : statements){
                statementsClone.add(s.clone());
            }
            opp.getInvocation().replace(statementsClone);
            System.out.println(ctClass);

            // reset model
            statementsClone.get(0).insertBefore(invocationClone);
            for(CtStatement s : statementsClone){
                s.delete();
            }
        }

        // add code for assignment (non void)
        for (InlineOpportunity opp: opportunities) {
            //System.out.println(opp.getInvocation().getParent().getClass());
            CtInvocation invocationClone = opp.getInvocation().clone();
            List<CtStatement> statements = opp.getExtractedMethodDeclaration().getBody().getStatements();
            List<CtStatement> statementsClone = new ArrayList<>();
            for (CtStatement s : statements) {
                statementsClone.add(s.clone());
            }
            CtExpression replacement;
            CtExpression returnExpression = null;
            if(opp.getInvocation().getParent() instanceof CtRHSReceiver) {

                int size = statementsClone.size();
                for(int i = 0; i<size-1;i++){
                    ((CtStatement)opp.getInvocation().getParent()).insertBefore(statementsClone.get(i));
                }
                returnExpression = ((CtReturn)statementsClone.get(size-1)).getReturnedExpression();
                opp.getInvocation().replace(returnExpression);
            } else {
                replacement = opp.getInvocation();
            }

            //replacement.replace(invocationClone);


            System.out.println(ctClass);

            // reset model
            if(opp.getInvocation().getParent() instanceof CtRHSReceiver) {
                returnExpression.replace(invocationClone);
            } else {
                statementsClone.get(0).insertBefore(invocationClone);
            }
            for (CtStatement s : statementsClone) {
                s.delete();
            }
        }

        // DONE print file?
        // inline with void/non-void method?
        // combine?
        // clone files?
        // clone source?


        // test multiple elements in opportunities
        // mention that ctClass can be cloned for each unique java file
    }
}
