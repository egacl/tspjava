package cl.agilesoft.algoritmos;

import cl.agilesoft.algoritmos.dto.Node;
import cl.agilesoft.algoritmos.dto.Tour;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class PmxHelper {

    public static Tour partiallyMappedCrossOverv2(Tour padre, Tour madre) {
        try {
            // System.out.println("Se inicia rutina crear hijo");
            int length = padre.getNodes().length;
            Node[] ady = new Node[length];
            Arrays.setAll(ady, i -> new Node(i, null, null, 0));

            int crossoverPoint1 = ThreadLocalRandom.current().nextInt(length);
            Node ptrp0 = padre.getNodes()[crossoverPoint1];
            int crossoverPoint2 = ThreadLocalRandom.current().nextInt(length);
            while (crossoverPoint1 == crossoverPoint2) {
                crossoverPoint2 = ThreadLocalRandom.current().nextInt(length);
            }
            // System.out.println("1");
            Node ptrp3 = padre.getNodes()[crossoverPoint2]; //t2 es t3->anterior

            //computa el recorrido mas corto (si parte de p0 o de p3 hacia el siguiente)
            int distanciaEntreCortes = ptrp3.position - ptrp0.position;
            if (distanciaEntreCortes < 0) {
                distanciaEntreCortes += length;
            }
            if (distanciaEntreCortes >= length / 2) {
                //elige el recorrido mas corto
                Node temp = ptrp0;
                ptrp0 = ptrp3;
                ptrp3 = temp;
            }
            // System.out.println("2");

            //para calcular el posicionamiento del hijo
            int offset = padre.getNodes()[0].position;

            //inicializa aux para recorrido y copia
            Node aux = ptrp0;
            int pos = aux.position - offset;
            if (pos < 0) {
                pos += length;
            }
            int nPos = pos;

            // System.out.println("3");
            //recorre la madre hasta posicion de inicio de corte
            Node ptrMadre0 = madre.getNodes()[0];
            if (nPos <= (length / 2)) {
                while ((nPos--) > 0) {
                    ptrMadre0 = ptrMadre0.next;
                }
            } else {
                nPos = length - nPos;
                while ((nPos--) > 0) {
                    ptrMadre0 = ptrMadre0.previous;
                }
            }

            // System.out.println("4");
            //inicializa los reemplazos
            int[] replaces = new int[length];
            Arrays.fill(replaces, -1);
            Node auxM = ptrMadre0;

            //copia el padre al hijo
            while (aux != ptrp3.next) {
                Node hijo = ady[aux.id];
                hijo.id = aux.id;
                hijo.position = pos++;
                if (pos == length) {
                    pos = 0;
                }
                hijo.next = ady[aux.next.id];
                hijo.previous = ady[aux.previous.id];
                if (aux.id != auxM.id) {
                    replaces[aux.id] = auxM.id;
                }
                aux = aux.next;
                auxM = auxM.next;
            }

            // System.out.println("5");

            Node previous = ady[ptrp3.id];
            while (auxM != ptrMadre0) {
                int id = getReplacementId(replaces, auxM.id);
                Node nodeChild = ady[id];
                nodeChild.id = id;
                nodeChild.position = pos++;
                if (pos == length) {
                    pos = 0;
                }
                nodeChild.previous = previous;
                nodeChild.previous.next = nodeChild;
                previous = nodeChild;
                auxM = auxM.next;
            }

            // System.out.println("6");

            ady[ptrp0.id].previous = ady[getReplacementId(replaces, ptrMadre0.previous.id)];
            ady[ptrp0.id].previous.next = ady[ptrp0.id];

            return new Tour(padre.getMap(), ady);
        } catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException(err);
        }
    }

    private static int getReplacementId(int[] replacements, int id) {
        while (replacements[id] != -1) id = replacements[id];
        return id;
    }

}
