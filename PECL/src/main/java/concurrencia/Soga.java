package concurrencia;

import log.Log;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Soga {
    //Atributos de la clase soga
    private Lock soga = new ReentrantLock();
    private Lock entradaSoga = new ReentrantLock();
    private Condition dormirNinoSoga = soga.newCondition();
    private Condition dormirMonitorSoga = soga.newCondition();
    private int numNinosEntradaSoga = 0, numNinosSoga = 0, equipoSoga1 = 0, equipoSoga2 = 0;
    private boolean abiertoSoga = false;
    private ArrayList<Niño> listaNinosSoga = new ArrayList<>();
    private ArrayList<Niño> listaNinosEquipoA = new ArrayList<>();
    private ArrayList<Niño> listaNinosEquipoB = new ArrayList<>();
    private ArrayList<String> listaEquipoA = new ArrayList<>();
    private ArrayList<String> listaEquipoB = new ArrayList<>();
    private ListaThreads colaEsperaSoga, monitorSoga, equipoA, equipoB;
    private Log log;
    //Métodos de la clase soga

    //Método constructor
    public Soga(JTextField tfEsperaSoga, JTextField tfMonitorSoga, JTextField tfEquipoA, JTextField tfEquipoB, Log _log) {
        this.colaEsperaSoga = new ListaThreads(tfEsperaSoga);
        this.monitorSoga = new ListaThreads(tfMonitorSoga);
        this.equipoA = new ListaThreads(tfEquipoA);
        this.equipoB = new ListaThreads(tfEquipoB);
        this.log = _log;
    }
    //Método que comprobará si un niño puede o no entrar a la soga
    public boolean comprobarEntradaSoga(Niño nino){
        //El niño podrá entrar a la soga solo si la actividad está abierta y si hay hueco en la lista de soga
        try{
            soga.lock();
            if((isAbiertoSoga()) && (getListaNinosSoga().size() < 10)){
                //Se verifica que la actividad soga está abierta y que hay hueco para entrar en la lista
                return true;
            }
            else{
                //No puede entrar
                return false;
            }
        }finally{
            soga.unlock();
        }
    }
    public void entrarSoga(Niño nino){
        try{
            soga.lock();
            //Como hay hueco en la soga, nos meteremos
            incrementaNumNinosEntradaSoga();
            getListaNinosSoga().add(nino);
            colaEsperaSoga.meterNino(nino);
            getLog().escribirEnLog("[SOGA] El niño " + nino.getID() + " ha entrado a la soga");
            //Una vez que el niño se mete en la lista, comprobaremos si es el ultimo niño que queda
            if(getListaNinosSoga().size() == 10){
                //Se verifica que es el último niño que queda para iniciar la actividad, por tanto deberá despertar al monitor
                dormirMonitorSoga.signal();
                //Una vez despertado, nos dormimos para que el monitor pueda trabajar
                try{
                    dormirNinoSoga.await();
                }catch(InterruptedException ie){}
            }
            else{
                //Se verifica que no es el ultimo niño, por tanto no avisa al monitor, simplemente se duerme hasta
                //que éste le despierte
                try{
                    dormirNinoSoga.await();
                }catch(InterruptedException ie){}

            }
        }finally{
            soga.unlock();
        }

    }
    //Método para que el monitor entre a la soga
    public void entradaMonitorSoga(Monitor monitor){
        try{
            soga.lock();
            //Cuando el monitor entra a la actividad, le diremos que es el monitor de soga
            monitorSoga.meterMonitor(monitor);
        }finally{
            soga.unlock();
        }
    }
    //Método para que el monitor espere a que la actividad este llena para poder comenzarla
    public void monitorEspera(Monitor monitor){
        try{
            soga.lock();
            //Una vez que el monitor está listo para iniciar una actividad, este abre de nuevo la actividad para que más niños se puedan meter
            setAbiertoSoga(true);
            //Dormimos al monitor a la espera de que los niños le despierten para iniciar una soga
            try{
                dormirMonitorSoga.await();
            }catch(InterruptedException ie){}
        }finally{
            soga.unlock();
        }
    }
    //Método para que el monitor de soga haga los equipos
    public void hacerEquiposSoga(Monitor monitor){
        try{
            soga.lock();
            //La soga va a comenzar, por tanto cerramos la actividad ya que ningun niño podrá entrar
            //si se esta jugando una soga
            setAbiertoSoga(false);
            //Recorremos la lista de los niños que han entrado con el fin de asignarles sus equipos
            for (int i = 0; i < getListaNinosSoga().size(); i++){
                int equipoAleatorio = (int)(Math.random()*(2)+1);
                Niño nino = getListaNinosSoga().get(i);
                switch(equipoAleatorio){
                    case 1:
                        //Verificamos que el equipo aleatorio ha sido el 1, ahora veremos i hay hueco
                        if(getEquipoSoga1() < 5){
                            //Se verifca que hay hueco en el EQUIPO A, por tanto se le añade a la lista de equipo a
                            decrementaNumNinosEntradaSoga();
                            incrementaEquipoSoga1();
                            getListaNinosEquipoA().add(nino);
                            getListaEquipoA().add(nino.getID());
                            colaEsperaSoga.sacarNino(nino);
                            equipoA.meterNino(nino);
                            //Le decimos la niño que pertenece al equipo 1 o A
                            nino.setEquipoSoga(1);
                            getLog().escribirEnLog("[SOGA] El monitor de soga " + monitor.getID() + " ha asignado al niño " + nino.getID() + " el equipo A");
                        }
                        else{
                            //Se verifica que no hay hueco en el EQUIPO A, por tanto se debe de ir al EQUIPO
                            decrementaNumNinosEntradaSoga();
                            incrementaEquipoSoga2();
                            getListaNinosEquipoB().add(nino);
                            getListaEquipoB().add(nino.getID());
                            colaEsperaSoga.sacarNino(nino);
                            equipoB.meterNino(nino);
                            //Le decimos la niño que pertenece al equipo 2 o B
                            nino.setEquipoSoga(2);
                            getLog().escribirEnLog("[SOGA] El monitor de soga " + monitor.getID() + " ha asignado al niño " + nino.getID() + " el equipo B");
                        }
                        break;
                    case 2:
                        if(getEquipoSoga2() < 5){
                            //Se verifca que hay hueco en el EQUIPO B, por tanto se le añade a la lista de equipo b
                            decrementaNumNinosEntradaSoga();
                            incrementaEquipoSoga2();
                            getListaNinosEquipoB().add(nino);
                            getListaEquipoB().add(nino.getID());
                            colaEsperaSoga.sacarNino(nino);
                            equipoB.meterNino(nino);
                            //Le decimos la niño que pertenece al equipo 2 o B
                            nino.setEquipoSoga(2);
                            getLog().escribirEnLog("[SOGA] El monitor de soga " + monitor.getID() + " ha asignado al niño " + nino.getID() + " el equipo B");
                        }
                        else{
                            //Se verifica que no hay hueco en el EQUIPO B, por tanto se debe de ir al EQUIPO A
                            decrementaNumNinosEntradaSoga();
                            incrementaEquipoSoga1();
                            getListaNinosEquipoA().add(nino);
                            getListaEquipoA().add(nino.getID());
                            colaEsperaSoga.sacarNino(nino);
                            equipoA.meterNino(nino);
                            //Le decimos la niño que pertenece al equipo 1 o A
                            nino.setEquipoSoga(1);
                            getLog().escribirEnLog("[SOGA] El monitor de soga " + monitor.getID() + " ha asignado al niño " + nino.getID() + " el equipo A");
                        }
                        break;
                }
            }
        }finally{
            soga.unlock();
        }
    }
    public void jugarSoga(Monitor monitor){
        try{
            soga.lock();
            //La actividad dura 7 segundos, por tanto el hilo se dormira durante 7 segundos y después anunciará el equipo ganador
            monitor.dormirMonitor(monitor, 7000);
            //Pasados los 7 segundos, anunciaremos el ganador al azar
            int equipoGanador = (int)(Math.random()*(2)+1);
            switch(equipoGanador){
                case 1:
                    //Se verifica que el equipo ganador es el EQUIPO A
                    getLog().escribirEnLog("[SOGA] El equipo ganador es el EQUIPO A, compuesto por: " + getListaEquipoA());
                    //Cada niño  recibirá 2 participaciones extra
                    for(int i = 0; i < 5; i++){
                        Niño nino = getListaNinosEquipoA().get(i);
                        nino.incrementarParticipaciones(2);
                    }
                    //Por tanto, si ha ganado el equipo A, ha perdido el equipo B, y a ellos se les añadirán 1 participacion extra
                    for(int i = 0; i < 5; i++){
                        Niño nino = getListaNinosEquipoB().get(i);
                        nino.incrementarParticipaciones(1);
                    }
                    break;
                case 2:
                    //Se verifica que el equipo ganador es el EQUIPO B
                    getLog().escribirEnLog("[SOGA] El equipo ganador es el EQUIPO B, compuesto por: " + getListaEquipoB());
                    //Cada niño  recibirá 2 participaciones extra
                    for(int i = 0; i < 5; i++){
                        Niño nino = getListaNinosEquipoB().get(i);
                        nino.incrementarParticipaciones(2);
                    }
                    //Por tanto, si ha ganado el equipo B, ha perdido el equipo A, y a ellos se les añadirán 1 participacion extra
                    for(int i = 0; i < 5; i++){
                        Niño nino = getListaNinosEquipoA().get(i);
                        nino.incrementarParticipaciones(1);
                    }
                    break;
            }
            //Como el monitor ha completado una acitivdad de soga, incrementamos en 1 sus sogas hechas
            monitor.incrementaSogasHechas(1);
            //Una vez que esté finalzada la actividad, el monitor vaciará las listas de los equipos para preparar la siguiente soga
            getListaEquipoA().clear();
            getListaEquipoB().clear();
            getListaNinosEquipoA().clear();
            getListaNinosEquipoB().clear();
            getListaNinosSoga().clear();
            setEquipoSoga1(0);
            setEquipoSoga2(0);
            setNumNinosSoga(0);
            //Una vez que esten todas las listas borradas, el monitor despertará a todos los niños para que abandonen la actividad
            dormirNinoSoga.signalAll();
            //El monitor se dormirá hasta que tenga que volver a preparar de nuevo la actividad
        }finally{
            soga.unlock();
        }
    }
    //Método para que los niños salgan de la soga
    public void salirSoga(Niño nino){
        try{
            soga.lock();
            //En primer lugar, nos quitaremos del equipo
            switch(nino.getEquipoSoga()){
                case 1:
                    //Pertenecía al equipo A
                    equipoA.sacarNino(nino);
                    break;
                case 2:
                    //Pertenecia al equipo B
                    equipoB.sacarNino(nino);
            }
            //Una vez que nos hayamos ido de los equipos, podremos salir de la soga
            getLog().escribirEnLog("[SOGA] El niño " + nino.getID() + " ha salido de la soga");
        }finally{
            soga.unlock();
        }
    }
    //Método para que el monitor salga de la soga
    public void salirMonitorSoga(Monitor monitor){
        try{
            entradaSoga.lock();
            //Para salirnos nos quitaremos del JTextField de monitor de soga
            monitorSoga.sacarMonitor(monitor);
        }finally{
            entradaSoga.unlock();
        }
    }
    //Método get del numero de niños en el equipo 1 o equipo A
    public int getEquipoSoga1() {
        return equipoSoga1;
    }
    //Método set del numero de niños en el equipo 1 o equipo A
    public void setEquipoSoga1(int equipoSoga1) {
        this.equipoSoga1 = equipoSoga1;
    }
    //Método que incrementa el numero de niños en el equipo 1 o equipo A
    public void incrementaEquipoSoga1(){
        equipoSoga1++;
    }
    //Método get del numero de niños en el equipo 2 o equipo B
    public int getEquipoSoga2() {
        return equipoSoga2;
    }
    //Método set del numero de niños en el equipo 2 o equipo B
    public void setEquipoSoga2(int equipoSoga2) {
        this.equipoSoga2 = equipoSoga2;
    }
    //Método que incrementa el numero de niños en el equipo 2 o equipo B
    public void incrementaEquipoSoga2(){
        equipoSoga2++;
    }
    //Método get del booleano abiertoSoga
    public boolean isAbiertoSoga() {
        return abiertoSoga;
    }
    //Método set del booleano abiertoSoga
    public void setAbiertoSoga(boolean abiertoSoga) {
        this.abiertoSoga = abiertoSoga;
    }
    //Método set para el numero de niños en la soga
    public void setNumNinosSoga(int num){
        numNinosSoga = num;
    }
    //Método get para el numero de niños que se encuentran en la cola de espera
    public int getNumNinosEntradaSoga() {
        return numNinosEntradaSoga;
    }
    //Método que incrementa el numero de niños que se encuentran en la cola de espera
    public void incrementaNumNinosEntradaSoga(){
        numNinosEntradaSoga++;
    }
    //Método que decrementa el numero de niños que se encuentran en la cola de espera
    public void decrementaNumNinosEntradaSoga(){
        numNinosEntradaSoga--;
    }
    //Método para acceder al log
    public Log getLog() {
        return log;
    }

    public ArrayList<Niño> getListaNinosSoga() {
        return listaNinosSoga;
    }

    public ArrayList<Niño> getListaNinosEquipoA() {
        return listaNinosEquipoA;
    }

    public ArrayList<Niño> getListaNinosEquipoB() {
        return listaNinosEquipoB;
    }

    public ArrayList<String> getListaEquipoA() {
        return listaEquipoA;
    }

    public ArrayList<String> getListaEquipoB() {
        return listaEquipoB;
    }
}
