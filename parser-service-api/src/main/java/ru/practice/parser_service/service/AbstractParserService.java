package ru.practice.parser_service.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public abstract class AbstractParserService<T> {

    protected abstract T parseData(String source);

    protected abstract void sendData(T data);

    protected abstract String getDataSource();

    protected void onStart() {
        log.debug("The beginning of the planned parsing");
    }

    protected void onFinish(T data) {
        if (data instanceof Collection<?> collection) {
            log.debug("The scheduled parsing is completed. Total items sent: {}", collection.size());
        } else {
            log.debug("The scheduled parsing is completed. Data sent: {}", data);
        }
    }

    protected void handleError(String source, Exception exception) {
        log.error("Parsing error source {}: {}", source, exception.getMessage());
    }

    public void parseAndSend() {
        String dataSource = getDataSource();
        onStart();
        try {
            log.debug("Parsing with source: {}", dataSource);
            T data = parseData(dataSource);
            if (isEmpty(data)) {
                log.debug("No data found for source: {}", dataSource);
                return;
            }
            sendData(data);
            onFinish(data);
        } catch (Exception e) {
            handleError(dataSource, e);
        }
    }

    private boolean isEmpty(T data) {
        if (data == null)
            return true;
        if (data instanceof Collection<?> collection)
            return collection.isEmpty();
        return false;
    }
}
