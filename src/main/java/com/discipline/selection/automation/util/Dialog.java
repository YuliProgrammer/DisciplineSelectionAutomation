package com.discipline.selection.automation.util;

import com.discipline.selection.automation.exceptions.InvalidDataException;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.discipline.selection.automation.util.Constants.XLSX_FILE_FORMAT;
import static com.discipline.selection.automation.util.Constants.XLS_FILE_FORMAT;

@UtilityClass
public class Dialog {

    public static String dialog(List<String> scheduleFileNames) throws InvalidDataException {
        Scanner scanner = new Scanner(System.in);

        String fileName = getFileNameDialog(scanner,
                "Введiть iм'я файлу, який мiстить 2 листа: список обраних та назви обраних дисциплiн: ");

        int fileCount = getFileCountsForSchedule(scanner);
        String scheduleFileName;
        for (int i = 0; i < fileCount; i++) {
            scheduleFileName = getFileNameDialog(scanner,
                    String.format("Введiть iм'я %d файлу, який мiстить розклад викладачiв: ", i + 1));
            while(scheduleFileNames.contains(scheduleFileName)){
                System.out.println("Ви вже ввели це ім'я файлу.");
                scheduleFileName = getFileNameDialog(scanner,
                        String.format("Введiть iм'я %d файлу, який мiстить розклад викладачiв: ", i + 1));
            }
            scheduleFileNames.add(scheduleFileName);
        }
        return fileName;
    }

    private static String getFileNameDialog(Scanner scanner, String message) {
        System.out.print(message);
        String fileName = scanner.nextLine().trim();
        if (fileName.isEmpty()) {
            System.out.println("Ім'я файлу не може бути порожнiм.");
            throw new InvalidDataException("Ім'я файлу не може бути порожнiм.");
        }

        checkFileFormat(fileName);
        checkIfFileExists(fileName);
        return fileName;
    }

    private static int getFileCountsForSchedule(Scanner scanner) {
        int fileCount;
        do {
            System.out.print("Із сколькома файлами розкладів плануєте працювати (має бути додатнє число)? ");
            fileCount = scanner.nextInt();
            scanner.nextLine();
        } while (fileCount <= 0);
        return fileCount;
    }

    private static void checkIfFileExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.printf("Файл з iм'ям \"%s\" не знайднено. " +
                            "Будь ласка, перевiрте правильнiсть iменi файлу (воно обов'язково має включати повний шлях до файлу).%n",
                    fileName);
            throw new InvalidDataException(String.format("Файл з iм'ям \"" + fileName + "\" не знайднено.", fileName));
        }
    }

    private static void checkFileFormat(String fileName) {
        if (fileName.endsWith(XLS_FILE_FORMAT)) {
            System.out.printf("Файл з iм'ям \"%s\" має невiрний формат \"%s\". Допустимий формат файлу \"%s\".%n",
                    fileName, XLS_FILE_FORMAT, XLSX_FILE_FORMAT);
            throw new InvalidDataException(
                    String.format("Файл з iм'ям \"%s\" має невiрний формат \"%s\".", fileName, XLS_FILE_FORMAT));
        }
    }

}
