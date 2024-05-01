package cl.agilesoft.algoritmos.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Node {

    public int id;
    public Node next;
    public Node previous;
    public int position;

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", next=" + next.id +
                ", previous=" + previous.id +
                ", position=" + position +
                '}';
    }
}
