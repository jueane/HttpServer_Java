import java.nio.channels.SelectionKey;

public class ChannelHandler {
    public SelectionKey selectionKey;

    public ChannelHandler(SelectionKey skey){
        selectionKey=skey;
    }

    public void OnConnected(){

    }

    public void OnDataArrived(){

    }

    public void OnConnectionClosed(){

    }

    public void OnExceptionOccurred(){

    }

}
