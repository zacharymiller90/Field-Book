package com.fieldbook.tracker;

import android.database.Cursor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Custom CSV Writing Class
 * V2 - WriteFile and WriteFile2 have been amended
 * Columns are now selectable
 */
public class CSVWriter {

    private PrintWriter pw;
    private char separator;
    private char quotechar;
    private char escapechar;
    private String lineEnd;
    private Cursor curCSV;

    /**
     * The character used for escaping quotes.
     */
    public static final char DEFAULT_ESCAPE_CHARACTER = '"';

    /**
     * The default separator to use if none is supplied to the constructor.
     */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';

    /**
     * The quote constant to use when you wish to suppress all quoting.
     */
    public static final char NO_QUOTE_CHARACTER = '\u0000';

    /**
     * The escape constant to use when you wish to suppress all escaping.
     */
    public static final char NO_ESCAPE_CHARACTER = '\u0000';

    /**
     * Default line terminator uses platform encoding.
     */
    public static final String DEFAULT_LINE_END = "\n";

    /**
     * Constructs CSVWriter using a comma for the separator.
     */
    public CSVWriter(Writer writer, Cursor c) {
        this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END, c);
    }

    /**
     * Constructs CSVWriter with supplied separator, quote char, escape char and
     * line ending.
     */
    public CSVWriter(Writer writer, char separator, char quotechar,
                     char escapechar, String lineEnd, Cursor c) {
        this.pw = new PrintWriter(writer);
        this.separator = separator;
        this.quotechar = quotechar;
        this.escapechar = escapechar;
        this.lineEnd = lineEnd;
        curCSV = c;
    }

    /**
     * Outputs the field book; First Name and Last name does not exist in
     * database, and so is passed in as a parameter
     * V2 - Range added, columns selectable
     */
    public void writeDatabaseFormat(ArrayList<String> range, String person, String location) throws Exception {
        // Simply loop through all items
        if (curCSV.getCount() > 0) {

            range.add("trait");
            range.add("value");
            range.add("person");
            range.add("timestamp");
            range.add("location");

            String[] labels = range.toArray(new String[range.size()]);

            writeNext(labels);

            curCSV.moveToPosition(-1);

            while (curCSV.moveToNext()) {
                String arrStr[] = new String[labels.length];

                for (int i = 0; i < labels.length - 5; i++) {
                    arrStr[i] = curCSV.getString(i);
                }

                int count = 0;

                for (int i = labels.length - 5; i < labels.length; i++) {
                    if (count == 0) {
                        arrStr[i] = curCSV.getString(i);
                    } else if (count == 1) {
                        arrStr[i] = curCSV.getString(i);
                    } else if (count == 2)
                        arrStr[i] = person;
                    else if (count == 4)
                        arrStr[i] = location;
                    else
                        arrStr[i] = curCSV.getString(curCSV.getColumnCount() - 1);

                    count += 1;
                }

                writeNext(arrStr);
            }
        }

        curCSV.close();
        close();
    }

    /**
     * Exports traits table
     */
    public void writeTraitFile(String[] labels) throws Exception {
        if (curCSV.getCount() > 0) {

            writeNext(labels);

            curCSV.moveToPosition(-1);

            while (curCSV.moveToNext()) {

                String arrStr[] = new String[labels.length];

                for (int i = 0; i < arrStr.length; i++) {
                    arrStr[i] = curCSV.getString(i);
                }

                writeNext(arrStr);
            }
        }

        curCSV.close();
        close();
    }

    /**
     * Generates data in an table style format
     */
    public void writeTableFormat(String[] labels, int rangeTotal) throws Exception {
        if (curCSV.getCount() > 0) {

            writeNext(labels);

            curCSV.moveToPosition(-1);

            while (curCSV.moveToNext()) {

                String arrStr[] = new String[labels.length];

                for (int k = 0; k < rangeTotal; k++)
                    arrStr[k] = curCSV.getString(k);

                // Get matching values for every row in the Range table
                for (int k = rangeTotal; k < labels.length; k++) {
                    arrStr[k] = curCSV.getString(k);
                }

                writeNext(arrStr);
            }
        }

        curCSV.close();
        close();
    }

    /**
     * Writes the next line to the file.
     */
    public void writeNext(String[] nextLine) {

        if (nextLine == null)
            return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nextLine.length; i++) {

            if (i != 0) {
                sb.append(separator);
            }

            String nextElement = nextLine[i];

            if (nextElement == null)
                continue;

            if (quotechar != NO_QUOTE_CHARACTER)
                sb.append(quotechar);

            for (int j = 0; j < nextElement.length(); j++) {
                char nextChar = nextElement.charAt(j);

                if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
                    sb.append(escapechar).append(nextChar);
                } else if (escapechar != NO_ESCAPE_CHARACTER
                        && nextChar == escapechar) {
                    sb.append(escapechar).append(nextChar);
                } else {
                    sb.append(nextChar);
                }
            }

            if (quotechar != NO_QUOTE_CHARACTER)
                sb.append(quotechar);
        }

        sb.append(lineEnd);
        pw.write(sb.toString());

    }

    /**
     * Close the underlying stream writer flushing any buffered content.
     */
    public void close() throws IOException {
        pw.flush();
        pw.close();
    }
}
