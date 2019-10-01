import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ChannelHandler {
    static JHttp jHttp=new JHttp();

    String uid;

    Selector selector;
    SelectionKey selectionKey;
    SocketChannel channel;

    static ByteBuffer inputBuffer = ByteBuffer.allocate(1024 * 4);
    static ByteBuffer outputBuffer = ByteBuffer.allocate(1024 * 200);

    public void init(SelectionKey skey) {
        selectionKey = skey;
        channel = (SocketChannel) selectionKey.channel();
        selector = skey.selector();

        uid =System.currentTimeMillis() +"_"+ hashCode();;
        // 日志_连接
        MsgQueue.current().publish("connect:"+ uid);
    }

    public void onConnected() {
//        System.out.println("New connection " + hashCode());
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
//            System.out.println("------read-----------------------------------");

            // 传给httpHandler处理
            byte[] respByts = jHttp.processRequest(s,this);

            outputBuffer.clear();
            outputBuffer.put(respByts);
            requestWrite();
        }
    }

    public void onReadyWrite() {
//        System.out.println("-------write-------------------------------");
        outputBuffer.flip();
        try {
            channel.write(outputBuffer);

            // 日志_应答
            MsgQueue.current().publish("resp:"+ uid);

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
