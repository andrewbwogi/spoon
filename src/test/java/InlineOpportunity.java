import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

public class InlineOpportunity {
    private CtMethod extractedMethodDeclaration;
    private CtInvocation invocation;

    void setExtractedMethodDeclaration(CtMethod e){
        this.extractedMethodDeclaration = e;
    }

    CtMethod getExtractedMethodDeclaration(){
        return extractedMethodDeclaration;
    }

    void setInvocation(CtInvocation e){
        this.invocation = e;
    }

    CtInvocation getInvocation(){
        return invocation;
    }
}
