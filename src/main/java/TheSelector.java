import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class TheSelector {

    public void begin() {
        System.out.println("Http_Java server is running");

        Selector selector = null;
        ServerSocketChannel ssc = null;
        try {
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(7777));
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        while (true) {
            try {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> itr = keys.iterator();
                while (itr.hasNext()) {
                    SelectionKey cur = itr.next();

                    // accept
                    if (cur.isAcceptable()) {
                        ChannelHandler ch=null;
                        try{
                             ch = new ChannelHandler();
                        }catch (OutOfMemoryError ex){
//                            itr.remove();
                            continue;
                        }

                        // AcceptableKey中channel就是ServerSocketChannel
                        SocketChannel acceptedChannel = ssc.accept();
                        acceptedChannel.configureBlocking(false);
                        acceptedChannel.register(selector, SelectionKey.OP_READ);
                        SelectionKey newKey = acceptedChannel.keyFor(selector);

                        ch.init(newKey);

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
                        }
                    }
                    itr.remove();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}