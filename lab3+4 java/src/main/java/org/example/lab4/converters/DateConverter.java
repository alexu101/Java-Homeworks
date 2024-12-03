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
        try {
            java.util.Date utilDate = dateFormat.parse(value);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Date) {
            return dateFormat.format((Date) value);
        } else {
            throw new RuntimeException("Value is not a java.sql.Date: " + value);
        }
    }
}