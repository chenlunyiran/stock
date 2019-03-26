package com.twotiger.stock.db.query;


/**
 * 
* @ClassName: Duad
* @Description: 双值结构
* @author liuqing
* @date 2012-11-24 上午8:35:20
* @version 1.0
* @update
* @param <F>
* @param <S>
 */
public class Duad<F,S> {
	public Duad(){
		
	}
	public Duad(F firstValue, S secondValue){
		this.firstValue = firstValue;
		this.secondValue = secondValue;
	}
	private F firstValue;
	private S secondValue;

	
    public static <F,S> Duad<F,S> valueOf(F firstValue,S secondValue){
        return  new Duad<>(firstValue,secondValue);
    }
	
	public F getFirstValue() {
		return firstValue;
	}
	public void setFirstValue(F firstValue) {
		this.firstValue = firstValue;
	}
	public S getSecondValue() {
		return secondValue;
	}
	public void setSecondValue(S secondValue) {
		this.secondValue = secondValue;
	}
	
	public F getName(){
		return firstValue;
	}
	public void setName(F name){
		firstValue=name;
	}
	public S getValue(){
		return secondValue;
	}
	
	public void setValue(S value){
		secondValue=value;
	}
	public F getMin(){
		return firstValue;
	}
	public void setMin(F minValue){
		this.firstValue=minValue;
	}
	public S getMax(){
		return this.secondValue;
	}
	public void setMax(S maxValue){
		this.secondValue=maxValue;
	}
    public F getKey(){
        return firstValue;
    }

     
}
