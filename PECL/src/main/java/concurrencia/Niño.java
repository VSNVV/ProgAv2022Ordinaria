package concurrencia;

import java.util.ArrayList;

public class Niño extends Thread {//Deberán ser modelados como hilos
    //Atributos de niño
    private int participaciones = 0;
    private int bandeja = -1;
    private int equipoSoga = 0;
    private int contadorActividadesTipoSogaTirolina = 0;
    private String ID;
    private Campamento campamento;
    private Soga soga;
    private Merendero merendero;
    private Tirolina tirolina;
    private ZonaComun zonaComun;
    private Paso paso;
    private boolean activo = true;

    //Método constructor
    public Niño(String nombre, Campamento campamento, Soga soga, Merendero merendero, Tirolina tirolina, ZonaComun zonaComun, Paso _paso) {
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
    public void run() {
        //Entrar campamento
        campamento.entrada(this);
        //Una vez dentro del campamento, elige una actividad al azar
        while (activo) {
            switch ((int) (Math.random() * 3 + 1)) {
                case 1:
                    //La actividad elegida ha sido merienda, pero solo podrá entrar si ha hecho 3 actividades de tipo soga o tirolina antes
                    if (getContadorActividadesTipoSogaTirolina() >= 3) {
                        //En primer lugar entramos al merendero
                        //Antes de pasar al merendero tendrñan que mirar a ver si se tienen que parar
                        getPaso().mirar();
                        merendero.entrarMerendero(this);
                        //Comprobamos si podemos coger bandeja o no, si no podemos nos quedamos dormidos hasta que un monitor nos avise de que hay una nueva
                        merendero.comprobarBandejaLimpia(this);
                        //Nos quedaremos dormidos hasta que alguno de los monitores nos sirva una bandeja
                        merendero.comerMerienda(this);
                        //Una vez hayan comido, dejarán la bandeja en la lista de bandejas sucias
                        merendero.dejarBandeja(this);
                        //Una vez la bandeja depositada en la lista de sucias, nos saldremos del merendero
                        //Antes de salir tendrán que mirar si se tienen que parar
                        getPaso().mirar();
                        merendero.salirMerendero(this);
                        //Hemos completado una actividad, por tanto nos sumamos una mas
                        incrementarParticipaciones(1);
                        //Ha terminado la actividad, por tanto, el niño deberña hacer una pausa de entre 2 y 4 segundos en la zona común
                        getPaso().mirar();
                        zonaComun.entrarZonaComun(this);
                        dormirNino(this, (int) (Math.random() * 4000 + 2000));
                        getPaso().mirar();
                        zonaComun.salirZonaComun(this);
                    }
                    break;
                case 2:
                    //La actividad elegida ha sido soga
                    //Primero verán si se puede entrar a la soga
                    if (soga.comprobarEntradaSoga(this)) {
                        //Una vez salgan del while, es porque la acitivdad ya está abierta, por tanto, entrarán a la actividad
                        //Antes de entrar a soga mirarán si se tienen que parar
                        getPaso().mirar();
                        soga.entrarSoga(this);
                        //Cuando se despierten los niños será la hora de salir de la actividad
                        //Antes de salir de la soga deben mirar si se tienen que parar
                        getPaso().mirar();
                        soga.salirSoga(this);
                        //Como hemos completado una actividad de soga, sumamos nuestro contador
                        incrementaContadorActividadesTipoSogaTirolina();
                        //Hemos hecho una actividad, por tanto nos sumamos una mas
                        incrementarParticipaciones(1);
                        //Ha terminado la actividad, por tanto, el niño deberña hacer una pausa de entre 2 y 4 segundos en la zona común
                        getPaso().mirar();
                        zonaComun.entrarZonaComun(this);
                        dormirNino(this, (int) (Math.random() * 4000 + 2000));
                        getPaso().mirar();
                        zonaComun.salirZonaComun(this);
                    } else {
                        //Se verifica que no se puede entrar, por tanto se elegirá una actividad nueva
                    }
                    break;
                case 3:
                    //La actividad elegida ha sido tirolina
                    tirolina.entrarTirolina(this);
                    //Si sale de aqui es que el niño está dentro de la tirolina, pero el monitor estará dormido, por tanto le tendrán que despertar
                    tirolina.avisarMonitorTirolina(this);
                    //El hilo se dormirá hasta que se pueda tirar
                    tirolina.tirarTirolina(this);
                    //Una vez que se haya tirado, se bajará de la tirolina
                    tirolina.bajaTirolina(this);
                    //Una vez que se haya bajado de la tirolina, se saldrá de la actividad
                    tirolina.salirTirolina(this);
                    //Al salir de la tirolina, nos sumamos una actividad de tipo tirolina o soga
                    incrementaContadorActividadesTipoSogaTirolina();
                    //También hemos hecho una actividad nueva
                    incrementarParticipaciones(1);
                    //Ha terminado la actividad, por tanto, el niño deberña hacer una pausa de entre 2 y 4 segundos en la zona común
                    //Antes de entrar en la zona comun deberán mirar si se tienen que parar
                    getPaso().mirar();
                    zonaComun.entrarZonaComun(this);
                    dormirNino(this, (int) (Math.random() * 4000 + 2000));
                    //Antes de salir tendrán que mirar si se tienen que parar
                    getPaso().mirar();
                    zonaComun.salirZonaComun(this);
                    break;
            }
            //Si un niño no tiene participaciones deberá abandonar el campamento
            if (getParticipaciones() == 15) {
                //Se verifica que el niño no tiene mas participaciones, por tanto deberá abandonar el campamento
                campamento.salir(this);
                setActivo(false);
            } else {
                //Tiene participaciones todavia, por tanto no abandonará el campamento
            }
        }
        campamento.salir(this);
    }

    //Getter del ID del niño
    public String getID() {
        return ID;
    }

    //Decidir si el hilo sigue activo o no
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    //Método para incrementar en 2 las participaciones de un niño por ganar en soga
    public void incrementarParticipaciones(int participacionesExtra) {
        participaciones = participaciones + participacionesExtra;
    }

    //Método get para la bandeja del merendero
    public int getBandeja() {
        return bandeja;
    }

    //Método set de la bandeja del merendero
    public void setBandeja(int bandeja) {
        this.bandeja = bandeja;
    }

    //Método para dormir un hilo, ya sea niño o monitor durante x tiempo
    public void dormirNino(Niño nino, int tiempo) {
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ie) {
        }
    }

    //Método para boorar un ID de un arraylist
    public void borrarDeLista(ArrayList lista, String nombre) {
        //Recorremos la lista buscando el nombre indicado
        for (int i = 0; i < lista.size(); i++) {
            if (nombre == lista.get(i)) {
                //Se encuentra el valor que deseamos borrar, por tanto lo borramos
                lista.remove(i);
            }
        }
    }

    //Método get para el numero de participaciones de un niño
    public int getParticipaciones() {
        return participaciones;
    }

    //Método get para el equipo de la soga
    public int getEquipoSoga() {
        return equipoSoga;
    }

    //Método set para el equipo de la soga
    public void setEquipoSoga(int equipoSoga) {
        this.equipoSoga = equipoSoga;
    }

    public int getContadorActividadesTipoSogaTirolina() {
        return contadorActividadesTipoSogaTirolina;
    }

    public void incrementaContadorActividadesTipoSogaTirolina() {
        contadorActividadesTipoSogaTirolina++;
    }

    public Paso getPaso() {
        return paso;
    }
}