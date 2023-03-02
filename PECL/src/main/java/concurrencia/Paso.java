package concurrencia;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Si el booleano vale false (abierto) el proceso puede continuar, pero  si es true(cerrado) el proceso se detiene
public class Paso{
    //Atributos de la clase paso
    private boolean cerrado = false;
    private Lock cerrojo = new ReentrantLock();
    private Condition parar = cerrojo.newCondition();

    //Métodos de la clase paso

    //Método constructor
    public Paso(){

    }
    //Método que mira si nos tenemos que detener o no
    public void mirar() {
        try {
            cerrojo.lock();
            while(isCerrado()) {
                try {
                    parar.await();
                } catch(InterruptedException ie){ }
            }
        }
        finally {
            cerrojo.unlock();
        }
    }
    //Método para reanudar ejecucion de hilos
    public void abrir() {
        try{
            cerrojo.lock();
            setCerrado(false);
            parar.signalAll();
        }
        finally {
            cerrojo.unlock();
        }
    }
    //Métodos para reanuar ejecucion de hilos
    public void cerrar() {
        try{
            cerrojo.lock();
            setCerrado(true);
        }
        finally
        {
            cerrojo.unlock();
        }
    }

    public boolean isCerrado() {
        return cerrado;
    }

    public void setCerrado(boolean cerrado) {
        this.cerrado = cerrado;
    }
}
