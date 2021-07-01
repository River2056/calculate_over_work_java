import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainApp {
    public static final DateFormat HH_MM = new SimpleDateFormat("HH:mm");
    public static final String MONTH = "06";

    public static void outputToConsoleAndFile(PrintWriter pw, String message) {
        System.out.println(message);
        pw.println(message);
    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        String fileName = String.format("workTime_%s.txt", MONTH);
        InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
        String line = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        File outputDirectory = new File("./output");
        if(!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }

        String outputFileName = String.format("workTime_output_%s.txt", MONTH);
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new FileOutputStream(new File(outputDirectory + File.separator + outputFileName)));
            final Date startWorkTime = HH_MM.parse("09:00");
            final Date endWorkTime = HH_MM.parse("18:30");
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            int sumOverWorkedHours = 0;
            String[] data = sb.toString().split("\n");
            for(String s: data) {
                String[] day = s.split(",");
                int month = Integer.parseInt(day[0].substring(0, 2));
                int date = Integer.parseInt(day[0].substring(2));
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DATE, date);
                Date actualArriveTime = HH_MM.parse(day[1]);
                Date actualLeaveTime = HH_MM.parse(day[2]);
                long diff = actualArriveTime.getTime() - startWorkTime.getTime();
                long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);

                // actual leave time
                outputToConsoleAndFile(pw, String.format("date: %s", day[0]));
                outputToConsoleAndFile(pw, String.format("actual arrive time: %s", HH_MM.format(actualArriveTime)));
                outputToConsoleAndFile(pw, String.format("actual leave time: %s", HH_MM.format(actualLeaveTime)));
                if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    long calDiff = actualLeaveTime.getTime() - actualArriveTime.getTime();
                    long min = TimeUnit.MILLISECONDS.toMinutes(calDiff);
                    outputToConsoleAndFile(pw, "is Saturday, only cal diff");
                    outputToConsoleAndFile(pw, String.format("overWorked hours in minutes: %s", min));
                    outputToConsoleAndFile(pw, String.format("hours: %s", min / 60));
                    sumOverWorkedHours += (min / 60);
                    outputToConsoleAndFile(pw, "\n");
                } else {
                    long leaveTime = endWorkTime.getTime() + diff;
                    Date newLeaveDate = new Date(leaveTime);

                    outputToConsoleAndFile(pw, String.format("calculate leave time: %s", HH_MM.format(newLeaveDate)));
                    long calculateWorkHours = actualLeaveTime.getTime() - leaveTime;
                    long min = TimeUnit.MILLISECONDS.toMinutes(calculateWorkHours);
                    outputToConsoleAndFile(pw, String.format("overWorked hours in minutes: %s", min));
                    outputToConsoleAndFile(pw, String.format("hours: %s", min / 60));
                    sumOverWorkedHours += (min / 60);
                    outputToConsoleAndFile(pw, "\n");
                }
            }
            outputToConsoleAndFile(pw, String.format("total overWorked hours: %s%n", sumOverWorkedHours));
            pw.flush();
            pw.close();
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}
