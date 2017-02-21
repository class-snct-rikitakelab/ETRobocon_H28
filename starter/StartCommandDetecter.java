package starter;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import hardware.TouchSensor;

public class StartCommandDetecter{

    private static final int   SOCKET_PORT          = 7360; // PCと接続するポート
    public static int START_COMMAND = 71;

    private static ServerSocket    server = null;
    private static Socket          client = null;
    private static InputStream     inputStream = null;
    private static DataInputStream dataInputStream = null;
    private static int             remoteCommand = 0;

    private TouchSensor touch;

	public StartCommandDetecter(TouchSensor touch){
		this.touch = touch;
	}

	public void esta(){
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

	public boolean checkCommand(){

		if(touch.touchSensorIsPressed() == true){
			return true;
		}

		if(remoteCommand ==  START_COMMAND){
			return true;
		}

		return false;
	}

}
