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

    public static List<String> dialog() throws InvalidDataException {
        Scanner scanner = new Scanner(System.in);

        List<String> fileNames = new ArrayList<>();
        fileNames.add(getFileNameDialog(scanner,
                "Введiть iм'я файлу, який мiстить 2 листа: список обраних та назви обраних дисциплiн: "));
        fileNames.add(getFileNameDialog(scanner, "Введiть iм'я файлу, який мiстить розклад викладачiв: "));

        return fileNames;
    }

    private static String getFileNameDialog(Scanner scanner, String message) {
        System.out.print(message);
        String fileName = scanner.nextLine().trim();
        if (fileName.isEmpty()) {
            System.out.println("iм'я файлу не може бути порожнiм.");
            throw new InvalidDataException("iм'я файлу не може бути порожнiм.");
        }

        checkFileFormat(fileName);
        checkIfFileExists(fileName);
        return fileName;
    }

    private static void checkIfFileExists(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println(String.format(
                    "Файл з iм'ям \"%s\" не знайднено. " +
                            "Будь ласка, перевiрте правильнiсть iменi файлу (воно обов'язково має включати повний шлях до файлу).",
                    fileName));
            throw new InvalidDataException(String.format("Файл з iм'ям \"" + fileName + "\" не знайднено.", fileName));
        }
    }

    private static void checkFileFormat(String fileName) {
        if (fileName.endsWith(XLS_FILE_FORMAT)) {
            System.out.println(String.format(
                    "Файл з iм'ям \"%s\" має невiрний формат \"%s\". Допустимий формат файлу \"%s\".",
                    fileName, XLS_FILE_FORMAT, XLSX_FILE_FORMAT));
            throw new InvalidDataException(
                    String.format("Файл з iм'ям \"%s\" має невiрний формат \"%s\".", fileName, XLS_FILE_FORMAT));
        }
    }

}
