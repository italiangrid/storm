package it.grid.storm.space.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecCommand {

	private static final Logger log = LoggerFactory.getLogger(ExecCommand.class);

	private Semaphore outputSem;
	private String output;
	private Semaphore errorSem;
	private String error;
	private Process p;

	private class InputWriter extends Thread {

		private String input;

		public InputWriter(String input) {

			this.input = input;
		}

		public void run() {

			PrintWriter pw = new PrintWriter(p.getOutputStream());
			pw.println(input);
			pw.flush();
		}
	}

	private class OutputReader extends Thread {

		public OutputReader() {

			try {
				outputSem = new Semaphore(1);
				outputSem.acquire();
			} catch (InterruptedException e) {
				log
					.warn("Interrupt occours when retrieve output form the execution on a native command. "
						+ e.getMessage());
			}
		}

		public void run() {

			try {
				StringBuffer readBuffer = new StringBuffer();
				BufferedReader isr = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
				String buff = new String();
				while ((buff = isr.readLine()) != null) {
					readBuffer.append(buff);
					System.out.println(buff);
				}
				output = readBuffer.toString();
				outputSem.release();
			} catch (IOException e) {
				log
					.warn("IO Exception occours when retrieve output form the execution on a native command. "
						+ e.getMessage());
			}
		}
	}

	private class ErrorReader extends Thread {

		public ErrorReader() {

			try {
				errorSem = new Semaphore(1);
				errorSem.acquire();
			} catch (InterruptedException e) {
				log
					.warn("Interrupt occours when retrieve ERROR output form the execution on a native command. "
						+ e.getMessage());
			}
		}

		public void run() {

			try {
				StringBuffer readBuffer = new StringBuffer();
				BufferedReader isr = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
				String buff = new String();
				while ((buff = isr.readLine()) != null) {
					readBuffer.append(buff);
				}
				error = readBuffer.toString();
				errorSem.release();
			} catch (IOException e) {
				log
					.warn("IO Exception occours when retrieve ERROR output form the execution on a native command. "
						+ e.getMessage());
			}
			if (error.length() > 0)
				log.warn("Error returned by native command: " + error);
		}
	}

	public ExecCommand(String command, String input) {

		try {
			p = Runtime.getRuntime().exec(makeArray(command));
			new InputWriter(input).start();
			new OutputReader().start();
			new ErrorReader().start();
			p.waitFor();
		} catch (IOException e) {
			log.warn("IO Exception occours during the execution of a native command "
				+ command + ". " + e.getMessage());
		} catch (InterruptedException e) {
			log
				.warn("Interrupted Exception occours during the execution of a native command "
					+ command + ". " + e.getMessage());
		}
	}

	public ExecCommand(String command) {

		try {
			p = Runtime.getRuntime().exec(makeArray(command));
			new OutputReader().start();
			new ErrorReader().start();
			p.waitFor();
		} catch (IOException e) {
			log.warn("IO Exception occours during the execution of a native command "
				+ command + ". " + e.getMessage());
		} catch (InterruptedException e) {
			log
				.warn("Interrupted Exception occours during the execution of a native command "
					+ command + ". " + e.getMessage());
		}
	}

	public String getOutput() {

		try {
			outputSem.acquire();
		} catch (InterruptedException e) {
			log
				.warn("Interrupt occours when acquiring Semaphore the execution on a native command. "
					+ e.getMessage());
		}
		String value = output;
		outputSem.release();
		return value;
	}

	public String getError() {

		try {
			errorSem.acquire();
		} catch (InterruptedException e) {
			log
				.warn("Interrupt occours when acquiring Semaphore the execution on a native command. "
					+ e.getMessage());
		}
		String value = error;
		errorSem.release();
		return value;
	}

	private String[] makeArray(String command) {

		ArrayList<String> commandArray = new ArrayList<String>();
		String buff = "";
		boolean lookForEnd = false;
		for (int i = 0; i < command.length(); i++) {
			if (lookForEnd) {
				if (command.charAt(i) == '\"') {
					if (buff.length() > 0)
						commandArray.add(buff);
					buff = "";
					lookForEnd = false;
				} else {
					buff += command.charAt(i);
				}
			} else {
				if (command.charAt(i) == '\"') {
					lookForEnd = true;
				} else if (command.charAt(i) == ' ') {
					if (buff.length() > 0)
						commandArray.add(buff);
					buff = "";
				} else {
					buff += command.charAt(i);
				}
			}
		}
		if (buff.length() > 0)
			commandArray.add(buff);

		String[] array = new String[commandArray.size()];
		for (int i = 0; i < commandArray.size(); i++) {
			array[i] = commandArray.get(i);
		}

		return array;
	}
}