package com.discipline.selection.automation.util;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@UtilityClass
public class CellStyleCreator {

    /**
     * Function that create cell style for even rows (rowIndex % 2 = 0)
     *
     * @param workbook - current workbook
     * @return cell style with all basics characteristics like:
     * 1. white foreground color
     * 2. thin pale blue borders
     * 3. font "Times New Roman", 14
     */
    public XSSFCellStyle createEvenCellStyleCharacteristics(XSSFWorkbook workbook) {
        XSSFCellStyle foregroundStyle = workbook.createCellStyle();
        foregroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        foregroundStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        foregroundStyle.setFont(createFont(workbook));
        foregroundStyle.setWrapText(true);
        thinBorders(foregroundStyle);
        return foregroundStyle;
    }

    /**
     * Function that create cell style for odd rows (rowIndex % 2 = 1)
     *
     * @param workbook - current workbook
     * @return cell style with all basics characteristics like:
     * 1. blue foreground color: rgb(211, 232, 248)
     * 2. thin pale blue borders
     * 3. font "Times New Roman", 14
     */
    public XSSFCellStyle createOddCellStyleCharacteristics(XSSFWorkbook workbook) {
        XSSFCellStyle foregroundStyle = workbook.createCellStyle();
        foregroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        foregroundStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(211, 232, 248)));
        foregroundStyle.setFont(createFont(workbook));
        foregroundStyle.setWrapText(true);
        thinBorders(foregroundStyle);
        return foregroundStyle;
    }

    /**
     * Function that create cell style for duplicated rows
     *
     * @param workbook - current workbook
     * @return cell style with all basics characteristics like:
     * 1. orange foreground color
     * 2. thin pale blue borders
     * 3. font "Times New Roman", 14
     */
    public XSSFCellStyle createDuplicatedCellStyleCharacteristics(XSSFWorkbook workbook) {
        return createDuplicatedOrFarCellStyle(workbook, IndexedColors.ORANGE.getIndex());
    }

    /**
     * Function that create cell style for far rows
     *
     * @param workbook - current workbook
     * @return cell style with all basics characteristics like:
     * 1. yellow foreground color
     * 2. thin pale blue borders
     * 3. font "Times New Roman", 14
     */
    public XSSFCellStyle createFarCellStyleCharacteristics(XSSFWorkbook workbook) {
        return createDuplicatedOrFarCellStyle(workbook, IndexedColors.YELLOW.getIndex());
    }

    /**
     * Function that create cell style for duplicated and far rows
     *
     * @param workbook - current workbook
     * @return cell style with all basics characteristics like:
     * 1. red foreground color
     * 2. thin pale blue borders
     * 3. font "Times New Roman", 14
     */
    public XSSFCellStyle createDuplicatedFarCellStyleCharacteristics(XSSFWorkbook workbook) {
        return createDuplicatedOrFarCellStyle(workbook, IndexedColors.RED.getIndex());
    }

    /**
     * Function that create cell style for main header
     *
     * @param workbook - current workbook
     * @return cell style with all basics characteristics like:
     * 1. blue foreground color: rgb(92, 159, 212)
     * 2. without borders
     * 3. white font "Times New Roman", 14
     */
    public XSSFCellStyle createMainHeaderCellStyleCharacteristics(XSSFWorkbook workbook) {
        XSSFCellStyle foregroundStyle = workbook.createCellStyle();
        foregroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        foregroundStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(92, 159, 212)));
        foregroundStyle.setFont(createWhiteFont(workbook));
        foregroundStyle.setWrapText(true);
        withoutBorders(foregroundStyle);
        return foregroundStyle;
    }

    /**
     * Function that create cell style for additional header that contains column index
     *
     * @param workbook - current workbook
     * @return cell style with all basics characteristics like:
     * 1. blue foreground color: rgb(134, 170, 208)
     * 2. without borders
     * 3. white font "Times New Roman", 14
     */
    public XSSFCellStyle createAdditionalHeaderCellStyleCharacteristics(XSSFWorkbook workbook) {
        XSSFCellStyle foregroundStyle = workbook.createCellStyle();
        foregroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        foregroundStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(134, 170, 208)));
        foregroundStyle.setFont(createWhiteFont(workbook));
        foregroundStyle.setWrapText(true);
        withoutBorders(foregroundStyle);
        return foregroundStyle;
    }

    private XSSFCellStyle createDuplicatedOrFarCellStyle(XSSFWorkbook workbook, short colorIndex) {
        XSSFCellStyle foregroundStyle = workbook.createCellStyle();
        foregroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        foregroundStyle.setFillForegroundColor(colorIndex);
        foregroundStyle.setFont(createFont(workbook));
        foregroundStyle.setWrapText(true);
        thinBorders(foregroundStyle);
        return foregroundStyle;
    }

    /**
     * Function create font "Times New Roman", 14
     *
     * @param workbook - current workbook
     * @return font style
     */
    private Font createFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Times New Roman");
        return font;
    }

    /**
     * Function create white font "Times New Roman", 14
     *
     * @param workbook - current workbook
     * @return font style
     */
    private Font createWhiteFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Times New Roman");
        return font;
    }

    /**
     * Void add thin pale blue borders for current style
     *
     * @param cellStyle - current cell style to add borders
     */
    private void thinBorders(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.PALE_BLUE.getIndex());

        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.PALE_BLUE.getIndex());

        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.PALE_BLUE.getIndex());

        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.PALE_BLUE.getIndex());
    }

    /**
     * Void delete all borders from current style
     *
     * @param cellStyle - current cell style to delete all borders
     */
    private void withoutBorders(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.NONE);
        cellStyle.setBorderTop(BorderStyle.NONE);
        cellStyle.setBorderRight(BorderStyle.NONE);
        cellStyle.setBorderLeft(BorderStyle.NONE);
    }

}
