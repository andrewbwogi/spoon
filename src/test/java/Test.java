
//public class Test<B extends Test<C, C>, C extends Test<C, C>> {
//public class Test {
//public class Test<B extends Test<B, C>, C> {

public class Test<B extends C, C extends Test<C,C>> {


//public class Test<B extends Test<B, C>, C> {

    B field;

        public void doSomething2() {
    }

    public <D extends Test<D, D>> D callFriend(String name, Class<B> type) {
        return null;
    }

    public <B> B callFriend2(String name, Class<B> type) {
        return null;
    }


}


