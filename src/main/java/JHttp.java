import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JHttp {
    String RESP_OK = "HTTP/1.1 200 OK\n";
    String CONN_CLOSED = "Connection: Closed\n";
    String RESP_NOT_FOUND = "HTTP/1.1 404 Not Found\n";

    static Map<String, RespData> cacheRespData = new HashMap<String, RespData>();

    public String readfile(String filename) throws IOException {
        File f = new File("webroot/" + filename);

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
        File f = new File("webroot/" + filename);
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

        if (cacheRespData.containsKey(relativePath)) {
            return cacheRespData.get(relativePath);
        }

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

        if (respData != null && !cacheRespData.containsKey(relativePath)) {
            cacheRespData.put(relativePath, respData);
        }

        return respData;
    }

    public byte[] processRequest(String requst) {
        String[] sArr = requst.split(" ");
        String filename = sArr[1];

        RespData respData = processRequestByPath(filename);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();

        if (respData != null) {
            byte[] body = respData.getBody();
//            String body = "你好";
            sb.append(RESP_OK);
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

        return byteArrayOutputStream.toByteArray();
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
