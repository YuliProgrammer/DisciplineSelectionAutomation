package com.discipline.selection.automation;

import com.discipline.selection.automation.exceptions.InvalidDataException;
import com.discipline.selection.automation.util.Dialog;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@EnableJpaRepositories
@SpringBootApplication
public class MainApplication implements ApplicationContextAware {

    public static String FILE_NAME;
    public static final List<String> SCHEDULE_FILE_NAMES = new ArrayList<>();

    static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MainApplication.applicationContext = applicationContext;
    }

    public static void main(String... args) {
        SpringApplication.run(MainApplication.class, args);

        try {
            FILE_NAME = Dialog.dialog(SCHEDULE_FILE_NAMES);
        } catch (InvalidDataException e) {
            return;
        }

        MainService mainService = applicationContext.getBean(MainService.class);
        mainService.execute();
    }

    // D:\University\Cursah\Test\2022 vxid mag 2sem22-23 (1).xlsx
    // D:\University\Cursah\Test\2022 rozklad mag. 2sem 03.01.xlsx

}
