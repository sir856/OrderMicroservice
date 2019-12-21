package com.developing.shop.orders.service;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.Item;
import com.developing.shop.orders.model.Order;
import com.developing.shop.orders.model.Status;
import com.developing.shop.orders.repository.ChosenItemRepository;
import com.developing.shop.orders.repository.ItemRepository;
import com.developing.shop.orders.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private String ORDER_BY = "orderBy";
    private String USERNAME = "userName";
    private String TOTAL_AMOUNT = "totalAmount";
    private String TOTAL_AMOUNT_FROM = "totalAmountFrom";
    private String TOTAL_AMOUNT_TO = "totalAmountTo";
    private String TOTAL_COST = "totalCost";
    private String TOTAL_COST_FROM = "totalCostFrom";
    private String TOTAL_COST_TO = "totalCostTo";
    private String STATUS = "status";

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    private final EntityManager em;

    private CriteriaBuilder cb;
    private CriteriaQuery<Order> cq;

    private Map<String, Function<String, Predicate>> predicatesMap;
    private Map<String, javax.persistence.criteria.Order> ordersMap;

    private final OrderRepository orderRepository;
    private final ChosenItemRepository chosenItemRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ChosenItemRepository chosenItemRepository,
                            ItemRepository itemRepository, EntityManager em) {
        this.orderRepository = orderRepository;
        this.chosenItemRepository = chosenItemRepository;
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
        order.setStatus(Status.COLLECTING);
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

            for (String key : params.keySet()) {
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
        return result;
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
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No order with id : " + id));
    }

    @Override
    public Order addItemToOrder(ChosenItem chosenItem, long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != Status.COLLECTING) {
            throw new IllegalStateException("Order already collected");
        }

        Item item = itemRepository.findById(chosenItem.getItem().getId()).
                orElseThrow(() -> new IllegalArgumentException("No item with id : " + chosenItem.getItem().getId()));

        item.changeAmount(chosenItem.getAmount());

        ChosenItem itemInBD = chosenItemRepository.getByCompositeKey(item.getId(), orderId);
        if (itemInBD != null) {
            chosenItem.setAmount(chosenItem.getAmount() + itemInBD.getAmount());
        }
        chosenItem.setOrder(order);
        chosenItem.setItem(item);
        chosenItem.setPrice(item.getPrice());
        chosenItemRepository.save(chosenItem);

        return order;
    }


    @Override
    public ChosenItem deleteItemFromOrder(long itemId, long orderId) {
        if (getOrderById(orderId).getStatus() != Status.COLLECTING) {
            throw new IllegalStateException("Order already collected");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new IllegalArgumentException("No item with id : " + itemId));
        ChosenItem chosenItem = chosenItemRepository.getByCompositeKey(itemId, orderId);
        item.changeAmount(-chosenItem.getAmount());
        itemRepository.save(item);
        chosenItemRepository.deleteByCompositeKey(itemId, orderId);
        return chosenItem;
    }

    @Override
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Order setCollected(long id) {
        Order order = getOrderById(id);
        if (order.getStatus() == Status.COLLECTING) {
            order.setStatus(Status.COLLECTED);
            orderRepository.save(order);
        }

        return order;
    }
}
