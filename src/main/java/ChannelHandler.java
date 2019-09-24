import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class ChannelHandler {

    static List<ChannelHandler> chList=new ArrayList<ChannelHandler>();

    Selector selector;
    SelectionKey selectionKey;
    SocketChannel channel;

    ByteBuffer inputBuffer = ByteBuffer.allocate(1024 * 10);
    ByteBuffer outputBuffer = ByteBuffer.allocate(1024 * 10);

    static int globalnum = 0;

    int num;

    public static ChannelHandler find(long hash){
        for(int i=0;i<chList.size();i++){
            ChannelHandler ch = chList.get(i);
            if(ch.channel.hashCode()==hash){
                return ch;
            }
        }
        return null;
    }


    public ChannelHandler(SelectionKey skey) {
        selectionKey = skey;
        channel = (SocketChannel) selectionKey.channel();
        num = ++globalnum;
        selector = skey.selector();

        chList.add(this);
    }

    public void onConnected() {
        System.out.println("new " + hashCode());
    }

    public void onDataArrived() {

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int len = 0;
        try {
            len = channel.read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (len == -1) {
            try {
                channel.close();
                onConnectionClosed();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (len == 0) {

        } else {

            byteBuffer.flip();
            String s = new String(byteBuffer.array(), 0, len);
            System.out.println(selectionKey.hashCode() + " recv: " + s);
            System.out.println("-----------------------------------------");

            // 传给httpHandler处理
            JHttp jHttp = new JHttp();
            String ret = jHttp.processRequest(s);
//            CharBuffer allocate1 = CharBuffer.allocate(ret.length());
            System.out.println("ret len:" + ret.length());
//            System.out.println(ret);


            byte[] bytes = null;
            try {
                bytes = s.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            outputBuffer.clear();
            outputBuffer.put(bytes);
            requestWrite();
//            bytes = s.getBytes();
//            ByteBuffer allocate = ByteBuffer.allocate(bytes.length);
//            allocate.put(bytes);
//            allocate.flip();
//            try {
//                channel.write(allocate);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }



    public void onReadyWrite() {
        System.out.println("------------------------------write--------");
        outputBuffer.flip();



        String body = "hi";
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\r\n");
//        sb.append(CONN_CLOSED);
//        sb.append("Content-Type: text/plain\r\n");
//        sb.append("Content-Length: " + body.length() + "\r\n\r\n");
        sb.append("Content-Type: text/plain\r\n");
        sb.append("Content-Length: " + body.length() + "\r\n\r\n");
        sb.append(body);


        ByteBuffer bb=ByteBuffer.allocate(100);
        bb.put(sb.toString().getBytes());

        bb.flip();
//        bb.rewind();

        try {
            channel.write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestRead();
    }

    void requestRead() {
        selectionKey.interestOps(SelectionKey.OP_READ);
//        try {
//            channel.register(selector, SelectionKey.OP_READ);
//        } catch (ClosedChannelException e) {
//            e.printStackTrace();
//        }
    }


    void requestWrite() {

        selectionKey.interestOps(SelectionKey.OP_WRITE);
//        try {
//            channel.register(selector, SelectionKey.OP_WRITE);
//        } catch (ClosedChannelException e) {
//            e.printStackTrace();
//        }
    }

    public void onConnectionClosed() {

    }

    public void onExceptionOccurred() {

    }

}
