package com.discipline.selection.automation.service.reader.impl;

import com.discipline.selection.automation.dto.IncomingDataDto;
import com.discipline.selection.automation.service.reader.ReadFromExcel;

public abstract class BasicExcelReaderChain<K, V> implements ReadFromExcel<K, V> {

    protected ReadFromExcel nextReader;
    protected static final IncomingDataDto incomingDataDto = new IncomingDataDto();

    @Override
    public void setNextReader(ReadFromExcel nextReader) {
        this.nextReader = nextReader;
    }

    @Override
    public void execute() {
        uploadData();

        if (nextReader != null) {
            nextReader.execute();
        }
    }

    @Override
    public IncomingDataDto getIncomingDataDto() {
        return incomingDataDto;
    }

}
