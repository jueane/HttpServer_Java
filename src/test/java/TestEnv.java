import org.junit.Test;

import java.util.Map;

public class TestEnv {

    public void test1(){
        Map<String, String> getenv = System.getenv();;
        String os1 = System.getenv("OS");
        System.out.println("os1 "+os1);


        String os = getenv.get("OS");
        System.out.println("os:"+os);

        for (String s : getenv.keySet()) {
            System.out.println(s);

        }



    }


}
