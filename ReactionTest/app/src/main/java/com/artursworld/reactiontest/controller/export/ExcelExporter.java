package com.artursworld.reactiontest.controller.export;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.artursworld.reactiontest.R;
import com.artursworld.reactiontest.controller.util.App;
import com.artursworld.reactiontest.controller.util.Strings;
import com.artursworld.reactiontest.controller.util.UtilsRG;
import com.artursworld.reactiontest.model.entity.MedicalUser;
import com.artursworld.reactiontest.model.entity.OperationIssue;
import com.artursworld.reactiontest.model.entity.ReactionGame;
import com.artursworld.reactiontest.model.persistence.manager.MedicalUserManager;
import com.artursworld.reactiontest.model.persistence.manager.OperationIssueManager;
import com.artursworld.reactiontest.model.persistence.manager.ReactionGameManager;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.CellFormat;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelExporter {

    private static String CLASS_NAME = ExcelExporter.class.getName();

    public static File export() {

        File file = null;
        try {
            // get SD environment
            File sd = Environment.getExternalStorageDirectory();

            // get file name to create
            StringBuilder builder = new StringBuilder();
            builder.append(Strings.getStringByRId(R.string.app_name));
            builder.append("-");
            builder.append(UtilsRG.dayAndhourFormat.format(new Date()));
            builder.append(".xls");
            String fileName = builder.toString();

            // get directory
            File baseDirectory = new File(sd.getAbsolutePath());
            File directory = new File(baseDirectory, Strings.getStringByRId(R.string.app_name));

            //create directory if not exist
            boolean isDirectoryCreated = directory.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = directory.mkdir();
            }

            if (isDirectoryCreated) {

                // create file path
                file = new File(directory, fileName);

                // create workbook containing sheets later on
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale(Locale.GERMAN.getLanguage(), Locale.GERMAN.getCountry()));
                WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);

                // cell format
                WritableCellFormat headerCellFormat = new WritableCellFormat();
                headerCellFormat.setAlignment(Alignment.CENTRE);
                headerCellFormat.setFont(
                        new WritableFont(
                                WritableFont.ARIAL,
                                10, WritableFont.BOLD,
                                false,
                                UnderlineStyle.NO_UNDERLINE,
                                jxl.format.Colour.BLACK));


                // create reaction times sheet
                WritableSheet reactionTimeSheet = workbook.createSheet(Strings.getStringByRId(R.string.sessions), 0);

                // header items
                List<String> reactionTimeHeaderList = Arrays.asList(Strings.getStringByRId(R.string.name), Strings.getStringByRId(R.string.age),
                        Strings.getStringByRId(R.string.gender), Strings.getStringByRId(R.string.creation_time), Strings.getStringByRId(R.string.operation_type),
                        Strings.getStringByRId(R.string.estimation_of_alertness), Strings.getStringByRId(R.string.brain_temperature),
                        Strings.getStringByRId(R.string.average), Strings.getStringByRId(R.string.median),
                        Strings.getStringByRId(R.string.reaction_times));

                reactionTimeSheet = createSheetHeader(reactionTimeSheet, headerCellFormat, reactionTimeHeaderList);

                // create events sheet
                WritableSheet eventSheet = workbook.createSheet(Strings.getStringByRId(R.string.events), 1);
                //TODO: fix this
                //eventSheet = ExcelExporter.addHadsdSheetHeader(eventSheet, headerCellFormat);

                // go through all user in database
                for (MedicalUser user : new MedicalUserManager(App.getAppContext()).getAllMedicalUsers()) {

                    for (OperationIssue operationIssue : new OperationIssueManager(App.getAppContext()).getAllOperationIssuesByMedicoId(user.getMedicalId())) {

                        // cell format
                        WritableCellFormat cellFormat = new WritableCellFormat();
                        cellFormat.setAlignment(Alignment.CENTRE);

                        //add data to distress thermometer sheet
                        reactionTimeSheet = ExcelExporter.getReactionTimesWorksheet(user, operationIssue, reactionTimeSheet, App.getAppContext(), cellFormat);

                        // add HADS-D sheet
                        //eventSheet = ExcelExporter.getHadsdWorksheet(user, eventSheet, App.getAppContext(), cellFormat);

                        autoFitColumnsByWritableSheets(reactionTimeSheet, eventSheet);
                    }
                }


                // close workbook
                workbook.write();
                workbook.close();

                Log.d(CLASS_NAME, "excel created successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * Auto fits every column in the sheets
     *
     * @param sheets the sheets to use
     */
    private static void autoFitColumnsByWritableSheets(WritableSheet... sheets) {
        for (WritableSheet sheet : sheets) {

            //
            WritableCellFormat newFormat;
            for (int j = 0; j < sheet.getColumns(); j++) {
                for (int k = 0; k < sheet.getRows(); k++) {
                    Cell readCell = sheet.getCell(j, k);
                    WritableCellFormat cellFormatObj = new WritableCellFormat();
                    CellFormat readFormat = readCell.getCellFormat() == null ? cellFormatObj : readCell.getCellFormat();
                    newFormat = new WritableCellFormat(readFormat);
                    try {
                        newFormat.setAlignment(Alignment.CENTRE);
                    } catch (WriteException e) {
                        Log.e(CLASS_NAME, e.getLocalizedMessage());
                    }
                }
            }
            //

            for (int i = 0; i < sheet.getColumns(); i++) {
                Cell[] cells = sheet.getColumn(i);
                int longestStrLen = -1;

                if (cells.length == 0)
                    continue;

                /* Find the widest cell in the column. */
                for (int j = 0; j < cells.length; j++) {
                    if (cells[j].getContents().length() > longestStrLen) {
                        String str = cells[j].getContents();
                        if (str == null || str.isEmpty())
                            continue;
                        longestStrLen = str.trim().length();
                    }
                }

                // If not found, skip the column.
                if (longestStrLen == -1)
                    continue;

                // If wider than the max width, crop width
                if (longestStrLen > 255)
                    longestStrLen = 255;
                CellView cv = sheet.getColumnView(i);

                // Every character is 256 units wide, so scale it
                cv.setSize(longestStrLen * 256 + 100);
                sheet.setColumnView(i, cv);
            }
        }
    }

    /**
     * @param sheet       the sheet to be used for the header
     * @param cellFormat  the cell format to use
     * @param headerItems the items to be used as header items
     * @return the sheet containing first row with header information
     */
    private static WritableSheet createSheetHeader(WritableSheet sheet, WritableCellFormat cellFormat, List<String> headerItems) {
        if (sheet != null) {
            try {

                for (int index = 0; index < headerItems.size(); index++) {
                    sheet.addCell(new Label(index, 0, headerItems.get(index), cellFormat));
                }

            } catch (WriteException e) {
                Log.e(CLASS_NAME, e.getLocalizedMessage());
            }
        }
        return sheet;
    }

    /**
     * Fills excel sheet with distress thermometer data
     *
     * @param sheet the sheet to fill with data
     * @param ctx   the context to use
     * @return the excel sheet containing distress thermometer data
     */
    public static WritableSheet getReactionTimesWorksheet(MedicalUser user, OperationIssue operationIssue, WritableSheet sheet, Context ctx, WritableCellFormat cellFormat) {

        if (sheet != null) {
            try {

                ReactionGameManager gameManager = new ReactionGameManager(ctx);
                List<ReactionGame> gameList = gameManager.getAllReactionGameList(operationIssue.getDisplayName(), "ASC");

                for (ReactionGame game : gameList) {

                    int rowSize = sheet.getRows();
                    sheet.addCell(new Label(0, rowSize, user.getMedicalId(), cellFormat));
                    sheet.addCell(new Label(1, rowSize, String.valueOf(user.getAge()), cellFormat));
                    sheet.addCell(new Label(2, rowSize, String.valueOf(user.getGender()), cellFormat));
                    sheet.addCell(new Label(3, rowSize, String.valueOf(game.getCreationDate()), cellFormat));
                    sheet.addCell(new Label(4, rowSize, String.valueOf(game.getTestType()), cellFormat));
                    sheet.addCell(new Label(5, rowSize, String.valueOf(game.getPatientsAlertnessFactor()), cellFormat));
                    sheet.addCell(new Label(6, rowSize, String.valueOf(game.getBrainTemperature()), cellFormat));
                    sheet.addCell(new Label(7, rowSize, String.valueOf(game.getAverageReactionTimeFormatted()), cellFormat));
                    sheet.addCell(new Label(8, rowSize, String.valueOf(ReactionGame.parseSecondsToMilliSeconds(gameManager.getMedianReactionTime(game.getReactionGameId()))), cellFormat));

                    String medianPerformance = String.valueOf(ReactionGame.parseSecondsToMilliSeconds(gameManager.getMedianReactionTime(game.getReactionGameId())));

                    sheet.addCell(new Label(9, rowSize, medianPerformance, cellFormat));

                    double[] reactionTimes = game.getReactionTimesByDB();
                    // for each reaction time within a test
                    int startIndex = 10;
                    for (int k = 0; k < reactionTimes.length; k++) {
                        double rawReactionTime = reactionTimes[k];
                        sheet.addCell(new Label(startIndex + k, rowSize, ReactionGame.parseSecondsToMilliSeconds(rawReactionTime), cellFormat));
                    }
                }
                Log.d(CLASS_NAME, "exporting sheet: " + sheet.toString());


            } catch (WriteException e) {
                Log.e(CLASS_NAME, e.getLocalizedMessage());
            }
        }
        return sheet;
    }


}

