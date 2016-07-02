package com.rbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.lexicalunit.nanodbc.Connection;
import com.lexicalunit.nanodbc.Nanodbc;
import com.lexicalunit.nanodbc.Result;

public class RPrompt {

    private static final String PROMPT = "R> ";

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No DSN specified");
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                Connection connection = Nanodbc.newConnection("DSN=" + args[0], 0L)) {
            repl(reader, connection);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void repl(BufferedReader reader, Connection connection) throws IOException {
        System.out.print(PROMPT);
        String line = reader.readLine();
        while (line != null) {
            if (line.trim().isEmpty()) {
                break;
            }
            try (Result result = connection.execute(line, 1, 0L)) {
                printResults(result);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
            System.out.print(PROMPT);
            line = reader.readLine();
        }
    }

    private static void printResults(Result result) {
        short cols = result.getNumColumns();
        while (result.next()) {
            for (short c = 0; c < cols; c++) {
                String value = !result.isNull(c) ? result.getString(c) : "<null>";
                System.out.print(result.getColumnName(c) + "=" + value + " ");
            }
            System.out.println();
        }
    }

}
