import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioTest {

    @Test
    public void test1() throws IOException {
        System.out.println("test nio");

        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(7777));

        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        int number = 0;
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> itr = keys.iterator();
            while (itr.hasNext()) {
                SelectionKey cur = itr.next();

//                System.out.println("hash:"+cur.hashCode());
                // accept
                if (cur.isAcceptable()) {
                    // AcceptableKey中channel就是ServerSocketChannel
                    SocketChannel acceptedChannel = ssc.accept();
                    acceptedChannel.configureBlocking(false);
                    acceptedChannel.register(selector, SelectionKey.OP_READ);
                    SelectionKey newKey = acceptedChannel.keyFor(selector);
                    number++;
                    newKey.attach(number);
                    System.out.println("accept number: " + number);
                }
                // read
                if (cur.isReadable()) {
                    System.out.println("read hash:" + cur.hashCode());
//
                    int num = -1;
//                    if(cur.attachment()!=null){
                    num = Integer.parseInt(cur.attachment().toString());
//                    }else{
//                        number++;
//                        cur.attach(number);
//                    }
                    SocketChannel channel = (SocketChannel) cur.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len = channel.read(byteBuffer);
                    System.out.println("收到数据长度：" + len + " ," + channel.isConnected() + ", " + channel.isOpen() + " ," + channel.isConnectionPending());
                    if (len == -1) {
                        System.out.println(num+" closed");
                        channel.close();
                    } else if (len == 0) {

                    } else {
                        String s = new String(byteBuffer.array(), 0, len);
                        System.out.println("-----------------------------------------");
                        System.out.println(num + " read: " + s);
                        System.out.println();
                    }
                }
                itr.remove();
                ;
            }


        }

    }
}
