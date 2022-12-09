package com.ydh.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Person {
	static int personCounter = 0;
	int id;
	int start;
	int end;
	int state;
	boolean use;

	public Person(int start, int end) {
		this.id = ++personCounter;
		this.start = start;
		this.end = end;
		this.state = (start < end) ? 1 : -1;
		this.use = false;
	}

}
