package com.kupanet.cashiersystem.model;

public class Order {
	private int id;
	private String numbers;
	private String time;
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id=id;
	}
	public String getNumbers(){
		return this.numbers;
	}
	public void setNumbers(String numbers){
		this.numbers=numbers;
	}
	public String getTime(){
		return this.time;
	}
	public void setTime(String time){
		this.time=time;
	}
}
