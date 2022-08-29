package com.qmzy;

import java.awt.EventQueue;

import javax.swing.JFrame;

	import javax.sound.sampled.AudioFileFormat;
	import javax.sound.sampled.AudioFormat;
	import javax.sound.sampled.AudioInputStream;
	import javax.sound.sampled.AudioSystem;
	import javax.sound.sampled.DataLine;
	import javax.sound.sampled.DataLine.Info;
	import javax.sound.sampled.LineUnavailableException;
	import javax.sound.sampled.SourceDataLine;
	import javax.sound.sampled.TargetDataLine;
	import javax.sound.sampled.UnsupportedAudioFileException;
	import javax.swing.JButton;
	import java.awt.event.ActionListener;
	import java.io.File;
	import java.io.IOException;
	import java.net.DatagramPacket;
	import java.net.DatagramSocket;
	import java.net.InetAddress;
	import java.net.SocketException;
	import java.net.UnknownHostException;
	import java.awt.event.ActionEvent;
	import javax.swing.JTextField;
	import javax.swing.JTextPane;
	import com.google.gson.Gson;
	import javax.swing.JTextArea;

	public class Client extends JFrame{

		private String name;
		private JTextField textField;
		private DatagramSocket socket;
		private InetAddress ip;
		private JTextArea textArea;
		private JButton playBtn;
		
		private AudioFormat format;
		public TargetDataLine targetDataLine;
		private boolean isStartAduio=true;
		
		public Client (String name) {
			super("聊天客户端："+name);
			this.name=name;
			setSize(500, 500);
			getContentPane().setLayout(null);
			initLayout();
			initUdp();
			show();
		}
		
		private void initLayout() {
			JButton sendBtn = new JButton("发送");
			sendBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String msg=textField.getText();
					sendMessage(msg, 0);
				}
			});
			sendBtn.setBounds(366, 419, 97, 23);
			getContentPane().add(sendBtn);
			
			textField = new JTextField();
			textField.setBounds(10, 419, 346, 23);
			getContentPane().add(textField);
			textField.setColumns(10);
			
			JButton soundRecordBtn = new JButton("点击录音");
			soundRecordBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(isStartAduio) {
						captureAudio();
						soundRecordBtn.setText("点击结束");
					}else {
						targetDataLine.stop();
						targetDataLine.close();
						soundRecordBtn.setText("点击录音");
						sendMessage("给您发送了语言,请点击播放", 1);
						playBtn.setVisible(true);
					}
					isStartAduio=!isStartAduio;
				}
			});
			soundRecordBtn.setBounds(48, 375, 97, 23);
			getContentPane().add(soundRecordBtn);
			
			playBtn = new JButton("点击播放");
			playBtn.setVisible(false);
			playBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playAduio();
					playBtn.setText("正在播放...");
				}
			});
			playBtn.setBounds(292, 375, 97, 23);
			getContentPane().add(playBtn);
			
			textArea = new JTextArea();
			textArea.setBounds(10, 0, 466, 365);
			getContentPane().add(textArea);
		}
		private void initUdp() {
			MyService.loginGroups(this);
			try {
				socket=new DatagramSocket();
				ip=InetAddress.getByName("127.0.0.1");
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
		}
		public void pushMessage(MessageBean bean) {
			textArea.append(bean.getName()+":"+bean.getContent()+"\n");
			if(bean.getType()==0) {
				playBtn.setVisible(false);
			}else {
				playBtn.setVisible(true);
			}
		}
		private void sendMessage(String msg,int type) {
			MessageBean bean=new MessageBean();
			bean.setName(name);
			bean.setContent(msg);
			bean.setType(type);
			Gson gson=new Gson();
			String json=gson.toJson(bean);
			byte[] bytes=json.getBytes();
			DatagramPacket datagramPacket=new DatagramPacket(bytes, bytes.length,ip,MyService.PORT);
			try {
				socket.send(datagramPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private void captureAudio() {
			format=getAudioForamt();
			DataLine.Info dataLineInfo=new DataLine.Info(TargetDataLine.class, format);
			try {
				targetDataLine=(TargetDataLine) AudioSystem.getLine(dataLineInfo);
				new CaptureThread().start();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
			
		}
		private AudioFormat getAudioForamt() {
			float sampleRate=11025.0f;
			int sampleSizeInBits=16;
			int channels=1;
			boolean signed=true;
			boolean bigEndian=false;
			return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		}
		class CaptureThread extends Thread{
			@Override
			public void run() {
				AudioFileFormat.Type fileType=AudioFileFormat.Type.WAVE;
				File audioFile=new File("chat.wav");
				try {
					targetDataLine.open(format);
					targetDataLine.start();
					AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void playAduio()  {
			AudioInputStream as;
			try {
				as=AudioSystem.getAudioInputStream(new File("chat.wav"));
				AudioFormat format=as.getFormat();
				SourceDataLine sdl=null;
				DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);
				sdl=(SourceDataLine) AudioSystem.getLine(info);
				sdl.open(format);
				sdl.start();
				int nBytesRead=0;
				byte[] abData=new byte[512];
				while(nBytesRead!=-1) {
					nBytesRead=as.read(abData,0,abData.length);
					if(nBytesRead>=0) {
						sdl.write(abData, 0, nBytesRead);
					}
				}
				sdl.drain();
				sdl.close();
				playBtn.setVisible(false);
				playBtn = new JButton("点击播放");
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
		
	}
	
