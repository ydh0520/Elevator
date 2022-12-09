package com.ydh.controller;

import java.util.HashMap;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ydh.dto.Call;
import com.ydh.dto.ElevatorSimulation;
import com.ydh.dto.Person;

@RestController
@RequestMapping("/api")
public class ElevatorController {

	HashMap<Integer, ElevatorSimulation> simulations;
	HashMap<Integer, Thread> threads;
	HashMap<Integer, Thread> autoThreads;

	int simulationid;

	@PostConstruct
	public void init() {
		this.simulations = new HashMap<>();
		this.threads = new HashMap<>();
		this.autoThreads = new HashMap<>();
		simulationid = 0;
	}

	@PostMapping("start")
	public ResponseEntity<String> startElevatorSimulation(@RequestBody Integer floor) {
		ElevatorSimulation elevatorSimulation = new ElevatorSimulation(++simulationid, floor);

		Thread t = new Thread(elevatorSimulation);
		t.start();

		simulations.put(elevatorSimulation.getId(), elevatorSimulation);
		threads.put(elevatorSimulation.getId(), t);

		String message = "Start Elevator Simlulation " + simulationid;
		System.out.println(">> " + message);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@PostMapping("call")
	public ResponseEntity<String> callElevator(@RequestParam Integer simulationId, @RequestBody Call call) {
		simulations.get(simulationId).call(new Person(call.getStart(), call.getEnd()));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("test")
	public void test(@RequestParam Integer simulationId) {

		simulations.get(simulationId).call(new Person(2, 10));
		simulations.get(simulationId).call(new Person(3, 15));
		simulations.get(simulationId).call(new Person(7, 12));
		simulations.get(simulationId).call(new Person(5, 8));
		simulations.get(simulationId).call(new Person(1, 4));
		simulations.get(simulationId).call(new Person(14, 2));
		simulations.get(simulationId).call(new Person(5, 1));
	}

	@DeleteMapping("end")
	public ResponseEntity<String> endElevatorSimulation(@RequestParam Integer simulationId) {
		String message = simulations.get(simulationId).finishSimulation();
		threads.get(simulationId).interrupt();
		if (autoThreads.containsKey(simulationId)) {
			autoThreads.get(simulationId).interrupt();
		}

		threads.remove(simulationId);
		simulations.remove(simulationId);
		autoThreads.remove(simulationId);

		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@PostMapping("auto")
	public void auto(@RequestParam Integer simulationId) {
		if (!autoThreads.containsKey(simulationId)) {
			auto auto = new auto(simulationid, simulations.get(simulationId).getMaxFloor());
			Thread t = new Thread(auto);
			t.start();
			autoThreads.put(simulationId, t);
		}
	}

	public class auto implements Runnable {
		public int maxfloor;
		public int simulationId;
		public Random random;

		public auto(int simulationId, int maxfloor) {
			this.maxfloor = maxfloor;
			this.simulationId = simulationId;
			this.random = new Random();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				int start = random.nextInt(maxfloor) + 1;
				int end = random.nextInt(maxfloor) + 1;

				while (start == end) {
					end = random.nextInt(maxfloor) + 1;
				}

				simulations.get(simulationId).call(new Person(start, end));

				try {
					Thread.sleep((random.nextInt(5) + 1) * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}
}
