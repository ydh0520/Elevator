package com.ydh.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Elevator {
	int id;
	int state;
	int floor;
	int target;
	int maxfloor;
	HashMap<Integer, List<Person>> personMap;

	public Elevator(int id, int maxfloor) {
		this.id = id;
		this.state = 0;
		this.floor = 1;
		this.target = 1;
		this.maxfloor = maxfloor;
		personMap = new HashMap<>();
	}

	public List<Person> move() {

		if (state == 2) {
			List<Person> list = personMap.get(this.floor);
			personMap.remove(this.floor);
			if (target == floor) {
				state = 0;
			} else {
				state = (floor < target) ? 1 : -1;
			}
			return list;
		} else {
			floor += state;
			if (personMap.containsKey(floor)) {
				state = 2;
			}
			return null;
		}

	}

	public boolean setTarget(int t) {
		if (t < 1 || t >= maxfloor) {
			return false;
		}
		
		if (state == 0) {
			target = t;
			if (target == floor) {
				state = 2;
			} else {
				state = (floor < target) ? 1 : -1;
			}
		} else if (state == 1) {
			target = Math.max(this.target, t);
		} else if (state == -1) {
			target = Math.min(this.target, t);
		}

		return true;
	}

	public boolean addPerson(Person p) {
		this.state = (floor < target) ? 1 : -1;
		if (floor == target) {
			state = 0;
		}
		if (state == 0 || p.state == state) {

			if (state == 0) {
				this.setTarget(p.getEnd());
			}

			int curTarget = p.getEnd();
			List<Person> personList = personMap.getOrDefault(curTarget, new LinkedList<>());

			personList.add(p);
			personMap.put(curTarget, personList);
			this.setTarget(curTarget);
			return true;
		} else {

			return false;
		}

	}

}
