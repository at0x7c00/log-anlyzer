package me.huqiao.loganlyzer.querylanguage;

public class StateMachine {
	
	public void change(String param){
		transfer(getCurrentState(),param);
	}
	
	private void transfer(State currentState, String param) {
		
	}

	public State getCurrentState(){
		return null;
	}
	
}
