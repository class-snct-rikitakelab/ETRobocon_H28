package ev3Viewer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * ログ送信クラス
 * */
public class LogSender {

	private static final int SOCKET_PORT = 7360;
	private ServerSocket server = null;
	private Socket socket = null;
	private DataOutputStream outputStream = null;
	private ArrayList<Log> Logs;


	public LogSender() {
		Logs = new ArrayList<Log>();
		// connect();
	}

	/**
	 * ビューアと接続します。
	 * @return 接続に成功した場合はtrue、失敗した場合はfalseを返します。
	 */
	public boolean connect() {
		if (server == null || outputStream == null) {
			try {
				server = new ServerSocket(SOCKET_PORT);
				socket = server.accept();
				// inputStream = new DataInputStream(socket.getInputStream()); // 現状viewerからsenderに送信する機能はない
				outputStream = new DataOutputStream(socket.getOutputStream());
			} catch (IOException ex) {
				server = null;
				outputStream = null;
				return false;
			}
		}
		return true;
	}

	/**
	 * ビューアとの接続を切断します。
	 * @return 切断に成功した場合はtrue、失敗した場合はfalseを返します。
	 */
	public boolean disconnect() {
		try {
			server.close();
		} catch (IOException ex) {
			return false;
		}
		return true;
	}


	/** ログデータを追加します。
	 *  @param name ログの名前
	 *  @param value ログの値
	 *  @param time タイムスタンプ */
	public void addLog(String name, String value, float time) {
		Log log = new Log();
		log.setName(name);
		log.setValue(value);
		log.setTime(time);
		Logs.add(log);
	}

	/** ログデータを追加します。
	 *  @param name ログの名前
	 *  @param value ログの値
	 *  @param time タイムスタンプ */
	public void addLog(String name, int value, float time) {
		Log log = new Log();
		log.setName(name);
		log.setValue(value);
		log.setTime(time);
		Logs.add(log);
	}

	/** ログデータを追加します。
	 *  @param name ログの名前
	 *  @param value ログの値
	 *  @param time タイムスタンプ */
	public void addLog(String name, float value, float time) {
		Log log = new Log();
		log.setName(name);
		log.setValue(value);
		log.setTime(time);
		Logs.add(log);
	}

	/** ログの表示方法を変更します。変更はsend時に適用されます。
	 *  @param name ログの名前
	 *  @param value ログの値 */
	public void setGlaph(String name, String value){
		Log log = new Log();
		log.setName("#" + name);
		log.setValue(value);
		log.setTime(0);
		Logs.add(log);
	}

	/** 送信していないログデータをすべて削除します。 */
	public void clear(){
		Logs.clear();
	}

	/**
	 * addLogを用いて取得したログデータをビューアに送信します。
	 * @return 送信に成功した場合はtrue、失敗した場合はfalseを返します。
	 */
	public boolean send() {
		if(outputStream == null)return false;
		String json = "";
		int size = Logs.size();
		if(!Logs.isEmpty()){
			try {
			for (int i = 0;i<size;++i) {
				json += Logs.get(i).getJson();
				if(i%72==70){
					/*文字列長が長くなりすぎるとあれなので定期的に送信する
					  送信間隔は50-500くらいで大丈夫だと思う   もう少し低くてもokかも */
					outputStream.writeBytes(json);
					json = "";
				}
			}
			} catch (IOException ex) {
				return false;
			}
		}
		try {
			outputStream.writeBytes(json);
		} catch (IOException ex) {
			return false;
		}
		Logs.clear();

		return true;
	}
}

class Log {
	private String name;//ログの名前
	private String value;//ログなどの値
	private float time;//タイムスタンプ

	public String getName() {return name;}
	public void setName(String name) {this.name = name;}

	public String getValue() {return value;}
	public void setValue(String value) {this.value = value;}
	public void setValue(int value) {this.value = String.valueOf(value);}
	public void setValue(float value) {this.value = String.valueOf(value);}

	public float getTime() {return time;}
	public void setTime(float time) {this.time = time;}

	public String getJson(){
		return "{" + name + "," + value + "," + time + "}";
		// setGlaphするときはnameの頭文字を"#"にする
	}

}