package com.qmzy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class MyService extends Thread{
	public static int PORT=10005;
	private static DatagramSocket socket;
	private static ArrayList<Client> mList=new ArrayList<>();
	public MyService() {
		try {
			socket=new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	public static void loginGroups(Client clientActivity) {
		if(clientActivity==null)
			return;
		mList.add(clientActivity);
	}
	
	private void receiveMessage() {
		byte[] buf=new byte[1024];
		DatagramPacket datagramPacket=new DatagramPacket(buf, buf.length);
		while(true) {
			try {
				socket.receive(datagramPacket);
				String msg=new String(datagramPacket.getData(),0,datagramPacket.getLength());
				Gson gson=new Gson();
				MessageBean bean=gson.fromJson(msg, MessageBean.class);
				for(Client clientActivity:mList) {
					//发送数据给每一个客户端
					clientActivity.pushMessage(bean);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
//		while(true) {
			receiveMessage();
//		}
	}
}