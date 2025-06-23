package io.github.shshdxk.data.filter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public abstract class SearchCriteria<T> extends CompositeFilter<T> {

    /**
     * qualifierRegex
     */
    protected final Pattern qualifierRegex;

    /**
     * getQualifiers
     * @return qualifiers
     */
    abstract protected List<String> getQualifiers();
    /**
     * 创建一个过滤器
     * @param input input
     * @return 过滤器
     */
    abstract protected SearchFilter<T> createFilter(String input);

    /**
     * createContainFilter
     * @param contains contains
     * @return containFilter
     */
    abstract protected SearchFilter<T> createContainFilter(Set<String> contains);

    /**
     * 构造一个搜索条件
     */
    public SearchCriteria() {
        this(Logic.AND);
    }

    /**
     * 构造一个搜索条件
     * @param logic logic
     */
    public SearchCriteria(Logic logic) {
        super(logic);
        this.qualifierRegex = buildQualifierRegex();
    }

    private static class SplitResult {
        List<String> qualifiers = Lists.newArrayList();
        List<String> contains = Lists.newArrayList();
    }

    /**
     * parse
     * @param input input
     */
    protected void parse(String input) {
        if (Strings.isNullOrEmpty(input)) {
            return;
        }

        SplitResult result = split(input);

        List<String> contains = result.contains;

        for (String each : result.qualifiers) {
            SearchFilter<T> filter = createFilter(each);
            if (filter != null && filter.isValid()) {
                addFilter(filter);
            } else {
                contains.add(each);
            }
        }

        if (!contains.isEmpty()) {
            addFilter(createContainFilter(Sets.newLinkedHashSet(contains)));
        }

    }

    private enum State {
        START,
        CONTAINS_QUOTE,
        PARAM_QUOTE,
    }

    // split the query into parts
    private SplitResult split(String input) {
        String[] tokens = Iterables.toArray(
                Splitter.on(" ").trimResults().omitEmptyStrings().split(input),
                String.class);

        SplitResult result = new SplitResult();

        String current = "";
        State state = State.START;

        for (String each : tokens) {
            Matcher m = qualifierRegex.matcher(each);
            boolean isParam = m.matches();

            if (isParam) {
                // end previous first
                switch (state) {
                    case START:
                        break;

                    case CONTAINS_QUOTE:
                        result.contains.add(current);
                        current = "";
                        break;

                    case PARAM_QUOTE:
                        result.qualifiers.add(current);
                        current = "";
                        break;
                }

                if (each.contains("\"") && !each.endsWith("\"")) {
                    state = State.PARAM_QUOTE;
                    current = each;
                } else {
                    result.qualifiers.add(each);
                }
            } else {
                // for a general word
                switch (state) {
                    case START:
                        if (each.startsWith("\"") && !each.endsWith("\"")) {
                            state = State.CONTAINS_QUOTE;
                            current = each;
                        } else {
                            result.contains.add(each);
                        }
                        break;

                    case CONTAINS_QUOTE:
                        if (each.startsWith("\"")) {
                            // end previous
                            result.contains.add(current);
                            current = "";

                            if (each.endsWith("\"")) {
                                result.contains.add(each);
                                state = State.START;
                            } else {
                                state = State.CONTAINS_QUOTE;
                                current = each;
                            }
                        } else if (each.endsWith("\"")) {
                            result.contains.add(current + " " + each);
                            current = "";
                            state = State.START;
                        } else {
                            current = current + " " + each;
                        }
                        break;

                    case PARAM_QUOTE:
                        if (each.endsWith("\"")) {
                            result.qualifiers.add(current + " " + each);
                            current = "";
                            state = State.START;
                        } else {
                            current = current + " " + each;
                        }
                        break;
                }
            }
        }

        if (!current.isEmpty()) {
            if (state == State.PARAM_QUOTE) {
                result.qualifiers.add(current);
            } else {
                result.contains.add(current);
            }
        }

        return result;
    }

    /**
     * buildQualifierRegex
     * @return qualifierRegex
     */
    protected Pattern buildQualifierRegex() {
        List<String> qualifiers = getQualifiers();
        String valids = Joiner.on("|").join(qualifiers);

        return Pattern.compile(
            "(?i)^-?(?<qualifier>" + valids + "):(?<value>.+)",
            Pattern.UNICODE_CASE);
    }

    /**
     * main
     * @param args args
     */
    public static void main(String[] args) {
        String p = "^-?(?<qualifier>id|created|updated|ended|caller|saviour|address|status):(?<value>.+)";
        Pattern pattern = Pattern.compile(p, Pattern.UNICODE_CASE);
        String str = "status:OPENED";
        Matcher matcher = pattern.matcher(str);
        System.out.println(matcher.matches());
    }
}
