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

//    static List<ChannelHandler> chList = new ArrayList<ChannelHandler>();

    Selector selector;
    SelectionKey selectionKey;
    SocketChannel channel;

    ByteBuffer inputBuffer = ByteBuffer.allocate(1024 * 1024 * 20);
    ByteBuffer outputBuffer = ByteBuffer.allocate(1024 * 1024 * 20);

//    public static ChannelHandler find(long hash) {
//        for (int i = 0; i < chList.size(); i++) {
//            ChannelHandler ch = chList.get(i);
//            if (ch.channel.hashCode() == hash) {
//                return ch;
//            }
//        }
//        return null;
//    }

    public void init(SelectionKey skey) {
        selectionKey = skey;
        channel = (SocketChannel) selectionKey.channel();
        selector = skey.selector();

//        chList.add(this);
    }

//    public ChannelHandler(SelectionKey skey) {
//        selectionKey = skey;
//        channel = (SocketChannel) selectionKey.channel();
//        num = ++globalnum;
//        selector = skey.selector();
//
//        chList.add(this);
//    }

    public void onConnected() {
        System.out.println("New connection " + hashCode());
    }

    public void onDataArrived() {
        inputBuffer.clear();

        int len = 0;
        try {
            len = channel.read(inputBuffer);
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
            inputBuffer.flip();
            String s = new String(inputBuffer.array(), 0, len);
            System.out.println("------read-----------------------------------");

            // 传给httpHandler处理
            JHttp jHttp = new JHttp();
            byte[] respByts = jHttp.processRequest(s);

            outputBuffer.clear();
            outputBuffer.put(respByts);
            requestWrite();
        }
    }


    public void onReadyWrite() {
        System.out.println("-------write-------------------------------");
        outputBuffer.flip();
        try {
            channel.write(outputBuffer);
            channel.close();
//            chList.remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        requestRead();
    }

    void requestRead() {
        selectionKey.interestOps(SelectionKey.OP_READ);
    }

    void requestWrite() {
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    public void onConnectionClosed() {

    }

    public void onExceptionOccurred() {

    }

}
