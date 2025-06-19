package io.github.shshdxk.data.specification;


import com.google.common.collect.Lists;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class OrderBy<T> implements Specification<T> {
    /**
     * 排序
     */
    private final Sort sort;

    public OrderBy(Sort sort) {
        this.sort = sort;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Order> orders;
        if (query.getOrderList() == null) {
            orders = Lists.newArrayList();
        } else {
            orders = Lists.newArrayList(query.getOrderList());
        }

        this.sort.forEach((order) -> {
            Path<?> path = root.get(order.getProperty());
            if (order.isAscending()) {
                orders.add(cb.asc(path));
            } else {
                orders.add(cb.desc(path));
            }

        });
        query.orderBy(orders);
        return null;
    }
}
