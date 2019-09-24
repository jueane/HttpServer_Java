import java.io.*;

public class JHttp {
    String RESP_OK = "HTTP/1.1 200 OK\n";
    String CONN_CLOSED = "Connection: Closed\n";
    String RESP_NOT_FOUND = "HTTP/1.1 404 Not Found\n";

    public String readfile(String filename) {
        File f = new File("webroot/" + filename);

        if (!f.exists()) {
            return null;
        }
        int len = (int) f.length();
        char[] fileContent = new char[len];

        try {
            FileReader reader = new FileReader(f);
            int actLen = reader.read(fileContent);
            String result = new String(fileContent, 0, actLen);
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] readBinaryFIle(String filename){
        File f = new File("webroot/" + filename);
        if (!f.exists()) {
            return null;
        }
        int len = (int) f.length();
        byte[] fileContent = new byte[len];

        try {
            FileInputStream fileIS=new FileInputStream(f);
            fileIS.read(fileContent);
            return fileContent;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    RespData processRequest(String relativePath){
        if("/".equals(relativePath))
            relativePath="/index.html";
        RespData respData=new RespData();

        if(relativePath.endsWith("favicon.ico")){
            respData.respType="Content-Type: image/x-icon\n";
//            respData.respBody=readBinaryFIle(relativePath);
        }

        return  null;
    }


    public static void main(String[] args) {
        System.out.println("hi");

        TheSelector ts=new TheSelector();
        try {
            ts.begin();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        JHttp jh=new JHttp();
//        System.out.println(jh.readfile("Home.html"));
    }

    public void test() {
        System.out.println("hi");
    }
}

class RespData{
    int type;
    String respType;
    String respBody;
    byte[] respBody2;
}
