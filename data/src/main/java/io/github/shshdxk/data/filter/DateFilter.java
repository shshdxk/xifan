package io.github.shshdxk.data.filter;

import com.google.common.base.Strings;
import io.github.shshdxk.common.TimeUtils;
import io.github.shshdxk.data.specification.Operator;
import io.github.shshdxk.data.specification.Specifications;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
@Getter
@Setter
public abstract class DateFilter<T> implements SearchFilter<T> {

    private final String field;
    private Operator operator;
    private Date date;

    static final Pattern pTime = Pattern.compile("(?<num>\\d+)(?<unit>h|d|w|m)");

    private final String original;

//    private int num;
//    private String unit;

    public DateFilter(String field, String param) {
        this.original = param;
        this.field = field;

        String str = param;
        if (str.startsWith(">=")) {
            operator = Operator.ge;
            str = str.substring(2);
        } else if (str.startsWith("<=")) {
            operator = Operator.le;
            str = str.substring(2);
        } else if (str.startsWith(">")) {
            operator = Operator.gt;
            str = str.substring(1);
        } else if (str.startsWith("<")) {
            operator = Operator.lt;
            str = str.substring(1);
        } else if (str.startsWith("=")) {
            operator = Operator.eq;
            str = str.substring(1);
        } else {
            operator = Operator.eq;
        }

        str = str.replace("\"", "");
        DateTime today = new DateTime().withTimeAtStartOfDay();

        Matcher m = pTime.matcher(str.toLowerCase());
        boolean duration = false;
        if (m.find()) {
            duration = true;
            int num = Integer.parseInt(m.group("num"));
            String unit = m.group("unit");

            if ("h".equalsIgnoreCase(unit)) {
                date = today.minusHours(num).toDate();
            } else if ("d".equalsIgnoreCase(unit)) {
                date = today.minusDays(num).toDate();
            } else if ("w".equalsIgnoreCase(unit)) {
                date = today.minusWeeks(num).toDate();
            } else if ("m".equalsIgnoreCase(unit)) {
                date = today.minusMonths(num).toDate();
            } else {
                date = null;
            }
        } else {
            date = TimeUtils.convert(str);
        }

        if (duration) {
            if (operator == Operator.ge) {
                operator = Operator.le;
            } else if (operator == Operator.gt) {
                operator = Operator.lt;
            } else if (operator == Operator.le) {
                operator = Operator.ge;
            } else if (operator == Operator.lt) {
                operator = Operator.gt;
            }
        }
    }

    @Override
    public Specification<T> toSpecification() {
        if (date == null || operator == null) {
            return null;
        }

        return switch (operator) {
            case eq -> Specifications.between(field, date, DateUtils.addDays(date, 1));
            case ge -> Specifications.ge(field, date);
            case gt -> Specifications.gt(field, date);
            case le -> Specifications.le(field, date);
            case lt -> Specifications.lt(field, date);
        };

    }

    @Override
    public String toExpression() {
        return original;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof DateFilter)) {
            return false;
        }

        DateFilter rhs = (DateFilter) other;
        return Objects.equals(field, rhs.field)
                && Objects.equals(operator, rhs.operator)
                && Objects.equals(date, rhs.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, operator, date);
    }

    @Override
    public String toString() {
        return toExpression();
    }

    @Override
    public boolean isValid() {
        return !Strings.isNullOrEmpty(field) &&
                operator != null &&
                date != null;
    }
}
