package concurrencia;

import log.Log;

import javax.swing.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZonaComun {
    //Atributos de la clase ZonaComun
    private Lock entradaNinosZonaComun = new ReentrantLock();
    private Lock entradaMonitoresZonaComun = new ReentrantLock();
    private ListaThreads monitoresZonaComun, ninosZonaComun;
    private Log log;

    //Métodos de la clase ZonaComun

    //Método constructor
    public ZonaComun(JTextField tfMonitoresZonaComun, JTextField tfNinosZonaComun, Log _log){
        this.monitoresZonaComun = new ListaThreads(tfMonitoresZonaComun);
        this.ninosZonaComun = new ListaThreads(tfNinosZonaComun);
        this.log = _log;
    }
    //Método para que los niños entren a la zona común
    public void entrarZonaComun(Niño nino){
        try{
            entradaNinosZonaComun.lock();
            //Les metemos a la lista de niños en la zona comun
            ninosZonaComun.meterNino(nino);
            getLog().escribirEnLog("[ZONA COMUN] El niño " + nino.getID() + " ha entrado a la zona comun");
        }finally{
            entradaNinosZonaComun.unlock();
        }
    }
    //Método para que los niños salgan de la zona común
    public void salirZonaComun(Niño nino){
        try{
            entradaNinosZonaComun.lock();
            //Para salir de la zona común simplemente nos quitamos de la lista de la zona comun
            ninosZonaComun.sacarNino(nino);
            getLog().escribirEnLog("[ZONA COMUN] El niño " + nino.getID() + " ha salido de la zona comun");
        }finally{
            entradaNinosZonaComun.unlock();
        }
    }
    //Método para que los monitores entren a la zona común
    public void entrarZonaComunMonitores(Monitor monitor){
        try{
            entradaMonitoresZonaComun.lock();
            //Para entrar a la zona común simplemente nos metemos en la lista de monitores que están en la zona común
            monitoresZonaComun.meterMonitor(monitor);
            getLog().escribirEnLog("[ZONA COMÚN] El monitor " + monitor.getID() + " ha entrado a la zona común");
        }finally{
            entradaMonitoresZonaComun.unlock();
        }
    }
    //Método para que los monitores salgan de la zona común
    public void salirZonaComunMonitores(Monitor monitor){
        try{
            entradaMonitoresZonaComun.lock();
            //Para salirnos de la zona común simplemente nos borramos de la lista de monitores que están en la zona común
            monitoresZonaComun.sacarMonitor(monitor);
            getLog().escribirEnLog("[ZONA COMÚN] El monitor " + monitor.getID() + " ha salido de la zona común");
        }finally{
            entradaMonitoresZonaComun.unlock();
        }
    }

    public Log getLog() {
        return log;
    }
}