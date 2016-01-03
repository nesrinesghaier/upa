/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.vpc.upa.impl.util;

/**
 *
 * @author vpc
 */
public class OrderedIem<T> implements Comparable<OrderedIem<T>>{
    public int order;
    public T value;

    public OrderedIem(int order, T value) {
        this.order = order;
        this.value = value;
    }

    public int compareTo(OrderedIem<T> o) {
        if(o ==null){
            return 1;
        }
        if(order<o.order){
            return -1;
        }else if(order>o.order){
            return 1;
        }else{
            return 0;
        }
    }
    
}
