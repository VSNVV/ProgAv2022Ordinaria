package concurrencia;

import java.util.ArrayList;

public class Monitor extends Thread {
    //Atributos de Monitor
    private String ID;
    private Campamento campamento;
    private Soga soga;
    private Merendero merendero;
    private Tirolina tirolina;
    private ZonaComun zonaComun;
    private Paso paso;
    boolean activo = true;
    boolean monitorDeSoga = false, monitorDeTirolina = false, monitorDeMerienda = false;
    private int tirolinasHechas = 0;
    private int meriendasServidas = 0;
    private int sogasHechas = 0;
    private int bandeja;
    //Método construtor
    public Monitor (String nombre, Campamento campamento, Soga soga, Merendero merendero, Tirolina tirolina, ZonaComun zonaComun, Paso _paso){
        this.ID = nombre;
        super.setName(nombre);
        this.campamento = campamento;
        this.soga = soga;
        this.merendero = merendero;
        this.tirolina = tirolina;
        this.zonaComun = zonaComun;
        this.paso = _paso;
    }
    //Método run
    public void run(){
        campamento.accesoMonitoresColas(this);
        campamento.elegirActividad(this);
        if (getMonitorDeSoga()){
            soga.entradaMonitorSoga(this);
            while (activo) {
                getPaso().mirar(); //Mirará antes de seguir si se tiene que parar
                soga.monitorEspera(this);
                //Una vez que salga del else, significa que ya podrá hacer los equipos
                soga.hacerEquiposSoga(this);
                //Una vez con los equipos hechos, liberamos la lista de niños de soga para que puedan entrar más, y
                //diremos el ganador y añadiremos 2 participaciones extra a los niños ganadores y 1 a los perdedores
                soga.jugarSoga(this);
                if(getSogasHechas() == 10){
                    //Antes de salir el monitor tiene que mirar si se tiene que parar
                    getPaso().mirar();
                    //Se verifica que el monitor lleva 10 sogas hechas, por tanto se irá de la soga
                    soga.salirMonitorSoga(this);
                    //Una vez fuera de la soga, nos vamos a la zona común
                    zonaComun.entrarZonaComunMonitores(this);
                    //Una vez dentro de la zona común, nos dormimos entre 1 y 2 segundos
                    dormirMonitor(this, (int)(Math.random()*2000+1000));
                    //Una vez despierto, abandonaremos la zona común
                    zonaComun.salirZonaComunMonitores(this);
                    //Una vez fuera de la zona común, reiniciamos las sogas hechas del monitor a 0
                    setSogasHechas(0);
                    //Antes de entrar deberá mirar si se tiene que parar
                    getPaso().mirar();
                    //Una vez reiniciadas las sogas hechas, podremos entrar a la actividad de nuevo
                    soga.entradaMonitorSoga(this);
                }
                else{
                    //Se verifica que no ha hecho 10 sogas, por tanto debe quedarse en soga y abrir la actividad para los siguientes niños
                    //Pero antes de volver tiene que mirar si se tiene que parar
                    getPaso().mirar();
                }
            }
        }
        else if (getMonitorDeTirolina()){
            //System.out.println("El monitor " + getID() + " es el monitor de tirolina");
            //Se verifica que el monitor no está en la tirolina, por tanto hay que entrar en la misma
            tirolina.entradaMonitorTirolina(this);
            while (activo){
                //Antes de hacer nada tendrá que mirar si se tiene que parar
                getPaso().mirar();
                //Una vez en la tirolina, el monitor esperará a que un niño esté en la preparación para poder prepararle
                tirolina.monitorEspera(this);
                //Cuando el niño le despierte, el monitor le preparará
                tirolina.preparaNinoTirolina(this);
                //Cuando el monitor vuelva a estado de ejecucion es que ya ha completado una tirolina, por tanto las incrementamos
                incrementaTirolinasHechas(1);
                //Cuando despierte el monitor comprobará si tiene 10 tirolinas hechas
                if(getTirolinasHechas() == 10){
                    //Antes de salir de la tirolina deberá comprobar si se tiene que parar o no
                    getPaso().mirar();
                    //Se verifica que tiene 10 tirolinas hechas, por tanto se irá a la zona común de 1 a 2 segundos
                    tirolina.salirMonitorTirolina(this);
                    //Una vez que nos hayamos salido de la tirolina, entramos en la zona común
                    zonaComun.entrarZonaComunMonitores(this);
                    //Una vez dentro de la zona común, nos dormimos entre 1 y 2 segundos
                    dormirMonitor(this, (int)(Math.random()*2000+1000));
                    //Una vez despierto, abandonaremos la zona común
                    zonaComun.salirZonaComunMonitores(this);
                    //Una vez fuera de la zona comñun volveremos a entrar a la tirolina, y reniniciamos sus tirolinas hechas a 0
                    setTirolinasHechas(0);
                    //Antes de entrar debemos comprobar si tenemos que parar o no
                    getPaso().mirar();
                    tirolina.entradaMonitorTirolina(this);
                }
                else{
                    //Antes de volver tiene que mirar si se tiene que parar o no
                    getPaso().mirar();
                    //Se verifica que no tiene 10 tirolinas hechas, por tanto se debe quedar en la tirolina hasta tener 10 hechas
                    tirolina.vuelveMonitor(this);
                }
            }
        }
        else if (getMonitorDeMerienda()){
            //Entramos al merendero
            merendero.entradaMonitorMerendero(this);
            while (activo){
                //Antes de hacer nada el monitor tendrá que revisar si se tiene que parar
                getPaso().mirar();
                //Una vez dentro del merendero, el monitor comprobará si hay bandejas sucias para limpiar
                merendero.comprobarBandejaSucia(this);
                //Una vez verificado que hay bandejas, cogemos una bandeja
                getPaso().mirar();
                merendero.cogerBandeja(this);
                //Una vez que tengamos la bandeja la limpiamos y la servimos
                getPaso().mirar();
                merendero.servirBandeja(this);
                //Si el monitor tiene 10 bandejas servidas, se ira a la zona común por 1 o 2 segundos
                if(getMeriendasServidas() == 10){
                    //Se verifica que el monitor ha servido 10 meriendas, por tanto se irá del merendero despues de reiniciar las meriendas servidas
                    setMeriendasServidas(0);
                    //Antes de salir del merendero tiene que ver si se tiene que parar
                    getPaso().mirar();
                    merendero.salirMonitoresMerendero(this);
                    //Entrará a la zona común
                    zonaComun.entrarZonaComunMonitores(this);
                    //Una vez dentro, nos dormimos entre 1 o 2 segundos
                    dormirMonitor(this, (int)(Math.random()*2000+1000));
                    //Una vez que nos despertemos, nos saldremos de la zona común
                    zonaComun.salirZonaComunMonitores(this);
                    //Antes de entrar al merendero de nuevo, debemos comprobar si no tenemos que parar
                    getPaso().mirar();
                    //Una vez hayamos salido de la zona común, volveremos al merendero
                    merendero.entradaMonitorMerendero(this);
                }
                else{
                    //Se verifica que no lleva 10 meriendas servidas, por tanto deberá seguir en el merendero hasta llegar a 10
                    //Antes de volver al merendero tiene que ver si se tiene que parar
                    getPaso().mirar();
                }
            }
        }
    }
    //Getter del id de monitor
    public String getID() {
        return ID;
    }
    //Método get de monitorDeSoga
    public boolean getMonitorDeSoga() {
        return monitorDeSoga;
    }
    //Método set de monitorDeSoga
    public void setMonitorDeSoga(boolean monitorDeSoga) {
        this.monitorDeSoga = monitorDeSoga;
    }
    //Método get de monitorDeTirolina
    public boolean getMonitorDeTirolina() {
        return monitorDeTirolina;
    }
    //Método set de monitorDeTirolina
    public void setMonitorDeTirolina(boolean monitorDeTirolina) {
        this.monitorDeTirolina = monitorDeTirolina;
    }
    //Método get de monitorDeMerienda
    public boolean getMonitorDeMerienda() {
        return monitorDeMerienda;
    }
    //Método set de monitorDeMerienda
    public void setMonitorDeMerienda(boolean monitorDeMerienda) {
        this.monitorDeMerienda = monitorDeMerienda;
    }
    //Método get de las meriendas servidas en el merendero por un monitor
    public int getMeriendasServidas() {
        return meriendasServidas;
    }
    //Método set de las meriendas servidas en el merendero por un monitor
    public void setMeriendasServidas(int meriendasServidas) {
        this.meriendasServidas = meriendasServidas;
    }
    //Método que incrementa las meriendas servidas por parte de un monitor
    public void incrementaMeriendasServidas(int incremento){
        meriendasServidas = meriendasServidas + incremento;
    }
    //Método get para la bandeja del merendero
    public int getBandeja() {
        return bandeja;
    }
    //Método set para la bandeja del merendero
    public void setBandeja(int bandeja) {
        this.bandeja = bandeja;
    }
    //Método get del numero de tirolinas que ha hecho un monitor
    public int getTirolinasHechas() {
        return tirolinasHechas;
    }
    //Método set para las tirolinas hechas de un monitor
    public void setTirolinasHechas(int tirolinasHechas) {
        this.tirolinasHechas = tirolinasHechas;
    }
    //Método para incrementar el numero de tirolinas hechas
    public void incrementaTirolinasHechas(int incremento){
        tirolinasHechas = tirolinasHechas + incremento;
    }
    //Método get de las sogas hechas por un monitor
    public int getSogasHechas() {
        return sogasHechas;
    }
    //Método set de la sosgas hechas por un monitor
    public void setSogasHechas(int sogasHechas) {
        this.sogasHechas = sogasHechas;
    }
    //Método para incrementar las sogas hechas por un monitor
    public void incrementaSogasHechas(int incremento){
        sogasHechas = sogasHechas + incremento;
    }
    //Método para dormir un monitor, ya sea niño o monitor durante x tiempo
    public void dormirMonitor(Monitor monitor, int tiempo){
        try{
            Thread.sleep(tiempo);
        }catch(InterruptedException ie){}
    }
    //Método para boorar un ID de un arraylist
    public void borrarDeLista(ArrayList lista, String nombre){
        //Recorremos la lista buscando el nombre indicado
        for (int i = 0; i < lista.size(); i++){
            if (nombre == lista.get(i)){
                //Se encuentra el valor que deseamos borrar, por tanto lo borramos
                lista.remove(i);
            }
        }
    }

    public Paso getPaso() {
        return paso;
    }
}