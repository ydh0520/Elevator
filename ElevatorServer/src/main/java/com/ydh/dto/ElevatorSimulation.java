package com.ydh.dto;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
public class ElevatorSimulation implements Runnable {
	int maxFloor;
	int waitCounter;
	int id;
	int personid;
	Elevator elevator;
	Queue<Person> personList;
	List<List<Person>> waitList;
	StringBuffer log;

	public ElevatorSimulation(int id, int floor) {
		this.maxFloor = floor;
		this.elevator = new Elevator(id, floor);
		this.personid = 0;
		this.waitList = new LinkedList<>();
		this.id = id;
		for (int i = 0; i <= maxFloor; i++) {
			this.waitList.add(new LinkedList<>());
		}
		this.waitCounter = 0;
		log = new StringBuffer();
	}

	public void call(Person p) {
		waitList.get(p.getStart()).add(p);
		waitCounter++;
		log.append("<호출> " + "pid : " + p.getId() + " (" + p.start + " > " + p.end + ")\n");
		System.out.println("<호출> " + "pid : " + p.getId() + " (" + p.start + " > " + p.end + ")");
	}

	@Override
	public void run() {
		int recentMovedirection = 0;
		try {
			while (true) {
				System.out.println(this.getId() + "호기 " + elevator.floor + " 층 입니다.");

				if (elevator.getState() == 2) {
					Iterator<?> itr = waitList.get(elevator.getFloor()).iterator();

					while (itr.hasNext()) {
						Person p = (Person) itr.next();
						if (elevator.addPerson(p)) {
							log.append("<탑승> " + "pid : " + p.getId() + " (" + p.start + " > " + p.end + ")\n");
							System.out.println("<탑승> " + "pid : " + p.getId() + " (" + p.start + " > " + p.end + ")");
							waitCounter--;
							itr.remove();
						}
					}
				}

				List<Person> finish = elevator.move();

				if (elevator.getState() == 1 || elevator.getState() == -1) {
					recentMovedirection = elevator.getState();
				}

				if (finish != null) {
					for (Person p : finish) {
						log.append("<도착> " + "pid : " + p.getId() + " (" + p.start + " > " + p.end + ")\n");
						System.out.println("<도착> " + "pid : " + p.getId() + " (" + p.start + " > " + p.end + ")");
					}
				}

				if (!waitList.get(elevator.getFloor()).isEmpty()) {
					elevator.setState(2);
				}

				if (elevator.getState() == 0) {
					if (waitCounter != 0) {
						int min = maxFloor;
						int max = 1;

						for (int i = 1; i < maxFloor; i++) {
							if (waitList.get(i).isEmpty()) {
								continue;
							} else {
								min = i;
								break;
							}
						}

						for (int i = maxFloor; i > 0; i--) {
							if (waitList.get(i).isEmpty()) {
								continue;
							} else {
								max = i;
								break;
							}
						}

						if (recentMovedirection == 1) {
							elevator.setTarget(min);
						} else {
							elevator.setTarget(max);
						}

					}
				}

				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String finishSimulation() {
		return log.toString();
	}
}
