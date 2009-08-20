/**
 * 
 */
package component.tape.recalltable;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.tape.recalltable.RecallTableCatalog;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;
import it.grid.storm.tape.recalltable.persistence.PropertiesDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ritz
 * 
 */
public class RecallTablePropertiesDBTest {

    private static final Logger log = LoggerFactory.getLogger(TaskResourceTest.class);


    public static void main(String[] args) {
        RecallTablePropertiesDBTest testDB = new RecallTablePropertiesDBTest();
        testDB.createTasks(3);
        // testDB.showDB();
        testDB.numberOfTasks();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        testDB.takeoverNTasks(2);
        // testDB.showDB();
        testDB.numberOfTasks();
        testDB.showDB();
        testDB.printTaskList(testDB.getInProgressTask());
        testDB.completedTasks(1);
        // testDB.showDB();
        testDB.numberOfTasks();
        testDB.purgeCatalog(2);
        testDB.numberOfTasks();

    }


    /**
     * 
     */
    private void showDB() {
        PropertiesDB tasksDB = new PropertiesDB(true);
        try {
            ArrayList<RecallTaskTO> allTasks = new ArrayList<RecallTaskTO>(tasksDB.getAll().values());
            for (RecallTaskTO recallTaskTO : allTasks) {
                log.debug(recallTaskTO.toString());
            }
        } catch (FileNotFoundException e) {
            log.error("RecallTask DB does not exists!");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IO Error while reading RecallTaskDB.");
            e.printStackTrace();
        } catch (DataAccessException de) {
            log.error("Data Access Error");
            de.printStackTrace();
        }
    }


    private void numberOfTasks() {
        RecallTableCatalog recallTableCatalog = new RecallTableCatalog(true);
        log.debug("#Tasks Queued = " + recallTableCatalog.getNumberTaskQueued());
        log.debug("#Tasks In progress = " + recallTableCatalog.getNumberTaskInProgress());
        log.debug("RecallTable size = " + recallTableCatalog.getRecallTableSize());
    }


    private void takeoverATask() {
        RecallTableCatalog recallTableCatalog = new RecallTableCatalog(true);
        RecallTaskTO task = recallTableCatalog.takeoverTask();
        log.debug("Taken Task : " + task);
    }


    private void takeoverNTasks(int n) {
        RecallTableCatalog recallTableCatalog = new RecallTableCatalog(true);
        ArrayList<RecallTaskTO> tasksList = recallTableCatalog.takeoverNTasks(n);
        printTaskList(tasksList);
    }


    private List<RecallTaskTO> getInProgressTask() {
        RecallTableCatalog recallTableCatalog = new RecallTableCatalog(true);
        ArrayList<RecallTaskTO> taskList = new ArrayList<RecallTaskTO>(recallTableCatalog.getInProgressTasks());
        return taskList;
    }


    private void printTaskList(List<RecallTaskTO> taskList) {
        for (Object element : taskList) {
            RecallTaskTO recallTaskTO = (RecallTaskTO) element;
            log.debug("Task : " + recallTaskTO);
        }
    }


    private void changeStatusInSuccess(RecallTaskTO task) {
        RecallTableCatalog recallTableCatalog = new RecallTableCatalog(true);
        recallTableCatalog.changeStatus(task.getTaskId(), RecallTaskStatus.SUCCESS);
    }


    private void completedTasks(int number) {
        ArrayList<RecallTaskTO> taskList = new ArrayList<RecallTaskTO>(getInProgressTask());
        log.debug("TaskList size = " + taskList.size() + " and number = " + number);

        if (taskList.size() > number) {
            taskList = new ArrayList<RecallTaskTO>(taskList.subList(0, number));
        }
        for (Object element : taskList) {
            RecallTaskTO recallTaskTO = (RecallTaskTO) element;
            changeStatusInSuccess(recallTaskTO);
        }
    }


    private void purgeCatalog(int n) {
        RecallTableCatalog recallTableCatalog = new RecallTableCatalog(true);
        recallTableCatalog.purgeCatalog(n);
    }


    /**
     * @param i
     */
    private void createTasks(int numTasks) {
        RecallTaskTO task;
        int year = 2009;
        int month = 8;
        int day = 18;
        int hour = 0;
        int min = 0;
        int sec = 0;
        int secRnd;
        int minRnd;
        int hourRnd;

        for (int j = 0; j < numTasks; j++) {
            hourRnd = (int) Math.round(Math.random() * 23);
            minRnd = (int) Math.round(Math.random() * 59);
            secRnd = (int) Math.round(Math.random() * 59);
            // log.debug("date-" + j + " = " + hourRnd + ":" + minRnd + ":" +
            // secRnd);
            GregorianCalendar gc = new GregorianCalendar(year, month, day, hour + hourRnd, min + minRnd, sec + secRnd);
            Date date = gc.getTime();
            Format formatter = new SimpleDateFormat(RecallTaskTO.dateFormat);
            log.debug("Date = " + date + "formatted = " + formatter.format(date));
            task = RecallTaskTO.createRandom(date, "test");
            log.debug("TASK: " + task);
            PropertiesDB tasksDB = new PropertiesDB(true);
            try {
                tasksDB.addRecallTask(task);
            } catch (FileNotFoundException e) {
                log.error("Properties DB does not exists!");
                e.printStackTrace();
            } catch (IOException e) {
                log.error("I/O Error");
                e.printStackTrace();
            } catch (DataAccessException de) {
                log.error("Data Access Error");
                de.printStackTrace();
            }
        }

    }

}
