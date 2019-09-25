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

                // accept
                if (cur.isAcceptable()) {
                    // AcceptableKey中channel就是ServerSocketChannel
                    SocketChannel acceptedChannel = ssc.accept();
                    acceptedChannel.configureBlocking(false);
                    acceptedChannel.register(selector, SelectionKey.OP_READ);
                    SelectionKey newKey = acceptedChannel.keyFor(selector);
                    ChannelHandler ch = new ChannelHandler(newKey);
                    ch.onConnected();
                    newKey.attach(ch);
                }
                // read
                if (cur.isReadable()) {
                    ChannelHandler ch = (ChannelHandler) cur.attachment();
                    ch.onDataArrived();
                }
                // write
                if (cur.isValid() && cur.isWritable()) {
                    if (cur.attachment() != null) {
                        ChannelHandler ch = (ChannelHandler) cur.attachment();
                        ch.onReadyWrite();
//                        System.out.println("写一次");
                    }
                }
                itr.remove();

            }


        }

    }
}
