package concurrencia;

import log.Log;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Campamento {
    //Atributos de campamento
    private Lock entrada = new ReentrantLock();
    private Lock seleccionActividadMonitores = new ReentrantLock();
    private Condition cola1 = entrada.newCondition(); //Condition de la cola 1
    private Condition cola2 = entrada.newCondition(); //Condition de la cola 2
    private int numNinosCampamento = 0; //No debe tener mas de 50 niños dentro
    private int turnoCola = 1;
    private boolean acceso_cola1 = false;
    private boolean acceso_cola2 = false;
    private ArrayList<Integer> eleccionActividad = new ArrayList<>(Arrays.asList(1, 1, 2, 3)); //1 --> Merienda || 2 --> Soga || 3 --> Tirolina
    private ListaThreads colaPuerta1, colaPuerta2;
    private ArrayList<Niño> aforoCampamento = new ArrayList<>();
    private Log log;

    //Método constructor
    public Campamento(JTextField tfcolaPuerta1, JTextField tfcolaPuerta2, Log _log) {
        this.colaPuerta1 = new ListaThreads(tfcolaPuerta1);
        this.colaPuerta2 = new ListaThreads(tfcolaPuerta2);
        this.log = _log;
    }

    //Métodos para comprobar la entrada de los niños
    public void entrada(Niño nino) {
        try {
            entrada.lock();
            //Elegmimos la cola de manera aleatoria
            int eleccionCola = (int)(Math.random()*2+1);
            switch (eleccionCola) {
                case 1:
                    //La cola elegida ha sido la cola 1
                    colaPuerta1.meterNino(nino);
                    //Antes de entrar, comprobaremos si la cola está abierta y si ademas hay hueco
                    if((getAcceso_cola1()) && (getNumNinosCampamento() < 50)){
                        //Se verifica que la cola 1 está abierta y hay hueco, por tanto se puede entrar
                        entra(nino, 1);
                    }
                    else{
                        //Se verifica que la cola no está abierta, y por tanto se tendra que esperar
                        try{
                            cola1.await();
                        }catch(InterruptedException ie){}
                        //Una vez que nos despierten es porque ya podremos entrar
                        entra(nino, 1);
                    }
                    break;
                case 2:
                    //La cola elegida ha sido la cola 1
                    colaPuerta2.meterNino(nino);
                    //Antes de entrar, comprobaremos si la cola está abierta y si ademas hay hueco
                    if((getAcceso_cola2()) && (getNumNinosCampamento() < 50)){
                        //Se verifica que la cola 1 está abierta y hay hueco, por tanto se puede entrar
                        entra(nino, 2);
                    }
                    else{
                        //Se verifica que la cola no está abierta, y por tanto se tendra que esperar
                        try{
                            cola2.await();
                        }catch(InterruptedException ie){}
                        //Una vez que nos despierten es porque ya podremos entrar
                        entra(nino, 2);
                    }
                    break;
            }
        } finally {
            entrada.unlock();
        }
    }
    //Método para que los niños entren a la cola
    public synchronized void entra(Niño nino, int cola){
        switch(cola){
            case 1:
                colaPuerta1.sacarNino(nino);
                incrementaNumNinosCampamento();
                aforoCampamento.add(nino);
                getLog().escribirEnLog("[CAMPAMENTO] El niño " + nino.getID() + " ha entrado al campamento por la cola 1");
                break;
            case 2:
                colaPuerta2.sacarNino(nino);
                incrementaNumNinosCampamento();
                aforoCampamento.add(nino);
                getLog().escribirEnLog("[CAMPAMENTO] El niño " + nino.getID() + " ha entrado al campamento por la cola 2");
                break;
        }
    }

    //Entrada de monitores al campamento
    public void accesoMonitoresColas(Monitor monitor) {
        //Como hacen falta dos monitores, uno en cada entrada del campamento, solo accederán 2 hilos de monitores a las colas
        try {
            entrada.lock();
            //Elegimos una cola a la que acceder aleatoriamente
            int eleccionCola = (int)(Math.random()*2+1);
            switch (eleccionCola) {
                case 1:
                    //La cola elegida ha sido la cola 1, comprobamos parametros de la cola 1
                    if (getAcceso_cola1()) {
                        //Se verifica que el acceso a la cola 1 está abierto, por tanto no será necesario abrirlo
                        getLog().escribirEnLog("[CAMPAMENTO] El monitor " + monitor.getID() + " ha entrado en la cola 1");
                        //Despertamos a tantos hilos de la cola 1 como huecos hayan disponibles
                        int huecosDisponibles = 0;
                        huecosDisponibles = 50 - getNumNinosCampamento();
                        for(int i = 0; i < huecosDisponibles; i++){
                            cola1.signal();
                        }
                    }
                    else {
                        //Se verifica que el acceso a la cola 1 está cerrada, por tanto se tardará un tiempo en abrir
                        int esperaAleatoria = (int) (Math.random() * 1000 + 500);
                        monitor.dormirMonitor(monitor, esperaAleatoria);
                        //Una vez esperado el tiempo aleatorio abrimos el acceso a la cola seleccionada previamente
                        setAcceso_cola1(true);
                        getLog().escribirEnLog("[CAMPAMENTO] El monitor " + monitor.getID() + " abrió y entro a la cola 1");
                        //Despertamos a tantos hilos de la cola 1 como huecos hayan disponibles
                        int huecosDisponibles = 0;
                        huecosDisponibles = 50 - getNumNinosCampamento();
                        for(int i = 0; i < huecosDisponibles; i++){
                            cola1.signal();
                        }
                    }
                    break;
                case 2:
                    //La cola elegida ha sido la cola 2, comprobamos parametros de la cola 2
                    if (getAcceso_cola2()) {
                        //Se verifica que el acceso a la cola 2 está abierto, por tanto no será necesario abrirlo
                        getLog().escribirEnLog("[CAMPAMENTO] El monitor " + monitor.getID() + " ha entrado en la cola 2");
                        //Despertamos a todos los hilos de la cola 2
                        //Despertamos a tantos hilos de la cola 2 como huecos hayan disponibles
                        int huecosDisponibles = 0;
                        huecosDisponibles = 50 - getNumNinosCampamento();
                        for(int i = 0; i < huecosDisponibles; i++){
                            cola2.signal();
                        }
                    }
                    else {
                        //Se verifica que el acceso a la cola 2 está cerradp, por tanto se tardará un tiempo en abrir
                        int esperaAleatoria = (int) (Math.random() * 1000 + 500);
                        monitor.dormirMonitor(monitor, esperaAleatoria);
                        //Una vez esperado el tiempo aleatorio abrimos el acceso a la cola seleccionada previamente
                        setAcceso_cola2(true);
                        getLog().escribirEnLog("[CAMPAMENTO] El monitor " + monitor.getID() + " abrió y entro a la cola 2");
                        //Despertamos a tantos hilos de la cola 2 como huecos hayan disponibles
                        int huecosDisponibles = 0;
                        huecosDisponibles = 50 - getNumNinosCampamento();
                        for(int i = 0; i < huecosDisponibles; i++){
                            cola2.signal();
                        }
                    }
                    break;
            }
        } finally {
            entrada.unlock();
        }
    }

    public void elegirActividad(Monitor monitor) {
        try {
            seleccionActividadMonitores.lock();
            //Generamos un numero aleatorio entre 0 y el rango de la lista
            int indiceAleatorio = (int)(Math.random()*(getEleccionActividad().size() - 1)+0);
            //Comprobamos el resultado y lo borramos para que no se repita
            int actividad = getEleccionActividad().get(indiceAleatorio);
            switch (actividad) {
                case 1:
                    //La actividad elegida ha sido Merienda
                    monitor.setMonitorDeMerienda(true);
                    break;
                case 2:
                    //La actividad elegida ha sido soga
                    monitor.setMonitorDeSoga(true);
                    break;
                case 3:
                    //La actividad elegida ha sido tirolina
                    monitor.setMonitorDeTirolina(true);
                    break;
            }
            //Una vez elegida la actividad, borrará el numero del array para que los monitores se distribuyan correctamente
            getEleccionActividad().remove(indiceAleatorio);
        } finally {
            seleccionActividadMonitores.unlock();
        }
    }

    public void salir(Niño nino) {
        //Salir del campamento y terminar con la ejecución de dicho hilo
        try {
            entrada.lock();
            //Nos quitamos de la lista de aforo de campamento
            boolean eliminado = false;
            while(!eliminado){
                for(int i = 0; i < getAforoCampamento().size(); i++){
                    if(nino == getAforoCampamento().get(i)){
                        getAforoCampamento().remove(i);
                        eliminado = true;
                    }
                }
            }
            //Averiguamos por que cola había accedido el hilo que se va
            switch (getTurnoCola()) {
                case 1:
                    //Las colas se van turnando de forma alterna a la hora de que entre un nuevo niño, y le toca a la cola 1
                    decrementaNumNinosCampamento();
                    //Deberá abandonar la lista de aforo de campamento
                    //Como accedió, por la cola 1, avisaremos que a la cola 1 puede entrar otro hilo nuevo
                    cola1.signal();
                    //Le diremos que el siguiente niño que entre debe ser de la cola 2
                    setTurnoCola(2);
                    break;
                case 2:
                    //Las colas se van turnando de forma alterna a la hora de que entre un nuevo niño, y le toca a la cola 1
                    //Decrementamos el numero de niños en el campamento, ya que uno se va
                    decrementaNumNinosCampamento();
                    //Como accedió, por la cola 2, avisaremos que a la cola 2 puede entrar otro hilo nuevo
                    cola2.signal();
                    //Le diremos que el siguiente niño que entre debe ser de la cola 1
                    setTurnoCola(1);
                    getLog().escribirEnLog("[CAMPAMENTO] El NIÑO " + nino.getID() + " salio del campamento y deja un hueco libre");
                    break;
            }
        } finally {
            entrada.unlock();
        }
    }

    //Método get para el acceso a la cola 1
    public boolean getAcceso_cola1() {
        return acceso_cola1;
    }
    //Método set para el acceso a la cola 1
    public void setAcceso_cola1(boolean acceso_cola1) {
        this.acceso_cola1 = acceso_cola1;
    }
    //Método get para el acceso a la cola 2
    public boolean getAcceso_cola2() {
        return acceso_cola2;
    }
    //Método set para el acceso a la cola 2
    public void setAcceso_cola2(boolean acceso_cola2) {
        this.acceso_cola2 = acceso_cola2;
    }
    //Método get para el numero de niños en el campamento
    public int getNumNinosCampamento() {
        return numNinosCampamento;
    }
    //Método para incrementar el numero de niños en el campamento
    public void incrementaNumNinosCampamento() {
        numNinosCampamento = numNinosCampamento + 1;
    }
    //Método para decrementar el numero de niños en el campamento
    public void decrementaNumNinosCampamento() {
        numNinosCampamento = numNinosCampamento - 1;
    }
    //Método get para el arraylist de eleccion de actividad de los monitores
    public ArrayList<Integer> getEleccionActividad() {
        return eleccionActividad;
    }
    //Método get de la lista de aforo de campamento
    public ArrayList<Niño> getAforoCampamento() {
        return aforoCampamento;
    }
    //Método get para coger en turno de la cola a la que le toca entrar
    public int getTurnoCola() {
        return turnoCola;
    }
    //Método set para coger en turno de la cola a la que le toca entrar
    public void setTurnoCola(int turnoCola) {
        this.turnoCola = turnoCola;
    }
    //Método get para coger el log
    public Log getLog() {
        return log;
    }
}