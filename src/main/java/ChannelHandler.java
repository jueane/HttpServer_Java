import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ChannelHandler {
    SelectionKey selectionKey;
    SocketChannel channel;

    static int globalnum = 0;

    int num;

    public ChannelHandler(SelectionKey skey) {
        selectionKey = skey;
        channel = (SocketChannel) selectionKey.channel();
        num = ++globalnum;
    }

    public void onConnected() {
        System.out.println("new "+hashCode());
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (len == 0) {

        } else {
            String s = new String(byteBuffer.array(), 0, len);
            System.out.println(hashCode()+" recv: "+s);
            System.out.println("-----------------------------------------");
        }
    }

    public void onConnectionClosed() {

    }

    public void onExceptionOccurred() {

    }

}
