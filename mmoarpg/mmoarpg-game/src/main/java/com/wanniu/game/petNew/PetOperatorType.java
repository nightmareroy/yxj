package com.wanniu.game.petNew;

public enum PetOperatorType {

	Add(0), Delete(1), Replace(2), Reset(3);

	private int value;

	private PetOperatorType(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
