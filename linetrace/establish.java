package linetrace;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class establish {

    private static final int   SOCKET_PORT          = 7360; // PCと接続するポート

    private static ServerSocket    server = null;
    private static Socket          client = null;
    private static InputStream     inputStream = null;
    private static DataInputStream dataInputStream = null;
    private static int             remoteCommand = 0;

    /*
     * 通信確立を行う
     */
	static void esta(){
		if (server == null) { // 未接続
            try {
                server = new ServerSocket(SOCKET_PORT);
                client = server.accept();
                inputStream = client.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
            } catch (IOException ex) {
                ex.printStackTrace();
                server = null;
                dataInputStream = null;
            }
        } else {
            try {
                if (dataInputStream.available() > 0) {
                    remoteCommand = dataInputStream.readInt();
                }
            } catch (IOException ex) {
            }
        }
	}

	/*
     * リモートコマンドのチェック
     */
    static boolean checkRemoteCommand(int command) {
        if (remoteCommand > 0) {
            if (remoteCommand == command) {
                return true;
            }
        }
        return false;
    }

    /*
     * 通信接続の解除
     */
    public static void finish(){
    	if (server != null) {
            try { server.close(); } catch (IOException ex) {}
        }
    }
}
