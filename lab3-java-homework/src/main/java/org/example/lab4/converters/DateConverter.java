package org.example.lab4.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@FacesConverter("dateConverter")
public class DateConverter implements Converter {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // return null for empty or null input
        }

        try {
            // Parse the date string into a java.util.Date
            java.util.Date utilDate = dateFormat.parse(value);
            // Convert java.util.Date to java.sql.Date
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return ""; // Return empty string if the value is null
        }

        if (value instanceof Date) {
            // If it's java.sql.Date, format it as a string
            return dateFormat.format((Date) value);
        } else if (value instanceof java.util.Date) {
            // If it's java.util.Date, convert it to java.sql.Date first, then format it
            return dateFormat.format(new Date(((java.util.Date) value).getTime()));
        } else {
            throw new RuntimeException("Value is not a java.sql.Date or java.util.Date: " + value);
        }
    }
}
