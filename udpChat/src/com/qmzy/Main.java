package com.qmzy;

public class Main {

	public static void main(String[] args) {
		new Client("С��");
		new Client("С��");
		MyService myService=new MyService();
		myService.start();
	}

}
