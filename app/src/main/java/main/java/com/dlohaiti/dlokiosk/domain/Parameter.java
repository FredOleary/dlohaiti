package main.java.com.dlohaiti.dlokiosk.domain;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.upperCase;

public class Parameter implements Comparable<Parameter> {
    private final String name;
    private final String unitOfMeasure;
    private final BigDecimal minimum;
    private final BigDecimal maximum;
    private final boolean hasRange;
    private final boolean isOkNotOk;
    private final Integer priority;

    public Parameter(String name, String unitOfMeasure, String minimum, String maximum, boolean isOkNotOk, Integer priority) {
        this.name = defaultString(name);
        this.unitOfMeasure = unitOfMeasure;
        this.minimum = parseBigDecimal(minimum, null);
        this.maximum = parseBigDecimal(maximum, null);
        this.hasRange = StringUtils.isNotBlank(minimum) && StringUtils.isNotBlank(maximum);
        this.isOkNotOk = isOkNotOk;
        this.priority = ObjectUtils.defaultIfNull(priority, Integer.MAX_VALUE);
    }

    private BigDecimal parseBigDecimal(String candidate, BigDecimal defaultValue) {
        if(NumberUtils.isNumber(candidate)) {
            return new BigDecimal(candidate).setScale(2);
        }
        return defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public boolean hasRange() {
        return hasRange;
    }

    public boolean isOkNotOk() {
        return isOkNotOk;
    }

    public Integer getPriority() {
        return priority;
    }


    public CharSequence getRange() {
        return minimum.toString() + " - " + maximum.toString();
    }

    public boolean considersInvalid(String value) {
        if(!NumberUtils.isNumber(value)) {
            return true;
        }
        BigDecimal val = new BigDecimal(value);
        return lessThan(val, BigDecimal.ZERO) || hasRange() && (lessThan(val, minimum) || greaterThan(val, maximum));
    }

    private static boolean lessThan(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) < 0;
    }

    private static boolean greaterThan(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) > 0;
    }

    @Override public int compareTo(Parameter parameter) {
        if(priority < parameter.priority) {
            return -1;
        }
        if(priority > parameter.priority) {
            return 1;
        }
        return upperCase(name).compareTo(upperCase(parameter.name));
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (hasRange != parameter.hasRange) return false;
        if (isOkNotOk != parameter.isOkNotOk) return false;
        if (maximum != null ? !maximum.equals(parameter.maximum) : parameter.maximum != null) return false;
        if (minimum != null ? !minimum.equals(parameter.minimum) : parameter.minimum != null) return false;
        if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
        if (priority != null ? !priority.equals(parameter.priority) : parameter.priority != null) return false;
        if (unitOfMeasure != null ? !unitOfMeasure.equals(parameter.unitOfMeasure) : parameter.unitOfMeasure != null)
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (unitOfMeasure != null ? unitOfMeasure.hashCode() : 0);
        result = 31 * result + (minimum != null ? minimum.hashCode() : 0);
        result = 31 * result + (maximum != null ? maximum.hashCode() : 0);
        result = 31 * result + (hasRange ? 1 : 0);
        result = 31 * result + (isOkNotOk ? 1 : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        return result;
    }
}
