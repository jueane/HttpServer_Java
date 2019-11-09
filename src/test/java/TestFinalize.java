import org.junit.Test;

public class TestFinalize {

    public static TestFz testFz ;

    public static void main(String[] args) throws InterruptedException {

        testFz= new TestFz();

        testFz.isAlive();

        testFz=null;
        System.gc();

        check();

        System.out.println("wait...");
        Thread.sleep(3000);


        check();

        testFz=null;
        System.gc();
        check();
        System.out.println("wait...");
        Thread.sleep(3000);


        check();

    }

    static void check() throws InterruptedException {
        if(testFz!=null){
            System.out.println("tfz is alive");

        }else{
            System.out.println("tfz is dead");
        }
    }


}


class TestFz{

    public static TestFinalize  mainobj;

    void isAlive(){
        System.out.println("i am alive");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("execute finalize");
        TestFinalize.testFz=this;
    }
}