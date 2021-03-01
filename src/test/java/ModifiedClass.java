public class ModifiedClass {
    void targetMethodDeclaration1() {
        int e = 5;
        boolean test = extractedMethodDeclaration3();
        int res;
        extractedMethodDeclaration1();
    }

    int targetMethodDeclaration2() {
        int e = 5;
        int res = extractedMethodDeclaration2(e);
        return res;
    }

    void extractedMethodDeclaration1() {
        int a = 0;
        ++a;
    }

    int extractedMethodDeclaration2(int b) {
        int a = 0;
        ++a;
        return a;
    }

    boolean extractedMethodDeclaration3() {
        int b = 0;
        ++b;
        return true;
    }
}
