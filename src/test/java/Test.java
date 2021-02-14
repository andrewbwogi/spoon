
//public class Test<B extends Test<C, C>, C extends Test<C, C>> {

public class Test<B extends Test<B, C>, C> {

        public void doSomething2() {
    }

}


/*
super access question
public class Test extends TestFile {

    Test(){
        super(1);
        super.s().s();
        super.tf.tf.s();
        m();
    }

    public void m(){

    }


}*/