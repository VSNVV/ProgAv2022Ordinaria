package concurrencia;

import log.Log;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Merendero {
    //Atributos de la clase merendero
    private Lock entradaMerendero = new ReentrantLock();
    private Lock bandejaLimpia = new ReentrantLock();
    private Lock bandejaSucia = new ReentrantLock();
    private Lock servirMerienda = new ReentrantLock();
    private Lock merendando = new ReentrantLock();
    private Condition colaEsperaMerienda = entradaMerendero.newCondition();
    private Condition colaServirMerienda = bandejaLimpia.newCondition();
    private Condition dormirMonitorMerienda = bandejaSucia.newCondition();
    private ArrayList<Integer> listaBandejasSucias = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25));
    private ArrayList<Integer> listaBandejasLimpias = new ArrayList<>(Arrays.asList());
    private ArrayList<String> listaMonitoresMerienda = new ArrayList<>();
    private ArrayList<String> listaNinosMerienda = new ArrayList<>();
    private ArrayList<String> listaEsperaMerienda = new ArrayList<>();
    private int numNinosMerienda = 0, numMonitoresMerienda = 0, numNinosMerendando = 0;
    private boolean abiertoMerendero = false;
    private ListaThreads colaEsperaMerendero, bandejasLimpias, bandejasSucias, monitoresMerendero, ninosMerendando;
    private Log log;

    //Métodos de la clase merendero

    //Método constructor
    public Merendero(JTextField tfEsperaMerendero, JTextField tfBandejasLimpias, JTextField tfBandejasSucias, JTextField tfMonitoresMerendero, JTextField tfNinosMerendando, Log _log){
        this.colaEsperaMerendero = new ListaThreads(tfEsperaMerendero);
        this.bandejasLimpias = new ListaThreads(tfBandejasLimpias);
        this.bandejasSucias = new ListaThreads(tfBandejasSucias);
        this.monitoresMerendero = new ListaThreads(tfMonitoresMerendero);
        this.ninosMerendando = new ListaThreads(tfNinosMerendando);
        this.log = _log;
    }
    //Método para que los niños entren a la actividad merienda
    public void entrarMerendero(Niño nino){
        try{
            entradaMerendero.lock();
            //En primer lugar se debe comprobar que se puede acceder al merendero
            if(comprobarEntradaMerendero(nino)){
                //Se verifica que se puede entrar
                incrementaNumNinosMerienda();
                //Se añade al niño a la lista de niños en el merendero
                colaEsperaMerendero.meterNino(nino);
                getListaNinosMerienda().add(nino.getID());
                getLog().escribirEnLog("[MERIENDA] El niño " + nino.getID() + " ha entrado al merendero");
                //Una vez dentro del merendero comprobamos si hay alguna bandeja lista para usar, si no esperaremos a que haya una para usar
            }
            else{
                //Se verifica que no se puede entrar, por lo tanto el niño se quedará esperando en la cola de espera
                colaEsperaMerendero.meterNino(nino);
                getLog().escribirEnLog("[MERIENDA] El niño " + nino.getID() + " está en la cola de espera");
                try{
                    getListaEsperaMerienda().add(nino.getID());
                    colaEsperaMerienda.await();
                }catch(InterruptedException ie){}
                //Una vez que pueda entrar entrará al merendero
                nino.borrarDeLista(getListaEsperaMerienda(), nino.getID());
                incrementaNumNinosMerienda();
                //Nos metemos en la lista de los niños que se encuentran en el merendero
                getListaNinosMerienda().add(nino.getID());
                getLog().escribirEnLog("[MERIENDA] El niño " + nino.getID() + " ha entrado al merendero");
            }
        }finally{
            entradaMerendero.unlock();
        }
    }
    //Método para comprobar si se puede acceder a la soga
    public boolean comprobarEntradaMerendero(Niño nino){
        boolean resultado = false;
        //Primero comprobaremos que está abierta la actividad
        if(isAbiertoMerendero()){
            //Se verifica que la actividad está abierta
            if(getNumNinosMerienda() < 20){
                //Se verifica que hay hueco
                resultado = true;
            }
            else{
                //Se verifica que no hay hueco, por tanto no se puede entrar, como resultado esta inicialmente en false, no hace falta cambiarlo
            }
        }
        return resultado;
    }
    //Método para que los niños comprueben si hay bandejas disponibles para comer
    public void comprobarBandejaLimpia(Niño nino){
        try{
            bandejaLimpia.lock();
            if(listaBandejasLimpias.size() == 0){
                //Se verifica que no hay bandejas listas, por tanto nos quedaremos esperando a que hayan
                try{
                    colaServirMerienda.await();
                }catch(InterruptedException ie){}
                //Una vez pueda continuar ejecucion, podrá coger una bandeja
                nino.setBandeja(listaBandejasLimpias.get(0));
                //Una vez que la tiene, se elimina de la lista de bandejas limpias
                listaBandejasLimpias.remove(0);
                bandejasLimpias.ponerNumeroLimpias(listaBandejasLimpias.size());
            }
            else{
                //Se verifica que hay bandejas listas, por tanto se coge la primera y se come
                nino.setBandeja(listaBandejasLimpias.get(0));
                //Una vez que la tiene, se elimina de la lista de bandejas limpias
                listaBandejasLimpias.remove(0);
                bandejasLimpias.ponerNumeroLimpias(listaBandejasLimpias.size());
            }
        }finally{
            bandejaLimpia.unlock();
        }
    }
    //Método para que los monitores comprueben si hay bandejas para limpiar
    public void comprobarBandejaSucia(Monitor monitor){
        try{
            bandejaSucia.lock();
            if(getListaBandejasSucias().size() == 0){
                //Se verifica que no hay platos sucios para limpiar, por tanto nos quedaremos dormidos hasta que un niño nos despierte cuando deje una bandeja para limpiar
                try{
                    dormirMonitorMerienda.await();
                }catch(InterruptedException ie){}
            }
            else{
                //Se verifica que hay bandejas para limpiar, por tanto se procede a coger la bandeja
            }
        }finally{
            bandejaSucia.unlock();
        }
    }
    //Método para el acceso de los monitores al merendero
    public void entradaMonitorMerendero(Monitor monitor){
        try{
            entradaMerendero.lock();
            //En primer lugar añadiremos al monitor en la lista de monitores
            getListaMonitoresMerienda().add(monitor.getID());
            monitoresMerendero.meterMonitor(monitor);
            incrementaNumMonitoresMerienda();
            //Una vez metidos en la lista abriremos la actividad
            setAbiertoMerendero(true);
        }finally{
            entradaMerendero.unlock();
        }
    }
    //Método para que el monitor coga una bandeja de la lista de bandejas sucias
    public void cogerBandeja(Monitor monitor){
        try{
            bandejaSucia.lock();
            //Cogeremos la primera bandeja que haya en el array de la lista de sucias
            monitor.setBandeja(getListaBandejasSucias().get(0));
            //Una vez tengamos la bandeja la eliminamos de sucias para proceder a limpiarla
            getListaBandejasSucias().remove(0);
            bandejasSucias.ponerNumeroSucias(getListaBandejasSucias().size());
        }finally{
            bandejaSucia.unlock();
        }
    }
    public void servirBandeja(Monitor monitor){
        //El monitor tarda en limpiar y servir la bandeja entre 3 y 5 segundos
        monitor.dormirMonitor(monitor, (int)(Math.random()*(5000)+3000));
        getLog().escribirEnLog("El monitor " + monitor.getID() + " ha limpiado la bandeja " + monitor.getBandeja());
        monitor.incrementaMeriendasServidas(1);
        //Una vez que el monitor haya esperado el tiempo necesario, pondrá la bandeja en la lista de bandejas listas para que los niños la puedan coger
        try{
            servirMerienda.lock();
            //Depositamos la bandeja limpia en la lista de bandejas limpias y despertamos a los niños que cuando llegaron no tenian bandeja
            try{
                bandejaLimpia.lock();
                listaBandejasLimpias.add(monitor.getBandeja());
                bandejasLimpias.ponerNumeroLimpias(listaBandejasLimpias.size());
                //Una vez que haya dejado la bandeja en la lista de bandejas limpias, el monitor se quedará sin bandeja
                monitor.setBandeja(-1);
                colaServirMerienda.signal();
            }finally{
                bandejaLimpia.unlock();
            }
        }finally{
            servirMerienda.unlock();
        }
    }
    public void comerMerienda(Niño nino){
        //El niño tardará en comer 7 segundos
        try{
            merendando.lock();
            colaEsperaMerendero.sacarNino(nino);
            ninosMerendando.meterNino(nino);
            incrementaNumNinosMerendando();
        }finally{
            merendando.unlock();
        }
        getLog().escribirEnLog("[MERIENDA] El niño " + nino.getID() + " está comiendo su merienda en la bandeja " + nino.getBandeja());
        nino.dormirNino(nino, 7000);
        try{
            merendando.lock();
            decrementaNumNinosMerendando();
        }finally{
            merendando.unlock();
        }
    }
    public void dejarBandeja(Niño nino){
        try{
            bandejaSucia.lock();
            //Añadimos nuestra bandeja a la lista de bandejas sucias para que la limpien los monitores
            getListaBandejasSucias().add(nino.getBandeja());
            bandejasSucias.ponerNumeroSucias(getListaBandejasSucias().size());
            //Por si hay un monitor dormido porque no habian bandejar para limpiar, ahora ya tiene una, por tanto le despertamos
            dormirMonitorMerienda.signal();
        }finally{
            bandejaSucia.unlock();
        }
        //Una vez depositada la bandeja en la lista de sucias, el niño no tiene ninguna bandeja con él
        nino.setBandeja(-1);
    }
    //Método para que los niños salgan del merendero
    public void salirMerendero(Niño nino){
        try{
            entradaMerendero.lock();
            decrementaNumNinosMerienda();
            nino.borrarDeLista(getListaNinosMerienda(), nino.getID());
            ninosMerendando.sacarNino(nino);
            getLog().escribirEnLog("[MERIENDA] El niño " + nino.getID() + " ha salido del merendero");
            //Avisaremos a los otros niños de que ha quedado una plaza libre para entrar
            colaEsperaMerienda.signal();
        }finally{
            entradaMerendero.unlock();
        }
    }
    //Método para que los monitores salgan del merendero
    public void salirMonitoresMerendero(Monitor monitor){
        try{
            entradaMerendero.lock();
            //En primer lugar, debemos comprobar si hay que cerrar la actividad
            if((getNumMonitoresMerienda() - 1) == 0){
                //Se verifica que cuando se vaya ese monitor, la actividad se quedará sin monitores, por tanto hay que cerrar la actividad
                setAbiertoMerendero(false);
            }
            else{
                //Se verifica que habrá otro monitor mas, por tanto no es necesario cerrar
                setAbiertoMerendero(true);
            }
            //Una vez decidido que hacer con la actividad, nos borramos de la lista de monitores de merendero
            decrementaNumMonitoresMerienda();
            getLog().escribirEnLog("[MERIENDA] El monitor" + monitor.getID() + " ha salido del merendero");
            monitor.borrarDeLista(getListaMonitoresMerienda(), monitor.getID());
            monitoresMerendero.sacarMonitor(monitor);
        }finally{
            entradaMerendero.unlock();
        }
    }
    //Método get para el numero de niños en el merendero
    public int getNumNinosMerienda() {
        return numNinosMerienda;
    }
    //Método set para el numero de niños en el merendero
    public void incrementaNumNinosMerienda(){
        numNinosMerienda++;
    }
    //Método que decrementa en 1 el numero de niños en el merendero
    public void decrementaNumNinosMerienda(){
        numNinosMerienda--;
    }
    //Método get para el numero de monitores en el merendero
    public int getNumMonitoresMerienda() {
        return numMonitoresMerienda;
    }
    //Método get para el booleano de abiertoMerendero
    public boolean isAbiertoMerendero() {
        return abiertoMerendero;
    }
    //Método que incrementa el numero de monitores en el merendero
    public void incrementaNumMonitoresMerienda(){
        numMonitoresMerienda++;
    }
    //Método que decrementa el numero de monitores en el merendero
    public void decrementaNumMonitoresMerienda(){
        numMonitoresMerienda--;
    }
    //Método set para el booleano de abiertoMerendero
    public void setAbiertoMerendero(boolean abiertoMerendero) {
        this.abiertoMerendero = abiertoMerendero;
    }
    //Método get para el numero de niños en el merendero
    public int getNumNinosMerendando() {
        return numNinosMerendando;
    }
    //Método para incrementar en 1 el numero de niños que estan merendando
    public void incrementaNumNinosMerendando(){
        numNinosMerendando++;
    }
    //Método para decrementar en 1 el numero de niños que estan merendando
    public void decrementaNumNinosMerendando(){
        numNinosMerendando--;
    }

    public ArrayList<String> getListaMonitoresMerienda() {
        return listaMonitoresMerienda;
    }

    public ArrayList<String> getListaNinosMerienda() {
        return listaNinosMerienda;
    }

    public ArrayList<String> getListaEsperaMerienda() {
        return listaEsperaMerienda;
    }

    public ArrayList<Integer> getListaBandejasSucias() {
        return listaBandejasSucias;
    }

    public ArrayList<Integer> getListaBandejasLimpias() {
        return listaBandejasLimpias;
    }

    public Log getLog() {
        return log;
    }
}
