package org.wso2.extension.siddhi.io.snmp.utils;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Map;

/**
 * This class is use to hold last 10 snmp events
 * by list of key-value map
 *
 * */
public class EventHolder { //ToDo ->  fixme ;-)
    private Logger log = Logger.getLogger(EventHolder.class);
    private volatile LinkedList<Map<String, String>> eventList;
    private int listSize = 10;

    public EventHolder() {
        eventList = new LinkedList<>();
    }

    public EventHolder(int size) {
        eventList = new LinkedList<>();
        this.listSize = size;
    }

    public int getEventCounts() {
        return eventList.size();
    }

    public void addEvent(Map<String, String> map) {
        eventList.addFirst(map);
        if (eventList.size() > listSize) {
            eventList.removeLast();
        }
    }

    public Map<String, String> getEvent(int index) {
        if (index > listSize) {
            return eventList.get(listSize);
        } else if (index < 0) {
            return eventList.get(0);
        }
        return eventList.get(index);
    }

    public String eventToString(int index) {
        return getEvent(index).toString();
    }

    public boolean assertDataContent(String value, int index) {
        try {
            log.debug(eventList.get(index).toString());
            if (eventList.get(index).containsValue(value)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.debug("Outofbound exception : " + e);
            return false;
        }
    }


    public void clear() {
        eventList = null;
        eventList = new LinkedList<>();
    }
}
