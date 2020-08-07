package io.CarbookingService.utils;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import io.CarbookingService.exceptionhandler.DataNotAllowedException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public interface CommonUtils {

	public static UnaryOperator<String> getFormattedCurrentDateTimeString = datePattern -> LocalDateTime.now().format(DateTimeFormatter.ofPattern(datePattern));
    public static BiFunction<LocalDateTime, String, String> getFormattedDateOfSpecificLocalDatetime = (localDateTime, s) -> localDateTime.format(DateTimeFormatter.ofPattern(s));
    public static BiFunction<LocalDateTime, LocalDateTime, Boolean> isDateBefore = LocalDateTime::isBefore;
    public static BiFunction<LocalDateTime, LocalDateTime, Boolean> isDateAfter = LocalDateTime::isAfter;
    public static BiFunction<LocalDateTime, LocalDateTime, Boolean> isDateEqual = LocalDateTime::isEqual;

    public static Function<String, Object> getLocalDateTimeFromDateString = dateString -> {
        LocalDateTime dateTime = null;
        try {
            dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (Exception e) {
            try {
                throw new DataNotAllowedException("Date Format:" + dateString + " is not allowed. Please provide Date as 'yyyy-MM-ddThh:mm:ss' format");
            } catch (DataNotAllowedException dataNotAllowedException) {
                return dataNotAllowedException;
            }
        }
        return dateTime;
    };

    public static Function<String, Object> getDateTimeFromDateString = (String dateString) -> {
        DateTime dateTime = null;
        try {
            Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$");
            if (DATE_PATTERN.matcher(dateString).matches()) {
                dateTime = DateTime.parse(dateString);
            }
        } catch (Exception e) {
            try {
                throw new DataNotAllowedException("Date Format:" + dateString + " is not allowed. Please provide Date as 'yyyy-MM-ddThh:mm:ss' format");
            } catch (DataNotAllowedException dataNotAllowedException) {
                return dataNotAllowedException;
            }
        }
        return dateTime;
    };

    public static DateTime convertStringToDateTime(String requestedDateString) {
        DateTime dateTime = null;
        try {
            Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$");
            if (DATE_PATTERN.matcher(requestedDateString).matches()) {
                dateTime = DateTime.parse(requestedDateString);
            }
        } catch (Exception e) {

        }
        return dateTime;
    }

    public static boolean isDateOverlap(String requestedStartDate, String requestedEndDate, String bookedStartDate, String bookedEndDate) {
        Interval requestedDatesInterval = new Interval(convertStringToDateTime(requestedStartDate), convertStringToDateTime(requestedEndDate));
        Interval bookedDatesInterval = new Interval(convertStringToDateTime(bookedStartDate), convertStringToDateTime(bookedEndDate));
        boolean result = requestedDatesInterval.overlaps(bookedDatesInterval);
        return result;
    }

    public static BiFunction<LocalDateTime, LocalDateTime, Long> getHours = (startDateTime, endDateTime) -> Duration.between(startDateTime, endDateTime).toHours();

    public static Supplier<Integer> getRandomUUID = () -> UUID.randomUUID().hashCode();

    public static <T> Object generateReport(String jasperReportSource, String reportFormatType, String reportPath, List<T> repository, String reportName){
            try {
                JasperReport jasperReport= JasperCompileManager.compileReport(jasperReportSource);
                JRBeanCollectionDataSource dataSource=new JRBeanCollectionDataSource(repository);
                Map<String,Object> reportParameters=new HashMap<>();
                reportParameters.put("createdBy","Infor-Car-Portal");
                JasperPrint jasperPrint= JasperFillManager.fillReport(jasperReport,reportParameters,dataSource);
                switch(reportFormatType.toLowerCase()){
                    case "html":
                        File htmlReportFile=new File(reportPath);
                        if(!htmlReportFile.exists()){
                            htmlReportFile.mkdirs();
                        }
                        JasperExportManager.exportReportToHtmlFile(jasperPrint, htmlReportFile+File.separator+reportName+".html");
                        break;
                    case "pdf":
                        File pdfReportFile=new File(reportPath);
                        if(!pdfReportFile.exists()){
                            pdfReportFile.mkdirs();
                        }
                        JasperExportManager.exportReportToHtmlFile(jasperPrint,pdfReportFile+File.separator+reportName+".pdf");
                        break;
                    default:
                        break;
                }
            }catch(JRException e){
                return "Sorry! Exception:"+e.getMessage();
            }
            return null;
        }

    public static Supplier<Integer> getRandomData = () -> (int) Math.round(Math.random() * 99999);

    public static UnaryOperator<LocalDateTime> getRandomLocalDateTime = localDateTime -> localDateTime.plusDays(new Random().nextInt((20 - 2 + 1) )+ 1);

    public static UnaryOperator<LocalDateTime> getRandomLocalDateTimeShortRange = localDateTime -> localDateTime.plusDays(new Random().nextInt((10 - 5 + 1) )+ 5);

    public static BinaryOperator<Double> getRandomDoubleWithinRange = (minLimit, maxLimit) -> minLimit + new Random().nextDouble() * (maxLimit - minLimit);
}	
