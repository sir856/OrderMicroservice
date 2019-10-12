package com.developing.shop.orders.service;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.Order;
import com.developing.shop.orders.model.Status;
import com.developing.shop.orders.repository.ChosenItemRepository;
import com.developing.shop.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class OrderServiceImpl implements OrderService {
    public String ORDER_BY = "orderBy";
    public String USERNAME = "userName";
    public String TOTAL_AMOUNT = "totalAmount";
    public String TOTAL_AMOUNT_FROM = "totalAmountFrom";
    public String TOTAL_AMOUNT_TO = "totalAmountTo";
    public String TOTAL_COST = "totalCost";
    public String TOTAL_COST_FROM = "totalCostFrom";
    public String TOTAL_COST_TO = "totalCostTo";
    public String STATUS = "status";


    private final EntityManager em;

    private CriteriaBuilder cb;
    private CriteriaQuery<Order> cq;

    private Map<String, Function<String, Predicate>> predicatesMap;
    private Map<String, javax.persistence.criteria.Order> ordersMap;

    private final OrderRepository orderRepository;

    private final ChosenItemRepository itemRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ChosenItemRepository itemRepository, EntityManager em) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;

        this.em = em;

        this.cb = em.getCriteriaBuilder();
        this.cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);

        predicatesMap = new HashMap<>();

        predicatesMap.put(USERNAME, name -> cb.like(root.get(USERNAME), name + "%"));
        predicatesMap.put(TOTAL_AMOUNT, amount -> cb.equal(root.get(TOTAL_AMOUNT), amount));
        predicatesMap.put(TOTAL_COST, price -> cb.equal(root.get(TOTAL_COST), price));
        predicatesMap.put(TOTAL_AMOUNT_FROM, amountFrom -> cb.ge(root.get(TOTAL_AMOUNT), Long.valueOf(amountFrom)));
        predicatesMap.put(TOTAL_COST_FROM, priceFrom -> cb.ge(root.get(TOTAL_COST), Long.valueOf(priceFrom)));
        predicatesMap.put(TOTAL_AMOUNT_TO, amountTo -> cb.le(root.get(TOTAL_AMOUNT), Long.valueOf(amountTo)));
        predicatesMap.put(TOTAL_COST_TO, priceTo -> cb.le(root.get(TOTAL_COST), Long.valueOf(priceTo)));
        predicatesMap.put(STATUS, status -> cb.equal(root.get(STATUS), Status.valueOf(status)));

        ordersMap = new HashMap<>();
        ordersMap.put(TOTAL_COST, cb.asc(root.get(TOTAL_COST)));
        ordersMap.put(TOTAL_AMOUNT, cb.asc(root.get(TOTAL_AMOUNT)));
        ordersMap.put(USERNAME, cb.asc(root.get(USERNAME)));
        ordersMap.put("-" + USERNAME, cb.desc(root.get(USERNAME)));
        ordersMap.put("-" + TOTAL_AMOUNT, cb.desc(root.get(TOTAL_AMOUNT)));
        ordersMap.put("-" + TOTAL_COST, cb.desc(root.get(TOTAL_COST)));
    }

    @Override
    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrders(Map<String, String> params) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        ArrayList<javax.persistence.criteria.Order> orders = new ArrayList<>();

        if (params != null) {

            if (params.containsKey(ORDER_BY)) {
                orders = getOrders(params.remove(ORDER_BY));
            }

            normalizeParams(params);

            for (String key : params.keySet() ) {
                if (predicatesMap.containsKey(key)) {
                    predicates.add(predicatesMap.get(key).apply(params.get(key)));
                }
            }
        }

        cq.where(predicates.toArray(new Predicate[0])).orderBy(orders.toArray(new javax.persistence.criteria.Order[0]));
        return em.createQuery(cq).getResultList();
    }

    private ArrayList<javax.persistence.criteria.Order> getOrders(String orders) {
        ArrayList<javax.persistence.criteria.Order> result = new ArrayList<>();

        for (String orderStr : orders.split(",")) {
            orderStr = orderStr.replaceAll("\\s", "");
            if (ordersMap.containsKey(orderStr)) {
                result.add(ordersMap.get(orderStr));
            }
        }
        return result ;
    }

    private void normalizeParams(Map<String, String> params) {
        if (params.containsKey(TOTAL_AMOUNT)) {
            params.remove(TOTAL_AMOUNT_FROM);
            params.remove(TOTAL_AMOUNT_TO);
        }

        if (params.containsKey(TOTAL_COST)) {
            params.remove(TOTAL_COST_TO);
            params.remove(TOTAL_COST_FROM);
        }
    }

    @Override
    public Order getOrderById(long id) {
        Order order = orderRepository.findById(id).orElse(null);
        return order;
    }

    @Override
    public Order addItem(ChosenItem item, long orderId) {
        Order order = getOrderById(orderId);
        item.setOrder(order);
        itemRepository.save(item);
        return order;
    }


    @Override
    public Order deleteItem(long itemId, long orderId) {
        itemRepository.deleteByCompositeKey(itemId, orderId);
        return orderRepository.findById(orderId).orElse(null);
    }
}
