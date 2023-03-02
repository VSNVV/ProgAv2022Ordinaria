package concurrencia;

import log.Log;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Tirolina {
    //Atributos de la clase Tirolina
    private Lock tirolina = new ReentrantLock();
    private Lock entradaTirolina = new ReentrantLock();
    private Condition colaTirolina = entradaTirolina.newCondition();
    private Condition dormirMonitorTirolina = tirolina.newCondition(); //Condition para dormir al monitor de la tirolina
    private Condition dormirNinoTirolina = tirolina.newCondition();
    private int numNiñosEsperandoTirolina = 0, numUsosTirolina = 0;
    private Niño ninoEnTirolina;
    private boolean abiertoTirolina = false, tirolinaEnUso = false, monitorDisponible = false, ninoEsperando = false, huecoLibre = false;
    private ListaThreads listaEsperaTirolina, monitorTirolina, preparacionTirolina, usandoTirolina, bajandoTirolina;
    private Log log;

    //Método constructor
    public Tirolina(JTextField tfEsperaTirolina, JTextField tfMonitorTirolina, JTextField tfPreparacionTirolina, JTextField tfUsandoTirolina, JTextField tfBajandoTirolina, Log _log){
        this.listaEsperaTirolina = new ListaThreads(tfEsperaTirolina);
        this.monitorTirolina = new ListaThreads(tfMonitorTirolina);
        this.preparacionTirolina = new ListaThreads(tfPreparacionTirolina);
        this.usandoTirolina = new ListaThreads(tfUsandoTirolina);
        this.bajandoTirolina = new ListaThreads(tfBajandoTirolina);
        this.log = _log;
    }
    //Método para que los niños puedan entrar a la actividad tirolina
    public void entrarTirolina(Niño nino){
        try{
            entradaTirolina.lock();
            if(isHuecoLibre()){
                //No tendremos que hacer cola
                setHuecoLibre(false);
            }
            else{
                //Se van todos a una cola a esperar que el monitor les avise para prepararles
                incrementaNumNinosEsperandoTirolina();
                listaEsperaTirolina.meterNino(nino);
                getLog().escribirEnLog("[TIROLINA] El niño " + nino.getID() + " ha entrado a la cola de espera");
                try{
                    colaTirolina.await();
                }catch(InterruptedException ie){}
                //Cuando nos despierte el monitor, nos iremos a la zona de preparacion
                decrementaNumNinosEsperandoTirolina();
                listaEsperaTirolina.sacarNino(nino);
            }
        }finally{
            entradaTirolina.unlock();
        }
    }
    //Método para que el monitor entre a la tirolina
    public void entradaMonitorTirolina(Monitor monitor){
        try{
            entradaTirolina.lock();
            //Cuando entra el monitor le identificamos como el monitor de la tirolina
            monitorTirolina.meterMonitor(monitor);
            getLog().escribirEnLog("[TIROLINA] El monitor " + monitor.getID() + " ha entrado a la tirolina");
            //Abrimos la actividad
            setAbiertoTirolina(true);
            if(getNumNinosEsperandoTirolina() > 0){
                //Se verifica que hay niños en la cola de espera, por tanto les daremos un signal para que vayan a la zona de preparacion
                colaTirolina.signal();
            }
            else{
                //No hay niños esperando, pero nosotros estaremos listos para atender a los niños, por tanto podemos abrir la cola para que el primer
                //niño que entre no tenga que hacer cola
                setHuecoLibre(true);

            }
        }finally{
            entradaTirolina.unlock();
        }
    }
    //Método para que el monitor espere a que un niño le prepare
    public void monitorEspera(Monitor monitor){
        try{
            tirolina.lock();
            if(getNinoEnTirolina() == null){
                //Se verifica que el niño no ha llegado todavia, por tanto nos tendrá que avisar cuando llegue
                setMonitorDisponible(true); //Monitor dormido
                try{
                    dormirMonitorTirolina.await();
                }catch(InterruptedException ie){}
                //Una vez nos haya avisado, nos ponemos a prepararle
            }
            else{
                //Hay un niño esperando, por tanto le prepararemos
            }
        }finally{
            tirolina.unlock();
        }
    }
    //Método para que el niño despierte al monitor de la tirolina para poder empezar la actividad
    public void avisarMonitorTirolina(Niño nino){
        //El monitor estará dormido cuando el niño entre, por tanto lo tiene que despertar, y cuando lo despierte se ira a dormir de nuevo
        try{
            tirolina.lock();
            if(isMonitorDisponible()){
                //El monitor esta dormido, por tanto le tenemos que despertar para que nos prepare
                setNinoEnTirolina(nino);
                dormirMonitorTirolina.signal();
                try{
                    dormirNinoTirolina.await();
                }catch(InterruptedException ie){}
            }
            else{
                //El monitor no esta dormido, no puede prepararnos, entonces le esperaremos
                setNinoEnTirolina(nino);
                try{
                    dormirNinoTirolina.await();
                }catch(InterruptedException ie){}
            }

        }finally{
            tirolina.unlock();
        }
    }
    //Método para que el monitor prepare al niño en la tirolina
    public void preparaNinoTirolina(Monitor monitor){
        try{
            tirolina.lock();
            //Una vez que el monitor ya está preparando al niño, le añadiremos a la preparacion
            preparacionTirolina.meterNino(getNinoEnTirolina());
            getLog().escribirEnLog("[SOGA] El monitor de soga " + monitor.getID() + " está preparando al niño " + getNinoEnTirolina().getID());
            //La preparacion dura 1 segundo, por tanto el monitor se dormirá durante 1 segundo mientras prepara al niño
            monitor.dormirMonitor(monitor, 1000);
            //Una vez preparado le quitamos de la preparacion
            preparacionTirolina.sacarNino(getNinoEnTirolina());
            //Una vez que el niño estña preparado para tirarse, le despertamos y nosotros nos iremos a dormir hasta que el niño salga de la actividad
            dormirNinoTirolina.signal();
            try{
                dormirMonitorTirolina.await();
            }catch(InterruptedException ie){}
        }finally{
            tirolina.unlock();
        }
    }
    //Método para que el niño se tire de la tirolina
    public void tirarTirolina(Niño nino){
        try{
            tirolina.lock();
            //Una vez que el niño ya está preparado para tirarse de la tirolina, por tanto le metemos en tirolina
            usandoTirolina.meterNino(nino);
            getLog().escribirEnLog("[TIROLINA] El niño " + nino.getID() + " se está tirando en la tirolina");
            //Tardará 3 segundos en tirarse
            nino.dormirNino(nino, 3000);
            //Una vez que ya se ha tirado, nos quitamos de tirolina
            usandoTirolina.sacarNino(nino);
        }finally{
            tirolina.unlock();
        }
    }
    //Método para bajar al niño de la tirolina
    public void bajaTirolina(Niño nino){
        try{
            tirolina.lock();
            //Estamos listos para bajarnos, por tanto nos metemos en la lista de bajando
            bajandoTirolina.meterNino(nino);
            getLog().escribirEnLog("[TIROLINA] El niño " + nino.getID() + " se esta bajando de la tirolina");
            //Tardaremos medio segundo en bajarnos
            nino.dormirNino(nino, 500);
            //Una vez que ya nos hemos bajado nos quitaremos de la lista de bajando
            bajandoTirolina.sacarNino(nino);
            //Hemos usado la tirolina, por tanto incrementamos su numero de usos
            incrementaNumUsosTirolina();
        }finally{
            tirolina.unlock();
        }
    }
    public void salirTirolina(Niño nino){
        try{
            tirolina.lock();
            //Para salirnos simplemente debemos quitarnos de niño usando tirolina
            setNinoEnTirolina(null);
            //Una vez hecho lo anterior, despertamos al monitor para que pueda preparar al siguiente niño
            dormirMonitorTirolina.signal();
            getLog().escribirEnLog("[TIROLINA] El niño " + nino.getID() + " ha salido de la tirolina");
        }
        finally{
            tirolina.unlock();
        }
    }
    //Método para que el monitor salga de la tirolina
    public void salirMonitorTirolina(Monitor monitor){
        try{
            entradaTirolina.lock();
            //Nos quitamos de monitor de tirolina
            monitorTirolina.sacarMonitor(monitor);
        }finally{
            entradaTirolina.unlock();
        }
    }
    //Método para que el monitor vuelva a la tirolina
    public void vuelveMonitor(Monitor monitor){
        try{
            entradaTirolina.lock();
            //Abrimos la actividad
            setAbiertoTirolina(true);
            if(getNumNinosEsperandoTirolina() > 0){
                //Se verifica que hay niños en la cola de espera, por tanto les daremos un signal para que vayan a la zona de preparacion
                colaTirolina.signal();
            }
            else{
                //No hay niños esperando, pero nosotros estaremos listos para atender a los niños, por tanto podemos abrir la cola para que el primer
                //niño que entre no tenga que hacer cola
                setHuecoLibre(true);
            }
        }finally{
            entradaTirolina.unlock();
        }
    }

    //Método set para abrir o cerrar la tirolina
    public void setAbiertoTirolina(boolean abiertoTirolina) {
        this.abiertoTirolina = abiertoTirolina;
    }
    //Método get para el numero de niños en la tirolina
    public int getNumNinosEsperandoTirolina() {
        return numNiñosEsperandoTirolina;
    }
    //Método que incrementa en 1 los niños de la tirolina
    public void incrementaNumNinosEsperandoTirolina(){
        numNiñosEsperandoTirolina = numNiñosEsperandoTirolina + 1;
    }
    //Método que decrementa en 1 los niños de la tirolina
    public void decrementaNumNinosEsperandoTirolina(){
        numNiñosEsperandoTirolina = numNiñosEsperandoTirolina - 1;
    }
    //Método get del booleano de tirolina en uso
    public boolean isMonitorDisponible() {
        return monitorDisponible;
    }
    //Método set para el booleano monitor disponible
    public void setMonitorDisponible(boolean monitorDisponible) {
        this.monitorDisponible = monitorDisponible;
    }
    //Método get para el niño en la tirolina
    public Niño getNinoEnTirolina() {
        return ninoEnTirolina;
    }
    //Método set para el niño en la tirolina
    public void setNinoEnTirolina(Niño ninoEnTirolina) {
        this.ninoEnTirolina = ninoEnTirolina;
    }

    public int getNumUsosTirolina() {
        return numUsosTirolina;
    }

    public void incrementaNumUsosTirolina(){
        numUsosTirolina++;
    }

    public Log getLog() {
        return log;
    }

    public boolean isHuecoLibre() {
        return huecoLibre;
    }

    public void setHuecoLibre(boolean huecoLibre) {
        this.huecoLibre = huecoLibre;
    }
}
