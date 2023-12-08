package com.discipline.selection.automation;

import com.discipline.selection.automation.dto.IncomingDataDto;
import com.discipline.selection.automation.service.reader.ReaderChainImpl;
import com.discipline.selection.automation.service.writer.WriterChainImpl;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class MainService {

    private ReaderChainImpl readerChain;

    @Autowired
    void setReaderChain(ReaderChainImpl readerChain) {
        this.readerChain = readerChain;
    }

    public void execute() {
        System.out.println("\nЧитання даних з вказаних файлiв...");
        IncomingDataDto incomingDataDto = readerChain.read();

        WriterChainImpl writerChain = new WriterChainImpl(incomingDataDto);
        writerChain.write();

        System.out.println("\nКiнець роботи програми.");
    }

}
