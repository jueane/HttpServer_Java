import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JHttp {

    String RESP_OK = "HTTP/1.1 200 OK\n";
    String CONN_CLOSED = "Connection: Closed\n";
    String RESP_NOT_FOUND = "HTTP/1.1 404 Not Found\n";

    //应答标记，用以区分最终服务端
    String RESP_IDENTIFIER = "Resp from: jm server\n";

    String rootpath;

    static Map<String, byte[]> cacheRespData = new HashMap<String, byte[]>();

    public JHttp() {
        String os = System.getenv("OS");
        System.out.println("OS is " + os);
        if ("Windows_NT".equals(os)) {
            rootpath = "webroot/";
            File f=new File(rootpath);
            if(!f.exists()){
                rootpath = "../mysite/webroot/";
            }
        } else {
            rootpath = "webroot/";
//            File f=new File(rootpath);
//            if(!f.exists()){
//                rootpath = "webroot/";
//            }
        }
    }

    public String readfile(String filename) throws IOException {
        File f = new File(rootpath + filename);

        if (!f.exists()) {
            System.out.println("file not found:" + f.getAbsolutePath());
            throw new IOException();
        }
        int len = (int) f.length();
        char[] fileContent = new char[len];

        FileReader reader = new FileReader(f);
        int actLen = reader.read(fileContent);
        String result = new String(fileContent, 0, actLen);
        return result;
    }

    public byte[] readBinaryFIle(String filename) throws IOException {
        File f = null;
        f = new File(rootpath + filename);

        if (!f.exists()) {
            System.out.println("file not found:" + f.getAbsolutePath());
            throw new IOException();
        }
        int len = (int) f.length();
        byte[] fileContent = new byte[len];

        FileInputStream fileIS = new FileInputStream(f);
        fileIS.read(fileContent);
        return fileContent;
    }

    RespData processRequestByPath(String relativePath) {
        RespData respData = new RespData();

        if ("/".equals(relativePath))
            relativePath = "/index.html";

        try {
            if (relativePath.endsWith(".html")) {
                respData.respType = "Content-Type: text/html\n";
                respData.setTextBody(readfile(relativePath));
            } else if (relativePath.endsWith(".css")) {
                respData.respType = "Content-Type: text/css\n";
                respData.setTextBody(readfile(relativePath));
            } else if (relativePath.endsWith(".txt")) {
                respData.respType = "Content-Type: text/plain\n";
                respData.setTextBody(readfile(relativePath));
            } else if (relativePath.endsWith(".png")) {
                respData.respType = "Content-Type: image/png\n";
                respData.setBinaryBody(readBinaryFIle(relativePath));
            } else if ("/favicon.ico".equals(relativePath)) {
                respData.respType = "Content-Type: image/x-icon\n";
                respData.setBinaryBody(readBinaryFIle(relativePath));
            } else {
                System.out.println("请求路径错误");
                return null;
            }
        } catch (IOException ex) {
            System.out.println("IO异常");
            return null;
        }

        return respData;
    }

    public byte[] processRequest(String requst, ChannelHandler ch) {
        String[] sArr = requst.split(" ");
        String reqURL = null;
        if (sArr != null && sArr.length > 1) {
            reqURL = sArr[1];
        } else {
            reqURL = "/";
        }

        // 日志_请求
        MsgQueue.current().publish("req " + reqURL + " uid:" + ch.uid);

        // 从缓存中查找
        if (cacheRespData.containsKey(reqURL)) {
            return cacheRespData.get(reqURL);
        }

        RespData respData = processRequestByPath(reqURL);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();

        if (respData != null) {
            byte[] body = respData.getBody();
            sb.append(RESP_OK);
            sb.append(RESP_IDENTIFIER);
            sb.append(respData.respType);

            if (body != null) {
                sb.append("Content-Length: " + body.length + "\n\n");
            } else {
                sb.append("Content-Length: " + 0 + "\n\n");
            }
            try {
                byteArrayOutputStream.write(sb.toString().getBytes());
                if (body != null) {
                    byteArrayOutputStream.write(body);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            sb.append(RESP_NOT_FOUND);
            sb.append("Content-Length: " + 0 + "\n\n");
            System.out.println("resp 404");
            try {
                byteArrayOutputStream.write(sb.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] respBytes = byteArrayOutputStream.toByteArray();

        // 加入缓存
        if (respData != null && !cacheRespData.containsKey(reqURL)) {
            cacheRespData.put(reqURL, respBytes);
        }

        return respBytes;
    }

}

class RespData {
    private int type;
    String respType;
    private String respBody;
    private byte[] respBody2;

    public void setTextBody(String body) {
        respBody = body;
        type = 1;
    }

    public void setBinaryBody(byte[] body) {
        type = 2;
        respBody2 = body;
    }

    public String getType() {
        return respType;
    }

    public byte[] getBody() {
        if (type == 1) {
//            System.out.println("body:"+respBody);
            return respBody.getBytes();
        } else if (type == 2) {
            return respBody2;
        }
        return null;
    }
}
