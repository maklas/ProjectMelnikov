package ru.maklas.melnikov.engine.input;

import ru.maklas.melnikov.engine.other.Event;

public class KeyTypeEvent implements Event {

	private final char character;

	public KeyTypeEvent(char character) {
		this.character = character;
	}

	public char getCharacter() {
		return character;
	}
}
