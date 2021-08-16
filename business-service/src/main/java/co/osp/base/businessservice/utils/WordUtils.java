package co.osp.base.businessservice.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class WordUtils {
    public void fromTemplate(String templateFile, String outputFile, Map<String, String> maps) throws IOException
    {
        Path path = Paths.get(templateFile);
        byte[] byteData = Files.readAllBytes(path);
        XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(byteData));
        iterateThroughParagraphs(document, maps);
        iterateThroughTables(document, maps);
        iterateThroughFooters(document, maps);
        document.write(new FileOutputStream(outputFile));
        document.close();
    }
    private void iterateThroughParagraphs(XWPFDocument doc, Map<String, String> fieldsForReport)
    {
        for (XWPFParagraph paragraph : doc.getParagraphs())
        {
            iterateThroughRuns(doc, paragraph, fieldsForReport);
        }
    }

    private void iterateThroughTables(XWPFDocument doc, Map<String, String> fieldsForReport)
    {
        for (XWPFTable tbl : doc.getTables())
        {
            for (XWPFTableRow row : tbl.getRows())
            {
                for (XWPFTableCell cell : row.getTableCells())
                {
                    for (XWPFParagraph paragraph : cell.getParagraphs())
                    {
                        iterateThroughRuns(doc, paragraph, fieldsForReport);
                    }
                }
            }
        }
    }

    private void iterateThroughFooters(XWPFDocument doc, Map<String, String> fieldsForReport)
    {
        for (XWPFFooter footer : doc.getFooterList())
        {
            for (XWPFParagraph paragraph : footer.getParagraphs())
            {
                iterateThroughRuns(doc, paragraph, fieldsForReport);
            }
        }
    }

    private void iterateThroughRuns(XWPFDocument document, XWPFParagraph paragraph, Map<String, String> fieldsForReport)
    {
        List<XWPFRun> runs = paragraph.getRuns();

        if (runs != null) {
            int runsSize = runs.size();

            for (int index = 0; index < runsSize; index++) {
                XWPFRun currentRun = runs.get(index);
                String text = currentRun.getText(0);
//System.out.println("Text: " + text);
                if (text != null && text.contains("#")) {
                    int skipIndex = replaceText(document, paragraph, index, runs, fieldsForReport);
                    index += skipIndex;
                }
            }
        }
    }

    private int replaceText(XWPFDocument document, XWPFParagraph paragraph, int index, List<XWPFRun> runs, Map<String, String> maps)
    {
        XWPFRun currentRun = runs.get(index);
        String text = currentRun.getText(0);

        long count = text.chars().filter(ch -> ch == '#').count();
        long left = count;
        for (int numb = 0; numb < count; numb+=2) {
            left = count - numb;
            if (left > 1) {
                int sIdx = text.indexOf("#");
                int eIdx = text.indexOf("#", sIdx + 1);
                String replacedText = text.substring(sIdx, eIdx).replace("#", "").toLowerCase();

                if (!maps.containsKey(replacedText)) continue;
                text = text.replace("#" + replacedText + "#", maps.get(replacedText));
            }
        }
        if (left==1){
            int idx = text.indexOf("#");
            String oriText = text.substring(0, idx);
            String endText = "";
            String replacedText = text.substring(idx).replace("#", "").toLowerCase();
            int lastIdx = index + 1;
            while(lastIdx < runs.size())
            {
                XWPFRun nxtRun = runs.get(lastIdx);
                if(nxtRun.getText(0).contains("#")) {
                    int eIdx = nxtRun.getText(0).indexOf("#");
                    replacedText += nxtRun.getText(0).substring(0, eIdx).replace("#", "").toLowerCase();
                    endText = nxtRun.getText(0).substring(eIdx + 1);
//System.out.println("Replaced: " + replacedText);
                    if(!maps.containsKey(replacedText)) return lastIdx - index;
                    break;
                }
                replacedText += nxtRun.getText(0);
                lastIdx++;
            }

//            currentRun.setText(oriText + maps.get(replacedText), 0);
//            for(int i = index + 1; i<=lastIdx; i++){
//                runs.get(i).setText("", 0);
//            }

            /// In the case of multiple lines
            String[] lines = maps.get(replacedText).split("\n");
            if (lines.length>1) {
                for (int i = 0; i < lines.length; i++) {
                    // For every run except last one, add a carriage return.
                    String textForLine = lines[i];
                    if (i == lines.length - 1) {
                        currentRun.setText(textForLine, 0);
                        currentRun.addCarriageReturn();
                    } else {
                        paragraph.insertNewRun(index + i);
                        XWPFRun newRun = paragraph.getRuns().get(i);
                        CTRPr rPr = newRun.getCTR().isSetRPr() ? newRun.getCTR().getRPr() : newRun.getCTR().addNewRPr();
                        rPr.set(currentRun.getCTR().getRPr());
                        newRun.setText(textForLine);
                        newRun.addCarriageReturn(); //ADD THE NEW LINE
                    }
                }
                runs = paragraph.getRuns();
                for (int i = index + 1; i <= lastIdx; i++) {
                    runs.get(lines.length + i - 1).setText("", 0);
                }
            }else {
                currentRun.setText(oriText + maps.get(replacedText) + endText, 0);
                for (int i = index + 1; i <= lastIdx; i++) {
                    runs.get(i).setText("", 0);
                }
            }
            return lastIdx - index;
        }

//        currentRun.setText(text, 0);
        /// In the case of multiple lines
        String[] lines = text.split("\n");
        if (lines.length>1) {
            for (int i = 0; i < lines.length; i++) {
                // For every run except last one, add a carriage return.
                String textForLine = lines[i];
                if (i == lines.length - 1) {
                    currentRun.setText(textForLine, 0);
                    currentRun.addCarriageReturn();
                } else {
                    paragraph.insertNewRun(index + i);
                    XWPFRun newRun = paragraph.getRuns().get(i);
                    CTRPr rPr = newRun.getCTR().isSetRPr() ? newRun.getCTR().getRPr() : newRun.getCTR().addNewRPr();
                    rPr.set(currentRun.getCTR().getRPr());
                    newRun.setText(textForLine);
                    newRun.addCarriageReturn(); //ADD THE NEW LINE
                }
            }
        }else {
            currentRun.setText(text, 0);
        }

        return 0;
    }
}
