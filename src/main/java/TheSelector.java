import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class TheSelector {

    public void begin() throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(7777));

        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        int number = 0;
        while (true) {
            System.out.println("wait");
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
//            if (keys.size() > 0)
                System.out.println("recv keys count:"+keys.size());
            Iterator<SelectionKey> itr = keys.iterator();
            while (itr.hasNext()) {
                SelectionKey cur = itr.next();
                System.out.println("key property: accept "+cur.isAcceptable()+" ,read "+cur.isReadable());;

                // accept
                if (cur.isAcceptable()) {
                    number++;
                    cur.attach(number);
                    ServerSocketChannel sschannel = (ServerSocketChannel) cur.channel();
                    SocketChannel accept = sschannel.accept();
                    if(accept==null){
                        if(cur.attachment()!=null){
                            int num = Integer.parseInt(cur.attachment().toString());
                            System.out.println(num+" 断开");
                        }else{
                            System.out.println("断开");
                        }
                    }else{
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                        System.out.println("accept number: " + number);
                    }
                }
                // read
                if (cur.isReadable()) {
                    SocketChannel channel = (SocketChannel) cur.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len=channel.read(byteBuffer);
                    String s = new String(byteBuffer.array(),0,len);
                    int num=-1;
                    if(cur.attachment()!=null){
                        num = Integer.parseInt(cur.attachment().toString());
                    }
                    System.out.println(num + " read: " + s);
                }
                itr.remove();;
            }
        }

    }


}